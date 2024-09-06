package com.app.khaleeji.Response.fetchMemoryPackage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Dcube on 04-07-2018.
 */

public class Daily implements Serializable{

    @SerializedName("media_id")
    @Expose
    private Integer media_id;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("media_type")
    @Expose
    private String mediaType;
    @SerializedName("caption")
    @Expose
    private String caption;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("media_time")
    @Expose
    private Integer media_time;
    @SerializedName("media_link")
    @Expose
    private String media_link;

    private String view_count;

    public String getView_count() {
        return view_count;
    }

    public void setView_count(String view_count) {
        this.view_count = view_count;
    }

    public Integer getMedia_id() {
        return media_id;
    }

    public void setMedia_id(Integer media_id) {
        this.media_id = media_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
