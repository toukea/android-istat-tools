package istat.android.base.tools;

/**
 * Created by istat on 26/09/17.
 */

public class TextUtils {
    public static boolean isEmpty(CharSequence s) {
        return s == null || s.length() == 0;
    }

    public static boolean isEmpty(Object o) {
        return o == null || o.toString().length() == 0;
    }

    public static String trim(CharSequence s) {
        if (s == null) {
            return null;
        }
        return s.toString().trim();
    }

    private static int countLines(String str) {
        String[] lines = str.split("\r\n|\r|\n");
        return lines.length;
    }

    //lineariz, linearise
    public static String linearized(String text) {
        return text.replaceAll("\n", " ");
    }

    public static boolean isTrimEmpty(String text) {
        return isEmpty(trim(text));
    }

    public static int length(String text) {
        if (text == null) {
            return 0;
        }
        return text.length();
    }

    public static int trimLength(String text) {
        if (text == null) {
            return 0;
        }
        return text.trim().length();
    }
}
