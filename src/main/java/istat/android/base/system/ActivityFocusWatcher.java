package istat.android.base.system;

import android.app.Activity;
import android.os.Handler;

public final class ActivityFocusWatcher extends Thread {
	OnFocusChangeListener mCallBack;
	Activity mActivity;
	long watchInterval=-1;
	private boolean run = false;
	private Handler mHandler;

	boolean lastState = false;

	public  interface OnFocusChangeListener {
		 void onFocusChanged(boolean hasFocus);
	}

	public ActivityFocusWatcher(Activity activity) {
		mHandler = new Handler();
		mActivity = activity;
	}

	public void setOnFocusChangeListener(OnFocusChangeListener callBack) {
		mCallBack = callBack;
	}

    public void setWatchInterval(long watchInterval) {
        this.watchInterval = watchInterval;
    }

    public boolean startWatching(OnFocusChangeListener callBack) {
		boolean out = isWatching();
		if (!out) {
			mCallBack = callBack;
			start();
		}
		return out;
	}

	@Override
	public void run() {

		while (run) {
            if(watchInterval>0){
                try {
                    sleep(watchInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
			checkActivityFocusState();
		}
	}

	private void checkActivityFocusState() {

		final boolean newState = mActivity.hasWindowFocus();
		if (newState != lastState && mCallBack != null && run) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {

					if (mCallBack != null)
						mCallBack.onFocusChanged(newState);
				}
			});
		}
		lastState = newState;
	}

	@Override
	public synchronized void start() {

		run = true;
		super.start();
	}

	public void stopWatching() {
		run = false;
	}

	public boolean isWatching() {
		return run;
	}
}
