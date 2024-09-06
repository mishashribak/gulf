
package com.app.khaleeji.Response;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NearByResponse {

    @SerializedName("data")
    @Expose
    private NearByUserModel data;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
//    @SerializedName("message_original")
//    @Expose
//    private String messageOriginal;

    public NearByUserModel getData() {
        return data;
    }

    public void setData(NearByUserModel data) {
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

//    public String getMessageOriginal() {
//        return messageOriginal;
//    }
//
//    public void setMessageOriginal(String messageOriginal) {
//        this.messageOriginal = messageOriginal;
//    }

}
