package istat.android.base.tools;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;

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
public class Bitmaps {
	public static Bitmap getBitmapFromURL(String url,boolean isPurgeable,boolean isScaled,int sampleSize){	
		
		BitmapFactory.Options o=new BitmapFactory.Options();
			o.inPurgeable=isPurgeable;
			o.inSampleSize=sampleSize;
			o.inScaled=isScaled;
		    	 // Bitmap im = BitmapFactory.decodeFile(Uri.parse(value).getPath(),o);
		    	// final Bitmap im;
			try {
				return BitmapFactory.decodeStream(new URL(url).openStream(), null,o);
				
				//v.post(new Runnable(){public void run(){v.setImageBitmap(im);}});
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return Bitmap.createBitmap(240, 320, Config.ARGB_8888);	
}
public static Bitmap getBitmapFromURL(String url){
		Bitmap   im=null;
		 try {
			 
		    	  im = BitmapFactory.decodeFile(new URL(url).getFile());
		   	} catch (Exception e) {
		   	}
		return im;
	}
public static Bitmap getBitmapFromPath(String path,boolean isPurgeable,boolean isScaled,int isSampleSize){	
		
		BitmapFactory.Options o=new BitmapFactory.Options();
			o.inPurgeable=isPurgeable;
			o.inSampleSize=isSampleSize;
			o.inScaled=isScaled;
		    	 // Bitmap im = BitmapFactory.decodeFile(Uri.parse(value).getPath(),o);
		    	// final Bitmap im;
			try {
				return BitmapFactory.decodeStream(new URL(path).openStream(), null,o);
				
				//v.post(new Runnable(){public void run(){v.setImageBitmap(im);}});
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return Bitmap.createBitmap(240, 320, Config.ARGB_8888);	
}

	public static Bitmap getBitmapFromPath(String path){
	Bitmap   im=null;
	 try {
	    	  im = BitmapFactory.decodeFile(path);
	   	} catch (Exception e) {
	   	}
	return im;
}
	public static Bitmap getBitmapFromRessource(Context ctx,int ressource){
 		Bitmap   im=null;
 		 try {
 		    	  im = BitmapFactory.decodeResource(ctx.getResources(),ressource);
 		   	} catch (Exception e) {
 		   	}
 		return im;
 	}
	
	
}
