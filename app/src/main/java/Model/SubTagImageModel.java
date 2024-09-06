package Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubTagImageModel {
    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("thumbnail")
    @Expose
    public String thumbnail;
}
