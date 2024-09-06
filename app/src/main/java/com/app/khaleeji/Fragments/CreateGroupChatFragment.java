package com.app.khaleeji.Fragments;

import android.content.Context;

import androidx.databinding.DataBindingUtil;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Adapter.CreateGroupChatAdapter;
import com.app.khaleeji.Adapter.GroupChatCreateUserAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.FriendData;
import com.app.khaleeji.Response.FriendlistResponse;
import com.app.khaleeji.databinding.FragmentCreateGroupChatBinding;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.AppConstants;
import Utility.AlertDialog;
import Utility.ApiClass;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateGroupChatFragment extends BaseFragment{

    private Context contextd;
    private FragmentCreateGroupChatBinding mBinding;
    private String TAG = CreateGroupChatFragment.class.getSimpleName();
    private  CreateGroupChatAdapter groupChatAdapter;
    private GroupChatCreateUserAdapter groupChatCreateUserAdapter;
    private List<FriendData> mFriendList;
    private ArrayList<FriendData> mSelFriendList = new ArrayList<>();
    private ArrayList<FriendData> mAlreadySelected;
    private boolean isFromUpdateGroup = false;
    public CreateGroupChatFragment() {

    }

    public CreateGroupChatFragment(boolean isFromUpdateGroup, ArrayList<FriendData> alreadySelected) {
        this.isFromUpdateGroup = isFromUpdateGroup;
//        mSelFriendList =alreadySelected;
        mAlreadySelected = alreadySelected;
    }


    public static CreateGroupChatFragment newInstance() {
        CreateGroupChatFragment fragment = new CreateGroupChatFragment();
        return fragment;
    }

    public static CreateGroupChatFragment newInstance(boolean isFromUpdateGroup, ArrayList<FriendData> alreadySelected) {
        CreateGroupChatFragment fragment = new CreateGroupChatFragment(isFromUpdateGroup, alreadySelected);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_group_chat, container, false);

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
                ((MainActivity)mActivity).back();
            }
        });

        groupChatAdapter = new CreateGroupChatAdapter(mActivity, new CreateGroupChatAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(FriendData friendData, int type) {
                if(type == 0){
                    //selected
                    mSelFriendList.add(friendData);
                    mFriendList.get(mFriendList.indexOf(friendData)).setChecked(true);
                }else{
                    if(mSelFriendList.size() > 0){
                        mSelFriendList.remove(friendData);
                        mFriendList.get(mFriendList.indexOf(friendData)).setChecked(false);
                    }
                }
                groupChatAdapter.setData(mFriendList);
                groupChatCreateUserAdapter.setData(mSelFriendList);

                showNextButton();
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        mBinding.rvGroup.setLayoutManager(mLayoutManager);
        mBinding.rvGroup.setAdapter(groupChatAdapter);
        mBinding.rvGroup.setIndexBarColor("#00ffffff");
        mBinding.rvGroup.setIndexBarTextColor("#382C70");
        mBinding.rvGroup.setIndexBarStrokeVisibility(false);
        mBinding.rvGroup.setIndexTextSize(12);

        groupChatCreateUserAdapter = new GroupChatCreateUserAdapter(mActivity, new GroupChatCreateUserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(FriendData friendData, int type) {
                if(type == AppConstants.TYPE_PROFILE){
                    Fragment_Process.addFragment(mActivity.getSupportFragmentManager(),new FriendProfileFragment(friendData.getId()),mActivity, R.id.framelayout_main);
                }else{
                    if(mSelFriendList.size() > 0){
                        mSelFriendList.remove(friendData);
                        mFriendList.get(mFriendList.indexOf(friendData)).setChecked(false);
                        showNextButton();
                    }
                }
                groupChatAdapter.setData(mFriendList);
                groupChatCreateUserAdapter.setData(mSelFriendList);
            }
        });
        groupChatCreateUserAdapter.setData(mSelFriendList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity,LinearLayoutManager.HORIZONTAL,false);
        mBinding.rvProfile.setLayoutManager(layoutManager);
        mBinding.rvProfile.setAdapter(groupChatCreateUserAdapter);

        mBinding.txtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelFriendList.size() <= 0)
                    return;
                if(!isFromUpdateGroup)
                    Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new CreateGroupFinalFragment(mSelFriendList, mFriendList.size()),mActivity, R.id.framelayout_main);
                else{
                    MessageEvent messageEvent = new MessageEvent();
                    messageEvent.setType(MessageEvent.MessageType.UPDATE_GROUP);
                    messageEvent.setFriendData(mSelFriendList);
                    EventBus.getDefault().post(messageEvent);
                    ((MainActivity)mActivity).back();
                }
            }
        });

        mBinding.searchEdit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (groupChatAdapter != null && groupChatCreateUserAdapter != null) {
                    groupChatAdapter.getFilter().filter(s);
                    groupChatCreateUserAdapter.getFilter().filter(s);
                }
            }
        });

        getMyfriendList();
    }

    private void showNextButton(){
        if(mSelFriendList.size() > 0){
            mBinding.rvProfile.setVisibility(View.VISIBLE);
            mBinding.txtNext.setVisibility(View.VISIBLE);
        }else{
            mBinding.txtNext.setVisibility(View.GONE);
            mBinding.rvProfile.setVisibility(View.GONE);
        }
        mBinding.txtSelNum.setText(mSelFriendList.size() + "/" + mFriendList.size());
    }

    private void getMyfriendList() {
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userid = SavePref.getInstance(mActivity).getUserdetail().getId();
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().REQUEST_TYPE, "myfriends");
        mparams.put(ApiClass.getmApiClass().USER_ID, String.valueOf(userid));
        String token = SavePref.getInstance(mActivity).getUserdetail().getToken();
        Call<FriendlistResponse> call = mApiInterface.friendRequestList("Bearer "+token,mparams);
        call.enqueue(new Callback<FriendlistResponse>() {
            @Override
            public void onResponse(Call<FriendlistResponse> call, Response<FriendlistResponse> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        FriendlistResponse  lasthotspot = response.body();
                        if(lasthotspot != null) {
                            if (lasthotspot.getStatus().equalsIgnoreCase("true")) {
                                if (lasthotspot.getData() != null && lasthotspot.getData().getData().size() > 0) {
                                    mFriendList = lasthotspot.getData().getData();
//                                    if( isFromUpdateGroup && mSelFriendList.size() > 0){
//                                        for (FriendData friendData: mFriendList) {
//                                            for(int i = 0; i< mSelFriendList.size(); i++){
//                                                if(friendData.getUsername().equals(mSelFriendList.get(i).getUsername())){
//                                                    friendData.setChecked(true);
//                                                    mSelFriendList.set(i, friendData);
//                                                    break;
//                                                }
//                                            }
//                                        }
//                                        groupChatCreateUserAdapter.setData(mSelFriendList);
//                                        showNextButton();
//                                    }
                                    if( isFromUpdateGroup && mAlreadySelected.size() > 0){
                                        for ( int k = 0; k < mFriendList.size(); k++) {
                                            for(int i = 0; i< mAlreadySelected.size(); i++){
                                                if(mFriendList.get(k).getUsername().equals(mAlreadySelected.get(i).getUsername())){
                                                    mFriendList.remove(k);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    groupChatAdapter.setData(mFriendList);
                                    if(isFromUpdateGroup){
                                        if(mFriendList.size() > 0)
                                             mBinding.txtNoData.setVisibility(View.GONE);
                                        else
                                            mBinding.txtNoData.setVisibility(View.VISIBLE);
                                    }else{
                                        mBinding.txtNoData.setVisibility(View.GONE);
                                    }

                                } else {
                                    mBinding.txtNoData.setVisibility(View.VISIBLE);
                                }
                            } else {
                                AlertDialog.showAlert(mActivity, getString(R.string.app_name), lasthotspot.getMessage(), getString(R.string.txt_Done), "", false, null, null);
                            }
                        }

                    } else {
                        System.out.println(response.errorBody());
                    }
                }
            }

            @Override
            public void onFailure(Call<FriendlistResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
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

}
