package com.app.khaleeji.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.khaleeji.Activity.MainActivity;
import com.google.firebase.iid.FirebaseInstanceId;
import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.LoginActivity;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.Basic_Response;
import com.app.khaleeji.Response.MobileResponse;
import com.app.khaleeji.Response.OtpResponse;
import com.app.khaleeji.Response.UserDetails;
import com.app.khaleeji.Response.forgotOtpResponse;

import java.util.HashMap;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import CustomView.CustomTextView;
import Utility.AlertDialog;
import Utility.ApiClass;
import Utility.CheckConnection;
import Utility.DebugLog;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import Utility.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneVerificationFragment extends BaseFragment{

    static String TAG = PhoneVerificationFragment.class.getName();
    private Context mContext;
    private View mRootView;
    private boolean mIsResetByPhone;
    private TextView txtVerify, txtResend, txtGetCode;
    private LinearLayout llVerify, llResend, llHelp, llResetByPhone, llResetByEmail;
    private CustomView.CustomTextView phoneTitle, emailTitle;
    private com.chaos.view.PinView pinView;
    private boolean mIsFromSetting = false;
    private boolean mIsFromForgot = false;
    private String mStrMobileNo;
    private String mStrCountryCode;
    private forgotOtpResponse mOtp;
    private UserDetails muserdetail = null;
    private String CountryID, CountryName, fullname, Password, lat, lng;
    private CustomTextView txtPhoneNumber;

    private boolean mIsForgotUsername;
    private String strUsername, mStrOtp;


    public PhoneVerificationFragment(boolean isFromForgotUsername, String username, String otp, String strCountryCode, String strMobileNo){
        mIsForgotUsername = isFromForgotUsername;
        strUsername = username;
        mStrOtp = otp;
        mStrCountryCode = strCountryCode;
        mStrMobileNo = strMobileNo;
        mIsResetByPhone = true;
    }

    public PhoneVerificationFragment(boolean isResetByPhone, boolean isFromForgot, forgotOtpResponse otp, String strCountryCode, String strMobileNo){
        mIsResetByPhone = isResetByPhone;
        mIsFromForgot = isFromForgot;
        mOtp = otp;
        mStrCountryCode = strCountryCode;
        mStrMobileNo = strMobileNo;
    }

    public PhoneVerificationFragment(boolean isResetByPhone, boolean isFromSetting, String strCountryCode, String strMobileNo){
        mIsResetByPhone = isResetByPhone;
        //removed plus character
        CountryID = strCountryCode;
        mStrCountryCode = "+" + strCountryCode;
        mStrMobileNo = strMobileNo;
        mIsFromSetting = isFromSetting;

    }

    public PhoneVerificationFragment(boolean isResetByPhone, UserDetails muserdetail, String country_id, String countryName
            , String fullname, String password, String lat, String lng){
        mIsResetByPhone = isResetByPhone;
        this.muserdetail = muserdetail;
        this.CountryID = country_id;
        this.fullname =fullname;
        this.Password = password;
        this.lat = lat;
        this.lng = lng;
        this.CountryName = countryName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = mActivity;
        if (mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_phoneverification, null);
        if(Build.VERSION.SDK_INT >= 21)
            mRootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.primaryDarkColor);
        initView();
        return mRootView;
    }


    private void setoptCode(String otp) {
        if (muserdetail != null) {
            muserdetail.setOtp(otp);
        }
    }

    public void initView(){
        if(muserdetail != null && ! mIsFromSetting)
            setoptCode(muserdetail.getOtp());
        txtPhoneNumber = mRootView.findViewById(R.id.txtPhoneNumber);
        if(mIsFromSetting || mIsFromForgot || mIsForgotUsername) {
            txtPhoneNumber.setText(mStrCountryCode + "-" + mStrMobileNo);
        }
        else{
            txtPhoneNumber.setText(CountryID + "-"+ muserdetail.getMobileNumber());
        }

        txtVerify = mRootView.findViewById(R.id.txtVerify);
        txtGetCode = mRootView.findViewById(R.id.txtGetCode);
        txtResend = mRootView.findViewById(R.id.txtResend);
        llVerify = mRootView.findViewById(R.id.llVerify);
        llResend = mRootView.findViewById(R.id.llResend);
        llHelp = mRootView.findViewById(R.id.llHelp);
        llResetByPhone = mRootView.findViewById(R.id.llResetByPhone);
        llResetByEmail = mRootView.findViewById(R.id.llResetByEmail);
        ImageView imgBack = mRootView.findViewById(R.id.imgBack);
        phoneTitle = mRootView.findViewById(R.id.phoneTitle);
        emailTitle = mRootView.findViewById(R.id.emailTitle);

        pinView = mRootView.findViewById(R.id.firstPinView);

        CustomTextView txtNeedHelp = mRootView.findViewById(R.id.txtNeedHelp);
        txtNeedHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.openURL("https://khaleejiapp.com/contact-us/", mActivity);
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(countDownTimer != null)
                    countDownTimer.cancel();
                ((LoginActivity)mActivity).back();
            }
        });
        
        txtGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtGetCode.setVisibility(View.GONE);
                llResend.setVisibility(View.VISIBLE);
                if(mIsFromForgot){
                    call_forgotApi();
                }else{
                    callOtpSignup();
                }

                txtResend.setText(getResources().getString(R.string.resend) + " "+"60"+" "+getResources().getString(R.string.sec));
                playCountDownTimer();
            }
        });

        txtVerify.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String strOtp = pinView.getText().toString();
                if( ! strOtp.isEmpty()){
                    if(mIsForgotUsername){
                        if(mStrOtp.equals(strOtp)){
                            if(countDownTimer != null)
                                countDownTimer.cancel();
                            Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), new ForgotUsernameFragment(strUsername), mActivity, R.id.framelayout_login);
                        } else {
                            Toast.makeText(mActivity, getString(R.string.otpnotmatched), Toast.LENGTH_SHORT).show();
                        }
                    }else if(mIsFromSetting) {
                        callingOtpVerificationApi(strOtp);
                    }
                    else if(mIsFromForgot){
                        if(mOtp.getOtp().equals(strOtp)){
                            if(countDownTimer != null)
                                countDownTimer.cancel();
                            Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), new ResetPasswordFragment(mOtp.getUserid()),mActivity, R.id.framelayout_login);
                        } else {
                            Toast.makeText(mActivity, getString(R.string.otpnotmatched), Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        if (CheckConnection.isNetworkAvailable(mContext)) {
                            if (muserdetail.getOtp().equalsIgnoreCase(strOtp)) {
                                if (CheckConnection.isNetworkAvailable(mContext)) {
                                    if(countDownTimer != null)
                                        countDownTimer.cancel();
                                    Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(),new ChooseGenderFragment( muserdetail,
                                            CountryID, fullname, Password, lat , lng ),mActivity, R.id.framelayout_login);
                                } else {
                                    Toast.makeText(mActivity, getString(R.string.network_down), Toast.LENGTH_SHORT).show();
//                                    AlertDialog.showAlert(mActivity, getString(R.string.app_name),
//                                            getString(R.string.network_down), getString(R.string.txt_Done),
//                                            "", false, null, null);
                                }
                            } else {
                                Toast.makeText(mActivity, getString(R.string.otpnotmatched), Toast.LENGTH_SHORT).show();
//                                AlertDialog.showAlert(mActivity, getString(R.string.app_name)
//                                        , getString(R.string.otpnotmatched),
//                                        getString(R.string.txt_Done), "", false,
//                                        null, null);
                            }
                        } else {
                            Toast.makeText(mActivity, getString(R.string.network_down), Toast.LENGTH_SHORT).show();
//                            AlertDialog.showAlert(mActivity, getString(R.string.app_name),
//                                    getString(R.string.network_down), getString(R.string.txt_Done),
//                                    getString(R.string.cancel), false, null, null);
                        }
                    }
                }
            }
        });
        llHelp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });

        if(mIsResetByPhone){
            llResetByPhone.setVisibility(View.VISIBLE);
            llResetByEmail.setVisibility(View.GONE);
            phoneTitle.setVisibility(View.VISIBLE);
            emailTitle.setVisibility(View.GONE);
        }else{
            llResetByPhone.setVisibility(View.GONE);
            llResetByEmail.setVisibility(View.VISIBLE);
            phoneTitle.setVisibility(View.GONE);
            emailTitle.setVisibility(View.VISIBLE);
        }
    }

    private CountDownTimer countDownTimer;
    private void playCountDownTimer(){
        if(countDownTimer != null)
            countDownTimer.cancel();
        countDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                if(isAdded() && txtResend != null)
                    txtResend.setText(getResources().getString(R.string.resend) + " "+millisUntilFinished / 1000+" "+getResources().getString(R.string.sec));
            }
            public void onFinish() {
                if(isAdded()){
                    txtGetCode.setVisibility(View.VISIBLE);
                    llResend.setVisibility(View.GONE);
                }
            }

        };
        countDownTimer.start();
    }

    private void call_forgotApi() {
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();
        mparams.put(ApiClass.FORGOT_TYPE, "mobile");
        mparams.put(ApiClass.MOBILE_NUMBER, mStrMobileNo);
        mparams.put(ApiClass.COUNTRY_CODE, mStrCountryCode);


        Call<Basic_Response> call;
        call = mApiInterface.forgot(mparams);

        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    final Basic_Response mBasic_Response = response.body();
                    if(mBasic_Response!=null && isAdded()){
                        if (mBasic_Response.getStatus().equalsIgnoreCase("true")) {
                            mOtp = mBasic_Response.getData();
                        }else {
                            Toast.makeText(mActivity,mBasic_Response.getMessage(), Toast.LENGTH_SHORT).show();
//                            Utility.AlertDialog.showAlert(mActivity, getString(R.string.app_name), mBasic_Response.getMessage(), getString(R.string.txt_Done),"", false, null, null);
                        }
                    }
                }else {
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Basic_Response> call, Throwable t) {
                ProgressDialog.hideprogressbar();

            }
        });

    }

    private void callOtpSignup() {
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = ApiClass.getmApiClass().buildDefaultParams(mActivity);
        mparams.put(ApiClass.getmApiClass().USERNAME, this.muserdetail.getUsername());
        mparams.put(ApiClass.getmApiClass().EMAIL, this.muserdetail.getEmail());
        mparams.put(ApiClass.getmApiClass().MOBILE_NUMBER, this.muserdetail.getMobileNumber());
        mparams.put(ApiClass.getmApiClass().COUNTRY_ID,CountryID);
        Call<OtpResponse> call = mApiInterface.getOtp(mparams);
        call.enqueue(new Callback<OtpResponse>() {
            @Override
            public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                ProgressDialog.hideprogressbar();
                Log.e(TAG, "validateSignup response received");
                if (response.isSuccessful()) {
                    Log.e(TAG, "validateSignup response Successful");
                    OtpResponse otpResponse = response.body();
                    if (otpResponse != null && isAdded()) {
                        if (otpResponse.getStatus().equalsIgnoreCase("true")) {
                           muserdetail = otpResponse.getUserDetails();
                        } else {
                            Toast.makeText(mActivity,otpResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                            AlertDialog.showAlert(mActivity, getString(R.string.app_name), otpResponse.getMessage(),
//                                    getString(R.string.txt_Done), getString(R.string.cancel), false, null, null);
                        }
                    }
                } else {
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<OtpResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
                DebugLog.log(1, TAG, "onResponse" + t.getMessage());
            }
        });
    }

    private void callingOtpVerificationApi(String otp) {
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams=ApiClass.getmApiClass().buildDefaultParams(mActivity);
        mparams.put(ApiClass.getmApiClass().USERID,SavePref.getInstance(mActivity).getUserdetail().getId().intValue());
        mparams.put(ApiClass.getmApiClass().MOBILE_NUMBER,mStrMobileNo);
        mparams.put(ApiClass.getmApiClass().COUNTRY_CODE,CountryID);
        mparams.put(ApiClass.getmApiClass().OTP,otp);

        Call<MobileResponse> call = mApiInterface.getOtpMobileVerification(mparams);

        call.enqueue(new Callback<MobileResponse>() {
            @Override
            public void onResponse(Call<MobileResponse> call, Response<MobileResponse> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    MobileResponse mOtpResponse= response.body();
                    if(isAdded() && mOtpResponse!=null){
                        if(mOtpResponse.getStatus().equalsIgnoreCase("true")){
                            //launch otp screen here

                            AlertDialog.showAlert(mActivity, getString(R.string.app_name), mOtpResponse.getMessage(), getString(R.string.txt_Done), getString(R.string.cancel), false, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(countDownTimer != null)
                                        countDownTimer.cancel();
                                    UserDetails muserDetail= SavePref.getInstance(mActivity).getUserdetail();
                                    muserDetail.setMobileNumber(mStrMobileNo);
                                    muserDetail.setCountryId(Integer.parseInt(CountryID));
                                    SavePref.getInstance(mActivity).saveUserdetail(muserDetail);
                                    ((LoginActivity)mActivity).back();
                                }
                            }, null);
                        }
                        else
                        {
                            Toast.makeText(mActivity,mOtpResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                            AlertDialog.showAlert(mActivity, getString(R.string.app_name), mOtpResponse.getMessage(), getString(R.string.txt_Done), getString(R.string.cancel), false, null, null);
                        }
                    }
                }
                else {
                    DebugLog.log(1, TAG, "onResponse else " + response.errorBody());
                }
            }
            @Override
            public void onFailure(Call<MobileResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
                DebugLog.log(1, TAG, "onResponse" + t.getMessage());
            }
        });

    }
}
