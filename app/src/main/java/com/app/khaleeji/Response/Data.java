package com.app.khaleeji.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Data implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("all_other_to_seeprofile")
    @Expose
    private Integer all_other_to_seeprofile;
    @SerializedName("all_other_to_seeDOB")
    @Expose
    private Integer all_other_to_seeDOB;
    @SerializedName("message_me")
    @Expose
    private Integer message_me;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("request_accept")
    @Expose
    private String request_accept;
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
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("address")
    @Expose

    private String address;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("is_view")
    @Expose
    private boolean is_view;
    @SerializedName("is_followed")
    @Expose
    private Integer is_followed;
    @SerializedName("is_friend")
    @Expose
    private boolean is_friend;
    @SerializedName("is_block")
    @Expose
    private boolean is_block;
    @SerializedName("is_block_byme")
    @Expose
    private boolean is_block_byme;
    @SerializedName("from_req_user")
    @Expose
    private String from_req_user;
    @SerializedName("answer")
    @Expose
    private String answer;
    @SerializedName("question")
    @Expose
    private String question;
    @SerializedName("all_other_see_username")
    @Expose
    private Integer all_other_see_username;
    @SerializedName("is_visible")
    @Expose
    private Integer is_visible;
    @SerializedName("is_superUser")
    @Expose
    private Integer is_superUser;
    @SerializedName("visibility")
    @Expose
    private String visibility;

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Integer getIs_visible() {
        return is_visible;
    }

    public void setIs_visible(Integer is_visible) {
        this.is_visible = is_visible;
    }

    public boolean isIs_block() {
        return is_block;
    }

    public boolean isIs_block_byme() {
        return is_block_byme;
    }

    public Integer getAll_other_to_seeDOB() {
        return all_other_to_seeDOB;
    }

    public void setAll_other_to_seeDOB(Integer all_other_to_seeDOB) {
        this.all_other_to_seeDOB = all_other_to_seeDOB;
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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public boolean isIs_friend() {
        return is_friend;
    }

    public void setIs_friend(boolean is_friend) {
        this.is_friend = is_friend;
    }

    public String getFrom_req_user() {
        return from_req_user;
    }

    public void setFrom_req_user(String from_req_user) {
        this.from_req_user = from_req_user;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean is_block() {
        return is_block;
    }

    public void setIs_block(boolean is_block) {
        this.is_block = is_block;
    }

    public boolean is_block_byme() {
        return is_block_byme;
    }

    public void setIs_block_byme(boolean is_block_byme) {
        this.is_block_byme = is_block_byme;
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


    public Integer getAll_other_to_seeprofile() {
        return all_other_to_seeprofile;
    }

    public void setAll_other_to_seeprofile(Integer all_other_to_seeprofile) {
        this.all_other_to_seeprofile = all_other_to_seeprofile;
    }

    public Integer getMessage_me() {
        return message_me;
    }

    public void setMessage_me(Integer message_me) {
        this.message_me = message_me;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRequest_accept() {
        return request_accept;
    }

    public void setRequest_accept(String request_accept) {
        this.request_accept = request_accept;
    }

    public boolean is_friend() {
        return is_friend;
    }


    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getAll_other_see_username() {
        return all_other_see_username;
    }

    public void setAll_other_see_username(Integer all_other_see_username) {
        this.all_other_see_username = all_other_see_username;
    }


    public String getProfile_picture_thumb() {
        return profile_picture_thumb;
    }

    public void setProfile_picture_thumb(String profile_picture_thumb) {
        this.profile_picture_thumb = profile_picture_thumb;
    }

    public boolean isIs_view() {
        return is_view;
    }

    public void setIs_view(boolean is_view) {
        this.is_view = is_view;
    }

    public Integer getIs_followed() {
        return is_followed;
    }

    public void setIs_followed(Integer is_followed) {
        this.is_followed = is_followed;
    }

    public Integer getIs_superUser() {
        return is_superUser;
    }

    public void setIs_superUser(Integer is_superUser) {
        this.is_superUser = is_superUser;
    }
}
