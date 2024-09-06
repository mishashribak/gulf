package com.app.khaleeji.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.FriendDailiesViewActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Adapter.DailyMemoryAdapter;
import com.app.khaleeji.Adapter.DailyUserAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.MemoryModel;
import com.app.khaleeji.Response.MemoryUpdate;
import com.app.khaleeji.Response.RemoveMediaResponse;
import com.app.khaleeji.Response.fetchDailiesOfFriends.FriendListDailiesOfFriends;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.AppConstants;
import CustomView.CustomTextView;
import CustomView.Video.VerticalSpacingItemDecorator;
import CustomView.Video.VideoPlayerRecyclerView;
import Utility.ApiClass;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static Constants.AppConstants.loadedHomeAllTab;
import static Constants.AppConstants.loadedHomeFriendTab;
import static Constants.AppConstants.strAllTab;
import static Constants.AppConstants.strFriendTab;
import static com.app.khaleeji.BroadCast.MessageEvent.MessageType.DAILY_REFRESH;

public class HomeFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private View view;
    private LinearLayout worldActiveTab ;
    private LinearLayout friendsActiveTab;
    private DailyUserAdapter dailyUserAdapter;
    private DailyMemoryAdapter dailyMemoryAdapter;
    private CustomTextView txtNoDataUser, txtNoDataMemory;
    private String mStrType = strAllTab;
    private static List<FriendListDailiesOfFriends> friendListsAllTab;
    private static List<MemoryModel> mMemoryModelListAllTab = new ArrayList<>();
    private static List<FriendListDailiesOfFriends> friendListsFriendTab;
    private static List<MemoryModel> mMemoryModelListFriendTab = new ArrayList<>();
    private MemoryUpdate memoryUpdate;
    private VideoPlayerRecyclerView rvMemory;
    private int mIndex = -1;
    private ImageView imgBadge;
    private ProgressBar progressBarLoadMore;
    private SwipeRefreshLayout swipeRefresh;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }
    
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent (MessageEvent messageEvent) {
        if(messageEvent.getType() == MessageEvent.MessageType.POST_REFRESH){
            if(mIndex != -1){
                if(mStrType.equals(strAllTab)){
                    mMemoryModelListAllTab.remove(mIndex);
                    mMemoryModelListAllTab.add(mIndex, messageEvent.getMemoryModel());
                }else{
                    mMemoryModelListFriendTab.remove(mIndex);
                    mMemoryModelListFriendTab.add(mIndex, messageEvent.getMemoryModel());
                }

                if(dailyMemoryAdapter != null){
                    dailyMemoryAdapter.notifyDataSetChanged();
                }
            }
            removeSticky();
        }else if(messageEvent.getType() == MessageEvent.MessageType.NEW_MSG_NOTIFICATION){
            imgBadge.setVisibility(View.VISIBLE);
        }else if(messageEvent.getType() == DAILY_REFRESH){
            int index = messageEvent.selectedFriendIndex;
            if(mStrType.equals(strAllTab)){
                if(index > -1 && index < friendListsAllTab.size()){
                    friendListsAllTab.get(index).setProfilePicture("/public/Reply.png");
                    rvMemory.setMemoryModels(mMemoryModelListAllTab);
                    dailyMemoryAdapter.setData(mMemoryModelListAllTab, friendListsAllTab, true);
                }
            }else{
                if(index > -1 && index < friendListsFriendTab.size()){
                    friendListsFriendTab.get(index).setProfilePicture("/public/Reply.png");
                    rvMemory.setMemoryModels(mMemoryModelListFriendTab);
                    dailyMemoryAdapter.setData(mMemoryModelListFriendTab, friendListsFriendTab, true);
                }
            }
        }else if(messageEvent.getType() == MessageEvent.MessageType.TOP_SCROLL){
            if(rvMemory != null)
                rvMemory.setVerticalScrollbarPosition(0);
        }
    }

    public void removeSticky(){
        MessageEvent stickyEvent = EventBus.getDefault().getStickyEvent(MessageEvent.class);
        if(stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent(stickyEvent);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        if(Build.VERSION.SDK_INT >= 21)
            view.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        initView();
        return view;
    }

    private void setActiveWorld(){
        worldActiveTab.setVisibility(View.VISIBLE);
        friendsActiveTab.setVisibility(View.GONE);
        mStrType = strAllTab;
        loadData();
    }

    private void setActiveFriends(){
        worldActiveTab.setVisibility(View.GONE);
        friendsActiveTab.setVisibility(View.VISIBLE);
        mStrType = strFriendTab;
        loadData();
    }

    private void initView() {
        ((MainActivity)mActivity).hide();
        progressBarLoadMore = view.findViewById(R.id.progressLoadMore);
        imgBadge = view.findViewById(R.id.imgBadge);
        if(SavePref.getInstance(mActivity).getNewMsgNotification()){
            imgBadge.setVisibility(View.VISIBLE);
        }else{
            imgBadge.setVisibility(View.INVISIBLE);
        }

        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        ImageView imgMenu = view.findViewById(R.id.imgMenu);
        imgMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).openMenu();
            }
        });
        ImageView imgSearch = view.findViewById(R.id.imgSearch);
        imgSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).openSearchTabFragment();
            }
        });
        ImageView imgMessage = view.findViewById(R.id.imgMessage);
        imgMessage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).openMessageFragment();
            }
        });

        txtNoDataUser = view.findViewById(R.id.txtNoDataUser);
        txtNoDataMemory = view.findViewById(R.id.txtNoDataMemory);

        CustomTextView txtWorld = view.findViewById(R.id.txtWorld);
        CustomTextView txtWorldB = view.findViewById(R.id.txtWorldB);
        CustomTextView txtFriends = view.findViewById(R.id.txtFriend);
        CustomTextView txtFriendsB = view.findViewById(R.id.txtFriendsB);
        worldActiveTab = view.findViewById(R.id.worldActiveTab);
        friendsActiveTab = view.findViewById(R.id.friendsActiveTab);
        txtWorld.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               setActiveWorld();
            }
        });
        txtWorldB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setActiveWorld();
            }
        });

        txtFriends.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setActiveFriends();
            }
        });
        txtFriendsB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setActiveFriends();
            }
        });

        rvMemory = view.findViewById(R.id.rvMemoryUpdates);

        dailyUserAdapter = new DailyUserAdapter(getContext(), new DailyUserAdapter.OnGroupUserClickListener() {
            @Override
            public void onGroupUserClick(int index) {
                rvMemory.stopVideo();
                if(mStrType.equals(strAllTab)){
                    if (friendListsAllTab != null && friendListsAllTab.size() > 0 && friendListsAllTab.get(index) != null
                            && friendListsAllTab.get(index).getMedia() != null && friendListsAllTab.get(index).getMedia().size() > 0){
                        startActivity(new Intent(mActivity, FriendDailiesViewActivity.class));
                        MessageEvent messageEvent = new MessageEvent();
                        messageEvent.setType(MessageEvent.MessageType.FRIEND_DAILIES_VIEW);
                        messageEvent.setFriendLists(friendListsAllTab, index);
                        EventBus.getDefault().postSticky(messageEvent);
                    }
                }else{
                    if (friendListsFriendTab != null && friendListsFriendTab.size() > 0 && friendListsFriendTab.get(index) != null
                            && friendListsFriendTab.get(index).getMedia() != null && friendListsFriendTab.get(index).getMedia().size() > 0){
                        startActivity(new Intent(mActivity, FriendDailiesViewActivity.class));
                        MessageEvent messageEvent = new MessageEvent();
                        messageEvent.setType(MessageEvent.MessageType.FRIEND_DAILIES_VIEW);
                        messageEvent.setFriendLists(friendListsFriendTab, index);
                        EventBus.getDefault().postSticky(messageEvent);
                    }
                }
            }

            @Override
            public void onHotspotClick(int index){
                Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), new HotSpotDetailsFragment(), mActivity, R.id.framelayout_main);
            }
        });

        dailyMemoryAdapter = new DailyMemoryAdapter(getContext(), new DailyMemoryAdapter.OnDailyItemClickListener() {
            @Override
            public void onItemClick(int index, int type) {
                rvMemory.stopVideo();

                List<MemoryModel> memoryModelList;
                if(mStrType.equals(strAllTab)){
                    memoryModelList = mMemoryModelListAllTab;
                }else{
                    memoryModelList = mMemoryModelListFriendTab;
                }

                if(type == -1){
                    if(index == 0){
//                        Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new FriendStatusFragment(), mActivity, R.id.framelayout_main);
                    }else {
                        mIndex = index;
                        Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new FriendDetailPostFragment(memoryModelList.get(index)), mActivity, R.id.framelayout_main);
                    }
                    return;
                }

                switch(type){
                    case AppConstants.TYPE_LIKES:
                        Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new PostLikesFragment(memoryModelList.get(index).getId()),mActivity, R.id.framelayout_main);
                        break;
                    case AppConstants.TYPE_CIRCLE_IMG:
                        if(memoryModelList.get(index).getUser_id().intValue() != SavePref.getInstance(mActivity).getUserdetail().getId().intValue()){
                            Fragment_Process.addFragment(mActivity.getSupportFragmentManager(),new FriendProfileFragment(memoryModelList.get(index).getUser_id()),mActivity, R.id.framelayout_main);
                        }else{
                            Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), ProfileFragment.newInstance(), mActivity, R.id.framelayout_main);
                        }
                        break;
                    case AppConstants.TYPE_COMMENTS:
                        Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), new FriendDetailPostFragment(memoryModelList.get(index)),mActivity, R.id.framelayout_main);
                        break;
                    case AppConstants.TYPE_MORE_REPORT:
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mActivity, R.style.MyAlertDialogStyle);
                        builder.setTitle(getResources().getString(R.string.app_name));
                        builder.setMessage(getString(R.string.are_you_sure_want_to_report));
                        builder.setPositiveButton(getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                callReportMedia(index, memoryModelList.get(index).getId());
                            }
                        });
                        builder.setNegativeButton(getString(R.string.txt_no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                        break;
                    case AppConstants.TYPE_VIEWS:
                        Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new ViewedUsersFragment(memoryModelList.get(index).getId()), mActivity, R.id.framelayout_main);
                        break;
                }
            }
        }, dailyUserAdapter);

        initRecyclerView();

        loadData();
                
    }
    
    private void loadData(){
        rvMemory.stopVideo();
        if(mStrType.equals(strAllTab)){
            if(!loadedHomeAllTab){
                mMemoryModelListAllTab.clear();
                callGetDailies();
                callGetMemories(1);
            }else{
                rvMemory.setMemoryModels(mMemoryModelListAllTab);
                dailyMemoryAdapter.setData(mMemoryModelListAllTab, friendListsAllTab, false);
            }
        }else{
            if(!loadedHomeFriendTab){
                mMemoryModelListFriendTab.clear();
                callGetDailies();
                callGetMemories(1);
            }else{
                rvMemory.setMemoryModels(mMemoryModelListFriendTab);
                dailyMemoryAdapter.setData(mMemoryModelListFriendTab, friendListsFriendTab, false);
            }
        }
    }

    @Override
    public void onRefresh() {
        callGetDailies();
        callGetMemories(1);
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

    private int lastPageNo = -1;
    private int currentPageNo;
    private void initRecyclerView(){
        currentPageNo = 1;
        progressBarLoadMore.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        rvMemory.setLayoutManager(layoutManager);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        rvMemory.addItemDecoration(itemDecorator);
        rvMemory.setAdapter(dailyMemoryAdapter);

        rvMemory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(!recyclerView.canScrollVertically(1)){
                        if(lastPageNo == currentPageNo)
                            return;
                        currentPageNo++;
                        loadNextDataFromApi(currentPageNo);
                    }
                }
            }
        });
    }


    private void loadNextDataFromApi(int page) {
        progressBarLoadMore.setVisibility(View.VISIBLE);
        callGetMemories(page);
    }
    
    private void reset(){
        rvMemory.releasePlayers();
        currentPageNo = 1;
        progressBarLoadMore.setVisibility(View.GONE);
        if(mStrType.equals(strAllTab)){
            mMemoryModelListAllTab.clear();
        }else{
            mMemoryModelListFriendTab.clear();
        }
    }

    private void callGetDailies(){
        reset();
//        ProgressDialog.showProgress(mActivity);
        swipeRefresh.setRefreshing(true);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map params = new HashMap();
        params.put(ApiClass.getmApiClass().USER_ID, SavePref.getInstance(mActivity).getUserdetail().getId().toString());
        params.put(ApiClass.getmApiClass().TYPE, mStrType);
        Call<JsonObject> call = mApiInterface.getDailies( params );
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject != null && isAdded()) {
                        if (jsonObject.get("status").getAsBoolean()) {
                            JsonArray jsonArray = jsonObject.getAsJsonArray("media");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<FriendListDailiesOfFriends>>() {}.getType();
                            try{
                                if(mStrType.equals(strAllTab)){
                                    if(friendListsAllTab != null)
                                        friendListsAllTab.clear();
                                    friendListsAllTab = gson.fromJson(jsonArray, type);
                                    dailyMemoryAdapter.setData(mMemoryModelListAllTab, friendListsAllTab, false);
                                }else{
                                    if(friendListsFriendTab != null)
                                        friendListsFriendTab.clear();
                                    friendListsFriendTab = gson.fromJson(jsonArray, type);
                                    dailyMemoryAdapter.setData(mMemoryModelListFriendTab, friendListsFriendTab, false);
                                }
                            }catch (JsonSyntaxException ex){
                                ex.printStackTrace();
                            }

                        }
                    }

                } else {
                    swipeRefresh.setRefreshing(false);
//                    ProgressDialog.hideprogressbar();
                    System.out.println(response.errorBody());
                }

            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
//                ProgressDialog.hideprogressbar();
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void callGetMemories(int page){
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map params = new HashMap();
        params.put(ApiClass.getmApiClass().USER_ID, SavePref.getInstance(mActivity).getUserdetail().getId().toString());
        params.put(ApiClass.getmApiClass().TYPE, mStrType);
        params.put("page", page);
        Call<JsonObject> call = mApiInterface.getMemories( params );
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                ProgressDialog.hideprogressbar();
                swipeRefresh.setRefreshing(false);
                progressBarLoadMore.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject != null && isAdded()) {
                        if (jsonObject.get("status").getAsBoolean()) {
                            JsonObject memoryObject = jsonObject.getAsJsonObject("memories");
                            Gson gson = new Gson();
                            Type type = new TypeToken<MemoryUpdate>() {}.getType();
                            try{
                                memoryUpdate= gson.fromJson(memoryObject, type);

                                if(mStrType.equals(strAllTab)){
                                    loadedHomeAllTab = true;
                                    if(memoryUpdate != null) {
                                        lastPageNo = memoryUpdate.getLastPage();
                                        if(page  == 1){
                                            mMemoryModelListAllTab.clear();
                                        }
                                        mMemoryModelListAllTab.addAll(memoryUpdate.getData());

                                        rvMemory.setMemoryModels(mMemoryModelListAllTab);
                                        dailyMemoryAdapter.setData(mMemoryModelListAllTab, friendListsAllTab, false);
                                        txtNoDataMemory.setVisibility(View.GONE);
                                    } else {
                                        txtNoDataMemory.setVisibility(View.VISIBLE);
                                    }
                                }else{
                                    loadedHomeFriendTab = true;
                                    if(memoryUpdate != null) {
                                        lastPageNo = memoryUpdate.getLastPage();
                                        if(page  == 1){
                                            mMemoryModelListFriendTab.clear();
                                        }
                                        mMemoryModelListFriendTab.addAll(memoryUpdate.getData());

                                        rvMemory.setMemoryModels(mMemoryModelListFriendTab);
                                        dailyMemoryAdapter.setData(mMemoryModelListFriendTab, friendListsFriendTab, false);
                                        txtNoDataMemory.setVisibility(View.GONE);
                                    } else {
                                        txtNoDataMemory.setVisibility(View.VISIBLE);
                                    }
                                }

                            }catch (JsonSyntaxException ex){
                                ex.printStackTrace();
                            }

                        }
                    }

                } else {
                    System.out.println(response.errorBody());
                }

            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
//                ProgressDialog.hideprogressbar();
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        rvMemory.stopVideo();
        rvMemory.releasePlayers();
    }
}