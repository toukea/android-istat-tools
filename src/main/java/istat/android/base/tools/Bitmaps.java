package istat.android.base.tools;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

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
public class Bitmaps {
    public static Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static InputStream toInputStream(Bitmap bitmap) {
        int size = bitmap.getHeight() * bitmap.getRowBytes();
        ByteBuffer buffer = ByteBuffer.allocate(size);
        bitmap.copyPixelsToBuffer(buffer);
        return new ByteArrayInputStream(buffer.array());
    }

    public static Bitmap getBitmapFromURL(String url, boolean isPurgeable, boolean isScaled, int sampleSize) {

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inPurgeable = isPurgeable;
        o.inSampleSize = sampleSize;
        o.inScaled = isScaled;
        // Bitmap im = BitmapFactory.decodeFile(Uri.parse(value).getPath(),o);
        // final Bitmap im;
        try {
            return BitmapFactory.decodeStream(new URL(url).openStream(), null, o);

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

    public static Bitmap getBitmapFromURL(String url) {
        Bitmap im = null;
        try {

            im = BitmapFactory.decodeFile(new URL(url).getFile());
        } catch (Exception e) {
        }
        return im;
    }

    public static Bitmap getBitmapFromPath(String path, boolean isPurgeable, boolean isScaled, int isSampleSize) {

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inPurgeable = isPurgeable;
        o.inSampleSize = isSampleSize;
        o.inScaled = isScaled;
        // Bitmap im = BitmapFactory.decodeFile(Uri.parse(value).getPath(),o);
        // final Bitmap im;
        try {
            return BitmapFactory.decodeStream(new URL(path).openStream(), null, o);

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

    public static Bitmap getBitmapFromPath(String path) {
        Bitmap im = null;
        try {
            im = BitmapFactory.decodeFile(path);
        } catch (Exception e) {
        }
        return im;
    }

    public static Bitmap getBitmapFromResource(Context ctx, int resource) {
        Bitmap im = null;
        try {
            im = BitmapFactory.decodeResource(ctx.getResources(), resource);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return im;
    }

    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;

        if (bmp.getWidth() != radius || bmp.getHeight() != radius) {
            float smallest = Math.min(bmp.getWidth(), bmp.getHeight());
            float factor = smallest / radius;
            sbmp = Bitmap.createScaledBitmap(bmp,
                    (int) (bmp.getWidth() / factor),
                    (int) (bmp.getHeight() / factor), false);
        } else {
            sbmp = bmp;
        }

        Bitmap output = Bitmap.createBitmap(radius, radius, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final String color = "#BAB399";
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, radius, radius);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor(color));
        canvas.drawCircle(radius / 2, radius / 2,
                radius / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);

        return output;
    }

    public static int getLiteDominantColor(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
        final int color = newBitmap.getPixel(0, 0);
        newBitmap.recycle();
        return color;
    }

    public static int getDominantColor(Bitmap bitmap) {
        return getDominantColor(bitmap, 0xFF000000);
    }

    public static int getDominantColor(Bitmap bitmap, int alpha) {
        if (bitmap == null) {
            return Color.TRANSPARENT;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int pixels[] = new int[size];
        //Bitmap bitmap2 = bitmap.copy(Bitmap.Config.ARGB_4444, false);
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int color;
        int r = 0;
        int g = 0;
        int b = 0;
        int a;
        int count = 0;
        for (int i = 0; i < pixels.length; i++) {
            color = pixels[i];
            a = Color.alpha(color);
            if (a > 0) {
                r += Color.red(color);
                g += Color.green(color);
                b += Color.blue(color);
                count++;
            }
        }
        r /= count;
        g /= count;
        b /= count;
        r = (r << 16) & 0x00FF0000;
        g = (g << 8) & 0x0000FF00;
        b = b & 0x000000FF;
        color = alpha | r | g | b;
        return color;
    }


}
