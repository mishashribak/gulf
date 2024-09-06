package com.app.khaleeji.Response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserDetails implements Parcelable {

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

    @SerializedName("otp")
    @Expose
    private String otp;

    @SerializedName("mobile_number")
    @Expose
    private String mobileNumber;

    @SerializedName("mobile_verify")
    @Expose
    private Integer mobileVerify;

    @SerializedName("country_id")
    @Expose
    private Integer countryId;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("api_token")
    @Expose
    private String token;

    @SerializedName("token")
    @Expose
    private String noti_token;

    @SerializedName("bg_picture")
    @Expose
    private String bg_picture;

    @SerializedName("dob")
    @Expose
    private String dob;

    @SerializedName("uid")
    @Expose
    private String uid;

    @SerializedName("plain_password")
    @Expose
    private String plainPassword;

    @SerializedName("last_active")
    @Expose
    private String lastActive;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("mobile_change_request")
    @Expose
    private String mobileChagneRequest;

    @SerializedName("lat")
    @Expose
    private String lat;

    @SerializedName("lng")
    @Expose
    private String lng;

    @SerializedName("is_superUser")
    @Expose
    private Integer isSuperUser;

    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    @SerializedName("status")
    @Expose
    private Integer status;

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

    @SerializedName("message_me")
    @Expose
    private Integer message_me=0;

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

    @SerializedName("deviceToken")
    @Expose
    private String deviceToken;

    @SerializedName("tag")
    @Expose
    private String tag;

    @SerializedName("mention")
    @Expose
    private String mention;

////////////////


    protected UserDetails(Parcel in) {
//        id = in.readInteger();
        fullName = in.readString();
        profilePicture = in.readString();
        email = in.readString();
        otp = in.readString();
        mobileNumber = in.readString();
        username = in.readString();
        token = in.readString();
        noti_token = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
//        mobileVerify = in.readInteger();
//        countryId = in.readInteger();
        dob = in.readString();
        uid = in.readString();
        plainPassword = in.readString();
        lastActive = in.readString();
        state = in.readString();
        city = in.readString();
        mobileChagneRequest = in.readString();
        lat = in.readString();
        lng = in.readString();
//        isSuperUser = in.readInteger();
//        isAppAdmin = in.readInteger();
//        status = in.readInteger();
        deletedAt = in.readString();
        dalilyUdatedAt = in.readString();
        gender = in.readString();
//        all_other_to_seeprofile = in.readInteger();
//        all_other_to_seeDOB = in.readInteger();
//        all_other_see_username = in.readInteger();
//        message_me = in.readInteger();
        question = in.readString();
        visibility = in.readString();
        privacy = in.readString();
        bio = in.readString();
        deviceToken = in.readString();
    }

    public static final Creator<UserDetails> CREATOR = new Creator<UserDetails>() {
        @Override
        public UserDetails createFromParcel(Parcel in) {
            return new UserDetails(in);
        }

        @Override
        public UserDetails[] newArray(int size) {
            return new UserDetails[size];
        }
    };

    public String getBg_picture() {
        return bg_picture;
    }

    public void setBg_picture(String bg_picture) {
        this.bg_picture = bg_picture;
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

    public String getNotiToken() {
        return noti_token;
    }

    public void setNotiToken(String noti_token) {
        this.noti_token = noti_token;
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

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getAll_other_to_seeprofile() {
        return all_other_to_seeprofile;
    }

    public void setAll_other_to_seeprofile(Integer all_other_to_seeprofile) {
        this.all_other_to_seeprofile = all_other_to_seeprofile;
    }

    public Integer getAll_other_to_seedob() {
        return all_other_to_seeDOB;
    }

    public void setAll_other_to_seedob(Integer all_other_to_seeDOB) {
        this.all_other_to_seeDOB = all_other_to_seeDOB;
    }

    public Integer getAll_other_to_seeusername() {
        return all_other_see_username;
    }

    public void setAll_other_to_seeusername(Integer all_other_see_username) {
        this.all_other_see_username = all_other_see_username;
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

    public Integer getMessageme() {
        return message_me;
    }

    public void setMessageme(Integer message_me) {
        this.message_me = message_me;
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

    public String getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }

    public String getLastActive() {
        return lastActive;
    }

    public void setLastActive(String lastActive) {
        this.lastActive = lastActive;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeInteger(id);
        dest.writeString(fullName);
        dest.writeString(profilePicture);
        dest.writeString(email);
        dest.writeString(otp);
        dest.writeString(mobileNumber);
        dest.writeString(username);
        dest.writeString(token);
        dest.writeString(noti_token);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
//        dest.writeInteger(mobileVerify);
//        dest.writeInteger(countryId);
        dest.writeString(dob);
        dest.writeString(uid);
        dest.writeString(plainPassword);
        dest.writeString(lastActive);
        dest.writeString(state);
        dest.writeString(city);
        dest.writeString(mobileChagneRequest);
        dest.writeString(lat);
        dest.writeString(lng);
//        dest.writeInteger(isSuperUser);
//        dest.writeInteger(isAppAdmin);
//        dest.writeInteger(status);
        dest.writeString(deletedAt);
        dest.writeString(dalilyUdatedAt);
        dest.writeString(gender);
//        dest.writeInteger(all_other_to_seeprofile);
//        dest.writeInteger(all_other_to_seeDOB);
//        dest.writeInteger(all_other_see_username);
//        dest.writeInteger(message_me);
        dest.writeString(question);
        dest.writeString(visibility);
        dest.writeString(privacy);
        dest.writeString(bio);
        dest.writeString(deviceToken);
    }
}