package com.app.khaleeji.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Adapter.FriendStatusHistoryAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.databinding.FragmentFriendStatusBinding;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.AppConstants;
import Model.StatusModel;
import Utility.ApiClass;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendStatusFragment extends BaseFragment {
    private FragmentFriendStatusBinding mbinding;
    private List<StatusModel> statusModelList;
    private FriendStatusHistoryAdapter friendStatusHistoryAdapter;
    private boolean isFromMe;
    private int fromUserId;
    private boolean isFromNotification;
    private String name;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent (MessageEvent messageEvent) {
        if(messageEvent.getType() == MessageEvent.MessageType.STATUS_REFRESH){
            isFromNotification = true;
            callGetStatusHistory();
            return;
        }

        if(messageEvent.getType() == MessageEvent.MessageType.NEW_MSG_NOTIFICATION){
            mbinding.imgBadge.setVisibility(View.VISIBLE);
        }
    }

    public FriendStatusFragment() {

    }

    public FriendStatusFragment(boolean isFromMe, int fromUserId, String name) {
        this.isFromMe = isFromMe;
        this.fromUserId = fromUserId;
        this.name = name;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_friend_status, container, false);
        View rootView = mbinding.getRoot();

        if(Build.VERSION.SDK_INT >= 21)
            rootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        initView();
        return rootView;
    }

    private void initView(){
        ((MainActivity)mActivity).hide();
        mbinding.txtFriendStatusTitle.setText( name +" "+getResources().getString(R.string.status));
        mbinding.imgBack.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                ((MainActivity)mActivity).back();
            }
        });

        if(SavePref.getInstance(mActivity).getNewMsgNotification()){
            mbinding.imgBadge.setVisibility(View.VISIBLE);
        }else{
            mbinding.imgBadge.setVisibility(View.INVISIBLE);
        }

        mbinding.imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)mActivity).openSearchTabFragment();
            }
        });

        mbinding.imgMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)mActivity).openMessageFragment();
            }
        });

        friendStatusHistoryAdapter = new FriendStatusHistoryAdapter(mActivity, isFromMe, new FriendStatusHistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(StatusModel statusModel, int type) {
                if(type == AppConstants.TYPE_LIKES){
                    callLikeStatus(statusModel);
                    return;
                }
                if(type == AppConstants.TYPE_CIRCLE_IMG){
                    if(statusModel.user_id != null){
                        if( !statusModel.user_id.equals(String.valueOf(SavePref.getInstance(mActivity).getUserdetail().getId()))){
                            Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new FriendProfileFragment(Integer.parseInt(statusModel.user_id)), mActivity, R.id.framelayout_main);
                        }else{
                            Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), ProfileFragment.newInstance(), mActivity, R.id.framelayout_main);
                        }
                    }
                    return;
                }

                if(type == AppConstants.TYPE_MORE_REMOVE){
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mActivity, R.style.MyAlertDialogStyle);
                    builder.setTitle(getResources().getString(R.string.app_name));
                    builder.setMessage(getString(R.string.are_you_sure_want_to_remove_status));
                    builder.setPositiveButton(getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            callRemoveStatus(statusModel);
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

                if(type == AppConstants.TYPE_MORE_REPORT){
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mActivity, R.style.MyAlertDialogStyle);
                    builder.setTitle(getResources().getString(R.string.app_name));
                    builder.setMessage(getString(R.string.are_you_sure_want_to_report_status));
                    builder.setPositiveButton(getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            callReportStatus(statusModel);
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

                if(type == -1){
                    Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new FriendStatusDetailFragment(statusModel, isFromMe), mActivity, R.id.framelayout_main);
                    return;
                }
            }

            @Override
            public void onLikers(StatusModel statusModel) {
                Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new StatusLikesFragment(statusModel), mActivity, R.id.framelayout_main);
            }

            @Override
            public void onViewers(StatusModel statusModel) {
                Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new StatusViewersFragment(statusModel), mActivity, R.id.framelayout_main);
            }

        });
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mbinding.rvStatusHistory.setLayoutManager(mLayoutManager);
        mbinding.rvStatusHistory.setAdapter(friendStatusHistoryAdapter);

        callGetStatusHistory();
    }


    private void callGetStatusHistory(){
        if(!isFromNotification)
            ProgressDialog.showProgress(mActivity);
        ApiInterface apiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getStatusHistory(fromUserId, SavePref.getInstance(mActivity).getUserdetail().getId());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(!isFromNotification)
                    ProgressDialog.hideprogressbar();
                isFromNotification = false;
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject != null) {
                        if (jsonObject.get("status").getAsBoolean()) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<StatusModel>>() {}.getType();
                            try{
                                statusModelList = gson.fromJson(jsonObject.get("data").getAsJsonArray(), type);
                                if(statusModelList != null && statusModelList.size() > 0){
                                    friendStatusHistoryAdapter.setData(statusModelList);
                                    mbinding.txtNoData.setVisibility(View.GONE);
                                }else{
                                    mbinding.txtNoData.setVisibility(View.VISIBLE);
                                    friendStatusHistoryAdapter.setData(null);
                                }
                            }catch (JsonSyntaxException ex){
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if(!isFromNotification)
                    ProgressDialog.hideprogressbar();
                isFromNotification = false;
            }
        });
    }

    private void callRemoveStatus(StatusModel statusModel){
        ProgressDialog.showProgress(mActivity);
        ApiInterface apiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.removeStatus(statusModel.status_id);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject != null) {
                        if (jsonObject.get("status").getAsBoolean()) {
                            statusModelList.remove(statusModel);
                            friendStatusHistoryAdapter.notifyDataSetChanged();

                            MessageEvent messageEvent = new MessageEvent();
                            messageEvent.setType(MessageEvent.MessageType.PROFILE_REFRESH);
                            EventBus.getDefault().post(messageEvent);
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

    private void callLikeStatus(StatusModel statusModel){
        ProgressDialog.showProgress(mActivity);
        ApiInterface apiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();
        mparams.put("user_id", SavePref.getInstance(mActivity).getUserdetail().getId());
        mparams.put("like", !statusModel.likeBefore ? "1" : "0");
        mparams.put("status_id", statusModel.status_id);
        Call<JsonObject> call = apiInterface.likeStatus(mparams);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject != null) {
                        if (jsonObject.get("status").getAsBoolean()) {
                            statusModel.likeBefore = !statusModel.likeBefore;

                            if(statusModel.likeBefore)
                                statusModel.likes_count = statusModel.likes_count + 1;
                            else
                                statusModel.likes_count = statusModel.likes_count - 1;

                            friendStatusHistoryAdapter.notifyDataSetChanged();
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

    private void callReportStatus(StatusModel statusModel){
        ProgressDialog.showProgress(mActivity);
        ApiInterface apiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();
        mparams.put("user_id", SavePref.getInstance(mActivity).getUserdetail().getId());
//        mparams.put("reason", "");
        mparams.put("status_id", statusModel.status_id);
        Call<JsonObject> call = apiInterface.reportStatus(mparams);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject != null) {
                        if (jsonObject.get("status").getAsBoolean()) {

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
}
