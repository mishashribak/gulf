package Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OtherUserDataList {

    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("user_id")
    @Expose
    private String user_id;
    @SerializedName("profile_picture")
    @Expose
    private String profile_picture;
    @SerializedName("deviceToken")
    @Expose
    private String deviceToken;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
