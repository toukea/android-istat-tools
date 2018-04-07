package istat.android.base.memories;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

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
        SavePreferences(this.context, this.file, key, value, this.mode);
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
        SavePreferences(this.context, this.file, key, gson.toJson(value), this.mode);
    }

    public <T> T load(String key, Class<T> cLass) {
        Gson gson = new Gson();
        String jsonString = LoadPreferences(this.context, this.file, key, (String) null, this.mode);
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        return gson.fromJson(jsonString, cLass);
    }

    public String load(String key) {
        return LoadPreferences(this.context, this.file, key, (String) null, this.mode);
    }

    public String load(String key, String deflt) {
        return LoadPreferences(this.context, this.file, key, deflt, this.mode);
    }

    public int loadInt(String key, int deflt) {
        try {
            return Integer.valueOf(LoadPreferences(this.context, this.file, key, "" + deflt, this.mode)).intValue();
        } catch (Exception var4) {
            return deflt;
        }
    }

    public long loadLong(String key, long deflt) {
        try {
            return Long.valueOf(LoadPreferences(this.context, this.file, key, "" + deflt, this.mode)).longValue();
        } catch (Exception var5) {
            return deflt;
        }
    }

    public float loadFloat(String key, float deflt) {
        try {
            return (float) Integer.valueOf(LoadPreferences(this.context, this.file, key, "" + deflt, this.mode)).intValue();
        } catch (Exception var4) {
            return deflt;
        }
    }

    public double loadDouble(String key, double deflt) {
        try {
            return (double) Integer.valueOf(LoadPreferences(this.context, this.file, key, "" + deflt, this.mode)).intValue();
        } catch (Exception var5) {
            return deflt;
        }
    }

    public boolean loadBoolean(String key, boolean deflt) {
        try {
            return Boolean.valueOf(LoadPreferences(this.context, this.file, key, "" + deflt, this.mode)).booleanValue();
        } catch (Exception var4) {
            return deflt;
        }
    }

    public boolean contain(String key) {
        return contain(this.context, this.file, key);
    }

    public Preferences setMode(int mode) {
        this.mode = mode;
        return this;
    }

    public int getMode() {
        return this.mode;
    }

    public SharedPreferences.Editor getEditor() {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(this.file, this.mode);
        return sharedPreferences.edit();
    }

    public static void SavePreferences(Context context, String File, String key, String value, int mode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(File, mode);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String LoadPreferences(Context context, String File, String key, String deflt, int mode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(File, mode);
        return sharedPreferences.getString(key, deflt);
    }

    public static boolean contain(Context context, String File, String key) {
        return context.getSharedPreferences(File, 0).contains(key);
    }
}
