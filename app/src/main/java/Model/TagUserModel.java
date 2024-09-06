package Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TagUserModel {
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("full_name")
    @Expose
    public String full_name;
    @SerializedName("username")
    @Expose
    public String username;

    @SerializedName("profile_picture")
    @Expose
    public String profilePicture;

    @SerializedName("dob")
    @Expose
    public String dob;

    @SerializedName("status")
    @Expose
    public String status;

    @SerializedName("privacy")
    @Expose
    public String privacy;

    @SerializedName("question")
    @Expose
    public String question;

    @SerializedName("isFriend")
    @Expose
    public String isFriend;

    @SerializedName("all_other_to_seeprofile")
    @Expose
    public String all_other_to_seeprofile;

    @SerializedName("all_other_see_username")
    @Expose
    public String all_other_see_username;

    @SerializedName("profileStatus")
    @Expose
    public String profileStatus;

    @SerializedName("bio")
    @Expose
    public String bio;

    @SerializedName("text")
    @Expose
    public String text;
}
