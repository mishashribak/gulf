package com.app.khaleeji.Fragments;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.CameraActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Adapter.SendGroupAdapter;
import com.app.khaleeji.Adapter.SendToAdapter;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.FriendData;
import com.app.khaleeji.Response.FriendlistResponse;
import com.app.khaleeji.Response.fetchHotspotAndFrndsDetail.LastHotSpotResponse;
import com.app.khaleeji.Response.groupListPackage.GroupListReponse;
import com.app.khaleeji.Response.groupListPackage.GroupModel;
import com.app.khaleeji.databinding.FragmentSendtoBinding;
import com.app.khaleeji.services.VideoUploadService;
import com.app.khaleeji.services.UploadService;
//import com.applozic.mobicomkit.api.conversation.Message;
//import com.applozic.mobicomkit.api.conversation.MobiComConversationService;
//import com.applozic.mobicomkit.contact.AppContactService;
//import com.applozic.mobicommons.people.contact.Contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.SessionData;
import CustomView.CustomCaptionDlg;
import Utility.ApiClass;
import Utility.GlobalVariable;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendToFragment  extends BaseFragment {

    private Context mContext;
    private View mRootView;
    private FragmentSendtoBinding mbinding;
    private boolean isDailiesSel = false;
    private boolean isMemoriesSel = false;
    private boolean isHotDailiesSel = false;
    private boolean isHotMemoriesSel = false;
    private boolean isFromMindDlg = false;
    private List<FriendData> mFriendList;
    private List<GroupModel> mGroupList;
    private  SendToAdapter mFriendAdapter;
    private SendGroupAdapter mSendGroupAdapter;
    private String  videoPath;
    private String waterMarkPath;
    private String mediaLink, thumb;
    private int mediaTime;
    private boolean isVideo;
    private TransferUtility transferUtility;
    private AmazonS3 s3;
    private String finalUrl, thumbUrl="";
    private String strCaption="";
    private ArrayList<String> strSel = new ArrayList<>();

    public SendToFragment() {
        // Required empty public constructor
        isFromMindDlg = false;
    }

    public SendToFragment(boolean isFromMindDlg) {
        // Required empty public constructor
        this.isFromMindDlg = isFromMindDlg;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = mActivity;
        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sendto, container, false);
        mRootView = mbinding.getRoot();
        if(Build.VERSION.SDK_INT >= 21)
            mRootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        initView();
        return mRootView;
    }

    public void credentialsProvider() {
        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                mContext,
                ApiClass.S3_COGNITO_POOL_ID, // Identity Pool ID
                Regions.AP_SOUTHEAST_1 // Region
        );
        setAmazonS3Client(credentialsProvider);
    }

    public void setAmazonS3Client(CognitoCachingCredentialsProvider credentialsProvider) {
        // Create an S3 client
        s3 = new AmazonS3Client(credentialsProvider);
        // Set the region of your S3 bucket
        s3.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_1));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void scheduleJob(Context context, String slctnType, String videoPath,
                                   String waterMarkPath, boolean isSoundOn
            , int media_time, String media_link, String caption, boolean isFromGroupChat,
           boolean isDailiesSel, boolean isMemoriesSel, boolean isHotMemoriesSel, boolean isHotDailiesSel, int hotSpotId,
                                  List<FriendData> friendList, List<GroupModel> groupModelList) {
        PersistableBundle pb = new PersistableBundle();
        pb.putString("slectionType", slctnType);
        pb.putString("videoPath", videoPath);
        pb.putString("waterMarkPath", waterMarkPath);
        pb.putBoolean("isSoundOn", isSoundOn);
        pb.putInt("media_time", media_time);
        pb.putString("media_link", media_link);
        pb.putString("caption", caption);
//        pb.putBoolean("isFromGroupChat", isFromGroupChat);
        pb.putBoolean("isDailiesSel", isDailiesSel);
        pb.putBoolean("isMemoriesSel", isMemoriesSel);
        pb.putBoolean("isHotMemoriesSel", isHotMemoriesSel);
        pb.putBoolean("isHotDailiesSel", isHotDailiesSel);
        pb.putInt("hotSpotId", hotSpotId);
//        pb.putBoolean("isUploadAgain", false);
        int[] id = null;
        if(friendList != null && friendList.size() > 0){
            int selCount = 0;
            for(int i =0 ; i < friendList.size(); i++){
                if(friendList.get(i).isChecked()){
                    selCount++;
                }
            }
            if(selCount > 0){
                id = new int[selCount];
                int tempNo = 0;
                for(int i =0 ; i < friendList.size(); i++){
                    if(friendList.get(i).isChecked()) {
                        if(tempNo >= selCount)
                            return;
                        id[tempNo] = friendList.get(i).getId();
                        tempNo++;
                    }
                }
            }

        }
        pb.putIntArray("friendIds", id);

        String groupId="";
        if(groupModelList != null && groupModelList.size() > 0){
            for(int i =0 ; i < groupModelList.size(); i++){
                if(groupModelList.get(i).isChecked()){
                    if(i == groupModelList.size()-1){
                        groupId += groupModelList.get(i).getId();
                    }else{
                        groupId += (groupModelList.get(i).getId()+",");
                    }
                }
            }
        }
        pb.putString("groupId", groupId);

        ComponentName serviceComponent = new ComponentName(context, VideoUploadService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(1 * 1000); // wait at least
        builder.setOverrideDeadline(3 * 1000); // maximum delay
        builder.setExtras(pb);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        builder.setRequiresDeviceIdle(true); // device should be idle
        builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void scheduleImageUploadJob( Context context, String videoPath, int media_time, String media_link, String caption, boolean isFromGroupChat,
                                   boolean isDailiesSel, boolean isMemoriesSel, boolean isHotMemoriesSel, boolean isHotDailiesSel, int hotSpotId,
                                               List<FriendData> friendList, List<GroupModel> groupModelList, String originalPath) {
        PersistableBundle pb = new PersistableBundle();
        pb.putString("slectionType", "send");
        pb.putString("videoPath", videoPath);
        pb.putInt("media_time", media_time);
        pb.putString("media_link", media_link);
        pb.putString("caption", caption);
        pb.putBoolean("isFromGroupChat", isFromGroupChat);
        pb.putBoolean("isDailiesSel", isDailiesSel);
        pb.putBoolean("isMemoriesSel", isMemoriesSel);
        pb.putBoolean("isHotMemoriesSel", isHotMemoriesSel);
        pb.putBoolean("isHotDailiesSel", isHotDailiesSel);
        pb.putInt("hotSpotId", hotSpotId);
        pb.putString("originalPath", originalPath);
//        pb.putBoolean("isUploadAgain", false);

        int[] id = null;
        if(friendList != null && friendList.size() > 0){
            int selCount = 0;
            for(int i =0 ; i < friendList.size(); i++){
                if(friendList.get(i).isChecked()){
                    selCount++;
                }
            }
            if(selCount > 0){
                id = new int[selCount];
                int tempNo = 0;
                for(int i =0 ; i < friendList.size(); i++){
                    if(friendList.get(i).isChecked()) {
                        if(tempNo >= selCount)
                            return;
                        id[tempNo] = friendList.get(i).getId();
                        tempNo++;
                    }
                }
            }

        }
        pb.putIntArray("friendIds", id);

        String groupId="";
        if(groupModelList != null && groupModelList.size() > 0){
            for(int i =0 ; i < groupModelList.size(); i++){
                if(groupModelList.get(i).isChecked()){
                    if(i == groupModelList.size()-1){
                        groupId += groupModelList.get(i).getId();
                    }else{
                        groupId += (groupModelList.get(i).getId()+",");
                    }
                }
            }
        }
        pb.putString("groupId", groupId);

        ComponentName serviceComponent = new ComponentName(context, UploadService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(1 * 1000); // wait at least
        builder.setOverrideDeadline(3 * 1000); // maximum delay
        builder.setExtras(pb);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        builder.setRequiresDeviceIdle(true); // device should be idle
        builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }

    CustomCaptionDlg captionDlg;
    public void showCaptionDlg(){
        captionDlg  = new CustomCaptionDlg(mContext, new CustomCaptionDlg.ItemClickInterface() {
            @Override
            public void onClick(String str) {
                strCaption = str;
                if((isMemoriesSel || isHotMemoriesSel) && strCaption.trim().isEmpty()){
                    Toast.makeText(mContext, getString(R.string.write_a_caption_for_your_memory),Toast.LENGTH_SHORT).show();
                    return;
                }
                captionDlg.disMissDialog();
                goToNext();
            }
        });
        captionDlg.setCanceledOnTouchOutside(false);
        captionDlg.showDialog();
    }

    private void goToNext(){
//        if(mFriendList != null && mFriendList.size() > 0){
//            AppContactService appContactService = new AppContactService(mActivity);
//            for(int i =0 ; i < mFriendList.size(); i++){
//                if(mFriendList.get(i).isChecked()){
//
//                    if(!appContactService.isContactPresent(mFriendList.get(i).getId()+"")){
//                        Contact contact = new Contact();
//                        contact.setUserId(mFriendList.get(i).getId()+"");
//                        contact.setFullName(mFriendList.get(i).getFull_name());
//                        contact.setImageURL(ApiClass.ImageBaseUrl + mFriendList.get(i).getProfile_picture());
//                        appContactService.add(contact);
//                    }
//
//                    Message message = new Message();
//                    List<String> filePaths = new ArrayList<>();
//                    filePaths.add(videoPath);
//                    message.setTo(mFriendList.get(i).getId()+""); //use this for individual
//                    message.setContactIds(mFriendList.get(i).getId()+""); //use this for individual
//                    if(isVideo)
//                        message.setContentType(Message.ContentType.VIDEO_MSG.getValue());
//                    else
//                        message.setContentType(Message.ContentType.ATTACHMENT.getValue());
//
//                    message.setFilePaths(filePaths);
//
//                    new MobiComConversationService(mActivity).sendMessage(message);
//                }
//            }
//        }
//
//        String groupId="";
//        if(mGroupList != null && mGroupList.size() > 0){
//            for(int i =0 ; i < mGroupList.size(); i++){
//                if(mGroupList.get(i).isChecked()){
//                    if(i == mGroupList.size()-1){
//                        groupId += mGroupList.get(i).getId();
//                    }else{
//                        groupId += (mGroupList.get(i).getId()+",");
//                    }
//                }
//            }
//        }
//
//        if( !isDailiesSel && !isMemoriesSel && !isHotDailiesSel && !isHotMemoriesSel && groupId.isEmpty()){
//            mActivity.finish();
//            return;
//        }

        if(isVideo){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(mActivity,getString(R.string.post_media_alert),Toast.LENGTH_SHORT).show();
                }
            });

            scheduleJob(mActivity.getApplicationContext(), "send", videoPath,
                    waterMarkPath, isSoundOn, mediaTime, mediaLink, strCaption, false,
                    isDailiesSel, isMemoriesSel, isHotMemoriesSel, isHotDailiesSel, hotSpotId, mFriendList, mGroupList);
            mActivity.finish();
        }
        else{
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(mActivity,getString(R.string.post_media_alert),Toast.LENGTH_SHORT).show();
                }
            });
            scheduleImageUploadJob( mActivity.getApplicationContext(), videoPath,  mediaTime, mediaLink, strCaption,  false,
                    isDailiesSel, isMemoriesSel, isHotMemoriesSel, isHotDailiesSel, hotSpotId, mFriendList, mGroupList, originalPath);
            mActivity.finish();
        }
    }

    private boolean isSoundOn;
    private String originalPath;
    private void initView(){
        Bundle bundle = getArguments();
        if (bundle != null) {
            originalPath = bundle.getString("originalPath");
            isVideo = bundle.getBoolean("isVideo");
            mediaLink = bundle.getString("media_link");
            mediaTime = bundle.getInt("media_time");
            videoPath  = bundle.getString("destPath");
            waterMarkPath = bundle.getString("waterMarkPath");
            isSoundOn = bundle.getBoolean("isSoundOn");
        }

        mbinding.imgBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(isFromMindDlg){
                    ((MainActivity)mActivity).back();
                }else{
                    ((CameraActivity)mActivity).back();
                }

            }
        });


        mbinding.imgNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if(!isFromMindDlg){
                    if( isMemoriesSel || isHotMemoriesSel){
                        showCaptionDlg();
                    }else{
                        goToNext();
                    }
                }
            }
        });

        mbinding.llDailies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDailiesSel = !isDailiesSel;
                if(isDailiesSel) {
                    mbinding.imgDailiesCheck.setVisibility(View.VISIBLE);
                    strSel.add("My Dailies");
                }
                else {
                    mbinding.imgDailiesCheck.setVisibility(View.GONE);
                    strSel.remove("My Dailies");
                }

                showBottomSend();
            }
        });

        mbinding.llMemories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMemoriesSel = !isMemoriesSel;
                if(isMemoriesSel) {
                    strSel.add("My Memories");
                    mbinding.imgMemoriesCheck.setVisibility(View.VISIBLE);
                }
                else {
                    mbinding.imgMemoriesCheck.setVisibility(View.GONE);
                    strSel.remove("My Memories");
                }

                showBottomSend();
            }
        });

        mbinding.llHotspotDailies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHotDailiesSel = !isHotDailiesSel;
                if(isHotDailiesSel) {
                    mbinding.imgHotspotDailiesCheck.setVisibility(View.VISIBLE);
                    strSel.add(mbinding.txtHotspotDaily.getText().toString());
                }
                else {
                    mbinding.imgHotspotDailiesCheck.setVisibility(View.GONE);
                   strSel.remove(mbinding.txtHotspotDaily.getText().toString());
                }

                showBottomSend();
            }
        });

        mbinding.llHotspotMemories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHotMemoriesSel = !isHotMemoriesSel;
                if(isHotMemoriesSel) {
                    mbinding.imgHotspotMemoriesCheck.setVisibility(View.VISIBLE);
                   strSel.add(mbinding.txtHotspotMemory.getText().toString());
                }
                else {
                    mbinding.imgHotspotMemoriesCheck.setVisibility(View.GONE);
                    strSel.remove(mbinding.txtHotspotMemory.getText().toString());
                }

                showBottomSend();
            }
        });

        mSendGroupAdapter = new SendGroupAdapter(mContext, new SendGroupAdapter.OnEventClickListener() {
            @Override
            public void onClick(int pos, boolean isSelected) {
                mGroupList.get(pos).setChecked(isSelected);
                if(isSelected){
                    strSel.add( mGroupList.get(pos).getGroupName());
                }else {
                    strSel.remove( mGroupList.get(pos).getGroupName());
                }

                showBottomSend();
            }
        });
        mbinding.rvGroups.setAdapter(mSendGroupAdapter);
        mbinding.rvGroups.setLayoutManager(new LinearLayoutManager(getContext()));

        mFriendAdapter = new SendToAdapter(mContext, new SendToAdapter.OnEventClickListener() {
            @Override
            public void onClick(int pos, boolean isSelected) {
                mFriendList.get(pos).setChecked(isSelected);
                if(isSelected){
                    strSel.add( mFriendList.get(pos).getFull_name());
                }else {
                   strSel.remove( mFriendList.get(pos).getFull_name());
                }

                showBottomSend();
            }
        });
        mbinding.rvBestFriends.setAdapter(mFriendAdapter);
        mbinding.rvBestFriends.setLayoutManager(new LinearLayoutManager(getContext()));

        mbinding.searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mFriendAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if(GlobalVariable.isFloatingDaily){
            mbinding.cardDailies.setVisibility(View.VISIBLE);
            mbinding.cardMemories.setVisibility(View.GONE);
        }else {
            mbinding.cardDailies.setVisibility(View.GONE);
            mbinding.cardMemories.setVisibility(View.VISIBLE);
        }

//        getMyfriendList();
        getLastHotspot();
        callGetGroups();

//        SendToRecentsAdapter adapterRecents = new SendToRecentsAdapter(mContext);
//        mbinding.rvRecents.setAdapter(adapterRecents);
//        mbinding.rvRecents.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    int hotSpotId = -1;
    private void getLastHotspot(){
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userid = SavePref.getInstance(mActivity).getUserdetail().getId();
       
        Call<LastHotSpotResponse> call = mApiInterface.getLastHotspot(userid);
        call.enqueue(new Callback<LastHotSpotResponse>() {
            @Override
            public void onResponse(Call<LastHotSpotResponse> call, Response<LastHotSpotResponse> response) {
//                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        LastHotSpotResponse  lasthotspot = response.body();
                        if(lasthotspot != null) {
                            if (lasthotspot.getStatus().equalsIgnoreCase("true") && lasthotspot.getData() != null) {
                                if(GlobalVariable.isFloatingDaily){
                                    mbinding.llHotspotDailies.setVisibility(View.VISIBLE);
                                }else {
                                    mbinding.llHotspotMemories.setVisibility(View.VISIBLE);
                                }

                                String name =lasthotspot.getData().getLocationName();
                                mbinding.txtHotspotMemory.setText(name+" "+getResources().getString(R.string.memories));
                                mbinding.txtHotspotDaily.setText(name+" "+getResources().getString(R.string.dailies));
                                hotSpotId = lasthotspot.getData().getId();
                            } else {
                                if(GlobalVariable.isFloatingDaily){
                                    mbinding.llHotspotDailies.setVisibility(View.GONE);
                                }else {
                                    mbinding.llHotspotMemories.setVisibility(View.GONE);
                                }

                            }
                            mbinding.scrollView.smoothScrollTo(0,0);
                        }

                    } else {
                        if(GlobalVariable.isFloatingDaily){
                            mbinding.llHotspotDailies.setVisibility(View.GONE);
                        }else {
                            mbinding.llHotspotMemories.setVisibility(View.GONE);
                        }
                        System.out.println(response.errorBody());
                    }
                }
            }

            @Override
            public void onFailure(Call<LastHotSpotResponse> call, Throwable t) {
//                ProgressDialog.hideprogressbar();
            }
        });
    }

    private void getMyfriendList() {
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
//                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        FriendlistResponse  lasthotspot = response.body();
                        if(lasthotspot != null) {
                            if (lasthotspot.getStatus().equalsIgnoreCase("true")) {
                                if (lasthotspot.getData() != null && lasthotspot.getData().getData().size() > 0) {
                                    mbinding.txtBestFriends.setVisibility(View.VISIBLE);
                                    mbinding.friendsCard.setVisibility(View.VISIBLE);
                                    mFriendList = lasthotspot.getData().getData();
                                    mFriendAdapter.setData(mFriendList);
//                                    mbinding.txtNoData.setVisibility(View.GONE);
                                } else {
                                    mbinding.txtBestFriends.setVisibility(View.GONE);
                                    mbinding.friendsCard.setVisibility(View.GONE);
//                                    mbinding.txtNoData.setVisibility(View.VISIBLE);
                                }
                                mbinding.scrollView.smoothScrollTo(0,0);
                            }
                        }

                    } else {
                        mbinding.txtBestFriends.setVisibility(View.GONE);
                        mbinding.friendsCard.setVisibility(View.GONE);
                        System.out.println(response.errorBody());
                    }
                }
            }

            @Override
            public void onFailure(Call<FriendlistResponse> call, Throwable t) {
//                ProgressDialog.hideprogressbar();
            }
        });
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
                        GroupListReponse mGroupListReponse = response.body();
                        if(mGroupListReponse != null){
                            if(mGroupListReponse.getStatus().equalsIgnoreCase("true")){
                                if ( mGroupListReponse.getData() != null && mGroupListReponse.getData().size() > 0) {
                                    mGroupList = mGroupListReponse.getData();
                                    mSendGroupAdapter.setData(mGroupList);
                                    mbinding.llGroups.setVisibility(View.VISIBLE);
                                } else {
                                    mbinding.llGroups.setVisibility(View.GONE);
                                }
                            }else{
                                Toast.makeText(mActivity, mGroupListReponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            mbinding.scrollView.smoothScrollTo(0,0);
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


    private void showBottomSend(){
        boolean isSelectedFriend = false;
        boolean isSelectedGroup =false;
        if(mFriendList != null){
            for(int i=0; i< mFriendList.size();i++){
                if(mFriendList.get(i).isChecked()){
                    isSelectedFriend = true;
                    break;
                }
            }
        }

        if(mGroupList != null){
            for(int i=0; i< mGroupList.size();i++){
                if(mGroupList.get(i).isChecked()){
                    isSelectedGroup = true;
                    break;
                }
            }
        }

        if(isDailiesSel || isMemoriesSel || isHotDailiesSel || isHotMemoriesSel || isSelectedFriend || isSelectedGroup ){
            mbinding.llBottomSend.setVisibility(View.VISIBLE);
            String str="";
            if(strSel.size() == 1){
                  str = strSel.get(0);
            }else if(strSel.size() > 1){
                for(int i= strSel.size()-1; i>= strSel.size()-2; i--){
                    if(i == strSel.size()-2) {
                        str += strSel.get(i);
                    }else{
                        str += strSel.get(i)+", ";
                    }

                }
            }

            mbinding.txtSelInfo.setText(str);

        }else{
            mbinding.llBottomSend.setVisibility(View.INVISIBLE);
        }
    }


}
