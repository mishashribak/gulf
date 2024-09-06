package com.app.khaleeji.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Adapter.NearByUserOnMapAdapter;
import com.app.khaleeji.R;
import com.app.khaleeji.databinding.FragmentNearbyUserBinding;


public class NearByUserFragment extends BaseFragment {
    private static final String TAG = NearByUserFragment.class.getSimpleName();
    private FragmentNearbyUserBinding mbinding;
    private NearByUserOnMapAdapter dlgNearByUserOnMapAdapter;

    public NearByUserFragment(){

    }

    public NearByUserFragment(NearByUserOnMapAdapter dlgNearByUserOnMapAdapter){
        this.dlgNearByUserOnMapAdapter = dlgNearByUserOnMapAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_nearby_user, container, false);
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
                if (dlgNearByUserOnMapAdapter != null) {
                    dlgNearByUserOnMapAdapter.getFilter().filter(s);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });


        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        mbinding.rvUsersOnMap.setLayoutManager(mLayoutManager);
        mbinding.rvUsersOnMap.setAdapter(dlgNearByUserOnMapAdapter);

    }

}
