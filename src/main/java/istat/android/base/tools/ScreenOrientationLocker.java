package istat.android.base.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.Surface;

import java.util.HashMap;

public class ScreenOrientationLocker {
    int activityHostOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    boolean orientationSetUp = false;
    Activity context;
    final static HashMap<Activity, ScreenOrientationLocker> cache = new HashMap<>();

    public final static int setUpOrientation(Activity activity) {
        ScreenOrientationLocker locker = new ScreenOrientationLocker(activity);
        cache.put(activity, locker);
        return locker.setUpOrientation();
    }

    public final static boolean restoreOrientation(Activity activity) {
        if (activity == null) {
            return false;
        }
        ScreenOrientationLocker locker = cache.remove(activity);
        if (locker != null) {
            locker.restoreOrientation();
            return true;
        } else {
            return false;
        }
    }

    public ScreenOrientationLocker(Activity activity) {
        this.context = activity;
    }

    public int setUpOrientation() {
        if (context == null) {
            return activityHostOrientation;
        }
        int currentOrientation = context.getRequestedOrientation();
        int orientation = context.getWindowManager().getDefaultDisplay()
                .getRotation();
        if (orientation == Surface.ROTATION_90
                || orientation == Surface.ROTATION_270) {
            istat.android.base.tools.ToolKits.Screen.setLandScape(context);
        } else {
            istat.android.base.tools.ToolKits.Screen.setPortrait(context);
        }
        activityHostOrientation = currentOrientation;
        orientationSetUp = true;
        return currentOrientation;
    }

    @SuppressLint("WrongConstant")
    public void restoreOrientation() {
        if (context == null) {
            return;
        }
        context.setRequestedOrientation(activityHostOrientation);
        activityHostOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        orientationSetUp = false;
    }
}
