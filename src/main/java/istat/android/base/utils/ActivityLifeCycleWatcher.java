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
    HashMap<Activity, Application.ActivityLifecycleCallbacks> activityApplicationLifecycleCallbackMap = new HashMap<>();
    HashMap<Application.ActivityLifecycleCallbacks, ActivityLifeCycleListener> applicationAndWatcherCallbackMap = new HashMap<>();

    public ActivityLifeCycleWatcher() {

    }

    public boolean isWatching(Activity activity) {
        return activityApplicationLifecycleCallbackMap.containsKey(activity);
    }

    public boolean isWatching(Activity activity, Application.ActivityLifecycleCallbacks lifecycleCallbacks) {
        return activityApplicationLifecycleCallbackMap.get(activity) == lifecycleCallbacks;
    }

    public boolean isWatching(Activity activity, ActivityLifeCycleListener lifecycleCallbacks) {
        Application.ActivityLifecycleCallbacks applicationCallback = activityApplicationLifecycleCallbackMap.get(activity);
        return applicationAndWatcherCallbackMap.get(applicationCallback) == lifecycleCallbacks;
    }

    @SuppressLint("NewApi")
    public void startWatching(Activity activity, ActivityLifeCycleListener watcherCallback) {
        Application.ActivityLifecycleCallbacks applicationCallback = createInternalCallback(activity, watcherCallback);
        activityApplicationLifecycleCallbackMap.put(activity, applicationCallback);
        applicationAndWatcherCallbackMap.put(applicationCallback, watcherCallback);
        activity.getApplication().registerActivityLifecycleCallbacks(applicationCallback);
    }

    public void startWatching(Activity activity, Application.ActivityLifecycleCallbacks callbacks) {
        activityApplicationLifecycleCallbackMap.put(activity, callbacks);
        activity.getApplication().registerActivityLifecycleCallbacks(callbacks);
    }

    @SuppressLint("NewApi")
    public void stopWatching(Activity activity) {
        if (activity != null) {
            Application.ActivityLifecycleCallbacks applicationCallback = activityApplicationLifecycleCallbackMap.get(activity);
            if (applicationCallback != null) {
                activity.getApplication().unregisterActivityLifecycleCallbacks(applicationCallback);
                activityApplicationLifecycleCallbackMap.remove(activity);
                applicationAndWatcherCallbackMap.remove(applicationCallback);
            }
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


        default void onActivityCreated(Bundle savedInstanceState) {
        }

        default void onActivityStarted() {
        }

        default void onActivityResumed() {
        }

        default void onActivityPaused() {
        }

        default void onActivityStopped() {
        }

        default void onActivitySaveInstanceState(Bundle outState) {
        }

        default void onActivityDestroyed() {
        }
    }

    public interface DefaultActivityLifeCycleListener extends ActivityLifeCycleListener {
        default void onActivityCreated(Bundle savedInstanceState) {

        }

        default void onActivityStarted() {

        }

        default void onActivityResumed() {

        }

        default void onActivityPaused() {

        }

        default void onActivityStopped() {

        }

        default void onActivitySaveInstanceState(Bundle outState) {

        }

        default void onActivityDestroyed() {

        }
    }
}
