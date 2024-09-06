package Model;

import java.util.ArrayList;
import java.util.List;

public class ZipData {

    private List<ZipCategoryData> data = new ArrayList<>();
    private String timestamp;
    private String status;
    private String message_original;
    private String message;

    public List<ZipCategoryData> getData() {
        return data;
    }

    public void setData(List<ZipCategoryData> data) {
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage_original() {
        return message_original;
    }

    public void setMessage_original(String message_original) {
        this.message_original = message_original;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
