package istat.android.base.tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

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
public final class ToolKits {
    public static class Appearance {
        public static void changeTypeFaceFromAsset(Context context,
                                                   String facePath, TextView tv) {
            Typeface face = Typeface.createFromAsset(context.getAssets(),
                    facePath);
            tv.setTypeface(face);
        }

        public static void changeTypeFace(File file, TextView tv) {
            Typeface face = Typeface.createFromFile(file);
            tv.setTypeface(face);
        }

        public static void changeTypeFace(String file, TextView tv) {
            Typeface face = Typeface.createFromFile(file);
            tv.setTypeface(face);
        }

    }

    public static class Network {
        public static boolean isNetworkConnected(Context context) {
            final ConnectivityManager conMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected()) {
                return true;
            } else {
                return false;
            }
        }
    }

    // ------------------------------------------------------------------

    public static class Word {

        // public class LocalesList {
        //
        // static public void main(String[] args) {
        //
        // Locale list[] = SimpleDateFormat.getAvailableLocales();
        // Set set = new TreeSet();
        // for (int i = 0; i < list.length; i++) {
        // set.add(list[i].getDisplayName()
        // +"\t\t\t:\t"+ list[i].toString());
        // }
        // Iterator it = set.iterator();
        // while (it.hasNext()) {
        // System.out.println(it.next() );
        // }
        // }
        // }
        public static boolean isEquals(String word1, String word2) {
            boolean out = true;
            char[] c1 = word1.toCharArray();
            char[] c2 = word2.toCharArray();
            int i = 0;
            for (char c : c1) {
                if (c != c2[i]) {
                    return false;
                }
                i++;
            }
            return out;
        }

        public static String toSentense(String word, String endingPontuation) {
            word = beginByUperCase(word);
            int index = word.indexOf(endingPontuation);
            // if(index!=word.length()-1)return word+endingPontuation;
            if (index <= 0)
                return word + endingPontuation;

            return word;
        }

        public static String beginByUperCase(String word) {
            if (word.length() > 1) {
                String beggin = word.substring(0, 1).toUpperCase(
                        Locale.getDefault());
                word = beggin + word.substring(1);
            }
            return word;
        }

        public static int parseInt(String s) {
            if (TextUtils.isEmpty(s))
                return 0;
            if (s.trim().length() == 0)
                return 0;
            try {
                return Integer.parseInt(s.trim());
            } catch (Exception e) {
                return 0;
            }
        }

        public static String numberToWords(long number, Locale locale) {
            if (locale.equals(Locale.FRANCE) || locale.equals(Locale.FRENCH)) {
                return FrenchNumberToWords.convert(number);
            }
            return EnglishNumberToWords.convert(number);

        }

        public static String wordToNumber(String word, Locale locale) {
            if (locale.equals(Locale.FRANCE) || locale.equals(Locale.FRENCH)) {
                return EnglishWordToNumber.replaceNumbers(word);
            }
            return EnglishWordToNumber.replaceNumbers(word);

        }

        public static String ShortWord(String word, int max) {
            if (word.length() <= max)
                return word;
            return word.substring(0, max) + "...";
        }

        public static String DistanceToKm(String word) {
            int d = (int) (Double.valueOf(word) * 1);
            if (word.equals("-1"))
                return "---";
            if (d > 1500)
                return "" + (int) (d / 1000) + "Km";
            else
                return d + "m";
        }

        public static float getpercentNumericValue(int progresstate, int STEP,
                                                   boolean comma) {
            if (comma)
                return ((float) (progresstate) * 100 / (float) (STEP));
            else
                return (int) ((float) (progresstate) * 100 / (float) (STEP));
        }

        public static String getpercentValue(int progresstate, int STEP,
                                             boolean comma) {
            if (comma)
                return ((float) (progresstate) * 100 / (float) (STEP)) + "%";
            else
                return (int) ((float) (progresstate) * 100 / (float) (STEP))
                        + "%";
        }

        public static String getpercentValue(int progresstate, int STEP) {
            return ((float) (progresstate) * 100 / (float) (STEP)) + "%";
        }

        public static float getpercentNumericValue(int progresstate, int STEP) {
            return ((float) (progresstate) * 100 / (float) (STEP));
        }

        /**
         * SweetNumber is used in order to get sweet Number definit like: if a<9
         * return "0a" else return just the number a
         *
         * @param a
         * @return a String that represent a "sweet" numeric value of that
         * number.
         */
        public static String sweetNumber(int a) {
            if (a > 9)
                return "" + a;
            else
                return "0" + a;
        }

        public static String adjustNumber(float a) {
            if ((int) a == a)
                return "" + (int) a;
            else
                return "" + a;
        }

    }

    public static class WordFormat {

        // String.format("%1$s %2$s %2$s %3$s", "a", "b", "c");
        public static boolean isEmail(String input) {
            // On d�clare le pattern que l�on doit suivre
            Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
            // On d�clare un matcher, qui comparera le pattern avec la string
            // pass�e en argument
            Matcher m = p.matcher(input);
            // Si l�adresse mail saisie ne correspond au format d�une adresse
            // mail
            return m.matches();
        }

        public static boolean isNumber(String a) {
            try {
                a = "" + Double.valueOf(a);
                return true;
            } catch (Exception e) {
                return false;
            }

        }

        public static boolean isInteger(String a) {
            try {
                a = "" + Integer.valueOf(a);
                return true;
            } catch (Exception e) {
                return false;
            }

        }

        @SuppressWarnings("deprecation")
        public static boolean isDate(String a) {
            try {
                new Date(a);
                return true;
            } catch (Exception e) {
                return false;
            }

        }
    }

    public static class Dates {

        public static boolean isDatePast(String datein) {

            try {
                Date date = new Date(datein);
                int compare = date.compareTo(new Date());
                Log.d("wordutil_isDate", "compare=" + compare);
                if (compare <= 0)
                    return true;
                else
                    return false;
            } catch (Exception e) {
                return true;
            }
        }

        public static Date addToDate(Date date1, long add) {
            return new Date(new Date().getTime() + add);
        }

        public static String smartSimpleTime() {

            Date date = new Date();
            SimpleDateFormat f = new SimpleDateFormat("HH:mm");
            return f.format(date);
        }

        public static String simpleDateTimePlus(long add) {

            Date date = new Date(new Date().getTime() + add);
            SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return f.format(date);
        }

        public static String simpleDateTime(long tSpan) {
            Date date = new Date(tSpan);
            SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return f.format(date);
        }

        // ---------------------------------------------------
        public static String simpleDate() {

            Date date = new Date();
            SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd");
            return f.format(date);
        }

        public static String simpleDateTime() {

            Date date = new Date();
            SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return f.format(date);
        }

    }

    // ------------------------------------------------------------------
    public static class Stream {

        public static String streamToString(java.io.InputStream inp,
                                            String encoding) {
            String out = "";
            StringBuilder total = new StringBuilder();
            String line;
            try {
                BufferedReader r = new BufferedReader(new InputStreamReader(
                        inp, encoding));
                while ((line = r.readLine()) != null) {
                    total.append(line);
                }
                out = total.toString();
                inp.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return out;

        }

        public static String streamToString(java.io.InputStream inp) {
            String out = "";
            byte[] b = new byte[8];
            int read = 0;
            try {
                while ((read = inp.read(b)) > -1) {
                    out = out + new String(b, 0, read);
                }
                inp.close();
            } catch (Exception e) {
            }

            return out;

        }

        public static void InputToOutput(String add, InputStream in,
                                         OutputStream out) throws Exception {
            byte[] b = new byte[8352];
            int read = 0;
            byte[] tb = add.getBytes();
            out.write(tb);
            while ((read = in.read(b)) != -1) {
                out.write(b, 0, read);
            }
            // out.write(add.getBytes(),0,tb.length);
            in.close();
        }

        public static OutputStream copyStream(File is, File os)
                throws FileNotFoundException {

            // if(!is.exists() ||is.isFile()) return null;
            // if(!os.exists() ||os.isFile()) os.mkdirs();
            return copyStream(new FileInputStream(is), new FileOutputStream(os));
        }

        public static OutputStream copyStream(String inputPath,
                                              String outputPath) throws FileNotFoundException {
            // File tmp=new File(is);
            // File fos=new File(os);
            // if(!tmp.exists() || tmp.isFile()) return null;
            // if(!fos.exists() ||fos.isFile()) fos.mkdirs();
            return copyStream(new FileInputStream(inputPath),
                    new FileOutputStream(outputPath));
        }

        public static OutputStream copyStream(InputStream is, OutputStream os) {
            final int buffer_size = 1024;
            try {

                byte[] bytes = new byte[buffer_size];
                for (; ; ) {
                    // Read byte from input stream

                    int count = is.read(bytes, 0, buffer_size);
                    if (count == -1)
                        break;

                    // Write byte from output stream
                    os.write(bytes, 0, count);
                }
            } catch (Exception ex) {
            }

            return os;
        }

        public static void copyStream(InputStream is, OutputStream os,
                                      int startByte) {
            final int buffer_size = 1024;
            try {

                byte[] bytes = new byte[buffer_size];

                is.skip(startByte);
                for (; ; ) {
                    // Read byte from input stream

                    int count = is.read(bytes, 0, buffer_size);
                    if (count == -1)
                        break;

                    // Write byte from output stream
                    os.write(bytes, 0, count);
                }
            } catch (Exception ex) {
            }
        }

        public static InputStream mergeInputStream(InputStream... stream) {
            InputStream tmp = stream[0];

            for (int i = 0; i < stream.length; i++) {
                tmp = new SequenceInputStream(tmp, stream[i]);
            }

            return tmp;
        }
    }

    public static class FileKits {
        @SuppressLint("NewApi")
        public static List<File> searchOnProviderFileWithExtention(
                Context context, String... extend) {
            List<File> out = new ArrayList<File>();
            if (extend == null || extend.length == 0)
                return out;
            ContentResolver cr = context.getContentResolver();
            Uri uri = MediaStore.Files.getContentUri("external");
            String[] projection = new String[]{MediaStore.Files.FileColumns.DATA};
            String selection = MediaStore.Files.FileColumns.DATA + " LIKE ?";
            if (extend.length > 1)
                for (int i = 1; i < extend.length; i++) {
                    selection += "OR " + MediaStore.Files.FileColumns.DATA
                            + " LIKE ?";

                }
            for (int i = 0; i < extend.length; i++) {
                extend[i] = "%" + extend[i];
            }
            String[] selectionArgs = extend;
            String sortOrder = null;
            Cursor allNonMediaFiles = cr.query(uri, projection, selection,
                    selectionArgs, sortOrder);
            if (allNonMediaFiles != null && allNonMediaFiles.getCount() > 0)
                while (allNonMediaFiles.moveToNext()) {
                    out.add(new File(allNonMediaFiles.getString(0)));
                }
            allNonMediaFiles.close();
            return out;
        }

        public static void writeString(String content, String filePath)
                throws IOException {
            if (!new File(filePath).getParentFile().exists())
                new File(filePath).getParentFile().mkdirs();
            FileWriter writer = new FileWriter(filePath);
            writer.write(content);
            writer.flush();
            writer.close();
        }

        public static String fileExtention(File file) {
            return fileExtention(file.getName());
        }

        public static String fileExtention(String file) {
            int index = file.lastIndexOf(".") + 1;
            if (index > 0 && file.length() > index)
                return file.substring(index);
            else
                return "";

        }

        public static String fileExt(String url) {
            if (url.indexOf("?") > -1) {
                url = url.substring(0, url.indexOf("?"));
            }
            if (url.lastIndexOf(".") == -1) {
                return null;
            } else {
                String ext = url.substring(url.lastIndexOf("."));
                if (ext.indexOf("%") > -1) {
                    ext = ext.substring(0, ext.indexOf("%"));
                }
                if (ext.indexOf("/") > -1) {
                    ext = ext.substring(0, ext.indexOf("/"));
                }
                return ext.toLowerCase();

            }
        }

        static String about(String url) {
            String out = "";
            File fl = new File(url);
            if (fl.exists()) {
                if (fl.isFile())
                    out = out + "F-";
                else if (fl.isDirectory())
                    out = out + "D-";
                if (fl.canRead())
                    out = out + "R-";
                else
                    out = out + "NR-";
                if (fl.canWrite())
                    out = out + "W-";
                else
                    out = out + "NW-";
                // if(fl.canExecute()) out=out+"X-";else out=out+"NX-";
                if (fl.isHidden())
                    out = out + "H-";
                else
                    out = out + "NH-";
            } else {
                return null;
            }
            return out;
        }

        // ----------------------------------------------------------------------------------------------
        public static boolean isHidden(String url) {
            if (new File(url).isHidden())
                return true;
            else
                return false;
        }

        public static boolean isReadable(String url) {
            if (new File(url).canRead())
                return true;
            else
                return false;
        }

        public static boolean isWritable(String url) {
            if (new File(url).canWrite())
                return true;
            else
                return false;
        }

        public static boolean isFile(String url) {
            if (new File(url).isFile())
                return true;
            else
                return false;
        }

        public static boolean isDirectory(String url) {
            if (new File(url).isDirectory())
                return true;
            else
                return false;
        }

        public static void clearDirectory(File directory) {
            // list all files inside cache directory
            File[] files = directory.listFiles();
            if (files == null)
                return;
            // delete all cache directory files
            for (File f : files) {
                if (f.isDirectory())
                    deleteDirectory(f);
                f.delete();
            }
        }

        public static void deleteDirectory(File directory) {
            // list all files inside cache directory
            File[] files = directory.listFiles();
            if (files == null)
                return;
            // delete all cache directory files
            for (File f : files) {
                if (f.isDirectory())
                    deleteDirectory(f);
                f.delete();
            }
            directory.delete();
        }

        // -------------------------------------------------------------------------------------------
        public static String fileSize(String url) {
            try {
                BufferedInputStream bfr = new BufferedInputStream(
                        new FileInputStream(url));
                float sise = bfr.available();
                bfr.close();
                if (sise >= Math.pow(1000, 3))
                    return Word.adjustNumber(sise / (1024 * 1024 * 1024))
                            + " Go";
                else if (sise >= 100000)
                    return Word.adjustNumber(sise / (1024 * 1024)) + " Mo";
                else if (sise >= 1000)
                    return Word.adjustNumber(sise / 1024) + " Ko";
                else
                    return "" + Word.adjustNumber(sise) + " oc";

            } catch (Exception e) {
            }
            return "--*--";
        }

        public static String fileSize(String url, int maxChar) {
            try {
                BufferedInputStream bfr = new BufferedInputStream(
                        new FileInputStream(url));
                float sise = bfr.available();
                bfr.close();
                if (sise >= Math.pow(1000, 3))
                    return Word.ShortWord(
                            Word.adjustNumber(sise / (1024 * 1024 * 1024)),
                            maxChar) + " Go";
                else if (sise >= 100000)
                    return Word.ShortWord(
                            Word.adjustNumber(sise / (1024 * 1024)), maxChar)
                            + " Mo";
                else if (sise >= 1000)
                    return Word.ShortWord(Word.adjustNumber(sise / 1024),
                            maxChar) + " Ko";
                else
                    return ""
                            + Word.ShortWord(Word.adjustNumber(sise), maxChar)
                            + " oc";

            } catch (Exception e) {
                Log.e("tkit.filesise.error", "Error:" + e);
            }
            return "--*--";
        }

        public static String integerToFilesize(int sise) {
            if (sise >= Math.pow(1000, 3))
                return Word
                        .adjustNumber((float) ((float) ((int) (((float) sise / (1024 * 1024 * 1024)) * 100)) / 100))
                        + " Go";
            else if (sise >= 100000)
                return Word
                        .adjustNumber((float) ((float) ((int) (((float) sise / (1024 * 1024)) * 100)) / 100))
                        + " Mo";
            else if (sise >= 1000)
                return Word
                        .adjustNumber((float) ((float) ((int) (((float) sise / (1024)) * 100)) / 100))
                        + " Ko";
            else
                return "" + Word.adjustNumber((float) sise) + " oc";
        }

        public static int URLSise(String url) {
            BufferedInputStream bfr = null;
            try {
                bfr = new BufferedInputStream(new FileInputStream(url));

                int a = bfr.available();
                bfr.close();
                return a;
            } catch (Exception e) {
            }
            return 0;
        }

        static int URLConnexionSize(String url) {
            try {
                URL monURL = new URL(url);
                URLConnection connexion = monURL.openConnection();
                return connexion.getContentLength();
            } catch (Exception e) {
                return 0;
            }
        }

    }

    public static class Software {
        public static void installApk(Context context, String apkfile) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(apkfile)),
                    "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

        public static Boolean isActivityRunning(Context context,
                                                Class<?> activityClass) {
            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> tasks = activityManager
                    .getRunningTasks(Integer.MAX_VALUE);

            for (ActivityManager.RunningTaskInfo task : tasks) {
                if (activityClass.getCanonicalName().equalsIgnoreCase(
                        task.baseActivity.getClassName()))
                    return true;
            }

            return false;
        }

        public static Boolean isActivityTop(Context context,
                                            Class<?> activityClass) {
            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager.getRunningTasks(1).get(0).topActivity
                    .getClassName().equals(activityClass.getCanonicalName())) {

                return true;
            }

            return false;
        }

        public static Boolean isProcessRunning(Context context,
                                               String processName) {
            ActivityManager manager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            for (RunningAppProcessInfo process : manager
                    .getRunningAppProcesses()) {
                if (processName.equals(process.processName)) {
                    return true;
                }
            }
            return false;
        }

        public static boolean hasPermission(Context context, String permission) {
            int res = context.checkCallingOrSelfPermission(permission);
            return (res == PackageManager.PERMISSION_GRANTED);
        }

        public static boolean isServiceRunning(Context context,
                                               Class<?> serviceClass) {
            ActivityManager manager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            for (RunningServiceInfo service : manager
                    .getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(
                        service.service.getClassName())) {
                    return true;
                }
            }
            return false;
        }

        public static boolean isUnknowAppEnable(Context context) {
            try {
                return Settings.Secure.getInt(context.getContentResolver(),
                        Settings.Secure.INSTALL_NON_MARKET_APPS) == 1;
            } catch (Exception e) {
                return false;
            }
        }
    }

    public static class Hardware {
        public static boolean hasSystemFeature(Context context, String feature) {
            PackageManager pm = context.getPackageManager();
            Method method = null;
            try {
                Class<?>[] parameters = new Class[1];
                parameters[0] = String.class;
                method = pm.getClass()
                        .getMethod("hasSystemFeature", parameters);
                Object[] parm = new Object[1];
                parm[0] = feature;
                Object retValue = method.invoke(pm, parm);
                if (retValue instanceof Boolean)
                    return Boolean.valueOf(retValue.toString());
                else
                    return false;
            } catch (Exception e) {
                return false;
            }
        }

        public static void closeKeyboard(Activity activity) {
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(
                    activity.getCurrentFocus() != null ? activity
                            .getCurrentFocus().getWindowToken() : null, 0);

        }

        public static boolean isLocationGpsEnable(Context context) {
            final LocationManager manager = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);

            return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }

        public static boolean isLocationNetworkEnable(Context context) {
            final LocationManager manager = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);

            return manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }

        public static void vibrate(Context context, int duration) {
            if (Software.hasPermission(context, permission.VIBRATE)) {
                ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE))
                        .vibrate(duration);

            }
        }

        public static void vibrate(Context context, long[] pattern, int repeate) {
            if (Software.hasPermission(context, permission.VIBRATE)) {
                ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE))
                        .vibrate(pattern, repeate);

            }
        }
    }

    public static class Screen {
        public static void setFullScreen(Activity activity) {
            activity.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        public static void setLandScape(Activity activity) {
            activity.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        public static Point getScreenDimension(Activity context) {
            Display display = context.getWindow().getWindowManager()
                    .getDefaultDisplay();
            return new Point(display.getWidth(), display.getHeight());
        }
    }

    public static class Dialogs {
        public static Dialog displayDialogExclamation(Context context,
                                                      String... args) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            // ((Activity)context).getWindowManager().
            // builder.setIcon(R.drawable.icon);
            builder.setTitle(args[0])
                    .setMessage(args[1])
                    .setCancelable(false)
                    .setPositiveButton(args[2],
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    try {
                                        dialog.cancel();
                                    } catch (Exception e) {
                                    }
                                }
                            });
            AlertDialog alert = builder.create();
            // if(!((Activity)(context)).isDestroyed())
            try {
                alert.show();
            } catch (Exception e) {
            }
            return alert;
        }

        public static Dialog displayDialogCloseExclamation(
                final Context context, String... args) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            // builder.setIcon(R.drawable.icon);
            builder.setTitle(args[0])
                    .setMessage(args[1])
                    .setCancelable(false)
                    .setPositiveButton(args[2],
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    try {
                                        dialog.cancel();
                                        ((Activity) context).finish();
                                    } catch (Exception e) {
                                    }
                                }
                            });
            AlertDialog alert = builder.create();
            // if(!((Activity)(context)).isDestroyed())
            try {
                alert.show();
            } catch (Exception e) {
            }
            return alert;
        }

        public static Dialog displayDialogExclamation(Context context,
                                                      int... args) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            // ((Activity)context).getWindowManager().
            // builder.setIcon(R.drawable.icon);
            builder.setTitle(args[0])
                    .setMessage(args[1])
                    .setCancelable(false)
                    .setPositiveButton(args[2],
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    try {
                                        dialog.cancel();
                                    } catch (Exception e) {
                                    }
                                }
                            });
            AlertDialog alert = builder.create();
            // if(!((Activity)(context)).isDestroyed())
            try {
                alert.show();
            } catch (Exception e) {
            }
            return alert;
        }

        public static Dialog displayDialogCloseExclamation(
                final Context context, int... args) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            // builder.setIcon(R.drawable.icon);
            builder.setTitle(args[0])
                    .setMessage(args[1])
                    .setCancelable(false)
                    .setPositiveButton(args[2],
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    try {
                                        dialog.cancel();
                                        ((Activity) context).finish();
                                    } catch (Exception e) {
                                    }
                                }
                            });
            AlertDialog alert = builder.create();
            // if(!((Activity)(context)).isDestroyed())
            try {
                alert.show();
            } catch (Exception e) {
            }
            return alert;
        }

        public static Dialog displayExitDialog(final Context context,
                                               String... args) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(args[0])
                    .setMessage(args[1])
                    .setCancelable(false)
                    .setPositiveButton(args[2],
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                    ((Activity) context).finish();
                                }
                            })
                    .setNegativeButton(args[3],
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
            return alert;
        }

        public static Dialog displayExitDialog(final Context context,
                                               int... args) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(args[0])
                    .setMessage(args[1])
                    .setCancelable(false)
                    .setPositiveButton(args[2],
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                    ((Activity) context).finish();
                                }
                            })
                    .setNegativeButton(args[3],
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
            return alert;
        }
        // public static void alert(Context context, Object message, Object
        // title) {
        // Intent intent = new Intent(context, Dialog.class);
        // intent.putExtra(Dialog.K_TITLE, String.valueOf(title).trim());
        // intent.putExtra(Dialog.K_MESSAGE, String.valueOf(message).trim());
        // context.startActivity(intent);
        // }
    }

    // ---------------------------------------------------------------------
    public static class IntentUtils {
        public static boolean intentHasContaint(Intent intent, String value) {
            try {
                value = intent.getExtras().getString(value);
                return true;
            } catch (Exception e) {
                return false;
            }

        }

        public static String stringFromIntent(Intent intent, String value,
                                              String Default) {
            try {
                value = intent.getExtras().getString(value);
                return value;
            } catch (Exception e) {
                return Default;
            }

        }

        public static boolean booleanFromIntent(Intent intent, String value,
                                                boolean Default) {
            boolean out = false;
            try {
                out = intent.getExtras().getBoolean(value);
                return out;
            } catch (Exception e) {
                return Default;
            }

        }

        public static int integerFromIntent(Intent intent, String value,
                                            int Default) {
            int out = -1;
            try {
                out = intent.getExtras().getInt(value);
                return out;
            } catch (Exception e) {
                return Default;
            }

        }
    }

    public static class IntentShortCuts {
        public static void enableGps(Context context) {
            Intent gpsOptionsIntent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            context.startActivity(gpsOptionsIntent);
        }

        public static Intent picturePickerIntent() {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            return intent;
        }

        public static void showPicturePicker(Context context) {
            context.startActivity(picturePickerIntent());
        }
    }

    public static class Utility {
        public static String getGalleryImagePathFromIntent(Context ctxt,
                                                           Intent imageReturnedIntent) {

            Uri selectedImage = imageReturnedIntent.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = ctxt.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;

        }
    }

    public static class Languages {
        public static String FRENCH = "fr", ARABIC = "ar", BASIQUE = "eu",
                BULGARIAN = "bg", BENGALI = "bn", CATALAN = "ca", CZECH = "cs",
                DANISH = "da", GERMAN = "de", GREEK = "el", ENCLISH = "en",
                ENGLISH_AUSTRALIAN = "en-AU", ENGLISH_GREAT_BRITAIN = "en-GB",
                SPANISH = "es", BASQUE = "eu", FARSI = "fa", FINNISH = "fi",
                FILIPINO = "fil", GALICIAN = "gl", GUJARATI = "gu",
                HINDI = "hi", CROATIAN = "hr", HUNGARIAN = "hu",
                INDONESIAN = "id", ITALIAN = "it", HEBREW = "iw",
                JAPANESE = "ja", KANNADA = "kn", KOREAN = "ko",
                LITHUANIAN = "lt", LATVIAN = "lv", MALAYALAM = "ml",
                MARATHI = "mr", DUTCH = "nl", NORWEGIAN_NYNORSK = "nn",
                NORWEGIAN = "no", ORIYA = "or", POLISH = "pl",
                PORTUGUESE = "pt", PORTUGUESE_BRAZIL = "pt-BR",
                PORTUGUESE_PORTUGAL = "pt-PT", ROMANSCH = "rm";
    }

}
