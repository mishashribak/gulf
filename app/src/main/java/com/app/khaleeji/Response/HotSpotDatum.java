
package com.app.khaleeji.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class HotSpotDatum implements Serializable{

    @SerializedName("distance")
    @Expose
    private Double distance;

    @SerializedName("distance_unit")
    @Expose
    private String  distance_unit;

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("is_admin")
    @Expose
    private Integer is_admin;

    @SerializedName("category_id")
    @Expose
    private Integer category_id=-1;

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

    @SerializedName("is_own")
    @Expose
    private Boolean isOwn;
    @SerializedName("hotspot_id")
    @Expose
    private Integer hotspotId;

    @SerializedName("is_checkined")
    @Expose
    private boolean isCheckined;

    @SerializedName("is_hotspot")
    @Expose
    private boolean isHotspot;



//    @SerializedName("icon")
//    @Expose
//    private String icon;
//    @SerializedName("vicinity")
//    @Expose
//    private String vicinity;
//    @SerializedName("photos")
//    @Expose
//    private String photos;
//
//    @SerializedName("types")
//    @Expose
//    private String types;
//
//    @SerializedName("category_type")
//    @Expose
//    private String category_type;
//
//    @SerializedName("category_image")
//    @Expose
//    private String category_image;


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

    public Boolean getOwn() {
        return isOwn;
    }

    public void setOwn(Boolean own) {
        isOwn = own;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getDistance_unit() {
        return distance_unit;
    }

    public void setDistance_unit(String distance_unit) {
        this.distance_unit = distance_unit;
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

    public Integer getHotspotId() {
        return hotspotId;
    }

    public void setHotspotId(Integer hotspotId) {
        this.hotspotId = hotspotId;
    }

    public boolean getIsHotspot() {
        return isHotspot;
    }

    public void setIsHotspot(boolean isHotspot) {
        this.isHotspot = isHotspot;
    }

    public boolean getIsCheckined() {
        return isCheckined;
    }

    public void setIsCheckined(boolean isCheckined) {
        this.isCheckined = isCheckined;
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


/*    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }


    public String getCategory_image() {
        return category_image;
    }

    public void setCategory_image(String category_image) {
        this.category_image = category_image;
    }

    public String getCategory_type() {
        return category_type;
    }

    public void setCategory_type(String category_type) {
        this.category_type = category_type;
    }*/

}
