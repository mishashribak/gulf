package com.app.khaleeji.Fragments;

import androidx.databinding.DataBindingUtil;

import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Adapter.GooglePlacesAutocompleteAdapter;
import com.app.khaleeji.Adapter.SearchTabSubTagAdapter;
import com.app.khaleeji.Adapter.SearchTabTagAdapter;
import com.app.khaleeji.Adapter.SearchTabNameUserListAdapter;
import com.app.khaleeji.Adapter.SearchTabTagUserAdapter;
import com.app.khaleeji.Adapter.SearchTabTopUserListAdapter;
import com.app.khaleeji.Adapter.SearchTabLocationListAdapter;
import com.app.khaleeji.Adapter.SendAnswerAdapter;
import com.app.khaleeji.Adapter.SearchTabTagMemoriesGridAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.Basic_Response;
import com.app.khaleeji.Response.MemoryModel;
import com.app.khaleeji.Response.Search.SearchByNameModel;
import com.app.khaleeji.Response.Search.SearchByNameResponse;
import com.app.khaleeji.Response.Search.SearchFriendsModel;
import com.app.khaleeji.Response.UserDetails;
import com.app.khaleeji.databinding.SearchtabfragmentBinding;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.AppConstants;
import Constants.Bundle_Identifier;
import CustomView.CustomSenderAnswerDlg;
import Model.AreaModel;
import Model.CountTagModel;
import Model.SubTagImageModel;
import Model.SubTagModel;
import Model.NewTagResponse;
import Model.QAData;
import Model.SearchTagResponse;
import Model.TagUserModel;
import Utility.ApiClass;
import Utility.CheckConnection;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchTabFragments extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static int tab_pos = 0;
    private SearchtabfragmentBinding mbinding;
    private SearchTabLocationListAdapter locationAdapter;
    private SearchTabTopUserListAdapter topAdapter;
    private SearchTabNameUserListAdapter nameAdatper;
    private SearchTabTagAdapter tagAdapter;
    private SearchTabTagMemoriesGridAdapter mTagMemoriesGridAdapter;
    private SearchTabSubTagAdapter tagSubTagAdapter;
    private SearchTabTagUserAdapter tagUserAdapter;
    private SearchFriendsModel mSearchFriendsModel;
    private int mPreSelTabPos;
    private int  mCurSelTabPos;
    private String strTempSearchTag, strTempSearchName;
    private UserDetails mSelectedUserInfo;
    private SearchByNameModel mSelectedUserInfoNameModel;
    private ArrayList<QAData> dataQAList;
    private String strTempSearchCity;
    private GooglePlacesAutocompleteAdapter mGooglePlacesAutocompleteAdapter;
    private static Thread mthread = null;
    private boolean isNotification = false;
    private String hashTag;
    private String hashType;

    public SearchTabFragments(){

    }

    public SearchTabFragments(String hashTag, String hashType){
        this.hashTag = hashTag;
        this.hashType = hashType;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent messageEvent) {
        if (messageEvent.getType() == MessageEvent.MessageType.NOTIFICATION) {
            if(mCurSelTabPos == 1)
                return;
            isNotification = true;
            selectTab(mCurSelTabPos);
            isNotification = false;
        }else if(messageEvent.getType() == MessageEvent.MessageType.NEW_MSG_NOTIFICATION){
            mbinding.imgBadge.setVisibility(View.VISIBLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mbinding = DataBindingUtil.inflate(inflater, R.layout.searchtabfragment, container, false);
        View view = mbinding.getRoot();
        if(Build.VERSION.SDK_INT >= 21)
            view.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        initView();
        return view;
    }

    private void initView() {
        ((MainActivity)mActivity).hide();

        if(SavePref.getInstance(mActivity).getNewMsgNotification()){
            mbinding.imgBadge.setVisibility(View.VISIBLE);
        }else{
            mbinding.imgBadge.setVisibility(View.INVISIBLE);
        }


        mbinding.imgMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).openMenu();
            }
        });
        mbinding.imgMessage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).openMessageFragment();
            }
        });
        mbinding.txtParentTag.setVisibility(View.GONE);
        mbinding.txtSubTag.setVisibility(View.GONE);
        setUpTabs();

        if(hashTag != null){
            selectTab(1);
            callSeachForUserByhastag(hashType);
            if(hashType.equals("1")){
                callHashTagUsers(getString(R.string.users), hashTag, hashType);
            }else if(hashType.equals("2")){
                callHashTagUsers(getString(R.string.statuses), hashTag, hashType);
            }else{
                mbinding.gridView.setVisibility(View.VISIBLE);
                mbinding.rvUsers.setVisibility(View.GONE);
                callGetMemoriesByHashTag(getString(R.string.memories), hashTag);
            }
            hashTag = null;
        }else{
            selectTab(2);
        }

        mbinding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               searchUsers();
            }
        });

        mbinding.searchEdit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                switch (mCurSelTabPos){
                    case 0:
                        if (topAdapter != null) {
                            topAdapter.getFilter().filter(s);
                        }
                        break;
//                    case 1:
//                        if (tagAdapter != null) {
//                            tagAdapter.getFilter().filter(s);
//                        }
//                        break;
                    case 2:
                        if (nameAdatper != null) {
                            nameAdatper.getFilter().filter(s);
                        }
                        break;
                    case 3:
                        if (locationAdapter != null) {
                            locationAdapter.getFilter().filter(s);
                        }
                        break;
                }

            }
        });

        mGooglePlacesAutocompleteAdapter = new GooglePlacesAutocompleteAdapter(mActivity, R.layout.row_placelist);
        mbinding.etLocation.setAdapter(mGooglePlacesAutocompleteAdapter);
        mbinding.etLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mGooglePlacesAutocompleteAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mGooglePlacesAutocompleteAdapter.setOnClickListener(new GooglePlacesAutocompleteAdapter.OnclickListener() {
            @Override
            public void OnSerachPlace(final String place) {
                if (mthread != null) {
                    mthread.interrupt();
                }
                mthread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        autocomplete(place);
                    }
                });
                mthread.start();

            }
        });
        mbinding.etLocation.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        try {
            String str =  adapterView.getItemAtPosition(position).toString();
            mbinding.etLocation.setText(str);
            strTempSearchCity = mbinding.etLocation.getText().toString();
            callSearchForUserBylocation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/textsearch";
    private static final String OUT_JSON = "/json";
    public ArrayList autocomplete(final String input) {

        final ArrayList resultList = new ArrayList();
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        StringBuilder sb = null;
        try {

            sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + ApiClass.GooglePlaceKey);
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());

            // sb.append("&components=country:"+country_code);

            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
//            Log.e(TAG, "Error processing Places API URL", e);

        } catch (IOException e) {
//            Log.e(TAG, "Error connecting to Places API", e);

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            resultList.clear();
            resultList.addAll(parseLocationResult(jsonObj));

           /* JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());*/
            /*for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }*/
        } catch (JSONException e) {

        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mGooglePlacesAutocompleteAdapter.setList(resultList);

            }
        });
        return resultList;
    }

    public static final String STATUS = "status";
    public static final String OK = "OK";
    public static final String SUPERMARKET_ID = "id";
    public static final String NAME = "name";
    public static final String PLACE_ID = "place_id";
    public static final String REFERENCE = "reference";
    public static final String VICINITY = "vicinity";
    public static final String ZERO_RESULTS = "ZERO_RESULTS";
    public static final String GEOMETRY = "geometry";
    public static final String LOCATION = "location";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lng";
    private ArrayList parseLocationResult(JSONObject result) {

        ArrayList<AreaModel> resultList = null;
        String id, place_id, placeName = null, reference, icon, vicinity = null;
        double latitude, longitude;


        try {
            final JSONArray jsonArray = result.getJSONArray("results");
            resultList = new ArrayList(jsonArray.length());
            if (result.getString(STATUS).equalsIgnoreCase(OK)) {


                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject place = jsonArray.getJSONObject(i);

                    id = place.getString(SUPERMARKET_ID);
                    place_id = place.getString(PLACE_ID);
                    if (!place.isNull(NAME)) {
                        placeName = place.getString(NAME);

                    }
                    if (!place.isNull(VICINITY)) {
                        vicinity = place.getString(VICINITY);
                    }
                    latitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION)

                            .getDouble(LATITUDE);
                    longitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION)

                            .getDouble(LONGITUDE);
                    reference = place.getString(REFERENCE);

                    resultList.add(new AreaModel(placeName, latitude, longitude));


                }


            } else if (result.getString(STATUS).equalsIgnoreCase(ZERO_RESULTS)) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity, "No Area found in  radius!!!",

                                Toast.LENGTH_LONG).show();
                    }
                });

            }

        } catch (JSONException e) {

            e.printStackTrace();

        }


        return resultList;
    }

    double lat;
    double lng;
    private String getCity() {
        if (SavePref.getInstance(mActivity).getLatitude() != 1 && SavePref.getInstance(mActivity).getLongitude() != 1) {
            lat = SavePref.getInstance(mActivity).getLatitude();
            lng = SavePref.getInstance(mActivity).getLongitude();
            List<Address> addresses;
            Geocoder geocoder;
            geocoder = new Geocoder(mActivity, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation((double)lat, (double) lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String  country = addresses.get(0).getCountryName();
                String country_code = addresses.get(0).getCountryCode();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();

                return city;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return "";
    }

    private void setUpTabs() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        locationAdapter = new SearchTabLocationListAdapter(mActivity);
        mbinding.rvUsers.setLayoutManager(mLayoutManager);
        locationAdapter.setOnClickListener(new SearchTabLocationListAdapter.OnclickListener() {
            @Override
            public void onItemClick(int type, int pos) {
                if (CheckConnection.isNetworkAvailable(mActivity))
                {
                    mSelectedUserInfoNameModel  = mSearchByNameResponse.getData().get(pos);
                    if(mSelectedUserInfoNameModel == null)
                        return;
                    if(type == 0){
                        if(mSelectedUserInfoNameModel.getId().intValue() != SavePref.getInstance(mActivity).getUserdetail().getId().intValue()){
                            Fragment_Process.addFragment(mActivity.getSupportFragmentManager(),new FriendProfileFragment(mSelectedUserInfoNameModel.getId()),mActivity, R.id.framelayout_main);
                        }else{
                            Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), ProfileFragment.newInstance(), mActivity, R.id.framelayout_main);
                        }

                        return;
                    }
                    if(type == 1){
                        //add
                        openSendAnswerDlg(mSelectedUserInfoNameModel.getQuestion(), mSelectedUserInfoNameModel);
                        return;
                    }
                    if(type == 2){
                        //unfriend
                        callUnfriend(mSelectedUserInfoNameModel);
                        return;
                    }

                    if(type == 3){
                        //cancel
                        callRejectFriendRequest(mSelectedUserInfoNameModel);
                        return;
                    }
                }
                else
                {
                    Toast.makeText(mActivity,  getString(R.string.network_down), Toast.LENGTH_SHORT).show();
//                    AlertDialog.showAlert(mActivity, getString(R.string.app_name), getString(R.string.network_down), getString(R.string.txt_Done), "", false, null, null);
                }
            }
        });

        topAdapter = new SearchTabTopUserListAdapter(mActivity);
        topAdapter.setOnClickListener(new SearchTabTopUserListAdapter.OnclickListener() {
            @Override
            public void onItemClick(int type, SearchByNameModel searchByNameModel) {
                if (CheckConnection.isNetworkAvailable(mActivity))
                {
                    mSelectedUserInfoNameModel  = searchByNameModel;
                    if(mSelectedUserInfoNameModel == null)
                        return;
                    if(type == 0){
                        if(mSelectedUserInfoNameModel.getId().intValue() != SavePref.getInstance(mActivity).getUserdetail().getId().intValue()){
                            Fragment_Process.addFragment(mActivity.getSupportFragmentManager(),new FriendProfileFragment(mSelectedUserInfoNameModel.getId()),mActivity, R.id.framelayout_main);
                        }else{
                            Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), ProfileFragment.newInstance(), mActivity, R.id.framelayout_main);
                        }

//                        ((MainActivity)mActivity).openFriendProfileFragment(mSelectedUserInfo.getId());
                        return;
                    }
                    if(type == 1){
                        openSendAnswerDlg(mSelectedUserInfoNameModel.getQuestion(), mSelectedUserInfoNameModel);
                        return;
                    }
                    if(type == 2){
                        //unfriend
                        callUnfriend(mSelectedUserInfoNameModel);
                        return;
                    }

                    if(type == 3){
                        //cancel
                        callRejectFriendRequest(mSelectedUserInfoNameModel);
                        return;
                    }
                }
                else
                {
                    Toast.makeText(mActivity,  getString(R.string.network_down), Toast.LENGTH_SHORT).show();
//                    AlertDialog.showAlert(mActivity, getString(R.string.app_name), getString(R.string.network_down), getString(R.string.txt_Done), "", false, null, null);
                }
            }
        });
        nameAdatper = new SearchTabNameUserListAdapter(mActivity);
        nameAdatper.setOnClickListener(new SearchTabNameUserListAdapter.OnclickListener() {
            @Override
            public void onItemClick(int type, SearchByNameModel pos) {
                if (CheckConnection.isNetworkAvailable(mActivity))
                {
                    mSelectedUserInfoNameModel = pos;
                    if(mSelectedUserInfoNameModel == null)
                        return;
                    if(type == 0){
                        if(mSelectedUserInfoNameModel.getId().intValue() != SavePref.getInstance(mActivity).getUserdetail().getId().intValue()){
                            Fragment_Process.addFragment(mActivity.getSupportFragmentManager(),new FriendProfileFragment(mSelectedUserInfoNameModel.getId()),mActivity, R.id.framelayout_main);
                        }else{
                            Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), ProfileFragment.newInstance(), mActivity, R.id.framelayout_main);
                        }
//                        ((MainActivity)mActivity).openFriendProfileFragment(model.getId());
                        return;
                    }
                    if(type == 1){
                        //add
                        openSendAnswerDlg(mSelectedUserInfoNameModel.getQuestion(), mSelectedUserInfoNameModel);
                        return;
                    }
                    if(type == 2){
                        //unfriend
                        callUnfriend(mSelectedUserInfoNameModel);
                        return;
                    }

                    if(type == 3){
                        //cancel
                        callRejectFriendRequest(mSelectedUserInfoNameModel);
                        return;
                    }
                }
                else
                {
                    Toast.makeText(mActivity,  getString(R.string.network_down), Toast.LENGTH_SHORT).show();
//                    AlertDialog.showAlert(mActivity, getString(R.string.app_name), getString(R.string.network_down), getString(R.string.txt_Done), "", false, null, null);
                }
            }
        });
        tagAdapter = new SearchTabTagAdapter(mActivity);
        tagAdapter.setOnClickListener(new SearchTabTagAdapter.OnclickListener() {
            @Override
            public void onItemClick(CountTagModel tagModel) {
                if(tagModel.type != null){
                    mbinding.txtParentTag.setVisibility(View.VISIBLE);
                    if (Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(Bundle_Identifier.ArabicLang)) {
                        mbinding.txtParentTag.setText(tagModel.arabicText);
                    }else{
                        mbinding.txtParentTag.setText(tagModel.englishText);
                    }
                    callSeachForUserByhastag(tagModel.type);
                }
            }
        });

        tagSubTagAdapter = new SearchTabSubTagAdapter(mActivity);
        tagSubTagAdapter.setOnClickListener(new SearchTabSubTagAdapter.OnclickListener() {
            @Override
            public void onItemClick(SubTagModel subTagModel) {
                mbinding.txtSubTag.setVisibility(View.VISIBLE);
                mbinding.txtSubTag.setText(subTagModel.tags);
                if(subTagModel.type.equals("3")){
                    //memory tag
                    mbinding.gridView.setVisibility(View.VISIBLE);
                    mbinding.rvUsers.setVisibility(View.GONE);
                    callGetMemoriesByHashTag(subTagModel.tags);
                }else{
                    mbinding.gridView.setVisibility(View.GONE);
                    mbinding.rvUsers.setVisibility(View.VISIBLE);
                    callHashTagUsers(subTagModel.tag, subTagModel.type);
                }
            }
        });

        mbinding.txtSubTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hashTag = null;
                mbinding.txtSubTag.setVisibility(View.GONE);
                mbinding.rvUsers.setVisibility(View.VISIBLE);
                mbinding.gridView.setVisibility(View.GONE);
                if (mSearchTagResponse != null && mSearchTagResponse.getData() != null && mSearchTagResponse.getData().size() > 0) {
                    mbinding.rvUsers.setAdapter(tagSubTagAdapter);
                    tagSubTagAdapter.setData(mSearchTagResponse.getData());
                    mbinding.txtNoData.setVisibility(View.GONE);
                } else {
                    mbinding.txtNoData.setVisibility(View.VISIBLE);
                    tagSubTagAdapter.setData(null);
                }
            }
        });

        mbinding.txtParentTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hashTag = null;
                mbinding.txtParentTag.setVisibility(View.GONE);
                mbinding.txtSubTag.setVisibility(View.GONE);
                mbinding.rvUsers.setAdapter(tagAdapter);
                mbinding.rvUsers.setVisibility(View.VISIBLE);
                mbinding.gridView.setVisibility(View.GONE);
                if(countTagModelList != null && countTagModelList.size() > 0){
                    mbinding.txtNoData.setVisibility(View.GONE);
                    tagAdapter.setData(countTagModelList);
                }else{
                    mbinding.txtNoData.setVisibility(View.VISIBLE);
                    tagAdapter.setData(null);
                }
            }
        });

        mTagMemoriesGridAdapter = new SearchTabTagMemoriesGridAdapter(mActivity, new SearchTabTagMemoriesGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int index, List<MemoryModel> memoryModelList, String tag) {
                Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new TagPostsFragment(index, memoryModelList, tag), mActivity, R.id.framelayout_main);
            }
        });
        mbinding.gridView.setAdapter(mTagMemoriesGridAdapter);

        tagUserAdapter = new SearchTabTagUserAdapter(mActivity);
        tagUserAdapter.setOnClickListener(new SearchTabTagUserAdapter.OnclickListener() {
            @Override
            public void onItemClick(int type, TagUserModel tagUserModel) {
                if (CheckConnection.isNetworkAvailable(mActivity))
                {
                    if(tagUserModel == null)
                        return;
                    if(type == 0){
                        if(tagUserModel.id != SavePref.getInstance(mActivity).getUserdetail().getId().intValue()){
                            Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new FriendProfileFragment(tagUserModel.id), mActivity, R.id.framelayout_main);
                        }else{
                            Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), ProfileFragment.newInstance(), mActivity, R.id.framelayout_main);
                        }
                        return;
                    }
                    /*if(type == 1){
                        //add
                        openSendAnswerDlg(tagUserModel.question, tagUserModel.id);
                        return;
                    }
                    if(type == 2){
                        //unfriend
                        callUnfriend(tagUserModel.id);
                        return;
                    }
                    if(type == 3){
                        //cancel
                        callRejectFriendRequest(tagUserModel.id);
                        return;
                    }*/
                }
                else
                {
                    Toast.makeText(mActivity,  getString(R.string.network_down), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mbinding.rvUsers.setAdapter(locationAdapter);
        mbinding.tabTop.setOnClickListener(this);
        mbinding.tabTag.setOnClickListener(this);
        mbinding.tabLocation.setOnClickListener(this);
        mbinding.tabName.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tabTop:
                selectTab(0);
                break;
            case R.id.tabTag:
                selectTab(1);
                break;
            case R.id.tabName:
                selectTab(2);
                break;
            case R.id.tabLocation:
                selectTab(3);
                break;
        }

    }

    private void callGetMemoriesByHashTag(String parentTag, String tag){
        ProgressDialog.showProgress(mActivity);
        mbinding.txtParentTag.setVisibility(View.VISIBLE);
        mbinding.txtSubTag.setVisibility(View.VISIBLE);
        mbinding.txtSubTag.setText(tag);
        mbinding.txtParentTag.setText(parentTag);

        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Call<JsonObject> call = mApiInterface.getMemoriesByHashTag( SavePref.getInstance(mActivity).getUserdetail().getId(), tag);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject != null && isAdded()) {
                        if (jsonObject.get("status").getAsBoolean()) {
                            JsonArray jsonArray = jsonObject.getAsJsonObject("memories").getAsJsonArray("data");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<MemoryModel>>() {}.getType();
                            try{
                                List<MemoryModel> memoryModelList = gson.fromJson(jsonArray, type);
                                if(memoryModelList != null && memoryModelList.size() > 0){
                                    mbinding.txtNoData.setVisibility(View.GONE);
                                    mTagMemoriesGridAdapter.setData(memoryModelList, tag);
                                }else{
                                    mbinding.txtNoData.setVisibility(View.VISIBLE);
                                    mTagMemoriesGridAdapter.setData(null, tag);
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
                ProgressDialog.hideprogressbar();
            }
        });
    }

    private void callGetMemoriesByHashTag(String tag){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Call<JsonObject> call = mApiInterface.getMemoriesByHashTag( SavePref.getInstance(mActivity).getUserdetail().getId(), tag);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject != null && isAdded()) {
                        if (jsonObject.get("status").getAsBoolean()) {
                            JsonArray jsonArray = jsonObject.getAsJsonObject("memories").getAsJsonArray("data");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<MemoryModel>>() {}.getType();
                            try{
                                List<MemoryModel> memoryModelList = gson.fromJson(jsonArray, type);
                                if(memoryModelList != null && memoryModelList.size() > 0){
                                    mbinding.txtNoData.setVisibility(View.GONE);
                                    mTagMemoriesGridAdapter.setData(memoryModelList, tag);
                                }else{
                                    mbinding.txtNoData.setVisibility(View.VISIBLE);
                                    mTagMemoriesGridAdapter.setData(null, tag);
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
                ProgressDialog.hideprogressbar();
            }
        });
    }

    public void searchUsers(){
        switch (mCurSelTabPos){
            case 0:
                callSearchForTop();
                break;
//            case 1:
//                callGetCountTag();
//                break;
            case 2:
                if(! mbinding.searchEdit.getText().toString().isEmpty()) {
                    strTempSearchName = mbinding.searchEdit.getText().toString();
                    callSearchForFriendsByname();
                }
                else
                    Toast.makeText(mActivity, getString(R.string.no_name), Toast.LENGTH_SHORT).show();
//                    AlertDialog.showAlert(mActivity,getString(R.string.app_name),getString(R.string.no_name) , getString(R.string.txt_Done), "", false, null, null);
                break;
            case 3:
                if(! mbinding.etLocation.getText().toString().isEmpty()) {
                    strTempSearchCity = mbinding.etLocation.getText().toString();
                    callSearchForUserBylocation();
                }
                else
                    Toast.makeText(mActivity, getString(R.string.no_city), Toast.LENGTH_SHORT).show();
//                    AlertDialog.showAlert(mActivity,getString(R.string.app_name),getString(R.string.no_city) , getString(R.string.txt_Done), "", false, null, null);

                break;

        }
    }

    public void selectTab(int type){
        mCurSelTabPos = type;
        mbinding.rvUsers.setVisibility(View.VISIBLE);
        mbinding.gridView.setVisibility(View.GONE);
        mbinding.searchEdit.setEnabled(true);
        mbinding.searchEdit.setHint(getResources().getString(R.string.searchForWords));
        mbinding.txtParentTag.setVisibility(View.GONE);
        mbinding.txtSubTag.setVisibility(View.GONE);
        switch (type){
            case 0:
                //Top
                mCurSelTabPos = 0;
                mPreSelTabPos = 0;
                mbinding.etLocation.setVisibility(View.GONE);
                mbinding.searchEdit.setVisibility(View.VISIBLE);
                mbinding.searchEdit.setText("");
                mbinding.tabTop.setBackgroundResource(R.drawable.rounded_box_searchtab_active);
                mbinding.tabName.setBackgroundResource(R.drawable.rounded_box_searchtab);
                mbinding.tabTag.setBackgroundResource(R.drawable.rounded_box_searchtab);
                mbinding.tabLocation.setBackgroundResource(R.drawable.rounded_box_searchtab);
                mbinding.tabTop.setTextColor(getResources().getColor(R.color.white));
                mbinding.tabTag.setTextColor(getResources().getColor(R.color.black));
                mbinding.tabName.setTextColor(getResources().getColor(R.color.black));
                mbinding.tabLocation.setTextColor(getResources().getColor(R.color.black));
                mbinding.rlSearchContent.setBackground(getResources().getDrawable(R.drawable.rounded_box_white));
                mbinding.rvUsers.setAdapter(topAdapter);
                callSearchForTop();
                break;
            case 1:
                //Tag
                mbinding.searchEdit.setEnabled(false);
                mbinding.searchEdit.setHint("");
                mCurSelTabPos = 1;
                mPreSelTabPos = 1;
                mbinding.etLocation.setVisibility(View.GONE);
                mbinding.searchEdit.setVisibility(View.VISIBLE);
                mbinding.searchEdit.setText(strTempSearchTag);
                mbinding.tabTop.setBackgroundResource(R.drawable.rounded_box_searchtab);
                mbinding.tabName.setBackgroundResource(R.drawable.rounded_box_searchtab);
                mbinding.tabTag.setBackgroundResource(R.drawable.rounded_box_searchtab_active);
                mbinding.tabLocation.setBackgroundResource(R.drawable.rounded_box_searchtab);
//                mbinding.rlSearchContent.setBackgroundColor(getResources().getColor(R.color.search_tag_bk));

                mbinding.tabTop.setTextColor(getResources().getColor(R.color.black));
                mbinding.tabTag.setTextColor(getResources().getColor(R.color.white));
                mbinding.tabName.setTextColor(getResources().getColor(R.color.black));
                mbinding.tabLocation.setTextColor(getResources().getColor(R.color.black));
                if(hashTag == null){
                    mbinding.rvUsers.setAdapter(tagAdapter);
                }
                callGetCountTag();
                break;
            case 2:
                //Name
                mCurSelTabPos = 2;
//                if (mPreSelTabPos == type)
//                    return;
                mPreSelTabPos = 2;
                mbinding.etLocation.setVisibility(View.GONE);
                mbinding.searchEdit.setVisibility(View.VISIBLE);
                mbinding.searchEdit.setText(strTempSearchName);
                mbinding.tabTop.setBackgroundResource(R.drawable.rounded_box_searchtab);
                mbinding.tabName.setBackgroundResource(R.drawable.rounded_box_searchtab_active);
                mbinding.tabTag.setBackgroundResource(R.drawable.rounded_box_searchtab);
                mbinding.tabLocation.setBackgroundResource(R.drawable.rounded_box_searchtab);
                mbinding.tabTop.setTextColor(getResources().getColor(R.color.black));
                mbinding.tabTag.setTextColor(getResources().getColor(R.color.black));
                mbinding.tabName.setTextColor(getResources().getColor(R.color.white));
                mbinding.tabLocation.setTextColor(getResources().getColor(R.color.black));
                mbinding.rlSearchContent.setBackground(getResources().getDrawable(R.drawable.rounded_box_white));
                mbinding.rvUsers.setAdapter(nameAdatper);
                if ( ! mbinding.searchEdit.getText().toString().isEmpty())
                    callSearchForFriendsByname();
//                else
//                    AlertDialog.showAlert(mActivity,getString(R.string.app_name),getString(R.string.no_name) , getString(R.string.txt_Done), "", false, null, null);
                break;
            case 3:
                mCurSelTabPos = 3;
                //Location
//                if (mPreSelTabPos == type)
//                    return;
                mPreSelTabPos =3;
                mbinding.etLocation.setVisibility(View.VISIBLE);
                mbinding.searchEdit.setVisibility(View.GONE);
                mbinding.searchEdit.setText(strTempSearchCity);
                mbinding.tabTop.setBackgroundResource(R.drawable.rounded_box_searchtab);
                mbinding.tabName.setBackgroundResource(R.drawable.rounded_box_searchtab);
                mbinding.tabTag.setBackgroundResource(R.drawable.rounded_box_searchtab);
                mbinding.tabLocation.setBackgroundResource(R.drawable.rounded_box_searchtab_active);
                mbinding.tabTop.setTextColor(getResources().getColor(R.color.black));
                mbinding.tabTag.setTextColor(getResources().getColor(R.color.black));
                mbinding.tabName.setTextColor(getResources().getColor(R.color.black));
                mbinding.tabLocation.setTextColor(getResources().getColor(R.color.white));
                mbinding.rlSearchContent.setBackground(getResources().getDrawable(R.drawable.rounded_box_white));
                mbinding.rvUsers.setAdapter(locationAdapter);
                if(! mbinding.searchEdit.getText().toString().isEmpty()) {
                    callSearchForUserBylocation();
                }
                break;
        }
    }

    private SearchByNameResponse mSearchByNameResponse;
    public  void callSearchForFriendsByname(){
        if(!isNotification)
            ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userid = SavePref.getInstance(mActivity).getUserdetail().getId();
        String name = mbinding.searchEdit.getText().toString();
        Call<SearchByNameResponse> call = mApiInterface.seachForFriendsByname(userid, name);
        call.enqueue(new Callback<SearchByNameResponse>() {
            @Override
            public void onResponse(Call<SearchByNameResponse> call, Response<SearchByNameResponse> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        mSearchByNameResponse = response.body();
                        if(mSearchByNameResponse!=null) {
                            if (mSearchByNameResponse.getStatus().equalsIgnoreCase("true")) {
                                if ( mSearchByNameResponse.getData() != null && mSearchByNameResponse.getData().size() > 0) {
                                    nameAdatper.setData(mSearchByNameResponse.getData());
                                    mbinding.txtNoData.setVisibility(View.GONE);
                                } else {
                                    mbinding.txtNoData.setVisibility(View.VISIBLE);
                                    nameAdatper.setData(null);
                                }
                            }else{
                                Toast.makeText(mActivity, mSearchByNameResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        System.out.println(response.errorBody());
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchByNameResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }


    private SearchTagResponse mSearchTagResponse;
    private List<CountTagModel> countTagModelList;

    public void callGetCountTag(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Call<JsonObject> call = mApiInterface.countTag( SavePref.getInstance(mActivity).getUserdetail().getId());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject != null && isAdded()) {
                        if (jsonObject.get("status").getAsBoolean()) {
                            JsonArray jsonArray = jsonObject.getAsJsonArray("data");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<CountTagModel>>() {}.getType();
                            try{
                                countTagModelList = gson.fromJson(jsonArray, type);
                                if(countTagModelList != null && countTagModelList.size() > 0){
                                    mbinding.txtNoData.setVisibility(View.GONE);
                                    tagAdapter.setData(countTagModelList);
                                }else{
                                    mbinding.txtNoData.setVisibility(View.VISIBLE);
                                    tagAdapter.setData(null);
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
                ProgressDialog.hideprogressbar();
            }
        });
    }

    public void callSeachForUserByhastag(String tagType){
        if(!isNotification)
            ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userid = SavePref.getInstance(mActivity).getUserdetail().getId();
        Call<SearchTagResponse> call = mApiInterface.seachForUserByhastag(userid, tagType);
        call.enqueue(new Callback<SearchTagResponse>() {
            @Override
            public void onResponse(Call<SearchTagResponse> call, Response<SearchTagResponse> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        mSearchTagResponse = response.body();

                        if(mSearchTagResponse!=null) {
                            if (mSearchTagResponse.getStatus().equalsIgnoreCase("true")) {
                                if(hashTag == null){
                                    mbinding.rvUsers.setAdapter(tagSubTagAdapter);
                                }
                                if (mSearchTagResponse.getData() != null && mSearchTagResponse.getData().size() > 0) {
                                    tagSubTagAdapter.setData(mSearchTagResponse.getData());
                                    mbinding.txtNoData.setVisibility(View.GONE);
                                } else {
                                    mbinding.txtNoData.setVisibility(View.VISIBLE);
                                    tagSubTagAdapter.setData(null);
                                }
                            } else {
                                Toast.makeText(mActivity,  mSearchTagResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        System.out.println(response.errorBody());
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchTagResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    private void callHashTagUsers(String parentTag, String tag, String type){
        mbinding.txtParentTag.setVisibility(View.VISIBLE);
        mbinding.txtSubTag.setVisibility(View.VISIBLE);
        mbinding.txtSubTag.setText(tag);
        mbinding.txtParentTag.setText(parentTag);

        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userid = SavePref.getInstance(mActivity).getUserdetail().getId();
        Call<NewTagResponse> call = mApiInterface.seachForHastag(userid, tag, type);
        call.enqueue(new Callback<NewTagResponse>() {
            @Override
            public void onResponse(Call<NewTagResponse> call, Response<NewTagResponse> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        NewTagResponse newTagResponse = response.body();
                        if(newTagResponse!=null) {
                            if (newTagResponse.status.equalsIgnoreCase("true")) {
                                mbinding.rvUsers.setAdapter(tagUserAdapter);
                                if ( newTagResponse.tagUserModels != null && newTagResponse.tagUserModels.size() > 0) {
                                    tagUserAdapter.setData(newTagResponse.tagUserModels, type, tag);
                                    mbinding.txtNoData.setVisibility(View.GONE);
                                } else {
                                    tagUserAdapter.setData(null, type, tag);
                                    mbinding.txtNoData.setVisibility(View.VISIBLE);
                                }
                            }else{
                                Toast.makeText(mActivity, newTagResponse.message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        System.out.println(response.errorBody());
                    }
                }
            }

            @Override
            public void onFailure(Call<NewTagResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    private void callHashTagUsers(String tag, String type){
        if(!isNotification)
            ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userid = SavePref.getInstance(mActivity).getUserdetail().getId();
        Call<NewTagResponse> call = mApiInterface.seachForHastag(userid, tag, type);
        call.enqueue(new Callback<NewTagResponse>() {
            @Override
            public void onResponse(Call<NewTagResponse> call, Response<NewTagResponse> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        NewTagResponse newTagResponse = response.body();
                        if(newTagResponse!=null) {
                            if (newTagResponse.status.equalsIgnoreCase("true")) {
                                mbinding.rvUsers.setAdapter(tagUserAdapter);
                                if ( newTagResponse.tagUserModels != null && newTagResponse.tagUserModels.size() > 0) {
                                    tagUserAdapter.setData(newTagResponse.tagUserModels, type, tag);
                                    mbinding.txtNoData.setVisibility(View.GONE);
                                } else {
                                    tagUserAdapter.setData(null, type, tag);
                                    mbinding.txtNoData.setVisibility(View.VISIBLE);
                                }
                            }else{
                                Toast.makeText(mActivity, newTagResponse.message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        System.out.println(response.errorBody());
                    }
                }
            }

            @Override
            public void onFailure(Call<NewTagResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    public void callSearchForUserBylocation(){
        if(!isNotification)
            ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userid = SavePref.getInstance(mActivity).getUserdetail().getId();
        String lat = SavePref.getInstance(mActivity).getUserdetail().getLat();
        String lng = SavePref.getInstance(mActivity).getUserdetail().getLng();
        String city = mbinding.etLocation.getText().toString();
        Call<SearchByNameResponse> call = mApiInterface.seachForUserBylocation(userid, lat, lng, city);
        call.enqueue(new Callback<SearchByNameResponse>() {
            @Override
            public void onResponse(Call<SearchByNameResponse> call, Response<SearchByNameResponse> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        mSearchByNameResponse = response.body();

                        if(mSearchByNameResponse!=null) {
                            if (mSearchByNameResponse.getStatus().equalsIgnoreCase("true")) {
                                if (mSearchByNameResponse.getData() != null && mSearchByNameResponse.getData().size() > 0) {
                                    locationAdapter.setData(mSearchByNameResponse.getData());
                                    mbinding.txtNoData.setVisibility(View.GONE);
                                } else {
                                    mbinding.txtNoData.setVisibility(View.VISIBLE);
                                    locationAdapter.setData(null);
                                }
                            } else {
                                Toast.makeText(mActivity,  mSearchByNameResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                                AlertDialog.showAlert(mActivity, getString(R.string.app_name), mSearchByNameResponse.getMessage(), getString(R.string.txt_Done),"", false, null, null);
                            }
                        }

                    } else{
                        System.out.println(response.errorBody());
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchByNameResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    public void callSearchForTop(){
        if(!isNotification)
            ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userid = SavePref.getInstance(mActivity).getUserdetail().getId();
        Call<SearchByNameResponse> call = mApiInterface.seachForTop(userid);
        call.enqueue(new Callback<SearchByNameResponse>() {
            @Override
            public void onResponse(Call<SearchByNameResponse> call, Response<SearchByNameResponse> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        mSearchByNameResponse = response.body();
                        if(mSearchByNameResponse!=null) {
                            if (mSearchByNameResponse.getStatus().equalsIgnoreCase("true")) {
                                if (mSearchByNameResponse.getData() != null && mSearchByNameResponse.getData().size() > 0) {
                                    topAdapter.setData(mSearchByNameResponse.getData());
                                    mbinding.txtNoData.setVisibility(View.GONE);
                                } else {
                                    mbinding.txtNoData.setVisibility(View.VISIBLE);
                                    topAdapter.setData(null);
                                }
                            } else {
                                Toast.makeText(mActivity,  mSearchByNameResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                                AlertDialog.showAlert(mActivity, getString(R.string.app_name), mSearchByNameResponse.getMessage(), getString(R.string.txt_Done), "", false, null, null);
                            }
                        }

                    } else {
                        System.out.println(response.errorBody());
//                        AlertDialog.showAlert(mActivity, getString(R.string.app_name), response.errorBody().toString(),
//                                getString(R.string.txt_Done), getString(R.string.cancel), false, null, null);
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchByNameResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }


    private String answer="", question;

    public String createAnswerString(ArrayList<QAData> list){
        int size = list.size();
        String strQuestions="";
        for(int i =0 ; i < size; i++){
            if( i != size - 1){
                strQuestions +=  list.get(i).getAnswer() + AppConstants.SEPERATOR;
            }else{
                strQuestions +=  list.get(i).getAnswer();
            }
        }
        return strQuestions;
    }


//    public void openSendAnswerDlg(NearUserDetail nearUserDetail){
//        dataQAList = new ArrayList<>();
//        String str = nearUserDetail.getQuestion();
//        if(str != null &&! str.isEmpty()){
//            int n;
//            while ((n =str.indexOf(AppConstants.SEPERATOR)) != -1){
//                String cutStr = str.substring(0, n);
//                dataQAList.add( new QAData(cutStr, ""));
//                str = str.substring(n+AppConstants.SEPERATOR.length());
//            }
//            dataQAList.add(new QAData(str, ""));
//        }
//
//        if(dataQAList.size() > 0){
//            SendAnswerAdapter adapter = new SendAnswerAdapter(getContext(), dataQAList, new SendAnswerAdapter.OnKeypressListener() {
//                @Override
//                public void onKeyPress(int index, String str) {
//                    dataQAList.get(index).setAnswer(str);
//                }
//            });
//
//            CustomSenderAnswerDlg dlg = new CustomSenderAnswerDlg(getContext(), nearUserDetail.getFullName(), adapter);
//            dlg.setOnItemClickListener(new CustomSenderAnswerDlg.OnDlgItemClickListener(){
//                public void onClick(){
//                    answer = createAnswerString(dataQAList);
//                    question = nearUserDetail.getQuestion();
//                    callSendFriendRequest();
//                }
//            });
//            dlg.setCanceledOnTouchOutside(false);
//            dlg.showDialog();
//        }else{
//            answer = "";
//            question="";
//            callSendFriendRequest();
//        }
//    }

    public void openSendAnswerDlg(String str, SearchByNameModel searchByNameModel ){
        dataQAList = new ArrayList<>();
        if(str != null &&! str.isEmpty()){
            int n;
            while ((n =str.indexOf(AppConstants.SEPERATOR)) != -1){
                String cutStr = str.substring(0, n);
                dataQAList.add( new QAData(cutStr, ""));
                str = str.substring(n+AppConstants.SEPERATOR.length());
            }
            dataQAList.add(new QAData(str, ""));
        }

        if(dataQAList.size() > 0){
            SendAnswerAdapter adapter = new SendAnswerAdapter(mActivity, dataQAList, new SendAnswerAdapter.OnKeypressListener() {
                @Override
                public void onKeyPress(int index, String str) {
                    dataQAList.get(index).setAnswer(str);
                }
            });

            if(mSelectedUserInfo != null){
                CustomSenderAnswerDlg dlg = new CustomSenderAnswerDlg(mActivity, mSelectedUserInfo.getFullName(), adapter);
                dlg.setOnItemClickListener(new CustomSenderAnswerDlg.OnDlgItemClickListener(){
                    public void onClick(){
                        answer = createAnswerString(dataQAList);
                        question = mSelectedUserInfo.getQuestion();
                        callSendFriendRequest(searchByNameModel);
                    }
                });
                dlg.setCanceledOnTouchOutside(false);
                dlg.showDialog();
            }else{
                CustomSenderAnswerDlg dlg = new CustomSenderAnswerDlg(mActivity, mSelectedUserInfoNameModel.getFull_name(), adapter);
                dlg.setOnItemClickListener(new CustomSenderAnswerDlg.OnDlgItemClickListener(){
                    public void onClick(){
                        answer = createAnswerString(dataQAList);
                        question = mSelectedUserInfoNameModel.getQuestion();
                        callSendFriendRequest(searchByNameModel);
                    }
                });
                dlg.setCanceledOnTouchOutside(false);
                dlg.showDialog();
            }

        }else{
            answer = "";
            question="";
            callSendFriendRequest(searchByNameModel);
        }
    }

    public void refreshUIByLocal(){
        switch (mCurSelTabPos){
            case 0:
                topAdapter.setData(mSearchByNameResponse.getData());
                break;
            case 1:
                tagAdapter.setData(countTagModelList);
                break;
            case 2:
                nameAdatper.setData(mSearchByNameResponse.getData());
                break;
            case 3:
                locationAdapter.setData(mSearchByNameResponse.getData());
                break;
        }
    }

    public void refreshUI(){
        isNotification = true;
        switch (mCurSelTabPos){
            case 0:
                callSearchForTop();
                break;
            case 1:
                callGetCountTag();
                break;
            case 2:
                callSearchForFriendsByname();
                break;
            case 3:
                callSearchForUserBylocation();
                break;
        }
        isNotification = false;
    }

    public void callSendFriendRequest(SearchByNameModel searchByNameModel){
        if(!isNotification)
            ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int fromUserId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();

        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().FROM_USER, fromUserId);
        mparams.put(ApiClass.getmApiClass().TO_USER, searchByNameModel.getId());
        mparams.put(ApiClass.getmApiClass().TO_ANS, answer);
        mparams.put(ApiClass.getmApiClass().QUESTION, question);

        Call<Basic_Response> call;
        call = mApiInterface.sendFriendRequest(mparams);
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Basic_Response basic_response = response.body();
                        if(basic_response.getStatus().equalsIgnoreCase("true")) {
                            if(! answer.isEmpty())
                                Toast.makeText(mActivity, getString(R.string.answer_success) , Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(mActivity, getString(R.string.request_success) , Toast.LENGTH_SHORT).show();
                                searchByNameModel.setIsFriend("3");
                                refreshUIByLocal();
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

    private void callUnfriend(SearchByNameModel searchByNameModel){
        if(!isNotification)
            ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int fromUserId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().FROM_USER, fromUserId);
        mparams.put(ApiClass.getmApiClass().TO_USER, searchByNameModel.getId());

        Call<Basic_Response> call;
        call = mApiInterface.unFriend(mparams);
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Basic_Response basic_response = response.body();
                        if(basic_response.getStatus().equalsIgnoreCase("true")){
                            searchByNameModel.setIsFriend("2");
                            refreshUIByLocal();
                        }else
                            Toast.makeText(mActivity,  basic_response.getMessage(), Toast.LENGTH_SHORT).show();
//                            AlertDialog.showAlert(mActivity, getString(R.string.app_name),basic_response.getMessage(),
//                                getString(R.string.txt_Done), getString(R.string.cancel), false, null, null);

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


    private void callRejectFriendRequest(SearchByNameModel searchByNameModel){
        if(!isNotification)
            ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int fromUserId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().FROM_USER, fromUserId);
        mparams.put(ApiClass.getmApiClass().TO_USER, searchByNameModel.getId());

        Call<Basic_Response> call;
        call = mApiInterface.rejectFriendRequest(mparams);
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Basic_Response basic_response = response.body();
                        if(basic_response.getStatus().equalsIgnoreCase("true")){
                            searchByNameModel.setIsFriend("2");
                            refreshUIByLocal();
                        }else
                            Toast.makeText(mActivity,  basic_response.getMessage(), Toast.LENGTH_SHORT).show();

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
