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

    private static int countLines(String str) {
        String[] lines = str.split("\r\n|\r|\n");
        return lines.length;
    }

    public static String linearize(String text) {
        return text.replaceAll("\n", " ");
    }
}
