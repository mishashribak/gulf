package com.app.khaleeji.Response.fetchDailiesOfFriends;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.app.khaleeji.Response.MediaModel;

import java.io.Serializable;
import java.util.List;


public class FriendListDailiesOfFriends implements Serializable {

    @SerializedName("user_id")
    @Expose
    private Integer id;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("profile_picture")
    @Expose
    private String profilePicture;
    @SerializedName("visibility")
    @Expose
    private String visibility;
    @SerializedName("medias")
    @Expose
    private List<MediaModel> media;
    @SerializedName("lastUpdatedDaily")
    @Expose
    private String lastUpdatedDaily;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("viewAll")
    @Expose
    private Integer isAllViewed;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }


    public String getLastUpdatedDaily() {
        return lastUpdatedDaily;
    }

    public void setLastUpdatedDaily(String lastUpdatedDaily) {
        this.lastUpdatedDaily = lastUpdatedDaily;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Integer getIsAllViewed() {
        return isAllViewed;
    }

    public void setIsAllViewed(Integer isAllViewed) {
        this.isAllViewed = isAllViewed;
    }

    public List<MediaModel> getMedia() {
        return media;
    }

    public void setMedia(List<MediaModel> media) {
        this.media = media;
    }

}


