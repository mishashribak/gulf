package Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatusViewersModel {

    @SerializedName("username")
    @Expose
    public String username;

    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("profile_picture")
    @Expose
    public String profile_picture;
}
