
package com.app.khaleeji.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GulfProfileMetUp implements Serializable {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data = new Data();

    @SerializedName("message_original")
    @Expose
    private String messageOriginal;


    public String getRequestaccept() {
        return requestaccept;
    }

    public void setRequestaccept(String requestaccept) {
        this.requestaccept = requestaccept;
    }

    @SerializedName("request_accept")
    @Expose
    private String requestaccept;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @SerializedName("type")
    @Expose
    private String type ="";



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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessageOriginal() {
        return messageOriginal;
    }

    public void setMessageOriginal(String messageOriginal) {
        this.messageOriginal = messageOriginal;
    }

}
