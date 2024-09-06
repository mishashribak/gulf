
package com.app.khaleeji.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Notification_Datum {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("media_id")
    @Expose
    private Integer media_id;

    @SerializedName("type")
    @Expose
    private Integer type;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("full_name")
    @Expose
    private String full_name;

    @SerializedName("profile_picture")
    @Expose
    private String profile_picture;

    @SerializedName("from_user")
    @Expose
    private Integer fromUserId;

    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @SerializedName("ago")
    @Expose
    private String ago;

    @SerializedName("agoArabic")
    @Expose
    private String agoArabic;

    @SerializedName("message")
    @Expose
    private String msg;


    @SerializedName("arabicMessage")
    @Expose
    private String arabicMessage;


    public Integer getMedia_id() {
        return media_id;
    }

    public void setMedia_id(Integer media_id) {
        this.media_id = media_id;
    }

    public String getAgoArabic() {
        return agoArabic;
    }

    public void setAgoArabic(String agoArabic) {
        this.agoArabic = agoArabic;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getAgo() {
        return ago;
    }

    public void setAgo(String ago) {
        this.ago = ago;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public Integer getType(){
        return type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public Integer getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Integer fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getArabicMessage() {
        return arabicMessage;
    }

    public void setArabicMessage(String arabicMessage) {
        this.arabicMessage = arabicMessage;
    }
}
