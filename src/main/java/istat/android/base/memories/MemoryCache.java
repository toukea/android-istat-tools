package istat.android.base.memories;


import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import android.graphics.Bitmap;
import android.util.Log;

import istat.android.base.interfaces.EntryGenerator;

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
 * 
 * @author Toukea Tatsi (Istat)
 *
 */
public class MemoryCache {

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

    public boolean containBitmap(String id) {
        String entry = this.entryGenerator.onGenerateEntry(id);
        return cache.containsKey(entry);
    }

    public Bitmap get(String id) {
        String entry = this.entryGenerator.onGenerateEntry(id);
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

    public void put(String id, Bitmap bitmap) {
        String entry = this.entryGenerator.onGenerateEntry(id);
        try {
            if (cache.containsKey(entry))
                size -= getSizeInBytes(cache.get(entry));
            cache.put(entry, bitmap);
            size += getSizeInBytes(bitmap);
            checkSize();
        } catch (Throwable th) {
            th.printStackTrace();
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

    long getSizeInBytes(Bitmap bitmap) {
        if (bitmap == null)
            return 0;
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    final static EntryGenerator DEFAULT_ENTRY_GENERATOR = new EntryGenerator() {
        @Override
        public String onGenerateEntry(String name) {
            return name;
        }
    };

    EntryGenerator entryGenerator=DEFAULT_ENTRY_GENERATOR;

    public void setEntryGenerator(EntryGenerator entryGenerator) {
        if (entryGenerator == null) {
            entryGenerator = DEFAULT_ENTRY_GENERATOR;
        }
        this.entryGenerator = entryGenerator;
    }
}