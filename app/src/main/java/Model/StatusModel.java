package Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StatusModel {

    @SerializedName("full_name")
    @Expose
    public String full_name;

    @SerializedName("user_id")
    @Expose
    public String user_id;

    @SerializedName("profile_picture")
    @Expose
    public String profile_picture;

    @SerializedName("text")
    @Expose
    public String text;

    @SerializedName("username")
    @Expose
    public String username;

    @SerializedName("created_at")
    @Expose
    public String created_at;

    @SerializedName("status_id")
    @Expose
    public String status_id;

    @SerializedName("views_count")
    @Expose
    public String views_count;

    @SerializedName("comments_count")
    @Expose
    public String comments_count;

    @SerializedName("likes_count")
    @Expose
    public Integer likes_count;

    @SerializedName("likeBefore")
    @Expose
    public boolean likeBefore;

}
