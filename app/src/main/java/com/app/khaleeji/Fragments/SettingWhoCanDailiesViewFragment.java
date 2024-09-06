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
import com.app.khaleeji.Response.VisibilityResponse;
import com.app.khaleeji.databinding.FragmentWhoviewBinding;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.Bundle_Identifier;
import Utility.ApiClass;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingWhoCanDailiesViewFragment extends BaseFragment{

    Context contextd;
    FragmentWhoviewBinding mBinding;
    String TAG = SettingWhoCanDailiesViewFragment.class.getSimpleName();

    ApiInterface deleteAccountCall, deactivateAccountCall;

    Menu menu;
    MenuItem itemCart, itemSearch;
    private GulflinkApplication gulflinkApplication;

    public SettingWhoCanDailiesViewFragment() {
    }

    public static SettingWhoCanDailiesViewFragment newInstance() {
        SettingWhoCanDailiesViewFragment fragment = new SettingWhoCanDailiesViewFragment();
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

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_whoview, container, false);

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

        mBinding.rlCustomGroup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(),new SettingCustomGroupFragment(),mActivity, R.id.framelayout_main);
            }
        });

        mBinding.btCheckEverone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.btMyFriends.setChecked(false);
                mBinding.btCheckEverone.setChecked(true);
                boolean on = ((ToggleButton) view).isChecked();
                callUpdateVisibility();
            }
        });

        mBinding.btMyFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.btCheckEverone.setChecked(false);
                mBinding.btMyFriends.setChecked(true);
                boolean on = ((ToggleButton) v).isChecked();
                callUpdateVisibility();
            }
        });

        callGetVisibility();
    }


    private void callUpdateVisibility(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Call<VisibilityResponse> call;
        String visibility = mBinding.btCheckEverone.isChecked() ? "everyone" : "myFriends";
        call = mApiInterface.updateVisibility(SavePref.getInstance(mActivity).getUserdetail().getId().intValue(), visibility);
        call.enqueue(new Callback<VisibilityResponse>() {
            @Override
            public void onResponse(Call<VisibilityResponse> call, Response<VisibilityResponse> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        VisibilityResponse visibilityResponse = response.body();
                        if(visibilityResponse != null){
                            if(visibilityResponse.getStatus().equalsIgnoreCase("true")){

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
                                    mBinding.btMyFriends.setChecked(true);
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

}
