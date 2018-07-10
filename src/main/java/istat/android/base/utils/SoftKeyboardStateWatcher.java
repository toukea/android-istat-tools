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
                if ((lastVisibleDecorViewHeight > visibleDecorViewHeight) && (lastVisibleDecorViewHeight / visibleDecorViewHeight >= 0.3f)) {
                    // visible
                    if (listener != null) listener.onKeyboardStateChanged(STATE_VISIBLE);
                } else if ((lastVisibleDecorViewHeight < visibleDecorViewHeight) && (visibleDecorViewHeight / lastVisibleDecorViewHeight >= 0.3f)) {
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
        watchView.getViewTreeObserver().addOnGlobalLayoutListener(
                mOnGlobalLayoutListener);
    }

    boolean watching = false;

    public void startWatching(OnKeyboardStateChangedListener listener) {
        this.listener = listener;
        initKeyboardListener();
        this.watching = true;
    }

    public boolean isWatching() {
        return watching;
    }

    public boolean stopWatching() {
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
