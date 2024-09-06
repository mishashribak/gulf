package Model;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

public class ZipCategoryData {

    private String id;
    private String filter_type;
    private String zip_name;
    private String location_name;
    private String type;
    private String url;
    private String created_at;
    private String updated_at;
    private String last_updated;
    private String category;
    private String zip_url;
    public List<String> stickerPathList = new ArrayList<>();

    public List<Integer> stickerDrawableList;


    public List<Integer> getStickerDrawableList() {
        return stickerDrawableList;
    }

    public void setStickerDrawableList(List<Integer> stickerDrawableList) {
        this.stickerDrawableList = stickerDrawableList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilter_type() {
        return filter_type;
    }

    public void setFilter_type(String filter_type) {
        this.filter_type = filter_type;
    }

    public String getZip_name() {
        return zip_name;
    }

    public void setZip_name(String zip_name) {
        this.zip_name = zip_name;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getZip_url() {
        return zip_url;
    }

    public void setZip_url(String zip_url) {
        this.zip_url = zip_url;
    }
}
