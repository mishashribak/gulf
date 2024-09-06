package com.app.khaleeji.Fragments;

import android.content.Context;

import androidx.databinding.DataBindingUtil;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.SettingActivity;
import com.app.khaleeji.Adapter.CreateNewGroupAdapter;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.Basic_Response;
import com.app.khaleeji.Response.CreateGroupResponse;
import com.app.khaleeji.Response.FriendData;
import com.app.khaleeji.databinding.FragmentCreateNewGroupBinding;

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
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingCreateNewGroupFragment extends BaseFragment{

    Context contextd;
    FragmentCreateNewGroupBinding mBinding;
    String TAG = SettingCreateNewGroupFragment.class.getSimpleName();
    private List<FriendData>mFriendList ;
    private CreateNewGroupAdapter mListAdapter;

    public SettingCreateNewGroupFragment( List<FriendData> list ) {
        mFriendList = list;
    }

    public static SettingCreateNewGroupFragment newInstance(List<FriendData> list) {
        SettingCreateNewGroupFragment fragment = new SettingCreateNewGroupFragment(list);
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

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_new_group, container, false);

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

        mListAdapter= new CreateNewGroupAdapter(getContext(), new CreateNewGroupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int index) {
                mFriendList.remove(index);
                mListAdapter.notifyDataSetChanged();
            }
        });

        mListAdapter.setData(mFriendList);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mBinding.rvGroup.setLayoutManager(mLayoutManager);
        mBinding.rvGroup.setAdapter(mListAdapter);

        mBinding.txtCreateNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create new group screen
                if( ! mBinding.etGroupName.getText().toString().isEmpty()){
                    if(mFriendList.size() > 0)
                        callCreateGroup();
                }else{
                    Toast.makeText(mActivity, "Please input group name", Toast.LENGTH_SHORT).show();
//                    AlertDialog.showAlert(mActivity, getString(R.string.app_name), "Please input group name", getString(R.string.txt_Done), "", false, null, null);
                }
            }
        });

        mBinding.mainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((BaseActivity)mActivity).hideSoftKeyboard();
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void callCreateGroup(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int fromUserId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().GROUP_NAME, mBinding.etGroupName.getText().toString());
        mparams.put(ApiClass.getmApiClass().CREATED_BY, fromUserId);
        String str="";
        for(int i= 0; i< mFriendList.size()-1; i++){
            str += (mFriendList.get(i).getId() + ",");
        }
        str += mFriendList.get(mFriendList.size()-1).getId();

        mparams.put(ApiClass.getmApiClass().GROUP_USERS, str);

        Call<CreateGroupResponse> call;
        call = mApiInterface.groupCreate(mparams);
        call.enqueue(new Callback<CreateGroupResponse>() {
            @Override
            public void onResponse(Call<CreateGroupResponse> call, Response<CreateGroupResponse> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        CreateGroupResponse basic_response = response.body();
                        if(basic_response != null) {
                            if( basic_response.getStatus().equalsIgnoreCase("true") ){
                                Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), SettingCustomGroupFragment.newInstance(),mActivity, R.id.framelayout_main);
                            }else{
                                Toast.makeText(mActivity, basic_response.getMessage(), Toast.LENGTH_SHORT).show();
                               /* AlertDialog.showAlert(mActivity, getString(R.string.app_name),basic_response.getMessage(),
                                        getString(R.string.txt_Done), "", false, null, null);*/
                            }
                        }

                    } else {
                        System.out.println(response.errorBody());
                    }
                }
            }

            @Override
            public void onFailure(Call<CreateGroupResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

}
