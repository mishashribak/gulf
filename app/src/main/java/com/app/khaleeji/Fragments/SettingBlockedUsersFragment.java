package com.app.khaleeji.Fragments;

import android.content.Context;

import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Activity.SettingActivity;
import com.app.khaleeji.Adapter.BlockedUsersAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.Basic_Response;
import com.app.khaleeji.Response.Search.SearchFriendsModel;
import com.app.khaleeji.Response.UserDetails;
import com.app.khaleeji.databinding.FragmentBlockedUsersBinding;
//import com.applozic.mobicomkit.api.account.user.UserBlockTask;
//import com.applozic.mobicomkit.feed.ApiResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.Bundle_Identifier;
import Model.BlockedUserData;
import Model.CustomGroupData;
import Utility.AlertDialog;
import Utility.ApiClass;
import Utility.CheckConnection;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SettingBlockedUsersFragment extends BaseFragment{

    Context contextd;
    FragmentBlockedUsersBinding mBinding;
    String TAG = SettingBlockedUsersFragment.class.getSimpleName();

    ApiInterface deleteAccountCall, deactivateAccountCall;

    Menu menu;
    MenuItem itemCart, itemSearch;

    private BlockedUsersAdapter mListAdapter;

    private static SettingBlockedUsersFragment _instance;

    public SettingBlockedUsersFragment( ) {

    }

    public static SettingBlockedUsersFragment newInstance() {
        if(_instance == null){
            _instance = new SettingBlockedUsersFragment();
        }
        return _instance;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_blocked_users, container, false);

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


        mListAdapter= new BlockedUsersAdapter(getContext(), new BlockedUsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int type, int index) {
                if(type == 1){
                    callUnblock( mUserList.get(index).getId().intValue(),  index);
                }else{
                    Intent intent = new Intent(mActivity, MainActivity.class);  //ShowCameraToolPictureActivity
                    intent.putExtra("startFragment", "FriendProfileFragment");
                    intent.putExtra("userId",mUserList.get(index).getId());
                    startActivity(intent);
                }
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mBinding.rvGroup.setLayoutManager(mLayoutManager);
        mBinding.rvGroup.setAdapter(mListAdapter);

        if (CheckConnection.isNetworkAvailable(contextd))
        {
            callGetBlockedUsers();
        }
        else
        {
            AlertDialog.showAlert(contextd, getString(R.string.app_name), getString(R.string.network_down), getString(R.string.txt_Done), getString(R.string.cancel), false, null, null);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private SearchFriendsModel mSearchFriendsModel;
    private List<UserDetails> mUserList;

    private void callGetBlockedUsers(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userid = SavePref.getInstance(mActivity).getUserdetail().getId();
        Call<SearchFriendsModel> call = mApiInterface.searchBlockedUsers(userid);
        call.enqueue(new Callback<SearchFriendsModel>() {
            @Override
            public void onResponse(Call<SearchFriendsModel> call, Response<SearchFriendsModel> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        mSearchFriendsModel = response.body();
                        if(mSearchFriendsModel != null){
                            if(mSearchFriendsModel.getStatus().equalsIgnoreCase("true")){
                                if ( mSearchFriendsModel.getData() != null && mSearchFriendsModel.getData().getData().size() > 0) {
                                    mUserList = mSearchFriendsModel.getData().getData();
                                    mListAdapter.setData(mUserList);
                                    mBinding.txtNoData.setVisibility(View.GONE);
                                } else {
                                    mBinding.txtNoData.setVisibility(View.VISIBLE);
                                }
                            }else{
                                AlertDialog.showAlert(mActivity, getString(R.string.app_name), mSearchFriendsModel.getMessage(),
                                        getString(R.string.txt_Done), "", false, null, null);
                            }
                        }

                    } else {
                        System.out.println(response.errorBody());
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchFriendsModel> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    public void callUnblock(int toUserId, int index){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int fromUserId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();

        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().FROM_USER, fromUserId);
        mparams.put(ApiClass.getmApiClass().TO_USER, toUserId);

        Call<Basic_Response> call;
        call = mApiInterface.sendUnblock(mparams);
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Basic_Response basic_response = response.body();
                        if(basic_response.getStatus().equalsIgnoreCase("true")){
                            mUserList.remove(index);
                            mListAdapter.notifyDataSetChanged();
//                            UserBlockTask.TaskListener  taskListener = new UserBlockTask.TaskListener() {
//                                @Override
//                                public void onSuccess(ApiResponse apiResponse) {
//
//                                    Log.i(" Block Status","User is blocked or unblocked"+apiResponse.isSuccess());
//                                }
//
//                                @Override
//                                public void onFailure(ApiResponse apiResponse, Exception exception) {
//
//                                }
//
//                                @Override
//                                public void onCompletion() {
//
//                                }
//                            };
//
//                            new UserBlockTask(getContext(), taskListener, toUserId+"", false).execute((Void) null);
                        }
                    } else {
                        System.out.println(response.errorBody());
                    }
                }
            }
            @Override
            public void onFailure(Call<Basic_Response> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

}
