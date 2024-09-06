package com.app.khaleeji.Response.fetchFriendsDailies;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Dcube on 20-07-2018.
 */

public class FriendList {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("country_id")
    @Expose
    private String countryId;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("height")
    @Expose
    private String height;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("high_school")
    @Expose
    private String highSchool;
    @SerializedName("university")
    @Expose
    private String university;
    @SerializedName("company_name")
    @Expose
    private String companyName;
    @SerializedName("bio")
    @Expose
    private String bio;
    @SerializedName("privacy")
    @Expose
    private String privacy;
    @SerializedName("profile_picture")
    @Expose
    private String profilePicture;
    @SerializedName("profile_picture_thumb")
    @Expose
    private String profilePictureThumb;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("question")
    @Expose
    private String question;
    @SerializedName("message_me")
    @Expose
    private Integer messageMe;
    @SerializedName("all_other_to_seeprofile")
    @Expose
    private Integer allOtherToSeeprofile;
    @SerializedName("all_other_to_seeDOB")
    @Expose
    private Integer allOtherToSeeDOB;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    @SerializedName("media")
    @Expose
    private List<Medium> media = null;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("full_name")
    @Expose
    private String full_name;

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getHighSchool() {
        return highSchool;
    }

    public void setHighSchool(String highSchool) {
        this.highSchool = highSchool;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getProfilePictureThumb() {
        return profilePictureThumb;
    }

    public void setProfilePictureThumb(String profilePictureThumb) {
        this.profilePictureThumb = profilePictureThumb;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getMessageMe() {
        return messageMe;
    }

    public void setMessageMe(Integer messageMe) {
        this.messageMe = messageMe;
    }

    public Integer getAllOtherToSeeprofile() {
        return allOtherToSeeprofile;
    }

    public void setAllOtherToSeeprofile(Integer allOtherToSeeprofile) {
        this.allOtherToSeeprofile = allOtherToSeeprofile;
    }

    public Integer getAllOtherToSeeDOB() {
        return allOtherToSeeDOB;
    }

    public void setAllOtherToSeeDOB(Integer allOtherToSeeDOB) {
        this.allOtherToSeeDOB = allOtherToSeeDOB;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Medium> getMedia() {
        return media;
    }

    public void setMedia(List<Medium> media) {
        this.media = media;
    }
}
