package com.app.khaleeji.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Adapter.GooglePlacesAutocompleteAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.Basic_Response;
import com.app.khaleeji.Response.HotSpotDatum;
import com.app.khaleeji.Response.HotSpotResponse;
import com.app.khaleeji.Response.MapCategoryResponse;
import com.app.khaleeji.Response.MapCategory_Datum;
import com.app.khaleeji.databinding.FragmentMapBinding;
import com.app.khaleeji.databinding.HotspotmapBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.AppConstants;
import Model.AreaModel;
import Utility.ApiClass;
import Utility.CheckConnection;
import Utility.DebugLog;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import Utility.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeetUpMapFragments extends BaseFragment implements GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, View.OnClickListener,
        AdapterView.OnItemClickListener, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = MeetUpMapFragments.class.getSimpleName();

    public static final String STATUS = "status";
    public static final String OK = "OK";
    public static final String ZERO_RESULTS = "ZERO_RESULTS";
    public static final String GEOMETRY = "geometry";
    public static final String LOCATION = "location";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lng";
    public static final String SUPERMARKET_ID = "id";
    public static final String NAME = "name";
    public static final String PLACE_ID = "place_id";
    public static final String REFERENCE = "reference";
    public static final String VICINITY = "vicinity";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/textsearch";
    private static final String OUT_JSON = "/json";
    public static int category_id = 6;
    GooglePlacesAutocompleteAdapter mGooglePlacesAutocompleteAdapter;
    double lat, lng;
    HotSpotResponse mHotspotResponse;
    HashMap<LatLng, Integer> Marker_hashmap = new HashMap<>();
    Thread mthread;
    Bitmap bitmap = null;
    String category_url = "";
    CameraPosition cameraPosition_old = null;

    private GoogleApiClient googleApiClient;
    private double longitude;
    private double latitude;
    private double centerLng;
    private double centerLat;
    private FragmentMapBinding mbinding;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private MapCategoryResponse mMapCategoryResponse;

    public MeetUpMapFragments(){

    }

    public static MeetUpMapFragments newInstance() {
        MeetUpMapFragments fragment = new MeetUpMapFragments();

        return fragment;
    }

    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        try {
            mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);
            view = mbinding.getRoot();
            Marker_hashmap.clear();
            mbinding.imgMenu.setOnClickListener(this);
            mbinding.imgSearch.setOnClickListener(this);
            mbinding.imgMessage.setOnClickListener(this);
            mbinding.ivGlLogo.setOnClickListener(this);
            mbinding.ivPeople.setOnClickListener(this);
            mbinding.ivPeople.setOnClickListener(this);
            mbinding.ivGps.setOnClickListener(this);
            initView();

            //set rounded screen
            if(Build.VERSION.SDK_INT >= 21)
                view.setClipToOutline(true);
            ((BaseActivity)mActivity).setStatusBarColor(R.color.darkPurple);
            return view;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        try {
            Utils.hideSoftKeyboard(mActivity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (mGooglePlacesAutocompleteAdapter.getList().size() > position) {
                lat = mGooglePlacesAutocompleteAdapter.getList().get(position).getLat();
                lng = mGooglePlacesAutocompleteAdapter.getList().get(position).getLng();
                if (mMap != null) {
                    CameraUpdate update = CameraUpdateFactory.newLatLng(new LatLng(lat, lng));
                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(13);
                    mMap.moveCamera(update);
                    mMap.animateCamera(zoom);
                    createOnlineMarker(lat, lng, "", "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public Marker createOnlineMarker(double latitude, double longitude, String title, String snippet) {
        return mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_red_32)));  //map_marker  marker_bag_ldpi    gl_red_location
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        googleApiClient = new GoogleApiClient.Builder(mActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
        mMap.clear();
        mbinding.ivZoomin.setOnClickListener(this);
        mbinding.ivZoomout.setOnClickListener(this);
        mbinding.ivGps.setOnClickListener(this);
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                centerLat = cameraPosition.target.latitude;
                centerLng = cameraPosition.target.longitude;
            }
        });
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
            }
        });
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                if (reason == REASON_GESTURE) {
                    if (cameraPosition_old != null) {
                        if (cameraPosition_old != null && cameraPosition_old.target.latitude == 0.0) {
                            DebugLog.log(1, "distance", "onCameraChange latitude 0.0");
                            cameraPosition_old = mMap.getCameraPosition();
                        }
                        Location old_location = new Location("");
                        old_location.setLatitude(cameraPosition_old.target.latitude);
                        old_location.setLongitude(cameraPosition_old.target.longitude);
                        Location maplocation = new Location("");
                        maplocation.setLatitude(mMap.getCameraPosition().target.latitude);
                        maplocation.setLongitude(mMap.getCameraPosition().target.longitude);
                        double distance = maplocation.distanceTo(old_location) / 1000;//in meters
                        if (distance > 1) {
                            lat = mMap.getCameraPosition().target.latitude;
                            lng = mMap.getCameraPosition().target.longitude;
                            cameraPosition_old = mMap.getCameraPosition();

                        }
                        DebugLog.log(1, "distance", "map drag : " + distance);
                    }
                } else if (reason == REASON_API_ANIMATION) {

                } else if (reason == REASON_DEVELOPER_ANIMATION) {

                }
            }
        });
        mMap.setOnMarkerClickListener(null);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (Marker_hashmap.size() > 0 && Marker_hashmap.containsKey(marker.getPosition())) {
                    int pos = Marker_hashmap.get(marker.getPosition()).intValue();
                    try {
                        if (mHotspotResponse.getData().get(pos).getIsHotspot()) {
                            Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), HotSpotDetailsFragment.newInstance(mHotspotResponse.getData().get(pos)),mActivity, R.id.framelayout_main);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });


        Log.e(TAG, "longitude : " + longitude);
        Log.e(TAG, "latitude : " + latitude);
        CameraUpdate update = CameraUpdateFactory.newLatLng(new LatLng(SavePref.getInstance(mActivity).getLatitude(), SavePref.getInstance(mActivity).getLongitude()));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
        mMap.moveCamera(update);
        mMap.animateCamera(zoom);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                performGlHotspotClicked();
            }
        }, 500);

        loadHotspots();
    }

    private void initView() {
        ((MainActivity)mActivity).hideCheckedInMark();
        ((MainActivity)mActivity).showMeetupObjects();

        mbinding.imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) mActivity).back();
            }
        });

        if(SavePref.getInstance(mActivity).getNewMsgNotification()){
            mbinding.imgBadge.setVisibility(View.VISIBLE);
        }else{
            mbinding.imgBadge.setVisibility(View.INVISIBLE);
        }

        lat = SavePref.getInstance(mActivity).getLatitude();
        lng = SavePref.getInstance(mActivity).getLongitude();
        mGooglePlacesAutocompleteAdapter = new GooglePlacesAutocompleteAdapter(mActivity, R.layout.row_placelist);
        mGooglePlacesAutocompleteAdapter.setOnClickListener(new GooglePlacesAutocompleteAdapter.OnclickListener() {
            @Override
            public void OnSerachPlace(final String place) {
                if (mthread != null) {
                    mthread.interrupted();
                    mthread = null;
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
        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.flmapcontainers, mapFragment).commit();
        }
        if (mapFragment != null)
            mapFragment.getMapAsync(this);
        mbinding.ivZoomout.setOnClickListener(this);
        mbinding.ivGps.setOnClickListener(this);
        mbinding.ivZoomin.setOnClickListener(this);
        if (CheckConnection.isNetworkAvailable(mActivity)) {
            callCategory();
        } else {
            Toast.makeText(mActivity, getString(R.string.network_down), Toast.LENGTH_SHORT).show();
        }
    }

    private void callCategory() {
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = ApiClass.getmApiClass().buildDefaultParams(mActivity);
        Call<MapCategoryResponse> call;
        call = mApiInterface.getmap_category(mparams);
        call.enqueue(new Callback<MapCategoryResponse>() {
            @Override
            public void onResponse(Call<MapCategoryResponse> call, Response<MapCategoryResponse> response) {
                ProgressDialog.hideprogressbar();
                mMapCategoryResponse = response.body();
                if (isAdded()) {
                    if (mMapCategoryResponse != null && mMapCategoryResponse.getStatus().equalsIgnoreCase("true")) {
                        MapCategory_Datum item = new MapCategory_Datum();
                        item.setIcon("");
                        item.setId(-1);
                        item.setName(mActivity.getString(R.string.hotspot_cat));
                        mMapCategoryResponse.getData().add(0, item);
                    } else if (mMapCategoryResponse != null) {
                        Toast.makeText(mActivity, mMapCategoryResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MapCategoryResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
                DebugLog.log(1, TAG, "onResponse" + t.getMessage());
            }
        });
    }


    //calling get profile api
    private List<HotSpotDatum> mListHotspotData;
    public void callGetHotspots(String category) {
        if(mMap == null)
            return;

        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();

        if(isGlClicked){
            mparams.put(ApiClass.Lat, centerLat);
            mparams.put(ApiClass.LNG, centerLng);
            isGlClicked = false;
        }else{
            mparams.put(ApiClass.Lat, this.latitude);
            mparams.put(ApiClass.LNG, this.longitude);
        }

        mparams.put("userid", SavePref.getInstance(mActivity).getUserdetail().getId().intValue());

        mparams.put(ApiClass.RADIUS, getVisibleRadius());
        mparams.put(ApiClass.TYPES, category);

        ProgressDialog.showProgress(mActivity);
        Call<HotSpotResponse> call = mApiInterface.getHotspotlist(mparams);
        call.enqueue(new Callback<HotSpotResponse>() {
            @Override
            public void onResponse(Call<HotSpotResponse> call, Response<HotSpotResponse> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful() && isAdded()) {
                    mHotspotResponse = response.body();
                    if(mHotspotResponse != null){
                        if (mHotspotResponse.getStatus().equalsIgnoreCase("true")) {

                            if( ! isMarker){
                                mListHotspotData = mHotspotResponse.getData();
                                MessageEvent messageEvent = new MessageEvent();
                                messageEvent.setType(MessageEvent.MessageType.WHERE_ARE_YOU);
                                messageEvent.setListHotspotData(mListHotspotData);
                                EventBus.getDefault().post(messageEvent);
                            }else{
                                createHotspotMarker(true);
                                isMarker = false;
                            }
                        } else {
                            Toast.makeText(mActivity, mHotspotResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<HotSpotResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    private void createHotspotMarker(boolean isbound) {
        if (mMap == null)
            return;
        mMap.clear();
        Marker_hashmap.clear();
        if (mHotspotResponse != null && mHotspotResponse.getData() != null) {
            final LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (int i = 0; i < mHotspotResponse.getData().size(); i++) {
                if (mHotspotResponse.getData().get(i) != null) {
                    addHotspotSlctdMarker(mHotspotResponse.getData().get(i), i);
                    builder.include(new LatLng(Double.parseDouble(mHotspotResponse.getData().get(i).getLat()),
                            Double.parseDouble(mHotspotResponse.getData().get(i).getLng())));
                }
            }

        }
        ProgressDialog.hideprogressbar();
    }

    public void addHotspotSlctdMarker(HotSpotDatum hotspot, int pos) {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.row_hotspotmarker, null);
        ImageView marker_image =  v.findViewById(R.id.ivmarker);
        MarkerOptions marker = null;
        int markerImage = 0;

        if(hotspot.getIsHotspot()  && hotspot.getIs_admin().intValue() == 1 ) {
            markerImage = R.drawable.tea;
        }else{
            switch (hotspot.getCategory_id()){
                case AppConstants.RESTAURANT:
                    markerImage = R.drawable.food;
                    break;
                case AppConstants.CAFE:
                    //cafe
                    markerImage = R.drawable.drink;
                    break;

                case AppConstants.SHOPPING:
                    //shopping
                    markerImage = R.drawable.shop;
                    break;

                case AppConstants.HOTEL:
                    //hotels
                    markerImage = R.drawable.sleep;
                    break;

                case AppConstants.OUTDOOR:
                    //outdoor
                    markerImage = R.drawable.tree;
                    break;
            }
        }

        marker = new MarkerOptions().position(new LatLng(Double.parseDouble(hotspot.getLat()),
                Double.parseDouble(hotspot.getLng())))
                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(marker_image, v, markerImage)))  //catgory_hotspot  //sponser_pin
                .anchor(0.5f, 1);
        FrameLayout.LayoutParams mparam = new FrameLayout.LayoutParams((int) getResources().getDimension(R.dimen.size_30), (int) getResources().getDimension(R.dimen.size_30));
        marker_image.setLayoutParams(mparam);
        mMap.addMarker(marker);
        Marker_hashmap.put(marker.getPosition(), pos);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent messageEvent) {
        if (messageEvent.getType() == MessageEvent.MessageType.HOTSPOTDETAILS) {
            ((MainActivity)mActivity).hideCheckedInMark();
            ((MainActivity)mActivity).resumeMeetupObjects();
            if(Build.VERSION.SDK_INT >= 21)
                view.setClipToOutline(true);
            ((BaseActivity)mActivity).setStatusBarColor(R.color.darkPurple);
        }else if(messageEvent.getType() == MessageEvent.MessageType.NEW_MSG_NOTIFICATION){
            mbinding.imgBadge.setVisibility(View.VISIBLE);
        }
    }

    public ArrayList autocomplete(final String input) {
        final ArrayList<AreaModel> resultList = new ArrayList();
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        StringBuilder sb = null;
        try {
            sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + ApiClass.GooglePlaceKey);
            sb.append("&query=" + input.replace(" ", "%20"));
            Log.d(TAG, "urlL" + sb.toString());
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error processing Places API URL", e);
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Places API", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        try {
            DebugLog.log(1, "result_name ", jsonResults.toString());
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            resultList.clear();
            resultList.addAll(parseLocationResult(jsonObj));
        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mGooglePlacesAutocompleteAdapter.setList(resultList);
            }
        });
        return resultList;
    }

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
                    AreaModel mAreaModel = new AreaModel();
                    id = place.getString(SUPERMARKET_ID);
                    place_id = place.getString(PLACE_ID);
                    latitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION)
                            .getDouble(LATITUDE);
                    longitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION)
                            .getDouble(LONGITUDE);

                    if (!place.isNull(NAME)) {
                        placeName = place.getString(NAME);
                        DebugLog.log(1, "reuslt_name ", placeName);
                        mAreaModel.setName(placeName);
                        mAreaModel.setLat(latitude);
                        mAreaModel.setLng(longitude);
                        resultList.add(mAreaModel);
                    }
                    if (!place.isNull(VICINITY)) {
                        vicinity = place.getString(VICINITY);
                    }
                    reference = place.getString(REFERENCE);
                }


            } else if (result.getString(STATUS).equalsIgnoreCase(ZERO_RESULTS)) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "No Area found in  radius!!!");
                        //Toast.makeText(mActivity, "No Area found in  radius!!!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "parseLocationResult: Error=" + e.getMessage());
        }
        return resultList;
    }


    private Bitmap getMarkerBitmapFromView(ImageView imageview, View view, int drawable_icon) {
        imageview.setImageResource(drawable_icon);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = view.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        view.draw(canvas);
        return returnedBitmap;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //Getting current location
    private void getCurrentLocation() {
        mMap.clear();
        //Creating a location object
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            //Getting longitude and latitude
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            CameraUpdate update = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
            mMap.moveCamera(update);
            mMap.animateCamera(zoom);

//            callUpdateLocation();
        }
    }

    private void callUpdateLocation(){
        if(SavePref.getInstance(mActivity).getUserdetail() == null)
            return;
        if (latitude != 0.0 && longitude != 0.0) {
            if(latitude == SavePref.getInstance(mActivity).getLatitude() &&
                    longitude == SavePref.getInstance(mActivity).getLongitude())
                return;
            ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
            Map mparams = new HashMap();
            mparams.put("user_id", SavePref.getInstance(mActivity).getUserdetail().getId());
            mparams.put("lat",latitude);
            mparams.put("lng", longitude);

            Call<Basic_Response> call = mApiInterface.updateLocation(mparams);
            call.enqueue(new Callback<Basic_Response>() {
                @Override
                public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                    if (response.isSuccessful()) {

                    }else {
                        System.out.println(response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<Basic_Response> call, Throwable t) {
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_gps:
                if (mMap != null) {
                    CameraUpdate update = CameraUpdateFactory.newLatLng(new LatLng(SavePref.getInstance(mActivity).getLatitude(), SavePref.getInstance(mActivity).getLongitude()));
                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
                    mMap.moveCamera(update);
                    mMap.animateCamera(zoom);
                }
                break;

            case R.id.iv_zoomin:
                if (mMap != null)
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                break;

            case R.id.iv_zoomout:
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
                break;

            case R.id.iv_gl_Logo:
                performGlHotspotClicked();
                break;

            case R.id.iv_people:
                if(mMap == null)
                    return;
                ((MainActivity)mActivity).showGroupUserDlg(getVisibleRadius(), centerLat, centerLng);

                break;

            case R.id.imgMenu:
                    try{
                        ((MainActivity)mActivity).openMenu();
                    }catch (Exception ex){
//                        Crashlytics.log(Log.DEBUG, "MeetupHotspotFragment.openMenu", ex.toString());
                    }
                break;
            case R.id.imgMessage:
                ((MainActivity)mActivity).openMessageFragment();
                break;
            case R.id.imgSearch:
                ((MainActivity)mActivity).openSearchTabFragment();
                break;

        }
    }

    private void loadHotspots() {
        category_id = -1;
        category_url = "";
        bitmap = null;
    }

    private boolean isMarker;
    private boolean isGlClicked;
    private void performGlHotspotClicked() {
        if(mMap == null)
            return;
        isGlClicked = true;
        bitmap = null;
        centerLat = mMap.getCameraPosition().target.latitude;
        centerLng = mMap.getCameraPosition().target.longitude;
        isMarker = true;
        callGetHotspots("flare");
    }

    public float getVisibleRadius(){
        VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();

        LatLng farRight = visibleRegion.farRight;
        LatLng farLeft = visibleRegion.farLeft;
        LatLng nearRight = visibleRegion.nearRight;
        LatLng nearLeft = visibleRegion.nearLeft;

        float[] distanceWidth = new float[2];
        Location.distanceBetween(
                (farRight.latitude+nearRight.latitude)/2,
                (farRight.longitude+nearRight.longitude)/2,
                (farLeft.latitude+nearLeft.latitude)/2,
                (farLeft.longitude+nearLeft.longitude)/2,
                distanceWidth
        );


        float[] distanceHeight = new float[2];
        Location.distanceBetween(
                (farRight.latitude+nearRight.latitude)/2,
                (farRight.longitude+nearRight.longitude)/2,
                (farLeft.latitude+nearLeft.latitude)/2,
                (farLeft.longitude+nearLeft.longitude)/2,
                distanceHeight
        );

        float distance;

        if (distanceWidth[0]>distanceHeight[0]){
            distance = distanceWidth[0];
        } else {
            distance = distanceHeight[0];
        }
        return distance/1000;
    }
}
