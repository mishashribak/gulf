package com.app.khaleeji.Response.groupListPackage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GroupMemberListResponse {
    @SerializedName("data")
    @Expose
    private List<GroupMemberModel> data;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;

    public List<GroupMemberModel> getData() {
        return data;
    }

    public void setData(List<GroupMemberModel> data) {
        this.data = data;
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
}
