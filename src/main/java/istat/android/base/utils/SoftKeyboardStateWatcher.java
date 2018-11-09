package istat.android.base.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

public class SoftKeyboardStateWatcher {
    public static final int STATE_HIDDEN = 0, STATE_VISIBLE = 1;
    private OnKeyboardStateChangedListener listener;
    private View watchView;
    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        private final Rect windowVisibleDisplayFrame = new Rect();
        private int lastVisibleDecorViewHeight;

        @Override
        public void onGlobalLayout() {
            watchView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame);
            final int visibleDecorViewHeight = windowVisibleDisplayFrame.height();

            if (lastVisibleDecorViewHeight != 0) {
                if ((lastVisibleDecorViewHeight > visibleDecorViewHeight) && ((float) (lastVisibleDecorViewHeight / visibleDecorViewHeight) >= 0.3f)) {
                    // visible
                    if (listener != null) listener.onKeyboardStateChanged(STATE_VISIBLE);
                } else if ((lastVisibleDecorViewHeight < visibleDecorViewHeight) && ((float) (visibleDecorViewHeight / lastVisibleDecorViewHeight) >= 0.3f)) {
                    //TODO sur le phone de PASCAL, il semblerai que cet event ne soit pas correctement lancé.
                    // hidden
                    if (listener != null) listener.onKeyboardStateChanged(STATE_HIDDEN);
                }
            }
            lastVisibleDecorViewHeight = visibleDecorViewHeight;
        }
    };

    public SoftKeyboardStateWatcher(Activity activity) {
        this(activity.getWindow().getDecorView());
    }

    public SoftKeyboardStateWatcher(View watchView) {
        this.watchView = watchView;//activity.findViewById(android.R.id.content);
    }

    private void initKeyboardListener() {
        watchView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
    }

    boolean watching = false;

    public synchronized  boolean startWatching(OnKeyboardStateChangedListener listener) {
        if (isWatching() || listener == this.listener) {
            return false;
        }
        this.listener = listener;
        initKeyboardListener();
        this.watching = true;
        return true;
    }

    public boolean isWatching() {
        return watching;
    }

    public synchronized boolean stopWatching() {
        this.listener = null;
        if (this.watching && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            watchView.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
            this.watching = false;
            return true;
        }
        return false;
    }

    public interface OnKeyboardStateChangedListener {
        void onKeyboardStateChanged(int state);
    }
}
