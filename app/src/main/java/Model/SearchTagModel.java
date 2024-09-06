package Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchTagModel {
    @SerializedName("total")
    @Expose
    private Integer total;

    @SerializedName("tag")
    @Expose
    private String tag;

    @SerializedName("label")
    @Expose
    private String label;

    @SerializedName("arabicLabel")
    @Expose
    private String arabicLabel;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getArabicLabel() {
        return arabicLabel;
    }

    public void setArabicLabel(String arabicLabel) {
        this.arabicLabel = arabicLabel;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
