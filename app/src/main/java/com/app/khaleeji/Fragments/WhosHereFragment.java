package com.app.khaleeji.Fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Adapter.SendAnswerAdapter;
import com.app.khaleeji.Adapter.WhosHereListAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.Basic_Response;
import com.app.khaleeji.Response.HotSpotDatum;
import com.app.khaleeji.Response.HotSpotDetails;
import com.app.khaleeji.Response.HotSpotUser;
import com.app.khaleeji.Response.HotSpotUserResponse;
import com.app.khaleeji.Response.UserDetails;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.AppConstants;
import CustomView.CustomEditText;
import CustomView.CustomSenderAnswerDlg;
import CustomView.CustomTextView;
import Model.QAData;
import Utility.AlertDialog;
import Utility.ApiClass;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WhosHereFragment  extends BaseFragment {

    private Context mContext;
    private View mRootView;
    private HotSpotDatum mHotspot;
    private CustomTextView txtNoData;
    private WhosHereListAdapter whoshereAdapter;
    private HotSpotUserResponse mHotSpotUserResponse;
    private HotSpotUser mHotSpotUser;
    private ArrayList<QAData> dataQAList;
    private String answer="", question;
    private HotSpotDetails mHotSpotDetails;
    private ImageView imgBadge;

    public WhosHereFragment(){

    }
    public WhosHereFragment(HotSpotDatum hotspot) {
        // Required empty public constructor
        mHotspot = hotspot;
    }

    public WhosHereFragment(HotSpotDetails hotSpotDetails) {
        // Required empty public constructor
        mHotSpotDetails = hotSpotDetails;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent (MessageEvent messageEvent) {
        if(messageEvent.getType() == MessageEvent.MessageType.NEW_MSG_NOTIFICATION){
            imgBadge.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = mActivity;
        if (mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_whoshere, null);
        if(Build.VERSION.SDK_INT >= 21)
            mRootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        initView();
        return mRootView;
    }

    private void initView(){
        imgBadge = mRootView.findViewById(R.id.imgBadge);
        if(SavePref.getInstance(mActivity).getNewMsgNotification()){
            imgBadge.setVisibility(View.VISIBLE);
        }else{
            imgBadge.setVisibility(View.INVISIBLE);
        }

        ((MainActivity)mActivity).hide();
        txtNoData = mRootView.findViewById(R.id.txtNoData);
        CustomEditText searchEdit = mRootView.findViewById(R.id.search_edit);

        searchEdit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (whoshereAdapter != null) {
                    whoshereAdapter.getFilter().filter(s);
                }
            }
        });

        RecyclerView mRvFriendChat = mRootView.findViewById(R.id.rv_friend_chat);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        whoshereAdapter = new WhosHereListAdapter(getContext(), new WhosHereListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int index, int type) {
                if(type == AppConstants.TYPE_CIRCLE_IMG){
                    int id = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();
                    if(id == mHotSpotUserResponse.getData().get(index).getId()){
                        Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), ProfileFragment.newInstance(), mActivity, R.id.framelayout_main);
                    }else{
                        Fragment_Process.addFragment(mActivity.getSupportFragmentManager(),new FriendProfileFragment(mHotSpotUserResponse.getData().get(index).getId()),mActivity, R.id.framelayout_main);
                    }
                    return;
                }
                if(type == AppConstants.TYPE_UNFRIEND){
                    mHotSpotUser = mHotSpotUserResponse.getData().get(index);
                    callUnfriend();
                    return;
                }

                if(type == AppConstants.TYPE_ADD_FRIEND){
                    mHotSpotUser = mHotSpotUserResponse.getData().get(index);
                    openSendAnswerDlg(mHotSpotUser);
                    return;
                }
            }
        });
        mRvFriendChat.setLayoutManager(mLayoutManager);
        mRvFriendChat.setAdapter(whoshereAdapter);

        ImageView imgBack = mRootView.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).back();
            }
        });
        ImageView imgSearch = mRootView.findViewById(R.id.imgSearch);
        imgSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).openSearchTabFragment();
            }
        });
        ImageView imgMessage = mRootView.findViewById(R.id.imgMessage);
        imgMessage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).openMessageFragment();
            }
        });

        callGetHotSpotUsers();
    }

    public void openSendAnswerDlg(HotSpotUser selectedUserInfo){
        dataQAList = new ArrayList<>();
        String str = selectedUserInfo.getQuestion();
        if(str != null &&! str.isEmpty()){
            int n;
            while ((n =str.indexOf(AppConstants.SEPERATOR)) != -1){
                String cutStr = str.substring(0, n);
                dataQAList.add( new QAData(cutStr, ""));
                str = str.substring(n+AppConstants.SEPERATOR.length());
            }
            dataQAList.add(new QAData(str, ""));
        }

        if(dataQAList.size() > 0){
            SendAnswerAdapter adapter = new SendAnswerAdapter(getContext(), dataQAList, new SendAnswerAdapter.OnKeypressListener() {
                @Override
                public void onKeyPress(int index, String str) {
                    dataQAList.get(index).setAnswer(str);
                }
            });

            CustomSenderAnswerDlg dlg = new CustomSenderAnswerDlg(getContext(), selectedUserInfo.getFull_name(), adapter);
            dlg.setOnItemClickListener(new CustomSenderAnswerDlg.OnDlgItemClickListener(){
                public void onClick(){
                    answer = createAnswerString(dataQAList);
                    question = selectedUserInfo.getQuestion();
                    callSendFriendRequest();
                }
            });
            dlg.setCanceledOnTouchOutside(false);
            dlg.showDialog();
        }else{
            answer = "";
            question="";
            callSendFriendRequest();
        }
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

    private void callSendFriendRequest(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int fromUserId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().FROM_USER, fromUserId);
        mparams.put(ApiClass.getmApiClass().TO_USER, mHotSpotUser.getId());
        mparams.put(ApiClass.getmApiClass().TO_ANS, answer);
        mparams.put(ApiClass.getmApiClass().QUESTION,question);

        Call<Basic_Response> call;
        call = mApiInterface.sendFriendRequest(mparams);
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Basic_Response basic_response = response.body();
                        if(basic_response.getStatus().equalsIgnoreCase("true")) {
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

    private void callUnfriend(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int fromUserId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().FROM_USER, fromUserId);
        mparams.put(ApiClass.getmApiClass().TO_USER, mHotSpotUser.getId());

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
                            Toast.makeText(mActivity,getString(R.string.remove_friend_success), Toast.LENGTH_SHORT).show();
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

    private void  callGetHotSpotUsers(){
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();
        int userid = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();
        mparams.put("userid", userid);
        if(mHotspot != null){
            mparams.put(ApiClass.PLACE_ID, mHotspot.getPlaceId());
            mparams.put(ApiClass.HOTSPOT_ID, mHotspot.getHotspotId());
        }else if(mHotSpotDetails != null){
            mparams.put(ApiClass.PLACE_ID, mHotSpotDetails.getPlaceId());
            mparams.put(ApiClass.HOTSPOT_ID, mHotSpotDetails.getId());
        }

        Call<HotSpotUserResponse> call = mApiInterface.getHotspotUserlist(mparams);
        call.enqueue(new Callback<HotSpotUserResponse>() {
            @Override
            public void onResponse(Call<HotSpotUserResponse> call, Response<HotSpotUserResponse> response) {
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        mHotSpotUserResponse = response.body();
                        if(mHotSpotUserResponse!=null) {
                            if (mHotSpotUserResponse.getStatus().equalsIgnoreCase("true")) {
                                if ( mHotSpotUserResponse.getData() != null && mHotSpotUserResponse.getData().size() > 0) {
                                    whoshereAdapter.setData(mHotSpotUserResponse.getData());
                                    txtNoData.setVisibility(View.GONE);
                                } else {
                                    txtNoData.setVisibility(View.VISIBLE);
                                }
                            }else{
                                AlertDialog.showAlert(mActivity,getString(R.string.app_name),mHotSpotUserResponse.getMessage() , getString(R.string.txt_Done), getString(R.string.cancel), false, null, null);
                            }
                        }
                    } else {
                        System.out.println(response.errorBody());
                    }
                }
            }

            @Override
            public void onFailure(Call<HotSpotUserResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

}
