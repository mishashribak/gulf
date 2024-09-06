package com.app.khaleeji.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MediaModel implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("hotspot_id")
    @Expose
    private Integer hotspotId;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("content_type")
    @Expose
    private String contentType;
    @SerializedName("ago")
    @Expose
    private String ago;
    @SerializedName("agoArabic")
    @Expose
    private String agoArabic;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private String deletedAt;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("is_topmemory")
    @Expose
    private Integer isTopmemory;
    @SerializedName("media_type")
    @Expose
    private String mediaType;
    @SerializedName("caption")
    @Expose
    private String caption;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("media_time")
    @Expose
    private Integer media_time;
    @SerializedName("media_link")
    @Expose
    private String media_link;
    @SerializedName("removed_by")
    @Expose
    private Object removedBy;
    @SerializedName("is_approved")
    @Expose
    private String isApproved;
    @SerializedName("comments")
    @Expose
    private String comments;
    @SerializedName("groupId")
    @Expose
    private String groupId;
    @SerializedName("already_viewed")
    @Expose
    private Integer alreadyViewed;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("view_count")
    @Expose
    private Integer view_count;

    @SerializedName("profile_picture")
    @Expose
    public String  profilePicture;

    @SerializedName("sourceType")
    @Expose
    public String  sourceType;

    @SerializedName("viewBefore")
    @Expose
    public Boolean  viewBefore;

    public Boolean getViewBefore() {
        return viewBefore;
    }

    public Integer getView_count() {
        return view_count;
    }

    public void setView_count(Integer view_count) {
        this.view_count = view_count;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(String isApproved) {
        this.isApproved = isApproved;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getAlreadyViewed() {
        return alreadyViewed;
    }

    public void setAlreadyViewed(Integer alreadyViewed) {
        this.alreadyViewed = alreadyViewed;
    }

    public Integer getHotspotId() {
        return hotspotId;
    }

    public void setHotspotId(Integer hotspotId) {
        this.hotspotId = hotspotId;
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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsTopmemory() {
        return isTopmemory;
    }

    public void setIsTopmemory(Integer isTopmemory) {
        this.isTopmemory = isTopmemory;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Object getRemovedBy() {
        return removedBy;
    }

    public void setRemovedBy(Object removedBy) {
        this.removedBy = removedBy;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getMedia_time() {
        return media_time;
    }

    public void setMedia_time(Integer media_time) {
        this.media_time = media_time;
    }

    public String getMedia_link() {
        return media_link;
    }

    public void setMedia_link(String media_link) {
        this.media_link = media_link;
    }

    public String getAgo() {
        return ago;
    }

    public String getAgoArabic() {
        return agoArabic;
    }
}
