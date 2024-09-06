package com.app.khaleeji.Response.groupListPackage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroupMemberModel {
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("status")
    @Expose
    private Integer status;

    @SerializedName("full_name")
    @Expose
    private String fullName;

    @SerializedName("username")
    @Expose
    private String userName;

    @SerializedName("profile_picture")
    @Expose
    private String profilePicture;

    @SerializedName("dob")
    @Expose
    private String dob;


    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String full_name) {
        this.fullName = full_name;
    }
}
