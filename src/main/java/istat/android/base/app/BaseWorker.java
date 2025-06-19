package istat.android.base.app;

import android.app.Service;
import android.os.Handler;

/**
 * Created by istat on 01/05/17.
 */

public abstract class BaseWorker extends Service {
    protected Handler workerHandler = new Handler();
    int timeOut = 0;
    private final Runnable workerRunnable = new Runnable() {
        public void run() {
            BaseWorker.this.executeTimeOut(BaseWorker.this.timeOut);
        }
    };

    protected void updateTimeOut(int time) {
        this.removeTimeOut();
        if (time > 0) {
            this.timeOut = time;
            this.workerHandler.postDelayed(this.workerRunnable, (long) this.timeOut);
        }

    }

    private void executeTimeOut(int timeOut2) {
        if (this.onTimeOut(timeOut2)) {
            try {
                this.stopSelf();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    protected void startTimeOut(int time) {
        if (time > this.getTimeOut()) {
            this.updateTimeOut(time);
        }

    }

    protected void removeTimeOut() {
        try {
            this.workerHandler.removeCallbacks(this.workerRunnable);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected int getTimeOut() {
        return this.timeOut;
    }

    protected boolean onTimeOut(int timeOut) {
        return true;
    }

    public void onDestroy() {
        this.removeTimeOut();
        super.onDestroy();
    }
}
