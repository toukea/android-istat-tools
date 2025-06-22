package istat.android.base.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
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
        return createBitmap(bitmaps, outputWidthHeightPair, CropType.CENTER_CROP, 1, 1, null);

    }

    public static Bitmap createBitmap(List<Bitmap> bitmaps,
                                      Pair<Integer, Integer> outputWidthHeightPair,
                                      CropType cropType) {
        return createBitmap(bitmaps, outputWidthHeightPair, cropType, 1, 1, null);

    }

    public static Bitmap createBitmap(List<Bitmap> bitmaps,
                                      Pair<Integer, Integer> outputWidthHeightPair,
                                      CropType cropType,
                                      int marginHorizontal,
                                      int marginVertical,
                                      Integer backgroundColor) {

        if (bitmaps == null || bitmaps.isEmpty()) return null;

        int outWidth = outputWidthHeightPair.first;
        int outHeight = outputWidthHeightPair.second;

        if (marginHorizontal * 2 >= outWidth || marginVertical * 2 >= outHeight) {
            throw new IllegalArgumentException("Margins are too large compared to the output size.");
        }

        int total = bitmaps.size();
        Bitmap result = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        if (backgroundColor != null) {
            result.eraseColor(backgroundColor);
        }

        Canvas canvas = new Canvas(result);

        int index = 0;
        switch (total) {
            case 1: {
                drawBitmap(canvas, bitmaps.get(index++), applyMargin(new Rect(0, 0, outWidth, outHeight), marginHorizontal, marginVertical), cropType);
                break;
            }
            case 2: {
                int w = outWidth / 2;
                drawBitmap(canvas, bitmaps.get(index++), applyMargin(new Rect(0, 0, w, outHeight), marginHorizontal, marginVertical), cropType);
                drawBitmap(canvas, bitmaps.get(index++), applyMargin(new Rect(w, 0, outWidth, outHeight), marginHorizontal, marginVertical), cropType);
                break;
            }
            case 3: {
                int w = outWidth / 2;
                int h = outHeight / 2;
                drawBitmap(canvas, bitmaps.get(index++), applyMargin(new Rect(0, 0, w, outHeight), marginHorizontal, marginVertical), cropType);
                drawBitmap(canvas, bitmaps.get(index++), applyMargin(new Rect(w, 0, outWidth, h), marginHorizontal, marginVertical), cropType);
                drawBitmap(canvas, bitmaps.get(index++), applyMargin(new Rect(w, h, outWidth, outHeight), marginHorizontal, marginVertical), cropType);
                break;
            }
            case 4: {
                int w = outWidth / 2;
                int h = outHeight / 2;
                drawBitmap(canvas, bitmaps.get(index++), applyMargin(new Rect(0, 0, w, h), marginHorizontal, marginVertical), cropType);
                drawBitmap(canvas, bitmaps.get(index++), applyMargin(new Rect(0, h, w, outHeight), marginHorizontal, marginVertical), cropType);
                drawBitmap(canvas, bitmaps.get(index++), applyMargin(new Rect(w, 0, outWidth, h), marginHorizontal, marginVertical), cropType);
                drawBitmap(canvas, bitmaps.get(index++), applyMargin(new Rect(w, h, outWidth, outHeight), marginHorizontal, marginVertical), cropType);
                break;
            }
            case 5: {
                int w = outWidth / 3;
                int h = outHeight / 2;
                drawBitmap(canvas, bitmaps.get(index++), applyMargin(new Rect(0, 0, w, h), marginHorizontal, marginVertical), cropType);
                drawBitmap(canvas, bitmaps.get(index++), applyMargin(new Rect(w, 0, 2 * w, h), marginHorizontal, marginVertical), cropType);
                drawBitmap(canvas, bitmaps.get(index++), applyMargin(new Rect(0, h, w, outHeight), marginHorizontal, marginVertical), cropType);
                drawBitmap(canvas, bitmaps.get(index++), applyMargin(new Rect(w, h, 2 * w, outHeight), marginHorizontal, marginVertical), cropType);
                drawBitmap(canvas, bitmaps.get(index++), applyMargin(new Rect(2 * w, h, outWidth, outHeight), marginHorizontal, marginVertical), cropType);
                break;
            }
            case 6: {
                int w = outWidth / 3;
                int h = outHeight / 2;
                drawBitmap(canvas, bitmaps.get(index++), applyMargin(new Rect(0, 0, w, h), marginHorizontal, marginVertical), cropType);
                drawBitmap(canvas, bitmaps.get(index++), applyMargin(new Rect(w, 0, 2 * w, h), marginHorizontal, marginVertical), cropType);
                drawBitmap(canvas, bitmaps.get(index++), applyMargin(new Rect(2 * w, 0, outWidth, h), marginHorizontal, marginVertical), cropType);
                drawBitmap(canvas, bitmaps.get(index++), applyMargin(new Rect(0, h, w, outHeight), marginHorizontal, marginVertical), cropType);
                drawBitmap(canvas, bitmaps.get(index++), applyMargin(new Rect(w, h, 2 * w, outHeight), marginHorizontal, marginVertical), cropType);
                drawBitmap(canvas, bitmaps.get(index++), applyMargin(new Rect(2 * w, h, outWidth, outHeight), marginHorizontal, marginVertical), cropType);
                break;
            }
            case 7:
            case 8:
            case 9:
            case 10: {
                int columns = 3;
                int rows = (int) Math.ceil(total / 3.0);
                int cellWidth = outWidth / columns;
                int cellHeight = outHeight / rows;
                for (int r = 0; r < rows && index < total; r++) {
                    for (int c = 0; c < columns && index < total; c++) {
                        int left = c * cellWidth;
                        int top = r * cellHeight;
                        int right = left + cellWidth;
                        int bottom = top + cellHeight;
                        drawBitmap(canvas, bitmaps.get(index++), applyMargin(new Rect(left, top, right, bottom), marginHorizontal, marginVertical), cropType);
                    }
                }
                break;
            }
            default: {
                // Fallback pour 11 et +
                int columns = (int) Math.ceil(Math.sqrt(total));
                int rows = (int) Math.ceil((double) total / columns);
                int cellWidth = outWidth / columns;
                int cellHeight = outHeight / rows;

                for (int r = 0; r < rows && index < total; r++) {
                    for (int c = 0; c < columns && index < total; c++) {
                        int left = c * cellWidth;
                        int top = r * cellHeight;
                        int right = left + cellWidth;
                        int bottom = top + cellHeight;
                        drawBitmap(canvas, bitmaps.get(index++), applyMargin(new Rect(left, top, right, bottom), marginHorizontal, marginVertical), cropType);
                    }
                }
                break;
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
