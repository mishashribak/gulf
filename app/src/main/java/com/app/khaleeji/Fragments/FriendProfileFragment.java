package com.app.khaleeji.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.ChatActivity;
import com.app.khaleeji.Activity.FriendDailiesViewActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Adapter.ProfileMemoriesGridAdapter;
import com.app.khaleeji.Adapter.SeeAnswerAdapter;
import com.app.khaleeji.Adapter.SendAnswerAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.Basic_Response;
import com.app.khaleeji.Response.GulfProfileResponse;
import com.app.khaleeji.Response.GulfProfiledata;
import com.app.khaleeji.Response.fetchDailiesOfFriends.FriendListDailiesOfFriends;
import com.app.khaleeji.databinding.FragmentFriendProfileBinding;
import com.squareup.picasso.Picasso;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.AppConstants;
import CustomView.CustomGridView;
import CustomView.CustomReportDlg;
import CustomView.CustomSeeAnswerDlg;
import CustomView.CustomSenderAnswerDlg;
import CustomView.CustomTextView;
import CustomView.SelectPicPopupWindow;
import Model.QAData;
import Utility.ApiClass;
import Utility.ChatHelper;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.khaleeji.Activity.MainActivity.REQUEST_ID_MESSAGE_PERMISSIONS;

public class FriendProfileFragment extends BaseFragment {

    private Context mContext;
    private View mRootView;
    private CustomGridView mGridHotMemory;
    private int mFriendId  ;
    private CustomTextView mTxtCountry;
    private CustomTextView mTxtGender;
    private CustomTextView mTxtBio;
    private CustomTextView mTxtProfileStatus;
    private CustomTextView mTxtLocation;
    private ImageView imgMyProfile, imgEditProfile, imgMyDailiesView;
    private CustomTextView mTxtDistance, txtNoData;
    private CustomTextView txtAddFriend, txtUnFriend, txtMessage;
    private RelativeLayout dobBox;
    private CustomTextView txtDOB;
    private CustomTextView txtUnBlock, txtCancelRequest;
//    private CustomTextView txtRejectRequest;
    private CustomTextView txtTitle;
    private RelativeLayout bioBox;
    private LinearLayout llMemories;
    private CustomTextView txtMyDailies;
    private ImageView imgBadge;
    private FragmentFriendProfileBinding mbinding;
    private boolean isNotification;

    public FriendProfileFragment(int friendId) {
        // Required empty public constructor
        mFriendId = friendId;
    }

    public FriendProfileFragment(){

    }

    public static FriendProfileFragment newInstance(int friendId) {
        FriendProfileFragment fragment = new FriendProfileFragment(friendId);
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent (MessageEvent messageEvent) {
        if(messageEvent.getType() == MessageEvent.MessageType.NEW_MSG_NOTIFICATION){
            imgBadge.setVisibility(View.VISIBLE);
            return;
        }

        if(messageEvent.getType() == MessageEvent.MessageType.PROFILE_REFRESH){
            isNotification = true;
            call_getProfile();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = mActivity;
        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_friend_profile, container, false);
        mRootView = mbinding.getRoot();
         mRootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        initView();
        return mRootView;
    }

    private ProfileMemoriesGridAdapter mProfileMemoriesGridAdapter;
    private ScrollView svinfo;

    private void initView(){
        ((MainActivity)mActivity).hide();

        imgBadge = mRootView.findViewById(R.id.imgBadge);
        if(SavePref.getInstance(mActivity).getNewMsgNotification()){
            imgBadge.setVisibility(View.VISIBLE);
        }else{
            imgBadge.setVisibility(View.INVISIBLE);
        }

        mbinding.llShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new MyAccountCodeFragment(gulfProfileData), mActivity, R.id.framelayout_main);
            }
        });

        mbinding.txtMyFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), new MyFriendFragment(), mActivity, R.id.framelayout_main);
            }
        });

        svinfo = mRootView.findViewById(R.id.svinfo);

        txtNoData = mRootView.findViewById(R.id.txtNoData);
        mTxtDistance = mRootView.findViewById(R.id.txtDistance);
        ImageView imgBack = mRootView.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity) mActivity).back();
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

        txtMyDailies = mRootView.findViewById(R.id.txtMyDailies);

        imgEditProfile = mRootView.findViewById(R.id.imgEditProfile);
        imgEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectDlg();
            }
        });

        mGridHotMemory = mRootView.findViewById(R.id.gridView);
        mProfileMemoriesGridAdapter = new ProfileMemoriesGridAdapter(mActivity, mContext, new ProfileMemoriesGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int index) {
                Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new FriendPostsFragment(index, true),mActivity, R.id.framelayout_main);
                MessageEvent messageEvent = new MessageEvent();
                messageEvent.setType(MessageEvent.MessageType.FRIEND_POSTS);
                messageEvent.setMemoryModelList(gulfProfileData.getMemoryList());
                EventBus.getDefault().postSticky(messageEvent);
            }
        });
        mGridHotMemory.setAdapter(mProfileMemoriesGridAdapter);

        mTxtLocation = mRootView.findViewById(R.id.txtLocation);
        mTxtProfileStatus = mRootView.findViewById(R.id.txtProfileStatus);
        mTxtBio = mRootView.findViewById(R.id.txtBio);
        mTxtCountry = mRootView.findViewById(R.id.txtCountry);
        mTxtGender = mRootView.findViewById(R.id.txtGender);
        imgMyProfile = mRootView.findViewById(R.id.imgMyProfile);
        imgMyDailiesView = mRootView.findViewById(R.id.imgMyDailiesView);
        txtAddFriend = mRootView.findViewById(R.id.txtAddFriend);
        txtUnBlock = mRootView.findViewById(R.id.txtUnBlock);
        txtTitle = mRootView.findViewById(R.id.txtTitle);
        bioBox = mRootView.findViewById(R.id.bioBox);
        llMemories = mRootView.findViewById(R.id.llMemories);
        txtCancelRequest = mRootView.findViewById(R.id.txtCancelRequest);
        txtUnFriend = mRootView.findViewById(R.id.txtUnFriend);
        txtMessage = mRootView.findViewById(R.id.txtMessage);
        dobBox = mRootView.findViewById(R.id.dobBox);
        txtDOB = mRootView.findViewById(R.id.txtDOB);

        mbinding.statusBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new FriendStatusFragment(false, mFriendId,
                       gulfProfileData != null ?gulfProfileData.getUsername() : ""), mActivity, R.id.framelayout_main);
            }
        });

        LinearLayout llMyDailies = mRootView.findViewById(R.id.llMyDailies);
        llMyDailies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gulfProfileData != null && gulfProfileData.getDailyList().size() >0){
                    startActivity(new Intent(mActivity, FriendDailiesViewActivity.class));
                    MessageEvent messageEvent = new MessageEvent();
                    messageEvent.setType(MessageEvent.MessageType.FRIEND_DAILIES_VIEW);
                    List<FriendListDailiesOfFriends> list = new ArrayList<>();
                    FriendListDailiesOfFriends dailiesOfFriends = new FriendListDailiesOfFriends();
                    dailiesOfFriends.setMedia(gulfProfileData.getDailyList());
                    list.add(dailiesOfFriends);
                    messageEvent.setFriendLists(list, 0);
                    EventBus.getDefault().postSticky(messageEvent);
                }
            }
        });

        txtAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSendAnswerDlg();
            }
        });

        txtUnFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callUnfriend();
            }
        });

        txtUnBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callUnBock();
            }
        });

        txtCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callCancelRequest();
            }
        });

        txtMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAndRequestPermissions())
                    openChat();
            }
        });

        mbinding.txtConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callAcceptFriendRequest();
            }
        });

        mbinding.txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callRejectFriendRequest();
            }
        });

        mbinding.txtSeeAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSeeAnswerDlg();
            }
        });

        View viewMenu = ((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_layout_pic, null);
        selectPicPopupWindow = new SelectPicPopupWindow(viewMenu, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btReport:
                        openReportDlg();
                        selectPicPopupWindow.dismiss();
                        break;
                    case R.id.btBlock:
                        selectPicPopupWindow.dismiss();
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mActivity, R.style.MyAlertDialogStyle);
                        builder.setTitle(getResources().getString(R.string.app_name));
                        builder.setMessage(getString(R.string.blockconfirm));
                        builder.setPositiveButton(getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                callBlock();
                            }
                        });
                        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                        break;
                    case R.id.btCancel:
                        selectPicPopupWindow.dismiss();
                        break;
                    case R.id.btUnBlock:
                        selectPicPopupWindow.dismiss();
                        callUnBock();
                        break;
                }
            }
        }, 0);

        call_getProfile();
    }

    private void callCancelRequest(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int fromUserId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().FROM_USER, fromUserId);
        mparams.put(ApiClass.getmApiClass().TO_USER, gulfProfileData.getId());

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
                            txtCancelRequest.setVisibility(View.GONE);
                            txtAddFriend.setVisibility(View.VISIBLE);
                            MessageEvent messageEvent = new MessageEvent();
                            messageEvent.setType(MessageEvent.MessageType.NOTIFICATION);
                            EventBus.getDefault().post(messageEvent);
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

    private void callUnBock(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int fromUserId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();

        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().FROM_USER, fromUserId);
        if(gulfProfileData != null)
            mparams.put(ApiClass.getmApiClass().TO_USER, gulfProfileData.getId());

        Call<Basic_Response> call;
        call = mApiInterface.sendUnblock(mparams);
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Basic_Response basic_response = response.body();
                        if(basic_response.getStatus().equalsIgnoreCase("true")){

                            txtMessage.setVisibility(View.VISIBLE);
                            selectPicPopupWindow.setBlockUI();
                            txtUnBlock.setVisibility(View.GONE);
                            txtAddFriend.setVisibility(View.VISIBLE);
                            MessageEvent messageEvent = new MessageEvent();
                            messageEvent.setType(MessageEvent.MessageType.NOTIFICATION);
                            EventBus.getDefault().post(messageEvent);

//                            UserBlockTask.TaskListener  taskListener = new UserBlockTask.TaskListener() {
//                                @Override
//                                public void onSuccess(ApiResponse apiResponse) {
//                                    Log.i(" Block Status","User is blocked or unblocked"+apiResponse.isSuccess());
//                                }
//
//                                @Override
//                                public void onFailure(ApiResponse apiResponse, Exception exception) {
//
//                                }
//
//                                @Override
//                                public void onCompletion() {
//
//                                }
//                            };
//
//                            new UserBlockTask(getContext(), taskListener, gulfProfileData.getId()+"", false).execute((Void) null);

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

    private SelectPicPopupWindow selectPicPopupWindow;
    private void openSelectDlg(){
        selectPicPopupWindow.showAtLocation(mRootView, Gravity.CENTER, 0, 0);
    }

    private void openReportDlg(){
        CustomReportDlg dlgReport = new CustomReportDlg(mContext, new CustomReportDlg.ItemClickInterface() {
            @Override
            public void onClick(String str) {
                callReport(str);
            }
        });
        dlgReport.setCanceledOnTouchOutside(false);
        dlgReport.showDialog();
    }

    private void callReport(String reason){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int fromUserId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();

        Map mparams = new HashMap();
        mparams.put("userid", fromUserId);
        mparams.put(ApiClass.getmApiClass().OTHER_ID, mFriendId);
        mparams.put("comment", reason);

        Call<Basic_Response> call;
        call = mApiInterface.report(mparams);
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Basic_Response basic_response = response.body();
                        if(basic_response.getStatus().equalsIgnoreCase("true")){
                            Toast.makeText(mActivity, getString(R.string.report_success), Toast.LENGTH_SHORT).show();
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

    public void callBlock(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int fromUserId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().FROM_USER, fromUserId);
        mparams.put(ApiClass.getmApiClass().TO_USER, mFriendId);

        Call<Basic_Response> call;
        call = mApiInterface.sendBlock(mparams);
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Basic_Response basic_response = response.body();
                        if(basic_response.getStatus().equalsIgnoreCase("true")){

                            txtAddFriend.setVisibility(View.GONE);
                            txtUnFriend.setVisibility(View.GONE);
                            txtUnBlock.setVisibility(View.VISIBLE);
                            txtCancelRequest.setVisibility(View.GONE);
                            mbinding.llReceiverLayout.setVisibility(View.GONE);
                            txtMessage.setVisibility(View.GONE);

                            selectPicPopupWindow.setUnblockUI();

                            MessageEvent messageEvent = new MessageEvent();
                            messageEvent.setType(MessageEvent.MessageType.BLOCK);
                            EventBus.getDefault().postSticky(messageEvent);

//                            UserBlockTask.TaskListener  taskListener = new UserBlockTask.TaskListener() {
//                                @Override
//                                public void onSuccess(ApiResponse apiResponse) {
//                                    Toast.makeText(mActivity, getString(R.string.block_success), Toast.LENGTH_SHORT).show();
//                                    Log.i(" Block Status","User is blocked or unblocked"+apiResponse.isSuccess());
//                                }
//
//                                @Override
//                                public void onFailure(ApiResponse apiResponse, Exception exception) {
//
//                                }
//
//                                @Override
//                                public void onCompletion() {
//
//                                }
//                            };
//
//                            new UserBlockTask(getContext(), taskListener, mFriendId+"", true).execute((Void) null);

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

    private ArrayList<QAData> dataQAList;
    private String answer="", question;

    public void openSeeAnswerDlg(){
        dataQAList = new ArrayList<>();
        String str = gulfProfileData.getQuestion();
        if(str != null &&! str.isEmpty()){
            int n;
            while ((n =str.indexOf(AppConstants.SEPERATOR)) != -1){
                String cutStr = str.substring(0, n);
                dataQAList.add( new QAData(cutStr, ""));
                str = str.substring(n+AppConstants.SEPERATOR.length());
            }
            dataQAList.add(new QAData(str, ""));
        }

        str = gulfProfileData.getAnswer();
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
                    Intent intent = new Intent(mActivity, ChatActivity.class);  //ShowCameraToolPictureActivity
                    startActivity(intent);
                }
            });
            dlg.setCanceledOnTouchOutside(false);
            dlg.showDialog();
        }else{
            Toast.makeText(mActivity,getString(R.string.no_question), Toast.LENGTH_SHORT).show();
        }
    }

    public void openSendAnswerDlg(){
        dataQAList = new ArrayList<>();
        String str = gulfProfileData.getQuestion();
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

            CustomSenderAnswerDlg dlg = new CustomSenderAnswerDlg(getContext(), gulfProfileData.getFullName(), adapter);
            dlg.setOnItemClickListener(new CustomSenderAnswerDlg.OnDlgItemClickListener(){
                public void onClick(){
                    answer = createAnswerString(dataQAList);
                    question = gulfProfileData.getQuestion();
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
        mparams.put(ApiClass.getmApiClass().TO_USER, gulfProfileData.getId());
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
                            txtAddFriend.setVisibility(View.GONE);
                            txtCancelRequest.setVisibility(View.VISIBLE);
                            MessageEvent messageEvent = new MessageEvent();
                            messageEvent.setType(MessageEvent.MessageType.NOTIFICATION);
                            EventBus.getDefault().post(messageEvent);

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
        mparams.put(ApiClass.getmApiClass().TO_USER, gulfProfileData.getId());

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
                            Toast.makeText(mActivity,getString(R.string.remove_friend_success), Toast.LENGTH_SHORT );

                            txtUnFriend.setVisibility(View.GONE);
                            txtAddFriend.setVisibility(View.VISIBLE);
                            MessageEvent messageEvent = new MessageEvent();
                            messageEvent.setType(MessageEvent.MessageType.NOTIFICATION);
                            EventBus.getDefault().post(messageEvent);
//                            AlertDialog.showAlert(mActivity, getString(R.string.app_name),getString(R.string.remove_friend_success),
//                                    getString(R.string.txt_Done), getString(R.string.cancel), false, null, null);
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

    private GulfProfiledata gulfProfileData;
    private void call_getProfile() {
        if(!isNotification)
            ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();
        Call<GulfProfileResponse> call = mApiInterface.getProfileWithFriendshipFlag(userId, mFriendId);
        call.enqueue(new Callback<GulfProfileResponse>() {
            @Override
            public void onResponse(Call<GulfProfileResponse> call, Response<GulfProfileResponse> response) {
                if(!isNotification)
                    ProgressDialog.hideprogressbar();
                isNotification = false;
                if (response.isSuccessful()) {
                    GulfProfileResponse mOtpResponse = response.body();

                    if(mOtpResponse!=null && isAdded()){
                        if (mOtpResponse.getStatus().equalsIgnoreCase("true")) {

                            gulfProfileData= mOtpResponse.getData();

                            selectPicPopupWindow.setBlockUI();

                            if(gulfProfileData != null ){

                                if(gulfProfileData.all_other_to_seeMyfriends == 0){
                                    mbinding.txtMyFriends.setVisibility(View.INVISIBLE);
                                }else {
                                    mbinding.txtMyFriends.setVisibility(View.VISIBLE);
                                }

                                if(gulfProfileData.getIsFriend() == 2){
                                    txtAddFriend.setVisibility(View.VISIBLE);
                                    txtUnFriend.setVisibility(View.GONE);
                                    txtUnBlock.setVisibility(View.GONE);
                                    txtCancelRequest.setVisibility(View.GONE);
                                    mbinding.llReceiverLayout.setVisibility(View.GONE);
                                }else if(gulfProfileData.getIsFriend() ==1){
                                    txtAddFriend.setVisibility(View.GONE);
                                    txtUnFriend.setVisibility(View.VISIBLE);
                                    txtUnBlock.setVisibility(View.GONE);
                                    txtCancelRequest.setVisibility(View.GONE);
                                    mbinding.llReceiverLayout.setVisibility(View.GONE);
                                }else  if(gulfProfileData.getIsFriend() == 4){
                                    txtUnBlock.setVisibility(View.VISIBLE);
                                    selectPicPopupWindow.setUnblockUI();
                                    txtUnFriend.setVisibility(View.GONE);
                                    txtAddFriend.setVisibility(View.GONE);
                                    txtCancelRequest.setVisibility(View.GONE);
                                    mbinding.llReceiverLayout.setVisibility(View.GONE);
                                }else  if(gulfProfileData.getIsFriend() == 3){
                                    txtUnBlock.setVisibility(View.GONE);
                                    txtUnFriend.setVisibility(View.GONE);
                                    txtAddFriend.setVisibility(View.GONE);
                                    txtCancelRequest.setVisibility(View.VISIBLE);
                                    mbinding.llReceiverLayout.setVisibility(View.GONE);
                                }else  if(gulfProfileData.getIsFriend() == 5){
                                    txtUnBlock.setVisibility(View.GONE);
                                    txtUnFriend.setVisibility(View.GONE);
                                    txtAddFriend.setVisibility(View.GONE);
                                    txtCancelRequest.setVisibility(View.GONE);
                                    mbinding.llReceiverLayout.setVisibility(View.VISIBLE);
                                }

                                if(gulfProfileData.getIsBlockedMe() == 1){
                                    txtUnBlock.setVisibility(View.GONE);
                                    txtUnFriend.setVisibility(View.GONE);
                                    txtAddFriend.setVisibility(View.GONE);
                                    txtCancelRequest.setVisibility(View.GONE);
                                    mbinding.llReceiverLayout.setVisibility(View.GONE);
                                    txtMessage.setVisibility(View.GONE);
                                }

                                if((gulfProfileData.getProfilePicture()!=null && gulfProfileData.getProfilePicture().trim().length()>0))
                                {
                                    try{
                                        Picasso.with(mActivity).load(ApiClass.ImageBaseUrl + gulfProfileData.getProfilePicture()).fit().centerCrop().placeholder(R.drawable.profile_placeholder).into(imgMyDailiesView);
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }

                                }

                                if((gulfProfileData.getBg_picture()!=null && gulfProfileData.getBg_picture().trim().length()>0))
                                {
                                    try{
                                        Picasso.with(mActivity).load(ApiClass.ImageBaseUrl + gulfProfileData.getBg_picture()).fit().centerCrop().into(imgMyProfile);
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }

                                }
                                if(gulfProfileData.getDailyList().size() == 0){
                                    txtMyDailies.setVisibility(View.GONE);
                                }else{
                                    txtMyDailies.setVisibility(View.VISIBLE);
                                }

                                if(gulfProfileData.getAll_other_to_seeDOB() == 1){
                                    dobBox.setVisibility(View.VISIBLE);
                                    txtDOB.setText(gulfProfileData.getDob());
                                }else{
                                    dobBox.setVisibility(View.GONE);
                                }
                                if(!gulfProfileData.getFullName().isEmpty()){
                                    txtTitle.setText(gulfProfileData.getFullName());
                                }

                                if(gulfProfileData.getPrivacy().equalsIgnoreCase("public")){
                                    mbinding.statusBox.setVisibility(View.VISIBLE);
                                    bioBox.setVisibility(View.VISIBLE);
                                }else {
                                    mbinding.statusBox.setVisibility(View.GONE);
                                    bioBox.setVisibility(View.GONE);
                                }

                                if(Float.parseFloat(gulfProfileData.getDistance()) <= 0.0){
                                    mbinding.info.setVisibility(View.GONE);
                                }else{
                                    mbinding.info.setVisibility(View.VISIBLE);
                                }
                                mTxtDistance.setText(String.format(Locale.US, "%.2f", Float.parseFloat(gulfProfileData.getDistance())) +"KM");
                                mTxtCountry.setText(getCountryNameFromCode(gulfProfileData.getCountryId()));
                                mTxtGender.setText(gulfProfileData.getGender());

                                SpannableString tag = getTags(gulfProfileData.getBio(), "1");
                                mTxtBio.setText(tag == null ? "" : tag);
                                mTxtBio.setMovementMethod(LinkMovementMethod.getInstance());

                                mTxtProfileStatus.setText(gulfProfileData.getProfileStatus());
                                mTxtLocation.setText(gulfProfileData.getCountry());
                                if(gulfProfileData.getMemoryList().size() > 0){
                                    txtNoData.setVisibility(View.GONE);
                                    llMemories.setVisibility(View.VISIBLE);
                                    mProfileMemoriesGridAdapter.setData(gulfProfileData.getMemoryList());
                                }else{
                                    txtNoData.setVisibility(View.VISIBLE);
                                    llMemories.setVisibility(View.GONE);
                                }
                                svinfo.smoothScrollTo(0,0);
                            }
                        }
                    }
                } else {
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<GulfProfileResponse> call, Throwable t) {
                if(!isNotification)
                    ProgressDialog.hideprogressbar();
                isNotification = false;
                Log.i("ProfileFragment", "onResponse" + t.getMessage());
            }
        });

    }

    public String getCountryNameFromCode(Integer code){
        if( code.intValue() == 966){
            return getResources().getString(AppConstants.COUNTRY_NAME[5]);
        }
        if( code.intValue() == 973){
            return getResources().getString(AppConstants.COUNTRY_NAME[0]);
        }
        if( code.intValue() == 964){
            return getResources().getString(AppConstants.COUNTRY_NAME[1]);
        }
        if( code.intValue() == 965){
            return getResources().getString(AppConstants.COUNTRY_NAME[2]);
        }
        if( code.intValue() == 968){
            return getResources().getString(AppConstants.COUNTRY_NAME[3]);
        }
        if( code.intValue() == 974){
            return getResources().getString(AppConstants.COUNTRY_NAME[4]);
        }
        if( code.intValue() == 971){
            return getResources().getString(AppConstants.COUNTRY_NAME[6]);
        }
        if( code.intValue() == 967){
            return getResources().getString(AppConstants.COUNTRY_NAME[7]);
        }
        return "";
    }

    private  boolean checkAndRequestPermissions() {
        int permissionCamera = ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.CAMERA);
        int permissionWriteStorage = ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionRecord = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.RECORD_AUDIO);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (permissionRecord != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            this.requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MESSAGE_PERMISSIONS);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if(requestCode == REQUEST_ID_MESSAGE_PERMISSIONS){
            Map<String, Integer> permsCamera = new HashMap<>();
            // Initialize the map with both permissions
            permsCamera.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
            permsCamera.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            permsCamera.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
            // Fill with actual results from user
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++)
                    permsCamera.put(permissions[i], grantResults[i]);
                // Check for both permissions
                if (permsCamera.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && permsCamera.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && permsCamera.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED ) {
                    // process the normal flow
                   openChat();
                }

            }
        }

    }

    private void openChat() {
//        AppContactService appContactService = new AppContactService(mActivity);
//        if(!appContactService.isContactPresent(gulfProfileData.getId()+"")){
//            Contact contact = new Contact();
//            contact.setUserId(gulfProfileData.getId()+"");
//            contact.setFullName(gulfProfileData.getFullName());
//            contact.setEmailId(gulfProfileData.getEmail());
//            contact.setImageURL(ApiClass.ImageBaseUrl + gulfProfileData.getProfilePicture());
//            appContactService.add(contact);
//        }
//        Intent intent = new Intent(mActivity, ConversationActivity.class);
//        intent.putExtra(ConversationUIService.USER_ID, String.valueOf(gulfProfileData.getId()));
//        intent.putExtra(ConversationUIService.DISPLAY_NAME, gulfProfileData.getFullName());
//
//        startActivity(intent);

        ChatHelper helper = new ChatHelper(mActivity);

        String roomId = helper.createRoomId(SavePref.getInstance(mContext).getUserdetail(), gulfProfileData);

        Intent intent = new Intent(mActivity, ChatActivity.class);
        intent.putExtra("isFromGroup", false);
        intent.putExtra("friendImg", ApiClass.ImageBaseUrl + gulfProfileData.getProfilePicture());
        intent.putExtra("friendName", gulfProfileData.getUsername());
        intent.putExtra("isOnline", "0");
        intent.putExtra("friendId", gulfProfileData.getId().toString());
        intent.putExtra("nodeName", roomId);
        intent.putExtra("isFriend", gulfProfileData.getIsFriend() ==1 ? true : false);
        startActivity(intent);
    }

    private void callAcceptFriendRequest(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().FROM_USER, mFriendId);
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
                            txtAddFriend.setVisibility(View.GONE);
                            txtUnFriend.setVisibility(View.VISIBLE);
                            txtUnBlock.setVisibility(View.GONE);
                            txtCancelRequest.setVisibility(View.GONE);
                            mbinding.llReceiverLayout.setVisibility(View.GONE);
                            MessageEvent messageEvent = new MessageEvent();
                            messageEvent.setType(MessageEvent.MessageType.NOTIFICATION);
                            EventBus.getDefault().post(messageEvent);
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

    private void callRejectFriendRequest(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int userId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();
        Map mparams = new HashMap();

        mparams.put(ApiClass.getmApiClass().FROM_USER, mFriendId );
        mparams.put(ApiClass.getmApiClass().TO_USER, userId);

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
                            txtAddFriend.setVisibility(View.VISIBLE);
                            txtUnFriend.setVisibility(View.GONE);
                            txtUnBlock.setVisibility(View.GONE);
                            txtCancelRequest.setVisibility(View.GONE);
                            mbinding.llReceiverLayout.setVisibility(View.GONE);
                            MessageEvent messageEvent = new MessageEvent();
                            messageEvent.setType(MessageEvent.MessageType.NOTIFICATION);
                            EventBus.getDefault().post(messageEvent);
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
}
