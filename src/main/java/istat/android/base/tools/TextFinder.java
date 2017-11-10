package istat.android.base.tools;

import java.io.InputStream;

/**
 * Created by istat on 16/05/17.
 */

public class TextFinder {
    String pattern;

    public TextFinder(String pattern) {
        this.pattern = pattern;
    }

    public void findOn(InputStream stream, OnFindListener listener) {
        String out = "";
        byte[] b = new byte[8];
        int read = 0;
        try {
            while ((read = stream.read(b)) > -1) {
                out = out + new String(b, 0, read);
                if (out.matches(pattern)) {
                    listener.onMatch(out);
                    out = "";
                }
            }
            stream.close();
        } catch (Exception e) {
        }
    }

    interface OnFindListener {
        void onMatch(String out);
    }
}
