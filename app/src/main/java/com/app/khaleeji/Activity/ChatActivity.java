package com.app.khaleeji.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.app.khaleeji.Adapter.ChatAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.Basic_Response;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.AppConstants;
import Constants.SessionData;
import CustomView.CustomReportDlg;
import CustomView.CustomTextView;
import Model.ChatData;
import Model.ChatDetails;
import Model.ChatUserData;
import Model.GroupData;
import Model.GroupInfo;
import Model.LastMessage;
import Model.UserData;
import Utility.ApiClass;
import Utility.ImagePicker;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static Constants.AppConstants.MEDIA_TYPE_VIDEO;

public class ChatActivity extends BaseActivity implements View.OnClickListener, ChatAdapter.OnMediaItemClickListener{

    private DatabaseReference postsRef;
    private ImageView sendButton, imgMic, imgGsnap, imgUploadImage;
    private EditText messageArea;
    private RecyclerView rvChat;
    private SimpleDateFormat sdf;
    private AmazonS3 s3;
    private TransferUtility transferUtility;
    private List<ChatData> chatDataList = new ArrayList<>();
    private ChatAdapter chatAdapter;
    private MediaRecorder myAudioRecorder;
    private String outputFile;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    private ChatUserData chatUserData;
    private ImageView imgBack, imgSetting;
    private LinearLayout imgAudioGif;
    private String nodeName, friendId, friendName;
    private MediaPlayer mediaPlayer;
    private GulflinkApplication gulflinkApplication;
    private GroupData groupData = new GroupData();
    private ChatDetails chatDetails;
    private Boolean isFromGroup = false;
    private RelativeLayout mRlMenu, mRlMenuGroup;
    private CustomTextView txtName;
    private de.hdodenhof.circleimageview.CircleImageView imgProfile;
    private RelativeLayout mRlayoutMain;
    private CustomTextView mTxtMenuBlock, mTxtMenuReport, mTxtMenuUnfriend;
    private CustomTextView txtMenuClearChat, txtMenuLeave, txtMenuSetting;
    private boolean isShow = false;
    private String mFriendImg;
    private String mFrinedName, mFrinedId;
    private String mGroupName, mGroupImage;
    private boolean mIsFromGroup;
    private boolean mIsOnline;
    private String mVideoUrl;
    private LinearLayout messageLayout;
    private LinearLayout progressBarLayout;
    private CustomTextView txtSending;
    private ProgressBar loadMoreProgress;
    private boolean isFriend;
    private boolean isRunningActivity;

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent (MessageEvent messageEvent) {
        if (messageEvent.getType() == MessageEvent.MessageType.MSG_IMAGE) {
            setFileToUpload(messageEvent.getSavedUrl(), AppConstants.MEDIA_TYPE_ONE_TIME_IMAGE);
        }else if (messageEvent.getType() == MessageEvent.MessageType.MSG_VIDEO) {
            mVideoUrl = messageEvent.getSavedUrl();
            createThumb(messageEvent.getSavedUrl(), AppConstants.MEDIA_TYPE_ONE_TIME_VIDEO);
        }else if(messageEvent.getType() == MessageEvent.MessageType.SENDING_MSG_PROGRESS){
            messageLayout.setVisibility(View.INVISIBLE);
            progressBarLayout.setVisibility(View.VISIBLE);
            txtSending.setText(getString(R.string.sending_your_video));
        }else if(messageEvent.getType() == MessageEvent.MessageType.CHAT_ROOM_UPDATE){
            nodeName = messageEvent.getGroupInfo().getRoomId();//new String(messageEvent.getGroupInfo().getRoomId()); //messageEvent.getGroupInfo().getRoomId();
            postsRef = database.getReference("GroupPosts").child(nodeName);
            mGroupImage = messageEvent.getGroupInfo().getGroupImage();
            mGroupName = messageEvent.getGroupInfo().getGroupName();
            txtName.setText(mGroupName);
            chatDataList.clear();
            if(mGroupImage != null && !mGroupImage.equals(""))
                Picasso.with(this).load(mGroupImage).placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder).fit().into(imgProfile);
            initListeners();
        }else if(messageEvent.getType() == MessageEvent.MessageType.BLOCK){
            finish();
        }
        remvoeSticky();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        isRunningActivity = true;

        SessionData.I().isUserBlocked = false;
        gulflinkApplication = (GulflinkApplication) this.getApplicationContext();

        if (getIntent().getExtras() != null) {
            mFriendImg = getIntent().getExtras().getString("friendImg") ;
            mFrinedName = getIntent().getExtras().getString("friendName") ;
            mFrinedId = getIntent().getExtras().getString("friendId") ;
            mIsOnline = getIntent().getExtras().getBoolean("isOnline") ;
            mIsFromGroup = getIntent().getExtras().getBoolean("isFromGroup") ;
            nodeName = getIntent().getExtras().getString("nodeName") ;
            mGroupImage = getIntent().getExtras().getString("groupImage") ;
            mGroupName = getIntent().getExtras().getString("groupName") ;
            isFriend = getIntent().getExtras().getBoolean("isFriend");
        }

        initView();
        initializeAudioPlayer();
        credentialsProvider();
        setTransferUtility();
        initListeners();
//        setAllUsersOffline();
    }

    private void setUpDelay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (chatUserData.getUser1().getUsername()
                        .equals(SavePref.getInstance(ChatActivity.this).getUserdetail().getUsername())) {
                    usersRef.child(chatUserData.getRoomId()).child("User1").child("isOnline").setValue("1");
                }
                if (chatUserData.getUser2().getUsername()
                        .equals(SavePref.getInstance(ChatActivity.this).getUserdetail().getUsername())) {
                    usersRef.child(chatUserData.getRoomId()).child("User2").child("isOnline").setValue("1");
                }
            }
        }, 2000);
    }

    private void initListeners() {
        messageArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (! mIsFromGroup && chatUserData != null) {
                    if (chatUserData.getUser1() != null && chatUserData.getUser1().getUsername()
                            .equals(SavePref.getInstance(ChatActivity.this).getUserdetail().getUsername())) {
                        usersRef.child(chatUserData.getRoomId()).child("User1").child("isOnline").setValue("2");
                    }
                    if (chatUserData.getUser2() != null && chatUserData.getUser2().getUsername()
                            .equals(SavePref.getInstance(ChatActivity.this).getUserdetail().getUsername())) {
                        usersRef.child(chatUserData.getRoomId()).child("User2").child("isOnline").setValue("2");

                    }
                    setUpDelay();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

       /* postsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                ChatData chatData = dataSnapshot.getValue(ChatData.class);
                String key = dataSnapshot.getKey();
                if (chatData != null && SavePref.getInstance(ChatActivity.this).getUserdetail() != null) {

                    if(!mIsFromGroup){
                        if (chatUserData != null) {
                            if (!chatData.getSenderName().equals(
                                    chatUserData.getUser1().getUsername())) {
                                if (chatUserData.getUser1().getIsOnChatScreen().equals("1")) {
                                    chatData.setIsRead("1");
                                }
                            }
                            if (!chatData.getSenderName().equals(
                                    chatUserData.getUser2().getUsername())) {
                                if (chatUserData.getUser2().getIsOnChatScreen().equals("1")) {
                                    chatData.setIsRead("1");
                                }
                            }
                        }
                    }else{
                        // whether all the other man read
                        if (groupData != null && groupData.getGroupMembers() != null) {
                            List<UserData> userDataList  = groupData.getGroupMembers();
                            for(int i =0 ; i< userDataList.size(); i++){
                                if (!chatData.getSenderName().equals(userDataList.get(i).getUsername())) {
                                    if (userDataList.get(i).getIsOnChatScreen().equals("1")) {
                                        if(!chatData.getIsRead().contains(userDataList.get(i).getUsername()))
                                            chatData.setIsRead(chatData.getIsRead() +"_"+userDataList.get(i).getUsername());
                                    }
                                }
                            }

                        }
                    }

                    postsRef.child(key).setValue(chatData);
                    Log.d("hello", "onChildAdded: " + chatData.getBodyText());
                    chatDataList.add(chatData);
                    rvChat.smoothScrollToPosition(chatDataList.size());
                    chatAdapter.notifyDataSetChanged();
                    sendLastMessageData(chatData);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        usersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(SavePref.getInstance(ChatActivity.this).getUserdetail() != null){
                    if (!mIsFromGroup) {
                        UserData user1 = dataSnapshot.child("User1").getValue(UserData.class);
                        UserData user2 = dataSnapshot.child("User2").getValue(UserData.class);
                        String roomId = dataSnapshot.child("roomId").getValue(String.class);
                        if (roomId != null && user1 != null && user2 != null) {
                            if (roomId.equals(nodeName)) {
                                chatUserData = new ChatUserData();
                                chatUserData.setUser1(user1);
                                chatUserData.setUser2(user2);
                                chatUserData.setRoomId(roomId);
                                SessionData.I().setChatUserData(chatUserData);

                                if (!user2.getUsername().equals(
                                        SavePref.getInstance(ChatActivity.this).getUserdetail().getUsername())) {
                                    if (user2.getIsOnline().equals("0")) {
                                        txtOnline.setText(getString(R.string.offline));
                                    }else if(user2.getIsOnline().equals("1")){
                                        txtOnline.setText(getString(R.string.online));
                                    }else if(user2.getIsOnline().equals("2")){
                                        txtOnline.setText(getString(R.string.typing));
                                    }
                                }else if (!user1.getUsername().equals(
                                        SavePref.getInstance(ChatActivity.this).getUserdetail().getUsername())) {
                                    if (user1.getIsOnline().equals("0")) {
                                        txtOnline.setText(getString(R.string.offline));
                                    }else if(user1.getIsOnline().equals("1")){
                                        txtOnline.setText(getString(R.string.online));
                                    }else if(user1.getIsOnline().equals("2")){
                                        txtOnline.setText(getString(R.string.typing));
                                    }
                                }

                                if (user1.getUsername().equals(
                                        SavePref.getInstance(ChatActivity.this).getUserdetail().getUsername())) {
                                    usersRef.child(chatUserData.getRoomId()).child("User1").child("isOnChatScreen")
                                            .setValue("1");
                                }else if (user2.getUsername().equals(
                                        SavePref.getInstance(ChatActivity.this).getUserdetail().getUsername())) {
                                    usersRef.child(chatUserData.getRoomId()).child("User2").child("isOnChatScreen")
                                            .setValue("1");
                                }
                            }
                        }
                    } else {
                        GroupInfo groupInfo = dataSnapshot.child("GroupInfo").getValue(GroupInfo.class);
                        if (groupInfo != null && groupInfo.getRoomId().equals(nodeName)) {
                            LastMessage lastMessage = dataSnapshot.child("lastMsg").getValue(LastMessage.class);
                            List<UserData> groupMembers = new ArrayList<>();

                            for (int i = 0; i < dataSnapshot.child("GroupMembers").getChildrenCount(); i++) {
                                UserData userData = dataSnapshot.child("GroupMembers")
                                        .child("User" + (i + 1)).getValue(UserData.class);
                                if (userData.getUsername().equals(SavePref.getInstance(getApplicationContext())
                                        .getUserdetail().getUsername())) {
                                    userData.setIsOnChatScreen("1");
                                    usersRef.child(groupInfo.getRoomId()).child("GroupMembers").child("User" + (i + 1)).setValue(userData);
                                }

                                groupMembers.add(userData);

                            }
                            groupData.setGroupInfo(groupInfo);
                            groupData.setGroupMembers(groupMembers);
                            groupData.setLastMsg(lastMessage);
                        }
                    }

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(SavePref.getInstance(ChatActivity.this).getUserdetail() != null){
                    if (!mIsFromGroup) {
                        UserData user1 = dataSnapshot.child("User1").getValue(UserData.class);
                        UserData user2 = dataSnapshot.child("User2").getValue(UserData.class);
                        String roomId = dataSnapshot.child("roomId").getValue(String.class);

                        if (user1 != null && user2 != null && roomId != null) {
                            chatUserData.setUser1(user1);
                            chatUserData.setUser2(user2);
                            chatUserData.setRoomId(roomId);
                            if (roomId.equals(nodeName)) {
                                if (!user1.getUsername().equals(
                                        SavePref.getInstance(ChatActivity.this).getUserdetail().getUsername())) {
                                    if (user1.getIsOnline().equals("0")) {
                                        txtOnline.setText(getString(R.string.offline));
                                    }else if(user1.getIsOnline().equals("1")){
                                        txtOnline.setText(getString(R.string.online));
                                    }else if(user1.getIsOnline().equals("2")){
                                        txtOnline.setText(getString(R.string.typing));
                                    }
                                    if (user1.getIsOnChatScreen().equals("1")) {
                                        for (int i = 0; i < chatDataList.size(); i++) {
                                            if (chatDataList.get(i).getIsRead().equals("0")) {
                                                chatDataList.get(i).setIsRead("1");
                                            }
                                        }
                                        chatAdapter.notifyDataSetChanged();

                                        if(isRunningActivity)
                                            usersRef.child(nodeName)
                                                .child("lastMsg").child("isRead").setValue("1");
                                    }

                                }else if (!user2.getUsername().equals(
                                        SavePref.getInstance(ChatActivity.this).getUserdetail().getUsername())) {
                                    if (user2.getIsOnline().equals("0")) {
                                        txtOnline.setText(getString(R.string.offline));
                                    }else if(user2.getIsOnline().equals("1")){
                                        txtOnline.setText(getString(R.string.online));
                                    }else if(user2.getIsOnline().equals("2")){
                                        txtOnline.setText(getString(R.string.typing));
                                    }
                                    if (user2.getIsOnChatScreen().equals("1")) {
                                        for (int i = 0; i < chatDataList.size(); i++) {
                                            if (chatDataList.get(i).getIsRead().equals("0")) {
                                                chatDataList.get(i).setIsRead("1");
                                            }
                                        }
                                        chatAdapter.notifyDataSetChanged();
                                        if(isRunningActivity)
                                            usersRef.child(nodeName)
                                                .child("lastMsg").child("isRead").setValue("1");
                                    }
                                }

                            }
                        }
                    }
                    else{
                        GroupInfo groupInfo = dataSnapshot.child("GroupInfo").getValue(GroupInfo.class);
                        if (groupInfo != null && groupInfo.getRoomId().equals(nodeName)) {
                            long members = dataSnapshot.child("GroupMembers").getChildrenCount();
                            for (int i = 0; i < members; i++) {
                                UserData userData = dataSnapshot.child("GroupMembers")
                                        .child("User" + (i + 1)).getValue(UserData.class);
                                if(!userData.getUsername().equals(SavePref.getInstance(ChatActivity.this).getUserdetail().getUsername())){
                                    if(userData.getIsOnChatScreen().equals("1")) {
                                        for(int j = 0; j< chatDataList.size(); j++){
                                            if( !chatDataList.get(j).getIsRead().equals("1") && !chatDataList.get(j).getIsRead().contains(userData.getUsername()))
                                                chatDataList.get(j).setIsRead(chatDataList.get(j).getIsRead() +"_"+ userData.getUsername());
                                            if(chatDataList.get(j).getIsRead().split("_").length == members ){
                                                chatDataList.get(j).setIsRead("1");
                                            }
                                        }
                                    }
                                }
                            }
                            chatAdapter.notifyDataSetChanged();
                        }
                    }
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        loadMoreItems();

        postsRef.limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(!isRunningActivity)
                    return;

                if(!isFirst){
                    newMsgCount++;
                    ChatData chatData = dataSnapshot.getValue(ChatData.class);
                    String key = dataSnapshot.getKey();
                    if (chatData != null && SavePref.getInstance(ChatActivity.this).getUserdetail() != null) {
                        if (!mIsFromGroup) {
                            if (chatUserData != null) {
                                if (!chatData.getSenderName().equals(
                                        chatUserData.getUser1().getUsername())) {
                                    if (chatUserData.getUser1().getIsOnChatScreen().equals("1")) {
                                        chatData.setIsRead("1");
                                    }
                                }
                                if (!chatData.getSenderName().equals(
                                        chatUserData.getUser2().getUsername())) {
                                    if (chatUserData.getUser2().getIsOnChatScreen().equals("1")) {
                                        chatData.setIsRead("1");
                                    }
                                }
                            }
                        } else {
                            // whether all the other man read
                            if (groupData != null && groupData.getGroupMembers() != null) {
                                List<UserData> userDataList = groupData.getGroupMembers();
                                for (int i = 0; i < userDataList.size(); i++) {
                                    if (!chatData.getSenderName().equals(userDataList.get(i).getUsername())) {
                                        if (userDataList.get(i).getIsOnChatScreen().equals("1")) {
                                            if (!chatData.getIsRead().contains(userDataList.get(i).getUsername()))
                                                chatData.setIsRead(chatData.getIsRead() + "_" + userDataList.get(i).getUsername());
                                        }
                                    }
                                }

                            }
                        }
                        postsRef.child(key).setValue(chatData);
                        chatDataList.add(chatData);
//                        chatDataList.sort(new ChatSorter());
                        if(!isLoadMore)
                            rvChat.smoothScrollToPosition(chatDataList.size());
                        chatAdapter.notifyDataSetChanged();
                        sendLastMessageData(chatData);
                    }//if
                }
                isFirst = false;
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

      /*  database.getReference("GroupChatUsers").limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        imgMic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startAudioRecording();
                imgAudioGif.setVisibility(View.VISIBLE);
                return true;
            }
        });

        imgMic.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    imgAudioGif.setVisibility(View.GONE);
                    stopAudioRecording();
                }
                return false;
            }
        });

    }

    private void setAllUsersOffline(){
        usersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String roomId = dataSnapshot.child("roomId").getValue(String.class);
                UserData user1 = dataSnapshot.child("User1").getValue(UserData.class);
                UserData user2 = dataSnapshot.child("User2").getValue(UserData.class);
                if(user1 != null)
                    usersRef.child(roomId).child("User1").child("isOnline").setValue("0");
                if(user2 != null)
                    usersRef.child(roomId).child("User2").child("isOnline").setValue("0");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private int pageSize = 30;
    private int pageNo = 1;
    private boolean isLoadMore = true;
    private boolean isFirst = true;
    private int newMsgCount = 0;

    private void loadMoreItems(){
        loadMoreProgress.setVisibility(View.VISIBLE);
        postsRef.limitToLast(pageNo * pageSize + newMsgCount).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isLoadMore){
                    pageNo++;
                    chatDataList.clear();
                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        ChatData chatData = postSnapshot.getValue(ChatData.class);
                        String key = postSnapshot.getKey();
                        if (chatData != null && SavePref.getInstance(ChatActivity.this).getUserdetail() != null) {
                            if (!mIsFromGroup) {
                                if (chatUserData != null) {
                                    if (!chatData.getSenderName().equals(
                                            chatUserData.getUser1().getUsername())) {
                                        if (chatUserData.getUser1().getIsOnChatScreen().equals("1")) {
                                            chatData.setIsRead("1");
                                        }
                                    }
                                    if (!chatData.getSenderName().equals(
                                            chatUserData.getUser2().getUsername())) {
                                        if (chatUserData.getUser2().getIsOnChatScreen().equals("1")) {
                                            chatData.setIsRead("1");
                                        }
                                    }
                                }
                            } else {
                                // whether all the other man read
                                if (groupData != null && groupData.getGroupMembers() != null) {
                                    List<UserData> userDataList = groupData.getGroupMembers();
                                    for (int i = 0; i < userDataList.size(); i++) {
                                        if (!chatData.getSenderName().equals(userDataList.get(i).getUsername())) {
                                            if (userDataList.get(i).getIsOnChatScreen().equals("1")) {
                                                if ( chatData.getIsRead() != null && !chatData.getIsRead().contains(userDataList.get(i).getUsername()))
                                                    chatData.setIsRead(chatData.getIsRead() + "_" + userDataList.get(i).getUsername());
                                            }
                                        }
                                    }

                                }
                            }
                            postsRef.child(key).setValue(chatData);
                            chatDataList.add(chatData);
//                            chatDataList.sort(new ChatSorter());
                            sendLastMessageData(chatData);
                        }//if
                    }
                    isLoadMore = false;
//                    checkCache(chatDataList);
                    chatAdapter.notifyDataSetChanged();
                    loadMoreProgress.setVisibility(View.GONE);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkCache(List<ChatData> chatDataList) {
        HashMap cacheMap = SavePref.getInstance(this).getCacheData();
        if(cacheMap != null){
            for ( ChatData data: chatDataList ) {

            }
        }else{

        }
    }

    public class ChatSorter implements Comparator<ChatData>
    {
        public int compare(ChatData left, ChatData right) {
//            try{
//                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
//                Date date = fmt.parse(left.getDate());
//                Date date1 = fmt.parse(right.getDate());
//            }catch (Exception ex){
//
//            }
//
//            long lt = date.getTime();
//            long rh = date1.getTime();
//            return lt > rh ? 1 : lt == rh? 0 : -1;
//        }
            return left.getDate().compareTo(right.getDate());
        }
    }

    private CustomTextView txtOnline;
    private void initView() {
        setStatusBarColor(R.color.yellow_dark);
        loadMoreProgress = findViewById(R.id.loadMoreProgress);
        messageLayout = findViewById(R.id.lyt_message_area);
        progressBarLayout = findViewById(R.id.progressBarLayout);
        txtSending = findViewById(R.id.txtSending);
        imgBack = findViewById(R.id.imgBack);
        imgSetting = findViewById(R.id.imgSetting);
        messageArea = findViewById(R.id.messageArea);
        sendButton = findViewById(R.id.sendButton);
        imgMic = findViewById(R.id.iv_mic);
        imgGsnap = findViewById(R.id.iv_gsnap);
        imgUploadImage = findViewById(R.id.iv_upload_image);
        imgAudioGif = findViewById(R.id.iv_audio_gif);
        rvChat = findViewById(R.id.rv_chat);
        mRlMenu = findViewById(R.id.rlMenu);
        mRlMenuGroup = findViewById(R.id.rlMenuGroup);
        mRlayoutMain = findViewById(R.id.rlMainLayout);
        mRlayoutMain.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard();
                return false;
            }
        });
        mTxtMenuBlock = findViewById(R.id.txtMenuBlock);
        mTxtMenuReport = findViewById(R.id.txtMenuReport);
        mTxtMenuUnfriend = findViewById(R.id.txtMenuUnfriend);

        txtMenuClearChat = findViewById(R.id.txtMenuClearChat);
        txtMenuLeave = findViewById(R.id.txtMenuLeave);
        txtMenuSetting = findViewById(R.id.txtMenuSetting);

        sendButton.setOnClickListener(this);
        imgUploadImage.setOnClickListener(this);
        imgGsnap.setOnClickListener(this);
        imgSetting.setOnClickListener(this);
        imgBack.setOnClickListener(this);

        txtMenuLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaveGroup();
                hideMenu();
            }
        });

        txtMenuSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideMenu();
                finish();
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);  //ShowCameraToolPictureActivity
                intent.putExtra("startFragment", "UpdateGroupFinalFragment");
                intent.putExtra("groupData", groupData);
                startActivity(intent);
            }
        });

        txtMenuClearChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideMenu();
                clearChat();
            }
        });

        mTxtMenuBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMenu();
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ChatActivity.this, R.style.MyAlertDialogStyle);
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
            }
        });
        mTxtMenuReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMenu();
                openReportDlg();
            }
        });
        if(isFriend){
            mTxtMenuUnfriend.setTextColor(Color.WHITE);
        }else{
            mTxtMenuUnfriend.setTextColor(Color.GRAY);
        }
        mTxtMenuUnfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFriend){
                    hideMenu();
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ChatActivity.this, R.style.MyAlertDialogStyle);
                    builder.setTitle(getResources().getString(R.string.app_name));
                    builder.setMessage(getString(R.string.areUSureUWantToUnFriendUser));
                    builder.setPositiveButton(getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            callUnfriend();
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
            }
        });

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        imgProfile = findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mIsFromGroup){
                    Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                    intent.putExtra("startFragment", "FriendProfileFragment");
                    intent.putExtra("userId", Integer.parseInt(mFrinedId));
                    startActivity(intent);
                }
            }
        });

        if(mFriendImg != null && !mFriendImg.equals(""))
            Picasso.with(this).load(mFriendImg).placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder).fit().into(imgProfile);

        if(mGroupImage != null && !mGroupImage.equals(""))
            Picasso.with(this).load(mGroupImage).placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder).fit().into(imgProfile);

        txtOnline = findViewById(R.id.txtOnline);
        if(mIsFromGroup){
            txtOnline.setVisibility(View.INVISIBLE);
        }else{
            txtOnline.setVisibility(View.VISIBLE);
        }

        if(mIsOnline){
            txtOnline.setText(getResources().getString(R.string.online));
        }else{
            txtOnline.setText(getResources().getString(R.string.offline));
        }

        txtName = findViewById(R.id.txtName);
        if(!mIsFromGroup)
            txtName.setText(mFrinedName);
        else
            txtName.setText(mGroupName);

        database = FirebaseDatabase.getInstance();
        if (!mIsFromGroup) {
            postsRef = database.getReference("Posts").child(nodeName);
            usersRef = database.getReference("chatUser");

        } else {
            postsRef = database.getReference("GroupPosts").child(nodeName);
            usersRef = database.getReference("GroupChatUsers");
        }

        chatDataList.clear();
        chatAdapter = new ChatAdapter(chatDataList, mIsFromGroup,  this, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(mLayoutManager);
        rvChat.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();

        rvChat.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(!recyclerView.canScrollVertically(-1)){
                        isLoadMore = true;
                        loadMoreItems();
                    }
                }
            }
        });
    }

    public void remvoeSticky(){
        MessageEvent stickyEvent = EventBus.getDefault().getStickyEvent(MessageEvent.class);
        if(stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent(stickyEvent);
        }
    }

    @Override
    public void onPause() {
        if (!mIsFromGroup) {
            setIsOnChatScreenStatus();
        }else{
            for(int i= 0 ; i< groupData.getGroupMembers().size(); i++){
                if (groupData.getGroupMembers().get(i).getUsername()
                        .equals(SavePref.getInstance(getApplicationContext()).getUserdetail().getUsername())) {
                    usersRef.child(groupData.getGroupInfo().getRoomId()).child("GroupMembers")
                            .child("User"+(i+1)).child("isOnChatScreen").setValue("0");
                    usersRef.child(groupData.getGroupInfo().getRoomId()).child("GroupMembers")
                            .child("User"+(i+1)).child("isOnline").setValue("0");
                    break;
                }
            }
        }
        releaseAudioPlayer();
        super.onPause();
    }

    private void setIsOnChatScreenStatus() {

        if (!SessionData.I().isUserBlocked) {
            if (SessionData.I().getChatUserData().getUser1().getUsername() != null &&
                    SessionData.I().getChatUserData().getUser1().getUsername().equals(
                            SavePref.getInstance(this).getUserdetail().getUsername())) {
                usersRef.child(SessionData.I().getChatUserData().getRoomId())
                        .child("User1").child("isOnChatScreen").setValue("0");
                usersRef.child(SessionData.I().getChatUserData().getRoomId())
                        .child("User1").child("isOnline").setValue("0");
            }else if (SessionData.I().getChatUserData().getUser2().getUsername() != null &&
                    SessionData.I().getChatUserData().getUser2().getUsername().equals(
                            SavePref.getInstance(this).getUserdetail().getUsername())) {
                usersRef.child(SessionData.I().getChatUserData().getRoomId()).child("User2")
                        .child("isOnChatScreen").setValue("0");
                usersRef.child(SessionData.I().getChatUserData().getRoomId()).child("User2")
                        .child("isOnline").setValue("0");
            }
        }
    }

    private void initializeAudioPlayer() {
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/recording" + System.currentTimeMillis() + ".3gp";

        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        myAudioRecorder.setOutputFile(outputFile);
        mediaPlayer = new MediaPlayer();
    }

    private void stopAudioRecording() {
        try {
            myAudioRecorder.stop();
            myAudioRecorder.release();
            myAudioRecorder = null;
        } catch (Exception ex) {
            ex.printStackTrace();
            myAudioRecorder = null;
            return;
        }
        setFileToUpload(outputFile, AppConstants.MEDIA_TYPE_AUDIO);
    }

    private void startAudioRecording() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                releaseAudioPlayer();
            }
        }
        if (myAudioRecorder == null) {
            initializeAudioPlayer();
        }
        try {
            myAudioRecorder.prepare();
        } catch (IllegalStateException | IOException ex) {
            ex.printStackTrace();
        }
        myAudioRecorder.start();
    }

    private void playAudio(String audioUrl, final int index) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        if (mediaPlayer.isPlaying()) {
            releaseAudioPlayer();
            mediaPlayer = new MediaPlayer();
        }
        try {
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
            releaseAudioPlayer();
            chatAdapter.notifyDataSetChanged();
            return;
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                releaseAudioPlayer();
                chatAdapter.notifyDataSetChanged();
            }
        });
    }

    private void releaseAudioPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 2) {
                Uri selectedMediaUri = data.getData();
                if (selectedMediaUri.toString().contains("image")) {
                    selectImageFromGalleryResult(data);
                } else  if (selectedMediaUri.toString().contains("video")) {
                    selectVideoFromGalleryResult(data);
                }

            }
        }
    }

    private void selectVideoFromGalleryResult(Intent data) {
        Uri uri_data = data.getData();
        try {
            mVideoUrl = getPath(uri_data);
            if (mVideoUrl != null){
                messageLayout.setVisibility(View.INVISIBLE);
                progressBarLayout.setVisibility(View.VISIBLE);
                txtSending.setText(getString(R.string.sending_your_video));
                createThumb(mVideoUrl, MEDIA_TYPE_VIDEO);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectImageFromGalleryResult(Intent data) {
        Uri uri_data = data.getData();
        try {
            String imageUrl = getImagePath(uri_data);
            if (imageUrl != null){
                setFileToUpload(imageUrl, AppConstants.MEDIA_TYPE_PHOTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        try {
//            Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(ChatActivity.this.getContentResolver(), uri_data);
//            int nh = (int) (originalBitmap.getHeight() * (512.0 / originalBitmap.getWidth()));
//            Bitmap compressedBitmap = Bitmap.createScaledBitmap(originalBitmap, 512, nh, true);
//            File compressedFile = Utils.createFileFromBitmap(compressedBitmap, ChatActivity.this);
//            strImagepath = compressedFile.getPath();
//            setFileToUpload(strImagepath, AppConstants.MEDIA_TYPE_PHOTO);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = ChatActivity.this.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public String getImagePath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = ChatActivity.this.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public void openGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/* video/*");
        startActivityForResult(i, 2);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_upload_image:
                openGallery();
                break;

            case R.id.iv_gsnap:
                SessionData.I().isFromGroup = mIsFromGroup;
                if(isFromGroup) {
//                chatDetails = new ChatDetails();
//                chatDetails.setFriendId("");
//                chatDetails.setFriendName(friendName);
//                chatDetails.setNodeName(nodeName);
//                chatDetails.setFromGroup(isFromGroup);
//                chatDetails.setPhotoClicked(true);
//                SessionData.I().setChatDetails(chatDetails);
                }
                Intent intent = new Intent(this, CameraActivity.class);
                intent.putExtra("isFromChat", true);
                startActivity(intent);
                break;

            case R.id.sendButton:
                isFirst = false;
                sendMessage(AppConstants.MEDIA_TYPE_TEXT, messageArea.getText().toString(), "");
                break;
            case R.id.imgBack:
                back();
//                if (!Objects.requireNonNull(getArguments().getBoolean("isFromOtherProfile"))) {
//                    openMessageFragment();
//                } else {
//                    ChatActivity.this.onBackPressed();
//                }
                break;
            case R.id.imgSetting:
                isShow = ! isShow;
                if( isShow )
                    showMenu();
                else
                    hideMenu();
//                if (!Objects.requireNonNull(getArguments().getBoolean("isFromGroup"))) {
//                    openBlockAndReportDialog();
//                } else {
//                    openGroupSettingDialog();
//                }
                break;
            case R.id.txtMenuBlock:
                hideMenu();
                isShow = false;
                break;
            case R.id.txtMenuReport:
                hideMenu();
                isShow = false;
                break;
            case R.id.txtMenuUnfriend:
                hideMenu();
                isShow = false;
                break;
        }
    }

    public void back(){
        isRunningActivity = false;
        finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        isRunningActivity = false;
    }

    @Override
    public void onBackPressed() {
        back();
    }

    public void hideMenu(){
        if(mIsFromGroup) {
            mRlMenuGroup.setVisibility(View.GONE);
        }
        else{
            mRlMenu.setVisibility(View.GONE);
        }

//        Animation animation  = AnimationUtils.loadAnimation(this, R.anim.menu_slide_up);
//        animation.setDuration(300);
//        mRlMenu.setAnimation(animation);
//        mRlMenu.animate().setListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                mRlMenu.setVisibility(View.GONE);
//            }
//        });
//        animation.start();
    }

    public void showMenu(){
        if(mIsFromGroup){
            mRlMenuGroup.setVisibility(View.VISIBLE);
        }else{
            mRlMenu.setVisibility(View.VISIBLE);
        }

//        Animation animation  = AnimationUtils.loadAnimation(this, R.anim.menu_slide);
//        animation.setDuration(300);
//        mRlMenu.setAnimation(animation);
//        mRlMenu.animate();
//        animation.start();
    }

    private void sendMessage(String mediaType, String messageText, String thumbImage) {
        String fr = sdf.format(new Date());
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        if (!messageText.equals("")) {
            ChatData chatData = new ChatData();
            chatData.setDate(ts);
            chatData.setBodyText(getMessageWithMediaType(messageText, mediaType));
            chatData.setSenderName(SavePref.getInstance(ChatActivity.this).getUserdetail().getUsername());
            chatData.setIsRead("0");
            chatData.setThumbImage(thumbImage);
            chatData.setMediaType(mediaType);
            chatData.setOneTimeMediaStatus("0");
            chatData.setProfilePic(ApiClass.ImageBaseUrl + SavePref.getInstance(ChatActivity.this).getUserdetail().getProfilePicture());
            chatData.setSenderId(String.valueOf(SavePref.getInstance(ChatActivity.this).getUserdetail().getId()));
            postsRef.push().setValue(chatData);
            messageArea.setText("");
            sendNotification(mediaType);
        }
    }

    private String getMessageWithMediaType(String messageText, String mediaType) {
        String messagePostFix = "";
        switch (mediaType) {

            case AppConstants.MEDIA_TYPE_AUDIO:
                messagePostFix = AppConstants.TYPE_AUDIO;
                break;
            case MEDIA_TYPE_VIDEO:
                messagePostFix = AppConstants.TYPE_VIDEO;
                break;
            case AppConstants.MEDIA_TYPE_ONE_TIME_IMAGE:
                messagePostFix = AppConstants.TYPE_PHOTO;
                break;
            case AppConstants.MEDIA_TYPE_PHOTO:
                messagePostFix = AppConstants.TYPE_PHOTO;
                break;
            case AppConstants.MEDIA_TYPE_ONE_TIME_VIDEO:
                messagePostFix = AppConstants.TYPE_VIDEO;
                break;
        }
        return messageText + messagePostFix;
    }

    private void sendLastMessageData(ChatData chatData) {
        LastMessage lastMessage = new LastMessage();
        lastMessage.setMessage(chatData.getBodyText());
        lastMessage.setDate(chatData.getDate());
        lastMessage.setIsRead(chatData.getIsRead());
        lastMessage.setUsername(chatData.getSenderName());
        usersRef.child(nodeName)
                .child("lastMsg").setValue(lastMessage);
    }

    private File savedVideoThumb;
    public void createThumb(String videoPath, String mediaType){
        File movFile = new File(videoPath);
        Bitmap thumbBmp = ThumbnailUtils.createVideoThumbnail(movFile.getAbsolutePath(),
                MediaStore.Video.Thumbnails.MINI_KIND);
        savedVideoThumb = saveThumbnail(thumbBmp);
        if(savedVideoThumb == null){
            progressBarLayout.setVisibility(View.GONE);
            return;
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                setThumbToUpload(savedVideoThumb, mediaType);
            }
        }, 100);

    }

    private String thumbUrl;
    public void setThumbToUpload(File f, String mediaType) {
        final Uri fileUri = Uri.parse(f.toString());
        String key = f.getName();
        if (fileUri != null) {
            TransferObserver transferObserver = transferUtility.upload(
                    ApiClass.S3_BUCKET_NAME,      /* The bucket to upload to */
                    key,                         /* The key for the uploaded object */
                    f                    /* The file where the data to upload exists */
            );
            thumbUrl = "https://s3-ap-southeast-1.amazonaws.com/" + ApiClass.S3_BUCKET_NAME + "/" + key;
            transferThumbObserverListener(transferObserver, mediaType);
        }
    }

    public void transferThumbObserverListener(TransferObserver transferObserver, String mediaType) {
        transferObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    setFileToUpload(mVideoUrl, mediaType);
                } else if (TransferState.FAILED == state) {
                    messageLayout.setVisibility(View.VISIBLE);
                    progressBarLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;

                Log.e("SendToVideo", "onProgressChangedThumb : " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("SendToVideo", "transferObserverListener onError : " + ex.getMessage());
                messageLayout.setVisibility(View.VISIBLE);
                progressBarLayout.setVisibility(View.GONE);
            }

        });
    }

    private File saveThumbnail(Bitmap bitmap) {
        try {
            // File file = new File(filePath);

            File file = ImagePicker.getThumbnailFile();
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();

            return file;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(null, "Save file error!");
            return null;
        }
    }

    public void setFileToUpload(String path, String mediaType) {
        try {
            final File tempFile = new File(path);
            final Uri fileUri = Uri.parse(tempFile.toString());
            String key = tempFile.getName();
            if (fileUri != null) {
                TransferObserver transferObserver = transferUtility.upload(
                        ApiClass.S3_BUCKET_NAME,       /* The bucket to upload to */
                        key,                          /* The key for the uploaded object */
                        tempFile                     /* The file where the data to upload exists */
                );
                String finalUrl = "https://s3-ap-southeast-1.amazonaws.com/" + ApiClass.S3_BUCKET_NAME + "/" + key;

                transferObserverListener(transferObserver, mediaType, finalUrl);

                if(mediaType.equals(AppConstants.MEDIA_TYPE_ONE_TIME_IMAGE) || mediaType.equals(AppConstants.MEDIA_TYPE_PHOTO)){
                    messageLayout.setVisibility(View.INVISIBLE);
                    progressBarLayout.setVisibility(View.VISIBLE);
                    txtSending.setText(getString(R.string.sending_your_photo));

                }else if(mediaType.equals(AppConstants.MEDIA_TYPE_AUDIO)) {
                    messageLayout.setVisibility(View.INVISIBLE);
                    progressBarLayout.setVisibility(View.VISIBLE);
                    txtSending.setText(getString(R.string.sending_your_voice));
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void transferObserverListener(TransferObserver transferObserver, String mediaType, String finalUrl) {
        transferObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    if(mediaType.equals(AppConstants.MEDIA_TYPE_ONE_TIME_IMAGE) || mediaType.equals(AppConstants.MEDIA_TYPE_PHOTO)){
                        sendMessage(mediaType, finalUrl,finalUrl);
                    }else if(mediaType.equals(AppConstants.MEDIA_TYPE_ONE_TIME_VIDEO) || mediaType.equals(MEDIA_TYPE_VIDEO)){
                        sendMessage(mediaType, finalUrl,thumbUrl);
                    }else if(mediaType.equals(AppConstants.MEDIA_TYPE_AUDIO)){
                        sendMessage(mediaType, finalUrl,"");
                        initializeAudioPlayer();
                    }else{
                        sendMessage(mediaType, finalUrl,"");
                    }

                    messageLayout.setVisibility(View.VISIBLE);
                    progressBarLayout.setVisibility(View.GONE);

//                    if (recordedVideoThumb != null && recordedVideoThumb.exists()) {
//                        recordedVideoThumb.delete();
//                    }
//                    if (tempFile != null && tempFile.exists()) {
//                        tempFile.delete();
//                    }

                } else if (TransferState.FAILED == state) {
                    messageLayout.setVisibility(View.VISIBLE);
                    progressBarLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;

                Log.e("SendToVideoContent", "onProgressChanged : " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("SendToVideoContent", "transferObserverListener onError : " + ex.getMessage());
                messageLayout.setVisibility(View.VISIBLE);
                progressBarLayout.setVisibility(View.GONE);
            }

        });
    }

    public void credentialsProvider() {
        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                ChatActivity.this,
                ApiClass.S3_COGNITO_POOL_ID, // Identity Pool ID
                Regions.AP_SOUTHEAST_1 // Region
        );
        setAmazonS3Client(credentialsProvider);
    }

    public void setAmazonS3Client(CognitoCachingCredentialsProvider credentialsProvider) {

        // Create an S3 client
        s3 = new AmazonS3Client(credentialsProvider);
        // Set the region of your S3 bucket
        s3.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_1));

    }

    public void setTransferUtility() {
        transferUtility = new TransferUtility(s3, ChatActivity.this);
    }

    @Override
    public void onMediaItemClick(String mediaTpe, String message, int position) {
        switch (mediaTpe) {
            case AppConstants.MEDIA_TYPE_AUDIO:
                playAudio(getOriginalUrl(message), position);
                break;
            case AppConstants.MEDIA_TYPE_PHOTO:
                openFullScreenActivity(message);
                break;
            case AppConstants.MEDIA_TYPE_ONE_TIME_IMAGE:
                ChatData chatData = chatDataList.get(position);
                if (!chatData.getSenderName()
                        .equals(SavePref.getInstance(ChatActivity.this).getUserdetail().getUsername())) {
                    if(chatData.getOneTimeMediaStatus().equals("2")){
                        chatAdapter.notifyDataSetChanged();
                        return;
                    }

                    String status = chatData.getOneTimeMediaStatus().equals("0") ? "2": "2";
                    chatDataList.get(position).setOneTimeMediaStatus(status);
                    updateMediaStatus(chatDataList.get(position).getDate(), status);
                    openFullScreenActivity(message);
                }else{
                    openFullScreenActivity(message);
                }
                break;
            case AppConstants.MEDIA_TYPE_ONE_TIME_VIDEO:
                chatData = chatDataList.get(position);
                if (!chatData.getSenderName()
                        .equals(SavePref.getInstance(ChatActivity.this).getUserdetail().getUsername())) {
                    if(chatData.getOneTimeMediaStatus().equals("2")){
                        chatAdapter.notifyDataSetChanged();
                        return;
                    }

                    String status = chatData.getOneTimeMediaStatus().equals("0") ? "2": "2";
                    chatDataList.get(position).setOneTimeMediaStatus(status);
                    updateMediaStatus(chatDataList.get(position).getDate(), status);
                    openVideoPlayerActivity(message);
                }else{
                    openVideoPlayerActivity(message);
                }
                break;
            case MEDIA_TYPE_VIDEO:
                openVideoPlayerActivity(message);
                break;

        }

    }

    private String getOriginalUrl(String message) {

        if (message.contains(AppConstants.TYPE_AUDIO)) {
            return message.replace(AppConstants.TYPE_AUDIO, "");

        } else if (message.contains(AppConstants.TYPE_VIDEO)) {
            return message.replace(AppConstants.TYPE_VIDEO, "");

        } else if (message.contains(AppConstants.TYPE_PHOTO)) {
            return message.replace(AppConstants.TYPE_PHOTO, "");

        }
        return message;
    }

    @Override
    public void onItemClick(String type, String date, int index) {
        if (type.equals(AppConstants.ON_LONG_CLICK)) {
            if (chatDataList.get(index).getSenderName()
                    .equals(SavePref.getInstance(ChatActivity.this).getUserdetail().getUsername())) {
                openDeleteMessageDialog(date, index);
            }
        }
    }

    private void openDeleteMessageDialog(final String date, final int index) {
        AlertDialog alertDialog = new AlertDialog.Builder(
                ChatActivity.this).create();
        alertDialog.setTitle(getString(R.string.delete_message));
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.txt_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        chatDataList.remove(index);
                        chatAdapter.notifyDataSetChanged();
                        sendLastMessageData(chatDataList.get(chatDataList.size()-1));
                        deleteMessage(date);
                        dialog.dismiss();
                    }
                });

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void deleteMessage(String date) {
        Query deleteQuery = postsRef.orderByChild("date").equalTo(date);
        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    data.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("", "onCancelled", databaseError.toException());
            }
        });
    }

    private void updateMediaStatus(String date, final String status) {
        Query query = postsRef.orderByChild("date").equalTo(date);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    postsRef.child(data.getKey()).child("oneTimeMediaStatus").setValue(status);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("", "onCancelled", databaseError.toException());
            }
        });
    }

    private void updateDataInLocalLists(String nodeName) {

        for (int i = 0; i < gulflinkApplication.chatUserDataList.size(); i++) {
            if (gulflinkApplication.chatUserDataList.get(i).getRoomId().equals(nodeName)) {
                gulflinkApplication.chatUserDataList.remove(i);
                break;
            }
        }
        for (int i = 0; i < gulflinkApplication.roomIdList.size(); i++) {
            if (gulflinkApplication.roomIdList.get(i).equals(nodeName)) {
                gulflinkApplication.roomIdList.remove(i);
                break;
            }
        }
    }

    private void clearChat(){
        postsRef.removeValue();
        usersRef.child(nodeName).child("lastMsg").removeValue();
        chatDataList.clear();
        chatAdapter.notifyDataSetChanged();
    }

    private void deleteUserChat() {
        postsRef.removeValue();
        updateDataInLocalLists(nodeName);
        SessionData.I().isUserBlocked = true;

        usersRef.child(nodeName).removeValue();
        finish();

//        Query deleteQuery = usersRef.orderByChild("roomId").equalTo(nodeName);
//        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                    data.getRef().removeValue();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e("", "onCancelled", databaseError.toException());
//            }
//        });


    }

    private void openFullScreenActivity(String imageUrl) {
        Intent intent = new Intent(ChatActivity.this, FullScreenImageActivity.class);
        intent.putExtra("imageUrl", getOriginalUrl(imageUrl));
        startActivity(intent);
    }

    private void openVideoPlayerActivity(String videoUrl) {
        Intent intent = new Intent(ChatActivity.this, VideoPlayerActivity.class);
        intent.putExtra("videoUrl", getOriginalUrl(videoUrl));
        startActivity(intent);
    }

    private void openReportDlg(){
        CustomReportDlg dlgReport = new CustomReportDlg(this, new CustomReportDlg.ItemClickInterface() {
            @Override
            public void onClick(String str) {
                callReport(str);
            }
        });
        dlgReport.setCanceledOnTouchOutside(false);
        dlgReport.showDialog();
    }

    private void callReport(String reason){
        ProgressDialog.showProgress(this);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int fromUserId = SavePref.getInstance(this).getUserdetail().getId().intValue();

        Map mparams = new HashMap();
        mparams.put("userid", fromUserId);
        mparams.put(ApiClass.getmApiClass().OTHER_ID, mFrinedId);
        mparams.put("comment", reason);

        Call<Basic_Response> call;
        call = mApiInterface.report(mparams);
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    Basic_Response basic_response = response.body();
                    if(basic_response.getStatus().equalsIgnoreCase("true")){
                        Toast.makeText(ChatActivity.this, getString(R.string.report_success), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    System.out.println(response.errorBody());
                }
            }
            @Override
            public void onFailure(Call<Basic_Response> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    public void callBlock(){
        ProgressDialog.showProgress(this);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int fromUserId = SavePref.getInstance(this).getUserdetail().getId().intValue();

        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().FROM_USER, fromUserId);
        mparams.put(ApiClass.getmApiClass().TO_USER, mFrinedId);

        Call<Basic_Response> call;
        call = mApiInterface.sendBlock(mparams);
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();

                if (response.isSuccessful()) {
                    Basic_Response basic_response = response.body();
                    if(basic_response.getStatus().equalsIgnoreCase("true")){
                        finish();
//                        deleteUserChat();
//                        Toast.makeText(ChatActivity.this, getString(R.string.block_success), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    System.out.println(response.errorBody());
                }

            }
            @Override
            public void onFailure(Call<Basic_Response> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    private void callUnfriend(){
        ProgressDialog.showProgress(this);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        int fromUserId = SavePref.getInstance(this).getUserdetail().getId().intValue();
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().FROM_USER, fromUserId);
        mparams.put(ApiClass.getmApiClass().TO_USER, mFrinedId);

        Call<Basic_Response> call;
        call = mApiInterface.unFriend(mparams);
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                ProgressDialog.hideprogressbar();

                if (response.isSuccessful()) {
                    Basic_Response basic_response = response.body();
                    if(basic_response.getStatus().equalsIgnoreCase("true")){
                        Toast.makeText(ChatActivity.this,getString(R.string.remove_friend_success), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    System.out.println(response.errorBody());
                }

            }

            @Override
            public void onFailure(Call<Basic_Response> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    public void sendNotification(String mediaType){
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);

//        JSONObject requestParam = new JSONObject();
        JsonObject requestParam = new JsonObject();
//        JSONArray idArray = new JSONArray();
        JsonArray idArray = new JsonArray();
        if(!mIsFromGroup){
            idArray.add(mFrinedId);
        }else{
            if (groupData != null && groupData.getGroupMembers() != null) {
                List<UserData> userDataList  = groupData.getGroupMembers();
                for(int i =0 ; i< userDataList.size(); i++){
                    if (!SavePref.getInstance(this).getUserdetail().getUsername().equals(
                            userDataList.get(i).getUsername())) {
                        idArray.add(userDataList.get(i).getUserId());
                    }
                }

            }
        }

        requestParam.add("ids", idArray);
        requestParam.addProperty("msgType", mediaType);
        requestParam.addProperty("full_name", SavePref.getInstance(this).getUserdetail().getFullName());
        requestParam.addProperty("user_id", SavePref.getInstance(this).getUserdetail().getId());

        Call<Basic_Response> call;
        call = mApiInterface.sendChatNotificaitons(requestParam);
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                if (response.isSuccessful()) {
                    Basic_Response basic_response = response.body();
                    if(basic_response.getStatus().equalsIgnoreCase("true")){
//                        Toast.makeText(ChatActivity.this, getString(R.string.block_success), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    System.out.println(response.errorBody());
                }

            }
            @Override
            public void onFailure(Call<Basic_Response> call, Throwable t) {
            }
        });
    }

    private void leaveGroup() {
        for (int i = 0; i < groupData.getGroupMembers().size(); i++) {
            if (groupData.getGroupMembers().get(i).getUsername()
                    .equals(SavePref.getInstance(ChatActivity.this).getUserdetail().getUsername())) {
                groupData.getGroupMembers().remove(i);
                break;
            }
        }

        groupData.getGroupInfo().setRoomId(createNodeName());
        usersRef.child(groupData.getGroupInfo().getRoomId()).child("GroupInfo").setValue(groupData.getGroupInfo());
        usersRef.child(groupData.getGroupInfo().getRoomId()).child("lastMsg").setValue(groupData.getLastMsg());

        for (int i = 0; i < groupData.getGroupMembers().size(); i++) {
            usersRef.child(groupData.getGroupInfo().getRoomId())
                    .child("GroupMembers").child("User" + (i + 1))
                    .setValue(groupData.getGroupMembers().get(i));
        }

        DatabaseReference postsRef = database.getReference("GroupPosts");
        for (int i = 0; i < chatDataList.size(); i++) {
            postsRef.child(groupData.getGroupInfo().getRoomId()).push().setValue(chatDataList.get(i));
        }
        usersRef.child(nodeName).removeValue();
        postsRef.child(nodeName).removeValue();

        updateDataInLocalLists(nodeName);
        finish();
    }

    private String createNodeName() {
        String node = "";
        ArrayList<String> nameList = new ArrayList<String>();
        for (int i = 0; i < groupData.getGroupMembers().size(); i++) {
            nameList.add(groupData.getGroupMembers().get(i).getUsername());
        }
        Collections.sort(nameList);
        for (int i = 0; i < nameList.size(); i++) {
            if (i == nameList.size() - 1) {
                node = node + nameList.get(i);
            } else {
                node = node + nameList.get(i) + "_";
            }
        }
        return node;
    }
}