package com.app.khaleeji.Fragments;

import android.content.Context;
import androidx.databinding.DataBindingUtil;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Activity.SettingActivity;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.Basic_Response;
import com.app.khaleeji.Response.NotficationsSettings;
import com.app.khaleeji.databinding.FragmentNotificationdetailBinding;

import java.util.HashMap;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Utility.AlertDialog;
import Utility.ApiClass;
import Utility.CheckConnection;
import Utility.DebugLog;
import Utility.OnSwipeTouchListener;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SettingNotificationDetailFragment extends BaseFragment {

    Context contextd;
    FragmentNotificationdetailBinding mBinding;
    Call<NotficationsSettings> call;
    int gulflink_notification,g_snap_notification,live_notification,friend_request_notification,chat_notification;
    NotficationsSettings  notificationssettings;
    static String TAG = SettingNotificationDetailFragment.class.getName();

    public SettingNotificationDetailFragment() {
    }

    public static SettingNotificationDetailFragment newInstance() {
        SettingNotificationDetailFragment fragment = new SettingNotificationDetailFragment();
        Bundle b = new Bundle();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notificationdetail, container, false);
        View rootView = mBinding.getRoot();
        if(Build.VERSION.SDK_INT >= 21)
            rootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        contextd = mActivity;

        if (CheckConnection.isNetworkAvailable(mActivity))
        {
            call_GetSetting();
        }
        else
        {
            Toast.makeText(mActivity, getString(R.string.network_down), Toast.LENGTH_SHORT).show();
//            AlertDialog.showAlert(mActivity, getString(R.string.app_name), getString(R.string.network_down), getString(R.string.txt_Done), "", false, null, null);
        }
        initView();
        return rootView;
    }

    private void initView() {
        mBinding.imgBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((SettingActivity)mActivity).back();
            }
        });

        mBinding.ivallowNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = ((ToggleButton) view).isChecked();
                callSetSetting("gulflink_notification", on ? 1: 0);
            }
        });
        mBinding.ivgsnaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = ((ToggleButton) view).isChecked();
                callSetSetting("g_snap_notification", on ? 1: 0);
            }
        });
        mBinding.ivlive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = ((ToggleButton) view).isChecked();
                callSetSetting("live_notification", on ? 1: 0);
            }
        });
        mBinding.ivfriendrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = ((ToggleButton) view).isChecked();
                callSetSetting("friend_request_notification", on ? 1: 0);
            }
        });
        mBinding.ivmessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = ((ToggleButton) view).isChecked();
                callSetSetting("chat_notification", on ? 1: 0);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private void call_GetSetting() {
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userid = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().USERID, userid);
        if(call!=null){
            call.cancel();
            call = null;

        }
        call = mApiInterface.getSettingList(mparams);
        call.enqueue(new Callback<NotficationsSettings>() {
            @Override
            public void onResponse(Call<NotficationsSettings> call, Response<NotficationsSettings> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    notificationssettings  = response.body();
                    if(notificationssettings!=null && isAdded()) {
                        if (notificationssettings.getStatus().equalsIgnoreCase("true")) {
                            if (notificationssettings.getData().getGulflinkNotification() == 1) {
                                mBinding.ivallowNotification.setChecked(true);
                                gulflink_notification = 1;
                            }else{
                                mBinding.ivallowNotification.setChecked(false);
                                gulflink_notification = 0;
                            }

                            if (notificationssettings.getData().getGSnapNotification() == 1) {
                                mBinding.ivgsnaps.setChecked(true);
                                g_snap_notification = 1;

                            } else {
                                mBinding.ivgsnaps.setChecked(false);
                                gulflink_notification =0;
                            }

                            if (notificationssettings.getData().getLiveNotification() == 1) {
                                mBinding.ivlive.setChecked(true);
                                live_notification =1;
                            } else {
                                mBinding.ivlive.setChecked(false);
                                live_notification =0;
                            }


                            if (notificationssettings.getData().getFriendRequestNotification() == 1) {
                                mBinding.ivfriendrequest.setChecked(true);
                                friend_request_notification =1;
                            } else {
                                mBinding.ivfriendrequest.setChecked(false);
                                friend_request_notification =0;
                            }


                            if (notificationssettings.getData().getChatNotification() == 1) {
                                SavePref.getInstance(mActivity).saveSettingsMessage("1");
                                mBinding.ivmessages.setChecked(true);
                                chat_notification =1;
                            } else {

                                SavePref.getInstance(mActivity).saveSettingsMessage("0");
                                mBinding.ivmessages.setChecked(false);
                                chat_notification =0;
                            }
                        } else {
                            Toast.makeText(mActivity, notificationssettings.getMessage(), Toast.LENGTH_SHORT).show();
//                            AlertDialog.showAlert(mActivity, getString(R.string.app_name), notificationssettings.getMessage(), getString(R.string.txt_Done), "", false, null, null);
                        }
                    }

                } else {
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<NotficationsSettings> call, Throwable t) {
                ProgressDialog.hideprogressbar();
                DebugLog.log(1, TAG, "onResponse" + t.getMessage());
            }
        });
    }


    private void callSetSetting(String type,  int Value) {
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int fromUserId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();

        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().USERID, fromUserId);
        mparams.put(ApiClass.getmApiClass().TYPE, type);
        mparams.put(ApiClass.getmApiClass().VALUE, Value);

        Call<Basic_Response> call;
        call = mApiInterface.setSettingList(mparams);
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Basic_Response basic_response = response.body();
                        if( ! basic_response.getStatus().equalsIgnoreCase("true")){
                            if(type.equals("gulflink_notification")){
                                mBinding.ivallowNotification.setChecked(false);
                            }
                            if(type.equals("g_snap_notification")){
                                mBinding.ivgsnaps.setChecked(false);
                            }
                            if(type.equals("live_notification")){
                                mBinding.ivlive.setChecked(false);
                            }
                            if(type.equals("friend_request_notification")){
                                mBinding.ivfriendrequest.setChecked(false);
                            }
                            if(type.equals("chat_notification")){
                                mBinding.ivmessages.setChecked(false);
                                SavePref.getInstance(mActivity).saveSettingsMessage("0");
                            }
                        }
                        Toast.makeText(mActivity, basic_response.getMessage(), Toast.LENGTH_SHORT).show();
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





}
