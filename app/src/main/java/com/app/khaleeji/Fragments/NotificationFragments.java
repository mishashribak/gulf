package com.app.khaleeji.Fragments;

import androidx.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Adapter.NotificationAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.FriendlistResponse;
import com.app.khaleeji.Response.NotificationResponse;
import com.app.khaleeji.Response.Notification_Datum;
import com.app.khaleeji.databinding.FragmentNotificationBinding;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Utility.ApiClass;
import Utility.DebugLog;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static Constants.AppConstants.loadedNotificationTab;

public class NotificationFragments extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{

    static String TAG = NotificationFragments.class.getName();
    private FragmentNotificationBinding mbinding;
    private NotificationAdapter mNotificationAdapter;
    private static List<Notification_Datum> notificationList;

    public static NotificationFragments newInstance() {
        NotificationFragments viewerListFragments  = new NotificationFragments();
        return viewerListFragments;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false);
        View view = mbinding.getRoot();

        if(Build.VERSION.SDK_INT >= 21)
            view.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        SavePref.getInstance(mActivity).setShowmessageReminder(false);
        initView();
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent (MessageEvent messageEvent) {
        if(messageEvent.getType() == MessageEvent.MessageType.NEW_MSG_NOTIFICATION){
            mbinding.imgBadge.setVisibility(View.VISIBLE);
        }else if(messageEvent.getType() == MessageEvent.MessageType.TOP_SCROLL){
            mbinding.rvNotifications.setVerticalScrollbarPosition(0);
        }
    }

    private void initView(){
        ((MainActivity)mActivity).hide();

        if(SavePref.getInstance(mActivity).getNewMsgNotification()){
            mbinding.imgBadge.setVisibility(View.VISIBLE);
        }else{
            mbinding.imgBadge.setVisibility(View.INVISIBLE);
        }

        mbinding.llRequestNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), new MyFriendFragment(true), mActivity, R.id.framelayout_main);
            }
        });
        
        mbinding.swipeRefresh.setOnRefreshListener(this);
        mbinding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mbinding.rvNotifications.setLayoutManager(linearLayoutManager);
        mNotificationAdapter = new NotificationAdapter(mActivity, new NotificationAdapter.OnClickListener() {
            @Override
            public void onClick(int pos, int type) {
                if(notificationList != null){
                    Notification_Datum notification_datum= notificationList.get(pos);
                    if(type == 0){
                        Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new FriendProfileFragment(notification_datum.getFromUserId()),mActivity, R.id.framelayout_main);
                    }else{
                        switch (notification_datum.getType()){
                            case 1:
                            case 3:
                                Fragment_Process.addFragment(mActivity.getSupportFragmentManager(),new FriendDetailPostFragment(notification_datum.getMedia_id(), notification_datum.getFromUserId()),mActivity, R.id.framelayout_main);
                                break;
                            case 7:
                                Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), MsgContactFragment.newInstance(), mActivity, R.id.framelayout_main);
                                break;
                        }
                    }
                }
            }
        });
        mbinding.rvNotifications.setAdapter(mNotificationAdapter);
        mbinding.imgMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).openMenu();
            }
        });
        mbinding.imgSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).openSearchTabFragment();
            }
        });
        mbinding.imgMessage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).openMessageFragment();
            }
        });

        SavePref.getInstance(mActivity).setNotificationCount(0);
        getReceivedRequest();

        if(!loadedNotificationTab){
            callGetNotifications();
        }else{
            initFromApi();
        }

    }

    private void callGetNotifications() {
//        ProgressDialog.showProgress(mActivity);
        mbinding.swipeRefresh.setRefreshing(true);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userid = SavePref.getInstance(mActivity).getUserdetail().getId();
        Call<NotificationResponse> call = mApiInterface.getNotifications(userid);
        call.enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
//                ProgressDialog.hideprogressbar();
                mbinding.swipeRefresh.setRefreshing(false);
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        NotificationResponse notification = response.body();
                        if(notification!=null) {
                            if (notification.getStatus().equalsIgnoreCase("true")) {
                                loadedNotificationTab = true;
                                notificationList = notification.getData();
                                initFromApi();
                            } else {
                                Toast.makeText(mActivity,notification.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    } else {
                        System.out.println(response.errorBody());
                    }
                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
//                ProgressDialog.hideprogressbar();
                mbinding.swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void initFromApi(){
        if (notificationList != null && notificationList.size() > 0) {
            mNotificationAdapter.setData(notificationList);
            mbinding.txtNoData.setVisibility(View.GONE);
        } else {
            mbinding.txtNoData.setVisibility(View.VISIBLE);
        }
    }

    private void getReceivedRequest() {
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userid = SavePref.getInstance(mActivity).getUserdetail().getId();
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().REQUEST_TYPE, "received");
        mparams.put(ApiClass.getmApiClass().USER_ID, String.valueOf(userid));
        String token = SavePref.getInstance(mActivity).getUserdetail().getToken();
        Call<FriendlistResponse> call = mApiInterface.friendRequestList("Bearer "+token,mparams);
        call.enqueue(new Callback<FriendlistResponse>() {
            @Override
            public void onResponse(Call<FriendlistResponse> call, Response<FriendlistResponse> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        FriendlistResponse  friendlistResponse = response.body();
                        if(friendlistResponse != null) {
                            if (friendlistResponse.getStatus().equalsIgnoreCase("true")) {
                                if (friendlistResponse.getData() != null && friendlistResponse.getData().getData().size() > 0) {
                                    mbinding.llRequestNumbers.setVisibility(View.VISIBLE);
                                    int requests = friendlistResponse.getData().getData().size();
                                    mbinding.txtReceivedRequest.setText(requests+"");
                                }else{
                                    mbinding.llRequestNumbers.setVisibility(View.GONE);
                                }
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
                DebugLog.log(1, TAG, "onResponse" + t.getMessage());
            }
        });
    }

    @Override
    public void onRefresh() {
        getReceivedRequest();
        callGetNotifications();
    }
}
