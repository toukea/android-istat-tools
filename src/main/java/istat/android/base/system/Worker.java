package istat.android.base.system;

import android.app.Service;
import android.os.Handler;

/**
 * Created by istat on 01/05/17.
 */

public abstract class Worker extends Service {
    protected Handler workerHandler = new Handler();
    int timeOut = 0;
    private Runnable workerRunnable = new Runnable() {
        public void run() {
            Worker.this.executeTimeOut(Worker.this.timeOut);
        }
    };

    public Worker() {
    }

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
            } catch (Exception var3) {
                ;
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
        } catch (Exception var2) {
            ;
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
