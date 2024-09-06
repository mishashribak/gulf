
package com.app.khaleeji.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class HotSpotMemory implements Serializable{

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("user_id")
    @Expose
    private Integer user_id;

    @SerializedName("hotspot_id")
    @Expose
    private Integer hotspot_id;

    @SerializedName("content_type")
    @Expose
    private String contentType;
    
    @SerializedName("url")
    @Expose
    private String  url;

    @SerializedName("updated_at")
    @Expose
    private String  updated_at;

    @SerializedName("created_at")
    @Expose
    private String  created_at;

    @SerializedName("deleted_at")
    @Expose
    private String  deleted_at;

    @SerializedName("status")
    @Expose
    private Integer status;

    @SerializedName("is_topmemory")
    @Expose
    private Integer is_topmemory;

    @SerializedName("media_type")
    @Expose
    private String  media_type;

    @SerializedName("media_time")
    @Expose
    private Integer media_time;

    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;

    @SerializedName("caption")
    @Expose
    private String caption;

    @SerializedName("media_link")
    @Expose
    private String media_link;

    @SerializedName("removed_by")
    @Expose
    private String removed_by;

    @SerializedName("is_approved")
    @Expose
    private String is_approved;

    @SerializedName("comments")
    @Expose
    private String comments;

    @SerializedName("groupId")
    @Expose
    private Integer groupId;


    public String getMedia_link() {
        return media_link;
    }

    public void setMedia_link(String media_link) {
        this.media_link = media_link;
    }

    public String getRemoved_by() {
        return removed_by;
    }

    public void setRemoved_by(String removed_by) {
        this.removed_by = removed_by;
    }

    public String getIs_approved() {
        return is_approved;
    }

    public void setIs_approved(String is_approved) {
        this.is_approved = is_approved;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIs_topmemory() {
        return is_topmemory;
    }

    public void setIs_topmemory(Integer is_topmemory) {
        this.is_topmemory = is_topmemory;
    }


    public Integer gethotspot_id() {
        return hotspot_id;
    }

    public void sethotspot_id(Integer hotspot_id) {
        this.hotspot_id = hotspot_id;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String geturl() {
        return url;
    }

    public void seturl(String url) {
        this.url = url;
    }

    public String getdeleted_at() {
        return deleted_at;
    }

    public void setdeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getmedia_type() {
        return media_type;
    }

    public void setmedia_type(String media_type) {
        this.media_type = media_type;
    }

    public Integer getmedia_time() {
        return media_time;
    }

    public void setmedia_time(Integer media_time) {
        this.media_time = media_time;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Integer getuser_id() {
        return user_id;
    }

    public void setuser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getcontentType() {
        return contentType;
    }

    public void setcontentType(String contentType) {
        this.contentType = contentType;
    }

    public String getcaption() {
        return caption;
    }

    public void setcaption(String caption) {
        this.caption = caption;
    }

    public String getthumbnail() {
        return thumbnail;
    }

    public void setthumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

}
