package com.app.khaleeji.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Adapter.LocationListAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.HotSpotDatum;
import com.app.khaleeji.Response.HotSpotResponse;
import com.app.khaleeji.databinding.FragmentWhereBinding;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Utility.ApiClass;
import Utility.CheckConnection;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WhereFragment extends BaseFragment {
    private static final String TAG = WhereFragment.class.getSimpleName();
    private FragmentWhereBinding mbinding;
    private LocationListAdapter locationListAdpater;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_where, container, false);
        View rootView = mbinding.getRoot();

        if(Build.VERSION.SDK_INT >= 21)
            rootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        initView();
        return rootView;
    }

    private void initView() {

        ((MainActivity)mActivity).hide();
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

        mbinding.searchEdit.addTextChangedListener(new TextWatcher() {


            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(locationListAdpater!=null) {
                    locationListAdpater.getFilter().filter(s);
                }
            }
        });

        locationListAdpater = new LocationListAdapter(mActivity, new LocationListAdapter.OnLocationClickListener() {
            @Override
            public void onLocationClick(List<HotSpotDatum> dataList, int index) {
                Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), HotSpotDetailsFragment.newInstance(dataList.get(index)), mActivity, R.id.framelayout_main);
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mbinding.rvHotspot.setLayoutManager(mLayoutManager);
        mbinding.rvHotspot.setAdapter(locationListAdpater);

        if (CheckConnection.isNetworkAvailable(mActivity)) {
//            callGetHotspots(mStrCategory);
        } else {
            Toast.makeText(mActivity, getString(R.string.network_down), Toast.LENGTH_SHORT).show();
        }
    }

    private String mStrCategory = "flare";
//    public void callGetHotspotApi(){
//        Fragment currentFragment = getCurrentFragment();
//        if( currentFragment instanceof MeetUpHotspotFragments){
//            if (CheckConnection.isNetworkAvailable(MainActivity.this)) {
//                ((MeetUpHotspotFragments)currentFragment).callGetHotspots(mStrCategory);
//            } else {
//                Toast.makeText(MainActivity.this,getString(R.string.network_down), Toast.LENGTH_SHORT).show();
//            }
//        }
//
//    }
//
//    public void callGetHotspots(String category) {
//
//        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
//        Map mparams = new HashMap();
//
//        mparams.put(ApiClass.Lat, this.latitude);
//        mparams.put(ApiClass.LNG, this.longitude);
//
//
//        mparams.put("userid", SavePref.getInstance(mActivity).getUserdetail().getId().intValue());
//
//        mparams.put(ApiClass.RADIUS, getVisibleRadius());
//        mparams.put(ApiClass.TYPES, category);
//
//        ProgressDialog.showProgress(mActivity);
//        Call<HotSpotResponse> call = mApiInterface.getHotspottlist(mparams);
//        call.enqueue(new Callback<HotSpotResponse>() {
//            @Override
//            public void onResponse(Call<HotSpotResponse> call, Response<HotSpotResponse> response) {
//                ProgressDialog.hideprogressbar();
//                if (response.isSuccessful() && isAdded()) {
//                    HotSpotResponse mHotspotResponse = response.body();
//                    if(mHotspotResponse != null){
//                        if (mHotspotResponse.getStatus().equalsIgnoreCase("true")) {
//                                mListHotspotData = mHotspotResponse.getData();
//                        } else {
//                            Toast.makeText(mActivity, mHotspotResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<HotSpotResponse> call, Throwable t) {
//                ProgressDialog.hideprogressbar();
//            }
//        });
//    }

}
