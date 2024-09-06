package com.app.khaleeji.Response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MeetupDetails implements Parcelable {


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
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("visibility")
    @Expose
    private String visibility;


    @SerializedName("all_other_to_seeDOB")
    @Expose
    private Integer all_other_to_seeDOB;

    @SerializedName("all_other_see_username")
    @Expose
    private Integer all_other_see_username;

    @SerializedName("txFirebaseToken")
    @Expose
    private String txFirebaseToken;

    @SerializedName("deviceToken")
    @Expose
    private String deviceToken;

    protected MeetupDetails(Parcel in) {
        fullName = in.readString();
        countryId = in.readString();
        gender = in.readString();
        height = in.readString();
        highSchool = in.readString();
        university = in.readString();
        companyName = in.readString();
        bio = in.readString();
        privacy = in.readString();
        profilePicture = in.readString();
        address = in.readString();
        lat = in.readString();
        lng = in.readString();
        question = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        txFirebaseToken = in.readString();
        deviceToken = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fullName);
        dest.writeString(countryId);
        dest.writeString(gender);
        dest.writeString(height);
        dest.writeString(highSchool);
        dest.writeString(university);
        dest.writeString(companyName);
        dest.writeString(bio);
        dest.writeString(privacy);
        dest.writeString(profilePicture);
        dest.writeString(address);
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeString(question);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeString(txFirebaseToken);
        dest.writeString(deviceToken);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MeetupDetails> CREATOR = new Creator<MeetupDetails>() {
        @Override
        public MeetupDetails createFromParcel(Parcel in) {
            return new MeetupDetails(in);
        }

        @Override
        public MeetupDetails[] newArray(int size) {
            return new MeetupDetails[size];
        }
    };

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

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Integer getAll_other_to_seeDOB() {
        return all_other_to_seeDOB;
    }

    public void setAll_other_to_seeDOB(Integer allOtherToSeeprofile) {
        this.all_other_to_seeDOB = all_other_to_seeDOB;
    }

    public Integer getAll_other_see_username() {
        return all_other_see_username;
    }

    public void setAll_other_see_username(Integer all_other_see_username) {
        this.all_other_see_username = all_other_see_username;
    }

    public String getTxFirebaseToken() {
        return txFirebaseToken;
    }

    public void setTxFirebaseToken(String txFirebaseToken) {
        this.txFirebaseToken = txFirebaseToken;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
