package com.app.khaleeji.Activity;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;

import com.app.khaleeji.Fragments.ForgotPasswordFragment;
import com.app.khaleeji.Fragments.LoginFragment;
import com.app.khaleeji.Fragments.PhoneVerificationFragment;
import com.app.khaleeji.R;
import com.app.khaleeji.databinding.ActivityLoginBinding;

import CustomView.CustomForgotPwdDlg;
import Utility.Fragment_Process;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding mbinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mbinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("startFragment").equals("OTPFragment")) {
                Fragment_Process.replaceFragment(getSupportFragmentManager(), new PhoneVerificationFragment(true, true,
                        getIntent().getExtras().getString("countryCode"), getIntent().getExtras().getString("mobileNo")), this, mbinding.framelayoutLogin.getId());
            } else if (getIntent().getExtras().getString("startFragment").equals("forgot")) {
                Fragment_Process.replaceFragment(getSupportFragmentManager(),new LoginFragment(this, true), this, mbinding.framelayoutLogin.getId());
            }
        }else{
            launchLogin();
        }
    }

    //calling  Login Page
    public void launchLogin() {
       Fragment_Process.replaceFragment(getSupportFragmentManager(), LoginFragment.newInstance(this), this, mbinding.framelayoutLogin.getId());

    }

//    private void showForget_PasswordDialog(final boolean isForgetpassword) {
//
//        CustomForgotPwdDlg dialog = new CustomForgotPwdDlg(this, new CustomForgotPwdDlg.OnDlgCloseListener() {
//            public void onClose(boolean isPhone) {
//                launchForgotPassword(isPhone);
//            }
//        });
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.showDialog();
//    }
//
//    //calling  Forgot Password
//    private void launchForgotPassword(boolean isPhone) {
//        Fragment_Process.replaceFragment(getSupportFragmentManager(), ForgotPasswordFragment.newInstance(isPhone, this), this, R.id.framelayout_login);
//    }

    public void back(){
        int backstack_count = getSupportFragmentManager().getBackStackEntryCount();
        if (backstack_count <= 1) {
            LoginActivity.this.finish();
        } else if (backstack_count > 1) {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }
}
