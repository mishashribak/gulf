package com.app.khaleeji.Response;

import java.util.ArrayList;
import java.util.List;

public class DialiesData {

    private String id;
    private String last_updated;
    private UserDailiesData user = new UserDailiesData();
    private String name;
    private String picture;
    private String is_all_viewed;
    private List<DailiesMediaData> get_media = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }

    public UserDailiesData getUser() {
        return user;
    }

    public void setUser(UserDailiesData user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getIs_all_viewed() {
        return is_all_viewed;
    }

    public void setIs_all_viewed(String is_all_viewed) {
        this.is_all_viewed = is_all_viewed;
    }

    public List<DailiesMediaData> getGet_media() {
        return get_media;
    }

    public void setGet_media(List<DailiesMediaData> get_media) {
        this.get_media = get_media;
    }
}
