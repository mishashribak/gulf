package com.app.khaleeji.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GulfProfileResponse {

    @SerializedName("notification_count")
    @Expose
    private Integer notificationCount;

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private GulfProfiledata data;

    public Integer getNotificationCount(){return  notificationCount;}

    public void setNotificationCount(Integer notificationCount) {
        this.notificationCount = notificationCount;
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

    public GulfProfiledata getData() {
        return data;
    }

    public void setData(GulfProfiledata data) {
        this.data = data;
    }
}
