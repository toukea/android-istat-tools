package istat.android.base.tools;

import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.Process;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import istat.android.base.interfaces.Decoder;

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

        public static final boolean isTodayTimestamp(long timestamp) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
            String currentDate = simpleDateFormat.format(new Date());
            String givenDateToString = simpleDateFormat.format(new Date(timestamp));
            return Objects.equals(currentDate, givenDateToString);
        }

        public static final boolean isDatePast(String dateToString) {
            try {
                Date e = new Date(dateToString);
                int compare = e.compareTo(new Date());
                Log.d("word_util_is:date", "compare=" + compare);
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

        public static final Dialog displayExitDialog(final Activity context, int... args) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(args[0]).setMessage(args[1]).setCancelable(false).setPositiveButton(args[2], new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    context.finish();
                }
            }).setNegativeButton(args[3], new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            if (!context.isFinishing()) {
                alert.show();
            }
            return alert;
        }
    }

    public static final class FileKits {

        public enum ScanUsage {
            IGNORE,
            TARGET
        }

        public FileKits() {
        }

        //        public static List<File> getStorage(Context context) {
//            final File[] appsDir = ContextCompat.getExternalFilesDirs(context, null);
//            final List<File> extRootPaths = new ArrayList<>();
//            for (final File file : appsDir) {
//                extRootPaths.add(file.getParentFile().getParentFile().getParentFile().getParentFile());
//            }
//            return extRootPaths;
//        }

        public static int deleteFiles(File directory, boolean recursively, Decoder<Boolean, File> shouldDeleteDecoder) {
            int deleted = 0;
            File[] files = directory.listFiles();
            if (files != null) {
                File[] var5 = files;
                int var4 = files.length;
                for (int i = 0; i < var4; ++i) {
                    File currentFile = var5[i];
                    if (currentFile.isDirectory() && recursively) {
                        deleteFiles(currentFile, true, shouldDeleteDecoder);
                    }
                    try {
                        if (shouldDeleteDecoder == null || shouldDeleteDecoder.decode(currentFile)) {
                            deleted++;
                            currentFile.delete();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (shouldDeleteDecoder == null || shouldDeleteDecoder.decode(directory)) {
                        deleted++;
                        directory.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return deleted;
        }

        public static String queryName(Context context, Uri uri) {
            ContentResolver resolver = context.getContentResolver();
            Cursor returnCursor =
                    resolver.query(uri, null, null, null, null);
            assert returnCursor != null;
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String name = returnCursor.getString(nameIndex);
            returnCursor.close();
            return name;
        }

        public static void startMediaScanner(Context context, File directory) {
            startMediaScanner(context, directory.getAbsolutePath());
        }

        public static void startMediaScanner(Context context, String filePath) {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri
                    .parse("file://" + filePath)));
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
        public static List<File> findFileWithExtension(Context context, String... extend) {
            return findFileWithExtension(context, null, extend);
        }

        @SuppressLint("NewApi")
        public static List<File> findFileWithExtension(Context context, Pair<String[], ScanUsage> pathListUsagePair, String... extend) {
            try {
                return findFileWithExtension(context, new Decoder<File, String>() {
                    @Override
                    public File decode(String s) throws Exception {
                        return new File(s);
                    }
                }, null, pathListUsagePair, extend);
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }

        /*
        OpenableColumns.DISPLAY_NAME);
    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE
         */
        //TODO remplacer le nom de cette methode avec findContentWithFileExtension
        @SuppressLint("NewApi")
        public static <T> List<T> findFileWithExtension(Context context, Decoder<T, String> decoder, String sortOrder, Pair<String[], ScanUsage> pathListUsagePair, String... extend) throws Exception {
            String[] targetDirs = null, ignoredDir = null;
            if (pathListUsagePair != null) {
                if (pathListUsagePair.second == null || pathListUsagePair.second == ScanUsage.IGNORE) {
                    ignoredDir = pathListUsagePair.first;
                } else {
                    targetDirs = pathListUsagePair.first;
                }
            }
            List<T> out = new ArrayList();
            if (extend == null || extend.length == 0)
                return out;
            ContentResolver cr = context.getContentResolver();
            Uri uri = MediaStore.Files.getContentUri("external");
            String[] projection = new String[]{MediaStore.Files.FileColumns.DATA};
            String selectionInclude = null;
            if (extend.length >= 1) {
                for (int i = 0; i < extend.length; i++) {
                    if (selectionInclude == null) {
                        selectionInclude = MediaStore.Files.FileColumns.DATA + " LIKE ?";
                    } else {
                        selectionInclude += "OR " + MediaStore.Files.FileColumns.DATA
                                + " LIKE ?";
                    }
                    extend[i] = "%" + (extend[i].startsWith(".") ? "" : ".") + extend[i];
                }
            }
            String selectionPathToTarget = null;
            if (targetDirs != null && targetDirs.length >= 1) {
                for (int i = 0; i < targetDirs.length; i++) {
                    if (selectionPathToTarget == null) {
                        selectionPathToTarget = MediaStore.Files.FileColumns.DATA + " LIKE ?";
                    } else {
                        selectionPathToTarget += "OR " + MediaStore.Files.FileColumns.DATA
                                + " LIKE ?";
                    }

                    targetDirs[i] = targetDirs[i] + "%";
                }
            }
            String selectionIgnore = null;
            if (ignoredDir != null && ignoredDir.length >= 1) {
                for (int i = 0; i < ignoredDir.length; i++) {
                    if (selectionIgnore == null) {
                        selectionIgnore = MediaStore.Files.FileColumns.DATA + " NOT LIKE ?";
                    } else {
                        selectionIgnore += "AND " + MediaStore.Files.FileColumns.DATA
                                + " NOT LIKE ?";
                    }

                    ignoredDir[i] = "%" + ignoredDir[i];
                }
            }
            String selection = "";
            if (!istat.android.base.tools.TextUtils.isEmpty(selectionInclude)) {
                selection += "(" + selectionInclude + ")";
            }
            if (!istat.android.base.tools.TextUtils.isEmpty(selectionPathToTarget)) {
                selection += (istat.android.base.tools.TextUtils.isEmpty(selection) ? "" : " AND ") + "(" + selectionPathToTarget + ")";
            }
            if (!istat.android.base.tools.TextUtils.isEmpty(selectionIgnore)) {
                selection += (istat.android.base.tools.TextUtils.isEmpty(selection) ? "" : " AND ") + "(" + selectionIgnore + ")";
            }
            List<String> argsList = new ArrayList<>();
            if (extend != null && extend.length > 0) {
                argsList.addAll(Arrays.asList(extend));
            }
            if (ignoredDir != null && ignoredDir.length > 0) {
                argsList.addAll(Arrays.asList(ignoredDir));
            }
            Cursor allNonMediaFiles = cr.query(uri, projection, selection,
                    argsList.toArray(new String[argsList.size()]), sortOrder);
            if (allNonMediaFiles != null && allNonMediaFiles.getCount() > 0) {
                T entity;
                while (allNonMediaFiles.moveToNext()) {
                    entity = decoder.decode(allNonMediaFiles.getString(0));
                    if (entity != null) {
                        out.add(entity);
                    }
                }
            }
            allNonMediaFiles.close();
            return out;
        }

        @SuppressLint("NewApi")
        public static <T> List<T> findFileWithMediaType(Context context, Decoder<T, String> decoder, String sortOrder, Pair<String[], ScanUsage> pathListUsagePair, String... mimeTypes) throws Exception {
            String[] targetDirs = null, ignoredDir = null;
            if (pathListUsagePair != null) {
                if (pathListUsagePair.second == null || pathListUsagePair.second == ScanUsage.IGNORE) {
                    ignoredDir = pathListUsagePair.first;
                } else {
                    targetDirs = pathListUsagePair.first;
                }
            }
            List<T> out = new ArrayList();
            if (mimeTypes == null || mimeTypes.length == 0)
                return out;
            ContentResolver cr = context.getContentResolver();
            Uri uri = MediaStore.Files.getContentUri("external");
            String[] projection = new String[]{MediaStore.Files.FileColumns.DATA};
            String selectionInclude = null;
            if (mimeTypes.length >= 1) {
                for (int i = 0; i < mimeTypes.length; i++) {
                    if (selectionInclude == null) {
                        selectionInclude = MediaStore.Files.FileColumns.MIME_TYPE + " = ?";
                    } else {
                        selectionInclude += "OR " + MediaStore.Files.FileColumns.MIME_TYPE
                                + " = ?";
                    }
                    mimeTypes[i] = mimeTypes[i];
                }
            }
            String selectionPathToTarget = null;
            if (targetDirs != null && targetDirs.length >= 1) {
                for (int i = 0; i < targetDirs.length; i++) {
                    if (selectionPathToTarget == null) {
                        selectionPathToTarget = MediaStore.Files.FileColumns.DATA + " LIKE ?";
                    } else {
                        selectionPathToTarget += "OR " + MediaStore.Files.FileColumns.DATA
                                + " LIKE ?";
                    }

                    targetDirs[i] = targetDirs[i] + "%";
                }
            }
            String selectionIgnore = null;
            if (ignoredDir != null && ignoredDir.length >= 1) {
                for (int i = 0; i < ignoredDir.length; i++) {
                    if (selectionIgnore == null) {
                        selectionIgnore = MediaStore.Files.FileColumns.MIME_TYPE + " != ?";
                    } else {
                        selectionIgnore += "AND " + MediaStore.Files.FileColumns.MIME_TYPE
                                + " != ?";
                    }

                    ignoredDir[i] = ignoredDir[i];
                }
            }
            String selection = "";
            if (!istat.android.base.tools.TextUtils.isEmpty(selectionInclude)) {
                selection += "(" + selectionInclude + ")";
            }
            if (!istat.android.base.tools.TextUtils.isEmpty(selectionPathToTarget)) {
                selection += (istat.android.base.tools.TextUtils.isEmpty(selection) ? "" : " AND ") + "(" + selectionPathToTarget + ")";
            }
            if (!istat.android.base.tools.TextUtils.isEmpty(selectionIgnore)) {
                selection += (istat.android.base.tools.TextUtils.isEmpty(selection) ? "" : " AND ") + "(" + selectionIgnore + ")";
            }
            List<String> argsList = new ArrayList<>();
            if (mimeTypes != null && mimeTypes.length > 0) {
                argsList.addAll(Arrays.asList(mimeTypes));
            }
            if (ignoredDir != null && ignoredDir.length > 0) {
                argsList.addAll(Arrays.asList(ignoredDir));
            }
            Cursor allNonMediaFiles = cr.query(uri, projection, selection,
                    argsList.toArray(new String[argsList.size()]), sortOrder);
            if (allNonMediaFiles != null && allNonMediaFiles.getCount() > 0) {
                T entity;
                while (allNonMediaFiles.moveToNext()) {
                    entity = decoder.decode(allNonMediaFiles.getString(0));
                    if (entity != null) {
                        out.add(entity);
                    }
                }
            }
            allNonMediaFiles.close();
            return out;
        }


        public static <T> List<T> findFiles(Context context, Decoder<T, String> decoder, Pair<String[], ScanUsage> pathsUsagePair, String[] nameContent, String... extend) throws Exception {
            return findFiles(context, decoder, null, pathsUsagePair, nameContent, extend);
        }

        @SuppressLint("NewApi")
        public static <T> List<T> findFiles(Context context, Decoder<T, String> decoder, String sortOrder, Pair<String[], ScanUsage> pathListUsagePair, String[] nameContent, String... extend) throws Exception {
            String[] targetDirs = null, ignoredDir = null;
            if (pathListUsagePair != null) {
                if (pathListUsagePair.second == null || pathListUsagePair.second == ScanUsage.IGNORE) {
                    ignoredDir = pathListUsagePair.first;
                } else {
                    targetDirs = pathListUsagePair.first;
                }
            }
            List<T> out = new ArrayList();
            if (extend == null || extend.length == 0)
                return out;
            ContentResolver cr = context.getContentResolver();
            Uri uri = MediaStore.Files.getContentUri("external");
            String[] projection = new String[]{MediaStore.Files.FileColumns.DATA};
            String selectionInclude = null;
            if (extend.length >= 1) {
                for (int i = 0; i < extend.length; i++) {
                    if (selectionInclude == null) {
                        selectionInclude = MediaStore.Files.FileColumns.DATA + " LIKE ?";
                    } else {
                        selectionInclude += "OR " + MediaStore.Files.FileColumns.DATA
                                + " LIKE ?";
                    }
                    extend[i] = "%" + extend[i];
                }
            }
            String selectionNameContent = null;
            if (nameContent.length >= 1) {
                for (int i = 0; i < nameContent.length; i++) {
                    if (nameContent[i] != null && nameContent[i].length() > 0) {
                        if (selectionNameContent == null) {
                            selectionNameContent = "LOWER(" + MediaStore.Files.FileColumns.DATA + ")" + " LIKE ?";
                        } else {
                            selectionNameContent += "AND LOWER(" + MediaStore.Files.FileColumns.DATA
                                    + ") LIKE ?";
                        }
                        nameContent[i] = "%" + nameContent[i].toLowerCase() + "%";
                    }
                }
            }
            String selectionPathToTarget = null;
            if (targetDirs != null && targetDirs.length >= 1) {
                for (int i = 0; i < targetDirs.length; i++) {
                    if (selectionPathToTarget == null) {
                        selectionPathToTarget = MediaStore.Files.FileColumns.DATA + " LIKE ?";
                    } else {
                        selectionPathToTarget += "OR " + MediaStore.Files.FileColumns.DATA
                                + " LIKE ?";
                    }

                    targetDirs[i] = targetDirs[i] + "%";
                }
            }
            String selectionIgnore = null;
            if (ignoredDir != null && ignoredDir.length >= 1) {
                for (int i = 0; i < ignoredDir.length; i++) {
                    if (selectionIgnore == null) {
                        selectionIgnore = MediaStore.Files.FileColumns.DATA + " NOT LIKE ?";
                    } else {
                        selectionIgnore += "AND " + MediaStore.Files.FileColumns.DATA
                                + " NOT LIKE ?";
                    }

                    ignoredDir[i] = "%" + ignoredDir[i];
                }
            }
            String selection = "";
            if (!istat.android.base.tools.TextUtils.isEmpty(selectionInclude)) {
                selection += "(" + selectionInclude + ")";
            }
            if (!istat.android.base.tools.TextUtils.isEmpty(selectionNameContent)) {
                selection += (istat.android.base.tools.TextUtils.isEmpty(selection) ? "" : " AND ") + "(" + selectionNameContent + ")";
            }
            if (!istat.android.base.tools.TextUtils.isEmpty(selectionPathToTarget)) {
                selection += (istat.android.base.tools.TextUtils.isEmpty(selection) ? "" : " AND ") + "(" + selectionPathToTarget + ")";
            }
            if (!istat.android.base.tools.TextUtils.isEmpty(selectionIgnore)) {
                selection += (istat.android.base.tools.TextUtils.isEmpty(selection) ? "" : " AND ") + "(" + selectionIgnore + ")";
            }

            List<String> argsList = new ArrayList<>();
            if (extend != null && extend.length > 0) {
                argsList.addAll(Arrays.asList(extend));
            }
            if (nameContent != null && nameContent.length > 0) {
                argsList.addAll(Arrays.asList(nameContent));
            }
            if (ignoredDir != null && ignoredDir.length > 0) {
                argsList.addAll(Arrays.asList(ignoredDir));
            }
            Cursor allNonMediaFiles = cr.query(uri, projection, selection,
                    argsList.toArray(new String[argsList.size()]), sortOrder);
            if (allNonMediaFiles != null && allNonMediaFiles.getCount() > 0) {
                T entity;
                while (allNonMediaFiles.moveToNext()) {
                    entity = decoder.decode(allNonMediaFiles.getString(0));
                    if (entity != null) {
                        out.add(entity);
                    }
                }
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

        public static final String fileExtension(File file) {
            return fileExtension(file.getName());
        }

        public static final String fileExtension(String fileUri) {
            if (fileUri == null) {
                return null;
            }
            int index = fileUri.lastIndexOf(".") + 1;
            return index > 0 && fileUri.length() > index ? fileUri.substring(index) : "";
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

        public static final boolean delete(File file) {
            if (file.isDirectory()) {
                int deleted = deleteDirectory(file);
                return deleted > 0;
            } else {
                return file.delete();
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

        public static final long computeDirectoryContentBytes(File directory) {
            long bytes = 0;
            File[] files = directory.listFiles();
            if (files != null) {
                int fileCount = files.length;
                for (int i = 0; i < fileCount; ++i) {
                    File currentFile = files[i];
                    if (currentFile.isDirectory()) {
                        bytes += computeDirectoryContentBytes(currentFile);
                    }
                }
                bytes += directory.length();
            }
            return bytes;
        }

        public static final int copyDirectory(File source, File destination) throws IOException {
            int copied = 0;
            File[] files = source.listFiles();
            if (files != null) {
                File[] var5 = files;
                int var4 = files.length;
                File targetFile;
                for (int var3 = 0; var3 < var4; ++var3) {
                    File f = var5[var3];
                    targetFile = new File(destination, f.getName());
                    if (f.isDirectory()) {
                        copyDirectory(f, targetFile);
                    }
                    copied++;
                    if (targetFile.isDirectory()) {
                        continue;
                    }
                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }
                    Stream.copyStream(f, targetFile);
                }
                copied++;
            }
            return copied;
        }

        public static final int moveDirectory(File source, File destination) throws IOException {
            copyDirectory(source, destination);
            return deleteDirectory(source);
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
                float size = (float) e.available();
                e.close();
                return (double) size >= Math.pow(1000.0D, 3.0D) ? ToolKits.Word.shortWord(ToolKits.Word.adjustNumber(size / 1.07374182E9F), maxChar) + " Go" : (size >= 100000.0F ? ToolKits.Word.shortWord(ToolKits.Word.adjustNumber(size / 1048576.0F), maxChar) + " Mo" : (size >= 1000.0F ? ToolKits.Word.shortWord(ToolKits.Word.adjustNumber(size / 1024.0F), maxChar) + " Ko" : ToolKits.Word.shortWord(ToolKits.Word.adjustNumber(size), maxChar) + " oc"));
            } catch (Exception var4) {
                Log.e("tkit.filesise.error", "Error:" + var4);
                return "--*--";
            }
        }

        public static final String integerToFileSize(int byteCount) {
            return (double) byteCount >= Math.pow(1000.0D, 3.0D) ? ToolKits.Word.adjustNumber((float) ((int) ((float) byteCount / 1.07374182E9F * 100.0F)) / 100.0F) + " Go" : (byteCount >= 100000 ? ToolKits.Word.adjustNumber((float) ((int) ((float) byteCount / 1048576.0F * 100.0F)) / 100.0F) + " Mo" : (byteCount >= 1000 ? ToolKits.Word.adjustNumber((float) ((int) ((float) byteCount / 1024.0F * 100.0F)) / 100.0F) + " Ko" : ToolKits.Word.adjustNumber((float) byteCount) + " oc"));
        }

        public static final int computeURLSize(String url) {
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

        static int computeURLConnexionSize(String url) {
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

        public Boolean isSoftKeyBoardVisible(Context context) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isAcceptingText()) {
                return true;
            } else {
                return false;
            }
        }

        public final static boolean isKeyboardShown(Context context) {
            return isKeyboardShown(context, null);
        }

        public final static boolean isKeyboardShown(Context context, View view) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (view != null) {
                return imm.isActive(view);
            } else {
                return imm.isActive();
            }
        }

        public final static void showKeyboard(Context context) {
            if (context == null) {
                return;
            }
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
        }

        public static void closeKeyboard(Activity activity) {
            if (activity == null) {
                return;
            }
            View targetView = activity.getCurrentFocus();
            if (targetView == null) {
                if (activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
                    targetView = activity.getWindow().getDecorView();
                }
            }
            if (targetView != null) {
                closeKeyboard(activity, targetView.getWindowToken());
            }
        }

        public static void closeKeyboard(View v) {
            if (v == null) {
                return;
            }
            IBinder token = v.getWindowToken();
            if (token == null) {
                View rootView = v.getRootView();
                if (rootView != null) {
                    token = rootView.getWindowToken();
                }
                if (token == null) {
                    token = v.getApplicationWindowToken();
                }
            }
            closeKeyboard(v.getContext(), token);
        }

        public static void closeKeyboard(Context context, IBinder token) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(token, 0);
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

        /**
         * Get ISO 3166-1 alpha-2 country code for this device (or null if not available)
         *
         * @param context Context reference to get the TelephonyManager instance from
         * @return country code or null
         */
        public static String queryDeviceLocationCountry(Context context) {
            //http://ip-api.com/json  to request from the web api
            try {
                final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                final String simCountry = tm.getSimCountryIso();
                if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                    return simCountry.toLowerCase(Locale.US);
                } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                    String networkCountry = tm.getNetworkCountryIso();
                    if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                        return networkCountry.toLowerCase(Locale.US);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @SuppressLint("MissingPermission")
        public static boolean vibrate(Context context, long duration) {
            if (context == null || duration <= 0) {
                return false;
            }
            if (ToolKits.Software.hasPermission(context, "android.permission.VIBRATE")) {
                ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(duration);
                return true;
            }
            return false;

        }

        @SuppressLint("MissingPermission")
        public static boolean vibrate(Context context, long[] pattern, int repeat) {
            if (context == null || pattern == null || pattern.length == 0) {
                return false;
            }
            if (ToolKits.Software.hasPermission(context, "android.permission.VIBRATE")) {
                ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(pattern, repeat);
                return true;
            }
            return false;
        }
    }

    public static final class IntentShortCuts {
        public IntentShortCuts() {
        }

        public static void startCropper(Activity context, Uri picUri, int code, Point dimens, Uri destination) throws ActivityNotFoundException {
            startCropper(context, picUri, code, null, dimens, destination);
        }

        //https://play.google.com/store/search?q=image%20crop&c=apps&hl=fr
        public static void startCropper(Activity context, Uri picUri, int code, Point aspect, Point dimens, Uri destination) throws ActivityNotFoundException {
//            if (aspect == null) {
//                aspect = new Point(1, 1);
//            }
            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            if (aspect != null) {
                cropIntent.putExtra("aspectX", aspect.x);
                cropIntent.putExtra("aspectY", aspect.y);
            }
            if (dimens != null) {
                cropIntent.putExtra("outputX", dimens.x);
                cropIntent.putExtra("outputY", dimens.y);
            }
            cropIntent.putExtra("return-data", true);
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, destination);
            cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            context.startActivityForResult(cropIntent, code);
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

        public static boolean isTablet(Context context) {
            return (context.getResources().getConfiguration().screenLayout
                    & Configuration.SCREENLAYOUT_SIZE_MASK)
                    >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        }

        public final static boolean isScreenVisible(View v) {
            Rect rect = new Rect();
            View root = v.getRootView();
            root.getHitRect(rect);
            return v.getLocalVisibleRect(rect);
        }

        /**
         * Lock the screen orientation
         *
         * @param context
         * @return current screen orientation in case you will have to restore it. using #Context#setRequestedOrientation]
         */
        public static int setOrientationLocked(Activity context) {
            if (context == null) {
                return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
            }
            int currentOrientation = context.getRequestedOrientation();
            int orientation = context.getWindowManager().getDefaultDisplay()
                    .getRotation();
            if (orientation == Surface.ROTATION_90
                    || orientation == Surface.ROTATION_270) {
                istat.android.base.tools.ToolKits.Screen.setLandScape(context);
            } else {
                istat.android.base.tools.ToolKits.Screen.setPortrait(context);
            }
            return currentOrientation;
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

        public final static boolean isLandScape(Activity context) {
            int orientation = context.getWindowManager().getDefaultDisplay()
                    .getRotation();
            if (orientation == Surface.ROTATION_90
                    || orientation == Surface.ROTATION_270) {
                return true;
            } else {
                return false;
            }
        }

        public final static boolean isPortrait(Activity context) {
            int orientation = context.getWindowManager().getDefaultDisplay()
                    .getRotation();
            if (orientation == Surface.ROTATION_90
                    || orientation == Surface.ROTATION_270) {
                return false;
            } else {
                return true;
            }
        }

        public static void setSystemUiVisible(Activity activity, boolean state) {
            if (state) {
                showSystemUI(activity);
            } else {
                hideToolBar(activity);
            }
        }

        public static void showSystemUI(Activity activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        }

        public static void hideToolBar(Activity activity) {

            // BEGIN_INCLUDE (get_current_ui_flags)
            // The UI options currently enabled are represented by a bitfield.
            // getSystemUiVisibility() gives us that bitfield.
            int uiOptions = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                uiOptions = activity.getWindow().getDecorView().getSystemUiVisibility();
            }
            int newUiOptions = uiOptions;
            // END_INCLUDE (get_current_ui_flags)
            // BEGIN_INCLUDE (toggle_ui_flags)
            boolean isImmersiveModeEnabled =
                    ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
//        if (isImmersiveModeEnabled) {
//            Log.i(Constants.TAG_DEF, "Turning immersive mode mode off. ");
//        } else {
//            Log.i(Constants.TAG_DEF, "Turning immersive mode mode on.");
//        }

            // Navigation bar hiding:  Backwards compatible to ICS.
            if (Build.VERSION.SDK_INT >= 14) {
                newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }

            // Status bar hiding: Backwards compatible to Jellybean
            if (Build.VERSION.SDK_INT >= 16) {
                newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
            }

            // Immersive mode: Backward compatible to KitKat.
            // Note that this flag doesn't do anything by itself, it only augments the behavior
            // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
            // all three flags are being toggled together.
            // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
            // Sticky immersive mode differs in that it makes the navigation and status bars
            // semi-transparent, and the UI flag does not get cleared when the user interacts with
            // the screen.
            if (Build.VERSION.SDK_INT >= 18) {
                newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                activity.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
            }
            //END_INCLUDE (set_ui_flags)

        }

    }

    public static final class Software {
        private Software() {
        }

        public static boolean isAppForeground(Context context) {
            if (!((KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE)).inKeyguardRestrictedInputMode()) {
                if (SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    SystemClock.sleep(10L);
                }
                int var1 = Process.myPid();
                List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses();
                if (runningAppProcessInfos != null) {
                    Iterator<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoIterator = runningAppProcessInfos.iterator();

                    while (runningAppProcessInfoIterator.hasNext()) {
                        ActivityManager.RunningAppProcessInfo appProcessInfo = runningAppProcessInfoIterator.next();
                        if (appProcessInfo.pid == var1) {
                            if (appProcessInfo.importance == 100) {
                                return true;
                            }

                            return false;
                        }
                    }
                }
            }
            return false;
        }


        @RequiresApi(api = Build.VERSION_CODES.M)
        public static boolean finishRunningTask(Context context, Class<?> activityClass) {
            if (activityClass == null) {
                return false;
            }
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.AppTask> tasks = activityManager.getAppTasks();
            Iterator<ActivityManager.AppTask> taskIterator = tasks.iterator();
            ActivityManager.AppTask task;
            ActivityManager.RecentTaskInfo taskInfo;
            while (taskIterator.hasNext()) {
                task = taskIterator.next();
                if (task != null) {
                    taskInfo = task.getTaskInfo();
                    if (taskInfo.baseActivity != null && activityClass.getCanonicalName().equalsIgnoreCase(taskInfo.baseActivity.getClassName())) {
                        task.finishAndRemoveTask();
                        return true;
                    }
                }
            }
            return false;
        }

        public static void installApk(Context context, String apkFile) {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setDataAndType(Uri.fromFile(new File(apkFile)), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

        public static boolean isRunningOnTop(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List tasks = activityManager.getRunningTasks(1);
            if (!tasks.isEmpty()) {
                ActivityManager.RunningTaskInfo task = (ActivityManager.RunningTaskInfo) tasks.get(0);
                ComponentName component = task.baseActivity;
                if (component == null) {
                    return false;
                }
                if (!Objects.equals(component.getPackageName(), context.getPackageName())) {
                    return false;
                }
                try {
                    Class cLass = Class.forName(component.getClassName());
                    return Activity.class.isAssignableFrom(cLass);
                } catch (Exception e) {
                    return false;
                }
            }
            return false;
        }

//        public static boolean hasRunningActivity(Context context) {
//            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//            List tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
//            Iterator taskIterator = tasks.iterator();
//
//            while (taskIterator.hasNext()) {
//                ActivityManager.RunningTaskInfo task = (ActivityManager.RunningTaskInfo) taskIterator.next();
//                try {
//                    Class cLass = Class.forName(task.baseActivity.getClassName());
//
//                } catch (Exception e) {
//
//                }
//            }
//            return false;
//        }

        public static Boolean isActivityRunning(Context context, Class<?> activityClass) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
            Iterator taskIterator = tasks.iterator();

            while (taskIterator.hasNext()) {
                ActivityManager.RunningTaskInfo task = (ActivityManager.RunningTaskInfo) taskIterator.next();
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Objects.equals(permission, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        Objects.equals(permission, Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
                    return Environment.isExternalStorageManager();
                }
                if (Objects.equals(permission, Manifest.permission.READ_EXTERNAL_STORAGE) && Environment.isExternalStorageManager()) {
                    return true;
                }
            }
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

        public static String getAppNameFromPkgName(Context context, String PackageName) {
            try {
                PackageManager packageManager = context.getPackageManager();
                ApplicationInfo info = packageManager.getApplicationInfo(PackageName, PackageManager.GET_META_DATA);
                String appName = (String) packageManager.getApplicationLabel(info);
                return appName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return "";
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

        public static void checkSignature(final Context context, String appCertificate) {
            try {
                Signature[] signatures = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).signatures;

                if (appCertificate == null || appCertificate.equals(signatures[0].toCharsString())) {
                    // Kill the process without warning. If someone changed the certificate
                    // is better not to give a hint about why the app stopped working
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            } catch (PackageManager.NameNotFoundException ex) {
                // Must never fail, so if it does, means someone played with the apk, so kill the process
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }

        public static boolean packageExists(Context context, final String packageName) {
            try {
                ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, 0);

                if (info == null) {
                    // No need really to test for null, if the package does not
                    // exist it will really rise an exception. but in case Google
                    // changes the API in the future lets be safe and test it
                    return false;
                }

                return true;
            } catch (Exception ex) {
                // If we get here only means the Package does not exist
            }

            return false;
        }

        public static long retrieveCurrentAppApkBuildTime(Context context) {
            return retrieveAppApkBuildTime(context, context.getPackageName());
        }

        /*
        A hint for solution "last modification time of classes.dex file" an newer AndroidStudio versions: In default config the timestamp is not written anymore to files in apk file. Timestamp is always "Nov 30 1979".
        You can change this behavior by adding this line to file
        %userdir%/.gradle/gradle.properties (create if not existing)
        android.keepTimestampsInApk = true

        https://stackoverflow.com/questions/7607165/how-to-write-build-time-stamp-into-apk
         */
        public static long retrieveAppApkBuildTime(Context context, String packageName) {
            try {
                ApplicationInfo ai = context.getPackageManager().getApplicationInfo(packageName, 0);
                ZipFile zf = new ZipFile(ai.sourceDir);
                ZipEntry ze = zf.getEntry("classes.dex");
                long time = ze.getTime();
                zf.close();
                return time;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }
    }

    public static final class Stream {
        public static final int DEFAULT_BUFFER_SIZE = 8192;

        private Stream() {
        }

        public static final String streamToString(InputStream inp, String encoding) {
            String out = "";
            StringBuilder total = new StringBuilder();

            try {
                BufferedReader e = new BufferedReader(new InputStreamReader(inp, encoding));

                String line;
                while ((line = e.readLine()) != null) {
                    if (!istat.android.base.tools.TextUtils.isEmpty(total.toString())) {
                        total.append("\n");
                    }
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
            byte[] b = new byte[DEFAULT_BUFFER_SIZE];
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

        public static final void inputToOutput(String add, InputStream in, OutputStream out) throws Exception {
            byte[] b = new byte[DEFAULT_BUFFER_SIZE];
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
            byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
            int count;
            while (true) {
                count = is.read(bytes, 0, DEFAULT_BUFFER_SIZE);
                out += count;
                if (count == -1) {
                    break;
                }
                os.write(bytes, 0, count);
            }
            return out;
        }

        @SuppressLint("NewApi")
        public static long bufferedCopyStream(InputStream in, OutputStream ou) throws IOException {
            return bufferedCopyStream(in, ou, true);
        }

        @SuppressLint("NewApi")
        public static long bufferedCopyStream(InputStream in, OutputStream ou, boolean autoClose) throws IOException {
            if (autoClose) {
                try (BufferedInputStream br = new BufferedInputStream(in);
                     BufferedOutputStream bw = new BufferedOutputStream(ou)) {
                    return copyStream(br, bw);
                }
            } else {
                BufferedInputStream br = new BufferedInputStream(in);
                BufferedOutputStream bw = new BufferedOutputStream(ou);
                return copyStream(br, bw);
            }
        }

//        @SuppressLint("NewApi")
//        public static long bufferedCopyStream(InputStream in, OutputStream ou) throws IOException {
//            long out = 0;
//            try (BufferedReader br = new BufferedReader(
//                    new InputStreamReader(in));
//                 BufferedWriter bw = new BufferedWriter(
//                         new OutputStreamWriter(ou))) {
//
//                String line;
//                while ((line = br.readLine()) != null) {
//                    out += line.length();
//                    bw.write(line);
//                    bw.newLine();
//                }
//            }
//            return out;
//        }


        public static final long copyStream(InputStream is, OutputStream os, Decoder<byte[], byte[]> transformation) throws Exception {
            long out = 0;
            byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
            int count;
            while (true) {
                count = is.read(bytes, 0, DEFAULT_BUFFER_SIZE);
                out += count;
                if (count == -1) {
                    break;
                }
                if (transformation != null) {
                    bytes = transformation.decode(bytes);
                }
                os.write(bytes, 0, count);
            }
            return out;
        }

        public static final long copyStream(InputStream inputStream, OutputStream outputStream, int startByte) throws IOException {
            long count = 0;
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            inputStream.skip(startByte);
            int n;
            while (-1 != (n = inputStream.read(buffer))) {
                outputStream.write(buffer, 0, n);
                count += n;
            }
            return count;

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
            return parseDouble(o, 0.0D);
        }

        public static final double parseDouble(Object o, double defaultValue) {
            String s = parseString(o);
            if (s != null && !s.equals("")) {
                if (s.trim().length() == 0) {
                    return defaultValue;
                } else {
                    try {
                        return Double.parseDouble(s.trim());
                    } catch (Exception var3) {
                        return defaultValue;
                    }
                }
            } else {
                return defaultValue;
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

        public static final Calendar parseToCalendar(Object obj) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            GregorianCalendar cal = new GregorianCalendar();

            try {
                cal.setTime(df.parse(parseString(obj)));
            } catch (Exception var4) {
                var4.printStackTrace();
            }

            return cal;
        }

        public static final String replaceFinalPunctuation(String word, String endingPunctuation) {
            if (TextUtils.isEmpty(word)) {
                return word;
            }
//            return word.replaceFirst("[^\n](\\p{Punct})$", endingPunctuation);
            return word.replaceFirst("(\\p{Punct})$", endingPunctuation);
        }

        public static final String toSentence(String word, String endingPunctuation) {
            if (TextUtils.isEmpty(word)) {
                return word;
            }
            word = beginByUpperCase(TextUtils.trim(word));
//            String regex = "((.*)(\\?|\\.|!|:|;|,|¿))$";
            String regex = "((.*)(\\p{Punct}))$";
            boolean match = word.replaceAll("\n", " ").matches(regex);
            if (match) {
                return word;
            }
            return word + endingPunctuation;
        }

        public static final String beginByUpperCase(String word) {
            try {
                if (word.length() > 1) {
                    String begin = word.substring(0, 1).toUpperCase(Locale.getDefault());
                    word = begin + word.substring(1);
                }
                return word;
            } catch (Exception e) {
                e.printStackTrace();
                return word;
            }
        }

        public static final String beginByLowerCase(String word) {
            try {
                if (word.length() > 1) {
                    String begin = word.substring(0, 1).toLowerCase(Locale.getDefault());
                    word = begin + word.substring(1);
                }
                return word;
            } catch (Exception e) {
                e.printStackTrace();
                return word;
            }
        }

        public static final int parseInt(Object obj) {
            return parseInt(obj, 0);
        }

        public static final int parseInt(Object obj, int defaultValue) {
            return parseInt(parseString(obj), defaultValue);
        }

        public static final int parseInt(String s) {
            return parseInt(s, 0);
        }

        public static final int parseInt(String s, int defaultValue) {
            return (int) parseDouble(s, defaultValue);
        }

        public static final String numberToWords(long number, Locale locale) {
            return !locale.equals(Locale.FRANCE) && !locale.equals(Locale.FRENCH) ? EnglishNumberToWords.convert(number) : FrenchNumberToWords.convert(number);
        }

        public static final String wordToNumber(String word, Locale locale) {
            return !locale.equals(Locale.FRANCE) && !locale.equals(Locale.FRENCH) ? EnglishWordToNumber.replaceNumbers(word) : EnglishWordToNumber.replaceNumbers(word);
        }

        public static final String shortWord(CharSequence charSequence, int max) {
            return shortWord(charSequence, max, "...");
        }

        public static final String shortWord(CharSequence charSequence, int max, String ellipsisEnding) {
            String word = charSequence.toString();
            return word.length() <= max ? word : word.substring(0, max) + (ellipsisEnding != null ? ellipsisEnding : "");
        }

        public static final String distanceToKm(String word) {
            int d = (int) (Double.valueOf(word).doubleValue() * 1.0D);
            return word.equals("-1") ? "---" : (d > 1500 ? d / 1000 + "Km" : d + "m");
        }

        public static final float getPercentNumericValue(int progression, int STEP, boolean comma) {
            return comma ? (float) progression * 100.0F / (float) STEP : (float) ((int) ((float) progression * 100.0F / (float) STEP));
        }

        public static final String getPercentValue(int progression, int STEP, boolean comma) {
            return comma ? (float) progression * 100.0F / (float) STEP + "%" : (int) ((float) progression * 100.0F / (float) STEP) + "%";
        }

        public static final String getPercentValue(int progression, int STEP) {
            return (float) progression * 100.0F / (float) STEP + "%";
        }

        public static final float getPercentNumericValue(int progression, int STEP) {
            return (float) progression * 100.0F / (float) STEP;
        }

        public static final String sweetNumber(int a) {
            return a > 9 || a <= 0 ? "" + a : "0" + a;
        }

        public static final String adjustNumber(double a) {
            return (double) ((int) a) == a ? "" + (int) a : "" + a;
        }

        public static final String adjustedSweetNumber(double a) {
            return (double) ((int) a) == a ? sweetNumber((int) a) : "" + a;
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

        public static final boolean isNumber(Object a) {
            if (a == null) {
                return false;
            }
            return isNumber(a.toString());
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
