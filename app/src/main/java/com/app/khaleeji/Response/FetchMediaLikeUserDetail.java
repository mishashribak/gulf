package com.app.khaleeji.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FetchMediaLikeUserDetail {

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
    private String mobile_number;
    @SerializedName("mobile_verify")
    @Expose
    private String mobile_verify;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("country_id")
    @Expose
    private String countryId;
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("api_token")
    @Expose
    private String api_token;
    @SerializedName("last_active")
    @Expose
    private String last_active;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("state")
    @Expose
    private String state;
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
    private String mobile_change_request;
    @SerializedName("created_at")
    @Expose
    private String created_at;
    @SerializedName("updated_at")
    @Expose
    private String updated_at;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("is_superUser")
    @Expose
    private Integer is_superUser;
    @SerializedName("is_appAdmin")
    @Expose
    private Integer is_appAdmin;
    @SerializedName("deleted_at")
    @Expose
    private String deleted_at;
    @SerializedName("dialy_updated_at")
    @Expose
    private String dialy_updated_at;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("question")
    @Expose
    private String question;
    @SerializedName("visibility")
    @Expose
    private String visibility;
    @SerializedName("privacy")
    @Expose
    private String privacy;
    @SerializedName("bio")
    @Expose
    private String bio;
    @SerializedName("profileStatus")
    @Expose
    private String profileStatus;
    @SerializedName("tag")
    @Expose
    private String tag;
    @SerializedName("mention")
    @Expose
    private String mention;
    @SerializedName("all_other_to_seeprofile")
    @Expose
    private Integer all_other_to_seeprofile;
    @SerializedName("all_other_to_seeDOB")
    @Expose
    private Integer all_other_to_seeDOB;
    @SerializedName("all_other_see_username")
    @Expose
    private Integer all_other_see_username;
    @SerializedName("message_me")
    @Expose
    private Integer message_me;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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


    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getMobile_verify() {
        return mobile_verify;
    }

    public void setMobile_verify(String mobile_verify) {
        this.mobile_verify = mobile_verify;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getApi_token() {
        return api_token;
    }

    public void setApi_token(String str){
        api_token = str;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getLast_active() {
        return last_active;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getVisibility() {
        return visibility;
    }

    public Integer getStatus() {
        return status;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public void setMention(String mention) {
        this.mention = mention;
    }

    public String getMention() {
        return mention;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setProfileStatus(String profileStatus) {
        this.profileStatus = profileStatus;
    }

    public String getProfileStatus() {
        return profileStatus;
    }

    public void setDialy_updated_at(String dialy_updated_at) {
        this.dialy_updated_at = dialy_updated_at;
    }

    public String getDialy_updated_at() {
        return dialy_updated_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setIs_appAdmin(Integer is_appAdmin) {
        this.is_appAdmin = is_appAdmin;
    }

    public Integer getIs_appAdmin() {
        return is_appAdmin;
    }

    public void setIs_superUser(Integer is_superUser) {
        this.is_superUser = is_superUser;
    }

    public Integer getAll_other_see_username() {
        return all_other_see_username;
    }

    public Integer getAll_other_to_seeDOB() {
        return all_other_to_seeDOB;
    }

    public Integer getAll_other_to_seeprofile() {
        return all_other_to_seeprofile;
    }

    public Integer getIs_superUser() {
        return is_superUser;
    }

    public Integer getMessage_me() {
        return message_me;
    }

    public void setLast_active(String last_active) {
        this.last_active = last_active;
    }

    public String getToken() {
        return token;
    }

    public String getMobile_change_request() {
        return mobile_change_request;
    }

    public String getState() {
        return state;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setAll_other_see_username(Integer all_other_see_username) {
        this.all_other_see_username = all_other_see_username;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setAll_other_to_seeDOB(Integer all_other_to_seeDOB) {
        this.all_other_to_seeDOB = all_other_to_seeDOB;
    }

    public void setAll_other_to_seeprofile(Integer all_other_to_seeprofile) {
        this.all_other_to_seeprofile = all_other_to_seeprofile;
    }

    public void setMessage_me(Integer message_me) {
        this.message_me = message_me;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setMobile_change_request(String mobile_change_request) {
        this.mobile_change_request = mobile_change_request;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

}
