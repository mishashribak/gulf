package com.app.khaleeji.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Adapter.LocationListAdapter;
import com.app.khaleeji.Adapter.MyPostsAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;

import com.app.khaleeji.Response.Basic_Response;
import com.app.khaleeji.Response.GulfProfiledata;
import com.app.khaleeji.Response.HotSpotDatum;
import com.app.khaleeji.Response.MemoryModel;
import com.app.khaleeji.Response.RemoveMediaResponse;
import com.app.khaleeji.databinding.FragmentMyPostsBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.AppConstants;
import CustomView.CustomLocationDlg;
import Utility.ApiClass;
import Utility.DebugLog;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPostsFragment extends BaseFragment {

    private Context mContext;
    private FragmentMyPostsBinding mbinding;
    private MyPostsAdapter mMyPostsAdapter;
    private List<MemoryModel> mMemoryModelList;
    private int nScrollPos = -1;

    public MyPostsFragment() {

    }

    public MyPostsFragment(int position, boolean b) {
        nScrollPos = position;
    }

    public static MyPostsFragment newInstance() {
        MyPostsFragment fragment = new MyPostsFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = mActivity;
        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_posts, container, false);
        View rootView = mbinding.getRoot();

        if(Build.VERSION.SDK_INT >= 21)
            rootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        initView();
        return rootView;
    }

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

        mMyPostsAdapter = new MyPostsAdapter(mActivity, nScrollPos, new MyPostsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int index, int type) {
                mbinding.rvPostHistory.pauseVideo();
                if(type == AppConstants.TYPE_LIKES) {
                    Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new PostLikesFragment(mMemoryModelList.get(index).getId()),mActivity, R.id.framelayout_main);
                    return;
                }

                if(type == AppConstants.TYPE_CIRCLE_IMG){
                    if(mMemoryModelList.get(index).getUser_id().intValue() != SavePref.getInstance(mActivity).getUserdetail().getId().intValue()){
                        Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new FriendProfileFragment(mMemoryModelList.get(index).getUser_id()),mActivity, R.id.framelayout_main);
                    }else{
                        Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), ProfileFragment.newInstance(), mActivity, R.id.framelayout_main);
                    }
                    return;
                }
                if(type == AppConstants.TYPE_POST_IMG){
                    mIndex = index;
                    Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), MyPostFragment.newInstance(mMemoryModelList.get(index)),mActivity, R.id.framelayout_main);
                    return;
                }
                if(type == AppConstants.TYPE_MORE_REMOVE){
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
                    builder.setTitle(getResources().getString(R.string.app_name));
                    builder.setMessage(getString(R.string.are_you_sure_want_to_remove));
                    builder.setPositiveButton(getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            callRemoveMedia(index, mMemoryModelList.get(index).getId());

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
        mbinding.rvPostHistory.setAdapter(mMyPostsAdapter);

        if(nScrollPos != -1){
            mbinding.rvPostHistory.scrollToPosition(nScrollPos);
        }

    }

    private void callRemoveMedia(int index, int id){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = ApiClass.getmApiClass().buildDefaultParams(mActivity);
        mparams.put("media_id", id);
        mparams.put("user_id", SavePref.getInstance(mActivity).getUserdetail().getId().intValue());

        Call<RemoveMediaResponse> call = mApiInterface.removeMediaService(mparams);
        call.enqueue(new Callback<RemoveMediaResponse>() {
            @Override
            public void onResponse(Call<RemoveMediaResponse> call, Response<RemoveMediaResponse> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    final RemoveMediaResponse mBasic_Response = response.body();
                    if(mBasic_Response!=null && isAdded()){
                        if (mBasic_Response.getStatus().equalsIgnoreCase("true")) {
                            mMemoryModelList.remove(index);
                            mbinding.rvPostHistory.setMemoryModels(mMemoryModelList);
                            mMyPostsAdapter.notifyDataSetChanged();
                            MessageEvent messageEvent = new MessageEvent();
                            messageEvent.setType(MessageEvent.MessageType.REMOVE_MEDIA);
                            EventBus.getDefault().post(messageEvent);
                        }
//                        Toast.makeText(mActivity,mBasic_Response.getMessage(), Toast.LENGTH_SHORT).show();
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

    private int mIndex = -1;
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent (MessageEvent messageEvent) {
        if(messageEvent.getType() == MessageEvent.MessageType.MY_POSTS){
            mMemoryModelList = messageEvent.getMemoryModelList();
            if(mMemoryModelList != null ){
                if(mMemoryModelList.size() > 0){
                    mbinding.rvPostHistory.setMemoryModels(mMemoryModelList);
                    mMyPostsAdapter.setData(mMemoryModelList);
                    mbinding.txtNoData.setVisibility(View.GONE);
                }
                else{
                    mbinding.txtNoData.setVisibility(View.VISIBLE);
                }
            }
            remvoeSticky();
        }else  if (messageEvent.getType() == MessageEvent.MessageType.REMOVE_MEDIA) {
            if(messageEvent.getMemoryModel() != null){
                mMemoryModelList.remove(messageEvent.getMemoryModel());
                mbinding.rvPostHistory.setMemoryModels(mMemoryModelList);
                mMyPostsAdapter.notifyDataSetChanged();
            }
            remvoeSticky();
        }else if(messageEvent.getType() == MessageEvent.MessageType.POST_REFRESH){
            if(mIndex != -1){
                mMemoryModelList.remove(mIndex);
                mMemoryModelList.add(mIndex, messageEvent.getMemoryModel());
                if(mMyPostsAdapter != null){
                    mMyPostsAdapter.notifyDataSetChanged();
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
        Log.e("MyPostsFragment", "onStop");
        mbinding.rvPostHistory.stopVideo();
    }

}
