package Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CountTagModel {
    @SerializedName("type")
    @Expose
    public String type;

    @SerializedName("count")
    @Expose
    public String count;

    @SerializedName("englishText")
    @Expose
    public String englishText;

    @SerializedName("arabicText")
    @Expose
    public String arabicText;

    @SerializedName("englishCount")
    @Expose
    public String englishCount;

    @SerializedName("arabicCount")
    @Expose
    public String arabicCount;

    @SerializedName("images")
    @Expose
    public List<String> images;

}
