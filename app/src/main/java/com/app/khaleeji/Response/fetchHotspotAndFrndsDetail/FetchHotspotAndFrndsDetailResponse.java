package com.app.khaleeji.Response.fetchHotspotAndFrndsDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Dcube on 06-10-2018.
 */

public class FetchHotspotAndFrndsDetailResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private FetchHotspotAndFrndsDetailData data;
    @SerializedName("message_original")
    @Expose
    private String messageOriginal;
    @SerializedName("message")
    @Expose
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public FetchHotspotAndFrndsDetailData getData() {
        return data;
    }

    public void setData(FetchHotspotAndFrndsDetailData data) {
        this.data = data;
    }


    public String getMessageOriginal() {
        return messageOriginal;
    }

    public void setMessageOriginal(String messageOriginal) {
        this.messageOriginal = messageOriginal;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


//    @SerializedName("status")
//    @Expose
//    private String status;
//    @SerializedName("data")
//    @Expose
//    private FetchHotspotAndFrndsDetailData data;
//    @SerializedName("message_original")
//    @Expose
//    private String messageOriginal;
//    @SerializedName("message")
//    @Expose
//    private String message;
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public FetchHotspotAndFrndsDetailData getData() {
//        return data;
//    }
//
//    public void setData(FetchHotspotAndFrndsDetailData data) {
//        this.data = data;
//    }
//
//    public String getMessageOriginal() {
//        return messageOriginal;
//    }
//
//    public void setMessageOriginal(String messageOriginal) {
//        this.messageOriginal = messageOriginal;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }

}
