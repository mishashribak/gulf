package com.app.khaleeji.Response;

public class HotspotData {

    private String user_id;
    private String username;
    private String is_checkin;
    private String checkedInTime;
    private String profile_picture;
    private String hotspot_id;
    private String is_admin;
    private String city;
    private String country;
    private String lat;
    private String lng;
    private String rating;
    private String category_id;
    private String category_imagee;
    private String location_name;
    private String place_id;
    private String created_at;
    private String image_hotspot;
    private String media_id;
    private String url;
    private String new_uploaded_timing;
    private String about;
    private String category_image;
    private String checkedTiming;
    private String is_hotspot;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIs_checkin() {
        return is_checkin;
    }

    public void setIs_checkin(String is_checkin) {
        this.is_checkin = is_checkin;
    }

    public String getCheckedInTime() {
        return checkedInTime;
    }

    public void setCheckedInTime(String checkedInTime) {
        this.checkedInTime = checkedInTime;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getHotspot_id() {
        return hotspot_id;
    }

    public void setHotspot_id(String hotspot_id) {
        this.hotspot_id = hotspot_id;
    }

    public String getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(String is_admin) {
        this.is_admin = is_admin;
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

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_imagee() {
        return category_imagee;
    }

    public void setCategory_imagee(String category_imagee) {
        this.category_imagee = category_imagee;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getImage_hotspot() {
        return image_hotspot;
    }

    public void setImage_hotspot(String image_hotspot) {
        this.image_hotspot = image_hotspot;
    }

    public String getMedia_id() {
        return media_id;
    }

    public void setMedia_id(String media_id) {
        this.media_id = media_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNew_uploaded_timing() {
        return new_uploaded_timing;
    }

    public void setNew_uploaded_timing(String new_uploaded_timing) {
        this.new_uploaded_timing = new_uploaded_timing;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getCategory_image() {
        return category_image;
    }

    public void setCategory_image(String category_image) {
        this.category_image = category_image;
    }

    public String getCheckedTiming() {
        return checkedTiming;
    }

    public void setCheckedTiming(String checkedTiming) {
        this.checkedTiming = checkedTiming;
    }

    public String getIs_hotspot() {
        return is_hotspot;
    }

    public void setIs_hotspot(String is_hotspot) {
        this.is_hotspot = is_hotspot;
    }

    public String getHotspotUpdate() {
        String sourceString = "<font color='black'><b>" + getLocation_name() + "</b></font>";
        return getCheckedTiming().replace(getLocation_name(), sourceString);
    }
}
