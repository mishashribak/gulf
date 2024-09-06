package com.app.khaleeji.Fragments;

import android.content.Context;

import androidx.databinding.DataBindingUtil;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.GulflinkApplication;
import com.app.khaleeji.Activity.SettingActivity;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.Basic_Response;
import com.app.khaleeji.Response.CreateGroupResponse;
import com.app.khaleeji.Response.VisibilityResponse;
import com.app.khaleeji.databinding.FragmentWhomsgBinding;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.Bundle_Identifier;
import Utility.AlertDialog;
import Utility.ApiClass;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingWhoCanMsgFragment extends BaseFragment {

    private Context contextd;
    private FragmentWhomsgBinding mBinding;


    public SettingWhoCanMsgFragment() {
    }

    public static SettingWhoCanMsgFragment newInstance() {
        SettingWhoCanMsgFragment fragment = new SettingWhoCanMsgFragment();
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_whomsg, container, false);

        View rootView = mBinding.getRoot();
        if(Build.VERSION.SDK_INT >= 21)
            rootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);

        initView();
        return rootView;
    }

    private void initView() {

        contextd = mActivity;

        mBinding.imgBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((SettingActivity)mActivity).back();
            }
        });

        mBinding.btCheckEverone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = ((ToggleButton) view).isChecked();
                mBinding.btCheckFriends.setChecked(false);
                mBinding.btCheckEverone.setChecked(true);
                callSetMessageMe();
            }
        });

        mBinding.btCheckFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.btCheckEverone.setChecked(false);
                mBinding.btCheckFriends.setChecked(true);
                callSetMessageMe();
            }
        });

        callGetVisibility();


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void callGetVisibility(){

        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Call<VisibilityResponse> call;
        call = mApiInterface.getVisibility(SavePref.getInstance(mActivity).getUserdetail().getId().intValue());
        call.enqueue(new Callback<VisibilityResponse>() {
            @Override
            public void onResponse(Call<VisibilityResponse> call, Response<VisibilityResponse> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        VisibilityResponse visibilityResponse = response.body();
                        if(visibilityResponse != null){
                            if(visibilityResponse.getStatus().equalsIgnoreCase("true")){
                                if(visibilityResponse.getData().equalsIgnoreCase("everyone")){
                                    mBinding.btCheckEverone.setChecked(true);
                                }else{
                                    mBinding.btCheckFriends.setChecked(true);
                                }
                            }
                        }
                    } else {
                        System.out.println(response.errorBody());
                    }
                }
            }

            @Override
            public void onFailure(Call<VisibilityResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });

    }

    public void callSetMessageMe(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int fromUserId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();
        Map mparams = new HashMap();
        mparams.put("status",mBinding.btCheckEverone.isChecked() ? 1 : 0);
        mparams.put("userid", fromUserId);

        Call<Basic_Response> call;
        call = mApiInterface.setMessageMeApi(mparams);
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Basic_Response basic_response = response.body();
                        if(basic_response != null){
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


}
