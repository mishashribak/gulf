package com.app.khaleeji.Fragments;

import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.ChatActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Adapter.MyFriendAdapter;
import com.app.khaleeji.Adapter.SeeAnswerAdapter;
import com.app.khaleeji.Adapter.SendAnswerAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.Basic_Response;
import com.app.khaleeji.Response.FriendData;
import com.app.khaleeji.Response.FriendlistResponse;
import com.app.khaleeji.databinding.FragmentMyfriendBinding;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.AppConstants;
import CustomView.CustomSeeAnswerDlg;
import CustomView.CustomYourAnswerDlg;
import Model.QAData;
import Model.StatusViewersModel;
import Utility.ApiClass;
import Utility.CheckConnection;
import Utility.DebugLog;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyFriendFragment extends BaseFragment {
    private static final String TAG = MyFriendFragment.class.getSimpleName();
    private MyFriendAdapter myFriendAdapter;
    private FriendlistResponse mFriendlistResponse;
    private FragmentMyfriendBinding mbinding;
    private int type = 0;
    private List<FriendData> mFriendList;
    private FriendData mSelFriendData;
    private ArrayList<QAData> dataQAList;
    private String answer="";
    private boolean isFromReceived;


    public MyFriendFragment(){

    }

    public MyFriendFragment(boolean isFromReceived){
        this.isFromReceived = isFromReceived;
    }

    private boolean isNotification = false;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent messageEvent) {
        if (messageEvent.getType() == MessageEvent.MessageType.NOTIFICATION) {
            isNotification = true;
            if(type ==0 ){
                setMyFriendsLayout();
            }else if(type == 1){
                setSendRequestLayout();
            }else if(type == 2){
                setReceivedRequestLayout();
            }
            isNotification = false;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_myfriend, container, false);
        View rootView = mbinding.getRoot();

        if(Build.VERSION.SDK_INT >= 21)
            rootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        initView();
        return rootView;
    }

    private void initView() {

        ((MainActivity)mActivity).hide();
        mbinding.imgMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).openMenu();
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

        myFriendAdapter = new MyFriendAdapter(mActivity, 0,null,new MyFriendAdapter.OnEventClickListener() {
            @Override
            public void onClick(List<FriendData> list, int type, FriendData pos) {
                mSelFriendData = pos;
                select(type);

            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        mbinding.rvMyFriends.setLayoutManager(mLayoutManager);
        mbinding.rvMyFriends.setAdapter(myFriendAdapter);

        mbinding.btMyFriends.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               if(type == 0) return;
                    setMyFriendsLayout();
            }
        });

        mbinding.btReceivedReq.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               if( type == 2 ) return;
               setReceivedRequestLayout();
            }
        });

        mbinding.btSentReq.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(type == 1) return;
                setSendRequestLayout();
            }
        });

        mbinding.searchEdit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (myFriendAdapter != null) {
                    myFriendAdapter.getFilter().filter(s);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        mbinding.rvMyFriends.setLayoutManager(linearLayoutManager);

        mbinding.rLMain.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((MainActivity)mActivity).hideSoftKeyboard();
                return false;
            }
        });

        mbinding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(! mbinding.searchEdit.getText().toString().isEmpty()){
//                    if (myFriendAdapter != null) {
//                        myFriendAdapter.getFilter().filter(mbinding.searchEdit.getText().toString());
//                    }
//                }
            }
        });

        if (CheckConnection.isNetworkAvailable(mActivity)) {
            if(isFromReceived){
                setReceivedRequestLayout();
            }else{
                setMyFriendsLayout();
            }
        } else {
            Toast.makeText(mActivity,getString(R.string.network_down), Toast.LENGTH_SHORT).show();
        }
    }

    private void setMyFriendsLayout(){
        type = 0;
        mbinding.btMyFriends.setBackground(getResources().getDrawable(R.drawable.rounded_box_darkpurple_20));
        mbinding.btSentReq.setBackground(getResources().getDrawable(R.drawable.rounded_corner_purple_border));
        mbinding.btReceivedReq.setBackground(getResources().getDrawable(R.drawable.rounded_corner_purple_border));
        mbinding.btMyFriends.setTextColor(getResources().getColor(R.color.white));
        mbinding.btSentReq.setTextColor(getResources().getColor(R.color.backgroundDefaultColor));
        mbinding.btReceivedReq.setTextColor(getResources().getColor(R.color.backgroundDefaultColor));
        myFriendAdapter = new MyFriendAdapter(mActivity, 0, null,new MyFriendAdapter.OnEventClickListener() {
            @Override
            public void onClick(List<FriendData> list, int type, FriendData pos) {
                mSelFriendData = pos;
                select(type);

            }
        });
        mbinding.rvMyFriends.setAdapter(myFriendAdapter);
        getMyfriendList();
    }

    private void select(int type){
        switch (type){
            case 0:
                callUnfriend();
                break;
            case 1:
                openYourAnswerDlg();
                break;
            case 2:
                openSeeAnswerDlg();
                break;
            case 3:
            case 4:
            case 5:
                Fragment_Process.addFragment(mActivity.getSupportFragmentManager(),new FriendProfileFragment(mSelFriendData.getId().intValue()),mActivity, R.id.framelayout_main);
//                ((MainActivity)mActivity).openFriendProfileFragment(mSelFriendData.getId().intValue());
                break;
            case 6:
                //confirm
                callAcceptFriendRequest();
                break;
            case 7:
                callRejectFriendRequest(7);
            case 9:
                //delete
                callRejectFriendRequest(9);
                break;
            case 8:
                //Nudge
                callSendFriendRequest();
                break;
        }
    }

    private void setSendRequestLayout(){
        type = 1;
        mbinding.btMyFriends.setBackground(getResources().getDrawable(R.drawable.rounded_corner_purple_border));
        mbinding.btSentReq.setBackground(getResources().getDrawable(R.drawable.rounded_box_darkpurple_20));
        mbinding.btReceivedReq.setBackground(getResources().getDrawable(R.drawable.rounded_corner_purple_border));
        mbinding.btMyFriends.setTextColor(getResources().getColor(R.color.backgroundDefaultColor));
        mbinding.btSentReq.setTextColor(getResources().getColor(R.color.white));
        mbinding.btReceivedReq.setTextColor(getResources().getColor(R.color.backgroundDefaultColor));
        myFriendAdapter = new MyFriendAdapter(mActivity, 2, null,new MyFriendAdapter.OnEventClickListener() {
            @Override
            public void onClick(List<FriendData> list, int type, FriendData pos) {
                mSelFriendData = pos;
                select(type);

            }
        });
        mbinding.rvMyFriends.setAdapter(myFriendAdapter);
        getSentRequest();
    }

    private void setReceivedRequestLayout(){
        type = 2;
        mbinding.btMyFriends.setBackground(getResources().getDrawable(R.drawable.rounded_corner_purple_border));
        mbinding.btSentReq.setBackground(getResources().getDrawable(R.drawable.rounded_corner_purple_border));
        mbinding.btReceivedReq.setBackground(getResources().getDrawable(R.drawable.rounded_box_darkpurple_20));
        mbinding.btMyFriends.setTextColor(getResources().getColor(R.color.backgroundDefaultColor));
        mbinding.btSentReq.setTextColor(getResources().getColor(R.color.backgroundDefaultColor));
        mbinding.btReceivedReq.setTextColor(getResources().getColor(R.color.white));
        myFriendAdapter = new MyFriendAdapter(mActivity, 1, null,new MyFriendAdapter.OnEventClickListener() {
            @Override
            public void onClick(List<FriendData> list, int type, FriendData pos) {
                mSelFriendData = pos;
                select(type);

            }
        });
        mbinding.rvMyFriends.setAdapter(myFriendAdapter);
        getReceivedRequest();
    }

    public String createAnswerString(ArrayList<QAData> list){
        int size = list.size();
        String strQuestions="";
        for(int i =0 ; i < size; i++){
            if( i != size - 1){
                strQuestions +=  list.get(i).getAnswer() + AppConstants.SEPERATOR;
            }else{
                strQuestions +=  list.get(i).getAnswer();
            }
        }
        return strQuestions;
    }

    public void openYourAnswerDlg(){
        dataQAList = new ArrayList<>();
        String str = mSelFriendData.getQuestion();
        //my answer
        if(str != null && ! str.isEmpty()){
            int n;
            while ((n =str.indexOf(AppConstants.SEPERATOR)) != -1){
                String cutStr = str.substring(0, n);
                dataQAList.add( new QAData(cutStr, ""));
                str = str.substring(n+AppConstants.SEPERATOR.length());
            }
            dataQAList.add(new QAData(str, ""));
        }

        str = mSelFriendData.getAnswer();
        if( str != null && ! str.isEmpty()){
            int n;
            int index = 0;
            while ((n =str.indexOf(AppConstants.SEPERATOR)) != -1){
                String cutStr = str.substring(0, n);
                if(index < dataQAList.size())
                    dataQAList.get(index).setAnswer(cutStr);
                str = str.substring(n+AppConstants.SEPERATOR.length());
                index++;
            }
            if(index < dataQAList.size())
                dataQAList.get(index).setAnswer(str);
        }

        if(dataQAList.size() > 0) {
            SendAnswerAdapter adapter = new SendAnswerAdapter(mActivity, dataQAList, new SendAnswerAdapter.OnKeypressListener() {
                @Override
                public void onKeyPress(int index, String str) {
                    dataQAList.get(index).setAnswer(str);
                }
            });

            CustomYourAnswerDlg dlg = new CustomYourAnswerDlg(mActivity,mSelFriendData.getFull_name(), adapter);
            dlg.setOnItemClickListener(new CustomYourAnswerDlg.OnDlgItemClickListener() {
                public void onClick() {
                    answer = createAnswerString(dataQAList);
                    updateAnswer();
                }
            });
            dlg.setCanceledOnTouchOutside(false);
            dlg.showDialog();
        }else{
            Toast.makeText(mActivity,"No Question", Toast.LENGTH_SHORT).show();
//            AlertDialog.showAlert(mActivity, getString(R.string.app_name), "No Questions", getString(R.string.txt_Done), "", false, null, null);
        }
    }

    public void openSeeAnswerDlg(){
        dataQAList = new ArrayList<>();
        String str = mSelFriendData.getQuestion();
        if(str != null &&! str.isEmpty()){
            int n;
            while ((n =str.indexOf(AppConstants.SEPERATOR)) != -1){
                String cutStr = str.substring(0, n);
                dataQAList.add( new QAData(cutStr, ""));
                str = str.substring(n+AppConstants.SEPERATOR.length());
            }
            dataQAList.add(new QAData(str, ""));
        }

        str = mSelFriendData.getAnswer();
        if(str != null &&! str.isEmpty()){
            int n;
            int index = 0;
            while ((n =str.indexOf(AppConstants.SEPERATOR)) != -1){
                String cutStr = str.substring(0, n);
                if(index < dataQAList.size())
                    dataQAList.get(index).setAnswer(cutStr);
                str = str.substring(n+AppConstants.SEPERATOR.length());
                index++;
            }
            if(index < dataQAList.size())
                dataQAList.get(index).setAnswer(str);
        }

        if(dataQAList.size() > 0) {
            SeeAnswerAdapter adapter = new SeeAnswerAdapter(mActivity, dataQAList, new SeeAnswerAdapter.OnCheckListener() {
                @Override
                public void onCheck(int index, boolean isChecked) {

                }
            });
            CustomSeeAnswerDlg dlg = new CustomSeeAnswerDlg(mActivity, adapter);
            dlg.setOnItemClickListener(new CustomSeeAnswerDlg.OnDlgItemClickListener(){
                public void onClick(int type){
                   switch (type){
                       case 1:
                           //reject
                           callRejectFriendRequest(7);
                           break;
                       case 2:
                           //dislike
                           callDislikeFriendRequest();
                           break;
                       case 3:
                           //accept
                           callAcceptFriendRequest();
                           break;
                   }
                }
            });
            dlg.setCanceledOnTouchOutside(false);
            dlg.showDialog();
        }else{
            Toast.makeText(mActivity,getString(R.string.no_question), Toast.LENGTH_SHORT).show();
        }

    }

    public void updateAnswer(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int fromUserId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();

        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().FROM_USER, fromUserId);
        mparams.put(ApiClass.getmApiClass().TO_USER, mSelFriendData.getId());
        mparams.put(ApiClass.getmApiClass().TO_ANS, answer);

        Call<Basic_Response> call;
        call = mApiInterface.updateAnswer(mparams);
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Basic_Response basic_response = response.body();
                        Toast.makeText(mActivity,basic_response.getMessage(), Toast.LENGTH_SHORT).show();
                        isNotification = true;
                        if(type ==0 ){
                            setMyFriendsLayout();
                        }else if(type == 1){
                            setSendRequestLayout();
                        }else if(type == 2){
                            setReceivedRequestLayout();
                        }
                        isNotification = false;
//                        AlertDialog.showAlert(mActivity, getString(R.string.app_name),basic_response.getMessage(),
//                                getString(R.string.txt_Done), "", false, null, null);

                    } else {
                        System.out.println(response.errorBody());
                    }
                }
            }

            @Override
            public void onFailure(Call<Basic_Response> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    private void getMyfriendList() {
        if(!isNotification)
            ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userid = SavePref.getInstance(mActivity).getUserdetail().getId();
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().REQUEST_TYPE, "myfriends");
        mparams.put(ApiClass.getmApiClass().USER_ID, String.valueOf(userid));
        String token = SavePref.getInstance(mActivity).getUserdetail().getToken();
        Call<FriendlistResponse> call = mApiInterface.friendRequestList("Bearer "+token,mparams);
        call.enqueue(new Callback<FriendlistResponse>() {
            @Override
            public void onResponse(Call<FriendlistResponse> call, Response<FriendlistResponse> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        mFriendlistResponse = response.body();
                        if(mFriendlistResponse != null) {
                            if (mFriendlistResponse.getStatus().equalsIgnoreCase("true")) {
                                if (mFriendlistResponse.getData() != null && mFriendlistResponse.getData().getData().size() > 0) {
                                    mFriendList = mFriendlistResponse.getData().getData();
                                    myFriendAdapter.setData(mFriendList);
                                    mbinding.txtNoData.setVisibility(View.GONE);
                                } else {
                                    mbinding.txtNoData.setVisibility(View.VISIBLE);
                                    myFriendAdapter.setData(null);
                                }
                            } else {
                                Toast.makeText(mActivity,mFriendlistResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                                AlertDialog.showAlert(mActivity, getString(R.string.app_name), mFriendlistResponse.getMessage(), getString(R.string.txt_Done), "", false, null, null);
                            }
                        }

                    } else {
                        System.out.println(response.errorBody());
                    }
                }
            }

            @Override
            public void onFailure(Call<FriendlistResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
                DebugLog.log(1, TAG, "onResponse" + t.getMessage());
            }
        });
    }

    private void  getReceivedRequest() {
        if(!isNotification)
            ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userid = SavePref.getInstance(mActivity).getUserdetail().getId();
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().REQUEST_TYPE, "received");
        mparams.put(ApiClass.getmApiClass().USER_ID, String.valueOf(userid));
        String token = SavePref.getInstance(mActivity).getUserdetail().getToken();
        Call<FriendlistResponse> call = mApiInterface.friendRequestList("Bearer "+token,mparams);
        call.enqueue(new Callback<FriendlistResponse>() {
            @Override
            public void onResponse(Call<FriendlistResponse> call, Response<FriendlistResponse> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        mFriendlistResponse = response.body();
                        if(mFriendlistResponse != null) {
                            if (mFriendlistResponse.getStatus().equalsIgnoreCase("true")) {
                                if (mFriendlistResponse.getData() != null && mFriendlistResponse.getData().getData().size() > 0) {
                                    mFriendList = mFriendlistResponse.getData().getData();
                                    myFriendAdapter.setData(mFriendList);
                                    mbinding.txtNoData.setVisibility(View.GONE);
                                } else {
                                    mbinding.txtNoData.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(mActivity,mFriendlistResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                                AlertDialog.showAlert(mActivity, getString(R.string.app_name), mFriendlistResponse.getMessage(), getString(R.string.txt_Done), "", false, null, null);
                            }
                        }
                    } else {
                        System.out.println(response.errorBody());
                    }
                }
            }

            @Override
            public void onFailure(Call<FriendlistResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
                DebugLog.log(1, TAG, "onResponse" + t.getMessage());
            }
        });
    }

    private void getSentRequest() {
        if(!isNotification)
            ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userid = SavePref.getInstance(mActivity).getUserdetail().getId();
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().REQUEST_TYPE, "sent");
        mparams.put(ApiClass.getmApiClass().USER_ID, String.valueOf(userid));
        String token = SavePref.getInstance(mActivity).getUserdetail().getToken();
        Call<FriendlistResponse> call = mApiInterface.friendRequestList("Bearer "+token,mparams);
        call.enqueue(new Callback<FriendlistResponse>() {
            @Override
            public void onResponse(Call<FriendlistResponse> call, Response<FriendlistResponse> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        mFriendlistResponse = response.body();
                        if(mFriendlistResponse != null) {
                            if (mFriendlistResponse.getStatus().equalsIgnoreCase("true")) {
                                if (mFriendlistResponse.getData() != null && mFriendlistResponse.getData().getData().size() > 0) {
                                    mFriendList = mFriendlistResponse.getData().getData();
                                    myFriendAdapter.setData(mFriendList);
                                    mbinding.txtNoData.setVisibility(View.GONE);
                                } else {
                                    mbinding.txtNoData.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(mActivity,mFriendlistResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                                AlertDialog.showAlert(mActivity, getString(R.string.app_name), mFriendlistResponse.getMessage(), getString(R.string.txt_Done), "", false, null, null);
                            }
                        }
                    } else {
                        System.out.println(response.errorBody());
                    }
                }
            }

            @Override
            public void onFailure(Call<FriendlistResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
                DebugLog.log(1, TAG, "onResponse" + t.getMessage());
            }
        });
    }

    private void callUnfriend(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int fromUserId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().FROM_USER, fromUserId);
        mparams.put(ApiClass.getmApiClass().TO_USER, mSelFriendData.getId());

        Call<Basic_Response> call;
        call = mApiInterface.unFriend(mparams);
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Basic_Response basic_response = response.body();
                        if(basic_response.getStatus().equalsIgnoreCase("true")){
                            myFriendAdapter.setData(null);
                            getMyfriendList();
                        }
                        else
                            Toast.makeText(mActivity,basic_response.getMessage(), Toast.LENGTH_SHORT).show();
//                        AlertDialog.showAlert(mActivity, getString(R.string.app_name),basic_response.getMessage(),
//                                getString(R.string.txt_Done), "", false, null, null);

                    } else {
                        System.out.println(response.errorBody());
                    }
                }
            }

            @Override
            public void onFailure(Call<Basic_Response> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    private void callSendFriendRequest(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int fromUserId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().FROM_USER, fromUserId);
        mparams.put(ApiClass.getmApiClass().TO_USER, mSelFriendData.getId());
        mparams.put(ApiClass.getmApiClass().TO_ANS, mSelFriendData.getAnswer() == null ? "" :  mSelFriendData.getAnswer());
        mparams.put(ApiClass.getmApiClass().QUESTION, mSelFriendData.getQuestion());

        Call<Basic_Response> call;
        call = mApiInterface.sendFriendRequest(mparams);
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Basic_Response basic_response = response.body();
                        if(basic_response.getStatus().equalsIgnoreCase("true")){
                            if(! answer.isEmpty())
                                Toast.makeText(mActivity, getString(R.string.answer_success) , Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(mActivity, getString(R.string.request_success) , Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        System.out.println(response.errorBody());
                    }
                }
            }

            @Override
            public void onFailure(Call<Basic_Response> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    private void callAcceptFriendRequest(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().FROM_USER, mSelFriendData.getId());
        mparams.put(ApiClass.getmApiClass().TO_USER, userId);

        Call<Basic_Response> call;
        call = mApiInterface.acceptFriendRequest(mparams);
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Basic_Response basic_response = response.body();
                        if(basic_response.getStatus().equalsIgnoreCase("true")){
                            myFriendAdapter.setData(null);
                            isNotification = true;
                            getReceivedRequest();
                            isNotification = false;
                        }
                    } else {
                        System.out.println(response.errorBody());
                    }
                }
            }

            @Override
            public void onFailure(Call<Basic_Response> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    private void callRejectFriendRequest(int type){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int fromUserId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();
        Map mparams = new HashMap();
        if(type == 7) {
            mparams.put(ApiClass.getmApiClass().FROM_USER, mSelFriendData.getId() );
            mparams.put(ApiClass.getmApiClass().TO_USER,fromUserId);
        }else{
            mparams.put(ApiClass.getmApiClass().FROM_USER, fromUserId );
            mparams.put(ApiClass.getmApiClass().TO_USER, mSelFriendData.getId());
        }


        Call<Basic_Response> call;
        call = mApiInterface.rejectFriendRequest(mparams);
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Basic_Response basic_response = response.body();
                        if(basic_response.getStatus().equalsIgnoreCase("true")){
                            myFriendAdapter.setData(null);
                            isNotification = true;
                            if(type == 7){
                                getReceivedRequest();
                            }else{
                                getSentRequest();
                            }
                            isNotification = false;
                        }

                    } else {
                        System.out.println(response.errorBody());
                    }
                }
            }

            @Override
            public void onFailure(Call<Basic_Response> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    private void callDislikeFriendRequest(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Call<JsonObject> call = mApiInterface.dislikeFriendRequest(SavePref.getInstance(mActivity).getUserdetail().getId(),  mSelFriendData.getId());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject != null && isAdded()) {
                        if (jsonObject.get("status").getAsBoolean()) {

                        }
                    }
                } else {
                    System.out.println(response.errorBody());
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }
}
