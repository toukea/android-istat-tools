package istat.android.base.memories;


import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import istat.android.base.interfaces.EntryGenerator;
import istat.android.base.tools.TextUtils;

/*
 * Copyright (C) 2014 Istat Dev.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Toukea Tatsi (Istat)
 */
//TODO il faut ajouter un trim static qui vide la memoire par rapport au limitCallable
public class MemoryCache implements Cache<Bitmap> {

    private static final String TAG = "MemoryCache";
    private static final Map<String, List<String>> entryNames = Collections.synchronizedMap(new HashMap<String, List<String>>());
    //Last argument true for LRU ordering
    private static final Map<String, Bitmap> cache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 1.5f, true));

    //current allocated size
    private static long size = 0;

    //max memory cache folder used to download images in bytes
    //TODO afin de permettre des comportement dynamique mettre un callable a la place.
    public final static long DEFAULT_CACHE_SIZE_LIMIT = 4000000l;
    final static Callable<Long> DEFAULT_LIMIT_CALLABLE = new Callable<Long>() {
        @Override
        public Long call() {
            //use 25% of available heap size
            return (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 2;
        }
    };
    private static Callable<Long> limitCallable = DEFAULT_LIMIT_CALLABLE;

    public MemoryCache() {
    }

    //TODO devrait être static et general a toute la class
    public static void setLimit(final long new_limit) {
        setLimit(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return new_limit;
            }
        });
        Log.i(TAG, "MemoryCache will use up to " + new_limit / 1024. / 1024. + "MB");
    }

    public static void setLimit(Callable<Long> callable) {
        limitCallable = callable != null ? callable : DEFAULT_LIMIT_CALLABLE;
    }

    public Bitmap get(String id) {
        String entry = this.entryGenerator.onGenerateEntry(id);
        if (TextUtils.isEmpty(entry)) {
            return null;
        }
        try {
            if (!cache.containsKey(entry))
                return null;
            //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
            return cache.get(entry);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public int size() {
        return cache.size();
    }

    public Bitmap remove(String id) {
        String entry = this.entryGenerator.onGenerateEntry(id);
        if (TextUtils.isEmpty(entry)) {
            return null;
        }
        Bitmap removed = cache.remove(entry);
        removeGeneratedEntryName(entry);
        return removed;
    }

    public void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    //TODO implementer la même gestion du cache que celle du Drive.
//    public Map<String, WeakReference<Bitmap>> getBundle() {
//        return cache;
//    }
//
    public void purge() {
//        try {
//            Bitmap currentBitmap;
//            for (WeakReference<Bitmap> bitRef : getBundle().values()) {
//                currentBitmap = bitRef.get();
//                if (currentBitmap != null) {
//                    currentBitmap.recycle();
//                }
//            }
//            cache.clear();
//            size = 0;
//            freeMemory();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }

    @Override
    public boolean containsKey(String filePath) {
        String entry = this.entryGenerator.onGenerateEntry(filePath);
        if (TextUtils.isEmpty(entry)) {
            return false;
        }//doit t'on pluto cherché dans
        return cache.containsKey(entry);
    }

    public Bitmap put(String id, Bitmap bitmap) {
        String entry = this.entryGenerator.onGenerateEntry(id);
        if (TextUtils.isEmpty(entry)) {
            return null;
        }
        try {
            if (cache.containsKey(entry))
                size -= getSizeInBytes(cache.get(entry));
            Bitmap bitmap1 = cache.put(entry, bitmap);
            addGeneratedEntryName(entry);
            size += getSizeInBytes(bitmap);
            checkSize();
            return bitmap1;
        } catch (Throwable th) {
            th.printStackTrace();
            return null;
        }
    }

    final static String TAG_ENTRY_NAME_DEFINITION_GENERATOR = "memory-map-entry";

    private void addGeneratedEntryName(String entryName) {
        String entryKey = this.entryGenerator.onGenerateEntry(TAG_ENTRY_NAME_DEFINITION_GENERATOR);
        List<String> list = entryNames.get(entryKey);
        if (list == null) {
            list = new ArrayList<>();
            entryNames.put(entryKey, list);
        }
        if (!list.contains(entryName)) {
            list.add(entryName);
        }
    }

    private boolean removeGeneratedEntryName(String entryName) {
        String entryKey = this.entryGenerator.onGenerateEntry(TAG_ENTRY_NAME_DEFINITION_GENERATOR);
        List<String> list = entryNames.get(entryKey);
        if (list != null) {
            return list.remove(entryName);
        }
        return false;
    }

    public static long getLimit() {
        try {
            long limit = limitCallable.call();
            Log.i(TAG, "limit= " + (limit / 1024 / 1024));
            return limit;
        } catch (Exception e) {
            e.printStackTrace();
            return DEFAULT_CACHE_SIZE_LIMIT;
        }
    }

    private void checkSize() {
        Log.i(TAG, "cache size=" + size + " length=" + cache.size());
        if (size > getLimit()) {
            Iterator<Entry<String, Bitmap>> iter = cache.entrySet().iterator();//least recently accessed item will be the first one iterated
            while (iter.hasNext()) {
                Entry<String, Bitmap> entry = iter.next();
                size -= getSizeInBytes(entry.getValue());
                iter.remove();
                if (size <= getLimit())
                    break;
            }
            Log.i(TAG, "Clean cache. New size " + cache.size());
        }
    }

    public static int clearAll() {
        try {
            //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
            int out = cache.size();
            cache.clear();
            entryNames.clear();
            size = 0;
            return out;
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public int clear() {
        int removed = 0;
        try {
            synchronized (entryNames) {
                //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
                if (entryNames.isEmpty()) {
                    return 0;
                }
                String entryKey = this.entryGenerator.onGenerateEntry(TAG_ENTRY_NAME_DEFINITION_GENERATOR);
                List<String> list = entryNames.get(entryKey);
                if (list == null || list.isEmpty()) {
                    return 0;
                }
                for (String entry : list) {
                    Bitmap bitmap = cache.remove(entry);
                    size = -getSizeInBytes(bitmap);
                    removed++;
                }

                list.clear();
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        return removed;
    }

    public static int trim() {
        int removed = 0;
        try {
            synchronized (entryNames) {
                //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
                if (entryNames.isEmpty()) {
                    return 0;
                }
                long limit = limitCallable.call();
                if (size < limit) {
                    return removed;
                }
                // String entryKey = this.entryGenerator.onGenerateEntry(TAG_ENTRY_NAME_DEFINITION_GENERATOR);
                for (Entry<String, List<String>> entry : entryNames.entrySet()) {
                    List<String> list = entryNames.get(entry.getKey());
                    if (list == null || list.isEmpty()) {
                        continue;
                    }
                    for (String entryName : list) {
                        Bitmap bitmap = cache.remove(entryName);
                        size = -getSizeInBytes(bitmap);
                        removed++;
                        if (size < limit) {
                            break;
                        }
                    }

                    list.clear();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return removed;
    }

    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return cache.keySet();
    }

    @Override
    public Collection<Bitmap> values() {
        //TODO implementer cette methode
        return null;
    }

    static long getSizeInBytes(Bitmap bitmap) {
        if (bitmap == null)
            return 0;
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    final static EntryGenerator DEFAULT_ENTRY_GENERATOR = new EntryGenerator() {
        @Override
        public String onGenerateEntry(String name) {
            if (name == null) {
                return null;
            }
            return name;
        }
    };

    EntryGenerator entryGenerator = DEFAULT_ENTRY_GENERATOR;

    public void setEntryGenerator(EntryGenerator entryGenerator) {
        if (entryGenerator == null) {
            entryGenerator = DEFAULT_ENTRY_GENERATOR;
        }
        this.entryGenerator = entryGenerator;
    }

    public EntryGenerator getEntryGenerator() {
        return entryGenerator;
    }
}