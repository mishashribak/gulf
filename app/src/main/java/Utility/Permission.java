package Utility;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class Permission {

    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 123;

    public static boolean checkPermissionCamera(Activity mActivity) {

        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.CAMERA))
                {
                    ActivityCompat.requestPermissions(mActivity,
                            new String[]{
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.RECORD_AUDIO,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
//                                    Manifest.permission.WRITE_SETTINGS
                            },
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }
                else {
                    ActivityCompat.requestPermissions(mActivity, new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
//                            Manifest.permission.WRITE_SETTINGS
                    }, MY_PERMISSIONS_REQUEST_CAMERA);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }


    public static final int ACCESSLOCATION = 124;

    public static boolean checkLocationPermision(Activity mActivity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESSLOCATION);

                } else {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESSLOCATION);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }



}
