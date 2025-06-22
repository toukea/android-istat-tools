package istat.android.base.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Pair;

import java.util.List;

public class PhotoCollageBitmapFactory {

    public enum CropType {
        CENTER_CROP,
        FIT_CENTER
    }

    public static Bitmap createBitmap(List<Bitmap> bitmaps,
                                      Pair<Integer, Integer> outputWidthHeightPair) {
        return createBitmap(bitmaps, outputWidthHeightPair, CropType.CENTER_CROP, 1, 1);

    }

    public static Bitmap createBitmap(List<Bitmap> bitmaps,
                                      Pair<Integer, Integer> outputWidthHeightPair,
                                      CropType cropType) {
        return createBitmap(bitmaps, outputWidthHeightPair, cropType, 1, 1);

    }

    public static Bitmap createBitmap(List<Bitmap> bitmaps,
                                      Pair<Integer, Integer> outputWidthHeightPair,
                                      CropType cropType,
                                      int marginHorizontal,
                                      int marginVertical) {

        if (bitmaps == null || bitmaps.isEmpty()) return null;

        int outWidth = outputWidthHeightPair.first;
        int outHeight = outputWidthHeightPair.second;
        int total = bitmaps.size();

        Bitmap result = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        if (total == 1) {
            Rect dst = applyMargin(new Rect(0, 0, outWidth, outHeight), marginHorizontal, marginVertical);
            drawBitmap(canvas, bitmaps.get(0), dst, cropType);
        } else if (total == 2) {
            Rect dst1 = applyMargin(new Rect(0, 0, outWidth / 2, outHeight), marginHorizontal, marginVertical);
            Rect dst2 = applyMargin(new Rect(outWidth / 2, 0, outWidth, outHeight), marginHorizontal, marginVertical);
            drawBitmap(canvas, bitmaps.get(0), dst1, cropType);
            drawBitmap(canvas, bitmaps.get(1), dst2, cropType);
        } else {
            // Première image en haut
            int firstHeight = outHeight / 2;
            Rect firstRect = applyMargin(new Rect(0, 0, outWidth, firstHeight), marginHorizontal, marginVertical);
            drawBitmap(canvas, bitmaps.get(0), firstRect, cropType);

            // Images restantes
            int remaining = total - 1;
            int columns = Math.max(2, (int) Math.ceil(Math.sqrt(remaining)));
            int rows = (int) Math.ceil((double) remaining / columns);

            int totalHMargin = (columns + 1) * marginHorizontal;
            int totalVMargin = (rows + 1) * marginVertical;
            int availableHeight = outHeight - firstHeight;

            int cellWidth = (outWidth - totalHMargin) / columns;
            int cellHeight = (availableHeight - totalVMargin) / rows;

            int index = 1;
            for (int row = 0; row < rows && index < total; row++) {
                for (int col = 0; col < columns && index < total; col++) {
                    int left = marginHorizontal + col * (cellWidth + marginHorizontal);
                    int top = firstHeight + marginVertical + row * (cellHeight + marginVertical);
                    int right = left + cellWidth;
                    int bottom = top + cellHeight;

                    Rect dst = new Rect(left, top, right, bottom);
                    drawBitmap(canvas, bitmaps.get(index), dst, cropType);
                    index++;
                }
            }
        }

        return result;
    }

    private static Rect applyMargin(Rect rect, int marginH, int marginV) {
        return new Rect(
                rect.left + marginH,
                rect.top + marginV,
                rect.right - marginH,
                rect.bottom - marginV
        );
    }

    private static void drawBitmap(Canvas canvas, Bitmap src, Rect dstRect, CropType cropType) {
        Rect srcRect = calculateSrcRect(src.getWidth(), src.getHeight(), dstRect.width(), dstRect.height(), cropType);
        canvas.drawBitmap(src, srcRect, dstRect, null);
    }

    private static Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight, CropType cropType) {
        if (cropType == CropType.FIT_CENTER) {
            return new Rect(0, 0, srcWidth, srcHeight);
        }

        float srcAspect = (float) srcWidth / srcHeight;
        float dstAspect = (float) dstWidth / dstHeight;

        if (srcAspect > dstAspect) {
            int cropWidth = (int) (srcHeight * dstAspect);
            int left = (srcWidth - cropWidth) / 2;
            return new Rect(left, 0, left + cropWidth, srcHeight);
        } else {
            int cropHeight = (int) (srcWidth / dstAspect);
            int top = (srcHeight - cropHeight) / 2;
            return new Rect(0, top, srcWidth, top + cropHeight);
        }
    }
}
