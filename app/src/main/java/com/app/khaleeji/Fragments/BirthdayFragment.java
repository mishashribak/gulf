package com.app.khaleeji.Fragments;

import androidx.databinding.DataBindingUtil;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Toast;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.LoginActivity;
import com.app.khaleeji.R;
import com.app.khaleeji.databinding.FragmentBirthdayBinding;

import java.util.Calendar;

import Utility.Fragment_Process;


public class BirthdayFragment extends BaseFragment{

    static String TAG = BirthdayFragment.class.getName();
    private Context mContext;
    private FragmentBirthdayBinding mbinding;
    private String[] strDate={"January","February", "March", "April", "May", "June","July","August","September","October","November","December"};

    private String mStrSelGender;
    private String mStrSelBirthday;
    private boolean isAllowedDOB;

    private ChooseGenderFragment.SignUpData mSignUpData;

    public BirthdayFragment(ChooseGenderFragment.SignUpData signUpData, String strGender){
        mStrSelGender = strGender;
        mSignUpData = signUpData;
    }

    public BirthdayFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = mActivity;

        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_birthday, container, false);
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
                //Toast.makeText(mContext,"Selected Date: " + mbinding.datePicker.getDayOfMonth() + "/" + (mbinding.datePicker.getMonth() + 1) + "/" + mbinding.datePicker.getYear(),Toast.LENGTH_SHORT).show();
                mStrSelBirthday =  mbinding.datePicker.getYear() + "-"+ (mbinding.datePicker.getMonth() + 1) +"-"+ mbinding.datePicker.getDayOfMonth();
                Fragment_Process.addFragment(mActivity.getSupportFragmentManager(),new HomeTownFragment(mSignUpData, mStrSelGender, mStrSelBirthday, isAllowedDOB),mActivity, R.id.framelayout_login);
            }
        });

        mbinding.imgBack.setOnClickListener(v -> ((LoginActivity)mActivity).back());

        mbinding.checkb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isAllowedDOB = isChecked;
            }
        });

//        mbinding.rlDatePicker.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final Calendar cldr = Calendar.getInstance();
//                int day = cldr.get(Calendar.DAY_OF_MONTH);
//                int month = cldr.get(Calendar.MONTH);
//                int year = cldr.get(Calendar.YEAR);
//                // date picker dialog
//                DatePickerDialog picker = new DatePickerDialog(mActivity, R.style.DatePickerDialogTheme,
//                        new DatePickerDialog.OnDateSetListener() {
//                            @Override
//                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                                mbinding.txtDate.setText(dayOfMonth + "-" + strDate[(monthOfYear + 1)-1] + "-" + year);
//                            }
//                        }, year, month, day);
//                picker.show();
//            }
//        });
    }
}
