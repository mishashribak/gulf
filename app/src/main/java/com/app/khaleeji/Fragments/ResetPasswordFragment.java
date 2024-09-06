package com.app.khaleeji.Fragments;

import android.content.Context;
import androidx.databinding.DataBindingUtil;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.LoginActivity;
import com.app.khaleeji.Activity.SettingActivity;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.Basic_Response;
import com.app.khaleeji.databinding.FragmentResetPasswordBinding;

import java.util.HashMap;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Utility.AlertDialog;
import Utility.ApiClass;
import Utility.DebugLog;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ResetPasswordFragment extends BaseFragment {
    public static final String TAG= ResetPasswordFragment.class.getSimpleName();
    Context contextd;
    FragmentResetPasswordBinding mBinding;
    String mUserId;
    public ResetPasswordFragment(String userId) {
        mUserId = userId;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reset_password, container, false);
        View rootView = mBinding.getRoot();
        contextd = mActivity;
        if(Build.VERSION.SDK_INT >= 21)
            rootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.primaryDarkColor);
        initView();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();

    }

    private void initView() {
        mBinding.imgBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((SettingActivity)mActivity).back();
            }
        });

        mBinding.mainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard(mActivity);
                return false;
            }
        });

        mBinding.txtConfirm.setEnabled(false);

        mBinding.edtNewPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if( ! s.toString().isEmpty() && s.toString().equals(mBinding.edtConfrimPwd.getText().toString())) {
                    mBinding.txtConfirm.setBackground(getResources().getDrawable(R.drawable.rounded_box_yellow_10));
                    mBinding.txtConfirm.setEnabled(true);
                }else{
                    mBinding.txtConfirm.setEnabled(false);
                    mBinding.txtConfirm.setBackground(getResources().getDrawable(R.drawable.rounded_box_reset));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.edtConfrimPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if( ! s.toString().isEmpty() && s.toString().equals(mBinding.edtNewPwd.getText().toString())){
                    mBinding.txtConfirm.setBackground(getResources().getDrawable(R.drawable.rounded_box_yellow_10));
                    mBinding.txtConfirm.setEnabled(true);
                }else{
                    mBinding.txtConfirm.setEnabled(false);
                    mBinding.txtConfirm.setBackground(getResources().getDrawable(R.drawable.rounded_box_reset));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.txtConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_ResetApi();
            }
        });

    }

    private void call_ResetApi() {
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();
        mparams.put(ApiClass.PASSWORD, mBinding.edtConfrimPwd.getText().toString());
        mparams.put(ApiClass.USER_ID, mUserId);

        Call<Basic_Response> call = mApiInterface.resetPasswordId(mparams);
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    Basic_Response mBasic_Response = response.body();
                    if(mBasic_Response!=null && isAdded()){
                        if (mBasic_Response.getStatus().equalsIgnoreCase("true")) {
                            Toast.makeText(mActivity, getString(R.string.password_changed), Toast.LENGTH_SHORT).show();
                            ((LoginActivity)mActivity).launchLogin();
                        } else {
                            Toast.makeText(mActivity, mBasic_Response.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Basic_Response> call, Throwable t) {
                ProgressDialog.hideprogressbar();
                DebugLog.log(1, TAG, "onResponse" + t.getMessage());
            }
        });

    }
}
