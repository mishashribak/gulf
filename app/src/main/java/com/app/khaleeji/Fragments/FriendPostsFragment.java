package com.app.khaleeji.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Adapter.FriendPostsAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;

import com.app.khaleeji.Response.GulfProfileResponse;
import com.app.khaleeji.Response.GulfProfiledata;
import com.app.khaleeji.Response.MemoryModel;
import com.app.khaleeji.Response.RemoveMediaResponse;
import com.app.khaleeji.databinding.FragmentFriendPostsBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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


public class FriendPostsFragment extends BaseFragment {

    private Context mContext;
    private FragmentFriendPostsBinding mbinding;
    private  List<MemoryModel> mMemoryModelList;
    private  int nScrollPos = -1;
    private int mIndex = -1;

    public FriendPostsFragment() {

    }

    public FriendPostsFragment(int position, boolean b) {
        nScrollPos = position;
    }

    public FriendPostsFragment(int userId) {
        call_getProfile(userId);
    }

    public static FriendPostsFragment newInstance() {
        FriendPostsFragment fragment = new FriendPostsFragment();

        return fragment;
    }

    private void call_getProfile(int userId) {
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        String token = SavePref.getInstance(mActivity).getUserdetail().getToken();
        Call<GulfProfileResponse> call = mApiInterface.getGulfProfile("Bearer "+token , String.valueOf(userId));
        call.enqueue(new Callback<GulfProfileResponse>() {
            @Override
            public void onResponse(Call<GulfProfileResponse> call, Response<GulfProfileResponse> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    GulfProfileResponse mOtpResponse = response.body();

                    if(mOtpResponse!=null && isAdded()){
                        if (mOtpResponse.getStatus().equalsIgnoreCase("true")) {
                            SavePref.getInstance(mActivity).saveGulfProfileResponse(mOtpResponse);

                            GulfProfiledata gulfProfileData= mOtpResponse.getData();
                            if(gulfProfileData != null ){
                                mMemoryModelList = gulfProfileData.getMemoryList();
                                if(listAdapter != null){
                                    mbinding.rvPostHistory.setMemoryModels(mMemoryModelList);
                                    listAdapter.setData(mMemoryModelList);
                                }
                            }

                        } else {
                            Utility.AlertDialog.showAlert(mActivity, getString(R.string.app_name), mOtpResponse.getMessage(), getString(R.string.txt_Done), getString(R.string.cancel), false, null, null);
                        }
                    }
                } else {
                    System.out.println(response.errorBody());
                    AlertDialog.showAlert(mActivity, getString(R.string.app_name), response.errorBody().toString(),
                            getString(R.string.txt_Done), getString(R.string.cancel), false, null, null);
                }
            }

            @Override
            public void onFailure(Call<GulfProfileResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
                Log.i("ProfileFragment", "onResponse" + t.getMessage());
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = mActivity;
        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_friend_posts, container, false);
        View rootView = mbinding.getRoot();

        if(Build.VERSION.SDK_INT >= 21)
            rootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        initView();
        return rootView;
    }

    private FriendPostsAdapter listAdapter;
    private void initView(){
        ((MainActivity)mActivity).hide();

        if(SavePref.getInstance(mActivity).getNewMsgNotification()){
            mbinding.imgBadge.setVisibility(View.VISIBLE);
        }else{
            mbinding.imgBadge.setVisibility(View.INVISIBLE);
        }

        mbinding.imgBack.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                ((MainActivity)mActivity).back();
            }
        });

        mbinding.imgMessage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                ((MainActivity)mActivity).openMessageFragment();
            }
        });

        listAdapter = new FriendPostsAdapter(mActivity, nScrollPos, new FriendPostsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int index, int type) {
                mbinding.rvPostHistory.pauseVideo();
                if(type == AppConstants.TYPE_LIKES) {
                    Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new PostLikesFragment(mMemoryModelList.get(index).getId()),mActivity, R.id.framelayout_main);
                    return;
                }
                if(type == AppConstants.TYPE_CIRCLE_IMG){
                    if(mMemoryModelList != null && mMemoryModelList.get(index).getUser_id() != null){
                        if( mMemoryModelList.get(index).getUser_id().intValue() != SavePref.getInstance(mActivity).getUserdetail().getId().intValue()){
                            Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new FriendProfileFragment(mMemoryModelList.get(index).getUser_id()), mActivity, R.id.framelayout_main);
                        }else{
                            Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), ProfileFragment.newInstance(), mActivity, R.id.framelayout_main);
                        }
                    }

                    return;
                }

                if(type == AppConstants.TYPE_POST_IMG){
                    mIndex = index;
                    Fragment_Process.addFragment(mActivity.getSupportFragmentManager(),new FriendDetailPostFragment(mMemoryModelList.get(index)),mActivity, R.id.framelayout_main);
                    return;
                }

                if(type == AppConstants.TYPE_MORE_REPORT){
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mActivity, R.style.MyAlertDialogStyle);
                    builder.setTitle(getResources().getString(R.string.app_name));
                    builder.setMessage(getString(R.string.are_you_sure_want_to_report));
                    builder.setPositiveButton(getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            callReportMedia(index, mMemoryModelList.get(index).getId());
                        }
                    });
                    builder.setNegativeButton(getString(R.string.txt_no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                    return;
                }

                if(type == AppConstants.TYPE_VIEWS){
                    Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new ViewedUsersFragment(mMemoryModelList.get(index).getId()), mActivity, R.id.framelayout_main);
                }
            }
        });
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mbinding.rvPostHistory.setLayoutManager(mLayoutManager);
        mbinding.rvPostHistory.setAdapter(listAdapter);
        mbinding.rvPostHistory.setMemoryModels(mMemoryModelList);
        listAdapter.setData(mMemoryModelList);

        if(nScrollPos != -1){
            mbinding.rvPostHistory.scrollToPosition(nScrollPos);
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent (MessageEvent messageEvent) {
        if(messageEvent.getType() == MessageEvent.MessageType.FRIEND_POSTS){
            mMemoryModelList = messageEvent.getMemoryModelList();
            if(mMemoryModelList != null ){
                if(mMemoryModelList.size() > 0){
                    listAdapter.setData(mMemoryModelList);
                    mbinding.rvPostHistory.setMemoryModels(mMemoryModelList);
                    mbinding.txtNoData.setVisibility(View.GONE);
                }
                else{
                    mbinding.txtNoData.setVisibility(View.VISIBLE);
                }
            }
            remvoeSticky();
        }else if(messageEvent.getType() == MessageEvent.MessageType.POST_REFRESH){
            if(mIndex != -1){
                mMemoryModelList.remove(mIndex);
                mMemoryModelList.add(mIndex, messageEvent.getMemoryModel());
                if(listAdapter != null){
                    listAdapter.notifyDataSetChanged();
                }
                remvoeSticky();
            }
        }else if(messageEvent.getType() == MessageEvent.MessageType.NEW_MSG_NOTIFICATION){
            mbinding.imgBadge.setVisibility(View.VISIBLE);
            remvoeSticky();
        }
    }

    public void remvoeSticky(){
        MessageEvent stickyEvent = EventBus.getDefault().getStickyEvent(MessageEvent.class);
        if(stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent(stickyEvent);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("FriendPostsFragment", "onStop");
        mbinding.rvPostHistory.stopVideo();
    }

    private void callReportMedia(int index, int id){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = ApiClass.getmApiClass().buildDefaultParams(mActivity);
        mparams.put("media_id", id);
        mparams.put("user_id", SavePref.getInstance(mActivity).getUserdetail().getId().intValue());

        Call<RemoveMediaResponse> call = mApiInterface.reportMediaService(mparams);
        call.enqueue(new Callback<RemoveMediaResponse>() {
            @Override
            public void onResponse(Call<RemoveMediaResponse> call, Response<RemoveMediaResponse> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    final RemoveMediaResponse mBasic_Response = response.body();
                    if(mBasic_Response!=null && isAdded()){
                        if (mBasic_Response.getStatus().equalsIgnoreCase("true")) {

                        }
                        Toast.makeText(mActivity,mBasic_Response.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else {
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<RemoveMediaResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }
}
