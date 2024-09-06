package com.app.khaleeji.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HotspotUpdate {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("created_at")
    @Expose
    private String  created_at;

    @SerializedName("location_name")
    @Expose
    private String locationName;

    @SerializedName("hotspot_id")
    @Expose
    private Integer hotspotId;

    @SerializedName("user_id")
    @Expose
    private Integer user_id;

    @SerializedName("full_name")
    @Expose
    private String full_name;

    @SerializedName("profile_picture")
    @Expose
    private String profile_picture;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("agoArabic")
    @Expose
    private String  agoArabic;

    @SerializedName("ago")
    @Expose
    private String  ago;

    @SerializedName("type")
    @Expose
    private Integer type;

    @SerializedName("status")
    @Expose
    private Integer status;


    public Integer getStatus() {
        return status;
    }


    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer gettype() {
        return type;
    }

    public void settype(Integer type) {
        this.type = type;
    }

    public String getfull_name() {
        return full_name;
    }

    public String getusername() {
        return username;
    }

    public void setfull_name(String full_name) {
        this.full_name = full_name;
    }

    public void setusername(String username) {
        this.username = username;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getagoArabic() {
        return agoArabic;
    }

    public void setagoArabic(String agoArabic) {
        this.agoArabic = agoArabic;
    }

    public String getago() {
        return ago;
    }

    public void setago(String ago) {
        this.ago = ago;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Integer getuser_id() {
        return user_id;
    }

    public void setuser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }


    public String getprofile_picture() {
        return profile_picture;
    }

    public void setprofile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }


    public Integer gethotspotId() {
        return hotspotId;
    }

    public void sethotspotId(Integer hotspotId) {
        this.hotspotId = hotspotId;
    }
}
