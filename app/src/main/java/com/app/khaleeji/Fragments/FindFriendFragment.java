package com.app.khaleeji.Fragments;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Adapter.FindFriendAdapter;
import com.app.khaleeji.Adapter.SendAnswerAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.Basic_Response;
import com.app.khaleeji.databinding.FindFriendFragmentBinding;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import CustomView.CustomSenderAnswerDlg;
import Model.ContactUserModel;
import Model.LocalContactModel;
import Model.QAData;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import Utility.ApiClass;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindFriendFragment extends BaseFragment{

    private static final String TAG = "FindFriendFragment";
    private FindFriendFragmentBinding mbinding;
    private FindFriendAdapter mFindFriendAdapter;
    public static List<ContactUserModel> mKahaleejiUsers;
    public static List<LocalContactModel> mLocalUsers;
    public static List<LocalContactModel> localContacts;
    private ArrayList<QAData> dataQAList;
    private ContactUserModel mSelectedUserInfo;
    private String answer="", question;
    private boolean isAllowedContactAccess = true;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent (MessageEvent messageEvent) {
        if(messageEvent.getType() == MessageEvent.MessageType.NEW_MSG_NOTIFICATION){
            mbinding.imgBadge.setVisibility(View.VISIBLE);
        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mbinding = DataBindingUtil.inflate(inflater, R.layout.find_friend_fragment, container, false);
        View view = mbinding.getRoot();
        if(Build.VERSION.SDK_INT >= 21)
            view.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        initView();
        return view;
    }

    private void initView() {
        ((MainActivity)mActivity).hide();

        mbinding.contactSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = ((ToggleButton) view).isChecked();
                if (on) {
                    mbinding.cardAllowContact.setVisibility(View.VISIBLE);
                } else {
                    mbinding.cardAllowContact.setVisibility(View.GONE);
                }
                mbinding.llContactLabel.setVisibility(View.GONE);
                mbinding.llContactUsers.setVisibility(View.GONE);
            }
        });

//        mbinding.contactAllowSwitch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                boolean on = ((ToggleButton) view).isChecked();
//                if (on) {
//                    isAllowedContactAccess = true;
//                }else{
//                    isAllowedContactAccess = false;
//                }
//            }
//        });

//        mbinding.contactAllowSwitch.setChecked(true);

        if(SavePref.getInstance(mActivity).getNewMsgNotification()){
            mbinding.imgBadge.setVisibility(View.VISIBLE);
        }else{
            mbinding.imgBadge.setVisibility(View.INVISIBLE);
        }

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

        mbinding.txtGoSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAllowedContactAccess){
                    mbinding.llContactLabel.setVisibility(View.VISIBLE);
                    mbinding.llContactUsers.setVisibility(View.VISIBLE);
                    mbinding.cardAllowContact.setVisibility(View.GONE);
                }
            }
        });

        mbinding.txtAllContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new AllContactsFragment(), mActivity, R.id.framelayout_main);
            }
        });

        mFindFriendAdapter = new FindFriendAdapter(mActivity, new FindFriendAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int type, int position) {
                if(type == 1) {
                    openSendAnswerDlg(mKahaleejiUsers.get(position));
                    return;
                }
                if(type == 2){
                    //unfriend
                    callUnfriend(mKahaleejiUsers.get(position));
                    return;
                }

                if(type == 3){
                    //cancel
                    callRejectFriendRequest(mKahaleejiUsers.get(position));
                    return;
                }
                Fragment_Process.addFragment(mActivity.getSupportFragmentManager(),
                        new FriendProfileFragment(Integer.parseInt(mKahaleejiUsers.get(position).id)),
                        mActivity, R.id.framelayout_main);
            }
        });
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        mbinding.rvFriends.setLayoutManager(mLayoutManager);
        mbinding.rvFriends.setAdapter(mFindFriendAdapter);

        mbinding.contactSwitch.setChecked(false);
        mbinding.contactSwitch.setEnabled(false);
        showContacts();
    }

    private static int PERMISSIONS_REQUEST_READ_CONTACTS = 111;
    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            mbinding.contactSwitch.setChecked(true);
            mbinding.contactSwitch.setEnabled(true);
            getContactList();
            if(localContacts.size() > 0){
                for(LocalContactModel localContactModel: localContacts){
                    String changedNumber = localContactModel.getPhoneNumber().replace(" ", "").replace("(", "")
                            .replace(")", "").replace("-", "");
                    localContactModel.setPhoneNumber(changedNumber);
                }
                callGetContactUsersByNumber();
            }
        }
    }

    private void getContactList() {
        localContacts = new ArrayList<>();
        ContentResolver cr = mActivity.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        localContacts.add(new LocalContactModel(id, name, phoneNo));
                        Log.i(TAG, "Name: " + name);
                        Log.i(TAG, "Phone Number: " + phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                mbinding.contactSwitch.setChecked(true);
                mbinding.contactSwitch.setEnabled(true);
                showContacts();
            } else {
                mbinding.contactSwitch.setChecked(false);
                mbinding.contactSwitch.setEnabled(false);
                Toast.makeText(mActivity, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
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
                                    mFindFriendAdapter.setData(mKahaleejiUsers);
                                    mbinding.txtNoData.setVisibility(View.GONE);
                                }else{
                                    for(int i = 0; i < localContacts.size(); i++){
                                        if(!containInLocalContact(localContacts.get(i)))
                                            mLocalUsers.add(localContacts.get(i));
                                    }
                                    mbinding.txtNoData.setVisibility(View.VISIBLE);
                                    mFindFriendAdapter.setData(null);
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

    private List<String> khaleejiUserLocalContactIds = new ArrayList<>();
    private void extractLocalContactIdFromKhaleejiUser(){
        for(ContactUserModel khaleejiUser: mKahaleejiUsers){
            for(LocalContactModel localContactModel : localContacts){
                //remove front 0
                if(removeFrontZeroString(localContactModel.getPhoneNumber()).
                        equals(removeFrontZeroString(khaleejiUser.mobile_number))){
                    khaleejiUserLocalContactIds.add(localContactModel.getId());
                    break;
                }
            }
        }
    }

    private String removeFrontZeroString(String str){
        if(str.startsWith("0")){
            return str.substring(1);
        }
        return str;
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

    private boolean containInLocalContact(LocalContactModel localContactModel){
        for(LocalContactModel localContactUser: mLocalUsers){
            if(localContactUser.getId().equals(localContactModel.getId())){
                return true;
            }
        }
        return false;
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
                            mSelectedUserInfo.isFriend = "3";
                            mFindFriendAdapter.setData(mKahaleejiUsers);
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

    private void callUnfriend(ContactUserModel contactUserModel){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int fromUserId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().FROM_USER, fromUserId);
        mparams.put(ApiClass.getmApiClass().TO_USER, contactUserModel.id);

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
                            contactUserModel.isFriend = "2";
                            mFindFriendAdapter.setData(mKahaleejiUsers);
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

    private void callRejectFriendRequest(ContactUserModel contactUserModel){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int fromUserId = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().FROM_USER, fromUserId);
        mparams.put(ApiClass.getmApiClass().TO_USER, contactUserModel.id);

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
                            contactUserModel.isFriend = "2";
                            mFindFriendAdapter.setData(mKahaleejiUsers);
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
