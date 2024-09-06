package Utility;

import android.content.Context;

/**
 * Created by user on 13/10/17.
 */

public class StatusBar {

    public static int getStatusBarHeight(Context mcontext) {
        int result = 0;
        int resourceId = mcontext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mcontext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
