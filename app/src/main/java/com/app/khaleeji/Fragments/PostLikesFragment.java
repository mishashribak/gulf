package com.app.khaleeji.Fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Adapter.PostLikesListAdapter;
import com.app.khaleeji.Adapter.SendAnswerAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.Basic_Response;
import com.app.khaleeji.Response.FetchMediaLikeResponse;
import com.app.khaleeji.Response.FetchMediaLikeUserDetail;
import com.app.khaleeji.Response.MemoryUpdateResponse;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.AppConstants;
import CustomView.CustomSenderAnswerDlg;
import CustomView.CustomTextView;
import Model.ChatUserData;
import Model.QAData;
import Utility.AlertDialog;
import Utility.ApiClass;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostLikesFragment  extends BaseFragment {

    private Context mContext;
    private View mRootView;
    private int mMediaId;
    private List<FetchMediaLikeUserDetail> mListMediaLikesUsers;
    private CustomTextView txtNoData;
    private PostLikesListAdapter mPostLikesAdapter;
    private FetchMediaLikeUserDetail mLikeUser;
    private ArrayList<QAData> dataQAList;
    private String answer="", question;
    private ImageView imgBadge;

    public PostLikesFragment() {

    }

    public PostLikesFragment(int mediaId) {
        // Required empty public constructor
        mMediaId = mediaId;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent (MessageEvent messageEvent) {
        if(messageEvent.getType() == MessageEvent.MessageType.NEW_MSG_NOTIFICATION){
            imgBadge.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = mActivity;
        if (mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_post_likes, null);
        if(Build.VERSION.SDK_INT >= 21)
            mRootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        initView();
        return mRootView;
    }

    private void initView(){
        ((MainActivity)mActivity).hide();

        imgBadge = mRootView.findViewById(R.id.imgBadge);
        if(SavePref.getInstance(mActivity).getNewMsgNotification()){
            imgBadge.setVisibility(View.VISIBLE);
        }else{
            imgBadge.setVisibility(View.INVISIBLE);
        }

        fetchMediaLikeUsers();
        RecyclerView mRvFriendChat = mRootView.findViewById(R.id.rv_friend_chat);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        mPostLikesAdapter = new PostLikesListAdapter(getContext(), new PostLikesListAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(int position, int type){
                switch (type){
                    case AppConstants.TYPE_CIRCLE_IMG:
                        if(mListMediaLikesUsers.get(position).getId().intValue() != SavePref.getInstance(mContext).getUserdetail().getId().intValue()){
                            Fragment_Process.addFragment(mActivity.getSupportFragmentManager(),new FriendProfileFragment(mListMediaLikesUsers.get(position).getId()),mActivity, R.id.framelayout_main);
                        }else{
                            Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), ProfileFragment.newInstance(), mActivity, R.id.framelayout_main);
                        }

//                        ((MainActivity) mActivity).openFriendProfileFragment(mListMediaLikesUsers.get(position).getId());
                        break;
                    case AppConstants.TYPE_UNFRIEND:
                        mLikeUser = mListMediaLikesUsers.get(position);
                        callUnfriend();
                        return;

                    case AppConstants.TYPE_ADD_FRIEND:
                        mLikeUser = mListMediaLikesUsers.get(position);
                        openSendAnswerDlg();
                        return;

                }
            }
        });
        mRvFriendChat.setLayoutManager(mLayoutManager);
        mRvFriendChat.setAdapter(mPostLikesAdapter);
        
        txtNoData = mRootView.findViewById(R.id.txtNoData);

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
    }

    
    private void fetchMediaLikeUsers(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Call<FetchMediaLikeResponse> call = mApiInterface.fetchMediaLikeUsers(mMediaId);

        call.enqueue(new Callback<FetchMediaLikeResponse>() {
            @Override
            public void onResponse(Call<FetchMediaLikeResponse> call, Response<FetchMediaLikeResponse> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful())
                {
                    FetchMediaLikeResponse fetchMediaLikeResponse = response.body();

                    if(fetchMediaLikeResponse!=null && isAdded()){
                        if (fetchMediaLikeResponse.getStatus().equalsIgnoreCase("true")) {

                            mListMediaLikesUsers= fetchMediaLikeResponse.getData();
                            if(mListMediaLikesUsers != null  && mListMediaLikesUsers.size() > 0) {
                                mPostLikesAdapter.setData(mListMediaLikesUsers);
                                txtNoData.setVisibility(View.GONE);
                            } else {
                                txtNoData.setVisibility(View.VISIBLE);
                            }
                            
                        } else {
                            Utility.AlertDialog.showAlert(mActivity, getString(R.string.app_name), fetchMediaLikeResponse.getMessage(), getString(R.string.txt_Done), "", false, null, null);
                        }

                    }
                } else {
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<FetchMediaLikeResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    public void openSendAnswerDlg(){
        dataQAList = new ArrayList<>();
        String str = mLikeUser.getQuestion();
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

            CustomSenderAnswerDlg dlg = new CustomSenderAnswerDlg(getContext(), mLikeUser.getFullName(), adapter);
            dlg.setOnItemClickListener(new CustomSenderAnswerDlg.OnDlgItemClickListener(){
                public void onClick(){
                    answer = createAnswerString(dataQAList);
                    question = mLikeUser.getQuestion();
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
        mparams.put(ApiClass.getmApiClass().TO_USER, mLikeUser.getId());
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
        mparams.put(ApiClass.getmApiClass().TO_USER, mLikeUser.getId());

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
}
