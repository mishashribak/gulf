package com.app.khaleeji.BroadCast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Utility.ApiClass;
import Utility.CheckConnection;
import Utility.DebugLog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 17/11/17.
 */

public class AnalayticsBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        callAnalytics(context);
    }


    private void callAnalytics(Context context) {

        if (CheckConnection.isNetworkAvailable(context)) {

        if(SavePref.getInstance(context).getUserdetail()!=null && SavePref.getInstance(context).getUserdetail().getId()!=null)
        {
            ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
            Map mparams = ApiClass.getmApiClass().buildDefaultParams(context);

            mparams.put("userid", SavePref.getInstance(context).getUserdetail().getId().toString());

            if(SavePref.getInstance(context).getLatitude()!=1)
            mparams.put("lat", SavePref.getInstance(context).getLatitude());

            if(SavePref.getInstance(context).getLongitude()!=1)
            mparams.put("lng", SavePref.getInstance(context).getLongitude());

            mparams.put(ApiClass.DEVICE_TYPE, "ANDROID");
            Call<String> call = mApiInterface.activeStatusLog(mparams);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        DebugLog.log(1, "AnalayticsBroadcast", "onResponse" + response);
                    } else {
                        DebugLog.log(1, "AnalayticsBroadcast", "onResponse" + response.errorBody());
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    //DebugLog.log(1, "AnalayticsBroadcast", "onResponse" + t.getMessage());
                }
            });
        }
      }
    }
  }
