package istat.android.base.memories;

import istat.android.base.tools.JSON;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class SESSION {
    static HashMap<String, Object> SESSION_DATA = new HashMap<String, Object>();
    static HashMap<String, Object> SESSION_TEMPORY_DATA = new HashMap<String, Object>();
    static HashMap<String, Object> SESSION_SPACE = new HashMap<String, Object>() {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        {
            put(Session.DEFAULT_NAME_SPACE, SESSION_DATA);
            put(Session.DEFAULT_TEMPORY, SESSION_TEMPORY_DATA);
        }
    };

    static String KEYSET_NAME = "key_set";
    static Session session;
    public static String CURRENT_NAME_SPACE = Session.DEFAULT_NAME_SPACE;
    private static String LAST_NAME_SPACE = Session.DEFAULT_NAME_SPACE;

    // public static Class<T extends Integer> get(String name,Class<T> cls) {
    // Object tmp = SESSION_DATA.get(name);
    // return Object;
    // }
    public static Object get(String name) {
        Object tmp = SESSION_DATA.get(name);
        return tmp;
    }

    public static Session getCurrentSession() {
        return session;
    }

    public static void destroyTag(String tagName) {
        clearValue(tagName);
        session.clear(tagName);

    }

    public static void destroy() {
        session.clear();
        session.finish();
    }

    public static void destroyNameSpace(Context context, String nameSpace) {
        open(context, nameSpace);
        session.clear();
        cancel(nameSpace);
    }

    public static boolean open(Context context) {
        return open(context, Session.DEFAULT_NAME_SPACE);
    }

    public static boolean open(Context context, String nameSpace) {
        if (!isOpen(nameSpace)) {
            if (session == null)
                return start(context, nameSpace);
            else {
                if (!SESSION_SPACE.containsKey(nameSpace))
                    start(context, nameSpace);
                else {
                    LAST_NAME_SPACE = CURRENT_NAME_SPACE;
                    CURRENT_NAME_SPACE = nameSpace;
                    SESSION_DATA = getSpace(nameSpace);
                    session.registerNameSpace(nameSpace);
                }
            }
        }
        return false;
    }

    // public static boolean open(Context context, String nameSpace) {
    // if (!isOpen(nameSpace))
    // return start(context, nameSpace, Session.DEFAULT_MODE);
    // return false;
    // }

    public static void close() {
        if (isOpen())
            saveCurrentInstance();
        cancel();
    }

    public static void closeNameSpace(String NameSpace) {
        if (isOpen())
            saveNameSpace(NameSpace);
        cancel();
    }

    public static void clearValue(String name) {
        SESSION_DATA.remove(name);
        SESSION_TEMPORY_DATA.remove(name);
    }

    public static void clearAllValues() {
        SESSION_DATA.clear();
        SESSION_TEMPORY_DATA.clear();
    }

    public static boolean contain(Object name) {
        return SESSION_DATA.containsKey(name);
    }

    @SuppressWarnings("unchecked")
    private static HashMap<String, Object> getSpace(String nameSpace) {
        return (HashMap<String, Object>) SESSION_SPACE.get(nameSpace);
    }

    public static boolean contain(Object name, String nameSpace) {
        HashMap<String, Object> space = getSpace(nameSpace);
        if (space != null)
            return space.containsKey(name);
        return false;

    }

    public static boolean canRetrieve(Object name) {
        return SESSION_DATA.containsKey(name)
                || SESSION_TEMPORY_DATA.containsKey(name);
    }

    public static boolean saveCurrentInstance() {
        if (session == null)
            return false;
        String nameSpace;
        if (!SESSION_SPACE.keySet().isEmpty()) {
            Iterator<String> sessionIterator = SESSION_SPACE.keySet()
                    .iterator();
            for (int i = 0; i < SESSION_SPACE.size(); i++) {
                if ((nameSpace = sessionIterator.next()) != null
                        && !nameSpace.equals(Session.DEFAULT_TEMPORY)) {
                    open(session.mContext, nameSpace);
                    session.saveState();
                }
            }
        } else
            return false;
        return true;
    }

    public static boolean saveCurrentNameSpace() {
        if (session == null)
            return false;
        session.saveState();
        return true;
    }

    public static void saveCurrentNameSpace(Context context) {
        if (session == null)
            start(context);
        session.saveState();
    }

    public static boolean saveNameSpace(String nameSpace) {
        session.registerNameSpace(nameSpace);
        if (session == null)
            return false;
        session.saveState();
        return true;
    }

    @SuppressWarnings("unchecked")
    public static void copyTo(String nameSpace) {
        if (!SESSION_SPACE.containsKey(nameSpace)) {
            SESSION_SPACE.put(nameSpace, SESSION_DATA.clone());
        } else {
            HashMap<String, Object> copy = (HashMap<String, Object>) SESSION_SPACE
                    .get(nameSpace);
            if (!SESSION_DATA.keySet().isEmpty()) {
                String[] table = new String[SESSION_DATA.size()];
                table = SESSION_DATA.keySet().toArray(table);
                for (String tmp : table) {
                    if (tmp != null) {
                        Object value = SESSION_DATA.get(tmp);
                        copy.put(tmp, value);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void copyTo(String nameSpace, String tagName) {
        if (!SESSION_SPACE.containsKey(nameSpace)) {
            SESSION_SPACE.put(nameSpace, new HashMap<String, Object>());
        }
        HashMap<String, Object> copy = (HashMap<String, Object>) SESSION_SPACE
                .get(nameSpace);
        if (!SESSION_DATA.keySet().isEmpty()) {
            String[] table = new String[SESSION_DATA.size()];
            table = SESSION_DATA.keySet().toArray(table);
            Object tmp = SESSION_DATA.get(tagName);
            copy.put(tagName, tmp);
        }
    }

    @SuppressWarnings("unchecked")
    public static void copyFrom(String nameSpace, String tagName) {
        if (SESSION_SPACE.containsKey(nameSpace)) {
            HashMap<String, Object> from = (HashMap<String, Object>) SESSION_SPACE
                    .get(nameSpace);
            if (from.containsKey(tagName))
                put(tagName, from.get(tagName));
        }
    }

    @SuppressWarnings("unchecked")
    public static void cloneTo(String nameSpace) {
        if (!SESSION_SPACE.containsKey(nameSpace)) {
            SESSION_SPACE.put(nameSpace, SESSION_DATA.clone());
        } else {
            SESSION_SPACE.put(nameSpace, new HashMap<String, Object>());
            HashMap<String, Object> clone = (HashMap<String, Object>) SESSION_DATA
                    .clone();
            HashMap<String, Object> copy = (HashMap<String, Object>) SESSION_SPACE
                    .get(nameSpace);
            if (!SESSION_DATA.keySet().isEmpty()) {
                String[] table = new String[SESSION_DATA.size()];
                table = SESSION_DATA.keySet().toArray(table);
                for (String tmp : table) {
                    if (tmp != null) {
                        Object value = clone.get(tmp);
                        copy.put(tmp, value);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void cloneTo(String nameSpace, String tagName) {
        if (!SESSION_SPACE.containsKey(nameSpace)) {
            SESSION_SPACE.put(nameSpace, SESSION_DATA.clone());
        } else {
            SESSION_SPACE.put(nameSpace, new HashMap<String, Object>());
            HashMap<String, Object> clone = (HashMap<String, Object>) SESSION_DATA
                    .clone();
            HashMap<String, Object> copy = (HashMap<String, Object>) SESSION_SPACE
                    .get(nameSpace);
            if (!SESSION_DATA.keySet().isEmpty()) {
                String[] table = new String[SESSION_DATA.size()];
                table = SESSION_DATA.keySet().toArray(table);
                Object tmp = clone.get(tagName);
                copy.put(tagName, tmp);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void cloneFrom(String nameSpace, String tagName) {
        if (SESSION_SPACE.containsKey(nameSpace)) {
            HashMap<String, Object> from = (HashMap<String, Object>) SESSION_SPACE
                    .get(nameSpace);
            HashMap<String, Object> clone = (HashMap<String, Object>) from
                    .clone();
            if (from.containsKey(tagName))
                put(tagName, clone.get(tagName));
        }
    }

    public static boolean isEmpty() {
        return SESSION_TEMPORY_DATA.size() > 3;
    }

    public static void put(String name, Object value) {
        put(name, value, true);
    }

    @SuppressWarnings("unchecked")
    public static void push(String nameSpace, String name, Object value) {
        if (!SESSION_SPACE.containsKey(nameSpace)) {
            SESSION_SPACE.put(nameSpace, SESSION_DATA.clone());
        }
        HashMap<String, Object> session = (HashMap<String, Object>) SESSION_SPACE
                .get(nameSpace);
        session.put(name, value);

    }

    public static void put(String name, Object value, boolean percistance) {
        if (percistance)
            SESSION_DATA.put(name, value);
        SESSION_TEMPORY_DATA.put(name, value);
    }

    public static int getInt(String name) {
        String value = getString(name);
        if (TextUtils.isEmpty(value))
            return 0;
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getString(String name) {
        Object tmp = get(name);
        return tmp == null ? ""/* null */ : tmp.toString();
    }

    public static boolean getBoolean(String name) {
        return Boolean.valueOf(getString(name));
    }

    public static double getDouble(String name) {
        String value = getString(name);
        if (TextUtils.isEmpty(value))
            return 0;
        try {
            return Double.valueOf(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public static Float getFloat(String name) {
        String value = getString(name);
        if (TextUtils.isEmpty(value))
            return 0f;
        try {
            return Float.valueOf(value);
        } catch (Exception e) {
            return 0.0f;
        }
    }

    @SuppressWarnings("unchecked")
    public static Object search(String tagName, String namespace) {
        if (!SESSION_SPACE.containsKey(namespace))
            return null;
        HashMap<String, Object> tmp = (HashMap<String, Object>) SESSION_SPACE
                .get(namespace);
        Object out = tmp.get(tagName);
        if (out != null)
            return out;
        else
            return SESSION_TEMPORY_DATA.get(tagName);
    }

    public static int searchInt(String name, String namespace) {
        String value = searchString(name, namespace);
        if (TextUtils.isEmpty(value))
            return 0;
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public static String searchString(String name, String namespace) {
        Object tmp = search(name, namespace);
        return tmp == null ? ""/* null */ : tmp.toString();
    }

    public static boolean searchBoolean(String name, String namespace) {
        return Boolean.valueOf(searchString(name, namespace));
    }

    public static double searchDouble(String name, String namespace) {
        String value = searchString(name, namespace);
        if (TextUtils.isEmpty(value))
            return 0;
        try {
            return Double.valueOf(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public static Float searchFloat(String name, String namespace) {
        String value = searchString(name, namespace);
        if (TextUtils.isEmpty(value))
            return 0f;
        try {
            return Float.valueOf(value);
        } catch (Exception e) {
            return 0.0f;
        }
    }

    public static Object retrieve(String tagName) {
        Object out = SESSION_DATA.get(tagName);
        if (out != null)
            return out;
        else
            return SESSION_TEMPORY_DATA.get(tagName);
    }

    public static int retrieveInt(String name) {
        String value = retrieveString(name);
        if (TextUtils.isEmpty(value))
            return 0;
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public static String retrieveString(String name) {
        Object tmp = retrieve(name);
        return tmp == null ? ""/* null */ : tmp.toString();
    }

    public static boolean retrieveBoolean(String name) {
        return Boolean.valueOf(retrieveString(name));
    }

    public static double retrieveDouble(String name) {
        String value = retrieveString(name);
        if (TextUtils.isEmpty(value))
            return 0;
        try {
            return Double.valueOf(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public static Float retrieveFloat(String name) {
        String value = retrieveString(name);
        if (TextUtils.isEmpty(value))
            return 0f;
        try {
            return Float.valueOf(value);
        } catch (Exception e) {
            return 0.0f;
        }
    }

    public static void restoreLastSavedInstance() {
        session.rebootSession();
    }

    public static void registerForNameSpace(String nameSpace, int mode) {
        if (CURRENT_NAME_SPACE.equals(nameSpace))
            return;
        if (session == null)
            throw new UnsupportedOperationException("session is not open");
        LAST_NAME_SPACE = CURRENT_NAME_SPACE;
        CURRENT_NAME_SPACE = nameSpace;
        if (!SESSION_SPACE.containsKey(nameSpace))
            start(session.mContext, nameSpace, mode);
        else
            SESSION_DATA = getSpace(nameSpace);
        session.registerNameSpace(nameSpace);

    }

    public static void registerForNameSpace(String nameSpace) {
        registerForNameSpace(nameSpace, Session.DEFAULT_MODE);
    }

    public static boolean cancel() {

        if (session == null)
            return false;
        session.finish();
        return true;
    }

    public static boolean cancel(String nameSpace) {
        if (session == null || !SESSION_SPACE.containsKey(nameSpace))
            return false;
        open(session.mContext, nameSpace);
        clearAllValues();
        SESSION_SPACE.remove(nameSpace);
        CURRENT_NAME_SPACE = getLastOpenedNameSpace();
        return true;
    }

    private static String getLastOpenedNameSpace() {
        return LAST_NAME_SPACE;
    }

    public static boolean isOpen() {
        return session != null;
    }

    public static boolean isOpen(String nameSpace) {
        return SESSION_SPACE.containsKey(nameSpace)
                && CURRENT_NAME_SPACE.equals(nameSpace) && isOpen();
    }

    private static boolean start(Context context) {
        return start(context, Session.DEFAULT_NAME_SPACE, Session.DEFAULT_MODE);
    }

    private static boolean start(Context context, String nameSpace) {
        return start(context, nameSpace, Session.DEFAULT_MODE);
    }

    @SuppressWarnings("unchecked")
    private static boolean start(Context context, String nameSpace, int mode) {
        LAST_NAME_SPACE = CURRENT_NAME_SPACE;
        CURRENT_NAME_SPACE = nameSpace;
        if (!SESSION_SPACE.containsKey(nameSpace)) {
            SESSION_SPACE.put(nameSpace, new HashMap<String, Object>());
        }
        SESSION_DATA = (HashMap<String, Object>) SESSION_SPACE.get(nameSpace);
        session = new Session(context, nameSpace, mode) {

            @Override
            protected void onStartSession(SharedPreferences sharedP) {

                String[] keySet = sharedP.getString(KEYSET_NAME, "").split(",");
                if (keySet.length > 0) {
                    for (String tmp : keySet) {
                        String value = sharedP.getString(tmp, "");
                        if (!TextUtils.isEmpty(value)) {
                            SESSION_DATA.put(tmp, value);
                        }
                    }
                }

            }

            @Override
            protected void onSaveSession(SharedPreferences sharedP) {

                String firstS = getFirstStartTime(), lastS = getLastStartTime(), lastSv = getLastSaveTime();
                Editor editor = sharedP.edit();
                editor.clear();

                String keySet = "";
                // -----------------------------------------------------------------

                if (!SESSION_DATA.keySet().isEmpty()) {
                    String[] table = new String[SESSION_DATA.size()];
                    table = SESSION_DATA.keySet().toArray(table);
                    for (String tmp : table) {
                        if (tmp != null) {
                            keySet += tmp;
                            editor.putString(tmp, SESSION_DATA.get(tmp)
                                    .toString());

                        }
                        if (SESSION_DATA.keySet().iterator().hasNext())
                            keySet += ",";
                    }
                }
                // -----------------------------------------------------------------
                editor.putString(KEYSET_NAME, keySet);
                editor.putString(TIME_FIRST_START, firstS);
                editor.putString(TIME_LAST_START, lastS);
                editor.putString(TIME_LAST_SAVE, lastSv);
                editor.commit();
            }

            @Override
            protected void onFinishSession(SharedPreferences sharedP) {

                SESSION_DATA.clear();
                SESSION_TEMPORY_DATA.clear();
                SESSION_SPACE.clear();
                SESSION_SPACE.put(Session.DEFAULT_NAME_SPACE, SESSION_DATA);
                session = null;
            }
        };
        return session.start();
    }

    public static abstract class Session {
        public static final String DEFAULT_NAME_SPACE = "istat.session_DEFAULT_SESSION",
                DEFAULT_TEMPORY = "istat.session_DEFAULT_TEMPORY",
                TIME_FIRST_START = "istat.Session_first_start",
                TIME_LAST_START = "istat.session_last_start",
                TIME_LAST_SAVE = "istat.session_last_save";
        static final int DEFAULT_MODE = 0;
        SharedPreferences sharedP;
        String startTime, saveTime;
        protected Context mContext;
        private boolean isStart = false;

        private Session(Context context) {
            initSesion(context, DEFAULT_NAME_SPACE, DEFAULT_MODE);
        }

        private Session(Context context, String nameSpace, int mode) {
            initSesion(context, nameSpace, mode);
        }

        private void initSesion(Context context, String nameSpace, int mode) {
            sharedP = context.getSharedPreferences(nameSpace, mode);
            mContext = context;
        }

        public boolean start() {
            Editor edit = sharedP.edit();
            boolean firstStart = isFirstStart();
            if (firstStart) {
                edit.putString(TIME_FIRST_START, startTime);
            } else {
                edit.putString(TIME_LAST_START, startTime);
            }
            edit.commit();
            onStartSession(sharedP);
            isStart = true;
            return firstStart;
        }

        @SuppressLint("SimpleDateFormat")
        public boolean isFirstStart() {
            String dateNow = new SimpleDateFormat("yyyy/MM/dd HH:mm")
                    .format(new Date());
            startTime = sharedP.getString(TIME_FIRST_START, dateNow);
            return startTime.equals(dateNow);
        }

        @SuppressLint("SimpleDateFormat")
        public boolean saveState() {
            String dateNow = new SimpleDateFormat("yyyy/MM/dd HH:mm")
                    .format(new Date());
            saveTime = sharedP.getString(TIME_LAST_SAVE, dateNow);
            Editor edit = sharedP.edit();
            edit.putString(TIME_LAST_SAVE, saveTime);
            edit.commit();
            onSaveSession(sharedP);
            return saveTime.equals(dateNow);
        }

        public String getFirstStartTime() {
            return sharedP.getString(TIME_FIRST_START, null);
        }

        public String getLastStartTime() {
            return startTime;
        }

        public String getLastSaveTime() {
            return saveTime;
        }

        /* sharedP.getString(TIME_FIRST_START, dateNow); */
        @SuppressWarnings("deprecation")
        public Date getFirstStartTimeAsDate() {
            String tmp = sharedP.getString(TIME_FIRST_START, null);
            return tmp != null ? new Date(tmp) : new Date();
        }

        @SuppressWarnings("deprecation")
        public Date getLastSaveTimeAsDate() {
            return saveTime != null ? new Date(saveTime) : new Date();
        }

        @SuppressWarnings("deprecation")
        public Date getLastStartTimeAsDate() {
            return startTime != null ? new Date(startTime) : new Date();
        }

        public void registerNameSpace(String nameSpace, int mode) {
            initSesion(mContext, nameSpace, mode);
        }

        public void registerNameSpace(String nameSpace) {
            registerNameSpace(nameSpace, DEFAULT_MODE);
        }

        public void rebootSession() {
            onStartSession(sharedP);
        }

        public boolean isStarted() {
            return isStart;
        }

        public boolean finish() {
            onFinishSession(sharedP);
            isStart = false;
            return true;
        }

        public void clear() {
            sharedP.edit().clear().commit();
        }

        public void clear(String tagName) {
            sharedP.edit().remove(tagName).commit();
        }

        protected abstract void onSaveSession(SharedPreferences sharedP);

        protected abstract void onStartSession(SharedPreferences sharedP);

        protected abstract void onFinishSession(SharedPreferences sharedP);

    }

    public static class Converter {
        public static Bundle converToBundle() {
            return convertToBundle(new Bundle());
        }

        public static Bundle convertToBundle(Bundle bundle) {
            String keySet = "";
            if (!SESSION_DATA.keySet().isEmpty()) {
                String[] table = new String[SESSION_DATA.size()];
                table = SESSION_DATA.keySet().toArray(table);
                for (String tmp : table) {
                    if (tmp != null) {
                        keySet += tmp;
                        bundle.putString(tmp, SESSION_DATA.get(tmp).toString());
                    }
                    if (SESSION_DATA.keySet().iterator().hasNext())
                        keySet += ",";
                }
            }
            return bundle;
        }

        public static JSONObject convertToJSONObject() throws JSONException {
            String keySet = "";
            JSONObject json = new JSONObject();
            if (!SESSION_DATA.keySet().isEmpty()) {
                String[] table = new String[SESSION_DATA.size()];
                table = SESSION_DATA.keySet().toArray(table);
                for (String tmp : table) {
                    if (tmp != null) {
                        keySet += tmp;
                        json.accumulate(tmp, SESSION.getString(tmp));
                    }
                    if (SESSION_DATA.keySet().iterator().hasNext())
                        keySet += ",";
                }
            }
            return json;
        }

        public static ContentValues convertToContentValues()
                throws JSONException {
            ContentValues paire = new ContentValues();
            if (!SESSION_DATA.keySet().isEmpty()) {
                String[] table = new String[SESSION_DATA.size()];
                table = SESSION_DATA.keySet().toArray(table);
                for (String tmp : table) {
                    if (tmp != null) {
                        paire.put(tmp, SESSION.getString(tmp));

                    }
                }
            }

            return paire;
        }

        public static void createFromJSONObject(JSONObject json)
                throws JSONException {
            List<String> keySet = JSON.JSONArrayToStringList(json.names());
            if (keySet.size() > 0) {
                for (String tmp : keySet) {
                    String value = json.optString(tmp);
                    if (!TextUtils.isEmpty(value)) {
                        SESSION_DATA.put(tmp, value);
                    }
                }
            }
        }

        public static void createFromBundle(Bundle bundle) {
            Iterator<String> keySet = bundle.keySet().iterator();
            while (keySet.hasNext()) {
                String tmp = keySet.next();
                String value = bundle.getString(tmp);
                if (!TextUtils.isEmpty(value)) {
                    SESSION_DATA.put(tmp, value);
                    Log.d("session_debug", tmp + "::" + value);
                }
            }
        }
    }

}
