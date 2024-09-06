package com.app.khaleeji.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.app.khaleeji.Response.fetchDailiesOfFriends.FriendListDailiesOfFriends;

import java.util.List;

public class MemoryUpdateResponse {
    @SerializedName("memories")
    @Expose
    private MemoryUpdate data;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("dailies")
    @Expose
    private List<FriendListDailiesOfFriends> daily;


    public List<FriendListDailiesOfFriends> getDaily() {
        return daily;
    }

    public void setDaily(List<FriendListDailiesOfFriends> daily) {
        this.daily = daily;
    }

    public MemoryUpdate getData() {
        return data;
    }

    public void setData(MemoryUpdate data) {
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
