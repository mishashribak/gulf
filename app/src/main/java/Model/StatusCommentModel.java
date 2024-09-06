package Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatusCommentModel {

    @SerializedName("user_id")
    @Expose
    public String user_id;

    @SerializedName("username")
    @Expose
    public String username;

    @SerializedName("profile_picture")
    @Expose
    public String profile_picture;

    @SerializedName("text")
    @Expose
    public String text;

    @SerializedName("comment_id")
    @Expose
    public String comment_id;

    @SerializedName("replies_count")
    @Expose
    public Integer replies_count;

    @SerializedName("likes_count")
    @Expose
    public Integer likes_count;

    @SerializedName("likeBefore")
    @Expose
    public boolean likeBefore;

    @SerializedName("ago")
    @Expose
    public String ago;

    @SerializedName("agoArabic")
    @Expose
    public String agoArabic;

    public boolean showReply;

}
