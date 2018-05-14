package istat.android.base.tools;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.PowerManager;

import android.support.v7.app.NotificationCompat;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by istat on 24/10/17.
 */

public class NoDoze {
    final static ConcurrentHashMap<Service, NoDoze> cache = new ConcurrentHashMap<>();
    Service service;
    Notification notification;
    int notificationId;
    PowerManager powerManager;
    NotificationManager notificationManager;
    BroadcastReceiver idleStateChangeStateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (powerManager.isDeviceIdleMode()) {
                    service.startForeground(notificationId, notification);
                } else {
                    service.stopForeground(false);
                }
            }

        }
    };

    NoDoze(Service service) {
        this.service = service;
        powerManager = (PowerManager) service.getSystemService(Context.POWER_SERVICE);
        notificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    void begin(int id, Notification notification, boolean display) {
        this.notificationId = id;
        IntentFilter intentFilter = new IntentFilter(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED);
        service.registerReceiver(idleStateChangeStateBroadcastReceiver, intentFilter);
        if (display) {

        }
    }

    boolean cancel() {
        try {
            service.unregisterReceiver(idleStateChangeStateBroadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cache.remove(service) != null;
    }


    public final static int DEFAULT_NOTIFICATION_ID = 100;

    public final static boolean begin(Service service, Notification notification) {
        return begin(true, service, notification, DEFAULT_NOTIFICATION_ID);
    }

    public final static boolean begin(Service service, Notification notification, int notificationId) {
        return begin(true, service, notification, notificationId);
    }

    public final static boolean begin(boolean notify, Service service, Notification notification) {
        return begin(notify, service, notification, DEFAULT_NOTIFICATION_ID);
    }

    public final static boolean begin(boolean notify, Service service, Notification notification, int notificationId) {
        if (cache.contains(service)) {
            return false;
        }
        NoDoze noDoze = new NoDoze(service);
        noDoze.begin(notificationId, notification, notify);
        cache.put(service, noDoze);
        return true;
    }

    public final static boolean cancel(Service service) {
        NoDoze noDoze = cache.get(service);
        if (service != null) {
            return noDoze.cancel();
        }
        return false;
    }

    public static final int count() {
        return cache.size();
    }

}
