package com.app.khaleeji.Response.fetchHotspotTimeLine;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Dcube on 04-07-2018.
 */

public class FetchHotspotTimelineResponse implements Serializable{

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("medialist")
    @Expose
    private HotspotMedialist medialist;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("message_original")
    @Expose
    private String messageOriginal;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public HotspotMedialist getMedialist() {
        return medialist;
    }

    public void setMedialist(HotspotMedialist medialist) {
        this.medialist = medialist;
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
