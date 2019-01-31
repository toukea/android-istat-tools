package istat.android.base.utils;

import istat.android.base.interfaces.EntryGenerator;
import istat.android.base.memories.Cache;
import istat.android.base.memories.FileCache;
import istat.android.base.memories.MemoryCache;
import istat.android.base.tools.Bitmaps;
import istat.android.base.tools.ToolKits;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import static istat.android.base.tools.Bitmaps.getBitmapFromPath;

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
public class ImageLoader {
    public final static String TAG = "ImageLoader";
    //TODO implementer un ImageRender|Handler qui manipule l'image avant l'affichage
    //TODO permettre de supporté la QUATLITY_AUTO qui resize auto l'image a la taille de La vue.
    public static int QUALITY_AUTO = -2, QUALITY_HIGH = -1, QUALITY_LOW = 85;
    final static int DEFAULT_PICTURE_ON_PROGRESS = android.R.drawable.ic_dialog_info,
            DEFAULT_PICTURE_ON_ERROR = android.R.drawable.ic_dialog_alert;
    Bitmap progressionBitmapHolder, errorBitmapHolder;
    int imageQuality = QUALITY_HIGH;
    // Initialize MemoryCache
    //  public static final MemoryCache DEFAULT_MEMORY_CACHE = new MemoryCache();
    MemoryCache memoryCache = new MemoryCache();//DEFAULT_MEMORY_CACHE;
    FileCache fileCache;
    Context context;
    // Create Map (collection) to store image and image url in key value pair
    private Map<ImageView, String> imageViews = Collections
            .synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    // handler to display images in UI thread
    Handler handler = new Handler(Looper.getMainLooper());

    public ImageLoader(Context context) {
        this(context,
                BitmapFactory.decodeResource(context.getResources(), DEFAULT_PICTURE_ON_ERROR)
                , BitmapFactory.decodeResource(context.getResources(), DEFAULT_PICTURE_ON_PROGRESS)
        );

    }

    public ImageLoader(Context context, int errorIcon, int progressIcon) {
        this(context, BitmapFactory.decodeResource(context.getResources(), errorIcon)
                , BitmapFactory.decodeResource(context.getResources(), progressIcon));
    }

    public ImageLoader(Context context, Bitmap errorIcon, Bitmap progressIcon) {
        this.context = context;
        // Creates a thread pool that reuses a fixed number of
        // threads operating off a shared unbounded queue.
        executorService = Executors.newFixedThreadPool(5);
        this.errorBitmapHolder = errorIcon;
        this.progressionBitmapHolder = progressIcon;
        memoryCache.setEntryGenerator(new EntryGenerator() {
            @Override
            public String onGenerateEntry(String name) {
                return name + "." + imageQuality;
            }
        });
    }

    public void setFileCache(FileCache fileCache) {
        if (fileCache == null) {
            fileCache = new FileCache(this.context);
        }
        this.fileCache = fileCache;
    }


    public void setImageQuality(int imageQuality) {
        this.imageQuality = imageQuality;
    }

    //TODO essayer d'éviter les OutOfMemory au chargement de des icon INT
    public void setErrorIcon(int icon) {
        if (icon == 0) {
            errorBitmapHolder = null;
            return;
        }
        errorBitmapHolder = BitmapFactory.decodeResource(getContext().getResources(), icon);
    }

    public void setProgressIcon(int icon) {
        if (icon == 0) {
            progressionBitmapHolder = null;
            return;
        }
        progressionBitmapHolder = BitmapFactory.decodeResource(getContext().getResources(), icon);
    }

    public void setErrorIcon(Bitmap icon) {
        errorBitmapHolder = icon;
    }

    public void setProgressIcon(Bitmap icon) {
        progressionBitmapHolder = icon;
    }


    public Bitmap getErrorIcon() {
        return errorBitmapHolder;
    }

    public Bitmap getProgressIcon() {
        return progressionBitmapHolder;
    }

    public FileCache getFileCache() {
        if (fileCache == null) {
            fileCache = new FileCache(getContext());
        }
        return fileCache;
    }

    public void displayImage(int resourceId, ImageView imageView, LoadCallback mLoadListener) {
        displayImage(resourceId + "", imageView, mLoadListener);
    }

    public void load(String path, LoadCallback mLoadListener) {
        displayImage(path, null, mLoadListener);
    }

    public void displayImage(String path, ImageView imageView) {
        displayImage(path, imageView, null);
    }

    public void displayImage(String path, ImageView imageView, LoadCallback loadCallback) {
        // Store image and url in Map
        imageViews.put(imageView, path);

        // Check image is stored in MemoryCache Map or not (see
        // MemoryCache.java)
        Bitmap bitmap = isUseMemoryCache() ? memoryCache.get(path) : null;
        PhotoToLoad photoToLoad = new PhotoToLoad(path, imageView);
        notifyImageLoad(photoToLoad, loadCallback);
        if (bitmap != null) {
            // if image is stored in MemoryCache Map then
            // Show image in listview row
            notifyImageLoaded(photoToLoad, loadCallback, bitmap);
        } else {
            // queue Photo to download create url
            queuePhoto(path, imageView, loadCallback);
        }
    }

    private void queuePhoto(String url, ImageView imageView, LoadCallback mLoadListener) {
        // Store image and url in PhotoToLoad object
        PhotoToLoad p = new PhotoToLoad(url, imageView);

        // pass PhotoToLoad object to PhotosLoader runnable class
        // and submit PhotosLoader runnable to executers to run runnable
        // Submits a PhotosLoader runnable task for execution
        Runnable photoToLoadRunnable = new PhotosLoader(p, mLoadListener);
        executorService.submit(photoToLoadRunnable);

    }

    public static Bitmap getBitmap(String url, Context activity, int imageQuality) throws IOException {
        return getBitmap(url, new FileCache(activity), new MemoryCache(), imageQuality, DEFAULT_RESOURCE_CONNECTION_HANDLER);
    }

    // Task for the queue
    public class PhotoToLoad {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        LoadCallback loadListener;

        PhotosLoader(PhotoToLoad photoToLoad, LoadCallback loadListener) {
            this.photoToLoad = photoToLoad;
            this.loadListener = loadListener;
        }

        @Override
        public void run() {
            try {
                // Check if image already downloaded
                if (imageViewReused(photoToLoad))
                    return;
                // download image create web url
                Bitmap bmp = getBitmap(photoToLoad, mResourceConnectionHandler);

                // set image data in Memory Cache


                if (imageViewReused(photoToLoad))
                    return;

                // Get bitmap to display
                BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad, this.loadListener);

                // Causes the Runnable bd (BitmapDisplayer) to be added to the
                // message queue.
                // The runnable will be run on the thread to which this handler
                // is attached.
                // BitmapDisplayer run method will call
                handler.post(bd);

            } catch (Throwable th) {
                th.printStackTrace();
                notifyImageLoadError(photoToLoad, loadListener, th);
            }
        }
    }

    public void setResourceConnectionHandler(ResourceConnectionHandler mResourceConnectionHandler) {
        this.mResourceConnectionHandler = mResourceConnectionHandler;
    }

    public final static ResourceConnectionHandler DEFAULT_RESOURCE_CONNECTION_HANDLER = new ResourceConnectionHandler() {
        InputStream is = null;
        private Map<String, URLConnection> connectionMap = Collections.synchronizedMap(new WeakHashMap<String, URLConnection>());

        @Override
        public InputStream onConnect(String url) {
            try {
                URI uri = URI.create(url);
                if (uri.getScheme() == null) {
                    url = "file://" + url;
                }
                URL imageUrl = new URL(url);
                URLConnection urlConnection = imageUrl.openConnection();
                connectionMap.put(url, urlConnection);
                if (urlConnection instanceof HttpURLConnection) {
                    HttpURLConnection conn = (HttpURLConnection) imageUrl
                            .openConnection();
                    conn.setConnectTimeout(30000);
                    conn.setReadTimeout(30000);
                    conn.setInstanceFollowRedirects(true);
                    is = conn.getInputStream();
                } else {
                    is = urlConnection.getInputStream();
                }
                return is;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return is;
        }

        @Override
        public void onDisconnect(String url, InputStream stream) {
            URLConnection urlConnection = connectionMap.get(url);
            if (urlConnection != null) {
                if (urlConnection instanceof HttpURLConnection) {
                    ((HttpURLConnection) urlConnection).disconnect();
                }
                connectionMap.remove(url);
            }
        }
    };
    private ResourceConnectionHandler mResourceConnectionHandler = DEFAULT_RESOURCE_CONNECTION_HANDLER;

    public interface ResourceConnectionHandler {
        InputStream onConnect(String url) throws IOException;

        void onDisconnect(String url, InputStream inputStream) throws IOException;
    }

    //TODO Essayer de voir comment délégué a une autre méthode getBitmap
    private Bitmap getBitmap(PhotoToLoad photosLoad, final ResourceConnectionHandler resourceConnectionHandler) throws IOException {
        String url = photosLoad.url;
        if (ToolKits.WordFormat.isInteger(url))
            return Bitmaps.getBitmapFromResource(getContext(),
                    Integer.valueOf(url));
        int quality = imageQuality;
        if (quality == QUALITY_AUTO && photosLoad.imageView != null) {
            quality = photosLoad.imageView.getHeight() >= photosLoad.imageView.getWidth() ?
                    photosLoad.imageView.getHeight() :
                    photosLoad.imageView.getWidth();
            if (quality == 0) {
                quality = QUALITY_HIGH;
            } else if (quality <= QUALITY_LOW) {
                quality = QUALITY_LOW;
            }
        }
        return getBitmap(url, useFileCache ? fileCache : null, useMemoryCache ? memoryCache : null, quality, resourceConnectionHandler);
    }

    public static Bitmap getBitmap(String url, FileCache cache, MemoryCache memoryCache, int quality, final ResourceConnectionHandler resourceConnectionHandler) throws IOException {
        File cachedFile = cache != null ? cache.resolve(url) : null;

        // create SD cache
        // CHECK : if trying to decode file which not exist in cache return null

        if (cachedFile != null && cachedFile.exists()) {
            Bitmap b;
            if (quality <= QUALITY_HIGH) {
                b = getBitmapFromPath(cachedFile.getAbsolutePath());
            } else {
                b = decodeFile(cachedFile, quality);
            }
            if (b != null)
                return b;
        }
        // Download image file create web
        try {
            Bitmap bitmap;
            InputStream is = resourceConnectionHandler.onConnect(url);
            if (is == null) {
                return null;
            }
            if (cachedFile != null) {
                OutputStream os = new FileOutputStream(cachedFile);
                ToolKits.Stream.copyStream(is, os);
                os.close();
                resourceConnectionHandler.onDisconnect(url, is);
                if (quality <= QUALITY_HIGH) {
                    bitmap = getBitmapFromPath(cachedFile.getAbsolutePath());
                } else {
                    bitmap = decodeFile(cachedFile, quality);
                }
            } else {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ToolKits.Stream.copyStream(is, os);
                byte[] bytes = os.toByteArray();
                resourceConnectionHandler.onDisconnect(url, is);
                if (quality <= QUALITY_HIGH) {
                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                } else {
                    bitmap = decodeByte(bytes, quality);
                }
                os.close();
            }
            if (memoryCache != null) {
                memoryCache.put(url, bitmap);
            }
            return bitmap;
        } catch (OutOfMemoryError ex) {
            Log.e(TAG, "error getting bitmap at: " + url);
            ex.printStackTrace();
            if (memoryCache != null) {
                memoryCache.clear();
            }
            return null;
        }
    }

    private Bitmap decodeFile(File f) {
        return decodeFile(f, imageQuality);
    }

    // Decodes image and scales it to reduce memory consumption
    public static Bitmap decodeFile(File f, int quality) {
        if (f == null) {
            return null;
        }
        try {

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(f);
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();

            // Find the correct scale value. It should be the power of 2.

            // Set width/height of recreated image
            final int REQUIRED_SIZE = quality;

            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with current scale values
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap decodeByte(byte[] f, int quality) {
        if (f == null) {
            return null;
        }
        try {

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(f, 0, f.length, o);

            // Find the correct scale value. It should be the power of 2.

            // Set width/height of recreated image
            final int REQUIRED_SIZE = quality;

            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with current scale values
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

            Bitmap bitmap = BitmapFactory.decodeByteArray(f, 0, f.length, o2);
            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //    boolean imageViewReused(PhotoToLoad photoToLoad) {
//
//        String tag = imageViews.get(photoToLoad.imageView);
//        // Check url is already exist in imageViews MAP
//        if (tag == null || !tag.equals(photoToLoad.url))
//            return true;
//        return false;
//    }
    /*
    Pris dans le code di Drive preferentiellement a l'existant.
     */
    boolean imageViewReused(PhotoToLoad photoToLoad) {

        String tag = imageViews.get(photoToLoad.imageView);
        // Check url is already exist in imageViews MAP
        if (tag == null || tag.equals(photoToLoad.url))
            return false;
//        if (tag == null || !tag.equals(photoToLoad.url))
//            return true;
        return true;
    }

    // Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        LoadCallback imageLoadListener;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p, LoadCallback imageLoader) {
            bitmap = b;
            photoToLoad = p;
            this.imageLoadListener = imageLoader;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;

            // Show bitmap on UI
            notifyImageLoaded(photoToLoad, imageLoadListener, bitmap);
        }
    }

    public void clearCache() {
        // Clear cache directory downloaded images and stored data in maps
        clearMemoryCache();
        fileCache.clear();
    }

    public void clearMemoryCache() {
        imageViews.clear();
        memoryCache.clear();
    }

    public void purgeMemory() {
        imageViews.clear();
        memoryCache.purge();
        if (errorBitmapHolder != null) {
            errorBitmapHolder = null;
        }
        if (progressionBitmapHolder != null) {
            progressionBitmapHolder = null;
        }
    }

    public void cancel() {
        cancel(true);
    }

    //TODO implementer la même gestion des Executor que sur le Drive.
//    public void stop() {
//        if (executorService != null) {
//            executorService.shutdownNow();
////            executorService = Executors.newFixedThreadPool(SIZE_POOL_THREAD_REMOTE);
//        }
////        if (executorServiceLocalStorage != null) {
////            executorServiceLocalStorage.shutdownNow();
////            executorServiceLocalStorage = Executors.newFixedThreadPool(SIZE_POOL_THREAD_LOCAL);
////        }
//    }

    public boolean stop() {
        try {
            if (executorService != null) {
                return !executorService.shutdownNow().isEmpty();
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void cancel(boolean purge) {
        stop();
        if (purge) {
            //TODO il serait meilleurs d'utiliser un purge, mais le purge entraine des instabilité.
            // purgeMemory();
            clearMemoryCache();
        }
    }


    public void clearFileCache() {
        if (fileCache != null) {
            fileCache.clear();
        }
    }

    public void setUseFileCache(boolean useFileCache) {
        this.useFileCache = useFileCache;
    }

    public void setUseMemoryCache(boolean useMemoryCache) {
        this.useMemoryCache = useMemoryCache;
    }

    public boolean isUseFileCache() {
        return useFileCache;
    }

    public boolean isUseMemoryCache() {
        return useMemoryCache;
    }

    private boolean useFileCache = true;
    private boolean useMemoryCache = true;

    public Context getContext() {
        return context;
    }

    public interface LoadCallback {
        boolean onLoad(PhotoToLoad phLoad);

        /**
         * @param phLoad
         * @param bitmap
         * @return where there or it should'nt use default LoadCallback. if
         * it is false, default navigationListener will not be used; true
         * otherwise
         */
        boolean onLoadSucceed(PhotoToLoad phLoad, Bitmap bitmap);


        boolean onLoadError(PhotoToLoad phLoad, Throwable error);

        void onLoadCompleted(PhotoToLoad phLoad, boolean success);

    }

    private void notifyImageLoad(PhotoToLoad photoToLoad, LoadCallback listener) {
        if ((listener == null || (listener != null && !listener.onLoad(photoToLoad))) && (photoToLoad.imageView != null && progressionBitmapHolder != null)) {
            photoToLoad.imageView.setImageBitmap(progressionBitmapHolder);
        }
    }

    private void notifyImageLoadError(PhotoToLoad photoToLoad, LoadCallback listener, Throwable th) {
        if (listener != null) {
            listener.onLoadCompleted(photoToLoad, false);
        }
        if ((listener == null || (listener != null && !listener.onLoadError(photoToLoad, th))) && (photoToLoad.imageView != null && errorBitmapHolder != null)) {
            photoToLoad.imageView.setImageBitmap(errorBitmapHolder);
        }
    }

    private void notifyImageLoaded(PhotoToLoad photoToLoad, LoadCallback listener, Bitmap bitmap) {
        if (bitmap != null) {
            if (listener == null || !listener.onLoadSucceed(photoToLoad, bitmap)) {
                if (photoToLoad != null && photoToLoad.imageView != null) {
                    photoToLoad.imageView.setImageBitmap(bitmap);
                }
            }
        } else {
            notifyImageLoadError(photoToLoad, listener, new IOException("Unable to load image from: " + photoToLoad.url));
        }
        if (listener != null) {
            listener.onLoadCompleted(photoToLoad, bitmap != null);
        }
    }

    public Cache<Bitmap> getMemoryCache() {
        return memoryCache;
    }

    public boolean isMemoryCached(String tag) {
        return memoryCache.containsKey(tag);
    }

    public boolean isFileCached(String tag) {
        File file = fileCache.getFile(tag);
        if (file == null) {
            return false;
        }
        return file.exists();
    }

    public boolean isCached(String tag) {
        return isMemoryCached(tag) || isFileCached(tag);
    }

    public synchronized Bitmap getCachedBitmap(String url) {
        if (isUseMemoryCache() && memoryCache.containsKey(url)) {
            return memoryCache.get(url);
        }
        File file = fileCache.get(url);
        if (file == null) {
            return null;
        }
        boolean fileExist = file.exists();
        if (!fileExist) {
            return null;
        }
        return getBitmapFromPath(file.getAbsolutePath());
    }

//    public Cache< Bitmap> getCache() {
//        return new Cache<Bitmap>() {
//            @Override
//            public Bitmap put(String key, Bitmap value) {
//                return null;
//            }
//
//            @Override
//            public Bitmap remove(String filePath) {
//                return null;
//            }
//
//            @Override
//            public boolean containsKey(String filePath) {
//                return false;
//            }
//
//            @Override
//            public Bitmap get(String name) {
//                return null;
//            }
//
//            @Override
//            public int size() {
//                return 0;
//            }
//
//            @Override
//            public void clear() {
//
//            }
//
//            @Override
//            public boolean isEmpty() {
//                return false;
//            }
//
//            @Override
//            public Set<String> keySet() {
//                return null;
//            }
//        };
//    }

}