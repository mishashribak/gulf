package com.app.khaleeji.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.ChatActivity;
import com.app.khaleeji.Activity.LoginActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Activity.SettingActivity;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.MobileResponse;
import com.app.khaleeji.databinding.FragmentPhonenumberBinding;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.AppConstants;
import Constants.Bundle_Identifier;
import CustomView.CustomCountryCodeDialog;
import Model.CountryModel;
import Utility.AlertDialog;
import Utility.ApiClass;
import Utility.DebugLog;
import Utility.Fragment_Process;
import Utility.GlobalVariable;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingChangeMobileNumberFragment extends BaseFragment {

    public static final String TAG = SettingChangeMobileNumberFragment.class.getSimpleName();
    private FragmentPhonenumberBinding mbinding;
    private String phone_or_email = "";
    private int is_old_country=0;
    private CustomCountryCodeDialog mCountryCodeDlg;
    private Context mContext;

    public SettingChangeMobileNumberFragment(Context ctx) {
        mContext = ctx;
        // Required empty public constructor
    }

    public static SettingChangeMobileNumberFragment newInstance(String tag, Context ctx) {
        SettingChangeMobileNumberFragment fragment = new SettingChangeMobileNumberFragment(ctx);
        Bundle b = new Bundle();
        b.putString(Bundle_Identifier.viaPhoneEmail, tag);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            phone_or_email = getArguments().getString(Bundle_Identifier.viaPhoneEmail);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_phonenumber, container, false);
        View view = mbinding.getRoot();
        if(Build.VERSION.SDK_INT >= 21)
            view.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        initView();
        return view;
    }

    @Override
    public void onResume() {
       super.onResume();


    }

    private void initView() {
        mContext = mActivity;

        mbinding.etmobileno.setText(SavePref.getInstance(mActivity).getUserdetail().getMobileNumber());

        mCountryCodeDlg = new CustomCountryCodeDialog(mContext, new CustomCountryCodeDialog.OnItemClickListener() {
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

        mbinding.imgBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((SettingActivity)mActivity).back();
            }
        });

        mbinding.mainlayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard(mActivity);
                return false;
            }
        });

        mbinding.btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_ChangeCredentialApi();
            }
        });

        mbinding.tgAllowFindMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = ((ToggleButton) view).isChecked();
                callSetMobileVisibility(on);
            }
        });

        callGetMobileVisibility();
    }

    private void callGetMobileVisibility(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface apiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getMobileVisibility(SavePref.getInstance(mActivity).getUserdetail().getId()+"");

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject != null) {
                        if (jsonObject.get("status").getAsBoolean()) {
                            if(jsonObject.get("data").getAsString() != null){
                                if(jsonObject.get("data").getAsString().equals("0")){
                                    mbinding.tgAllowFindMobile.setChecked(false);
                                }else{
                                    mbinding.tgAllowFindMobile.setChecked(true);
                                }
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

    private void callSetMobileVisibility(boolean isChecked){
        ProgressDialog.showProgress(mActivity);
        ApiInterface apiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.setMobileVisibility(SavePref.getInstance(mActivity).getUserdetail().getId(), isChecked? 1 : 0);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject != null) {
                        if (jsonObject.get("status").getAsBoolean()) {
                            if(jsonObject.get("data").getAsString() != null){

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


    private String strMobileNo;
    private String strCountryCode;
    private void call_ChangeCredentialApi() {
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();
        strMobileNo = mbinding.etmobileno.getText().toString().trim();
        strCountryCode = mbinding.txtCountryCode.getText().subSequence(1,4).toString();
        mparams.put(ApiClass.MOBILE_NUMBER,strMobileNo);
        mparams.put(ApiClass.USERID, SavePref.getInstance(mActivity).getUserdetail().getId().intValue());
        mparams.put(ApiClass.COUNTRY_CODE, strCountryCode);

        Call<MobileResponse> call;
        call = mApiInterface.changemobilenumber(mparams);
        call.enqueue(new Callback<MobileResponse>() {
            @Override
            public void onResponse(Call<MobileResponse> call, Response<MobileResponse> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    final MobileResponse mBasic_Response = response.body();
                    if(mBasic_Response!=null && isAdded()){
                        if (mBasic_Response.getStatus().equalsIgnoreCase("true")) {
                                AlertDialog.showAlert(mActivity, getString(R.string.app_name), mBasic_Response.getMessage(), getString(R.string.txt_Done), getString(R.string.cancel), false, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(mActivity, LoginActivity.class);  //ShowCameraToolPictureActivity
                                        intent.putExtra("startFragment", "OTPFragment");
                                        intent.putExtra("isFromSetting", true);
                                        intent.putExtra("countryCode", strCountryCode);
                                        intent.putExtra("mobileNo", strMobileNo);
                                        startActivity(intent);
                                    }
                                }, null);
                        }else {
                            AlertDialog.showAlert(mActivity, getString(R.string.app_name), mBasic_Response.getMessage(), getString(R.string.txt_Done), getString(R.string.cancel), false, null, null);
                        }
                   }
                }else {
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<MobileResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
                DebugLog.log(1, TAG, "onResponse" + t.getMessage());
            }
        });

    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCountryselected(CountryModel mCountryModel) {
//        if(is_old_country==1){
//            if (mbinding.tvolcountycode != null)
//                mbinding.tvolcountycode.setText(mCountryModel.getCode());
//
//        }
//        else if(is_old_country==2){
//            if (mbinding.txtViewcountrycode != null)
//                mbinding.txtViewcountrycode.setText(mCountryModel.getCode());
//
//        }
//        if(is_old_country==3){
//              if (mbinding.tvconfirmcountrycode != null)
//                  mbinding.tvconfirmcountrycode.setText(mCountryModel.getCode());
//        }

    }


}