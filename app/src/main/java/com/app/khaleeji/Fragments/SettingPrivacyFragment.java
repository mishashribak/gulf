package com.app.khaleeji.Fragments;

import androidx.databinding.DataBindingUtil;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.LoginActivity;
import com.app.khaleeji.Activity.SettingActivity;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.Basic_Response;
import com.app.khaleeji.databinding.FragmentPrivacySettingBinding;

import java.util.HashMap;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.SessionData;
import Utility.AlertDialog;
import Utility.ApiClass;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SettingPrivacyFragment extends BaseFragment{

    static String TAG = SettingPrivacyFragment.class.getName();
    private Context mContext;
    private FragmentPrivacySettingBinding mbinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = mActivity;

        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_privacy_setting, container, false);
        View rootView = mbinding.getRoot();

        if(Build.VERSION.SDK_INT >= 21)
            rootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        initView();
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void initView(){
        mbinding.tvWhoCanMsg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Fragment_Process.addFragment(mActivity.getSupportFragmentManager(),new SettingWhoCanMsgFragment(),mActivity, R.id.framelayout_main);
            }
        });

        mbinding.tvWhoCanView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Fragment_Process.addFragment(mActivity.getSupportFragmentManager(),new SettingWhoCanDailiesViewFragment(),mActivity, R.id.framelayout_main);
            }
        });


        mbinding.llPermissions.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mbinding.imgDetailPermissons.setVisibility(View.INVISIBLE);
                mbinding.cardMenuPermissions.setVisibility(View.VISIBLE);
            }
        });

        mbinding.imgBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((SettingActivity)mActivity).back();
            }
        });

        mbinding.llBlockedUsers.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Fragment_Process.addFragment(mActivity.getSupportFragmentManager(),new SettingBlockedUsersFragment(),mActivity, R.id.framelayout_main);
            }
        });

        mbinding.llDeactiveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
                builder.setTitle(getResources().getString(R.string.app_name));
                builder.setMessage(getString(R.string.are_you_sure_want_to_deacitvate_account));
                builder.setPositiveButton(getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callDeactive();

                    }
                });
                builder.setNegativeButton(getString(R.string.txt_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
                builder.show();
            }
        });

        mbinding.llDeleteAllAccountData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
                builder.setTitle(getResources().getString(R.string.app_name));
                builder.setMessage(getString(R.string.are_you_sure_want_to_delete_account));
                builder.setPositiveButton(getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callDelete();

                    }
                });
                builder.setNegativeButton(getString(R.string.txt_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
                builder.show();
            }
        });
    }

    private void callDeactive(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);

        Call<Basic_Response> call;
        call = mApiInterface.deactivateUserAccount(SavePref.getInstance(mActivity).getUserdetail().getId().toString());
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Basic_Response basic_response = response.body();
                        if(basic_response.getStatus().equalsIgnoreCase("true")){
                            clearAppData();
                        }
//                        AlertDialog.showAlert(mActivity, getString(R.string.app_name),basic_response.getMessage(),
//                                getString(R.string.txt_Done), "", false, null, null);

                    } else {
                        System.out.println(response.errorBody());
                    }
                }
            }
            @Override
            public void onFailure(Call<Basic_Response> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    private void callDelete(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);

        Call<Basic_Response> call;
        call = mApiInterface.deleteUserAccount( SavePref.getInstance(mActivity).getUserdetail().getId().toString());
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Basic_Response basic_response = response.body();
                        if(basic_response.getStatus().equalsIgnoreCase("true")){
                            clearAppData();
//                            AlertDialog.showAlert(mActivity, getString(R.string.app_name),basic_response.getMessage(),
//                                    getString(R.string.txt_Done), "", false, null, null);
                        }

                    } else {
                        System.out.println(response.errorBody());
                    }
                }
            }
            @Override
            public void onFailure(Call<Basic_Response> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    private void clearAppData() {
        String device_key = SavePref.getInstance(mActivity).getFirebase_DeviceKey();
        SavePref.getInstance(mActivity).clearPref();
        SavePref.getInstance(mActivity).save_FirebaseDeviceKey(device_key);
        SessionData.I().clearLocalData();
//        gulflinkApplication.roomIdList.clear();
//        gulflinkApplication.chatUserDataList.clear();
        openLoginActivity();
    }

    private void openLoginActivity() {
        Intent intent = new Intent(mActivity, LoginActivity.class);
        SessionData.makeIntentAsClearHistory(intent);
        startActivity(intent);
    }
}
