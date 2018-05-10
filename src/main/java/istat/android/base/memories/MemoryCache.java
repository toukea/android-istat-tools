package istat.android.base.memories;


import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.graphics.Bitmap;
import android.util.Log;

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
public class MemoryCache implements Cache<Bitmap> {

    private static final String TAG = "MemoryCache";

    //Last argument true for LRU ordering
    private static final Map<String, Bitmap> cache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 1.5f, true));

    //current allocated size
    private long size = 0;

    //max memory cache folder used to download images in bytes
    private long limit = 4000000;

    public MemoryCache() {

        //use 25% of available heap size
        setLimit(Runtime.getRuntime().maxMemory() / 4);
    }

    public void setLimit(long new_limit) {

        limit = new_limit;
        Log.i(TAG, "MemoryCache will use up to " + limit / 1024. / 1024. + "MB");
    }

    public boolean containkey(String id) {
        String entry = this.entryGenerator.onGenerateEntry(id);
        if (TextUtils.isEmpty(entry)) {
            return false;
        }
        return cache.containsKey(entry);
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
        return removed;
    }

    @Override
    public boolean containsKey(String filePath) {
        return false;
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
            size += getSizeInBytes(bitmap);
            checkSize();
            return bitmap1;
        } catch (Throwable th) {
            th.printStackTrace();
            return null;
        }
    }

    private void checkSize() {
        Log.i(TAG, "cache size=" + size + " length=" + cache.size());
        if (size > limit) {
            Iterator<Entry<String, Bitmap>> iter = cache.entrySet().iterator();//least recently accessed item will be the first one iterated
            while (iter.hasNext()) {
                Entry<String, Bitmap> entry = iter.next();
                size -= getSizeInBytes(entry.getValue());
                iter.remove();
                if (size <= limit)
                    break;
            }
            Log.i(TAG, "Clean cache. New size " + cache.size());
        }
    }

    public void clear() {
        try {
            //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78

            cache.clear();
            size = 0;
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return cache.keySet();
    }

    long getSizeInBytes(Bitmap bitmap) {
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