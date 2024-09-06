package com.app.khaleeji.Fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.crashlytics.android.Crashlytics;
import com.app.khaleeji.Adapter.HotspotListAdapter;
import com.app.khaleeji.Response.Basic_Response;
import com.app.khaleeji.Response.HotspotUpdateResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Adapter.GooglePlacesAutocompleteAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.HotSpotDatum;
import com.app.khaleeji.Response.HotSpotResponse;
import com.app.khaleeji.Response.MapCategoryResponse;
import com.app.khaleeji.Response.MapCategory_Datum;
import com.app.khaleeji.Response.NearByResponse;
import com.app.khaleeji.Response.NearUserDetail;
import com.app.khaleeji.databinding.HotspotmapBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.AppConstants;
import Constants.Bundle_Identifier;
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

public class MeetUpHotspotFragments extends BaseFragment implements GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = MeetUpHotspotFragments.class.getSimpleName();
    double lat, lng;
    HotSpotResponse mHotspotResponse;
    HashMap<LatLng, Integer> Marker_hashmap = new HashMap<>();
    Bitmap bitmap = null;
    CameraPosition cameraPosition_old = null;
    private GoogleApiClient googleApiClient;
    private double longitude;
    private double latitude;
    private double centerLng;
    private double centerLat;
    private HotspotmapBinding mbinding;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private HotspotListAdapter hotspotListAdapter;
    private HotspotUpdateResponse hotspot;

    public MeetUpHotspotFragments(){

    }

    public static MeetUpHotspotFragments newInstance() {
        MeetUpHotspotFragments fragment = new MeetUpHotspotFragments();

        return fragment;
    }

    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        try {
            mbinding = DataBindingUtil.inflate(inflater, R.layout.hotspotmap, container, false);
            view = mbinding.getRoot();
            Marker_hashmap.clear();
            mbinding.imgMenu.setOnClickListener(this);
            mbinding.imgSearch.setOnClickListener(this);
            mbinding.imgMessage.setOnClickListener(this);
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
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(false);
        googleApiClient = new GoogleApiClient.Builder(mActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
        mMap.clear();
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

        CameraUpdate update = CameraUpdateFactory.newLatLng(new LatLng(SavePref.getInstance(mActivity).getLatitude(), SavePref.getInstance(mActivity).getLongitude()));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
        mMap.moveCamera(update);
        mMap.animateCamera(zoom);

//        performGlHotspotClicked();
    }
    
    private void initView() {
        ((MainActivity)mActivity).hideCheckedInMark();
        ((MainActivity)mActivity).showMeetupObjects();

        if(SavePref.getInstance(mActivity).getNewMsgNotification()){
            mbinding.imgBadge.setVisibility(View.VISIBLE);
        }else{
            mbinding.imgBadge.setVisibility(View.INVISIBLE);
        }

        lat = SavePref.getInstance(mActivity).getLatitude();
        lng = SavePref.getInstance(mActivity).getLongitude();

        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.flmapcontainers, mapFragment).commit();
        }
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        hotspotListAdapter = new HotspotListAdapter(mActivity, new HotspotListAdapter.OnHotSpotClickListener() {
            @Override
            public void onHotSpotClick(int index) {
                if(hotspot != null && hotspot.getData() != null)
                    Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(),
                            new HotSpotDetailsFragment(hotspot.getData().get(index).gethotspotId(), SavePref.getInstance(mActivity).getUserdetail().getId()), mActivity, R.id.framelayout_main);
            }

            @Override
            public void onHotSpotTitleClick(int index){
                if(hotspot != null){
                    if(hotspot.getData().get(index).getuser_id().intValue() == SavePref.getInstance(mActivity).getUserdetail().getId().intValue()){
                        Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), ProfileFragment.newInstance(), mActivity, R.id.framelayout_main);
                    }else{
                        Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(),new FriendProfileFragment(hotspot.getData().get(index).getuser_id()), mActivity, R.id.framelayout_main);
                    }
                }
            }
        });

        mbinding.rvHotspotsUpdate.setAdapter(hotspotListAdapter);

        mbinding.llVisited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), MeetUpMapFragments.newInstance(), mActivity, R.id.framelayout_main);
            }
        });

        if (CheckConnection.isNetworkAvailable(mActivity)) {
            showHotspotWindow();
        } else {
            Toast.makeText(mActivity, getString(R.string.network_down), Toast.LENGTH_SHORT).show();
        }
    }

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

    public void showHotspotWindow(){
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userid = SavePref.getInstance(mActivity).getUserdetail().getId();
        Call<HotspotUpdateResponse> call = mApiInterface.hotspotsUpdates(userid);
        call.enqueue(new Callback<HotspotUpdateResponse>() {
            @Override
            public void onResponse(Call<HotspotUpdateResponse> call, Response<HotspotUpdateResponse> response) {
                if (response.isSuccessful()) {
                    hotspot = response.body();
                    if(hotspot!=null) {
                        if (hotspot.getStatus().equalsIgnoreCase("true")) {
                            if ( hotspot.getData() != null && hotspot.getData().size() > 0) {
                                hotspotListAdapter.setData(hotspot.getData());
                                mbinding.txtNoData.setVisibility(View.GONE);
                            } else {
                                mbinding.txtNoData.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } else {
                    System.out.println(response.errorBody());
                }
            }
            @Override
            public void onFailure(Call<HotspotUpdateResponse> call, Throwable t) {
            }
        });
    }

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

    private void getCurrentLocation() {
        mMap.clear();
        //Creating a location object
        if (ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

            callUpdateLocation();
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

    private boolean isMarker;
    private boolean isGlClicked;

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
