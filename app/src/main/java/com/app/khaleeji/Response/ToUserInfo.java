
package com.app.khaleeji.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ToUserInfo {

    @SerializedName("message_me")
    @Expose
    private Integer message_me;

    @SerializedName("all_other_to_seeprofile")
    @Expose
    private Integer all_other_to_seeprofile;

    @SerializedName("all_other_to_seeDOB")
    @Expose
    private Integer all_other_to_seeDOB;

    @SerializedName("all_other_see_username")
    @Expose
    private Integer all_other_see_username;

    @SerializedName("visibility")
    @Expose
    private String visibility;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("dob")
    @Expose
    private String dob;

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("country_id")
    @Expose
    private String countryId;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("height")
    @Expose
    private String height;
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
    private String profile_picture_thumb;
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
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    @SerializedName("uid")
    @Expose
    private String uid;


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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
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

    public String getProfile_picture_thumb() {
        return profile_picture_thumb;
    }

    public void setProfile_picture_thumb(String profile_picture_thumb) {
        this.profile_picture_thumb = profile_picture_thumb;
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

    public Integer getMessage_me() {
        return message_me;
    }

    public void setMessage_me(Integer message_me) {
        this.message_me = message_me;
    }

    public Integer getAll_other_to_seeprofile() {
        return all_other_to_seeprofile;
    }

    public void setAll_other_to_seeprofile(Integer all_other_to_seeprofile) {
        this.all_other_to_seeprofile = all_other_to_seeprofile;
    }

    public Integer getAll_other_to_seeDOB() {
        return all_other_to_seeprofile;
    }

    public void setAll_other_to_seeDOB(Integer all_other_to_seeprofile) {
        this.all_other_to_seeprofile = all_other_to_seeprofile;
    }

    public Integer getAll_other_see_username() {
        return all_other_see_username;
    }

    public void setAll_other_see_username(Integer all_other_see_username) {
        this.all_other_see_username = all_other_see_username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
