package com.app.khaleeji.Response.fetchHotspotAndFrndsDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HotspotDetailData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("is_admin")
    @Expose
    private Integer isAdmin;
    @SerializedName("category_id")
    @Expose
    private Integer categoryId;
    @SerializedName("location_name")
    @Expose
    private String locationName;
    @SerializedName("about")
    @Expose
    private String about;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("pin_icon")
    @Expose
    private String pinIcon;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("place_id")
    @Expose
    private String placeId;
    @SerializedName("is_active")
    @Expose
    private Integer isActive;
    @SerializedName("country")
    @Expose
    private Object country;
    @SerializedName("city")
    @Expose
    private Object city;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;


    private  boolean isChecked = false;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Integer isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPinIcon() {
        return pinIcon;
    }

    public void setPinIcon(String pinIcon) {
        this.pinIcon = pinIcon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Object getCountry() {
        return country;
    }

    public void setCountry(Object country) {
        this.country = country;
    }

    public Object getCity() {
        return city;
    }

    public void setCity(Object city) {
        this.city = city;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}
