package com.app.khaleeji.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Adapter.StatusLikersAdapter;
import com.app.khaleeji.Adapter.StatusViewersAdapter;
import com.app.khaleeji.R;
import com.app.khaleeji.databinding.FragmentStatusLikesBinding;
import com.app.khaleeji.databinding.FragmentStatusViewersBinding;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.AppConstants;
import CustomView.CustomTextView;
import Model.QAData;
import Model.StatusLikersModel;
import Model.StatusModel;
import Model.StatusViewersModel;
import Utility.ApiClass;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatusViewersFragment extends BaseFragment {


    private CustomTextView txtNoData;
    private StatusViewersAdapter mStatusViewersAdapter;
    private ArrayList<QAData> dataQAList;
    private String answer="", question;
    private StatusModel statusModel;
    private FragmentStatusViewersBinding mbinding;

    public StatusViewersFragment(){

    }

    public StatusViewersFragment(StatusModel statusModel) {
       this.statusModel = statusModel;
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent (MessageEvent messageEvent) {
//        if(messageEvent.getType() == MessageEvent.MessageType.NEW_MSG_NOTIFICATION){
//            imgBadge.setVisibility(View.VISIBLE);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_status_viewers, container, false);
        View rootView = mbinding.getRoot();

        if(Build.VERSION.SDK_INT >= 21)
            rootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        initView();
        return rootView;
    }

    private void initView(){
        ((MainActivity)mActivity).hide();

//        imgBadge = mRootView.findViewById(R.id.imgBadge);
//        if(SavePref.getInstance(mActivity).getNewMsgNotification()){
//            imgBadge.setVisibility(View.VISIBLE);
//        }else{
//            imgBadge.setVisibility(View.INVISIBLE);
//        }

        callGetStatusLikers();

        mStatusViewersAdapter = new StatusViewersAdapter(mActivity, new StatusViewersAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(StatusViewersModel statusViewersModel, int type){
                switch (type){
                    case AppConstants.TYPE_CIRCLE_IMG:
                        if(statusViewersModel.id.intValue() != SavePref.getInstance(mActivity).getUserdetail().getId().intValue()){
                            Fragment_Process.addFragment(mActivity.getSupportFragmentManager(),new FriendProfileFragment(statusViewersModel.id),mActivity, R.id.framelayout_main);
                        }else{
                            Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), ProfileFragment.newInstance(), mActivity, R.id.framelayout_main);
                        }

                        break;
//                    case AppConstants.TYPE_UNFRIEND:
//                        mLikeUser = mListMediaLikesUsers.get(position);
//                        callUnfriend();
//                        return;
//
//                    case AppConstants.TYPE_ADD_FRIEND:
//                        mLikeUser = mListMediaLikesUsers.get(position);
//                        openSendAnswerDlg();
//                        return;

                }
            }
        });

        mbinding.rvStatusViewers.setAdapter(mStatusViewersAdapter);
        
        mbinding.imgBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).back();
            }
        });
       
    }

    private List<StatusViewersModel> statusViewersModelList;
    private void callGetStatusLikers(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Call<JsonObject> call = mApiInterface.getStatusViewers(statusModel.status_id);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject != null && isAdded()) {
                        if (jsonObject.get("status").getAsBoolean()) {
                            JsonArray jsonArray = jsonObject.getAsJsonArray("data");
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<StatusViewersModel>>() {}.getType();
                            try{
                                statusViewersModelList = gson.fromJson(jsonArray, type);
                                if(statusViewersModelList != null && statusViewersModelList.size() > 0){
                                    mbinding.txtNoData.setVisibility(View.GONE);
                                    mStatusViewersAdapter.setData(statusViewersModelList);
                                }else{
                                    mbinding.txtNoData.setVisibility(View.VISIBLE);
                                    mStatusViewersAdapter.setData(null);
                                }
                            }catch (JsonSyntaxException ex){
                                ex.printStackTrace();
                            }
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

  /*  public void openSendAnswerDlg(){
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
    }*/

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

   /* private void callSendFriendRequest(){
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
    }*/
}
