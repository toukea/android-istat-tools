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
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Rect;
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
import android.view.View;
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
    public ToolKits() {
    }

    public static final class Appearance {
        public Appearance() {
        }

        public static final void changeTypeFaceFromAsset(Context context, String facePath, TextView tv) {
            Typeface face = Typeface.createFromAsset(context.getAssets(), facePath);
            tv.setTypeface(face);
        }

        public static final void changeTypeFace(File file, TextView tv) {
            Typeface face = Typeface.createFromFile(file);
            tv.setTypeface(face);
        }

        public static final void changeTypeFace(String file, TextView tv) {
            Typeface face = Typeface.createFromFile(file);
            tv.setTypeface(face);
        }
    }

    public static final class Dates {
        public Dates() {
        }

        public static final boolean isDatePast(String datein) {
            try {
                Date e = new Date(datein);
                int compare = e.compareTo(new Date());
                Log.d("wordutil_isDate", "compare=" + compare);
                return compare <= 0;
            } catch (Exception var3) {
                return true;
            }
        }

        public static final Date addToDate(Date date1, long add) {
            return new Date((new Date()).getTime() + add);
        }

        public static final String smartSimpleTime() {
            Date date = new Date();
            SimpleDateFormat f = new SimpleDateFormat("HH:mm");
            return f.format(date);
        }

        public static final String simpleDateTimePlus(long add) {
            Date date = new Date((new Date()).getTime() + add);
            SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return f.format(date);
        }

        public static final String simpleDateTime(long tSpan) {
            Date date = new Date(tSpan);
            SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return f.format(date);
        }

        public static final String simpleDate() {
            Date date = new Date();
            SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd");
            return f.format(date);
        }

        public static final String simpleDateTime() {
            Date date = new Date();
            SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            return f.format(date);
        }
    }

    public static final class Dialogs {
        public Dialogs() {
        }

        public static final Dialog displayDialogExclamation(Context context, String... args) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(args[0]).setMessage(args[1]).setCancelable(false).setPositiveButton(args[2], new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    try {
                        dialog.cancel();
                    } catch (Exception var4) {
                        ;
                    }

                }
            });
            AlertDialog alert = builder.create();

            try {
                alert.show();
            } catch (Exception var5) {
                ;
            }

            return alert;
        }

        public static final Dialog displayDialogCloseExclamation(final Context context, String... args) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(args[0]).setMessage(args[1]).setCancelable(false).setPositiveButton(args[2], new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    try {
                        dialog.cancel();
                        ((Activity) context).finish();
                    } catch (Exception var4) {
                        ;
                    }

                }
            });
            AlertDialog alert = builder.create();

            try {
                alert.show();
            } catch (Exception var5) {
                ;
            }

            return alert;
        }

        public static final Dialog displayDialogExclamation(Context context, int... args) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(args[0]).setMessage(args[1]).setCancelable(false).setPositiveButton(args[2], new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    try {
                        dialog.cancel();
                    } catch (Exception var4) {
                        ;
                    }

                }
            });
            AlertDialog alert = builder.create();

            try {
                alert.show();
            } catch (Exception var5) {
                ;
            }

            return alert;
        }

        public static final Dialog displayDialogCloseExclamation(final Context context, int... args) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(args[0]).setMessage(args[1]).setCancelable(false).setPositiveButton(args[2], new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    try {
                        dialog.cancel();
                        ((Activity) context).finish();
                    } catch (Exception var4) {
                        ;
                    }

                }
            });
            AlertDialog alert = builder.create();

            try {
                alert.show();
            } catch (Exception var5) {
                ;
            }

            return alert;
        }

        public static final Dialog displayExitDialog(final Context context, String... args) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(args[0]).setMessage(args[1]).setCancelable(false).setPositiveButton(args[2], new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    ((Activity) context).finish();
                }
            }).setNegativeButton(args[3], new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            return alert;
        }

        public static final Dialog displayExitDialog(final Context context, int... args) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(args[0]).setMessage(args[1]).setCancelable(false).setPositiveButton(args[2], new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    ((Activity) context).finish();
                }
            }).setNegativeButton(args[3], new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            return alert;
        }
    }

    public static final class FileKits {
        public FileKits() {
        }
        public static int countLines(String filename) throws IOException {
            InputStream is = new BufferedInputStream(new FileInputStream(filename));
            try {
                byte[] c = new byte[1024];
                int count = 0;
                int readChars = 0;
                boolean empty = true;
                while ((readChars = is.read(c)) != -1) {
                    empty = false;
                    for (int i = 0; i < readChars; ++i) {
                        if (c[i] == '\n') {
                            ++count;
                        }
                    }
                }
                return (count == 0 && !empty) ? 1 : count;
            } finally {
                is.close();
            }
        }

        @SuppressLint("NewApi")
        public static List<File> searchOnProviderFileWithExtention(
                Context context, String... extend) {
            List<File> out = new ArrayList();
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

        public static final void writeString(String content, String filePath) throws IOException {
            if (!(new File(filePath)).getParentFile().exists()) {
                (new File(filePath)).getParentFile().mkdirs();
            }

            FileWriter writer = new FileWriter(filePath);
            writer.write(content);
            writer.flush();
            writer.close();
        }

        public static final void appendString(String content, String filePath) throws IOException {
            if (!(new File(filePath)).getParentFile().exists()) {
                (new File(filePath)).getParentFile().mkdirs();
            }

            FileWriter writer = new FileWriter(filePath);
            writer.append(content);
            writer.flush();
            writer.close();
        }

        public static final String fileExtention(File file) {
            return fileExtention(file.getName());
        }

        public static final String fileExtention(String file) {
            int index = file.lastIndexOf(".") + 1;
            return index > 0 && file.length() > index ? file.substring(index) : "";
        }

        public static final String fileExt(String url) {
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
                if (fl.isFile()) {
                    out = out + "F-";
                } else if (fl.isDirectory()) {
                    out = out + "D-";
                }

                if (fl.canRead()) {
                    out = out + "R-";
                } else {
                    out = out + "NR-";
                }

                if (fl.canWrite()) {
                    out = out + "W-";
                } else {
                    out = out + "NW-";
                }

                if (fl.isHidden()) {
                    out = out + "H-";
                } else {
                    out = out + "NH-";
                }

                return out;
            } else {
                return null;
            }
        }

        public static final boolean isHidden(String url) {
            return (new File(url)).isHidden();
        }

        public static final boolean isReadable(String url) {
            return (new File(url)).canRead();
        }

        public static final boolean isWritable(String url) {
            return (new File(url)).canWrite();
        }

        public static final boolean isFile(String url) {
            return (new File(url)).isFile();
        }

        public static final boolean isDirectory(String url) {
            return (new File(url)).isDirectory();
        }

        public static final void clearDirectory(File directory) {
            File[] files = directory.listFiles();
            if (files != null) {
                File[] var5 = files;
                int var4 = files.length;

                for (int var3 = 0; var3 < var4; ++var3) {
                    File f = var5[var3];
                    if (f.isDirectory()) {
                        deleteDirectory(f);
                    }

                    f.delete();
                }

            }
        }

        public static final int deleteDirectory(File directory) {
            int deleted = 0;
            File[] files = directory.listFiles();
            if (files != null) {
                File[] var5 = files;
                int var4 = files.length;

                for (int var3 = 0; var3 < var4; ++var3) {
                    File f = var5[var3];
                    if (f.isDirectory()) {
                        deleteDirectory(f);
                    }
                    deleted++;
                    f.delete();
                }
                deleted++;
                directory.delete();
            }
            return deleted;
        }

        public static final String fileSize(String url) {
            try {
                BufferedInputStream bfr = new BufferedInputStream(new FileInputStream(url));
                float sise = (float) bfr.available();
                bfr.close();
                return (double) sise >= Math.pow(1000.0D, 3.0D) ? ToolKits.Word.adjustNumber(sise / 1.07374182E9F) + " Go" : (sise >= 100000.0F ? ToolKits.Word.adjustNumber(sise / 1048576.0F) + " Mo" : (sise >= 1000.0F ? ToolKits.Word.adjustNumber(sise / 1024.0F) + " Ko" : ToolKits.Word.adjustNumber(sise) + " oc"));
            } catch (Exception var3) {
                return "--*--";
            }
        }

        public static final String fileSize(String url, int maxChar) {
            try {
                BufferedInputStream e = new BufferedInputStream(new FileInputStream(url));
                float sise = (float) e.available();
                e.close();
                return (double) sise >= Math.pow(1000.0D, 3.0D) ? ToolKits.Word.shortWord(ToolKits.Word.adjustNumber(sise / 1.07374182E9F), maxChar) + " Go" : (sise >= 100000.0F ? ToolKits.Word.shortWord(ToolKits.Word.adjustNumber(sise / 1048576.0F), maxChar) + " Mo" : (sise >= 1000.0F ? ToolKits.Word.shortWord(ToolKits.Word.adjustNumber(sise / 1024.0F), maxChar) + " Ko" : ToolKits.Word.shortWord(ToolKits.Word.adjustNumber(sise), maxChar) + " oc"));
            } catch (Exception var4) {
                Log.e("tkit.filesise.error", "Error:" + var4);
                return "--*--";
            }
        }

        public static final String integerToFilesize(int sise) {
            return (double) sise >= Math.pow(1000.0D, 3.0D) ? ToolKits.Word.adjustNumber((float) ((int) ((float) sise / 1.07374182E9F * 100.0F)) / 100.0F) + " Go" : (sise >= 100000 ? ToolKits.Word.adjustNumber((float) ((int) ((float) sise / 1048576.0F * 100.0F)) / 100.0F) + " Mo" : (sise >= 1000 ? ToolKits.Word.adjustNumber((float) ((int) ((float) sise / 1024.0F * 100.0F)) / 100.0F) + " Ko" : ToolKits.Word.adjustNumber((float) sise) + " oc"));
        }

        public static final int URLSise(String url) {
            BufferedInputStream bfr = null;

            try {
                bfr = new BufferedInputStream(new FileInputStream(url));
                int a = bfr.available();
                bfr.close();
                return a;
            } catch (Exception var3) {
                return 0;
            }
        }

        static int URLConnexionSize(String url) {
            try {
                URL e = new URL(url);
                URLConnection connexion = e.openConnection();
                return connexion.getContentLength();
            } catch (Exception var3) {
                return 0;
            }
        }
    }

    public static final class Hardware {
        public Hardware() {
        }

        public static final boolean hasSystemFeature(Context context, String feature) {
            PackageManager pm = context.getPackageManager();
            Method method = null;

            try {
                Class[] e = new Class[]{String.class};
                method = pm.getClass().getMethod("hasSystemFeature", e);
                Object[] parm = new Object[]{feature};
                Object retValue = method.invoke(pm, parm);
                return retValue instanceof Boolean ? Boolean.valueOf(retValue.toString()).booleanValue() : false;
            } catch (Exception var7) {
                return false;
            }
        }

        public final static void showKeyboard(Context context) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
        }

        public static final void closeKeyboard(Activity activity) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getCurrentFocus() != null ? activity.getCurrentFocus().getWindowToken() : null, 0);
        }

        public static final void closeKeyboard(View v) {
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }

        public static final boolean isLocationEnable(Context context) {
            return isLocationGpsEnable(context) || isLocationNetworkEnable(context);
        }

        public static final boolean isLocationGpsEnable(Context context) {
            LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return manager.isProviderEnabled("gps");
        }

        public static final boolean isItAbleToProvideLocation(Context context) {
            return isLocationGpsEnable(context) || isLocationNetworkEnable(context) && ToolKits.Network.isNetworkConnected(context);
        }

        public static final boolean isLocationNetworkEnable(Context context) {
            LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return manager.isProviderEnabled("network");
        }

        @SuppressLint("MissingPermission")
        public static final void vibrate(Context context, int duration) {
            if (ToolKits.Software.hasPermission(context, "android.permission.VIBRATE")) {
                ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate((long) duration);
            }

        }

        @SuppressLint("MissingPermission")
        public static final void vibrate(Context context, long[] pattern, int repeate) {
            if (ToolKits.Software.hasPermission(context, "android.permission.VIBRATE")) {
                ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(pattern, repeate);
            }

        }
    }

    public static final class IntentShortCuts {
        public IntentShortCuts() {
        }

        public static final void enableGps(Context context) {
            Intent gpsOptionsIntent = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
            context.startActivity(gpsOptionsIntent);
        }

        public static final Intent picturePickerIntent() {
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            return intent;
        }

        public static final void showPicturePicker(Context context) {
            context.startActivity(picturePickerIntent());
        }
    }

    public static final class IntentUtils {
        public IntentUtils() {
        }

        public static final boolean intentHasContaint(Intent intent, String value) {
            try {
                value = intent.getExtras().getString(value);
                return true;
            } catch (Exception var3) {
                return false;
            }
        }

        public static final String stringFromIntent(Intent intent, String value, String Default) {
            try {
                value = intent.getExtras().getString(value);
                return value;
            } catch (Exception var4) {
                return Default;
            }
        }

        public static final boolean booleanFromIntent(Intent intent, String value, boolean Default) {
            boolean out = false;

            try {
                out = intent.getExtras().getBoolean(value);
                return out;
            } catch (Exception var5) {
                return Default;
            }
        }

        public static final int integerFromIntent(Intent intent, String value, int Default) {
            boolean out = true;

            try {
                int out1 = intent.getExtras().getInt(value);
                return out1;
            } catch (Exception var5) {
                return Default;
            }
        }
    }

    public static final class Languages {
        public static final String FRENCH = "fr";
        public static final String ARABIC = "ar";
        public static final String BASIQUE = "eu";
        public static final String BULGARIAN = "bg";
        public static final String BENGALI = "bn";
        public static final String CATALAN = "ca";
        public static final String CZECH = "cs";
        public static final String DANISH = "da";
        public static final String GERMAN = "de";
        public static final String GREEK = "el";
        public static final String ENCLISH = "en";
        public static final String ENGLISH_AUSTRALIAN = "en-AU";
        public static final String ENGLISH_GREAT_BRITAIN = "en-GB";
        public static final String SPANISH = "es";
        public static final String BASQUE = "eu";
        public static final String FARSI = "fa";
        public static final String FINNISH = "fi";
        public static final String FILIPINO = "fil";
        public static final String GALICIAN = "gl";
        public static final String GUJARATI = "gu";
        public static final String HINDI = "hi";
        public static final String CROATIAN = "hr";
        public static final String HUNGARIAN = "hu";
        public static final String INDONESIAN = "id";
        public static final String ITALIAN = "it";
        public static final String HEBREW = "iw";
        public static final String JAPANESE = "ja";
        public static final String KANNADA = "kn";
        public static final String KOREAN = "ko";
        public static final String LITHUANIAN = "lt";
        public static final String LATVIAN = "lv";
        public static final String MALAYALAM = "ml";
        public static final String MARATHI = "mr";
        public static final String DUTCH = "nl";
        public static final String NORWEGIAN_NYNORSK = "nn";
        public static final String NORWEGIAN = "no";
        public static final String ORIYA = "or";
        public static final String POLISH = "pl";
        public static final String PORTUGUESE = "pt";
        public static final String PORTUGUESE_BRAZIL = "pt-BR";
        public static final String PORTUGUESE_PORTUGAL = "pt-PT";
        public static final String ROMANSCH = "rm";

        public Languages() {
        }
    }

    public static final class Network {
        public Network() {
        }

        public static final boolean isNetworkConnected(Context context) {
            ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            @SuppressLint("MissingPermission") NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
    }

    public static final class Screen {
        public Screen() {
        }

        public final static boolean isScreenVisible(View v) {
            Rect rect = new Rect();
            View root = v.getRootView();
            root.getHitRect(rect);
            return v.getLocalVisibleRect(rect);
        }

        public static final void setFullScreen(Activity activity) {
            activity.getWindow().setFlags(1024, 1024);
        }

        public static final void setPortrait(Activity activity) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        public static final void setLandScape(Activity activity) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        public static final void setNoSensor(Activity activity) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }

        public static final void setFullSensor(Activity activity) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }

        public static final Point getScreenDimension(Activity context) {
            Display display = context.getWindow().getWindowManager().getDefaultDisplay();
            return new Point(display.getWidth(), display.getHeight());
        }
    }

    public static final class Software {
        public Software() {
        }

        public static final void installApk(Context context, String apkfile) {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setDataAndType(Uri.fromFile(new File(apkfile)), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

        public static final Boolean isActivityRunning(Context context, Class<?> activityClass) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
            Iterator var5 = tasks.iterator();

            while (var5.hasNext()) {
                ActivityManager.RunningTaskInfo task = (ActivityManager.RunningTaskInfo) var5.next();
                if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName())) {
                    return Boolean.valueOf(true);
                }
            }

            return Boolean.valueOf(false);
        }

        public static final Boolean isActivityTop(Context context, Class<?> activityClass) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            return ((ActivityManager.RunningTaskInfo) activityManager.getRunningTasks(1).get(0)).topActivity.getClassName().equals(activityClass.getCanonicalName()) ? Boolean.valueOf(true) : Boolean.valueOf(false);
        }

        public static final Boolean isProcessRunning(Context context, String processName) {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            Iterator var4 = manager.getRunningAppProcesses().iterator();

            while (var4.hasNext()) {
                RunningAppProcessInfo process = (RunningAppProcessInfo) var4.next();
                if (processName.equals(process.processName)) {
                    return Boolean.valueOf(true);
                }
            }

            return Boolean.valueOf(false);
        }

        public static final boolean hasPermission(Context context, String permission) {
            int res = context.checkCallingOrSelfPermission(permission);
            return res == 0;
        }

        public static final boolean isServiceRunning(Context context, Class<?> serviceClass) {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            Iterator var4 = manager.getRunningServices(2147483647).iterator();

            while (var4.hasNext()) {
                RunningServiceInfo service = (RunningServiceInfo) var4.next();
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }

            return false;
        }

        public static final boolean isUnknowAppEnable(Context context) {
            try {
                return Settings.Secure.getInt(context.getContentResolver(), "install_non_market_apps") == 1;
            } catch (Exception var2) {
                return false;
            }
        }

        public static File getApplicationInstallationFile(Context context, String packageName) throws FileNotFoundException {
            if (packageName == null) {
                throw new RuntimeException("Package name can't be null");
            }
            final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(mainIntent, 0);
            for (ResolveInfo info : apps) {
                File file = new File(info.activityInfo.applicationInfo.publicSourceDir);
                if (packageName.equalsIgnoreCase(info.activityInfo.packageName)) {
                    return file;
                }
            }
            throw new FileNotFoundException("Application not Found for package name:" + packageName);
        }
    }

    public static final class Stream {
        private Stream() {
        }

        public static final String streamToString(InputStream inp, String encoding) {
            String out = "";
            StringBuilder total = new StringBuilder();

            try {
                BufferedReader e = new BufferedReader(new InputStreamReader(inp, encoding));

                String line;
                while ((line = e.readLine()) != null) {
                    total.append(line);
                }

                out = total.toString();
                inp.close();
            } catch (IOException var6) {
                var6.printStackTrace();
            }

            return out;
        }

        public static final String streamToString(InputStream inp) {
            String out = "";
            byte[] b = new byte[1024];
            try {
                int read1;
                while ((read1 = inp.read(b)) > -1) {
                    out = out + new String(b, 0, read1);
                }

                inp.close();
            } catch (Exception var5) {
            }
            return out;
        }

        public static final void InputToOutput(String add, InputStream in, OutputStream out) throws Exception {
            byte[] b = new byte[8352];
            boolean read = false;
            byte[] tb = add.getBytes();
            out.write(tb);

            int read1;
            while ((read1 = in.read(b)) != -1) {
                out.write(b, 0, read1);
            }

            in.close();
        }

        public static OutputStream copyStream(File is, File os) throws IOException {
            return copyStream(is.getAbsolutePath(), os.getAbsolutePath());
        }

        public static final OutputStream copyStream(String inputPath, String outputPath) throws IOException {
            InputStream inputStream = new FileInputStream(inputPath);
            OutputStream outputStream = new FileOutputStream(outputPath);
            copyStream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
            return outputStream;
        }

        public static final long copyStream(InputStream is, OutputStream os) throws IOException {
            long out = 0;
            byte[] bytes = new byte[1024];
            int count;
            while (true) {
                count = is.read(bytes, 0, 1024);
                out += count;
                if (count == -1) {
                    break;
                }
                os.write(bytes, 0, count);
            }
            return out;
        }

        public static final long copyStream(InputStream is, OutputStream os, int startByte) throws IOException {
            long out = 0;

                byte[] bytes = new byte[1024];
                is.skip((long) startByte);

                while (true) {
                    int count = is.read(bytes, 0, 1024);
                    out += count;
                    if (count == -1) {
                        break;
                    }

                    os.write(bytes, 0, count);
                }
            return out;

        }

        public static final InputStream mergeInputStream(InputStream... stream) {
            Object tmp = stream[0];

            for (int i = 0; i < stream.length; ++i) {
                tmp = new SequenceInputStream((InputStream) tmp, stream[i]);
            }

            return (InputStream) tmp;
        }
    }

    public static final class Utility {
        public Utility() {
        }

        public static final String getGalleryImagePathFromIntent(Context ctxt, Intent imageReturnedIntent) {
            Uri selectedImage = imageReturnedIntent.getData();
            String[] filePathColumn = new String[]{"_data"};
            Cursor cursor = ctxt.getContentResolver().query(selectedImage, filePathColumn, (String) null, (String[]) null, (String) null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }
    }

    public static final class Word {
        public Word() {
        }

        public static final String parseString(Object obj) {
            return obj == null ? "" : obj.toString();
        }

        public static final boolean isEmpty(String string) {
            return string == null || string.equals("");
        }

        public static final boolean isEmpty(Object string) {
            return string == null || "".equals("" + string);
        }

        public static final Boolean parseBoolean(Object s, boolean defaults) {
            return isEmpty(s) ? Boolean.valueOf(defaults) : Boolean.valueOf(parseString(s));
        }

        public static final long parseLong(String s) {
            if (s != null && !s.equals("")) {
                if (s.trim().length() == 0) {
                    return 0L;
                } else {
                    try {
                        double e = Double.parseDouble(s.trim());
                        return (long) e;
                    } catch (Exception var3) {
                        return 0L;
                    }
                }
            } else {
                return 0L;
            }
        }

        public static final int parseInt(int s) {
            return s;
        }

        public static final double parseDouble(Object o) {
            String s = parseString(o);
            if (s != null && !s.equals("")) {
                if (s.trim().length() == 0) {
                    return 0.0D;
                } else {
                    try {
                        return Double.parseDouble(s.trim());
                    } catch (Exception var3) {
                        return 0.0D;
                    }
                }
            } else {
                return 0.0D;
            }
        }

        public static final float parseFloat(Object o) {
            String s = parseString(o);
            if (s != null && !s.equals("")) {
                if (s.trim().length() == 0) {
                    return 0.0F;
                } else {
                    try {
                        return Float.parseFloat(s.trim());
                    } catch (Exception var3) {
                        return 0.0F;
                    }
                }
            } else {
                return 0.0F;
            }
        }

        public static final long parseLong(Object s) {
            return parseLong(parseString(s));
        }

        public static final Boolean parseBoolean(Object s) {
            return Boolean.valueOf(parseString(s));
        }

        public static final Calendar parseToCalandar(Object obj) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            GregorianCalendar cal = new GregorianCalendar();

            try {
                cal.setTime(df.parse(parseString(obj)));
            } catch (Exception var4) {
                var4.printStackTrace();
            }

            return cal;
        }

        public static final String toSentence(String word, String endingPontuation) {
            word = beginByUpperCase(word);
            int index = word.indexOf(endingPontuation);
            return index <= 0 ? word + endingPontuation : word;
        }

        public static final String beginByUpperCase(String word) {
            if (word.length() > 1) {
                String beggin = word.substring(0, 1).toUpperCase(Locale.getDefault());
                word = beggin + word.substring(1);
            }

            return word;
        }

        public static final String beginByLowerCase(String word) {
            if (word.length() > 1) {
                String beggin = word.substring(0, 1).toLowerCase(Locale.getDefault());
                word = beggin + word.substring(1);
            }

            return word;
        }

        public static final int parseInt(Object obj) {
            return parseInt(parseString(obj));
        }

        public static final int parseInt(String s) {
            if (TextUtils.isEmpty(s)) {
                return 0;
            } else if (s.trim().length() == 0) {
                return 0;
            } else {
                try {
                    return Integer.parseInt(s.trim());
                } catch (Exception var2) {
                    return 0;
                }
            }
        }

        public static final String numberToWords(long number, Locale locale) {
            return !locale.equals(Locale.FRANCE) && !locale.equals(Locale.FRENCH) ? EnglishNumberToWords.convert(number) : FrenchNumberToWords.convert(number);
        }

        public static final String wordToNumber(String word, Locale locale) {
            return !locale.equals(Locale.FRANCE) && !locale.equals(Locale.FRENCH) ? EnglishWordToNumber.replaceNumbers(word) : EnglishWordToNumber.replaceNumbers(word);
        }

        public static final String shortWord(String word, int max) {
            return word.length() <= max ? word : word.substring(0, max) + "...";
        }

        public static final String DistanceToKm(String word) {
            int d = (int) (Double.valueOf(word).doubleValue() * 1.0D);
            return word.equals("-1") ? "---" : (d > 1500 ? d / 1000 + "Km" : d + "m");
        }

        public static final float getpercentNumericValue(int progresstate, int STEP, boolean comma) {
            return comma ? (float) progresstate * 100.0F / (float) STEP : (float) ((int) ((float) progresstate * 100.0F / (float) STEP));
        }

        public static final String getpercentValue(int progresstate, int STEP, boolean comma) {
            return comma ? (float) progresstate * 100.0F / (float) STEP + "%" : (int) ((float) progresstate * 100.0F / (float) STEP) + "%";
        }

        public static final String getpercentValue(int progresstate, int STEP) {
            return (float) progresstate * 100.0F / (float) STEP + "%";
        }

        public static final float getpercentNumericValue(int progresstate, int STEP) {
            return (float) progresstate * 100.0F / (float) STEP;
        }

        public static final String sweetNumber(int a) {
            return a > 9 ? "" + a : "0" + a;
        }

        public static final String adjustNumber(float a) {
            return (float) ((int) a) == a ? "" + (int) a : "" + a;
        }
    }

    public static final class WordFormat {
        public WordFormat() {
        }

        public static final boolean isEmail(String input) {
            Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
            Matcher m = p.matcher(input);
            return m.matches();
        }

        public static final boolean isNumber(String a) {
            try {
                (new StringBuilder()).append(Double.valueOf(a)).toString();
                return true;
            } catch (Exception var2) {
                return false;
            }
        }

        public static final boolean isInteger(String a) {
            try {
                (new StringBuilder()).append(Integer.valueOf(a)).toString();
                return true;
            } catch (Exception var2) {
                return false;
            }
        }

        public static final boolean isDate(String a) {
            try {
                new Date(a);
                return true;
            } catch (Exception var2) {
                return false;
            }
        }
    }

}
