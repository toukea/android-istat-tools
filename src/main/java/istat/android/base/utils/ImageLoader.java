package istat.android.base.utils;

import istat.android.base.memories.FileCache;
import istat.android.base.memories.MemoryCache;
import istat.android.base.tools.Bitmaps;
import istat.android.base.tools.ToolKits;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

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
public class ImageLoader {

	public static int QUALITY_HIGH = 0, QUALITY_LOW = 1;
	final static int DEFAULT_PICTURE_ON_PROGRESS = android.R.drawable.ic_dialog_info,
			DEFAULT_PICTURE_ON_ERROR = android.R.drawable.ic_dialog_alert;
	Bitmap progressionBitmapHolder, errorBitmapHolder;
	int imageQuality = QUALITY_HIGH;
	// Initialize MemoryCache
	public static final MemoryCache DEFAULT_MEMORY_CACHE = new MemoryCache();
	MemoryCache memoryCache = DEFAULT_MEMORY_CACHE;
	FileCache fileCache;
	Context context;
	// Create Map (collection) to store image and image url in key value pair
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	private ImageLoadListener mLoadListener = getDefaultLoadListener();
	ExecutorService executorService;
	// handler to display images in UI thread
	Handler handler = new Handler();

	public ImageLoader(Context context) {
		this(context,
				BitmapFactory.decodeResource(context.getResources(), DEFAULT_PICTURE_ON_ERROR)
				, BitmapFactory.decodeResource(context.getResources(), DEFAULT_PICTURE_ON_PROGRESS)
		);

	}

	public ImageLoader(Context context, int errorIcon, int progressIcon) {
		this(context);
		this.errorBitmapHolder = BitmapFactory.decodeResource(context.getResources(), errorIcon);
		this.errorBitmapHolder = BitmapFactory.decodeResource(context.getResources(), progressIcon);

	}

	public ImageLoader(Context context, Bitmap errorIcon, Bitmap progressIcon) {
		this.context = context;
		fileCache = new FileCache(this.context);

		// Creates a thread pool that reuses a fixed number of
		// threads operating off a shared unbounded queue.
		executorService = Executors.newFixedThreadPool(5);
		this.errorBitmapHolder = errorIcon;
		this.errorBitmapHolder = progressIcon;

	}

	public void setFileCache(FileCache DriveCache) {
		if (DriveCache == null) {
			DriveCache = new FileCache(this.context);
		}
		this.fileCache = DriveCache;
	}

	public void setMemoryCache(MemoryCache memoryCache) {
		if (memoryCache == null) {
			memoryCache = DEFAULT_MEMORY_CACHE;
		}
		this.memoryCache = memoryCache;
	}

	public void setImageQuality(int imageQuality) {
		this.imageQuality = imageQuality;
	}

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

	public void setLoadListener(ImageLoadListener loadCallback) {
		this.mLoadListener = loadCallback;
	}

	public Bitmap getErrorIcon() {
		return errorBitmapHolder;
	}

	public Bitmap getProgressIcon() {
		return progressionBitmapHolder;
	}

	public ImageLoadListener getLoadListener() {
		return mLoadListener;
	}

	public FileCache getFileCache() {
		return fileCache;
	}

	public void displayImage(int resourceId, ImageView imageView) {
		displayImage(resourceId + "", imageView);
	}

	public void preLoadImage(String path) {
		displayImage(path, null, mLoadListener);
	}

	public void preLoadImage(String path, ImageLoadListener mLoadListener) {
		displayImage(path, null, mLoadListener);
	}

	public void displayImage(String path, ImageView imageView) {
		displayImage(path, imageView, mLoadListener);
	}

	public void displayImage(String path, ImageView imageView, ImageLoadListener mLoadListener) {
		// Store image and url in Map
		imageViews.put(imageView, path);

		// Check image is stored in MemoryCache Map or not (see
		// MemoryCache.java)
		Bitmap bitmap = memoryCache.get(path);

		if (bitmap != null) {
			// if image is stored in MemoryCache Map then
			// Show image in listview row
			// imageView.setImageBitmap(bitmap);
			if (mLoadListener == null || !mLoadListener.onLoadFinish(new PhotoToLoad(path, imageView),
					bitmap)) {
				getDefaultLoadListener().onLoadFinish(
						new PhotoToLoad(path, imageView), bitmap);
			}
		} else {
			// queue Photo to download create url
			queuePhoto(path, imageView, mLoadListener);

			// Before downloading image show default image
			// imageView.setImageResource(PICTURE_ON_PROGRESS);
			if (mLoadListener == null || !mLoadListener.onLoad(new PhotoToLoad(path, imageView))) {
				getDefaultLoadListener().onLoad(new PhotoToLoad(path, imageView));
			}
		}
	}

	private void queuePhoto(String url, ImageView imageView, ImageLoadListener mLoadListener) {
		// Store image and url in PhotoToLoad object
		PhotoToLoad p = new PhotoToLoad(url, imageView);

		// pass PhotoToLoad object to PhotosLoader runnable class
		// and submit PhotosLoader runnable to executers to run runnable
		// Submits a PhotosLoader runnable task for execution
		Runnable photoToLoadRunnable = new PhotosLoader(p, mLoadListener);
		executorService.submit(photoToLoadRunnable);

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
		ImageLoadListener loadListener;

		PhotosLoader(PhotoToLoad photoToLoad, ImageLoadListener loadListener) {
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
				Bitmap bmp = getBitmap(photoToLoad.url);

				// set image data in Memory Cache
				memoryCache.put(photoToLoad.url, bmp);

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
				// if (!mLoadListener.onLoadFinish(photoToLoad, null)) {
				getDefaultLoadListener().onLoadFinish(photoToLoad, null);
				//    }
			}
		}
	}

	public void setResourceConnectionHandler(ResourceConnectionHandler mResourceConnectionHandler) {
		this.mResourceConnectionHandler = mResourceConnectionHandler;
	}

	private ResourceConnectionHandler mResourceConnectionHandler = new ResourceConnectionHandler() {
		InputStream is = null;
		URLConnection urlConnection;

		@Override
		public InputStream onConnect(String url) {
			try {
				URL imageUrl = new URL(url);
				URLConnection urlConnection = imageUrl.openConnection();
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
			if (urlConnection instanceof HttpURLConnection)
				((HttpURLConnection) urlConnection).disconnect();
		}
	};

	public interface ResourceConnectionHandler {
		InputStream onConnect(String url);

		void onDisconnect(String url, InputStream inputStream);
	}

	private Bitmap getBitmap(String url) {

		if (ToolKits.WordFormat.isInteger(url))
			return getBitmapFromResource(context,
					Integer.valueOf(url));

		File f = fileCache.getFile(url);

		// create SD cache
		// CHECK : if trying to decode file which not exist in cache return null
		Bitmap b;
		if (imageQuality == QUALITY_LOW)
			b = decodeFile(f);
		else
			b = getBitmapFromPath(f.getAbsolutePath());

		if (b != null)
			return b;

		// Download image file create web
		try {
			Bitmap bitmap;
			InputStream is = mResourceConnectionHandler.onConnect(url);
			OutputStream os = new FileOutputStream(f);
			ToolKits.Stream.copyStream(is, os);
			os.close();
			mResourceConnectionHandler.onDisconnect(url, is);
			if (imageQuality == QUALITY_LOW) {
				bitmap = decodeFile(f);
			} else {
				FileInputStream bitmapStream = new FileInputStream(f);
				bitmap = BitmapFactory.decodeStream(bitmapStream);
				bitmapStream.close();
			}
			return bitmap;
		} catch (Throwable ex) {
			ex.printStackTrace();
			if (ex instanceof OutOfMemoryError) {
				memoryCache.clear();
			}
			return null;
		}


	}

	public static Bitmap getBitmap(String URL, Context context, int Quality) {
		if (ToolKits.WordFormat.isInteger(URL))
			return getBitmapFromResource(context,
					Integer.valueOf(URL));

		File f = new FileCache(context).getFile(URL);

		// create SD cache
		// CHECK : if trying to decode file which not exist in cache return null
		Bitmap b;

		if (Quality == QUALITY_LOW)
			b = decodeFile(f);
		else
			b = getBitmapFromPath(f.getAbsolutePath());

		if (b != null)
			return b;

		// Download image file create web
		try {

			Bitmap bitmap = null;
			InputStream is;
			URL imageUrl = new URL(URL);
			URLConnection urlConnection = imageUrl.openConnection();
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
			OutputStream os = new FileOutputStream(f);
			ToolKits.Stream.copyStream(is, os);
			os.close();
			if (urlConnection instanceof HttpURLConnection)
				((HttpURLConnection) urlConnection).disconnect();
			if (Quality == QUALITY_LOW)
				bitmap = decodeFile(f);
			else
				bitmap = getBitmapFromPath(f.getAbsolutePath());

			return bitmap;

		} catch (Throwable ex) {

			Log.e("error", "" + ex);
			return null;
		}
	}

	public static Bitmap getBitmap(String URL, FileCache DriveCache, int Quality) {
		if (ToolKits.WordFormat.isInteger(URL))
			return getBitmapFromResource(DriveCache.getContext(),
					Integer.valueOf(URL));

		File f = DriveCache.getFile(URL);

		// create SD cache
		// CHECK : if trying to decode file which not exist in cache return null
		Bitmap b;

		if (Quality == QUALITY_LOW)
			b = decodeFile(f);
		else
			b = getBitmapFromPath(f.getAbsolutePath());

		if (b != null)
			return b;

		// Download image file create web
		try {

			Bitmap bitmap = null;
			InputStream is;
			URL imageUrl = new URL(URL);
			URLConnection urlConnection = imageUrl.openConnection();
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
			OutputStream os = new FileOutputStream(f);
			ToolKits.Stream.copyStream(is, os);
			os.close();
			if (urlConnection instanceof HttpURLConnection)
				((HttpURLConnection) urlConnection).disconnect();
			if (Quality == QUALITY_LOW)
				bitmap = decodeFile(f);
			else
				bitmap = getBitmapFromPath(f.getAbsolutePath());

			return bitmap;

		} catch (Throwable ex) {

			Log.e("error", "" + ex);
			return null;
		}
	}

	// Decodes image and scales it to reduce memory consumption
	public static Bitmap decodeFile(File f) {

		try {

			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			FileInputStream stream1 = new FileInputStream(f);
			BitmapFactory.decodeStream(stream1, null, o);
			stream1.close();

			// Find the correct scale value. It should be the power of 2.

			// Set width/height of recreated image
			final int REQUIRED_SIZE = 85;

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

		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {

		String tag = imageViews.get(photoToLoad.imageView);
		// Check url is already exist in imageViews MAP
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;
		ImageLoadListener imageLoader;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p, ImageLoadListener imageLoader) {
			bitmap = b;
			photoToLoad = p;
			this.imageLoader = imageLoader;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;

			// Show bitmap on UI
			if (imageLoader == null || !imageLoader.onLoadFinish(photoToLoad, bitmap)) {
				getDefaultLoadListener().onLoadFinish(photoToLoad, bitmap);
			}
		}
	}

	public void clearCache() {
		// Clear cache directory downloaded images and stored data in maps
		memoryCache.clear();
		fileCache.clear();
	}

	public void clearMemoryCache() {
		memoryCache.clear();
	}

	public void clearFileCache() {
		fileCache.clear();
	}

	public ImageLoadListener getDefaultLoadListener() {
		return new ImageLoadListener() {

			@Override
			public boolean onLoad(PhotoToLoad phLoad) {
				if (progressionBitmapHolder != null) {
					phLoad.imageView.setImageBitmap(progressionBitmapHolder);
				}
				return true;
			}

			@Override
			public boolean onLoadFinish(PhotoToLoad phLoad, Bitmap bitmap) {
				if (bitmap == null) {
					if (errorBitmapHolder != null) {
						bitmap = errorBitmapHolder;
						if (phLoad != null) {
							phLoad.imageView.setImageBitmap(bitmap);
						}
					}
				} else {
					if (phLoad != null) {
						phLoad.imageView.setImageBitmap(bitmap);
					}
				}
				return true;
			}

		};
	}

	public interface ImageLoadListener {
		boolean onLoad(PhotoToLoad phLoad);

		/**
		 * @param phLoad
		 * @param bitmap
		 * @return where there or it should'nt use default ImageLoadListener. if
		 * it is false, default navigationListener will not be used; true
		 * otherwise
		 */
		boolean onLoadFinish(PhotoToLoad phLoad, Bitmap bitmap);

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

	public static Bitmap getBitmapFromPath(String path) {
		Bitmap im = null;
		try {
			im = BitmapFactory.decodeFile(path);
		} catch (Exception e) {
		}
		return im;
	}

	public static Bitmap getBitmapFromResource(Context ctx, int resourceId) {
		Bitmap im = null;
		try {
			im = BitmapFactory.decodeResource(ctx.getResources(), resourceId);
		} catch (Exception e) {
		}
		new Callable<String>() {

			@Override
			public String call() throws Exception {
				return null;
			}
		};
		return im;
	}

	public Context getContext() {
		return context;
	}
}