package com.app.khaleeji.Fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Adapter.FamilyGroupAdapter;
import com.app.khaleeji.Adapter.FindFriendAdapter;
import com.app.khaleeji.Adapter.InviteAdapter;
import com.app.khaleeji.Adapter.NearByUserOnMapAdapter;
import com.app.khaleeji.Adapter.SendAnswerAdapter;
import com.app.khaleeji.Adapter.UsersOnKhalleejiAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.Basic_Response;
import com.app.khaleeji.databinding.FragmentAllContactsBinding;
import com.app.khaleeji.databinding.FragmentNearbyUserBinding;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.AppConstants;
import CustomView.CustomSenderAnswerDlg;
import Model.ContactUserModel;
import Model.LocalContactModel;
import Model.QAData;
import Utility.ApiClass;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.khaleeji.Fragments.FindFriendFragment.localContacts;
import static com.app.khaleeji.Fragments.FindFriendFragment.mKahaleejiUsers;
import static com.app.khaleeji.Fragments.FindFriendFragment.mLocalUsers;


public class AllContactsFragment extends BaseFragment {
    private static final String TAG = AllContactsFragment.class.getSimpleName();
    private FragmentAllContactsBinding mbinding;
    private UsersOnKhalleejiAdapter usersOnKhalleejiAdapter;
    private InviteAdapter inviteAdapter;
    private ArrayList<QAData> dataQAList;
    private ContactUserModel mSelectedUserInfo;
    private String answer="", question;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent (MessageEvent messageEvent) {
        if(messageEvent.getType() == MessageEvent.MessageType.NEW_MSG_NOTIFICATION){
            mbinding.imgBadge.setVisibility(View.VISIBLE);
        }

    }

    public AllContactsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_all_contacts, container, false);
        View rootView = mbinding.getRoot();

        if(Build.VERSION.SDK_INT >= 21)
            rootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        initView();
        return rootView;
    }

    private void initView() {

        ((MainActivity)mActivity).hide();

        if(SavePref.getInstance(mActivity).getNewMsgNotification()){
            mbinding.imgBadge.setVisibility(View.VISIBLE);
        }else{
            mbinding.imgBadge.setVisibility(View.INVISIBLE);
        }

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
                if (usersOnKhalleejiAdapter != null) {
                    usersOnKhalleejiAdapter.getFilter().filter(s);
                }
                if(inviteAdapter != null){
                    inviteAdapter.getFilter().filter(s);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        usersOnKhalleejiAdapter = new UsersOnKhalleejiAdapter(mActivity, new UsersOnKhalleejiAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int type, int position) {
                if(type == 1) {
                    openSendAnswerDlg(mKahaleejiUsers.get(position));
                    return;
                }
                if(type == 2){
                    //unfriend
                    callUnfriend(Integer.parseInt(mKahaleejiUsers.get(position).id));
                    return;
                }

                if(type == 3){
                    //cancel
                    callRejectFriendRequest(Integer.parseInt(mKahaleejiUsers.get(position).id));
                    return;
                }
                Fragment_Process.addFragment(mActivity.getSupportFragmentManager(),
                        new FriendProfileFragment(Integer.parseInt(mKahaleejiUsers.get(position).id)),
                        mActivity, R.id.framelayout_main);
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mbinding.rvUsersOnKhaleeji.setLayoutManager(mLayoutManager);
        mbinding.rvUsersOnKhaleeji.setAdapter(usersOnKhalleejiAdapter);
//        mbinding.rvUsersOnKhaleeji.setIndexBarColor("#00ffffff");
//        mbinding.rvUsersOnKhaleeji.setIndexBarTextColor("#382C70");
//        mbinding.rvUsersOnKhaleeji.setIndexBarStrokeVisibility(false);
//        mbinding.rvUsersOnKhaleeji.setIndexTextSize(12);
        usersOnKhalleejiAdapter.setData(mKahaleejiUsers);

        inviteAdapter = new InviteAdapter(mActivity, new InviteAdapter.OnItemClickListener() {
            @Override
            public void onInvite(LocalContactModel localContactModel) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("smsto:"+ localContactModel.getPhoneNumber())); // This ensures only SMS apps respond
                intent.putExtra("sms_body", getString(R.string.add_on_me) + " "+getProfileShareLink());
                startActivity(intent);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        mbinding.rvInviteUsers.setLayoutManager(layoutManager);
        mbinding.rvInviteUsers.setAdapter(inviteAdapter);
        mbinding.rvInviteUsers.setIndexBarColor("#00ffffff");
        mbinding.rvInviteUsers.setIndexBarTextColor("#382C70");
        mbinding.rvInviteUsers.setIndexBarStrokeVisibility(false);
        mbinding.rvInviteUsers.setIndexTextSize(12);
        inviteAdapter.setData(mLocalUsers);

    }

    private String getProfileShareLink() {
        Uri.Builder builder = new Uri.Builder()
                .scheme("https")
                .authority("khaleejiapp.com")
                .path("/app.html")
                .appendQueryParameter("user", String.valueOf(SavePref.getInstance(mActivity).getUserdetail().getUsername()));

        final Uri appLink = builder.build();
//        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
//                .setLink(deepLink)
//                .setDomainUriPrefix("https://khaleeji.page.link")
//                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
//                .buildDynamicLink();
        try{
            URL dynamicLinkUrl = new URL(URLDecoder.decode(appLink.toString(), "UTF-8"));
            return dynamicLinkUrl.toString();
        }catch (UnsupportedEncodingException | MalformedURLException ex){
            ex.printStackTrace();
            return null;
        }
    }

    public void openSendAnswerDlg(ContactUserModel userDetails){
        mSelectedUserInfo = userDetails;
        dataQAList = new ArrayList<>();
        String str = userDetails.question;
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
            SendAnswerAdapter adapter = new SendAnswerAdapter(mActivity, dataQAList, new SendAnswerAdapter.OnKeypressListener() {
                @Override
                public void onKeyPress(int index, String str) {
                    dataQAList.get(index).setAnswer(str);
                }
            });

            CustomSenderAnswerDlg dlg = new CustomSenderAnswerDlg(mActivity, userDetails.full_name, adapter);
            dlg.setOnItemClickListener(new CustomSenderAnswerDlg.OnDlgItemClickListener(){
                public void onClick(){
                    answer = createAnswerString(dataQAList);
                    question = mSelectedUserInfo.question;
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

    private void callUnfriend(int toId){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int fromUserId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().FROM_USER, fromUserId);
        mparams.put(ApiClass.getmApiClass().TO_USER, toId);

        Call<Basic_Response> call;
        call = mApiInterface.unFriend(mparams);
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Basic_Response basic_response = response.body();
                        if(basic_response.getStatus().equalsIgnoreCase("true")) {
                            callGetContactUsersByNumber();
                        }else
                            Toast.makeText(mActivity,getString(R.string.remove_friend_success), Toast.LENGTH_SHORT).show();

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

    private void callRejectFriendRequest(int friendId){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int fromUserId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().FROM_USER, fromUserId);
        mparams.put(ApiClass.getmApiClass().TO_USER,friendId);

        Call<Basic_Response> call;
        call = mApiInterface.rejectFriendRequest(mparams);
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();
                if (isAdded()) {
                    if (response.isSuccessful()) {
                        Basic_Response basic_response = response.body();
                        if(basic_response.getStatus().equalsIgnoreCase("true")) {
                            callGetContactUsersByNumber();
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

    public void callGetContactUsersByNumber(){
        ProgressDialog.showProgress(mActivity);
        mKahaleejiUsers = new ArrayList<>();
        mLocalUsers = new ArrayList<>();
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        JsonObject requestParam = new JsonObject();
        JsonArray idArray = new JsonArray();
        for (LocalContactModel localContactModel: localContacts) {
            idArray.add(localContactModel.getPhoneNumber());
        }
        requestParam.addProperty("user_id", SavePref.getInstance(mActivity).getUserdetail().getId()+"");
        requestParam.add("contacts", idArray);
        Call<JsonObject> call = mApiInterface.getContactUsersByNumber( requestParam );
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
                            Type type = new TypeToken<List<ContactUserModel>>() {}.getType();
                            try{
                                List<ContactUserModel> responseModel = gson.fromJson(jsonArray, type);
                                if(responseModel != null && responseModel.size() > 0){
                                    mKahaleejiUsers.addAll(responseModel);
                                    extractLocalContactIdFromKhaleejiUser();
                                    for(int i = 0; i < localContacts.size(); i++){
                                        if(!contain(localContacts.get(i))){
                                            if(!containInLocalContact(localContacts.get(i)))
                                                mLocalUsers.add(localContacts.get(i));
                                        }
                                    }
                                    usersOnKhalleejiAdapter.setData(mKahaleejiUsers);
                                    mbinding.txtNoData.setVisibility(View.GONE);
                                }else{
                                    mbinding.txtNoData.setVisibility(View.VISIBLE);
                                    usersOnKhalleejiAdapter.setData(null);
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

    public void callSendFriendRequest(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int fromUserId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();

        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().FROM_USER, fromUserId);
        mparams.put(ApiClass.getmApiClass().TO_USER, mSelectedUserInfo.id);
        mparams.put(ApiClass.getmApiClass().TO_ANS, answer);
        mparams.put(ApiClass.getmApiClass().QUESTION, question);

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
                            callGetContactUsersByNumber();
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

    private List<String> khaleejiUserLocalContactIds = new ArrayList<>();
    private void extractLocalContactIdFromKhaleejiUser(){
        for(ContactUserModel khaleejiUser: mKahaleejiUsers){
            for(LocalContactModel localContactModel : localContacts){
                if(localContactModel.getPhoneNumber().equals(khaleejiUser.mobile_number)){
                    khaleejiUserLocalContactIds.add(localContactModel.getId());
                    break;
                }
            }
        }
    }

    private boolean containInLocalContact(LocalContactModel localContactModel){
        for(LocalContactModel localContactUser: mLocalUsers){
            if(localContactUser.getId().equals(localContactModel.getId())){
                return true;
            }
        }
        return false;
    }

    private boolean contain(LocalContactModel localContactModel){
        for(ContactUserModel khaleejiUser: mKahaleejiUsers){
            if(khaleejiUser.mobile_number.equals(localContactModel.getPhoneNumber())){
                return true;
            }
        }

        for(String khaleejiUserContactId : khaleejiUserLocalContactIds){
            if(khaleejiUserContactId.equals(localContactModel.getId())){
                return true;
            }
        }

        return false;
    }

}
