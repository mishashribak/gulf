package com.app.khaleeji.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MemoryModel {

    @SerializedName("content_type")
    @Expose
    private String content_type;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("media_type")
    @Expose
    private String mediaType;
    @SerializedName("caption")
    @Expose
    private String caption;
    @SerializedName("created_at")
    @Expose
    private String created_at;
    @SerializedName("hotspot_id")
    @Expose
    private Integer hotspot_id;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("like_count")
    @Expose
    private Integer like_count;
    @SerializedName("comment_count")
    @Expose
    private Integer comment_count;
    @SerializedName("view_count")
    @Expose
    private Integer view_count;
    @SerializedName("ago")
    @Expose
    private String ago;
    @SerializedName("agoArabic")
    @Expose
    private String agoArabic;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("profile_picture")
    @Expose
    private String profile_picture;
    @SerializedName("full_name")
    @Expose
    private String full_name;
    @SerializedName("user_id")
    @Expose
    private Integer user_id;
    @SerializedName("media_time")
    @Expose
    private Integer media_time;

    @SerializedName("updated_at")
    @Expose
    private String  updated_at;

    @SerializedName("deleted_at")
    @Expose
    private String  deleted_at;

    @SerializedName("status")
    @Expose
    private Integer status;

    @SerializedName("media_link")
    @Expose
    private String media_link;

    @SerializedName("comments")
    @Expose
    private String comments;

    @SerializedName("groupId")
    @Expose
    private Integer groupId;

    @SerializedName("is_like")
    @Expose
    private Boolean isLike;

    public Boolean getLike() {
        return isLike;
    }


    public void setLike(Boolean like) {
        isLike = like;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getMedia_link() {
        return media_link;
    }

    public void setMedia_link(String media_link) {
        this.media_link = media_link;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public Integer getHotspot_id() {
        return hotspot_id;
    }

    public void setHotspot_id(Integer hotspot_id) {
        this.hotspot_id = hotspot_id;
    }

    public Integer getMedia_time() {
        return media_time;
    }

    public void setMedia_time(Integer media_time) {
        this.media_time = media_time;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public Integer getComment_count() {
        return comment_count;
    }

    public void setComment_count(Integer comment_count) {
        this.comment_count = comment_count;
    }

    public Integer getLike_count() {
        return like_count;
    }

    public void setLike_count(Integer like_count) {
        this.like_count = like_count;
    }

    public Integer getView_count() {
        return view_count;
    }

    public void setView_count(Integer view_count) {
        this.view_count = view_count;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAgo() {
        return ago;
    }

    public void setAgo(String ago) {
        this.ago = ago;
    }

    public String getAgoArabic() {
        return agoArabic;
    }

    public void setAgoArabic(String agoArabic) {
        this.agoArabic = agoArabic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }
}
