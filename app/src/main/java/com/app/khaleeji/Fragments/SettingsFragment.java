package com.app.khaleeji.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.GulflinkApplication;
import com.app.khaleeji.Activity.LoginActivity;
import com.app.khaleeji.Activity.SettingActivity;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.blockistPackage.BlockListResponse;
import com.app.khaleeji.databinding.FragmentSettingsBinding;

import java.util.Locale;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.Bundle_Identifier;
import Constants.SessionData;
import Utility.ApiClass;
import Utility.Fragment_Process;
import Utility.SavePref;
import Utility.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SettingsFragment extends BaseFragment implements View.OnClickListener {

    Context mContext;
    FragmentSettingsBinding mBinding;
    String TAG = SettingsFragment.class.getSimpleName();

    ApiInterface deleteAccountCall, deactivateAccountCall;

    Menu menu;
    MenuItem itemCart, itemSearch;
    private GulflinkApplication gulflinkApplication;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
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

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);

        View rootView = mBinding.getRoot();
        if(Build.VERSION.SDK_INT >= 21)
            rootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        deactivateAccountCall = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        deleteAccountCall = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        gulflinkApplication = (GulflinkApplication) mActivity.getApplicationContext();
        initView();
        return rootView;
    }

    private void initView() {
//        ((MainActivity)mActivity).hide();
//        ((MainActivity)mActivity).showFullScreen();

        mContext = mActivity;
        mBinding.llPrivacySetting.setOnClickListener(this);
        mBinding.notifications.setOnClickListener(this);
        mBinding.changePassword.setOnClickListener(this);
       // mBinding.tvswitchlanguage.setOnClickListener(this);
        mBinding.changeEmail.setOnClickListener(this);
        mBinding.changemobilenumber.setOnClickListener(this);
        mBinding.tvGulfTerms.setOnClickListener(this);
        mBinding.imgBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((SettingActivity)mActivity).back();
            }
        });
        //mBinding.imgNotiActive.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//
//            }
//        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {

        //((MainActivity)mContext).showOptionsMenu();
        super.onPause();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.notifications:

                Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), SettingNotificationDetailFragment.newInstance(), mActivity, R.id.framelayout_main);

                break;

//            case R.id.tvBlockList:
//
//                Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), BlockListFragment.newInstance("", ""), mActivity, R.id.framelayout_main);
//
//                break;


            case R.id.changePassword:
                Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), SettingUpdatePasswordFragment.newInstance(true), mActivity, R.id.framelayout_main);
                break;

//            case R.id.whoCan:
//                if (mBinding.whoCanArrow.isSelected()) {
//                    mBinding.whoCanArrow.setSelected(false);
//                    mBinding.dropContent.setVisibility(View.GONE);
//                } else {
//                    mBinding.dropContent.setVisibility(View.VISIBLE);
//                    mBinding.whoCanArrow.setSelected(true);
//                }
//                break;
//            case R.id.messageMe:
//                Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), ContactMeFragment.newInstance(), mActivity, R.id.framelayout_main);
//                break;
//            case R.id.viewMyStory:
//                Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), ViewMyStoryFragment.newInstance(true), mActivity, R.id.framelayout_main);
//                break;
//            case R.id.tvswitchlanguage:
//                Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), Fragment_language.newInstance(), mActivity, R.id.framelayout_main);
//                break;
            case R.id.changeEmail:
                Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), SettingChangeEmailFragment.newInstance(), mActivity, R.id.framelayout_main);
                break;
//            case R.id.changeusername:
//                Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), SettingChangeMobileNumberFragment.newInstance("username"), mActivity, R.id.framelayout_main);
//                break;
            case R.id.changemobilenumber:
                Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), SettingChangeMobileNumberFragment.newInstance("phone", mContext), mActivity, R.id.framelayout_main);
                break;
//            case R.id.DeactivateAccount:
//                deactiviteDialog();
//                break;
//            case R.id.DeleteAccount:
//                delelteAccountDialog();
//                break;
            case R.id.llPrivacySetting:
                Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), new SettingPrivacyFragment(), mActivity, R.id.framelayout_main);
                break;
            case R.id.tvGulfTerms:
                Utils.openURL(ApiClass.GULF_TERMS_URL, mActivity);
                break;
        }
    }

    private void deactiviteDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setMessage(R.string.are_you_sure_want_to_deacitvate_account);
        alertDialogBuilder.setPositiveButton(getString(R.string.txt_yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        callDeactivateApi();
                    }
                });

        alertDialogBuilder.setNegativeButton(getString(R.string.txt_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void delelteAccountDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setMessage("Are you sure, You want to Delete Account");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        callDeleteApi();
                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void callDeleteApi() {
//        mBinding.progressBar.setVisibility(View.VISIBLE);
//        deleteAccountCall.deleteUserAccount(SavePref.getInstance(mActivity).getUserdetail().getId().toString())
//                .enqueue(new Callback<BlockListResponse>() {
//                    @Override
//                    public void onResponse(Call<BlockListResponse> call, Response<BlockListResponse> response) {
//                     //   mBinding.progressBar.setVisibility(View.GONE);
//                        if (response.isSuccessful()) {
//                            BlockListResponse blockListResponse = response.body();
//                            if (blockListResponse.getStatus().equalsIgnoreCase("true")) {
//                                clearAppData();
//                            }
//                        }
//                      mBinding.progressBar.setVisibility(View.GONE);
//                    }
//                    @Override
//                    public void onFailure(Call<BlockListResponse> call, Throwable t) {
//                        mBinding.progressBar.setVisibility(View.GONE);
//                    }
//                });
    }

    private void callDeactivateApi() {
//        mBinding.progressBar.setVisibility(View.VISIBLE);
//        deactivateAccountCall.deactivateUserAccount(SavePref.getInstance(mActivity).getUserdetail().getId()
//                .toString()).enqueue(new Callback<BlockListResponse>() {
//            @Override
//            public void onResponse(Call<BlockListResponse> call, Response<BlockListResponse> response) {
//                mBinding.progressBar.setVisibility(View.GONE);
//                BlockListResponse blockListResponse = response.body();
//                if (blockListResponse.getStatus().equalsIgnoreCase("true")) {
//                    clearAppData();
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BlockListResponse> call, Throwable t) {
//                mBinding.progressBar.setVisibility(View.GONE);
//            }
//        });
    }

    private void clearAppData() {
        String device_key = SavePref.getInstance(mActivity).getFirebase_DeviceKey();
        SavePref.getInstance(mActivity).clearPref();
        SavePref.getInstance(mActivity).save_FirebaseDeviceKey(device_key);
        SessionData.I().clearLocalData();
        gulflinkApplication.roomIdList.clear();
        gulflinkApplication.chatUserDataList.clear();
        openLoginActivity();
    }

    private void openLoginActivity() {
        Intent intent = new Intent(mActivity, LoginActivity.class);
        SessionData.makeIntentAsClearHistory(intent);
        startActivity(intent);
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        menu.clear();
//        super.onCreateOptionsMenu(menu, inflater);
//    }

//    public void hideOptionsMenu() {
//        if (menu != null) {
//            MenuItem itemSearch = menu.findItem(R.id.action_search);
//            itemSearch.setVisible(false);
//            MenuItem itemNtfcn = menu.findItem(R.id.action_notification);
//            itemNtfcn.setVisible(false);
//        }
//    }
}
