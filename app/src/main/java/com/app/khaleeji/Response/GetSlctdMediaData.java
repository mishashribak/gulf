package com.app.khaleeji.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class GetSlctdMediaData {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("caption")
    @Expose
    private String caption;
    @SerializedName("media_type")
    @Expose
    private String media_type;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("profile_picture")
    @Expose
    private String profile_picture;
    @SerializedName("created_at")
    @Expose
    private String created_at;
    @SerializedName("full_name")
    @Expose
    private String full_name;
    @SerializedName("like_count")
    @Expose
    private Integer likeCount;

    @SerializedName("is_like")
    @Expose
    private Integer isLike;

    @SerializedName("comment_count")
    @Expose
    private Integer commentCount;
    @SerializedName("comments")
    @Expose
    private List<GetSlctdMediaComments> comments = null;

    public Integer getLike() {
        return isLike;
    }


    public void setLike(Integer like) {
        isLike = like;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public List<GetSlctdMediaComments> getComments() {
        return comments;
    }

    public void setComments(List<GetSlctdMediaComments> comments) {
        this.comments = comments;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Integer getIsLike() {
        return isLike;
    }

    public void setIsLike(Integer isLike) {
        this.isLike = isLike;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }
}
