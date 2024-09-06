package com.app.khaleeji.Response.fetchDailiesOfFriends;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Dcube on 20-08-2018.
 */

public class MediumDailiesOfFriends implements Serializable{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("content_type")
    @Expose
    private String contentType;
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
    private Object deletedAt;
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
    @SerializedName("removed_by")
    @Expose
    private Object removedBy;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("media_time")
    @Expose
    private Integer media_time;
    @SerializedName("media_link")
    @Expose
    private String media_link;


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

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
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


}
