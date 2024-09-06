package com.app.khaleeji.Fragments;

import android.content.Context;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.ChatActivity;
import com.app.khaleeji.Activity.LoginActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Activity.SettingActivity;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.Basic_Response;
import com.app.khaleeji.databinding.FragmentChangePasswordBinding;

import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.Bundle_Identifier;
import Utility.AlertDialog;
import Utility.ApiClass;
import Utility.DebugLog;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SettingUpdatePasswordFragment extends BaseFragment {
    public static final String TAG= SettingUpdatePasswordFragment.class.getSimpleName();
    public boolean isCameFromSetting;
    Context contextd;
    FragmentChangePasswordBinding mBinding;
    public SettingUpdatePasswordFragment() {
    }

    public static SettingUpdatePasswordFragment newInstance(boolean isCameFromSetting) {
        SettingUpdatePasswordFragment fragment = new SettingUpdatePasswordFragment();
        Bundle b = new Bundle();
        b.putBoolean(Bundle_Identifier.ISCAME_FROM_SETTING,isCameFromSetting);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isCameFromSetting=getArguments().getBoolean(Bundle_Identifier.ISCAME_FROM_SETTING);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_change_password, container, false);
        View rootView = mBinding.getRoot();
        contextd = mActivity;
        if(Build.VERSION.SDK_INT >= 21)
            rootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
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

//        mBinding.txtForgotPwd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mActivity, LoginActivity.class);  //ShowCameraToolPictureActivity
//                intent.putExtra("startFragment", "forgot");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//            }
//        });

        mBinding.txtConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBinding.edtCurrentPwd.getText().toString().trim().length() == 0) {
                    AlertDialog.showAlert(mActivity, getString(R.string.app_name), getString(R.string.passwordBlank), getString(R.string.txt_Done), "", false, null, null);
                } else if (mBinding.edtNewPwd.getText().toString().trim().length() == 0) {
                    AlertDialog.showAlert(mActivity, getString(R.string.app_name), getString(R.string.newpasswordBlanck), getString(R.string.txt_Done), "", false, null, null);
                }
                else if (mBinding.edtNewPwd.getText().toString().trim().length() < 6 || mBinding.edtNewPwd.getText().toString().length()>20) {
                AlertDialog.showAlert(mActivity, getString(R.string.app_name), getString(R.string.passwordnotlong), getString(R.string.txt_Done), "", false, null, null);
                }
                else if (mBinding.edtConfrimPwd.getText().toString().trim().length() == 0) {
                    AlertDialog.showAlert(mActivity, getString(R.string.app_name), getString(R.string.confirmpasswordBlanck), getString(R.string.txt_Done), "", false, null, null);
                } else if (!mBinding.edtNewPwd.getText().toString().equalsIgnoreCase(mBinding.edtConfrimPwd.getText().toString().trim())) {
                    AlertDialog.showAlert(mActivity, getString(R.string.app_name), getString(R.string.confimpasswordnotmatch), getString(R.string.txt_Done), "", false, null, null);
                } else {
                    //run change password api here
                    call_ChangePassword();
                }
            }
        });
    }

    private void call_ChangePassword() {
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = ApiClass.getmApiClass().buildDefaultParams(mActivity);
        mparams.put(ApiClass.getmApiClass().USER_ID,SavePref.getInstance(mActivity).getUserdetail().getId().toString());
        mparams.put(ApiClass.getmApiClass().NEWPASSWORD, mBinding.edtNewPwd.getText().toString().trim());
        mparams.put(ApiClass.getmApiClass().OLDPASSWORD, mBinding.edtCurrentPwd.getText().toString().trim());
        Call<Basic_Response> call = mApiInterface.changePassword(mparams);
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    Basic_Response mBasic_Response = response.body();
                    DebugLog.log(1, TAG, "onResponse" + mBasic_Response.getMessage() );
                   if(mBasic_Response!=null && isAdded()){
                    if (mBasic_Response.getStatus().equalsIgnoreCase("true")) {
                        Utility.AlertDialog.showAlert(mActivity, getString(R.string.app_name), mBasic_Response.getMessage(), getString(R.string.txt_Done), "", false, null, null);
                        ((SettingActivity)mActivity).back();

                    } else {
                        Utility.AlertDialog.showAlert(mActivity, getString(R.string.app_name), mBasic_Response.getMessage(), getString(R.string.txt_Done), "", false, null, null);
                    }
                   }
                } else {
                    DebugLog.log(1, TAG, "onResponsee error" +response.errorBody());
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
