package com.app.khaleeji.Fragments;

import android.app.Activity;

import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.LoginActivity;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.Basic_Response;
import com.app.khaleeji.databinding.FragmentForgotpasswordBinding;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.AppConstants;
import Constants.Bundle_Identifier;
import CustomView.CustomCountryCodeDialog;
import Utility.ApiClass;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordFragment extends BaseFragment {

    public static final String TAG = ForgotPasswordFragment.class.getSimpleName();
    private FragmentForgotpasswordBinding mbinding;
    private boolean mIsResetByPhone=true;
    private CustomCountryCodeDialog mCountryCodeDlg;
    private Context mContext;
    private boolean mIsForgotUsername;

    public ForgotPasswordFragment(Context ctx) {
        // Required empty public constructor
        mContext = ctx;
    }

    public ForgotPasswordFragment(){

    }

    public static ForgotPasswordFragment newInstance(boolean mIsResetByPhone, Context ctx) {
        ForgotPasswordFragment fragment = new ForgotPasswordFragment(ctx);
        Bundle b = new Bundle();
        b.putBoolean(Bundle_Identifier.ISFORGOT_PASSWORD,mIsResetByPhone);
        fragment.setArguments(b);
        return fragment;
    }

    public static ForgotPasswordFragment newInstance(boolean mIsResetByPhone, boolean isForgotUsername,  Context ctx) {
        ForgotPasswordFragment fragment = new ForgotPasswordFragment(ctx);
        Bundle b = new Bundle();
        b.putBoolean(Bundle_Identifier.ISFORGOT_PASSWORD,mIsResetByPhone);
        b.putBoolean("isForgotUsername",isForgotUsername);
        fragment.setArguments(b);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIsResetByPhone= getArguments().getBoolean(Bundle_Identifier.ISFORGOT_PASSWORD);
            mIsForgotUsername= getArguments().getBoolean("isForgotUsername");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_forgotpassword, container, false);
        View view = mbinding.getRoot();
        ((BaseActivity)mActivity).setStatusBarColor(R.color.primaryDarkColor);
        initView();
        return view;
    }

    private void initView() {

        mCountryCodeDlg = new CustomCountryCodeDialog(mActivity, new CustomCountryCodeDialog.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                mbinding.txtCountryCode.setText(AppConstants.COUNTRY_CODE[pos]);
                mbinding.imgFlag.setImageDrawable(getResources().getDrawable(AppConstants.FLAGS[pos]));
            }
        });
        mCountryCodeDlg.setCanceledOnTouchOutside(false);

        mbinding.llCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountryCodeDlg.showDialog();
            }
        });
        mbinding.llMainlayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard(mActivity);
                return false;
            }
        });

        mbinding.txtContinue.setEnabled(false);
        mbinding.etmobileno.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if(!s.equals("") ) {
                    mbinding.txtContinue.setBackgroundResource(R.drawable.rounded_box_yellow_20);
                    mbinding.txtContinue.setEnabled(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });

        mbinding.etEmail.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if(!s.equals("") ) {
                    mbinding.txtContinue.setBackgroundResource(R.drawable.rounded_box_yellow_20);
                    mbinding.txtContinue.setEnabled(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });

        mbinding.txtContinue.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if(mIsForgotUsername){
                    callForgotUsername();
                }else{
                    if(mIsResetByPhone)
                        call_forgotApi("mobile");

                    else{
                        call_forgotApi("email");
                    }
                }
            }
        });

        mbinding.imgBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((LoginActivity)mActivity).back();
            }
        });

        if(mIsResetByPhone){
            mbinding.llResetByPhone.setVisibility(View.VISIBLE);
            mbinding.llResetByEmail.setVisibility(View.GONE);
        }else{
            mbinding.llResetByPhone.setVisibility(View.GONE);
            mbinding.llResetByEmail.setVisibility(View.VISIBLE);
        }
    }


    private void call_forgotApi(final String forgot_type) {
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();
        mparams.put(ApiClass.FORGOT_TYPE, forgot_type);
        if(forgot_type.equals("email")){
            mparams.put(ApiClass.EMAIL, mbinding.etEmail.getText().toString().trim());
        }else{
            mparams.put(ApiClass.MOBILE_NUMBER, mbinding.etmobileno.getText().toString().trim());
            mparams.put(ApiClass.COUNTRY_CODE, mbinding.txtCountryCode.getText().toString().trim());
        }

        if (Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(Bundle_Identifier.ArabicLang)) {
            mparams.put("language", "ar");
        }else{
            mparams.put("language", "en");
        }

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
                        if(mIsResetByPhone) {
                            Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), new PhoneVerificationFragment(mIsResetByPhone, true, mBasic_Response.getData(),
                                    mbinding.txtCountryCode.getText().toString().trim(), mbinding.etmobileno.getText().toString().trim()), mActivity, R.id.framelayout_login);
                        }else{
                            Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), new EmailInfoFragment(), mActivity, R.id.framelayout_login);
                        }

                    }else {
                        Toast.makeText(mActivity, mBasic_Response.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void callForgotUsername() {
        ProgressDialog.showProgress(mActivity);
        ApiInterface apiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();
        mparams.put(ApiClass.MOBILE_NUMBER, mbinding.etmobileno.getText().toString().trim());
        mparams.put("country_id", mbinding.txtCountryCode.getText().toString().trim());

        if (Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(Bundle_Identifier.ArabicLang)) {
            mparams.put("language", "ar");
        }else{
            mparams.put("language", "en");
        }

        Call<JsonObject> call = apiInterface.forgotUsername(mparams);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject != null && isAdded()) {
                        if (jsonObject.get("status").getAsBoolean()) {
                            if(jsonObject.get("user_details").getAsJsonObject() != null){
                                String username = jsonObject.get("user_details").getAsJsonObject().get("username").getAsString();
                                String otp = jsonObject.get("user_details").getAsJsonObject().get("otp").getAsString();
                                Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(),
                                        new PhoneVerificationFragment(true, username, otp, mbinding.txtCountryCode.getText().toString().trim(), mbinding.etmobileno.getText().toString().trim()), mActivity, R.id.framelayout_login);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

}