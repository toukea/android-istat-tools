package istat.android.base.memories;

import java.io.File;

import android.content.Context;
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
 * @author Toukea Tatsi (Istat)
 */
public class FileCache {
    File cacheDir;
    String dirName = "istat";
    Context context;

    public FileCache(Context context) {
        this.context = context;
        // Find the dir at SDCARD to save cached images
        dirName = context.getPackageName();
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            // if SDCARD is mounted (SDCARD is present on device and mounted)
            cacheDir = new File(
                    android.os.Environment.getExternalStorageDirectory(),
                    dirName);
        } else {
            // if checking on simulator the createDirectives cache dir in your application
            cacheDir = context.getCacheDir();
        }
        createCacheDir();
        Log.d("FileCache", "CacheDir::" + cacheDir.getAbsolutePath());
    }


    public FileCache(Context context, String dirNames) {
        this.context = context;
        // Find the dir at SDCARD to save cached images
        dirName = dirNames;
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            // if SDCARD is mounted (SDCARD is present on device and mounted)
            cacheDir = new File(
                    android.os.Environment.getExternalStorageDirectory(),
                    dirName);
        } else {
            // if checking on simulator the createDirectives cache dir in your application
            // context
            cacheDir = new File(context.getCacheDir(), dirNames);
        }
        Log.d("FileCache(C,S)", "CacheDir::" + cacheDir.getAbsolutePath());
        createCacheDir();
    }

    public FileCache(Context context, File cacheDir) {
        this.context = context;
        this.cacheDir = cacheDir;
    }

    private void createCacheDir() {
        if (!cacheDir.exists()) {
            // createDirectives cache dir in your application context
            cacheDir.mkdirs();
        }
    }

    public FileCache getInternal() {

        // if checking on simulator the createDirectives cache dir in your application
        // context
        cacheDir = context.getCacheDir();

        createCacheDir();
        return this;
    }

    public FileCache getExternal() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            // if SDCARD is mounted (SDCARD is present on device and mounted)
            cacheDir = new File(
                    android.os.Environment.getExternalStorageDirectory(),
                    dirName);
        } else {
            // if checking on simulator the createDirectives cache dir in your application
            // context
            cacheDir = context.getCacheDir();
        }

        createCacheDir();
        return this;
    }

    public File getRootDir() {
        createCacheDir();
        return cacheDir;
    }

    public File getDir(String name) {
        name = name + (name.endsWith("/") ? "" : "/");
        return new File(getRootDir(), name);
    }

    public File getFile(String url) {
        // Identify images by hashcode or encode by URLEncoder.encode.
        String filename = entryGenerator.onGenerateEntry(url);
        createCacheDir();
        File f = new File(cacheDir, filename);
        return f;
    }

    public File getFileFromCacheName(String fullName) {
        // Identify images by hashcode or encode by URLEncoder.encode.
        createCacheDir();
        File f = new File(cacheDir, fullName);
        return f;

    }

    public long getCacheSize() {
        // list all files inside cache directory
        long out = 0;
        createCacheDir();
        File[] files = cacheDir.listFiles();
        if (files == null)
            return 0;
        // delete all cache directory files
        for (File f : files)
            out += f.length();
        return out;
    }

    public void clearByLivingTime(long maxLivingTime) {
        createCacheDir();
        // list all files inside cache directory
        File[] files = cacheDir.listFiles();
        if (files == null)
            return;
        // delete all cache directory files
        for (File file : files) {
            if (System.currentTimeMillis() - file.lastModified() >= maxLivingTime) {
                Log.d("FileCache", "clearByLivingTime::deleted=" + file.getPath() + ", current=" + System.currentTimeMillis() + ", modified=" + file.lastModified() + ", diff=" + (System.currentTimeMillis() - file.lastModified()) + ", maxLiveTime=" + maxLivingTime);
                file.delete();
            }
        }
    }

    public void clear() {
        createCacheDir();
        // list all files inside cache directory
        File[] files = cacheDir.listFiles();
        if (files == null)
            return;
        // delete all cache directory files
        for (File f : files)
            f.delete();
    }

    public static long getInternalCacheSize(Context context) {

        // if SDCARD is mounted (SDCARD is present on device and mounted)
        File cacheDir = context.getCacheDir();

        return getDirSize(cacheDir);

    }

    public FileCache setDirName(String dirname) {
        dirName = dirname;
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(
                    android.os.Environment.getExternalStorageDirectory(),
                    dirName);
        } else {
            cacheDir = context.getCacheDir();
        }
        createCacheDir();
        return this;
    }

    public static void clearApplicationData(Context context) {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                }
            }
        }
    }

    public static void clearInternalCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    public static long getDirSize(File dir) {
        long out = 0;
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                out = out + new File(dir, children[i]).length();
            }
        }

        // The directory is now empty so delete it
        return out;
    }

    public static boolean isExternalStorageMounted() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    // -----------------------------------------------------------------------------------------------

    public Context getContext() {
        return context;
    }

    public final static EntryGenerator DEFAULT_ENTRY_GENERATOR = new EntryGenerator() {
        @Override
        public String onGenerateEntry(String name) {
            String filename = String.valueOf(name.hashCode());
            return filename;
        }
    };

    EntryGenerator entryGenerator = DEFAULT_ENTRY_GENERATOR;

    public void setEntryGenerator(EntryGenerator entryGenerator) {
        if (entryGenerator == null) {
            entryGenerator = DEFAULT_ENTRY_GENERATOR;
        }
        this.entryGenerator = entryGenerator;
    }

    public File getCacheDir() {
        this.createCacheDir();
        return this.cacheDir;
    }
}