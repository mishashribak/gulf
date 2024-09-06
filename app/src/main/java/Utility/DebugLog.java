package Utility;

import android.util.Log;

/**
 * Created by user on 14/9/17.
 * use these log for entire app so that after debuging we disable it
 */

public class DebugLog {

    public static void log(int case_tag, String Tag, String message) {
        switch (case_tag) {
            case 1:
                Log.v(Tag, message); // Verbose
                break;
            case 2:
                Log.d(Tag, message); // Debug
                break;
            case 3:
                Log.i(Tag, message); // Info
                break;
            case 4:
                Log.w(Tag, message); // Warning
                break;
            case 5:
                Log.e(Tag, message); // Error
                break;
        }

    }
}
