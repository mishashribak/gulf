package com.app.khaleeji.Activity;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;
import Constants.SessionData;
import Model.ChatUserData;
import Model.GroupData;
import Model.GroupInfo;
import Model.LastMessage;
import Model.UserData;
import Utility.SavePref;


public class GulflinkApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private DatabaseReference myRef1, databaseGroupReference;
    private String roomId, tempUserName="";
    public List<String> roomIdList = new ArrayList<>();
    public List<ChatUserData> chatUserDataList = new ArrayList<>();
    public List<GroupData> groupChatUserDataList = new ArrayList<>();
    FirebaseDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Application", "onCreate");
        database = FirebaseDatabase.getInstance();
        myRef1 = database.getReference("chatUser");
        databaseGroupReference = database.getReference("GroupChatUsers");
        registerActivityLifecycleCallbacks(this);
        SessionData.I().init(getApplicationContext());
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if(activity instanceof  ChatActivity){
            Log.e("Application", "onActivityResumed");
            roomIdList.clear();
            chatUserDataList.clear();
            groupChatUserDataList.clear();
            databaseGroupReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (SavePref.getInstance(getApplicationContext()).getUserdetail() != null) {
                        tempUserName = SavePref.getInstance(getApplicationContext()).getUserdetail().getUsername();
                        GroupData groupData = new GroupData();
                        GroupInfo groupInfo = dataSnapshot.child("GroupInfo").getValue(GroupInfo.class);
                        if (groupInfo != null && groupInfo.getRoomId() != null && groupInfo.getRoomId().contains(""+
                                SavePref.getInstance(getApplicationContext()).getUserdetail().getId())) {
                            LastMessage lastMessage = dataSnapshot.child("lastMsg").getValue(LastMessage.class);
                            List<UserData> groupMembers = new ArrayList<>();

                            for (int i = 0; i < dataSnapshot.child("GroupMembers").getChildrenCount(); i++) {
                                UserData userData = dataSnapshot.child("GroupMembers")
                                        .child("User" + (i + 1)).getValue(UserData.class);
                                if (userData.getUsername().equals(SavePref.getInstance(getApplicationContext())
                                        .getUserdetail().getUsername())) {
                                    userData.setIsOnline("1");
                                    userData.setIsOnChatScreen("1");
                                    databaseGroupReference.child(groupInfo.getRoomId()).child("GroupMembers").child("User" + (i + 1)).setValue(userData);
                                }
                                groupMembers.add(userData);
                            }
                            groupData.setGroupInfo(groupInfo);
                            groupData.setGroupMembers(groupMembers);
                            groupData.setLastMsg(lastMessage);
                            groupChatUserDataList.add(groupData);
                        }
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    GroupInfo groupInfo = dataSnapshot.child("GroupInfo").getValue(GroupInfo.class);
                    if(groupInfo == null)
                        return;
                    for (int i = 0; i < groupChatUserDataList.size(); i++) {
                        if (groupChatUserDataList.get(i).getGroupInfo().getRoomId().equals(groupInfo.getRoomId())) {
                            groupChatUserDataList.remove(i);
                            break;
                        }
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            myRef1.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (SavePref.getInstance(getApplicationContext()).getUserdetail() != null) {
                        tempUserName = SavePref.getInstance(getApplicationContext()).getUserdetail().getUsername();
                        UserData user1 = dataSnapshot.child("User1").getValue(UserData.class);
                        UserData user2 = dataSnapshot.child("User2").getValue(UserData.class);
                        if(user1 != null && user2 != null && dataSnapshot.getKey().contains(user1.getUserId())){
                            LastMessage lastMessage = dataSnapshot.child("lastMsg").getValue(LastMessage.class);
                            roomId = dataSnapshot.child("roomId").getValue(String.class);
                            ChatUserData chatUserData = new ChatUserData();
                            chatUserData.setUser1(user1);
                            chatUserData.setUser2(user2);
                            chatUserData.setLastMessage(lastMessage);
                            chatUserData.setRoomId(roomId);
                            if (chatUserData == null) {
                                return;
                            }
                            if (user1.getUsername().equals(SavePref.getInstance(getApplicationContext())
                                    .getUserdetail().getUsername())) {
                                user1.setIsOnline("1");

                                user1.setIsOnChatScreen("1");


                                if (roomId != null) {
                                    myRef1.child(roomId).child("User1").setValue(user1);
                                    myRef1.child(roomId).child("User2").setValue(user2);
                                }
                            } else if (user2.getUsername().equals(SavePref.getInstance(getApplicationContext())
                                    .getUserdetail().getUsername())) {
                                user2.setIsOnline("1");
                                user2.setIsOnChatScreen("1");
                                if (roomId != null) {
                                    myRef1.child(roomId).child("User1").setValue(user1);
                                    myRef1.child(roomId).child("User2").setValue(user2);
                                }
                            }

                            chatUserDataList.add(chatUserData);
                            SessionData.I().setChatUserList(chatUserDataList);
                            roomIdList.add(roomId);
                        }
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    String roomId = dataSnapshot.child("roomId").getValue(String.class);
                    for (int i = 0; i < chatUserDataList.size(); i++) {
                        if (chatUserDataList.get(i).getRoomId().equals(roomId)) {
                            chatUserDataList.remove(i);
                            break;
                        }
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

//    private ChatUserData getChatUserData(GroupInfo groupInfo, LastMessage lastMsg) {
//        ChatUserData chatUserData = new ChatUserData();
//        chatUserData.setRoomId(groupInfo.getRoomId());
//        chatUserData.setFromGroup(true);
//        chatUserData.getUser1().setUsername(groupInfo.getGroupName());
//        chatUserData.getUser1().setProfilePic(groupInfo.getGroupImage());
//        chatUserData.getUser2().setUsername(groupInfo.getGroupName());
//        chatUserData.getUser2().setProfilePic(groupInfo.getGroupImage());
//        chatUserData.setLastMessage(lastMsg);
//        return chatUserData;
//    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.e("Application", "onActivityPaused");
//        SessionData.I().saveLocalData();
        if (SavePref.getInstance(getApplicationContext()).getUserdetail() == null && !tempUserName.isEmpty()) {
            for (int i = 0; i < chatUserDataList.size(); i++) {
                if (chatUserDataList.get(i).getUser1().getUsername()
                        .equals(tempUserName)) {
                    myRef1.child(roomIdList.get(i)).child("User1").child("isOnline").setValue("0");
                    myRef1.child(roomIdList.get(i)).child("User1").child("isOnChatScreen").setValue("0");
                    break;
                } else if (chatUserDataList.get(i).getUser2().getUsername()
                        .equals(tempUserName)) {
                    myRef1.child(roomIdList.get(i)).child("User2").child("isOnline").setValue("0");
                    myRef1.child(roomIdList.get(i)).child("User2").child("isOnChatScreen").setValue("0");
                    break;
                }
            }

            for(int i= 0 ; i< groupChatUserDataList.size(); i++){
                for(int j=0; j < groupChatUserDataList.get(i).getGroupMembers().size(); j++){
                    if (groupChatUserDataList.get(i).getGroupMembers().get(j).getUsername()
                            .equals(tempUserName)) {
                        databaseGroupReference.child(groupChatUserDataList.get(i).getGroupInfo().getRoomId()).child("GroupMembers")
                                .child("User"+(j+1)).child("isOnline").setValue("0");
                        databaseGroupReference.child(groupChatUserDataList.get(i).getGroupInfo().getRoomId()).child("GroupMembers")
                                .child("User"+(j+1)).child("isOnChatScreen").setValue("0");
                        break;
                    }
                }
            }
            tempUserName = "";
            groupChatUserDataList.clear();
            chatUserDataList.clear();
            return;
        }
        for (int i = 0; i < chatUserDataList.size(); i++) {
            if (chatUserDataList.get(i).getUser1().getUsername()
                    .equals(SavePref.getInstance(getApplicationContext()).getUserdetail().getUsername())) {
                myRef1.child(roomIdList.get(i)).child("User1").child("isOnline").setValue("0");
                myRef1.child(roomIdList.get(i)).child("User1").child("isOnChatScreen").setValue("0");
            } else if (chatUserDataList.get(i).getUser2().getUsername()
                    .equals(SavePref.getInstance(getApplicationContext()).getUserdetail().getUsername())) {
                myRef1.child(roomIdList.get(i)).child("User2").child("isOnline").setValue("0");
                myRef1.child(roomIdList.get(i)).child("User2").child("isOnChatScreen").setValue("0");
            }
        }
        for(int i= 0 ; i< groupChatUserDataList.size(); i++){
            for(int j=0; j < groupChatUserDataList.get(i).getGroupMembers().size(); j++){
                if (groupChatUserDataList.get(i).getGroupMembers().get(j).getUsername()
                        .equals(SavePref.getInstance(getApplicationContext()).getUserdetail().getUsername())) {
                    databaseGroupReference.child(groupChatUserDataList.get(i).getGroupInfo().getRoomId()).child("GroupMembers")
                            .child("User"+(j+1)).child("isOnline").setValue("0");
                    databaseGroupReference.child(groupChatUserDataList.get(i).getGroupInfo().getRoomId()).child("GroupMembers")
                            .child("User"+(j+1)).child("isOnChatScreen").setValue("0");
                    break;
                }
            }
        }
        groupChatUserDataList.clear();
        chatUserDataList.clear();
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.e("Application", "onActivityStopped");

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }


}
