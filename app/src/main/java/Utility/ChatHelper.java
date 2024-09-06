package Utility;

import android.content.Context;
import android.content.Intent;

import com.app.khaleeji.Response.fetchDailiesOfFriends.FriendListDailiesOfFriends;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.app.khaleeji.Activity.ChatActivity;
import com.app.khaleeji.Activity.GulflinkApplication;
import com.app.khaleeji.Response.FriendData;
import com.app.khaleeji.Response.GulfProfiledata;
import com.app.khaleeji.Response.UserDetails;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import Model.ChatData;
import Model.LastMessage;
import Model.UserData;

public class ChatHelper {
    private DatabaseReference myRef1, databaseGroupReference;
    private Context mContext;
    private FirebaseDatabase database;
    private DatabaseReference postsRef;
    private GulflinkApplication gulflinkApplication;

    public ChatHelper(Context context){
        mContext = context;
        database = FirebaseDatabase.getInstance();
        myRef1 = database.getReference("chatUser");
        databaseGroupReference = database.getReference("GroupChatUsers");
        postsRef = database.getReference("GroupPosts");
        gulflinkApplication = (GulflinkApplication) mContext.getApplicationContext();
    }

    public String createRoomId( UserDetails user1, FriendListDailiesOfFriends user2, boolean isFromChat){
        String roomId;
        String[] userArray = new String[2] ;
        userArray[0] = ""+user1.getId();
        userArray[1] = ""+user2.getId();
        Arrays.sort(userArray);
        roomId = userArray[0]+"_"+userArray[1];

        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        myRef1.child(roomId).child("createdAt").setValue(ts);
        myRef1.child(roomId).child("roomId").setValue(roomId);
        myRef1.child(roomId).child("User1").setValue(getUserData(user1, "1"));
        myRef1.child(roomId).child("User2").setValue(getUserData(user2, "0", isFromChat));
        return roomId;
    }

    public String createRoomId( UserDetails user1, GulfProfiledata user2){
        String roomId;
        String[] userArray = new String[2] ;
        userArray[0] = ""+user1.getId();
        userArray[1] = ""+user2.getId();
        Arrays.sort(userArray);
        roomId = userArray[0]+"_"+userArray[1];

        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        myRef1.child(roomId).child("createdAt").setValue(ts);
        myRef1.child(roomId).child("roomId").setValue(roomId);
        myRef1.child(roomId).child("User1").setValue(getUserData(user1, "1"));
        myRef1.child(roomId).child("User2").setValue(getUserData(user2, "0"));
        return roomId;
    }

    public String createGroupRoomId(UserDetails[] groupUsers, String groupName, String groupImage){

        String groupRoomId = createRoomName(groupUsers);
        databaseGroupReference.child(groupRoomId).child("GroupInfo").child("roomId").setValue(groupRoomId);
        databaseGroupReference.child(groupRoomId).child("GroupInfo").child("groupName").setValue(groupName);
        databaseGroupReference.child(groupRoomId).child("GroupInfo").child("groupImage").setValue(groupImage);

        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        databaseGroupReference.child(groupRoomId).child("createdAt").setValue(ts);

        int defltValue = 1;
        for ( UserDetails userdetail : groupUsers) {
            databaseGroupReference.child(groupRoomId).child("GroupMembers").child("User"+defltValue).setValue(getUserData(userdetail, "0"));
            defltValue += 1;
        }

        return groupRoomId;
    }

    public String createGroupRoomId(List<FriendData> groupUsers, String groupName, String groupImage){

        UserDetails userDetails = SavePref.getInstance(mContext).getUserdetail();
        FriendData myData = new FriendData();
        myData.setId(userDetails.getId());
        myData.setUsername(userDetails.getUsername());
        myData.setProfile_picture(userDetails.getProfilePicture());
        groupUsers.add(0,myData);

        String groupRoomId = createRoomName(groupUsers, groupName);
        databaseGroupReference.child(groupRoomId).child("GroupInfo").child("roomId").setValue(groupRoomId);
        databaseGroupReference.child(groupRoomId).child("GroupInfo").child("groupName").setValue(groupName);
        databaseGroupReference.child(groupRoomId).child("GroupInfo").child("groupImage").setValue(groupImage);

        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        databaseGroupReference.child(groupRoomId).child("GroupInfo").child("createdAt").setValue(ts);

        int defltValue = 1;
        for ( FriendData userdetail : groupUsers) {
            if(userdetail.getUsername().equals(userDetails.getUsername())){
                databaseGroupReference.child(groupRoomId).child("GroupMembers").child("User"+defltValue).setValue(getUserData(userdetail, "1"));
            }else{
                databaseGroupReference.child(groupRoomId).child("GroupMembers").child("User"+defltValue).setValue(getUserData(userdetail, "0"));
            }
            defltValue += 1;
        }

        return groupRoomId;
    }

    public void updateGroupRoomId(String oldRoomId, List<FriendData> groupUsers, String groupName,
                                  String groupImage, LastMessage oldLastMessage, List<ChatData> chatDataList){

        UserDetails userDetails = SavePref.getInstance(mContext).getUserdetail();
//        FriendData myData = new FriendData();
//        myData.setId(userDetails.getId());
//        myData.setUsername(userDetails.getUsername());
//        myData.setProfile_picture(userDetails.getProfilePicture());
//        groupUsers.add(0,myData);

        String groupRoomId = createRoomName(groupUsers, groupName);

        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        databaseGroupReference.child(groupRoomId).child("GroupInfo").child("createdAt").setValue(ts);
        databaseGroupReference.child(groupRoomId).child("GroupInfo").child("roomId").setValue(groupRoomId);
        databaseGroupReference.child(groupRoomId).child("GroupInfo").child("groupName").setValue(groupName);
        databaseGroupReference.child(groupRoomId).child("GroupInfo").child("groupImage").setValue(groupImage);

        int defltValue = 1;
        boolean isDeletedMe = true;
        for ( FriendData userdetail : groupUsers) {
            if(userdetail.getUsername().equals(userDetails.getUsername())){
                isDeletedMe = false;
                databaseGroupReference.child(groupRoomId).child("GroupMembers").child("User"+defltValue).setValue(getUserData(userdetail, "1"));
            }else{
                databaseGroupReference.child(groupRoomId).child("GroupMembers").child("User"+defltValue).setValue(getUserData(userdetail, "0"));
            }
            defltValue += 1;
        }

        //delete
        if (!oldRoomId.equals(groupRoomId)){
            databaseGroupReference.child(oldRoomId).removeValue();
            database.getReference("GroupPosts").child(oldRoomId).removeValue();

            updateDataInLocalLists(oldRoomId);
        }

        databaseGroupReference.child(groupRoomId).child("lastMsg").setValue(oldLastMessage);
        postsRef = database.getReference("GroupPosts");
        for (int i = 0; i < chatDataList.size(); i++) {
            postsRef.child(groupRoomId).push().setValue(chatDataList.get(i));
        }

        if(!isDeletedMe){
            Intent intent = new Intent(mContext, ChatActivity.class);
            intent.putExtra("isFromGroup", true);
            intent.putExtra("groupName", groupName);
            intent.putExtra("groupImage", groupImage);
            intent.putExtra("nodeName", groupRoomId);
            mContext.startActivity(intent);
        }
    }

    private void updateDataInLocalLists(String nodeName) {

        for (int i = 0; i < gulflinkApplication.groupChatUserDataList.size(); i++) {
            if (gulflinkApplication.groupChatUserDataList.get(i).getGroupInfo().getRoomId().equals(nodeName)) {
                gulflinkApplication.groupChatUserDataList.remove(i);
                break;
            }
        }
    }

    private String createRoomName(UserDetails[] groupData) {
        String node = "";
        ArrayList<String> nameList = new ArrayList<String>();
        for (int i = 0; i < groupData.length; i++) {
            nameList.add(groupData[i].getUsername());
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

    private String createRoomName(List<FriendData> groupData, String groupName) {
        String node = "";
        ArrayList<String> nameList = new ArrayList<String>();
        for (int i = 0; i < groupData.size(); i++) {
            nameList.add(String.valueOf(groupData.get(i).getId()));
        }
        nameList.add(groupName);
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

    private UserData getUserData(UserDetails userdetail, String isOnline) {
        UserData data = new UserData();
        data.setEmail("");
        data.setProfilePic(ApiClass.ImageBaseUrl + userdetail.getProfilePicture());
        data.setUsername(userdetail.getUsername());
        data.setUserId(""+userdetail.getId());
        data.setDeviceToken(userdetail.getNotiToken());
        data.setIsAdmin("0");
        data.setIsOnline(isOnline);
        data.setIsOnChatScreen("0");
        return data;
    }


    private UserData getUserData(FriendListDailiesOfFriends userdetail, String isOnline, boolean isFromChat) {
        UserData data = new UserData();
        data.setEmail("");
        if(isFromChat){
            data.setProfilePic(userdetail.getProfilePicture());
        }else{
            data.setProfilePic(ApiClass.ImageBaseUrl + userdetail.getMedia().get(0).profilePicture);
        }

        data.setUsername(userdetail.getUsername());
        data.setUserId(""+userdetail.getId());
        data.setDeviceToken("");
        data.setIsAdmin(isOnline);
        data.setIsOnline(isOnline);
        data.setIsOnChatScreen("0");
        return data;
    }

    private UserData getUserData(FriendData userdetail, String isOnline) {
        UserData data = new UserData();
        data.setEmail("");
        data.setProfilePic(ApiClass.ImageBaseUrl + userdetail.getProfile_picture());
        data.setUsername(userdetail.getUsername());
        data.setUserId(""+userdetail.getId());
        data.setDeviceToken("");
        data.setIsAdmin(isOnline);
        data.setIsOnline(isOnline);
        data.setIsOnChatScreen("0");
        return data;
    }

    private UserData getUserData(GulfProfiledata userdetail, String isOnline) {
        UserData data = new UserData();
        data.setEmail("");
        data.setProfilePic(ApiClass.ImageBaseUrl + userdetail.getProfilePicture());
        data.setUsername(userdetail.getUsername());
        data.setUserId(""+userdetail.getId());
        data.setDeviceToken(userdetail.getDeviceToken());
        data.setIsAdmin(isOnline);
        data.setIsOnline(isOnline);
        data.setIsOnChatScreen("0");
        return data;
    }
}
