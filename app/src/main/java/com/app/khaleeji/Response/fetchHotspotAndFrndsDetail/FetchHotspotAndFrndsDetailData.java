package com.app.khaleeji.Response.fetchHotspotAndFrndsDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import Model.MyGroupData;

/**
 * Created by Dcube on 06-10-2018.
 */

public class FetchHotspotAndFrndsDetailData {


//    @SerializedName("friends")
//    @Expose
//    private Friends friends;
//    @SerializedName("recent_friends")
//    @Expose
//    private RecentFriends recentFriends;
    @SerializedName("hotspot_details")
    @Expose
    private HotspotDetails hotspotDetails;

  @SerializedName("my_groups")
    @Expose
    private List<MyGroupData> myGroups= new ArrayList<>();

    public List<MyGroupData> getMyGroups() {
        return myGroups;
    }

    public void setMyGroups(List<MyGroupData> myGroups) {
        this.myGroups = myGroups;
    }

//    public Friends getFriends() {
//        return friends;
//    }
//
//    public void setFriends(Friends friends) {
//        this.friends = friends;
//    }

//    public RecentFriends getRecentFriends() {
//        return recentFriends;
//    }
//
//    public void setRecentFriends(RecentFriends recentFriends) {
//        this.recentFriends = recentFriends;
//    }

    public HotspotDetails getHotspotDetails() {
        return hotspotDetails;
    }

    public void setHotspotDetails(HotspotDetails hotspotDetails) {
        this.hotspotDetails = hotspotDetails;
    }
}
