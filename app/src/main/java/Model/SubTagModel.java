package Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SubTagModel {

    @SerializedName("tags")
    @Expose
    public String tags;

    @SerializedName("total")
    @Expose
    public String total;

    @SerializedName("label")
    @Expose
    public String label;

    @SerializedName("arabicLabel")
    @Expose
    public String arabicLabel;

    @SerializedName("tag")
    @Expose
    public String tag;

    @SerializedName("type")
    @Expose
    public String type;

    @SerializedName("images")
    @Expose
    public List<SubTagImageModel> images;

    @SerializedName("ids")
    @Expose
    public List<String> ids;

}
