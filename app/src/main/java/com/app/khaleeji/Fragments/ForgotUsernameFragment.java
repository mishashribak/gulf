package com.app.khaleeji.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;

import com.app.khaleeji.Activity.LoginActivity;
import com.app.khaleeji.R;
import com.app.khaleeji.databinding.FragmentForgotUsernameBinding;

import CustomView.CustomForgotPwdDlg;
import Utility.Fragment_Process;

public class ForgotUsernameFragment extends BaseFragment{

    static String TAG = ForgotUsernameFragment.class.getName();
    private Context mContext;
    private View mRootView;
    private FragmentForgotUsernameBinding mbinding;
    private String strUsername;


    public ForgotUsernameFragment() {

    }

    public ForgotUsernameFragment(String username) {
        strUsername = username;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_forgot_username, container, false);
        View view = mbinding.getRoot();
        initView();
        return view;
    }

    public void initView(){
        mbinding.txtUsername.setText(strUsername);
        mbinding.tvForgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForget_PasswordDialog();
            }
        });
        mbinding.txtSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LoginActivity)mActivity).launchLogin();
            }
        });
    }

    private void showForget_PasswordDialog() {

        CustomForgotPwdDlg dialog = new CustomForgotPwdDlg(getContext(), new CustomForgotPwdDlg.OnDlgCloseListener() {
            public void onClose(boolean isPhone) {
                launchForgotPassword(isPhone);
            }
        });
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);
        dialog.showDialog();
    }

    //calling  Forgot Password
    private void launchForgotPassword(boolean isPhone) {
        Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), ForgotPasswordFragment.newInstance(isPhone, mActivity), mActivity, R.id.framelayout_login);
    }
}

