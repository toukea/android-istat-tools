package istat.android.base.memories;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.Map;

import istat.android.base.tools.TextUtils;

/**
 * Created by istat on 27/05/17.
 */

public class Preferences {
    Context context;
    String file = "DEFAULT";
    int mode = 0;

    public Preferences(Context context) {
        this.context = context;
    }

    public boolean clear(String key) {
        if (!this.contain(key)) {
            return false;
        } else {
            SharedPreferences.Editor editor = this.getEditor();
            editor.remove(key);
            editor.commit();
            return true;
        }
    }

    public boolean clear() {
        SharedPreferences.Editor editor = this.getEditor();
        editor.clear();
        editor.commit();
        return true;
    }

    public Preferences getFile(String name) {
        this.file = name;
        return this;
    }

    public void save(String key, String value) {
        savePreferences(this.context, this.file, key, value, this.mode);
    }

    public void save(String key, int value) {
        save(key, String.valueOf(value));
    }

    public void save(String key, float value) {
        save(key, String.valueOf(value));
    }

    public void save(String key, double value) {
        save(key, String.valueOf(value));
    }

    public void save(String key, long value) {
        save(key, String.valueOf(value));
    }

    public void save(String key, boolean value) {
        save(key, String.valueOf(value));
    }

    public void save(String key, Object value) {
        Gson gson = new Gson();
        String savedValue = gson.toJson(value);
        savePreferences(this.context, this.file, key, savedValue, this.mode);
    }

    public <T> T load(String key, Class<T> cLass) {
        String jsonString = loadPreferences(this.context, this.file, key, null, this.mode);
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        Gson gson = new Gson();
        return gson.fromJson(jsonString, cLass);
    }

    public String load(String key) {
        return loadPreferences(this.context, this.file, key, null, this.mode);
    }

    public String load(String key, String defaultValue) {
        return loadPreferences(this.context, this.file, key, defaultValue, this.mode);
    }

    public int loadInt(String key, int defaultValue) {
        try {
            return Integer.valueOf(loadPreferences(this.context, this.file, key, "" + defaultValue, this.mode)).intValue();
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public long loadLong(String key, long defaultValue) {
        try {
            return Long.valueOf(loadPreferences(this.context, this.file, key, "" + defaultValue, this.mode)).longValue();
        } catch (Exception var5) {
            return defaultValue;
        }
    }

    public float loadFloat(String key, float defaultValue) {
        try {
            return (float) Float.valueOf(loadPreferences(this.context, this.file, key, "" + defaultValue, this.mode)).intValue();
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public double loadDouble(String key, double defaultValue) {
        try {
            return Double.valueOf(loadPreferences(this.context, this.file, key, "" + defaultValue, this.mode)).intValue();
        } catch (Exception var5) {
            return defaultValue;
        }
    }

    public boolean loadBoolean(String key, boolean defaultValue) {
        try {
            return Boolean.valueOf(loadPreferences(this.context, this.file, key, "" + defaultValue, this.mode)).booleanValue();
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    @Deprecated
    public boolean contain(String key) {
        return contains(this.context, this.file, key);
    }

    public boolean contains(String key) {
        return contains(this.context, this.file, key);
    }

    public Preferences setMode(int mode) {
        this.mode = mode;
        return this;
    }

    public int getMode() {
        return this.mode;
    }

    public Map<String, ?> getAll() {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(this.file, this.mode);
        return sharedPreferences.getAll();
    }

    public SharedPreferences.Editor getEditor() {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(this.file, this.mode);
        return sharedPreferences.edit();
    }

    public static void savePreferences(Context context, String File, String key, String value, int mode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(File, mode);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
        editor.apply();
    }

    public static String loadPreferences(Context context, String File, String key, String defaultValue, int mode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(File, mode);
        return sharedPreferences.getString(key, defaultValue);
    }

    public static boolean contains(Context context, String File, String key) {
        return context.getSharedPreferences(File, 0).contains(key);
    }

    public int length() {
        return getAll().size();
    }
}
