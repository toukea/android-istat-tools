package istat.android.base.memories;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import istat.android.base.interfaces.Decoder;
import istat.android.base.tools.TextUtils;
import istat.android.base.tools.ToolKits;

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

    public String getNameSpace() {
        return file;
    }

    public Preferences getFile(String name) {
        this.file = name;
        return this;
    }

    public void save(String key, String value) {
        savePreferences(this.context, this.file, key, value, this.mode);
    }

    public void save(HashMap<String, Object> nameValues) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(file, mode);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (Map.Entry<String, Object> entry : nameValues.entrySet()) {
            editor.putString(entry.getKey(), String.valueOf(entry.getValue()));
        }
        editor.commit();
        editor.apply();
    }

    public void save(List<String> keys, Decoder<String, Object> keyValueDecoder) {
        try {
            save(keys, keyValueDecoder, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(List<String> keys, Decoder<String, Object> keyValueDecoder, boolean throwOnError) throws Exception {
        SharedPreferences sharedPreferences = context.getSharedPreferences(file, mode);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (String key : keys) {
            try {
                editor.putString(key, String.valueOf(keyValueDecoder.decode(key)));
            } catch (Exception e) {
                if (throwOnError) {
                    throw e;
                }
            }
        }
        editor.commit();
        editor.apply();
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

    public void saveBoolean(String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(file, mode);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
        editor.apply();
    }

    public void save(String key, Object value) {
        Gson gson = new Gson();
        String savedValue = gson.toJson(value);
        savePreferences(this.context, this.file, key, savedValue, this.mode);
    }

    public double decrementValue(String key) {
        return incrementValue(key, -1);
    }

    public double decrementValue(String key, double decrementing) {
        return incrementValue(key, decrementing, null, null);
    }

    public double decrementValue(String key, double decrementing, double minValue) {
        return incrementValue(key, decrementing, minValue, null);
    }

    public double incrementValue(String key) {
        return incrementValue(key, 1);
    }

    public double incrementValue(String key, double incrementation) {
        return incrementValue(key, incrementation, null, null);
    }

    public double incrementValue(String key, double incrementation, double maxValue) {
        return incrementValue(key, incrementation, null, maxValue);
    }

    public double incrementValue(String key, double incrementation, Double minValue, Double maxValue) {
        String value = load(key);
        double doubleValue;
        if (value != null && !ToolKits.WordFormat.isNumber(value)) {
            return 0;
        } else {
            doubleValue = ToolKits.Word.parseDouble(value);
        }
        doubleValue = doubleValue + incrementation;
        if (maxValue != null && doubleValue > maxValue) {
            return maxValue;
        } else if (minValue != null && doubleValue < minValue) {
            return minValue;
        }
        save(key, doubleValue);
        return doubleValue;
    }

    public <T> T load(String key, Class<T> cLass, T defaultValue) {
        try {
            return load(key, cLass);
        } catch (Exception e) {
            return defaultValue;
        }
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

    @Deprecated
    public String load(String key, String defaultValue) {
        return loadPreferences(this.context, this.file, key, defaultValue, this.mode);
    }

    public int loadInt(String key, int defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(file, mode);
        try {
            if (sharedPreferences.contains(key)) {
                return sharedPreferences.getInt(key, defaultValue);
            }
            return defaultValue;
        } catch (Exception e1) {
            try {
                return Integer.parseInt(loadPreferences(this.context, this.file, key, "" + defaultValue, this.mode));
            } catch (Exception e2) {
                return defaultValue;
            }
        }
    }

    public long loadLong(String key, long defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(file, mode);
        try {
            if (sharedPreferences.contains(key)) {
                return sharedPreferences.getLong(key, defaultValue);
            }
            return defaultValue;
        } catch (Exception e1) {
            try {
                return Long.parseLong(loadPreferences(this.context, this.file, key, "" + defaultValue, this.mode));
            } catch (Exception e2) {
                return defaultValue;
            }
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
        SharedPreferences sharedPreferences = context.getSharedPreferences(file, mode);
        try {
            if (sharedPreferences.contains(key)) {
                return sharedPreferences.getBoolean(key, defaultValue);
            }
            return defaultValue;
        } catch (Exception e1) {
            try {
                return Boolean.parseBoolean(loadPreferences(this.context, this.file, key, "" + defaultValue, this.mode));
            } catch (Exception e2) {
                return defaultValue;
            }
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

    public boolean containsKeyValue(String preferenceKey, Object value) {
        if (!contains(preferenceKey)) {
            return false;
        }
        String persistedValue = load(preferenceKey);
        return Objects.equals(persistedValue, String.valueOf(value));
    }

    public String loadString(String key, String defaultValue) {
        return load(key, defaultValue);
    }
}
