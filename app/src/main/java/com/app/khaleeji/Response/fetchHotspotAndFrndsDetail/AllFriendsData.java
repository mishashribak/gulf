package com.app.khaleeji.Response.fetchHotspotAndFrndsDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Dcube on 06-10-2018.
 */

public class AllFriendsData {


    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("from_user")
    @Expose
    private Integer fromUser;
    @SerializedName("to_user")
    @Expose
    private Integer toUser;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("answer")
    @Expose
    private String answer;
    @SerializedName("is_blocked_from")
    @Expose
    private Integer isBlockedFrom;
    @SerializedName("is_blocked_to")
    @Expose
    private Integer isBlockedTo;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("friend_name")
    @Expose
    private String friendName;
    @SerializedName("request_accept")
    @Expose
    private String requestAccept;
    @SerializedName("from_user_info")
    @Expose
    private AllFriendsFromUserInfo fromUserInfo;
    @SerializedName("to_user_info")
    @Expose
    private AllFriendsToUserInfo toUserInfo;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFromUser() {
        return fromUser;
    }

    public void setFromUser(Integer fromUser) {
        this.fromUser = fromUser;
    }

    public Integer getToUser() {
        return toUser;
    }

    public void setToUser(Integer toUser) {
        this.toUser = toUser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Integer getIsBlockedFrom() {
        return isBlockedFrom;
    }

    public void setIsBlockedFrom(Integer isBlockedFrom) {
        this.isBlockedFrom = isBlockedFrom;
    }

    public Integer getIsBlockedTo() {
        return isBlockedTo;
    }

    public void setIsBlockedTo(Integer isBlockedTo) {
        this.isBlockedTo = isBlockedTo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getRequestAccept() {
        return requestAccept;
    }

    public void setRequestAccept(String requestAccept) {
        this.requestAccept = requestAccept;
    }

    public AllFriendsFromUserInfo getFromUserInfo() {
        return fromUserInfo;
    }

    public void setFromUserInfo(AllFriendsFromUserInfo fromUserInfo) {
        this.fromUserInfo = fromUserInfo;
    }

    public AllFriendsToUserInfo getToUserInfo() {
        return toUserInfo;
    }

    public void setToUserInfo(AllFriendsToUserInfo toUserInfo) {
        this.toUserInfo = toUserInfo;
    }

//    @SerializedName("full_name")
//    @Expose
//    private String fullName;
//    @SerializedName("id")
//    @Expose
//    private Integer id;
//    @SerializedName("from_user")
//    @Expose
//    private Integer fromUser;
//    @SerializedName("to_user")
//    @Expose
//    private Integer toUser;
//    @SerializedName("status")
//    @Expose
//    private String status;
//    @SerializedName("answer")
//    @Expose
//    private String answer;
//    @SerializedName("is_blocked_from")
//    @Expose
//    private Integer isBlockedFrom;
//    @SerializedName("is_blocked_to")
//    @Expose
//    private Integer isBlockedTo;
//    @SerializedName("created_at")
//    @Expose
//    private String createdAt;
//    @SerializedName("updated_at")
//    @Expose
//    private String updatedAt;
//    @SerializedName("friend_name")
//    @Expose
//    private String friendName;
//    @SerializedName("request_accept")
//    @Expose
//    private String requestAccept;
//    @SerializedName("from_user_info")
//    @Expose
//    private AllFriendsFromUserInfo fromUserInfo;
//    @SerializedName("to_user_info")
//    @Expose
//    private AllFriendsToUserInfo toUserInfo;
//
//    public String getFullName() {
//        return fullName;
//    }
//
//    public void setFullName(String fullName) {
//        this.fullName = fullName;
//    }
//
//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    public Integer getFromUser() {
//        return fromUser;
//    }
//
//    public void setFromUser(Integer fromUser) {
//        this.fromUser = fromUser;
//    }
//
//    public Integer getToUser() {
//        return toUser;
//    }
//
//    public void setToUser(Integer toUser) {
//        this.toUser = toUser;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public String getAnswer() {
//        return answer;
//    }
//
//    public void setAnswer(String answer) {
//        this.answer = answer;
//    }
//
//    public Integer getIsBlockedFrom() {
//        return isBlockedFrom;
//    }
//
//    public void setIsBlockedFrom(Integer isBlockedFrom) {
//        this.isBlockedFrom = isBlockedFrom;
//    }
//
//    public Integer getIsBlockedTo() {
//        return isBlockedTo;
//    }
//
//    public void setIsBlockedTo(Integer isBlockedTo) {
//        this.isBlockedTo = isBlockedTo;
//    }
//
//    public String getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(String createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public String getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public void setUpdatedAt(String updatedAt) {
//        this.updatedAt = updatedAt;
//    }
//
//    public String getFriendName() {
//        return friendName;
//    }
//
//    public void setFriendName(String friendName) {
//        this.friendName = friendName;
//    }
//
//    public String getRequestAccept() {
//        return requestAccept;
//    }
//
//    public void setRequestAccept(String requestAccept) {
//        this.requestAccept = requestAccept;
//    }
//
//    public AllFriendsFromUserInfo getFromUserInfo() {
//        return fromUserInfo;
//    }
//
//    public void setFromUserInfo(AllFriendsFromUserInfo fromUserInfo) {
//        this.fromUserInfo = fromUserInfo;
//    }
//
//    public AllFriendsToUserInfo getToUserInfo() {
//        return toUserInfo;
//    }
//
//    public void setToUserInfo(AllFriendsToUserInfo toUserInfo) {
//        this.toUserInfo = toUserInfo;
//    }


}

