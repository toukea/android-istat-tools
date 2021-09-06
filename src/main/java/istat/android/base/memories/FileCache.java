package istat.android.base.memories;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

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
public class FileCache implements Cache<File> {
    File cacheDir;
    String dirName = "istat";
    Context context;

    public FileCache(Context context) {
        this(context, context.getPackageName());
    }


    public FileCache(Context context, String dirNames) {
        this.context = context;
        // Find the dir at SDCARD to save cached images
        this.dirName = dirNames;
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            // if SDCARD is mounted (SDCARD is present on device and mounted)
            cacheDir = new File(
                    android.os.Environment.getExternalStorageDirectory(),
                    dirNames);
        } else {
            // if checking on simulator the createDirectives cache dir in your application
            // context
            cacheDir = new File(context.getCacheDir(), dirNames);
        }
        if (cacheDir != null) {
            Log.d("FileCache", "CacheDir::" + cacheDir.getAbsolutePath());
        }
    }

    public FileCache(Context context, File cacheDir) {
        this(context, cacheDir, null);
    }

    public FileCache(Context context, File cacheDir, String nameSpace) {
        this.context = context;
        if (TextUtils.isEmpty(nameSpace)) {
            this.cacheDir = cacheDir;
        } else {
            this.cacheDir = new File(cacheDir, nameSpace);
        }
    }

    private void createCacheDir() {
        if (cacheDir != null && !cacheDir.exists()) {
            // createDirectives cache dir in your application context
            cacheDir.mkdirs();
        }
    }

    public File getRootDir() {
        return getRootDir(true);
    }

    public File getRootDir(boolean createIfNotExist) {
        if (createIfNotExist) {
            createCacheDir();
        }
        return cacheDir;
    }

    public File getDir(String name) {
        name = name + (name.endsWith("/") ? "" : "/");
        return new File(getRootDir(), name);
    }

    public File resolve(String uri) {
        // Identify images by hashcode or encode by URLEncoder.encode.
        String filename = entryGenerator.onGenerateEntry(uri);
        if (TextUtils.isEmpty(filename)) {
            return null;
        }
        createCacheDir();
        File f = new File(cacheDir, filename);
        return f;
    }

    public File getFile(String relativeFilePath) {
        // Identify images by hashcode or encode by URLEncoder.encode.
        createCacheDir();
        File f = new File(cacheDir, relativeFilePath);
        return f;

    }

    public long getCacheContentBytes() {
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

    public int clear() {
        createCacheDir();
        // list all files inside cache directory
        File[] files = cacheDir.listFiles();
        if (files == null)
            return 0;
        int count = 0;
        // delete all cache directory files
        for (File f : files) {
            if (f.delete()) {
                count++;
            }
        }
        return count;
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

    public File createTempFile(String prefix, String suffix) throws IOException {
        File tempFile = File.createTempFile(prefix, suffix, getRootDir());
        return tempFile;
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
            if (name == null || name.startsWith("file://")) {
                return null;
            }
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

    @Deprecated
    public File getCacheDir() {
        this.createCacheDir();
        return this.cacheDir;
    }

    public EntryGenerator getEntryGenerator() {
        return entryGenerator;
    }

    public File remove(String iconUri) {
        File file = resolve(iconUri);
        if (file != null && file.exists()) {
            file.delete();
            return file;
        }
        return null;
    }

    @Override
    public File put(String key, File value) {
        return value;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public Set<String> keySet() {
        //TODO implementer cette methode
        return null;
    }

    @Override
    public Collection<File> values() {
        //TODO implementer cette methode
        return null;
    }

    @Override
    public boolean containsKey(String filePath) {
        return false;
    }

    @Override
    public File get(String name) {
        String fileName = entryGenerator != null ? entryGenerator.onGenerateEntry(name) : name;
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }
        return new File(getRootDir(), fileName);
    }

    @Override
    public int size() {
        return getRootDir().list().length;
    }
}