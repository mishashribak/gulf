package com.app.khaleeji.Response.blockistPackage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Dcube on 19-11-2018.
 */

public class BlockListResponse {

    @SerializedName("data")
    @Expose
    private List<BlockedUserData> blockedUserData = null;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("message_original")
    @Expose
    private String messageOriginal;

    public List<BlockedUserData> getBlockedUserData() {
        return blockedUserData;
    }

    public void setBlockedUserData(List<BlockedUserData> blockedUserData) {
        this.blockedUserData = blockedUserData;
    }

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

    public String getMessageOriginal() {
        return messageOriginal;
    }

    public void setMessageOriginal(String messageOriginal) {
        this.messageOriginal = messageOriginal;
    }



}

