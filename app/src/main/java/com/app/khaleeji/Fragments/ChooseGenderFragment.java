package com.app.khaleeji.Fragments;

import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.LoginActivity;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.UserDetails;
import com.app.khaleeji.databinding.FragmentChooseGenderBinding;

import Utility.Fragment_Process;


public class ChooseGenderFragment extends BaseFragment{

    static String TAG = ChooseGenderFragment.class.getName();
    private Context mContext;
    private FragmentChooseGenderBinding mbinding;
    private String mSelectedGender;
    private SignUpData mSignUpData;

    public class SignUpData {
        public UserDetails mUserDetails;
        public String mStrCountryId;
        public String mStrFullName;
        public String mStrPassword;
        public String mStrLat;
        public String mStrLng;

        public SignUpData(UserDetails muserdetail, String country_id
                , String fullname, String password, String lat, String lng){
            mUserDetails = muserdetail;
            mStrCountryId = country_id;
            mStrFullName = fullname;
            mStrPassword = password;
            mStrLat = lat;
            mStrLng = lng;
        }
    }

    public ChooseGenderFragment(UserDetails muserdetail, String country_id
            , String fullname, String password, String lat, String lng){
        mSignUpData = new SignUpData(muserdetail, country_id, fullname, password, lat, lng);
    }

    public ChooseGenderFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = mActivity;

        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_choose_gender, container, false);
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
        mSelectedGender = "male";
        mbinding.txtContinue.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(! mSelectedGender.isEmpty())
                    Fragment_Process.addFragment(mActivity.getSupportFragmentManager(),new BirthdayFragment(mSignUpData,mSelectedGender),mActivity, R.id.framelayout_login);
            }
        });

        mbinding.imgBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((LoginActivity)mActivity).back();
            }
        });

        mbinding.llFemale.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mSelectedGender = "female";
                mbinding.llFemale.setBackground(getResources().getDrawable(R.drawable.rounded_box_gender_male));
                mbinding.llMale.setBackground(getResources().getDrawable(R.drawable.rounded_box_gender_female));
                mbinding.imgFemale.setImageDrawable(getResources().getDrawable(R.drawable.gender_male));
                mbinding.imgMale.setImageDrawable(getResources().getDrawable(R.drawable.gender_female));
                mbinding.txtFemale.setTextColor(getResources().getColor(R.color.white));
                mbinding.txtMale.setTextColor(getResources().getColor(R.color.backgroundDefaultColor));
            }
        });

        mbinding.llMale.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mSelectedGender = "male";
                mbinding.llFemale.setBackground(getResources().getDrawable(R.drawable.rounded_box_gender_female));
                mbinding.llMale.setBackground(getResources().getDrawable(R.drawable.rounded_box_gender_male));
                mbinding.imgFemale.setImageDrawable(getResources().getDrawable(R.drawable.gender_female));
                mbinding.imgMale.setImageDrawable(getResources().getDrawable(R.drawable.gender_male));
                mbinding.txtFemale.setTextColor(getResources().getColor(R.color.backgroundDefaultColor));
                mbinding.txtMale.setTextColor(getResources().getColor(R.color.white));
            }
        });
    }
}
