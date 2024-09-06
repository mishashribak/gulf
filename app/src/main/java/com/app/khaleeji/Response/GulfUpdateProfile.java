package com.app.khaleeji.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GulfUpdateProfile
{
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("file")
    @Expose
    private String file;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("user_details")
    @Expose
    private UserDetails userDetails;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }
}
