package com.app.khaleeji.Fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Adapter.GooglePlacesAutocompleteAdapter;
import com.app.khaleeji.Adapter.ProfileQuestionAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.GulfProfiledata;
import com.app.khaleeji.Response.GulfUpdateProfile;
import com.app.khaleeji.Response.UserDetails;
import com.app.khaleeji.databinding.FragmentProfileEditBinding;
//import com.applozic.mobicomkit.Applozic;
//import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
//import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
//import com.applozic.mobicomkit.api.account.user.User;
//import com.applozic.mobicomkit.api.account.user.UserService;
//import com.applozic.mobicomkit.contact.AppContactService;
//import com.applozic.mobicomkit.listners.AlCallback;
//import com.applozic.mobicomkit.listners.AlLoginHandler;
//import com.applozic.mobicomkit.listners.AlPushNotificationHandler;
//import com.applozic.mobicommons.people.contact.Contact;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.AppConstants;
import CustomView.CustomCountryDialog;
import CustomView.CustomEditText;
import CustomView.SelectPicPopupWindow;
import Model.AreaModel;
import Utility.ApiClass;
import Utility.DebugLog;
import Utility.ProgressDialog;
import Utility.SavePref;
import Utility.Utils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class ProfileEditFragment  extends BaseFragment implements AdapterView.OnItemClickListener{

    private Context mContext;
    private View mRootView;
    private FragmentProfileEditBinding mbinding;
    private int mSelectedIndex, mPreSelectedIndex;
    private CustomCountryDialog mCustomCountryDialog;
    private String[] strDate={"January","February", "March", "April", "May", "June","July","August","September","October","November","December"};
    // Spinner Drop down elements
    private String mStrCountryName="";
    private String mStrSelQuestions="";

    private boolean isMale;
    private SelectPicPopupWindow selectPicPopupWindow;
    private String strImagepath, strBkImagePath;
    private String mStrBirthday;
    private ArrayList<String> mListProfileQuestions = new ArrayList();
    private ProfileQuestionAdapter listAdapter;
    private GooglePlacesAutocompleteAdapter mGooglePlacesAutocompleteAdapter;
    private static Thread mthread = null;
    private boolean isProfileFg = true;
    GulfProfiledata mData;


    public ProfileEditFragment() {
        // Required empty public constructor
    }

    public ProfileEditFragment(GulfProfiledata data) {
        // Required empty public constructor
       mData = data;
    }

    public static ProfileEditFragment newInstance(GulfProfiledata data) {
        ProfileEditFragment fragment = new ProfileEditFragment(data);
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent (MessageEvent messageEvent) {
        if(messageEvent.getType() == MessageEvent.MessageType.NEW_MSG_NOTIFICATION){
            mbinding.imgBadge.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = mActivity;
        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_edit, container, false);
        mRootView = mbinding.getRoot();
        if(Build.VERSION.SDK_INT >= 21)
            mRootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        initView();
        return mRootView;
    }

    private void initView(){
        ((MainActivity)mActivity).hide();

        if(SavePref.getInstance(mActivity).getNewMsgNotification()){
            mbinding.imgBadge.setVisibility(View.VISIBLE);
        }else{
            mbinding.imgBadge.setVisibility(View.INVISIBLE);
        }

        mbinding.imgBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).back();
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

        mbinding.rlFemale.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setFemale();
            }
        });

        mbinding.rlMale.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               setMale();
            }
        });

        listAdapter = new ProfileQuestionAdapter(mListProfileQuestions  ,getContext(), new ProfileQuestionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int index) {
                mSelectedIndex = index;
            }
        });
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mbinding.rvQuestions.setLayoutManager(mLayoutManager);
        mbinding.rvQuestions.setAdapter(listAdapter);

        mbinding.imgQuestionAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if( ! mbinding.txtQuestions.getText().toString().isEmpty() && mListProfileQuestions.size() < 3) {
                    mListProfileQuestions.add(mbinding.txtQuestions.getText().toString());
                    listAdapter.notifyDataSetChanged();
                    mbinding.txtQuestions.setText("");
                    if(mListProfileQuestions.size() == 3){
                        hideSoftKeyboard(mActivity);
                        mbinding.txtQuestions.setFocusable(false);
                    }
                }
            }
        });

        mbinding.imgQuestionRemove.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(mListProfileQuestions.size() > 0 && mSelectedIndex >= 0){
                    if(mSelectedIndex == mPreSelectedIndex){
                        mListProfileQuestions.remove(mListProfileQuestions.size()-1);
                    }else{
                        mListProfileQuestions.remove(mSelectedIndex);
                        mPreSelectedIndex = mSelectedIndex;
                    }
                    listAdapter.notifyDataSetChanged();
                }
                if( mListProfileQuestions.size() < 3) {
                    mbinding.txtQuestions.setFocusableInTouchMode(true);
                }
            }
        });

        mCustomCountryDialog = new CustomCountryDialog(mContext, new CustomCountryDialog.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                mStrCountryName = getResources().getString(AppConstants.COUNTRY_NAME[pos]);
                mbinding.txtCountry.setText(mStrCountryName);
            }
        });
        mCustomCountryDialog.setCanceledOnTouchOutside(false);
        mbinding.rlCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mCustomCountryDialog.showDialog();
            }
        });

      /*  mbinding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();

                // Showing selected spinner item
               // Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        mbinding.txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_UpdateProfile();
            }
        });



        // Creating adapter for spinner
       // ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //mbinding.spinner.setAdapter(dataAdapter);

        mbinding.checkPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mbinding.checkPrivate.setChecked(false);
                mbinding.checkPublic.setChecked(true);
                mbinding.llPrivateCheckBox.setVisibility(View.GONE);
            }
        });
        mbinding.checkPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mbinding.checkPrivate.setChecked(true);
                mbinding.checkPublic.setChecked(false);
                mbinding.llPrivateCheckBox.setVisibility(View.VISIBLE);
            }
        });

        mbinding.rlDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                DatePickerDialog picker = new DatePickerDialog(mActivity, R.style.DatePickerDialogTheme,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                               setBirthDate(year, monthOfYear, dayOfMonth);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        mbinding.imgMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isProfileFg = false;
                if(checkProfilePermission()) {
                    openSelectDlg();
                }

            }
        });

        mbinding.imgCircleProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isProfileFg = true;
                if(checkProfilePermission()) {
                    openSelectDlg();
                }
            }
        });

        mbinding.imgLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mbinding.etLocation.setText(getCity());
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
        initFromApi();
    }



    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        try {
            String str = adapterView.getItemAtPosition(position).toString();
            mbinding.etLocation.setText(str);
        } catch (Exception e) {

        }
    }


    private void getLocationLatLongfromAddress(String address) {
        try {
            List<Address> addresses;
            Geocoder geocoder;
            geocoder = new Geocoder(mActivity, Locale.getDefault());
            List<Address> returnedaddresses = geocoder.getFromLocationName(address, 1);

            if (returnedaddresses != null) {
                lat = returnedaddresses.get(0).getLatitude();
                lng = returnedaddresses.get(0).getLongitude();
//                DebugLog.log(1, TAG, "lat " + lat + " lng" + lng);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
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


    public void initFromApi(){
        GulfProfiledata gulfProfileData = SavePref.getInstance(mActivity).getGulfProfileResponse().getData();
        mbinding.etBio.setText(gulfProfileData.getBio());
        mbinding.etFullName.setText(gulfProfileData.getFullName());
        if(gulfProfileData.getGender().trim().equals("male")){
            setMale();
        }else{
            setFemale();
        }
        mStrCountryName = getCountryNameFromCode(gulfProfileData.getCountryId());
        mbinding.txtCountry.setText(mStrCountryName);
        String[] separated = gulfProfileData.getDob().split("-");
        if(separated != null && separated.length == 3){
            setBirthDate(separated[0], separated[1], separated[2]);
        }
        if(gulfProfileData.getPrivacy().trim().toLowerCase().equals("public")){
            mbinding.checkPrivate.setChecked(false);
            mbinding.checkPublic.setChecked(true);
            mbinding.llPrivateCheckBox.setVisibility(View.GONE);
        }else{
            mbinding.checkPrivate.setChecked(true);
            mbinding.checkPublic.setChecked(false);
            mbinding.llPrivateCheckBox.setVisibility(View.VISIBLE);
        }

        mListProfileQuestions.clear();
        String str = gulfProfileData.getQuestion();
        if(str != null &&! str.isEmpty()){
            int n;
            while ((n =str.indexOf(AppConstants.SEPERATOR)) != -1){
                String cutStr = str.substring(0, n);
                mListProfileQuestions.add(cutStr);
                str = str.substring(n+AppConstants.SEPERATOR.length());
            }
            mListProfileQuestions.add(str);
            listAdapter.notifyDataSetChanged();
        }

        if(gulfProfileData.all_other_to_seeMyfriends == 1){
            mbinding.cbAllowFriendList.setChecked(true);
        }else{
            mbinding.cbAllowFriendList.setChecked(false);
        }

        if(gulfProfileData.getAll_other_see_username() == 1){
            mbinding.chAllUserUsername.setChecked(true);
        }else{
            mbinding.chAllUserUsername.setChecked(false);
        }

        if(gulfProfileData.getAll_other_to_seeprofile() == 1){
            mbinding.chAllUserPicture.setChecked(true);
        }else{
            mbinding.chAllUserPicture.setChecked(false);
        }

        if(gulfProfileData.getAll_other_to_seeDOB() == 1){
            mbinding.chAllUserDOB.setChecked(true);
        }else{
            mbinding.chAllUserDOB.setChecked(false);
        }

        if((gulfProfileData.getProfilePicture()!=null && gulfProfileData.getProfilePicture().trim().length()>0))
        {
            Picasso.with(mActivity).load(ApiClass.ImageBaseUrl + gulfProfileData.getProfilePicture()).fit().centerCrop().into(mbinding.imgCircleProfile);
        }

        if((gulfProfileData.getBg_picture()!=null && gulfProfileData.getBg_picture().trim().length()>0))
        {
            Picasso.with(mActivity).load(ApiClass.ImageBaseUrl + gulfProfileData.getBg_picture()).fit().centerCrop().into(mbinding.imgMyProfile);
        }

        mbinding.etLocation.setText(gulfProfileData.getCountry());
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

    public void setBirthDate(int year, int monthOfYear, int dayOfMonth) {
        mbinding.txtDate.setText(dayOfMonth + "-" + strDate[(monthOfYear + 1)-1] + "-" + year);
        mStrBirthday = year + "-" + monthOfYear+1 +"-"+dayOfMonth;
    }

    public void setBirthDate(String year, String monthOfYear, String dayOfMonth) {
        mbinding.txtDate.setText(dayOfMonth + "-" + strDate[Integer.valueOf(monthOfYear).intValue()-1] + "-" + year);
        mStrBirthday = year + "-" + monthOfYear +"-"+dayOfMonth;
    }

    public void setFemale(){
        mbinding.rlFemale.setBackground(getResources().getDrawable(R.drawable.rounded_box_yellow_20));
        mbinding.rlMale.setBackground(getResources().getDrawable(R.drawable.rounded_box_yellow_border_20));
        mbinding.imgFemale.setImageDrawable(getResources().getDrawable(R.drawable.gender_male_white));
        mbinding.imgMale.setImageDrawable(getResources().getDrawable(R.drawable.gender_female_yellow));
        mbinding.txtFemale.setTextColor(getResources().getColor(R.color.white));
        mbinding.txtMale.setTextColor(getResources().getColor(R.color.yellow_dark));
        isMale = false;
    }

    public void setMale(){
        mbinding.rlFemale.setBackground(getResources().getDrawable(R.drawable.rounded_box_yellow_border_20));
        mbinding.rlMale.setBackground(getResources().getDrawable(R.drawable.rounded_box_yellow_20));
        mbinding.imgFemale.setImageDrawable(getResources().getDrawable(R.drawable.gender_female_yellow));
        mbinding.imgMale.setImageDrawable(getResources().getDrawable(R.drawable.gender_male_white));
        mbinding.txtFemale.setTextColor(getResources().getColor(R.color.yellow_dark));
        mbinding.txtMale.setTextColor(getResources().getColor(R.color.white));
        isMale = true;
    }

    public static final int REQUEST_ID_PROFILE_PERMISSIONS = 3;
    private boolean checkProfilePermission(){
        int permissionCamera = ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA);
        int permissionWriteStorage = ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(mActivity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_PROFILE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_PROFILE_PERMISSIONS:
                Map<String, Integer> perm = new HashMap<>();
                perm.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perm.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perm.put(permissions[i], grantResults[i]);
                    if (perm.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perm.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        openSelectDlg();
                    }
                }
                return;
        }
    }

    private void call_UpdateProfile() {
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);

        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), SavePref.getInstance(mActivity).getUserdetail().getId().toString());
        RequestBody fullname = RequestBody.create(MediaType.parse("text/plain"), mbinding.etFullName.getText() != null ? mbinding.etFullName.getText().toString().trim():"");
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"),mData != null ? mData.getEmail() :"");
        RequestBody countryId = RequestBody.create(MediaType.parse("text/plain") ,mData != null ? mData.getCountryId().toString() : "0");
        RequestBody gender = RequestBody.create(MediaType.parse("text/plain"), isMale? "male" : "female");
        RequestBody countryName = RequestBody.create(MediaType.parse("text/plain"), mStrCountryName != null ? mStrCountryName :"");
        RequestBody birth = RequestBody.create(MediaType.parse("text/plain"), mStrBirthday);
        RequestBody visibility = RequestBody.create(MediaType.parse("text/plain"), mData != null ? mData.getVisibility() : "everyone");
        RequestBody isPublic = RequestBody.create(MediaType.parse("text/plain"), mbinding.checkPublic.isChecked() ? "public" : "private");
        RequestBody bio = RequestBody.create(MediaType.parse("text/plain"), mbinding.etBio.getText() != null ? mbinding.etBio.getText().toString(): "");
        RequestBody profileQuestion = RequestBody.create(MediaType.parse("text/plain"), getProfileQuestions());
//        RequestBody allowUsername = RequestBody.create(MediaType.parse("text/plain"), mbinding.chAllUserUsername.isChecked()? "1" : "0");
//        RequestBody allowPic = RequestBody.create(MediaType.parse("text/plain"), mbinding.chAllUserPicture.isChecked()? "1" : "0");
        RequestBody allowUsername = RequestBody.create(MediaType.parse("text/plain"), "1");
        RequestBody allowPic = RequestBody.create(MediaType.parse("text/plain"),  "1" );
        RequestBody allowDob = RequestBody.create(MediaType.parse("text/plain"), mbinding.chAllUserDOB.isChecked()? "1" : "0");
        RequestBody mobileNo = RequestBody.create(MediaType.parse("text/plain"),mData!=null ? mData.getMobileNumber():"");
        RequestBody username = RequestBody.create(MediaType.parse("text/plain"),mData!=null ? mData.getUsername():"");
        RequestBody allowMyFriend = RequestBody.create(MediaType.parse("text/plain"), mbinding.cbAllowFriendList.isChecked()?"1": "0");

        MultipartBody.Part body = null;
        if(strImagepath!=null && new File(strImagepath).exists())
        {
            File file = new File(strImagepath);
            RequestBody requestFile =RequestBody.create(MediaType.parse("multipart/form-data"), file);
            // MultipartBody.Part is used to send also the actual file name
            body = MultipartBody.Part.createFormData(ApiClass.getmApiClass().PROFILE_PICTURE, file.getName(), requestFile);
        }

        MultipartBody.Part bodyBg = null;
        if(strBkImagePath!=null && new File(strBkImagePath).exists())
        {
            File file = new File(strBkImagePath);
            RequestBody requestFile =RequestBody.create(MediaType.parse("multipart/form-data"), file);
            // MultipartBody.Part is used to send also the actual file name
            bodyBg = MultipartBody.Part.createFormData("bg_picture", file.getName(), requestFile);
        }

        Call<GulfUpdateProfile> call = mApiInterface.updateProfile(id, fullname, email, countryId, gender,countryName
                ,birth,visibility, isPublic,bio, profileQuestion,allowUsername, allowPic, allowDob, mobileNo, username,
                allowMyFriend, body, bodyBg);

        call.enqueue(new Callback<GulfUpdateProfile>() {
            @Override
            public void onResponse(Call<GulfUpdateProfile> call, Response<GulfUpdateProfile> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    GulfUpdateProfile mOtpResponse = response.body();
                    if(mOtpResponse!=null && isAdded() )
                    {
                        if (mOtpResponse.getStatus().equalsIgnoreCase("true")) {
                            UserDetails muserdetail= mOtpResponse.getUserDetails();
                            String token = SavePref.getInstance(mActivity).getUserdetail().getToken();
                            muserdetail.setToken(token);
                            SavePref.getInstance(mActivity).saveUserdetail(muserdetail);

//                            if(Applozic.isConnected(mActivity)) {
////                                User user = new User();
////                                user.setUserId(mOtpResponse.getUserDetails().getId() + ""); //userId it can be any unique user identifier NOTE : +,*,? are not allowed chars in userId.
////                                user.setDisplayName(mOtpResponse.getUserDetails().getFullName()); //displayName is the name of the user which will be shown in chat messages
////                                user.setEmail(mOtpResponse.getUserDetails().getEmail()); //optional
////                                user.setImageLink(ApiClass.ImageBaseUrl + mOtpResponse.getUserDetails().getProfilePicture());
////                                user.setAuthenticationTypeId(User.AuthenticationType.APPLOZIC.getValue());  //User.AuthenticationType.APPLOZIC.getValue() for password verification from Applozic server and User.AuthenticationType.CLIENT.getValue() for access Token verification from your server set access token as password
////
////                                UserService.getInstance(mActivity).updateUser(user, new AlCallback() {
////                                    @Override
////                                    public void onSuccess(Object response) {
////                                        Log.i("User", "Update success ");
////                                    }
////
////                                    @Override
////                                    public void onError(Object error) {
////                                        Log.i("User", "Update failed ");
////                                    }
////                                });
////                            }

                            MessageEvent messageEvent = new MessageEvent();
                            messageEvent.setType(MessageEvent.MessageType.PROFILE_REFRESH);
                            EventBus.getDefault().postSticky(messageEvent);

                           ((MainActivity)mActivity).updateProfile();
                           ((MainActivity)mActivity).openProfileFragment();
                        }
                        Toast.makeText(mActivity, mOtpResponse.getMessage() , Toast.LENGTH_SHORT).show();

                    }
                } else {
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<GulfUpdateProfile> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });

    }

    public String getProfileQuestions(){
        int size = mListProfileQuestions.size();
        for(int i =0 ; i < size; i++){
            if( i != size - 1){
                mStrSelQuestions +=  mListProfileQuestions.get(i) + AppConstants.SEPERATOR;
            }else{
                mStrSelQuestions +=  mListProfileQuestions.get(i);
            }

        }

        return mStrSelQuestions;
    }

    private void openSelectDlg(){
        View view = ((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_layout_camera, null);
        selectPicPopupWindow = new SelectPicPopupWindow(view, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btCancel:
                        selectPicPopupWindow.dismiss();
                        break;
                    case R.id.btCamera:
                        selectPicPopupWindow.dismiss();
                        openCamera();
                        break;
                    case R.id.btGallery:
                        selectPicPopupWindow.dismiss();
                        openGallery();
                        break;
                }
            }
        }, 1);
        selectPicPopupWindow.showAtLocation(mRootView, Gravity.CENTER, 0, 0);
    }

    private Uri imageUri;
    public void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = mActivity.getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 1);
    }

    public void openGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 2);
    }

    private void captureImageResult(File compressedFile) {
        try {
//            Bitmap originalBitmap = (Bitmap) data.getExtras().get("data");
//            int nh = (int) (originalBitmap.getHeight() * (512.0 / originalBitmap.getWidth()));
//            Bitmap compressedBitmap = Bitmap.createScaledBitmap(originalBitmap, 512, nh, true);
//            File compressedFile = Utils.createFileFromBitmap(compressedBitmap, mContext);
            if(isProfileFg){
                strImagepath = compressedFile.getPath();
            }else{
                strBkImagePath = compressedFile.getPath();
            }
            if(isProfileFg){
                Glide.with(mActivity).load(compressedFile.getPath()).centerCrop().into( mbinding.imgCircleProfile);
            }else{
                Glide.with(mActivity).load(compressedFile.getPath()).centerCrop().into( mbinding.imgMyProfile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == 1){
                Uri sourceUri = imageUri;
                File file = getImageFile();
                if(file == null)
                    return;
                Uri destinationUri = Uri.fromFile(file);
                if(isProfileFg){
                    UCrop.of(sourceUri, destinationUri)
                            .withAspectRatio(1,1)
                            .start(mActivity, this);
                }else{
                    UCrop.of(sourceUri, destinationUri)
                            .withAspectRatio(16, 9)
                            .start(mActivity, this);
                }
            }else if (requestCode == 2 && data != null) {
                Uri sourceUri = data.getData();
                File file = getImageFile();
                if(file == null)
                    return;
                Uri destinationUri = Uri.fromFile(file);
                if(isProfileFg){
                    UCrop.of(sourceUri, destinationUri)
                            .withAspectRatio(1,1)
                            .start(mActivity, this);
                }else{
                    UCrop.of(sourceUri, destinationUri)
                            .withAspectRatio(16, 9)
                            .start(mActivity, this);
                }

            }else if (requestCode == UCrop.REQUEST_CROP) {
                final Uri resultUri = UCrop.getOutput(data);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                            mActivity.getContentResolver(), resultUri);
                    int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                    File compressedFile = Utils.createFileFromBitmap(scaledBitmap, mContext);
                    captureImageResult(compressedFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private File getImageFile() {
        try {
            String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
            File storageDir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DCIM
                    ), "Camera"
            );
            if(!storageDir.exists())
                storageDir.mkdir();
            File file = File.createTempFile(
                    imageFileName, ".jpg", storageDir
            );
//            String currentPhotoPath = "file:" + file.getAbsolutePath();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
    }


}
