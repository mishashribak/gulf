package com.app.khaleeji.Response.fetchFriendsDailies;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Dcube on 20-07-2018.
 */

public class Data {

    @SerializedName("friendList")
    @Expose
    private List<FriendList> friendList = null;

    public List<FriendList> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<FriendList> friendList) {
        this.friendList = friendList;
    }
}
