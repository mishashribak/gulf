package com.app.khaleeji.Fragments;

import android.content.Context;

import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.ChatActivity;
import com.app.khaleeji.Activity.SettingActivity;
import com.app.khaleeji.Adapter.CustomGroupAdapter;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.Basic_Response;
import com.app.khaleeji.Response.groupListPackage.GroupModel;
import com.app.khaleeji.Response.groupListPackage.GroupListReponse;
import com.app.khaleeji.databinding.FragmentCustomGroupBinding;
import java.util.List;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Utility.ApiClass;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SettingCustomGroupFragment extends BaseFragment{

    private Context contextd;
    private FragmentCustomGroupBinding mBinding;
    private String TAG = SettingCustomGroupFragment.class.getSimpleName();
    private CustomGroupAdapter listAdapter;
    private GroupListReponse mGroupListReponse;
    private List<GroupModel> mGroupList;


    public SettingCustomGroupFragment(){}

    public static SettingCustomGroupFragment newInstance() {
        SettingCustomGroupFragment fragment = new SettingCustomGroupFragment();
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

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_custom_group, container, false);

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

        //group edit
        listAdapter = new CustomGroupAdapter(getContext(), new CustomGroupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int index) {

               Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), SettingGroupFragment.newInstance(mGroupList.get(index)),mActivity, R.id.framelayout_main);
            }

            @Override
            public void onRemove(int index){
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mActivity, R.style.MyAlertDialogStyle);
                builder.setTitle(getResources().getString(R.string.app_name));
                builder.setMessage(getString(R.string.group_remove_confirm));
                builder.setPositiveButton(getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callRemoveGroup(mGroupList.get(index).getId(), index);
                    }
                });
                builder.setNegativeButton(getString(R.string.txt_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mBinding.rvGroup.setLayoutManager(mLayoutManager);
        mBinding.rvGroup.setAdapter(listAdapter);

        mBinding.txtCreateNewGroup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), SettingSelGroupUserFragment.newInstance(),mActivity, R.id.framelayout_main);
            }
        });

        callGetGroups();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void callGetGroups(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userid = SavePref.getInstance(mActivity).getUserdetail().getId();
        Call<GroupListReponse> call = mApiInterface.getGroupList(userid);
        call.enqueue(new Callback<GroupListReponse>() {
            @Override
            public void onResponse(Call<GroupListReponse> call, Response<GroupListReponse> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        mGroupListReponse = response.body();
                        if(mGroupListReponse != null){
                            if(mGroupListReponse.getStatus().equalsIgnoreCase("true")){
                                if ( mGroupListReponse.getData() != null && mGroupListReponse.getData().size() > 0) {
                                    mGroupList = mGroupListReponse.getData();
                                    listAdapter.setData(mGroupList);
                                    mBinding.txtNoData.setVisibility(View.GONE);
                                } else {
                                    mBinding.txtNoData.setVisibility(View.VISIBLE);
                                }
                            }else{
                                Toast.makeText(mActivity, mGroupListReponse.getMessage(), Toast.LENGTH_SHORT).show();
//                                AlertDialog.showAlert(mActivity, getString(R.string.app_name), mGroupListReponse.getMessage(),
//                                        getString(R.string.txt_Done), "", false, null, null);
                            }
                        }

                    } else {
                        System.out.println(response.errorBody());
                    }
                }
            }

            @Override
            public void onFailure(Call<GroupListReponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    private void callRemoveGroup(int groupId, int index){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);

        Call<Basic_Response> call = mApiInterface.removeGroup(groupId);
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Basic_Response basic_response = response.body();
                        if(basic_response != null){
                            if(basic_response.getStatus().equalsIgnoreCase("true")){
                                mGroupList.remove(index);
                                listAdapter.notifyDataSetChanged();
                            }else{
                                Toast.makeText(mActivity, mGroupListReponse.getMessage(), Toast.LENGTH_SHORT).show();

                            }
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
