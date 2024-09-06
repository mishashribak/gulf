
package com.app.khaleeji.Response;

import android.provider.MediaStore;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class HotSpotDetails implements Serializable{

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("is_admin")
    @Expose
    private Integer is_admin;

    @SerializedName("category_id")
    @Expose
    private Integer category_id;

    @SerializedName("lat")
    @Expose
    private String lat;

    @SerializedName("lng")
    @Expose
    private String lng;

    @SerializedName("location_name")
    @Expose
    private String locationName;

    @SerializedName("about")
    @Expose
    private String  about;

    @SerializedName("image")
    @Expose
    private String  image;

    @SerializedName("pin_icon")
    @Expose
    private String  pin_icon;

    @SerializedName("place_id")
    @Expose
    private String placeId;

    @SerializedName("is_active")
    @Expose
    private Integer is_active;

    @SerializedName("country")
    @Expose
    private String country;

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("rating")
    @Expose
    private Double rating;

    @SerializedName("updated_at")
    @Expose
    private String  updated_at;

    @SerializedName("created_at")
    @Expose
    private String  created_at;

    @SerializedName("distance_unit")
    @Expose
    private String  distance_unit;

    @SerializedName("distance")
    @Expose
    private Double distance;


    @SerializedName("memories")
    @Expose
    private List<MemoryModel> data;

    @SerializedName("dailies")
    @Expose
    private List<MediaModel> daily;

    @SerializedName("is_hotspot")
    @Expose
    private boolean isHotSpot;

    @SerializedName("is_checkined")
    @Expose
    private boolean isCheckedIn;

    public List<MediaModel> getDaily() {
        return daily;
    }

    public void setDaily(List<MediaModel> daily) {
        this.daily = daily;
    }

    public boolean isCheckedIn() {
        return isCheckedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        isCheckedIn = checkedIn;
    }

    public boolean isHotSpot() {
        return isHotSpot;
    }

    public void setHotSpot(boolean hotSpot) {
        isHotSpot = hotSpot;
    }

    public String getDistance_unit() {
        return distance_unit;
    }

    public void setDistance_unit(String distance_unit) {
        this.distance_unit = distance_unit;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public List<MemoryModel> getData() {
        return data;
    }

    public void setData(List<MemoryModel> data) {
        this.data = data;
    }

    public Integer getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Integer category_id) {
        this.category_id = category_id;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getPin_icon() {
        return pin_icon;
    }

    public void setPin_icon(String pin_icon) {
        this.pin_icon = pin_icon;
    }

    public Integer getIs_active() {
        return is_active;
    }

    public void setIs_active(Integer is_active) {
        this.is_active = is_active;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Integer getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(Integer is_admin) {
        this.is_admin = is_admin;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
