package com.app.khaleeji.Response;

import java.util.ArrayList;
import java.util.List;

public class DailiesMediaData {

    private String id;
    private String user_id;
    private String mime_type;
    private String url;
    private String last_updated;
    private String is_viewed;
    private List<MediaViewDataList> media_views = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }

    public String getIs_viewed() {
        return is_viewed;
    }

    public void setIs_viewed(String is_viewed) {
        this.is_viewed = is_viewed;
    }

    public List<MediaViewDataList> getMedia_views() {
        return media_views;
    }

    public void setMedia_views(List<MediaViewDataList> media_views) {
        this.media_views = media_views;
    }
}
