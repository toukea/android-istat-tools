package istat.android.base.memories;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

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
 * Deprecated use {@see Preferences} instead.
 */
@Deprecated
public class Configs {
    Context context;
    String file = "DEFAULT";
    int mode = 0;

    public Configs(Context context) {
        this.context = context;
    }

    public boolean clear(String key) {
        if (!this.contain(key)) {
            return false;
        } else {
            Editor editor = this.getEditor();
            editor.remove(key);
            editor.commit();
            return true;
        }
    }

    public boolean clear() {
        Editor editor = this.getEditor();
        editor.clear();
        editor.commit();
        return true;
    }

    public Configs getFile(String file) {
        this.file = file;
        return this;
    }

    public void save(String key, String value) {
        SavePreferences(this.context, this.file, key, value, this.mode);
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

    public Configs setMode(int mode) {
        this.mode = mode;
        return this;
    }

    public int getMode() {
        return this.mode;
    }

    public Editor getEditor() {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(this.file, this.mode);
        return sharedPreferences.edit();
    }

    public static void SavePreferences(Context context, String File, String key, String value, int mode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(File, mode);
        Editor editor = sharedPreferences.edit();
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
