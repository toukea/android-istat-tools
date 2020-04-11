package istat.android.base.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

public class ActivityLifecycleTaskRunner {
    public static int
            WHEN_ACTIVITY_CREATED = 0,
            WHEN_ACTIVITY_STARTED = 1,
            WHEN_ACTIVITY_RESUMED = 2,
            WHEN_ACTIVITY_PAUSED = 3,
            WHEN_ACTIVITY_STOPPED = 4,
            WHEN_ACTIVITY_SAVE_INSTANCE_STATE = 5,
            WHEN_ACTIVITY_DESTROYED = 6;
    final ConcurrentSkipListMap<String, List<RunnableDelayPair>> taskQueue = new ConcurrentSkipListMap<>();
    Handler mHandler = new Handler(Looper.getMainLooper());

    static ActivityLifecycleTaskRunner instance;
    Application application;

    Application.ActivityLifecycleCallbacks mActivityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            executeTask(activity, WHEN_ACTIVITY_CREATED);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            executeTask(activity, WHEN_ACTIVITY_STARTED);

        }

        @Override
        public void onActivityResumed(Activity activity) {
            executeTask(activity, WHEN_ACTIVITY_RESUMED);
        }

        @Override
        public void onActivityPaused(Activity activity) {
            executeTask(activity, WHEN_ACTIVITY_PAUSED);
        }

        @Override
        public void onActivityStopped(Activity activity) {
            executeTask(activity, WHEN_ACTIVITY_STOPPED);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            executeTask(activity, WHEN_ACTIVITY_SAVE_INSTANCE_STATE);
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            executeTask(activity, WHEN_ACTIVITY_DESTROYED);
        }
    };

    private void executeTask(Activity activity, int when) {
        String entryName = getEntryName(activity.getClass(), when);
        List<RunnableDelayPair> pairs = taskQueue.get(entryName);
        if (pairs != null && !pairs.isEmpty()) {
            for (RunnableDelayPair pair : pairs) {
                if (pair.runnable != null) {
                    if (pair.delay == 0) {
                        pair.runnable.run();
                    } else {
                        mHandler.postDelayed(pair.runnable, pair.delay);
                    }
                }
            }
            taskQueue.remove(entryName);
        }
        if (taskQueue.isEmpty()) {
            release();
        }
    }

    private void release() {
        application.unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
        instance = null;
        application = null;
    }

    private static String getEntryName(Class<? extends Activity> cLass, int when) {
        return cLass.getSimpleName() + when;
    }


    private ActivityLifecycleTaskRunner(Application application) {
        this.application = application;
        application.registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
    }

    public static boolean planToRun(Application application, Class<? extends Activity> target, Runnable runnable, int when) {
        return planToRun(application, target, runnable, when, 0);
    }

    public static boolean planToRun(Application application, Class<? extends Activity> target, Runnable runnable, int when, long delay) {
        if (instance == null) {
            instance = new ActivityLifecycleTaskRunner(application);
        }
        return instance.planToRun(target, runnable, when, delay);
    }

    public static int unPlanAll() {
        if (instance == null) {
            return 0;
        }
        //TODO retourne le mombre de tache qui on été déplanifié.
        instance.release();
        return 0;
    }

    public static int unplanAll(Runnable runnable) {
        if (instance == null) {
            return 0;
        }
        //TODO retourne le mombre de tache qui on été déplanifié.
        return 0;
    }

    public static int unplanAll(Class<? extends Activity> target, Runnable runnable) {
        if (instance == null) {
            return 0;
        }
        //TODO retourne le mombre de tache qui on été déplanifié.
        return 0;
    }

    public static boolean unplan(Class<? extends Activity> target, int when, Runnable runnable) {
        if (instance == null) {
            return false;
        }
        //TODO retourne le si oui ou non on a retrouver cette tache pour la deplanifier.
        return true;
    }


    private boolean planToRun(Class<? extends Activity> target, Runnable runnable, int when, long delay) {
        String entryName = getEntryName(target, when);
        RunnableDelayPair pair = new RunnableDelayPair(runnable, delay);
        List<RunnableDelayPair> pairs = taskQueue.get(entryName);
        if (pairs == null) {
            pairs = new ArrayList<>();
            taskQueue.put(entryName, pairs);
        }
        pairs.add(pair);
        return false;
    }

    class RunnableDelayPair {
        final Runnable runnable;
        final long delay;

        public RunnableDelayPair(Runnable runnable, long delay) {
            this.runnable = runnable;
            this.delay = delay;
        }
    }
}
