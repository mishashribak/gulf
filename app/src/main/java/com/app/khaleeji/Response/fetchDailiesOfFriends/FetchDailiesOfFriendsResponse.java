package com.app.khaleeji.Response.fetchDailiesOfFriends;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class  FetchDailiesOfFriendsResponse {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("data")
    @Expose
    private DataDailiesOfFriends data;

    @SerializedName("message")
    @Expose
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DataDailiesOfFriends getData() {
        return data;
    }

    public void setData(DataDailiesOfFriends data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
