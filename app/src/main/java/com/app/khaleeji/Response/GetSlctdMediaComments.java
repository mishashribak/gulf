package com.app.khaleeji.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetSlctdMediaComments {

    @SerializedName("comment_id")
    @Expose
    private Integer commentId;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("commentor_id")
    @Expose
    private Integer commentorId;
    @SerializedName("profile_picture")
    @Expose
    private String profilePicture;
    @SerializedName("full_name")
    @Expose
    private String full_name;

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getCommentorId() {
        return commentorId;
    }

    public void setCommentorId(Integer commentorId) {
        this.commentorId = commentorId;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

}
