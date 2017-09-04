package istat.android.base.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.HashMap;

/**
 * Created by istat on 14/05/17.
 */

public class ActivityLifeCycleWatcher {
    HashMap<Activity, Application.ActivityLifecycleCallbacks> cache = new HashMap<>();

    public ActivityLifeCycleWatcher() {

    }

    @SuppressLint("NewApi")
    public void startWatching(Activity activity, ActivityLifeCycleListener callbacks) {
        Application.ActivityLifecycleCallbacks callbacks1 = createInternalCallback(activity, callbacks);
        cache.put(activity, createInternalCallback(activity, callbacks));
        activity.getApplication().registerActivityLifecycleCallbacks(callbacks1);
    }

    @SuppressLint("NewApi")
    public void stopWatching(Activity activity) {
        if (activity != null) {
            Application.ActivityLifecycleCallbacks callbacks = cache.get(activity);
            activity.getApplication().unregisterActivityLifecycleCallbacks(callbacks);
            cache.remove(callbacks);
        }
    }

    private Application.ActivityLifecycleCallbacks createInternalCallback(final Activity watchingActivity, final ActivityLifeCycleListener callbacks) {
        return new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                if (callbacks != null && activity == watchingActivity) {
                    callbacks.onActivityCreated(savedInstanceState);
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (callbacks != null && activity == watchingActivity) {
                    callbacks.onActivityStarted();
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (callbacks != null && activity == watchingActivity) {
                    callbacks.onActivityResumed();
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
                if (callbacks != null && activity == watchingActivity) {
                    callbacks.onActivityPaused();
                }
            }

            @Override
            public void onActivityStopped(Activity activity) {
                if (callbacks != null && activity == watchingActivity) {
                    callbacks.onActivityStopped();
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                if (callbacks != null && activity == watchingActivity) {
                    callbacks.onActivitySaveInstanceState(outState);
                }
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (callbacks != null && activity == watchingActivity) {
                    callbacks.onActivityDestroyed();
                }
                stopWatching(activity);
            }
        };
    }

    public interface ActivityLifeCycleListener {


        void onActivityCreated(Bundle savedInstanceState);

        void onActivityStarted();

        void onActivityResumed();

        void onActivityPaused();

        void onActivityStopped();

        void onActivitySaveInstanceState(Bundle outState);

        void onActivityDestroyed();
    }
}
