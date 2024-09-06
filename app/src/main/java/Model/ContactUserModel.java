package Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContactUserModel {
    @SerializedName("username")
    @Expose
    public String username;

    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("mobile_number")
    @Expose
    public String mobile_number;

    @SerializedName("full_name")
    @Expose
    public String full_name;

    @SerializedName("question")
    @Expose
    public String question;

    @SerializedName("profile_picture")
    @Expose
    public String profile_picture;

    @SerializedName("status")
    @Expose
    public String status;

    @SerializedName("isFriend")
    @Expose
    public String isFriend;
}
