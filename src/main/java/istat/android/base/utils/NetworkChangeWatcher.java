package istat.android.base.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkChangeWatcher extends BroadcastReceiver {
    static NetworkChangeWatcher instance;
    final ConcurrentHashMap<Context, List<OnNetworkChangeListener>> contextListenerMap = new ConcurrentHashMap<>();
    boolean lastKnowConnectionState = false;
    boolean isWatching = false;

    private NetworkChangeWatcher() {

    }

    private static NetworkChangeWatcher getInstance() {
        if (instance == null) {
            instance = new NetworkChangeWatcher();
        }
        return instance;
    }

    public static int unregisterListener(OnNetworkChangeListener listener) {
        int removedCount = 0;
        Collection<List<OnNetworkChangeListener>> lists = getInstance().contextListenerMap.values();
        for (List<OnNetworkChangeListener> list : lists) {
            if (list.remove(listener)) {
                removedCount++;
            }
        }
        return removedCount;
    }

    public static boolean unregisterListeners(Context context) {
        NetworkChangeWatcher instance = getInstance();
        List<OnNetworkChangeListener> listeners = instance.contextListenerMap.remove(context);
        instance.unregisterBroadcastReceiver(context);
        return listeners != null && !listeners.isEmpty();
    }


    public static boolean registerListener(Activity activity, OnNetworkChangeListener listener, boolean stopOnActivityDestroyed) {
        boolean output = _registerListener(activity, listener);
        if (output && stopOnActivityDestroyed) {
            activity.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

                }

                @Override
                public void onActivityStarted(@NonNull Activity activity) {

                }

                @Override
                public void onActivityResumed(@NonNull Activity activity) {

                }

                @Override
                public void onActivityPaused(@NonNull Activity activity) {

                }

                @Override
                public void onActivityStopped(@NonNull Activity activity) {

                }

                @Override
                public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(@NonNull Activity activity) {
                    unregisterListeners(activity);
                    activity.getApplication().unregisterActivityLifecycleCallbacks(this);
                }
            });
        }
        return output;
    }

    public static boolean registerListener(Context context, OnNetworkChangeListener listener) {
        if (context instanceof Activity) {
            return registerListener((Activity) context, listener, true);
        }
        return _registerListener(context, listener);
    }

    private static boolean _registerListener(Context context, OnNetworkChangeListener listener) {
        NetworkChangeWatcher instance = getInstance();
        List<OnNetworkChangeListener> listeners = instance.contextListenerMap.get(context);
        if (listeners != null) {
            if (listeners.contains(listener)) {
                return false;
            }
        } else {
            listeners = new ArrayList<>();
            instance.contextListenerMap.put(context, listeners);
            instance.registerBroadcastReceiver(context);
        }
        listeners.add(listener);
        return true;
    }

    private void registerBroadcastReceiver(Context context) {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(this, filter);
    }

    private void unregisterBroadcastReceiver(Context context) {
        context.unregisterReceiver(this);
    }


    @Override
    @SuppressLint("MissingPermission")
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras() != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            List<OnNetworkChangeListener> listeners = contextListenerMap.get(context);
            boolean isConnected = networkInfo != null && networkInfo.isConnected();
            if (listeners != null && !listeners.isEmpty()) {
                for (OnNetworkChangeListener listener : listeners) {
                    listener.onNetworkInfoChanged(networkInfo);
                    if (lastKnowConnectionState != isConnected) {
                        listener.onNetworkStateChanged(isConnected);
                    }
                }
            }
            lastKnowConnectionState = isConnected;
        }
    }

    public interface OnNetworkChangeListener {
        default void onNetworkInfoChanged(NetworkInfo networkInfo) {
            //Nothing to do
        }

        void onNetworkStateChanged(boolean connected);
    }
}
