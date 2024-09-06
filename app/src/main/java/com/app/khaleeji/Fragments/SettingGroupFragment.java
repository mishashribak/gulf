package com.app.khaleeji.Fragments;

import android.content.Context;

import androidx.databinding.DataBindingUtil;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.SettingActivity;
import com.app.khaleeji.Adapter.GroupEditAdapter;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.Basic_Response;
import com.app.khaleeji.Response.groupListPackage.GroupMemberModel;
import com.app.khaleeji.Response.groupListPackage.GroupModel;
import com.app.khaleeji.databinding.FragmentFamilyGroupBinding;
import java.util.List;
import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Utility.AlertDialog;
import Utility.ApiClass;
import Utility.ProgressDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingGroupFragment extends BaseFragment{

    private Context contextd;
    private FragmentFamilyGroupBinding mBinding;
    private String TAG = SettingGroupFragment.class.getSimpleName();
    private String mGroupTitle;
    private List<GroupMemberModel> mGroupMemberList;
    private GroupModel mGroupModel;

    public SettingGroupFragment(GroupModel list) {
        mGroupTitle = list.getGroupName();
        mGroupMemberList = list.getData();
        mGroupModel = list;
    }

    public static SettingGroupFragment newInstance(GroupModel list) {
        SettingGroupFragment fragment = new SettingGroupFragment(list);
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

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_family_group, container, false);

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

        mBinding.txtTitle.setText(mGroupTitle);
        GroupEditAdapter listAdapter = new GroupEditAdapter(mGroupMemberList, getContext(), new GroupEditAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int index, int type) {
                if(type == 0){
                    //selected
                   mGroupMemberList.get(index).setStatus(1);
                }else{
                   mGroupMemberList.get(index).setStatus(0);
                }

            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mBinding.rvGroup.setLayoutManager(mLayoutManager);
        mBinding.rvGroup.setAdapter(listAdapter);
        mBinding.rvGroup.setIndexBarColor("#00ffffff");
        mBinding.rvGroup.setIndexBarTextColor("#382C70");
        mBinding.rvGroup.setIndexBarStrokeVisibility(false);
        mBinding.rvGroup.setIndexTextSize(12);

        mBinding.txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callUpdateGroup();
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

    public void callUpdateGroup(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        String str="";
        for(int i= 0; i< mGroupMemberList.size()-1; i++){
            if(mGroupMemberList.get(i).getStatus() == 1)
                str += (mGroupMemberList.get(i).getId() + ",");
        }
        if(mGroupMemberList.get(mGroupMemberList.size()-1).getStatus() == 1)
            str += mGroupMemberList.get(mGroupMemberList.size()-1).getId();

        Call<Basic_Response> call;
        call = mApiInterface.groupUpdate(mGroupModel.getId().intValue(), str);
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Basic_Response basic_response = response.body();
                        if(basic_response != null) {
                            if( basic_response.getStatus().equalsIgnoreCase("true") ){
                                ((SettingActivity)mActivity).back();
                            }else{
                                AlertDialog.showAlert(mActivity, getString(R.string.app_name),basic_response.getMessage(),
                                        getString(R.string.txt_Done), "", false, null, null);
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
