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
    final ConcurrentSkipListMap<String, List<TaskDelayPair>> taskQueue = new ConcurrentSkipListMap<>();
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

    private void executeTask(final Activity activity, final int when) {
        String entryName = getEntryName(activity.getClass(), when);
        List<TaskDelayPair> pairs = taskQueue.get(entryName);
        if (pairs != null && !pairs.isEmpty()) {
            for (final TaskDelayPair pair : pairs) {
                if (pair.activityTask != null) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            pair.activityTask.onRun(activity, when);
                        }
                    };
                    if (pair.delay == 0) {
                        runnable.run();
                    } else {
                        mHandler.postDelayed(runnable, pair.delay);
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

    public static boolean planToRun(Application application, Class<? extends Activity> target, final Runnable runnable, int when) {
        return planToRun(application, target, runnable, when, 0);
    }

    public static boolean planToRun(Application application, Class<? extends Activity> target, final Runnable runnable, int when, long delay) {
        if (runnable == null) {
            return false;
        }
        return planToRun(application, target, new ActivityTak() {
            @Override
            public void onRun(Activity activity, int when) {
                runnable.run();
            }
        }, when, delay);

    }

    public static boolean planToRun(Application application, Class<? extends Activity> target, ActivityTak activityTak, int when) {
        return planToRun(application, target, activityTak, when, 0);
    }

    public static boolean planToRun(Application application, Class<? extends Activity> target, ActivityTak activityTak, int when, long delay) {
        if (activityTak == null) {
            return false;
        }
        if (instance == null) {
            instance = new ActivityLifecycleTaskRunner(application);
        }
        if (when < 0) {
            when = 0;
        }
        return instance.planToRun(target, activityTak, when, delay);
    }

    public static int unPlanAll() {
        if (instance == null) {
            return 0;
        }
        //TODO retourne le mombre de tache qui on été déplanifié.
        instance.release();
        return 0;
    }

    public static int unplanAll(ActivityTak activityTak) {
        if (instance == null) {
            return 0;
        }
        //TODO retourne le mombre de tache qui on été déplanifié.
        return 0;
    }

    public static int unplanAll(Class<? extends Activity> target, ActivityTak activityTak) {
        if (instance == null) {
            return 0;
        }
        //TODO retourne le mombre de tache qui on été déplanifié.
        return 0;
    }

    public static boolean unplan(Class<? extends Activity> target, int when, ActivityTak activityTak) {
        if (instance == null) {
            return false;
        }
        //TODO retourne le si oui ou non on a retrouver cette tache pour la deplanifier.
        return true;
    }


    private boolean planToRun(Class<? extends Activity> target, ActivityTak activityTak, int when, long delay) {
        String entryName = getEntryName(target, when);
        TaskDelayPair pair = new TaskDelayPair(activityTak, delay);
        List<TaskDelayPair> pairs = taskQueue.get(entryName);
        if (pairs == null) {
            pairs = new ArrayList<>();
            taskQueue.put(entryName, pairs);
        }
        pairs.add(pair);
        return false;
    }

    class TaskDelayPair {
        final ActivityTak activityTask;
        final long delay;

        public TaskDelayPair(ActivityTak runnable, long delay) {
            this.activityTask = runnable;
            this.delay = delay;
        }
    }

    public interface ActivityTak {
        void onRun(Activity activity, int when);
    }
}
