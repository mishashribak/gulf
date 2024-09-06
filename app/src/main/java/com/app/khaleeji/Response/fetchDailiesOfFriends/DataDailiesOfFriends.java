package com.app.khaleeji.Response.fetchDailiesOfFriends;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.app.khaleeji.Response.Basic_Response;

import java.util.List;


public class DataDailiesOfFriends {

    @SerializedName("friendList")
    @Expose
    private List<FriendListDailiesOfFriends> friendList = null;

    @SerializedName("myList")
    @Expose
    private FriendListDailiesOfFriends mylist;


    public List<FriendListDailiesOfFriends> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<FriendListDailiesOfFriends> friendList) {
        this.friendList = friendList;
    }

    public FriendListDailiesOfFriends getMylist() {
        return mylist;
    }

    public void setMylist(FriendListDailiesOfFriends mylist) {
        this.mylist = mylist;
    }
}
