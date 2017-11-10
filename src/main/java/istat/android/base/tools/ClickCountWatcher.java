package istat.android.base.tools;

import android.view.View;
import android.view.View.OnClickListener;

public class ClickCountWatcher {
    public final static int DEFAULT_TIME_LATENCY = 1000;

    public static void startWatching(final View view, final int count, final ClickCountListener callBack) {
        startWatching(view, DEFAULT_TIME_LATENCY, count, callBack);
    }

    public static void startWatching(final View view, final int timeLatency, final int count, final ClickCountListener callBack) {
        view.setOnClickListener(new OnClickListener() {
            long lastTime = System.currentTimeMillis();
            int clickCount = 0;

            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis() - lastTime >= timeLatency) {
                    clickCount = 0;
                }
                lastTime = System.currentTimeMillis();
                clickCount++;
                if (clickCount >= count) {
                    if (callBack != null) {
                        callBack.onClickCountReached(view, clickCount);
                    }
                    clickCount = 0;
                }

            }
        });
    }

    public interface ClickCountListener {
        void onClickCountReached(View v, int count);
    }
}
