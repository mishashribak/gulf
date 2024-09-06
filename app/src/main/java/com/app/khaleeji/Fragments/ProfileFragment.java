package com.app.khaleeji.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Activity.MyDailiesViewActivity;
import com.app.khaleeji.Adapter.ProfileMemoriesGridAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.GulfProfileResponse;
import com.app.khaleeji.Response.GulfProfiledata;
import com.app.khaleeji.databinding.FragmentProfileBinding;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.AppConstants;
import Constants.SessionData;
import CustomView.CustomGridView;
import CustomView.CustomTextView;
import Model.QAData;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.ApiClass;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static Constants.AppConstants.loadedProfileTab;

public class ProfileFragment  extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{

    private Context mContext;
    private ScrollView svinfo;
    private View mRootView;
    private CustomGridView mGridHotMemory;
    private ImageView mImgProfileEdit;
    private ImageView imgMyProfile, imgMyDailiesView;
    private CustomTextView mTxtCountry;
    private CustomTextView mTxtGender;
    private CustomTextView mTxtBio, mTxtDOB;
    private CustomTextView mTxtProfileStatus;
    private CustomTextView mTxtLocation, txtNoData;
    private CustomTextView txtMyDailies, txtUploadInfo;
    private ProfileMemoriesGridAdapter mProfileMemoriesGridAdapter;
    private LinearLayout llMemories, llUploadInfo;
    private boolean isNotification = false;
    private ImageView imgBadge;
    private SwipeRefreshLayout swipeRefresh;
    private FragmentProfileBinding mbinding;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = mActivity;
        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        mRootView = mbinding.getRoot();
        if(Build.VERSION.SDK_INT >= 21)
            mRootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        initView();
        return mRootView;
    }

    private void initView(){
        ((MainActivity)mActivity).hide();

        mbinding.llShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new MyAccountCodeFragment(gulfProfileData), mActivity, R.id.framelayout_main);
            }
        });

        mbinding.txtMyFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), new MyFriendFragment(), mActivity, R.id.framelayout_main);
            }
        });

        mbinding.statusBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), new FriendStatusFragment(true, SavePref.getInstance(mActivity).getUserdetail().getId(),
                        SavePref.getInstance(mActivity).getUserdetail().getUsername()), mActivity, R.id.framelayout_main);
            }
        });

        swipeRefresh = mRootView.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        imgBadge = mRootView.findViewById(R.id.imgBadge);
        if(SavePref.getInstance(mActivity).getNewMsgNotification()){
            imgBadge.setVisibility(View.VISIBLE);
        }else{
            imgBadge.setVisibility(View.INVISIBLE);
        }

        txtUploadInfo = mRootView.findViewById(R.id.txtUploadInfo);
        llUploadInfo = mRootView.findViewById(R.id.llUploadInfo);
        svinfo = mRootView.findViewById(R.id.svinfo);
        llMemories = mRootView.findViewById(R.id.llMemories);
        txtNoData = mRootView.findViewById(R.id.txtNoData);
        txtMyDailies = mRootView.findViewById(R.id.txtMyDailies);
        mTxtLocation = mRootView.findViewById(R.id.txtLocation);
        mTxtProfileStatus = mRootView.findViewById(R.id.txtProfileStatus);
        mTxtBio = mRootView.findViewById(R.id.txtBio);
        mTxtCountry = mRootView.findViewById(R.id.txtCountry);
        mTxtDOB = mRootView.findViewById(R.id.txtDOB);
        mTxtGender = mRootView.findViewById(R.id.txtGender);
        imgMyProfile = mRootView.findViewById(R.id.imgMyProfile);
        imgMyDailiesView = mRootView.findViewById(R.id.imgMyDailiesView);
        ImageView imgMenu = mRootView.findViewById(R.id.imgMenu);
        imgMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).openMenu();
            }
        });
        ImageView imgSearch = mRootView.findViewById(R.id.imgSearch);
        imgSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).openSearchTabFragment();
            }
        });
        ImageView imgMessage = mRootView.findViewById(R.id.imgMessage);
        imgMessage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).openMessageFragment();
            }
        });

        mGridHotMemory = mRootView.findViewById(R.id.gridView);
        mProfileMemoriesGridAdapter = new ProfileMemoriesGridAdapter(mActivity, mContext, new ProfileMemoriesGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int index) {
                Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new MyPostsFragment(index, true),mActivity, R.id.framelayout_main);
                MessageEvent messageEvent = new MessageEvent();
                messageEvent.setType(MessageEvent.MessageType.MY_POSTS);
                messageEvent.setMemoryModelList(gulfProfileData.getMemoryList());
                EventBus.getDefault().postSticky(messageEvent);
            }
        });
        mGridHotMemory.setAdapter(mProfileMemoriesGridAdapter);

        mImgProfileEdit = mRootView.findViewById(R.id.imgEditProfile);
        mImgProfileEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).openEditProfileFragment(gulfProfileData);
            }
        });

        LinearLayout llMyDailies = mRootView.findViewById(R.id.llMyDailies);
        llMyDailies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gulfProfileData != null && gulfProfileData.getDailyList().size() >0){
                    startActivity(new Intent(mContext, MyDailiesViewActivity.class));
                    MessageEvent messageEvent = new MessageEvent();
                    messageEvent.setType(MessageEvent.MessageType.MY_DAILIES_VIEW);
                    messageEvent.setGulfProfileData(gulfProfileData);
                    EventBus.getDefault().postSticky(messageEvent);
                }
            }
        });

        if( SessionData.I().uploadInfoModelList.size() > 0){
            llUploadInfo.setVisibility(View.VISIBLE);
            txtUploadInfo.setText(getString(R.string.upload_info, SessionData.I().uploadInfoModelList.size()+""));
        }else{
            llUploadInfo.setVisibility(View.GONE);
            txtUploadInfo.setText("");
        }

        llUploadInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new MediaUploadFragment() , mActivity, R.id.framelayout_main);
            }
        });

        if( ! loadedProfileTab){
            callGetProfile();
        }else{
            initFromApi();
        }

    }

    @Override
    public void onRefresh() {
        callGetProfile();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent (MessageEvent messageEvent) {
        if (messageEvent.getType() == MessageEvent.MessageType.REMOVE_MEDIA) {
            isNotification = true;
            callGetProfile();
            isNotification = false;
            remvoeSticky();
        }else  if(messageEvent.getType() == MessageEvent.MessageType.PROFILE_REFRESH){
            if( SessionData.I().uploadInfoModelList.size() > 0 ){
                llUploadInfo.setVisibility(View.VISIBLE);
                txtUploadInfo.setText(getString(R.string.upload_info, SessionData.I().uploadInfoModelList.size()+""));
            }else{
                llUploadInfo.setVisibility(View.GONE);
                txtUploadInfo.setText("");
            }
            loadedProfileTab = false;
            isNotification = true;
            callGetProfile();
            isNotification = false;
            remvoeSticky();
        }else if(messageEvent.getType() == MessageEvent.MessageType.NEW_MSG_NOTIFICATION){
            imgBadge.setVisibility(View.VISIBLE);
            remvoeSticky();
        }else if(messageEvent.getType() == MessageEvent.MessageType.UPLOAD_INFO){
            if( SessionData.I().uploadInfoModelList.size() > 0 ){
                llUploadInfo.setVisibility(View.VISIBLE);
                txtUploadInfo.setText(getString(R.string.upload_info, SessionData.I().uploadInfoModelList.size()+""));
            }else{
                llUploadInfo.setVisibility(View.GONE);
                txtUploadInfo.setText("");
            }
            remvoeSticky();
        }else if(messageEvent.getType() == MessageEvent.MessageType.TOP_SCROLL){
            if(svinfo != null)
                svinfo.smoothScrollTo(0,0);
            remvoeSticky();
        }
    }

    public void remvoeSticky(){
        MessageEvent stickyEvent = EventBus.getDefault().getStickyEvent(MessageEvent.class);
        if(stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent(stickyEvent);
        }
    }

    private static GulfProfiledata gulfProfileData;
    private void callGetProfile() {
        if( !isNotification )
            swipeRefresh.setRefreshing(true);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();
        Call<GulfProfileResponse> call = mApiInterface.getProfileWithFriendshipFlag(userId, userId);
        call.enqueue(new Callback<GulfProfileResponse>() {
            @Override
            public void onResponse(Call<GulfProfileResponse> call, Response<GulfProfileResponse> response) {
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful()) {
                    GulfProfileResponse mOtpResponse = response.body();

                    if(mOtpResponse!=null && isAdded()){
                        if (mOtpResponse.getStatus().equalsIgnoreCase("true")) {
                            SavePref.getInstance(mActivity).saveGulfProfileResponse(mOtpResponse);
                            loadedProfileTab = true;
                            gulfProfileData= mOtpResponse.getData();
                            initFromApi();
                        }

                    } else {
                        Toast.makeText(mActivity,  mOtpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<GulfProfileResponse> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                llMemories.setVisibility(View.GONE);
                Log.i("ProfileFragment", "onResponse" + t.getMessage());
            }
        });

    }

    private void initFromApi(){
        if(gulfProfileData != null ){
            if(gulfProfileData.all_other_to_seeMyfriends == 0){
                mbinding.txtMyFriends.setVisibility(View.INVISIBLE);
            }else {
                mbinding.txtMyFriends.setVisibility(View.VISIBLE);
            }
            if((gulfProfileData.getProfilePicture()!=null && gulfProfileData.getProfilePicture().trim().length()>0))
            {
                try{
                    Picasso.with(mActivity).load(ApiClass.ImageBaseUrl + gulfProfileData.getProfilePicture()).fit().centerCrop().placeholder(R.drawable.profile_placeholder).into(imgMyDailiesView);
                }catch (Exception ex){
                    ex.printStackTrace();
                }

            }

            if((gulfProfileData.getBg_picture()!=null && gulfProfileData.getBg_picture().trim().length()>0))
            {
                try{
                    Picasso.with(mActivity).load(ApiClass.ImageBaseUrl + gulfProfileData.getBg_picture()).fit().centerCrop().into(imgMyProfile);
                }catch (Exception ex){
                    ex.printStackTrace();
                }

            }

            if(gulfProfileData.getDailyList().size() == 0){
                txtMyDailies.setVisibility(View.GONE);
            }else{
                txtMyDailies.setVisibility(View.VISIBLE);
            }

            mTxtCountry.setText(getCountryNameFromCode(gulfProfileData.getCountryId()));
            mTxtDOB.setText(gulfProfileData.getDob());

            if(gulfProfileData.getGender().equalsIgnoreCase("male")){
                mTxtGender.setText(getString(R.string.male));
            }else{
                mTxtGender.setText(getString(R.string.female));
            }

            SpannableString tag = getTags(gulfProfileData.getBio(), "1");
            mbinding.txtBio.setText(tag == null ? "" : tag);
            mbinding.txtBio.setMovementMethod(LinkMovementMethod.getInstance());

            mTxtProfileStatus.setText(gulfProfileData.getProfileStatus());
            mTxtLocation.setText(gulfProfileData.getCountry());
            if(gulfProfileData.getMemoryList().size() > 0){
                txtNoData.setVisibility(View.GONE);
                llMemories.setVisibility(View.VISIBLE);
                mProfileMemoriesGridAdapter.setData(gulfProfileData.getMemoryList());
            }else{
                txtNoData.setVisibility(View.VISIBLE);
                llMemories.setVisibility(View.GONE);
            }
            svinfo.smoothScrollTo(0,0);

            try{
                // Picasso.with(mActivity).load(ApiClass.ImageBaseUrl + gulfProfileData.getDailyList().get(0).getUrl()).fit().into(imgMyDailiesView);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }else{
            txtMyDailies.setVisibility(View.GONE);
        }
    }

    public String getCountryNameFromCode(Integer code){
        if( code.intValue() == 966){
            return getResources().getString(AppConstants.COUNTRY_NAME[5]);
        }
        if( code.intValue() == 973){
            return getResources().getString(AppConstants.COUNTRY_NAME[0]);
        }
        if( code.intValue() == 964){
            return getResources().getString(AppConstants.COUNTRY_NAME[1]);
        }
        if( code.intValue() == 965){
            return getResources().getString(AppConstants.COUNTRY_NAME[2]);
        }
        if( code.intValue() == 968){
            return getResources().getString(AppConstants.COUNTRY_NAME[3]);
        }
        if( code.intValue() == 974){
            return getResources().getString(AppConstants.COUNTRY_NAME[4]);
        }
        if( code.intValue() == 971){
            return getResources().getString(AppConstants.COUNTRY_NAME[6]);
        }
        if( code.intValue() == 967){
            return getResources().getString(AppConstants.COUNTRY_NAME[7]);
        }
        return "";
    }

}
