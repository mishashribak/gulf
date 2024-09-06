package com.app.khaleeji.Response.fetchHotspotTimeLine;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Dcube on 04-07-2018.
 */

public class HotspotMemory {

    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("media_type")
    @Expose
    private String mediaType;
    @SerializedName("caption")
    @Expose
    private String caption;
    @SerializedName("media_id")
    @Expose
    private Integer media_id;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Integer getMedia_id() {
        return media_id;
    }

    public void setMedia_id(Integer media_id) {
        this.media_id = media_id;
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





}
