package com.app.khaleeji.Fragments;

import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;

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
import com.app.khaleeji.Activity.LoginActivity;
import com.app.khaleeji.Adapter.GooglePlacesAutocompleteAdapter;
import com.app.khaleeji.R;
import com.app.khaleeji.databinding.FragmentHometownBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Model.AreaModel;
import Utility.ApiClass;
import Utility.Fragment_Process;
import Utility.SavePref;


public class HomeTownFragment extends BaseFragment implements AdapterView.OnItemClickListener{

    static String TAG = HomeTownFragment.class.getName();
    private Context mContext;
    private FragmentHometownBinding mbinding;

    private String mStrSelGender;
    private String mStrSelBirthday;
    private String mStrHomeTown;
    private boolean mIsAllowedDOB;
    private GooglePlacesAutocompleteAdapter mGooglePlacesAutocompleteAdapter;
    private static Thread mthread = null;

    private ChooseGenderFragment.SignUpData mSignUpData;

    public HomeTownFragment(ChooseGenderFragment.SignUpData signUpData, String strGender, String strBirthday, boolean isAllowedDOB){
        mStrSelGender = strGender;
        mStrSelBirthday = strBirthday;
        mIsAllowedDOB = isAllowedDOB;
        mSignUpData = signUpData;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = mActivity;
        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_hometown, container, false);
        View rootView = mbinding.getRoot();
        if(Build.VERSION.SDK_INT >= 21)
            rootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.primaryDarkColor);
        initView();
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void initView(){
        mbinding.txtContinue.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if( mStrHomeTown != null && !mStrHomeTown.isEmpty() )
                    Fragment_Process.addFragment(mActivity.getSupportFragmentManager(),new ProfileQuestionFragment(mSignUpData, mStrSelGender, mStrSelBirthday,mIsAllowedDOB,mStrHomeTown),mActivity, R.id.framelayout_login);
            }
        });

        mbinding.imgBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((LoginActivity)mActivity).back();
            }
        });

        mbinding.imgLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mbinding.txtHomeTown.setText(getCity());
            }
        });

        mGooglePlacesAutocompleteAdapter = new GooglePlacesAutocompleteAdapter(mActivity, R.layout.row_placelist);
        mbinding.txtHomeTown.setAdapter(mGooglePlacesAutocompleteAdapter);

        mbinding.txtHomeTown.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mStrHomeTown = s.toString();
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

        mbinding.txtHomeTown.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        try {
            String str = adapterView.getItemAtPosition(position).toString();
            mbinding.txtHomeTown.setText(str);
        } catch (Exception e) {

        }
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





}
