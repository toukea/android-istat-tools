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
 * 
 * @author Toukea Tatsi (Istat)
 * 
 */
public class Configs {
	Context context;
	String file = "DEFAULT";
	int mode = 0;

	public Configs(Context context) {
		this.context = context;
	}

	public Configs getFile(String file) {
		this.file = file;
		return this;
	}

	public void save(String key, Object value) {
		if (value != null)
			SavePreferences(context, file, key, value.toString(), mode);
	}

	public String load(String key) {
		return LoadPreferences(context, file, key, null, mode);
	}

	public String load(String key, String deflt) {
		return LoadPreferences(context, file, key, deflt, mode);
	}

	public int loadInt(String key, int deflt) {
		try {
			return Integer.valueOf(LoadPreferences(context, file, key, ""
					+ deflt, mode));
		} catch (Exception e) {
			return deflt;
		}

	}

	public float loadFloat(String key, float deflt) {
		try {
			return Integer.valueOf(LoadPreferences(context, file, key, ""
					+ deflt, mode));
		} catch (Exception e) {
			return deflt;
		}

	}

	public double loadDouble(String key, double deflt) {
		try {
			return Integer.valueOf(LoadPreferences(context, file, key, ""
					+ deflt, mode));
		} catch (Exception e) {
			return deflt;
		}

	}

	public boolean lodBoolean(String key) {
		return loadBoolean(key, false);
	}

	public boolean loadBoolean(String key, boolean deflt) {
		try {
			return Boolean.valueOf(LoadPreferences(context, file, key, ""
					+ deflt, mode));
		} catch (Exception e) {
			return deflt;
		}

	}

	public boolean contain(String key) {
		return contain(context, file, key);
	}

	public Configs setMode(int mode) {
		this.mode = mode;
		return this;
	}

	public int getMode() {
		return mode;
	}

	public Editor getEditor() {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				file, mode);
		return sharedPreferences.edit();
	}

	public static void SavePreferences(Context context, String File,
			String key, String value, int mode) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				File, mode);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();

	}

	public static String LoadPreferences(Context context, String File,
			String key, String deflt, int mode) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				File, mode);
		return sharedPreferences.getString(key, deflt);
	}

	public static boolean contain(Context context, String File, String key) {
		return context.getSharedPreferences(File, 0).contains(key);
	}
}
