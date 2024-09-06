package com.app.khaleeji.Fragments;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.LoginActivity;
import com.app.khaleeji.Adapter.MindMentionAdapter;
import com.app.khaleeji.Adapter.TextItemAdapter;
import com.app.khaleeji.R;
import com.app.khaleeji.databinding.FragmentProfileQuestionBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Constants.AppConstants;
import Utility.Fragment_Process;


public class ProfileQuestionFragment extends BaseFragment{

    static String TAG = ProfileQuestionFragment.class.getName();
    private Context mContext;
    private FragmentProfileQuestionBinding mbinding;
    private ArrayList<String> mListProfileQuestions;
    private int mSelectedIndex, mPreSelectedIndex;

    private String mStrSelGender;
    private String mStrSelBirthday;
    private String mStrSelHomeTown;
    private String mStrSelQuestions="";
    private boolean mIsAllowedDOB;
    private static final int REQUEST_ID_PROFILE_PERMISSIONS = 1;
    private boolean isSkip;

    private ChooseGenderFragment.SignUpData mSignUpData;

    public ProfileQuestionFragment(ChooseGenderFragment.SignUpData signUpData, String strSelGender, String strSelBirthday, boolean isAllowedDOB, String strHomeTown){
        mStrSelGender = strSelGender;
        mStrSelBirthday = strSelBirthday;
        mStrSelHomeTown = strHomeTown;
        mIsAllowedDOB = isAllowedDOB;
        mSignUpData = signUpData;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = mActivity;
        mListProfileQuestions = new ArrayList();
        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_question, container, false);
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

        TextItemAdapter listAdapter = new TextItemAdapter(mListProfileQuestions  ,getContext(), new TextItemAdapter.OnItemClickListener() {
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

        mbinding.txtContinue.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                isSkip = false;
                if(checkProfilePermission()) {
                    Fragment_Process.addFragment(mActivity.getSupportFragmentManager(),new ProfilePictureFragment(mSignUpData, mStrSelGender, mStrSelBirthday,
                            mIsAllowedDOB, mStrSelHomeTown, getProfileQuestions()), mActivity, R.id.framelayout_login);
                }
            }
        });

        mbinding.txtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSkip = true;
                if(checkProfilePermission()) {
                    Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(),new ProfilePictureFragment(mSignUpData, mStrSelGender, mStrSelBirthday,
                            mIsAllowedDOB,mStrSelHomeTown, ""), mActivity, R.id.framelayout_login);
                }
            }
        });

        mbinding.rvQuestions.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard(mActivity);
                return false;
            }
        });


        mbinding.rlmain.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard(mActivity);
                return false;
            }
        });

        mbinding.imgBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((LoginActivity)mActivity).back();
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
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
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
                        if( ! isSkip ){
                            Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(),new ProfilePictureFragment(mSignUpData, mStrSelGender, mStrSelBirthday,
                                    mIsAllowedDOB, mStrSelHomeTown, getProfileQuestions()), mActivity, R.id.framelayout_login);
                        }else{
                            Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(),new ProfilePictureFragment( mSignUpData, mStrSelGender, mStrSelBirthday,
                                    mIsAllowedDOB, mStrSelHomeTown, ""), mActivity, R.id.framelayout_login);
                        }
                    }
                }
                return;
        }
    }

}
