package Utility;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.app.khaleeji.R;

import retrofit2.http.PUT;

public class CheckConnection {

    public static Context mContext;

    public static boolean isNetworkAvailable(Context mcontext) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mcontext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void isNetworkAvailable() {
        if(mContext == null)
            return;
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if(activeNetworkInfo != null && !activeNetworkInfo.isConnected()){
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Utility.AlertDialog.showAlert(mContext,
                            mContext.getString(R.string.app_name), mContext.getString(R.string.network_down),
                            mContext.getString(R.string.txt_Done),"",
                            false, null, null);
                }
            });
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Network[] networks = connectivityManager.getAllNetworks();
//            NetworkInfo networkInfo;
//            for (Network mNetwork : networks) {
//                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
//                if (!networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Utility.AlertDialog.showAlert(mContext,
//                                    mContext.getString(R.string.app_name), mContext.getString(R.string.network_down),
//                                    mContext.getString(R.string.txt_Done),"",
//                                    false, null, null);
//                        }
//                    });
////                    return true;
//                }
//            }
//        }else {
//            if (connectivityManager != null) {
//                //noinspection deprecation
//                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
//                if (info != null) {
//                    for (NetworkInfo anInfo : info) {
//                        if (anInfo.getState() != NetworkInfo.State.CONNECTED) {
//                            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Utility.AlertDialog.showAlert(mContext,
//                                            mContext.getString(R.string.app_name), mContext.getString(R.string.network_down),
//                                            mContext.getString(R.string.txt_Done),"",
//                                            false, null, null);
//                                }
//                            });
////                            return true;
//                        }
//                    }
//                }
//            }
//        }
    }
}
