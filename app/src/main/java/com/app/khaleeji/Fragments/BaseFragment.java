package com.app.khaleeji.Fragments;

import android.app.Activity;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.app.khaleeji.R;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import Constants.AppConstants;
import Utility.Fragment_Process;


public class BaseFragment extends Fragment {

    protected FragmentActivity mActivity;

    public BaseFragment(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FragmentActivity) {
            mActivity = (FragmentActivity) context;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventFragment(LatLng currentPos) {

    }

    public SpannableString getTags(String str, String hashType){
        if(str != null &&! str.isEmpty()){
            SpannableString ss = new SpannableString(str);
            int startPos;
            int len = 0;
            while ((startPos = str.indexOf("#")) != -1){
                int endPos;
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View textView) {
                        TextView tv = (TextView) textView;
                        Spanned s = (Spanned) tv.getText();
                        int start = s.getSpanStart(this);
                        int end = s.getSpanEnd(this);
                        Log.d("ProfileFragment", "onClick [" + s.subSequence(start, end) + "]");
                        Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), new SearchTabFragments(s.subSequence(start, end).toString(), hashType), mActivity, R.id.framelayout_main);
                    }
                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                    }
                };
                len = len + startPos;
                str = str.substring(startPos);
                if ((endPos = str.indexOf(" ")) != -1){
                    ss.setSpan(clickableSpan, len, len + endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }else{
                    ss.setSpan(clickableSpan, len, len + str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    return ss;
                }
                len = len + endPos + 1;
                str = str.substring(endPos + 1);
            }

            return ss;
        }
        return null;
    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null && activity.getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public Fragment getCurrentFragment() {
        try {
            int index = this.getFragmentManager().getBackStackEntryCount() - 1;
            FragmentManager.BackStackEntry backEntry = getFragmentManager().getBackStackEntryAt(index);
            String tag = backEntry.getName();
            Fragment fragment = getFragmentManager().findFragmentByTag(tag);
            return fragment;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
