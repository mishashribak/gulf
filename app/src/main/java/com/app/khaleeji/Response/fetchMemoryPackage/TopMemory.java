package com.app.khaleeji.Response.fetchMemoryPackage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Dcube on 04-07-2018.
 */

public class TopMemory {

    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("media_type")
    @Expose
    private String mediaType;
    @SerializedName("caption")
    @Expose
    private String caption;

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
