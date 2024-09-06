package com.app.khaleeji.Fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.app.khaleeji.Adapter.ContactListAdapter;
import com.app.khaleeji.databinding.FragmentMessageBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.ChatActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.UserDetails;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.Bundle_Identifier;
import Model.ChatUserData;
import Model.GroupInfo;
import Model.LastMessage;
import Model.UserData;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static Utility.ApiClass.BASE_URL;
import static com.app.khaleeji.Activity.MainActivity.REQUEST_ID_MESSAGE_PERMISSIONS;

public class MsgContactFragment extends BaseFragment implements View.OnClickListener,
        ContactListAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private View view;
    private RecyclerView rvFriendChatList;
    private ImageView imgAdd;
    private EditText edtSearch;
    private ContactListAdapter friendListAdapter;
    private List<ChatUserData> chatUserDataList = new ArrayList<>();
    private FirebaseDatabase database;
    private DatabaseReference myRef1, databaseGroupReference;
    private String friendImg;
    private FragmentMessageBinding mbinding;

    public static MsgContactFragment newInstance() {
        MsgContactFragment fragment = new MsgContactFragment();
        return fragment;
    }

    @Override
    public void onRefresh() {
        mbinding.swipeRefresh.setRefreshing(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message, container, false);
        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_message, container, false);
        view = mbinding.getRoot();
        if(Build.VERSION.SDK_INT >= 21)
            view.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        initView();
        SavePref.getInstance(mActivity).setNewMsgNotification(false);
        setFireBaseListeners();
        search();
        return view;
    }

    private void initView() {
        mbinding.swipeRefresh.setOnRefreshListener(this);
        mbinding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        ((MainActivity)mActivity).hide();
        ImageView imgMenu = view.findViewById(R.id.imgMenu);
        imgMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).openMenu();
            }
        });
        ImageView imgSearch = view.findViewById(R.id.imgSearch);
        imgSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ((MainActivity)mActivity).openSearchTabFragment();
            }
        });

        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);

        database = FirebaseDatabase.getInstance();
        myRef1 = database.getReference("chatUser");
        databaseGroupReference = database.getReference("GroupChatUsers");
        rvFriendChatList = view.findViewById(R.id.rv_friend_chat);
        imgAdd = view.findViewById(R.id.ivAdd);
        imgSearch = view.findViewById(R.id.search_btn);
        edtSearch = view.findViewById(R.id.search_edit);
        chatUserDataList.clear();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        friendListAdapter = new ContactListAdapter(chatUserDataList, getContext(), this);
        rvFriendChatList.setLayoutManager(mLayoutManager);
        rvFriendChatList.setAdapter(friendListAdapter);
        imgAdd.setOnClickListener(this);
        imgSearch.setOnClickListener(this);
    }

    private void search() {

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                friendListAdapter.getFilter().filter(s.toString());

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivAdd:
                openGroupListFragment();
                break;
            case R.id.search_btn:

                break;
        }
    }

    private void openGroupListFragment() {
        Fragment_Process.addFragment(mActivity.getSupportFragmentManager(),
                CreateGroupChatFragment.newInstance(), mActivity, R.id.framelayout_main);
    }

    private String friendId, friendName, nodeName;
    private boolean isOnline;
    private boolean isGroupChat;
    @Override
    public void onItemClick(int type, String nodeName, ChatUserData chatUserData) {
        this.nodeName = nodeName;
        //is group chat
        if(type != 2 && type != 3){
            //is one to one chat
            isGroupChat = false;
            if (!chatUserData.getUser1().getUsername().toLowerCase()
                    .equals(SavePref.getInstance(getContext())
                            .getUserdetail().getUsername().toLowerCase())) {
                friendId = chatUserData.getUser1().getUserId();
                friendName = chatUserData.getUser1().getUsername();
                friendImg = chatUserData.getUser1().getProfilePic();
                isOnline = chatUserData.getUser1().getIsOnline().equals("1")? true : false;
            } else {
                friendId = chatUserData.getUser2().getUserId();
                friendName = chatUserData.getUser2().getUsername();
                friendImg = chatUserData.getUser2().getProfilePic();
                isOnline = chatUserData.getUser2().getIsOnline().equals("1")? true : false;
            }

            if(type == 1){
                Fragment_Process.addFragment(mActivity.getSupportFragmentManager(),new FriendProfileFragment(Integer.parseInt(friendId)),mActivity, R.id.framelayout_main);
            }else{
                isBlocked(friendId);
            }
        }else if(type == 3) {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mActivity, R.style.MyAlertDialogStyle);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage(getString(R.string.are_you_sure_want_to_remove_msg));
            builder.setPositiveButton(getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if(chatUserData.isFromGroup()){
                        databaseGroupReference.child(nodeName).removeValue();
                        database.getReference("GroupPosts").child(nodeName).removeValue();
                    }else{
                        myRef1.child(nodeName).removeValue();
                        database.getReference("Posts").child(nodeName).removeValue();
                    }

                }
            });
            builder.setNegativeButton(getString(R.string.txt_no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
        else if(type == 2){
            //group chat
            isGroupChat = true;
            groupName = chatUserData.getUser1().getUsername();
            groupImage = chatUserData.getUser1().getProfilePic();
            if(checkAndRequestPermissions()) {
                startGroupChat( );
            }
        }
    }

    private void isBlocked(String friendId){
        ProgressDialog.showProgress(mActivity);
        ApiInterface apiInterface = RequestClient.getClient(BASE_URL).create(ApiInterface.class);
        int me = SavePref.getInstance(mActivity).getUserdetail().getId().intValue();

        Map mparams = new HashMap();
        mparams.put("user_id", me);
        mparams.put("other_id", friendId);

        Call<JsonObject> call = apiInterface.isBlocked(mparams);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject != null && isAdded()) {
                        if (jsonObject.get("status").getAsBoolean()) {
                            if(!jsonObject.get("isBlocked").getAsBoolean()){
                                if(checkAndRequestPermissions()) {
                                    startChat();
                                }
                            }else{
                                String message;
                                if (Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(Bundle_Identifier.ArabicLang)) {
                                    message = jsonObject.get("arabicChatMessage").getAsString();
                                }else{
                                    message = jsonObject.get("chatMessage").getAsString();
                                }
                                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mActivity, R.style.MyAlertDialogStyle);
                                builder.setTitle(getResources().getString(R.string.app_name));
                                builder.setMessage(message);
                                builder.setPositiveButton(getString(R.string.ok_text), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    private String groupName;
    private String groupImage;
    private void startGroupChat(){
        Intent intent = new Intent(mActivity, ChatActivity.class);
        intent.putExtra("isFromGroup", true);
        intent.putExtra("groupName", groupName);
        intent.putExtra("groupImage", groupImage);
        intent.putExtra("nodeName", nodeName);
        startActivity(intent);
    }

    private void startChat(){
        Intent intent = new Intent(mActivity, ChatActivity.class);
        intent.putExtra("isFromGroup", false);
        intent.putExtra("friendImg", friendImg);
        intent.putExtra("friendName", friendName);
        intent.putExtra("isOnline", isOnline);
        intent.putExtra("friendId", friendId);
        intent.putExtra("nodeName", nodeName);
        startActivity(intent);
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
                    if(!isGroupChat){
                        startChat();
                    }else{
                        startGroupChat();
                    }
                }
            }
        }
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

    private void setFireBaseListeners() {
        databaseGroupReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try{
                    GroupInfo groupInfo = dataSnapshot.child("GroupInfo").getValue(GroupInfo.class);
                    LastMessage lastMsg = dataSnapshot.child("lastMsg").getValue(LastMessage.class);
                    if(groupInfo == null)
                        return;
                    if (groupInfo.getRoomId().contains(String.valueOf(SavePref.getInstance(getContext())
                            .getUserdetail().getId()))) {
                        chatUserDataList.add(getChatUserData(groupInfo, lastMsg));
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                try{
                    GroupInfo groupInfo = dataSnapshot.child("GroupInfo").getValue(GroupInfo.class);
                    LastMessage lastMsg = dataSnapshot.child("lastMsg").getValue(LastMessage.class);
                    if(groupInfo == null)
                        return;
                    for (int i = 0; i < chatUserDataList.size(); i++) {
                        if (chatUserDataList.get(i).getRoomId() != null) {
                            if (chatUserDataList.get(i).getRoomId().equals(groupInfo.getRoomId())) {
                                chatUserDataList.set(i, getChatUserData(groupInfo, lastMsg));
                                break;
                            }
                        }
                    }
                    Collections.sort(chatUserDataList, new ChatSorter());
                    friendListAdapter.notifyDataSetChanged();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                try{
                    GroupInfo groupInfo = dataSnapshot.child("GroupInfo").getValue(GroupInfo.class);
                    if(groupInfo == null)
                        return;
                    for (int i = 0; i < chatUserDataList.size(); i++) {
                        if (chatUserDataList.get(i).getRoomId().equals(groupInfo.getRoomId())) {
                            chatUserDataList.remove(i);
                            break;
                        }
                    }

                    friendListAdapter.notifyDataSetChanged();
                    Log.d("", "onChildRemoved: ");
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        UserDetails userDetails = SavePref.getInstance(getContext()).getUserdetail();
//        ProgressDialog.showProgress(mActivity);
        mbinding.swipeRefresh.setRefreshing(true);
        myRef1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try{
                    if (userDetails != null) {
                        UserData user1 = dataSnapshot.child("User1").getValue(UserData.class);
                        UserData user2 = dataSnapshot.child("User2").getValue(UserData.class);
                        LastMessage lastMessage = dataSnapshot.child("lastMsg").getValue(LastMessage.class);
                        String roomId = dataSnapshot.child("roomId").getValue(String.class);
                        String createdAt = dataSnapshot.child("createdAt").getValue(String.class);

                        if(user1 == null || user2 == null || roomId == null)
                            return;

                        ChatUserData chatUserData = new ChatUserData();
                        chatUserData.setUser1(user1);
                        chatUserData.setUser2(user2);
                        chatUserData.setLastMessage(lastMessage);
                        chatUserData.setRoomId(roomId);
                        chatUserData.setCreatedAt(createdAt);

                        if (chatUserData.getRoomId().contains(""+userDetails.getId())) {
                            chatUserDataList.add(chatUserData);
//                            Collections.sort(chatUserDataList, new ChatSorter());
                            friendListAdapter.notifyDataSetChanged();
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                try{
                    UserData user1 = dataSnapshot.child("User1").getValue(UserData.class);
                    UserData user2 = dataSnapshot.child("User2").getValue(UserData.class);
                    LastMessage lastMessage = dataSnapshot.child("lastMsg")
                            .getValue(LastMessage.class);
                    String roomId = dataSnapshot.child("roomId").getValue(String.class);
                    String createdAt = dataSnapshot.child("createdAt").getValue(String.class);

                    if(user1 == null || user2 == null || roomId == null)
                        return;

                    ChatUserData chatUserData = new ChatUserData();
                    chatUserData.setUser1(user1);
                    chatUserData.setUser2(user2);
                    chatUserData.setLastMessage(lastMessage);
                    chatUserData.setRoomId(roomId);
                    chatUserData.setCreatedAt(createdAt);
                    for (int i = 0; i < chatUserDataList.size(); i++) {
                        if (chatUserDataList.get(i).getRoomId() != null) {
                            if (chatUserDataList.get(i).getRoomId().equals(chatUserData.getRoomId())) {
                                chatUserDataList.set(i, chatUserData);
                                break;
                            }
                        }
                    }
                    if(chatUserDataList.size() == 0){
                        chatUserDataList.add(chatUserData);
                    }

                    Collections.sort(chatUserDataList, new ChatSorter());

                    friendListAdapter.notifyDataSetChanged();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                try{
                    String roomId = dataSnapshot.child("roomId").getValue(String.class);
                    for (int i = 0; i < chatUserDataList.size(); i++) {
                        if (chatUserDataList.get(i).getRoomId().equals(roomId)) {
                            chatUserDataList.remove(i);
                            break;
                        }
                    }
//                    Collections.sort(chatUserDataList, new ChatSorter());

                    friendListAdapter.notifyDataSetChanged();
                    Log.d("", "onChildRemoved: ");
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Collections.sort(chatUserDataList, new ChatSorter());

                friendListAdapter.notifyDataSetChanged();
                mbinding.swipeRefresh.setRefreshing(false);
//                ProgressDialog.hideprogressbar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private ChatUserData getChatUserData(GroupInfo groupInfo, LastMessage lastMsg) {
        ChatUserData chatUserData = new ChatUserData();
        chatUserData.setRoomId(groupInfo.getRoomId());
        chatUserData.setFromGroup(true);
        chatUserData.getUser1().setUsername(groupInfo.getGroupName());
        chatUserData.getUser1().setProfilePic(groupInfo.getGroupImage());
        chatUserData.getUser2().setUsername(groupInfo.getGroupName());
        chatUserData.getUser2().setProfilePic(groupInfo.getGroupImage());
        chatUserData.setCreatedAt(groupInfo.getCreatedAt());
        chatUserData.setLastMessage(lastMsg);
        return chatUserData;
    }

    public class ChatSorter implements Comparator<ChatUserData>
    {
        public int compare(ChatUserData left, ChatUserData right) {
            String leftDate;
            String rightDate;
            if(left.getLastMessage() == null){
                leftDate = left.getCreatedAt();
            }else{
                leftDate = left.getLastMessage().getDate();
            }

            if(right.getLastMessage() != null){
                rightDate = right.getLastMessage().getDate();
            }else{
                rightDate = right.getCreatedAt();
            }

            if(leftDate == null){
                return -1;
            }

            if(rightDate == null){
                return 1;
            }

            if( Long.parseLong(leftDate) < Long.parseLong(rightDate) )
                return 1;
            else if( Long.parseLong(leftDate) == Long.parseLong(rightDate) )
                return 0;
            else
                return -1;
        }
    }
}