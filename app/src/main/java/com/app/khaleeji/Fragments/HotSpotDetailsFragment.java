package com.app.khaleeji.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.FriendDailiesViewActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Activity.MyDailiesViewActivity;
import com.app.khaleeji.Adapter.HotSpotMemoriesGridAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.CheckInResponse;
import com.app.khaleeji.Response.HotSpotDatum;
import com.app.khaleeji.Response.HotSpotDetails;
import com.app.khaleeji.Response.HotSpotDetailsResponse;
import com.app.khaleeji.Response.HotSpotMemory;
import com.app.khaleeji.Response.MemoryModel;
import com.app.khaleeji.Response.fetchDailiesOfFriends.FriendListDailiesOfFriends;
import com.app.khaleeji.Response.fetchHotspotTimeLine.HotspotMemory;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.AppConstants;
import CustomView.CustomGridView;
import CustomView.CustomTextView;
import Utility.ApiClass;
import Utility.Fragment_Process;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import Utility.ProgressDialog;
import Utility.AlertDialog;

public class HotSpotDetailsFragment  extends BaseFragment {

    private Context mContext;
    private View mRootView;
    private CustomGridView mGridHotMemory;
    private HotSpotDatum mHotspot;
    private CustomTextView txtCheck;
//    private List<HotSpotMemory> mListHotSpotMemory;
    private CustomTextView txtNoData;
    private HotSpotMemoriesGridAdapter hotSpotMemoriesGridAdapter;
    private int mHotspotId;
    private boolean isFromHotSpotUpdate;
    private HotSpotDetails mHotSpotDetails;
    private CustomTextView txtFaraway;
    private de.hdodenhof.circleimageview.CircleImageView imgDaily;
    private CustomTextView txtDailies;
    private LinearLayout llMemories;
    private ImageView imgBadge;

    public HotSpotDetailsFragment(HotSpotDatum data) {
        mHotspot = data;
        isFromHotSpotUpdate = false;
    }

    public HotSpotDetailsFragment(int  hotspotId, int userId) {
        mHotspotId = hotspotId;
        isFromHotSpotUpdate = true;
        getHotspotDetails(hotspotId, userId);
    }

    public HotSpotDetailsFragment(){

    }

    public static HotSpotDetailsFragment newInstance(HotSpotDatum data) {
        HotSpotDetailsFragment fragment = new HotSpotDetailsFragment(data);
//        Bundle b = new Bundle();
//        b.putSerializable(Bundle_Identifier.UserData, mHotspot);
//        b.putString("Came_from", CommingFrom);
//        fragment.setArguments(b);
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent (MessageEvent messageEvent) {
        if(messageEvent.getType() == MessageEvent.MessageType.NEW_MSG_NOTIFICATION){
            imgBadge.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = mActivity;
        if (mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_hotspotdetails, null);
        initView();
        return mRootView;
    }

    private CustomTextView txtWho;
    private ScrollView svinfo;
    private RelativeLayout rlMemory;
    private LinearLayout findLocation;

    private void initView(){

        imgBadge = mRootView.findViewById(R.id.imgBadge);
        if(SavePref.getInstance(mActivity).getNewMsgNotification()){
            imgBadge.setVisibility(View.VISIBLE);
        }else{
            imgBadge.setVisibility(View.INVISIBLE);
        }


        findLocation = mRootView.findViewById(R.id.findLocation);
        findLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri="";
                if(! isFromHotSpotUpdate){
                    uri = String.format(Locale.ENGLISH, "google.navigation:q= %f, %f", Float.parseFloat(mHotspot.getLat()), Float.parseFloat(mHotspot.getLng()));
                }else{
                    if(mHotSpotDetails != null){
                        uri = String.format(Locale.ENGLISH, "google.navigation:q= %f, %f", Float.parseFloat(mHotSpotDetails.getLat()), Float.parseFloat(mHotSpotDetails.getLng()));
                    }
                }
                if(!uri.isEmpty()){
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW,  Uri.parse(uri));
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            }
        });
        llMemories = mRootView.findViewById(R.id.llMemories);
        rlMemory = mRootView.findViewById(R.id.rlMemory);
        svinfo = mRootView.findViewById(R.id.svinfo);

        txtWho = mRootView.findViewById(R.id.txtWho);
        txtWho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFromHotSpotUpdate) {
                    Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new WhosHereFragment(mHotspot) ,mActivity, R.id.framelayout_main);
//                    ((MainActivity)mActivity).openWhosHereFragment(mHotspot);
                }else{
                    Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new WhosHereFragment(mHotSpotDetails) ,mActivity, R.id.framelayout_main);
//                    ((MainActivity)mActivity).openWhosHereFragment(mHotSpotDetails);
                }

            }
        });
        //set rounded screen
        if(Build.VERSION.SDK_INT >= 21)
            mRootView.setClipToOutline(true);
        ((MainActivity)mActivity).hide();
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);

        txtFaraway = mRootView.findViewById(R.id.txtFaraway);

        imgDaily = mRootView.findViewById(R.id.imgMyDailiesView);

        txtNoData = mRootView.findViewById(R.id.txtNoData);
        CustomTextView txtTitle = mRootView.findViewById(R.id.txtTitle);
        ImageView imgBack = mRootView.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if( ! isFromHotSpotUpdate){
                    MessageEvent messageEvent = new MessageEvent();
                    messageEvent.setType(MessageEvent.MessageType.HOTSPOTDETAILS);
                    EventBus.getDefault().post(messageEvent);
                }
                ((MainActivity)mActivity).back();
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
        ImageView imgHotspot= mRootView.findViewById(R.id.imgHotspot);
        CustomTextView txtCountry = mRootView.findViewById(R.id.txtCountry);
        CustomTextView txtDistance = mRootView.findViewById(R.id.txtDistance);
        CustomTextView infoContent =mRootView.findViewById(R.id.infoContent);
        LinearLayout llDailiesUpdates = mRootView.findViewById(R.id.llDailiesUpdates);
        txtDailies = mRootView.findViewById(R.id.txtDailies);
        llDailiesUpdates.setOnClickListener(view -> {
           openDetailHotspotDaily();
        });

        mGridHotMemory = mRootView.findViewById(R.id.gridView);
        CustomTextView txtHotspotMemories = mRootView.findViewById(R.id.txtHotspotMemories);
        txtCheck = mRootView.findViewById(R.id.txtCheck);

        txtCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtCheck.isEnabled())
                    callCheckedInApi();
            }
        });

        if(!isFromHotSpotUpdate && mHotspot != null){
            if(mHotspot.getIsHotspot()  && mHotspot.getIs_admin().intValue() == 1 ) {
                imgDaily.setImageDrawable(mContext.getResources().getDrawable(R.drawable.tea));
            }else{
                switch (mHotspot.getCategory_id()){
                    case AppConstants.RESTAURANT:
                        //restaurants
//                        if(mHotspot.getIsHotspot()  && mHotspot.getIs_admin().intValue() == 0 ) {
                            imgDaily.setImageDrawable(mContext.getResources().getDrawable(R.drawable.food));
//                        }else if( ! mHotspot.getIsHotspot()){
//                            //grey
//                            imgDaily.setImageDrawable(mContext.getResources().getDrawable(R.drawable.food_grey));
//                        }
                        break;
                    case AppConstants.CAFE:
                        //cafe
//                        if(mHotspot.getIsHotspot()  && mHotspot.getIs_admin().intValue() == 0 ) {
                            imgDaily.setImageDrawable(mContext.getResources().getDrawable(R.drawable.drink));
//                        }else if( ! mHotspot.getIsHotspot()){
//                            //grey
//                            imgDaily.setImageDrawable(mContext.getResources().getDrawable(R.drawable.drink_grey));
//                        }
                        break;

                    case AppConstants.SHOPPING:
                        //shopping
//                        if(mHotspot.getIsHotspot()  && mHotspot.getIs_admin().intValue() == 0 ) {
                            imgDaily.setImageDrawable(mContext.getResources().getDrawable(R.drawable.shop));
//                        }else if( ! mHotspot.getIsHotspot()){
//                            //grey
//                            imgDaily.setImageDrawable(mContext.getResources().getDrawable(R.drawable.shop_grey));
//                        }
                        break;

                    case AppConstants.HOTEL:
                        //hotels
//                        if(mHotspot.getIsHotspot()  && mHotspot.getIs_admin().intValue() == 0 ) {
                            imgDaily.setImageDrawable(mContext.getResources().getDrawable(R.drawable.sleep));
//                        }else if( ! mHotspot.getIsHotspot()){
//                            //grey
//                            imgDaily.setImageDrawable(mContext.getResources().getDrawable(R.drawable.sleep_grey));
//                        }
                        break;

                    case AppConstants.OUTDOOR:
                        //outdoor
//                        if(mHotspot.getIsHotspot()  && mHotspot.getIs_admin().intValue() == 0 ) {
                            imgDaily.setImageDrawable(mContext.getResources().getDrawable(R.drawable.tree));
//                        }else if( ! mHotspot.getIsHotspot()){
//                            //grey
//                            imgDaily.setImageDrawable(mContext.getResources().getDrawable(R.drawable.tree_grey));
//                        }
                        break;
                }
            }

            if(mHotspot.getDistance() > 5 && mHotspot.getDistance_unit().equalsIgnoreCase("km")){
                if(! mHotspot.getIsCheckined()){
                    txtFaraway.setVisibility(View.VISIBLE);
                    txtCheck.setText(getResources().getString(R.string.check_in));
                }else{
                    txtFaraway.setVisibility(View.GONE);
                    txtCheck.setText(getResources().getString(R.string.your_checked));
                }
                txtCheck.setBackgroundResource(R.drawable.rounded_disable_purple_bg);
                txtCheck.setEnabled(false);
            }else{
                txtFaraway.setVisibility(View.GONE);
                if(mHotspot.getIsCheckined()){
                    txtCheck.setText(getResources().getString(R.string.your_checked));
                    txtCheck.setBackgroundResource(R.drawable.rounded_disable_purple_bg);
                    txtCheck.setEnabled(false);
                }else{
                    txtCheck.setBackgroundResource(R.drawable.rounded_corners_purple_bg);
                    txtCheck.setText(getResources().getString(R.string.check_in));
                    txtCheck.setEnabled(true);
                }
            }

            txtTitle.setText(mHotspot.getLocationName().trim());
            if(mHotspot.getImage() != null && !mHotspot.getImage().isEmpty())
                Picasso.with(mActivity).load( mHotspot.getImage()).fit().into(imgHotspot);
            txtCountry.setText(mHotspot.getCity());
            if(mHotspot.getDistance_unit().equalsIgnoreCase("meter")){
                txtDistance.setText(mHotspot.getDistance()+"M");
            }else{
                txtDistance.setText(mHotspot.getDistance()+"KM");
            }

            infoContent.setText(mHotspot.getAbout());
            if( ! mHotspot.getIsHotspot()){
                txtWho.setVisibility(View.GONE);
                txtHotspotMemories.setVisibility(View.GONE);
                llDailiesUpdates.setVisibility(View.GONE);
            }else{
                callGetHotspotDetails();
            }

            hotSpotMemoriesGridAdapter = new HotSpotMemoriesGridAdapter(mActivity,mContext, new HotSpotMemoriesGridAdapter.OnItemClickListener(){
                public void onItemClick( int index){
                    openDetailHotspotMemory(index);
                }
            });
            mGridHotMemory.setAdapter(hotSpotMemoriesGridAdapter);
//            mGridHotMemory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                 openDetailHotspotMemory();
//                }
//            });

        }else{
            if(mHotSpotDetails != null){

                if(mHotSpotDetails.getIs_admin().intValue() == 1 ) {
                    imgDaily.setImageDrawable(mContext.getResources().getDrawable(R.drawable.tea));
                }else{
                    switch (mHotSpotDetails.getCategory_id()){
                        case AppConstants.RESTAURANT:
                            //restaurants
//                            if( mHotSpotDetails.getIs_admin().intValue() == 0 ) {
                                imgDaily.setImageDrawable(mContext.getResources().getDrawable(R.drawable.food));
//                            }else {
//                                //grey
//                                imgDaily.setImageDrawable(mContext.getResources().getDrawable(R.drawable.food_grey));
//                            }
                            break;
                        case AppConstants.CAFE:
                            //cafe
//                            if( mHotSpotDetails.getIs_admin().intValue() == 0 ) {
                                imgDaily.setImageDrawable(mContext.getResources().getDrawable(R.drawable.drink));
//                            }else{
//                                //grey
//                                imgDaily.setImageDrawable(mContext.getResources().getDrawable(R.drawable.drink_grey));
//                            }
                            break;

                        case AppConstants.SHOPPING:
                            //shopping
//                            if(mHotSpotDetails.getIs_admin().intValue() == 0 ) {
                                imgDaily.setImageDrawable(mContext.getResources().getDrawable(R.drawable.shop));
//                            }else{
//                                //grey
//                                imgDaily.setImageDrawable(mContext.getResources().getDrawable(R.drawable.shop_grey));
//                            }
                            break;

                        case AppConstants.HOTEL:
                            //hotels
//                            if(mHotSpotDetails.getIs_admin().intValue() == 0 ) {
                                imgDaily.setImageDrawable(mContext.getResources().getDrawable(R.drawable.sleep));
//                            }else{
//                                //grey
//                                imgDaily.setImageDrawable(mContext.getResources().getDrawable(R.drawable.sleep_grey));
//                            }
                            break;

                        case AppConstants.OUTDOOR:
                            //outdoor
//                            if(mHotSpotDetails.getIs_admin().intValue() == 0 ) {
                                imgDaily.setImageDrawable(mContext.getResources().getDrawable(R.drawable.tree));
//                            }else{
//                                //grey
//                                imgDaily.setImageDrawable(mContext.getResources().getDrawable(R.drawable.tree_grey));
//                            }
                            break;
                    }
                }

                if(mHotSpotDetails.getDistance() > 5 && mHotSpotDetails.getDistance_unit().equalsIgnoreCase("km")){
                   if(! mHotSpotDetails.isCheckedIn()){
                       txtFaraway.setVisibility(View.VISIBLE);
                       txtCheck.setText(getResources().getString(R.string.check_in));
                   }else {
                       txtFaraway.setVisibility(View.GONE);
                       txtCheck.setText(getResources().getString(R.string.your_checked));
                   }
                    txtCheck.setBackgroundResource(R.drawable.rounded_disable_purple_bg);
                    txtCheck.setEnabled(false);
                }else{
                    txtFaraway.setVisibility(View.GONE);
                    if(mHotSpotDetails.isCheckedIn()){
                        txtCheck.setBackgroundResource(R.drawable.rounded_disable_purple_bg);
                        txtCheck.setText(getResources().getString(R.string.your_checked));
                        txtCheck.setEnabled(false);
                    }else{
                        txtCheck.setBackgroundResource(R.drawable.rounded_corners_purple_bg);
                        txtCheck.setText(getResources().getString(R.string.check_in));
                        txtCheck.setEnabled(true);
                    }
                }

                txtNoData = mRootView.findViewById(R.id.txtNoData);
                txtTitle.setText(mHotSpotDetails.getLocationName());
                Picasso.with(mActivity).load( mHotSpotDetails.getImage()).fit().into(imgHotspot);
                txtCountry.setText(mHotSpotDetails.getCity());
                txtDistance.setText(mHotSpotDetails.getDistance()+mHotSpotDetails.getDistance_unit());
                infoContent.setText(mHotSpotDetails.getAbout());
                HotSpotMemoriesGridAdapter hotSpotMemoriesGridAdapter = new HotSpotMemoriesGridAdapter(mActivity,mContext, new HotSpotMemoriesGridAdapter.OnItemClickListener(){
                    public void onItemClick( int index){
                        openDetailHotspotMemory(index);
                    }
                });

                mGridHotMemory.setAdapter(hotSpotMemoriesGridAdapter);

                List<MemoryModel> hotSpotMemoryList = mHotSpotDetails.getData();
                if(hotSpotMemoryList != null && hotSpotMemoryList.size() > 0){
                    txtNoData.setVisibility(View.GONE);
                    llMemories.setVisibility(View.VISIBLE);
                    hotSpotMemoriesGridAdapter.setData(hotSpotMemoryList);
                }else {
                    txtNoData.setVisibility(View.VISIBLE);
                    llMemories.setVisibility(View.GONE);
                }

//                mGridHotMemory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                        openDetailHotspotMemory();
//                    }
//                });

                if(mHotSpotDetails != null && mHotSpotDetails.getDaily() != null && mHotSpotDetails.getDaily().size() > 0){
                    txtDailies.setVisibility(View.VISIBLE);
                }else{
                    txtDailies.setVisibility(View.GONE);
                }

            }
        }

        svinfo.smoothScrollTo(0,0);
    }

    private void callCheckedInApi(){
        if(mHotspot == null && !isFromHotSpotUpdate)
            return;
        if(mHotSpotDetails == null && isFromHotSpotUpdate)
            return;
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = ApiClass.getmApiClass().buildDefaultParams(mActivity);
        Call<CheckInResponse> call;
        int userid = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();
        mparams.put("userid", userid);

        if(isFromHotSpotUpdate){
            mparams.put(ApiClass.Lat, mHotSpotDetails.getLat());
            mparams.put(ApiClass.LNG, mHotSpotDetails.getLng());
            mparams.put(ApiClass.PLACE_ID, mHotSpotDetails.getPlaceId());
            mparams.put(ApiClass.LOCATION_NAME, mHotSpotDetails.getLocationName());
            mparams.put(ApiClass.IMAGE, mHotSpotDetails.getImage());
            mparams.put(ApiClass.CATEGORY_ID, mHotSpotDetails.getCategory_id());
            mparams.put(ApiClass.RATING, mHotSpotDetails.getRating());
        }else{
            mparams.put(ApiClass.Lat, mHotspot.getLat());
            mparams.put(ApiClass.LNG, mHotspot.getLng());
            mparams.put(ApiClass.PLACE_ID, mHotspot.getPlaceId());
            mparams.put(ApiClass.LOCATION_NAME, mHotspot.getLocationName());
            mparams.put(ApiClass.IMAGE, mHotspot.getImage());
            mparams.put(ApiClass.CATEGORY_ID, mHotspot.getCategory_id());
            mparams.put(ApiClass.RATING, mHotspot.getRating());
        }

        call = mApiInterface.checkin(mparams);
        call.enqueue(new Callback<CheckInResponse>() {
            @Override
            public void onResponse(Call<CheckInResponse> call, Response<CheckInResponse> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful() && isAdded()) {
                    CheckInResponse mBasic_Response = response.body();

                    if (mBasic_Response != null && mBasic_Response.getStatus().equalsIgnoreCase("true")) {
                        txtCheck.setText(getResources().getString(R.string.your_checked));
                        txtCheck.setBackgroundResource(R.drawable.rounded_disable_purple_bg);
                        txtCheck.setEnabled(false);
                    } else {
                        AlertDialog.showAlert(mActivity, getString(R.string.app_name), mBasic_Response.getMessage(), getString(R.string.Done), "", false, null, null);
                    }
                }

            }

            @Override
            public void onFailure(Call<CheckInResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    private List<MemoryModel> mListHotSpotMemory;
    private void callGetHotspotDetails(){
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().hotspot_id, mHotspot.getHotspotId());
        int fromUserId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();
        mparams.put(ApiClass.USER_ID,fromUserId);

        Call<HotSpotDetailsResponse> call = mApiInterface.getHotspotDetails(mparams);

        call.enqueue(new Callback<HotSpotDetailsResponse>() {
            @Override
            public void onResponse(Call<HotSpotDetailsResponse> call, Response<HotSpotDetailsResponse> response) {
                if (response.isSuccessful() && isAdded()) {
                    HotSpotDetailsResponse memoryResponse = response.body();
                    if(memoryResponse != null){
                        if(memoryResponse.getStatus().equalsIgnoreCase("true")){
                            mListHotSpotMemory = memoryResponse.getData().getData();
                            mHotSpotDetails = memoryResponse.getData();
                            if(mHotSpotDetails != null && mHotSpotDetails.getDaily() != null){
                                if(mHotSpotDetails.getDaily().size() > 0){
                                    txtDailies.setVisibility(View.VISIBLE);
                                }else{
                                    txtDailies.setVisibility(View.GONE);
                                }
                            }
                            if(mListHotSpotMemory != null && mListHotSpotMemory.size() > 0){
                                txtNoData.setVisibility(View.GONE);
                                llMemories.setVisibility(View.VISIBLE);
                                hotSpotMemoriesGridAdapter.setData(mListHotSpotMemory);
                            }else {
                                txtNoData.setVisibility(View.VISIBLE);
                                llMemories.setVisibility(View.GONE);
                            }

                        }else{
//                            AlertDialog.showAlert(mActivity, getString(R.string.app_name),
//                                    memoryResponse.getMessage(), getString(R.string.txt_Done),
//                                    "", true, null, null);
                        }
                    }

                } else {
                    System.out.println(response.errorBody());
                }

            }

            @Override
            public void onFailure(Call<HotSpotDetailsResponse> call, Throwable t) {
            }
        });
    }

    private void getHotspotDetails(int hotSpotid, int userId){
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().hotspot_id,hotSpotid);
        mparams.put(ApiClass.USER_ID,userId);

        Call<HotSpotDetailsResponse> call = mApiInterface.getHotspotDetails(mparams);

        call.enqueue(new Callback<HotSpotDetailsResponse>() {
            @Override
            public void onResponse(Call<HotSpotDetailsResponse> call, Response<HotSpotDetailsResponse> response) {
                if (response.isSuccessful() && isAdded()) {
                    HotSpotDetailsResponse memoryResponse = response.body();
                    if(memoryResponse != null){
                        if(memoryResponse.getStatus().equalsIgnoreCase("true")){
                            mHotSpotDetails = memoryResponse.getData();
                            initView();
                        }else{
//                            AlertDialog.showAlert(mActivity, getString(R.string.app_name),
//                                    memoryResponse.getMessage(), getString(R.string.txt_Done),
//                                    "", true, null, null);
                        }
                    }

                } else {
                    System.out.println(response.errorBody());
                }

            }

            @Override
            public void onFailure(Call<HotSpotDetailsResponse> call, Throwable t) {
            }
        });
    }

    private void openDetailHotspotMemory(int index){
        if(mHotSpotDetails == null || mHotSpotDetails.getData() == null)
            return;
        Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new FriendPostsFragment(index, true),mActivity, R.id.framelayout_main);
        MessageEvent messageEvent = new MessageEvent();
        messageEvent.setType(MessageEvent.MessageType.FRIEND_POSTS);
        messageEvent.setMemoryModelList(mHotSpotDetails.getData());
        EventBus.getDefault().postSticky(messageEvent);
    }

    private void openDetailHotspotDaily(){
        if(mHotSpotDetails != null && mHotSpotDetails.getDaily() != null && mHotSpotDetails.getDaily().size() > 0){
            startActivity(new Intent(mActivity, FriendDailiesViewActivity.class));
            MessageEvent messageEvent = new MessageEvent();
            messageEvent.setType(MessageEvent.MessageType.FRIEND_DAILIES_VIEW);
            List<FriendListDailiesOfFriends> list = new ArrayList<>();
            FriendListDailiesOfFriends dailiesOfFriends = new FriendListDailiesOfFriends();
            dailiesOfFriends.setMedia(mHotSpotDetails.getDaily());
            list.add(dailiesOfFriends);
            messageEvent.setFriendLists(list, 0);
            EventBus.getDefault().postSticky(messageEvent);
        }
    }
}
