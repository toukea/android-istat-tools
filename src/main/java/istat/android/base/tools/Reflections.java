package istat.android.base.tools;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import dalvik.system.DexFile;

/**
 * Created by istat on 31/10/16.
 */

public class Reflections {
    public static final Class<?> getGenericTypeClass(Class<?> baseClass, int genericIndex) {
        try {
            String className = ((ParameterizedType) baseClass
                    .getGenericSuperclass()).getActualTypeArguments()[genericIndex]
                    .toString().replaceFirst("class", "").trim();
            Class<?> clazz = Class.forName(className);
            return clazz;
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Class is not parametrized with generic type!!! Please use extends <> ");
        }
    }

    public static List<Field> getAllFieldFields(Class<?> cLass, boolean includeSuper, boolean acceptStatic) {
        return getAllFieldFields(cLass, true, includeSuper, acceptStatic);
    }

    public static List<Field> getAllFieldFields(Class<?> cLass, boolean includingPrivate, boolean includingSuper, boolean acceptStatic) {
        if (includingSuper) {
            return getAllFieldIncludingPrivateAndSuper(cLass, !includingPrivate, acceptStatic);
        } else {
            List<Field> fields = new ArrayList<Field>();
            Field[] tmp = cLass.getDeclaredFields();
            for (Field f : tmp) {
                if (f != null && (f.toString().contains("static") && !acceptStatic)) {
                    continue;
                }
                if (!includingPrivate || f.isAccessible()) {
                    fields.add(f);
                }
            }
            return fields;
        }
    }

    public static List<Field> getAllFieldIncludingPrivateAndSuper(Class<?> cLass) {
        return getAllFieldIncludingPrivateAndSuper(cLass, true, false);
    }

    public static List<Field> getAllFieldIncludingPrivateAndSuper(Class<?> cLass, boolean accessibleOnly) {
        return getAllFieldIncludingPrivateAndSuper(cLass, accessibleOnly, false);
    }

    public static List<Field> getAllFieldIncludingPrivateAndSuper(Class<?> cLass, boolean accessibleOnly, boolean acceptStatic) {
        List<Field> fields = new ArrayList<Field>();
        while (/*!cLass.equals(Object.class) ||*/!cLass.getCanonicalName().startsWith("java")) {
            for (Field field : cLass.getDeclaredFields()) {
                if (field != null && (field.toString().contains("static") && !acceptStatic)) {
                    continue;
                }
                if (!accessibleOnly || field.isAccessible()) {
                    fields.add(field);
                }
            }
            cLass = cLass.getSuperclass();
        }
        return fields;
    }

    public static <T> T newInstance(Class<T> cLass) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        try {
            return cLass.newInstance();
        } catch (Exception e) {
            Constructor<T> constructor = cLass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();

        }
    }

    public final static String[] fetchPackageClass(Context context, String packageName) {
        ArrayList<String> classes = new ArrayList<String>();
        try {
            String packageCodePath = context.getPackageCodePath();
            DexFile df = new DexFile(packageCodePath);
            for (Enumeration<String> iterator = df.entries(); iterator.hasMoreElements(); ) {
                String className = iterator.nextElement();
                if (className.contains(packageName)) {
                    classes.add(className);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes.toArray(new String[classes.size()]);
    }

    public final static Class<?> getFieldTypeClass(Field field) {
        return (Class<?>) getFieldType(field);
    }

    public final static Type getFieldType(Field field) {
        Type type;
        try {
            type = field.getGenericType();
            Log.d("asInstance", "onTRY=" + type);
        } catch (Exception e) {
            type = field.getType();
            Log.d("asInstance", "onCatch=" + type);
        }
        return type;
    }

    public static HashMap<String, Object> toMap(Object object) throws IllegalAccessException {
        return toMap(null, object);
    }

    public static HashMap<String, Object> toMap(String prefix, Object object) throws IllegalAccessException {
        if (prefix == null) {
            prefix = "";
        }
        HashMap<String, Object> map = new HashMap<>();
        List<Field> fields = getAllFieldFields(object.getClass(), true, false);
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            field.setAccessible(true);
            map.put(prefix + field.getName(), field.get(object));
        }
        return map;
    }

    public static <T> boolean equals(T object, T object2, String... fieldNames) throws NoSuchFieldException, IllegalAccessException {
        if (object == object2) {
            return true;
        } else if (object == null || object2 == null) {
            return false;
        }
        Field field;
        Class cLass = object.getClass();
        for (String fieldName : fieldNames) {
            field = cLass.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(object);
            Object value2 = field.get(object2);
            if (value != value2 && !ToolKits.Word.parseString(value).equals(ToolKits.Word.parseString(value2))) {
                return false;
            }
        }
        return true;
    }
}
