package Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.app.khaleeji.Response.Search.SearchUserModel;

import java.util.List;

public class SearchTagResponse {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<SubTagModel> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SubTagModel> getData(){
        return  data;
    }

    public void setData(List<SubTagModel> data){
        this.data = data;
    }
}
