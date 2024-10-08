package com.app.khaleeji.Response.Search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class SearchFriendsModel {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private SearchUserModel data;

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

    public SearchUserModel getData(){
        return  data;
    }

    public void setData(SearchUserModel data){
        this.data = data;
    }

}
