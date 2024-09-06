package com.app.khaleeji.Response.friendsNotInGroupPackage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Dcube on 21-11-2018.
 */

public class GetFriendsNotInGroupData {

    @SerializedName("user")
    @Expose
    private List<GetFriendsNotInGroupUserData> getFriendsNotInGroupUserDataList = null;

    public List<GetFriendsNotInGroupUserData> getGetFriendsNotInGroupUserDataList() {
        return getFriendsNotInGroupUserDataList;
    }

    public void setGetFriendsNotInGroupUserDataList(List<GetFriendsNotInGroupUserData> getFriendsNotInGroupUserDataList) {
        this.getFriendsNotInGroupUserDataList = getFriendsNotInGroupUserDataList;
    }
}
