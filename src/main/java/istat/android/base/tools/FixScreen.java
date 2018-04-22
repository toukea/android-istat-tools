package istat.android.base.tools;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.Surface;

/**
 * Created by istat on 20/04/18.
 */

public class FixScreen {
    public static int activityHostOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    public static int setUpOrientation(Activity context) {
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
        return currentOrientation;
    }

    public static void restoreOrientation(Activity context) {
        if (activityHostOrientation != 0) {
            context.setRequestedOrientation(activityHostOrientation);
            activityHostOrientation = 0;
        }
    }
}
