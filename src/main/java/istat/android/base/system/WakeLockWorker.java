package istat.android.base.system;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by istat on 17/05/17.
 */

public abstract class WakeLockWorker extends Worker {
    protected PowerManager.WakeLock powerWakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        powerWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getName());
        powerWakeLock.acquire();
    }

    @Override
    public void onDestroy() {
        powerWakeLock.release();
        super.onDestroy();
    }

    public PowerManager.WakeLock getPowerWakeLock() {
        return powerWakeLock;
    }
}
