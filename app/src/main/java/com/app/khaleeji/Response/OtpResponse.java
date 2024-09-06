package com.app.khaleeji.Response;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OtpResponse {
    @SerializedName("user_details")
    @Expose
    private UserDetails userDetails;
    @SerializedName("meetup_details")
    @Expose
    private MeetupDetails meetupDetails;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("is_active")
    @Expose
    private Integer is_active;

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
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


    public MeetupDetails getMeetupDetails() {
        return meetupDetails;
    }

    public void setMeetupDetails(MeetupDetails meetupDetails) {
        this.meetupDetails = meetupDetails;
    }

    public Integer getIs_active(){
        return is_active;
    }

    public void setIs_active(Integer is_active) {
        this.is_active = is_active;
    }
}





