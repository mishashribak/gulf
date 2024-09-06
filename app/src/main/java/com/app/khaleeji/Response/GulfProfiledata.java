package com.app.khaleeji.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GulfProfiledata {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("profile_picture")
    @Expose
    private String profilePicture;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobile_number")
    @Expose
    private String mobileNumber;
    @SerializedName("mobile_verify")
    @Expose
    private Integer mobileVerify;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("country_id")
    @Expose
    private Integer countryId;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("api_token")
    @Expose
    private String token;
    @SerializedName("last_active")
    @Expose
    private String lastActive;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("bg_picture")
    @Expose
    private String bg_picture;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("mobile_change_request")
    @Expose
    private String mobileChagneRequest;
//    @SerializedName("token")
//    @Expose
//    private String token;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("is_superUser")
    @Expose
    private Integer isSuperUser;
    @SerializedName("is_appAdmin")
    @Expose
    private Integer isAppAdmin;
    @SerializedName("deleted_at")
    @Expose
    private String deletedAt;
    @SerializedName("dialy_updated_at")
    @Expose
    private String dalilyUdatedAt;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("all_other_to_seeprofile")
    @Expose
    private Integer all_other_to_seeprofile=0;
    @SerializedName("all_other_to_seeDOB")
    @Expose
    private Integer all_other_to_seeDOB=0;
    @SerializedName("all_other_see_username")
    @Expose
    private Integer all_other_see_username=0;

    @SerializedName("all_other_to_seeMyfriends")
    @Expose
    public Integer all_other_to_seeMyfriends;

    @SerializedName("message_me")
    @Expose
    private Integer message_me=0;
    @SerializedName("question")
    @Expose
    private String question;
    @SerializedName("answer")
    @Expose
    private String answer;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("visibility")
    @Expose
    private String visibility;
    @SerializedName("privacy")
    @Expose
    private String privacy;
    @SerializedName("bio")
    @Expose
    private String bio;
    @SerializedName("deviceToken")
    @Expose
    private String deviceToken;
    @SerializedName("profileStatus")
    @Expose
    private String profileStatus;

    @SerializedName("memories")
    @Expose
    private List<MemoryModel> memories;

    @SerializedName("isFriend")
    @Expose
    private Integer isFriend;

    @SerializedName("isBlockedMe")
    @Expose
    private Integer isBlockedMe;

    @SerializedName("dailes")
    @Expose
    private List<MediaModel> dailyList;

    public String getAnswer() {
        return answer;
    }

    public String getDistance() {
        return distance;
    }

    public String getBg_picture() {
        return bg_picture;
    }

    public void setBg_picture(String bg_picture) {
        this.bg_picture = bg_picture;
    }

    public Integer getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(Integer isFriend) {
        this.isFriend = isFriend;
    }

    public List<MemoryModel> getMemoryList() {
        return memories;
    }

    public void setMemoryList(List<MemoryModel> mediaList) {
        this.memories = mediaList;
    }

    public List<MediaModel> getDailyList() {
        return dailyList;
    }

    public void setDailyList(List<MediaModel> dailyList) {
        this.dailyList = dailyList;
    }

    public Integer getIsBlockedMe() {
        return isBlockedMe;
    }

    public void setIsBlockedMe(Integer isBlockedMe) {
        this.isBlockedMe = isBlockedMe;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAll_other_to_seeprofile() {
        return all_other_to_seeprofile;
    }

    public void setAll_other_to_seeprofile(Integer all_other_to_seeprofile) {
        this.all_other_to_seeprofile = all_other_to_seeprofile;
    }

    public Integer getAll_other_to_seeDOB() {
        return all_other_to_seeDOB;
    }

    public void setAll_other_to_seeDOB(Integer all_other_to_seeDOB) {
        this.all_other_to_seeDOB = all_other_to_seeDOB;
    }

    public Integer getAll_other_see_username() {
        return all_other_see_username;
    }

    public void setAll_other_see_username(Integer all_other_see_username) {
        this.all_other_see_username = all_other_see_username;
    }

    public Integer getMessage_me() {
        return message_me;
    }

    public void setMessage_me(Integer message_me) {
        this.message_me = message_me;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Integer getMobileVerify() {
        return mobileVerify;
    }

    public void setMobileVerify(Integer mobileVerify) {
        this.mobileVerify = mobileVerify;
    }
    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLastActive() {
        return lastActive;
    }

    public void setLastActive(String lastActive) {
        this.lastActive = lastActive;
    }

    public String getCountry(){return this.country;}

    public void setCountry(String country){
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMobileChagneRequest() {
        return mobileChagneRequest;
    }

    public void setMobileChagneRequest(String mobileChagneRequest) {
        this.mobileChagneRequest = mobileChagneRequest;
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

    public Integer getSuperUser() {
        return isSuperUser;
    }

    public void setSuperUser(Integer isSuperUser) {
        this.isSuperUser = isSuperUser;
    }

    public Integer getAppAdmin() {
        return isAppAdmin;
    }

    public void setAppAdmin(Integer isAppAdmin) {
        this.isAppAdmin = isAppAdmin;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getDalilyUdatedAt() {
        return dalilyUdatedAt;
    }

    public void setDalilyUdatedAt(String dalilyUdatedAt) {
        this.dalilyUdatedAt = dalilyUdatedAt;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getProfileStatus() {
        return profileStatus;
    }

    public void setProfileStatus(String deviceToken) {
        this.profileStatus = profileStatus;
    }
}
