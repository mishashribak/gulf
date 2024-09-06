package com.app.khaleeji.Response.friendsNotInGroupPackage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetFriendsNotInGroupResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private GetFriendsNotInGroupData getFriendsNotInGroupData;
    @SerializedName("message_original")
    @Expose
    private String messageOriginal;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public GetFriendsNotInGroupData getGetFriendsNotInGroupData() {
        return getFriendsNotInGroupData;
    }

    public void setGetFriendsNotInGroupData(GetFriendsNotInGroupData getFriendsNotInGroupData) {
        this.getFriendsNotInGroupData = getFriendsNotInGroupData;
    }

    public String getMessageOriginal() {
        return messageOriginal;
    }

    public void setMessageOriginal(String messageOriginal) {
        this.messageOriginal = messageOriginal;
    }

}
