package com.app.khaleeji.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MediaViewsData {

    @SerializedName("full_name")
    @Expose
    private String full_name;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("profile_picture")
    @Expose
    private String profile_picture;

    @SerializedName("created_at")
    @Expose
    private String created_at;

    @SerializedName("agoArabic")
    @Expose
    private String agoArabic;

    @SerializedName("ago")
    @Expose
    private String ago;

    @SerializedName("user_id")
    @Expose
    private Integer user_id;


    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAgo() {
        return ago;
    }

    public void setAgo(String ago) {
        this.ago = ago;
    }

    public String getAgoArabic() {
        return agoArabic;
    }

    public void setAgoArabic(String agoArabic) {
        this.agoArabic = agoArabic;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
