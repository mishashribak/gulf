
package com.app.khaleeji.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NearUserDetail implements Serializable {

    @SerializedName("distance")
    @Expose
    private Double distance;
    @SerializedName("distance_unit")
    @Expose
    private String distance_unit;
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
    private Integer mobile_verify;
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
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private String deleted_at;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("is_superUser")
    @Expose
    private Integer is_superUser;
    @SerializedName("is_appAdmin")
    @Expose
    private Integer is_appAdmin;
    @SerializedName("dialy_updated_at")
    @Expose
    private String dialy_updated_at;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("all_other_to_seeprofile")
    @Expose
    private Integer all_other_to_seeprofile;
    @SerializedName("all_other_see_username")
    @Expose
    private Integer all_other_see_username;

    @SerializedName("all_other_to_seeDOB")
    @Expose
    private Integer all_other_to_seeDOB;
    @SerializedName("message_me")
    @Expose
    private Integer message_me;
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

    public String getMention() {
        return mention;
    }

    public void setMention(String mention) {
        this.mention = mention;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getProfileStatus() {
        return profileStatus;
    }

    public void setProfileStatus(String profileStatus) {
        this.profileStatus = profileStatus;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getDialy_updated_at() {
        return dialy_updated_at;
    }

    public void setDialy_updated_at(String dialy_updated_at) {
        this.dialy_updated_at = dialy_updated_at;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public Integer getIs_appAdmin() {
        return is_appAdmin;
    }

    public void setIs_appAdmin(Integer is_appAdmin) {
        this.is_appAdmin = is_appAdmin;
    }

    public Integer getIs_superUser(){
        return this.is_superUser;
    }

    public void setIs_superUser(Integer is_superUser) {
        this.is_superUser = is_superUser;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMobile_change_request(){
        return  mobile_change_request;
    }

    public void setMobile_change_request(String mobile_change_request){
        this.mobile_change_request = mobile_change_request;
    }
    public String getCity(){
        return  city;
    }

    public void setCity(String city){
        this.city = city;
    }
    public String getState(){
        return  state;
    }

    public void setState(String state){
        this.state = state;
    }
    public String getCountry(){
        return  country;
    }

    public void setCountry(String country){
        this.country = country;
    }
    public String getToken(){
        return  token;
    }

    public void setToken(String token){
        this.token = token;
    }
    public String getLast_active(){
        return  last_active;
    }

    public void setLast_active(String last_active){
        this.last_active = last_active;
    }
    public String getUid(){
        return  uid;
    }

    public void setUid(String uid){
        this.uid = uid;
    }
    public String getDob(){
        return  dob;
    }

    public void setDob(String dob){
        this.dob = dob;
    }

    public String getDistanceUnit(){
        return  distance_unit;
    }

    public void setDistanceUnit(String unit){
        distance_unit = unit;
    }

    public String getEmail(){
        return  email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getMobile_number(){
        return  mobile_number;
    }

    public void setMobile_number(String mobile_number){
        this.mobile_number = mobile_number;
    }

    public Integer getMobile_verify() {
        return mobile_verify;
    }

    public void setMobile_verify(Integer mobile_verify) {
        this.mobile_verify = mobile_verify;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public Integer getAll_other_see_username() {
        return all_other_see_username;
    }

    public void setAll_other_see_username(Integer all_other_see_username) {
        this.all_other_see_username = all_other_see_username;
    }

    public Integer getAll_other_to_seeDOB() {
        return all_other_to_seeDOB;
    }

    public void setAll_other_to_seeDOB(Integer all_other_to_seeDOB) {
        this.all_other_to_seeDOB = all_other_to_seeDOB;
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



    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

}
