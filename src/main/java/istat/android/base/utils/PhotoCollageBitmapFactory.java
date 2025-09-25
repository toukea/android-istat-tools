package istat.android.base.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class PhotoCollageBitmapFactory {

    public enum CropType {
        CENTER_CROP,
        FIT_CENTER
    }

    public static Bitmap createBitmap(List<Bitmap> bitmaps,
                                      Pair<Integer, Integer> outputWidthHeightPair) {
        return createBitmap(bitmaps, outputWidthHeightPair, CropType.CENTER_CROP, 1, 1, null);

    }

    public static Bitmap createBitmap(List<Bitmap> bitmaps,
                                      Pair<Integer, Integer> outputWidthHeightPair,
                                      CropType cropType) {
        return createBitmap(bitmaps, outputWidthHeightPair, cropType, 1, 1, null);

    }

    private static class RectBox {
        Rect rect;
        Bitmap bitmap;

        RectBox(Rect rect) {
            this.rect = rect;
        }
    }

    public static Bitmap createBitmap(List<Bitmap> bitmaps,
                                      Pair<Integer, Integer> outputWidthHeightPair,
                                      CropType cropType,
                                      int marginHorizontal,
                                      int marginVertical,
                                      Integer backgroundColor) {
        if (bitmaps == null || bitmaps.isEmpty()) return null;

        int width = outputWidthHeightPair.first;
        int height = outputWidthHeightPair.second;

        List<Rect> regions = generateSubdivisions(width, height, bitmaps.size());

        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        // Background
        if (backgroundColor != null) {
            canvas.drawColor(backgroundColor);
        } else {
            canvas.drawColor(Color.TRANSPARENT);
        }

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setFilterBitmap(true);

        for (int i = 0; i < regions.size(); i++) {
            if (i >= bitmaps.size()) break;

            Rect target = applyMargins(regions.get(i), marginHorizontal, marginVertical);
            Bitmap source = bitmaps.get(i);
            Rect srcRect = computeCropRect(source, cropType);

            canvas.drawBitmap(source, srcRect, target, paint);
        }

        return result;
    }

    private static List<Rect> generateSubdivisions(int width, int height, int count) {
        List<Rect> result = new ArrayList<>();
        result.add(new Rect(0, 0, width, height));

        while (result.size() < count) {
            // Find largest rect
            int largestIndex = 0;
            int largestArea = 0;
            for (int i = 0; i < result.size(); i++) {
                Rect r = result.get(i);
                int area = (r.right - r.left) * (r.bottom - r.top);
                if (area > largestArea) {
                    largestArea = area;
                    largestIndex = i;
                }
            }
            Rect toSplit = result.remove(largestIndex);
            boolean splitVertically = (toSplit.width() >= toSplit.height());

            if (splitVertically) {
                int midX = toSplit.left + toSplit.width() / 2;
                result.add(new Rect(toSplit.left, toSplit.top, midX, toSplit.bottom));
                result.add(new Rect(midX, toSplit.top, toSplit.right, toSplit.bottom));
            } else {
                int midY = toSplit.top + toSplit.height() / 2;
                result.add(new Rect(toSplit.left, toSplit.top, toSplit.right, midY));
                result.add(new Rect(toSplit.left, midY, toSplit.right, toSplit.bottom));
            }
        }

        return result;
    }

    private static Rect applyMargins(Rect base, int hMargin, int vMargin) {
        return new Rect(
                base.left + hMargin,
                base.top + vMargin,
                base.right - hMargin,
                base.bottom - vMargin
        );
    }

    private static Rect computeCropRect(Bitmap bitmap, CropType cropType) {
        int bw = bitmap.getWidth();
        int bh = bitmap.getHeight();

        if (cropType == CropType.CENTER_CROP) {
            int size = Math.min(bw, bh);
            int left = (bw - size) / 2;
            int top = (bh - size) / 2;
            return new Rect(left, top, left + size, top + size);
        } else {
            return new Rect(0, 0, bw, bh);
        }
    }
}
