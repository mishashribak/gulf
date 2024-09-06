package com.app.khaleeji.BroadCast;

import android.os.Bundle;

import com.app.khaleeji.Response.FriendData;
import com.app.khaleeji.Response.GulfProfiledata;
import com.app.khaleeji.Response.HotSpotDatum;
import com.app.khaleeji.Response.MediaModel;
import com.app.khaleeji.Response.MemoryModel;
import com.app.khaleeji.Response.fetchDailiesOfFriends.FriendListDailiesOfFriends;
import com.app.khaleeji.Response.fetchMemoryPackage.Memory;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import Model.GroupInfo;
import Model.UploadInfoModel;

public class MessageEvent {

    private List<MemoryModel> memoryModelList;
    private MemoryModel memoryModel;
    private List<HotSpotDatum> mListHotspotData;
    private GulfProfiledata gulfProfileData;
    private MessageType type;
    private  List<FriendListDailiesOfFriends> friendLists;
    private ArrayList<FriendData> friendData;
    private int pageNo;
    private Bundle bundle;
    private String savedUrl;
    private GroupInfo groupInfo;
    public String uploadId;
    public int selectedFriendIndex;

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public List<FriendListDailiesOfFriends> getFriendLists() {
        return friendLists;
    }

    public void setFriendLists(List<FriendListDailiesOfFriends> list, int selectedIndex) {
        this.friendLists = list;
        this.pageNo = selectedIndex;
    }

    public GroupInfo getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(GroupInfo groupInfo) {
        this.groupInfo = groupInfo;
    }

    public ArrayList<FriendData> getFriendData() {
        return friendData;
    }

    public void setFriendData(ArrayList<FriendData> friendData) {
        this.friendData = friendData;
    }

    public MemoryModel getMemoryModel() {
        return memoryModel;
    }

    public void setMemoryModel(MemoryModel memoryModel) {
        this.memoryModel = memoryModel;
    }

    public List<MemoryModel> getMemoryModelList() {
        return memoryModelList;
    }

    public void setMemoryModelList(List<MemoryModel> memoryModelList) {
        this.memoryModelList = memoryModelList;
    }

    public GulfProfiledata getGulfProfileData() {
        return gulfProfileData;
    }

    public void setGulfProfileData(GulfProfiledata gulfProfileData) {
        this.gulfProfileData = gulfProfileData;
    }

    public List<HotSpotDatum> getListHotspotData() {
        return mListHotspotData;
    }

    public void setListHotspotData(List<HotSpotDatum> mListHotspotData) {
        this.mListHotspotData = mListHotspotData;
    }

    public String getSavedUrl() {
        return savedUrl;
    }

    public void setSavedUrl(String savedUrl) {
        this.savedUrl = savedUrl;
    }

    public void setPageNo(int pageNo){
        this.pageNo = pageNo;
    }

    public int getPageNo(){
        return pageNo;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public enum MessageType {
        FRIEND_POSTS,
        MY_POSTS,
        MY_DAILIES_VIEW,
        FRIEND_DAILIES_VIEW,
        WHERE_ARE_YOU,
        MY_POST,
        FRIEND_POST,
        VIDEO_PLAY_READY,
        SEND_TO,
        HOTSPOTDETAILS,
        NOTIFICATION,
        REMOVE_MEDIA,
        MSG_VIDEO,
        MSG_IMAGE,
        SENDING_MSG_PROGRESS,
        UPDATE_GROUP,
        CHAT_ROOM_UPDATE,
        NEW_MSG,
        NEW_MSG_NOTIFICATION,
        POST_REFRESH,
        STATUS_REFRESH,
        PROFILE_REFRESH,
        BLOCK,
        UPLOAD_INFO,
        UPLOAD_CANCEL,
        DAILY_REFRESH,
        TOP_SCROLL,
    }

}
