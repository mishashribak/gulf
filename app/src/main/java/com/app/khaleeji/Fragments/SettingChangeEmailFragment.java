package com.app.khaleeji.Fragments;

import android.content.DialogInterface;
import androidx.databinding.DataBindingUtil;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Activity.SettingActivity;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.Basic_Response;
import com.app.khaleeji.Response.UserDetails;
import com.app.khaleeji.Response.VisibilityResponse;
import com.app.khaleeji.databinding.FragmentEmailchangeBinding;

import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Utility.AlertDialog;
import Utility.ApiClass;
import Utility.DebugLog;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SettingChangeEmailFragment extends BaseFragment {

    public static final String TAG = SettingChangeEmailFragment.class.getSimpleName();
    private FragmentEmailchangeBinding mbinding;

    public SettingChangeEmailFragment() {
        // Required empty public constructor
    }


    public static SettingChangeEmailFragment newInstance() {
        SettingChangeEmailFragment fragment = new SettingChangeEmailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_emailchange, container, false);

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
        mbinding.imgBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((SettingActivity)mActivity).back();
            }
        });
        mbinding.llMainlayout.setOnTouchListener(new View.OnTouchListener() {
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

        getEmail();

    }

    private void call_ChangeCredentialApi() {

        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = ApiClass.getmApiClass().buildDefaultParams(mActivity);
        mparams.put(ApiClass.TYPE, "email");
        mparams.put(ApiClass.OLDEmail, SavePref.getInstance(mActivity).getUserdetail().getEmail());
        mparams.put(ApiClass.VALUE, mbinding.edtNewEmail.getText().toString().trim());
        mparams.put("userid", SavePref.getInstance(mActivity).getUserdetail().getId().intValue());

        Call<Basic_Response> call;

       call = mApiInterface.changeUsernameEmailMobile(mparams);


        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    final Basic_Response mBasic_Response = response.body();
                   if(mBasic_Response!=null && isAdded()){
                    if (mBasic_Response.getStatus().equalsIgnoreCase("true")) {
                        Toast.makeText(mActivity,getString(R.string.email_change_success), Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(mActivity,mBasic_Response.getMessage(), Toast.LENGTH_SHORT).show();
//                        AlertDialog.showAlert(mActivity, getString(R.string.app_name), mBasic_Response.getMessage(), getString(R.string.txt_Done),"", false, null, null);
                    }
                   }
                }else {
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

    private void getEmail(){

        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Call<VisibilityResponse> call;
        call = mApiInterface.getEmail(SavePref.getInstance(mActivity).getUserdetail().getId().intValue());
        call.enqueue(new Callback<VisibilityResponse>() {
            @Override
            public void onResponse(Call<VisibilityResponse> call, Response<VisibilityResponse> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        VisibilityResponse visibilityResponse = response.body();
                        if(visibilityResponse != null){
                            if(visibilityResponse.getStatus().equalsIgnoreCase("true")){
                                String email = visibilityResponse.getData();
                                Spanned htmlAsSpanned = Html.fromHtml(getResources().getString(R.string.your_email) +" <b>"+email+"</b>");
                                mbinding.txtEmail.setText(htmlAsSpanned);
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