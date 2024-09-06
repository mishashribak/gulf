package Model;

import java.util.ArrayList;
import java.util.List;

public class LocalData {
    private List<ZipCategoryData> zipCategoryDataList = new ArrayList<>();
    public String timeStamp = "";

    public List<ZipCategoryData> getZipCategoryDataList() {
        return zipCategoryDataList;
    }

    public void setZipCategoryDataList(List<ZipCategoryData> zipCategoryDataList) {
        this.zipCategoryDataList = zipCategoryDataList;
    }

    public void setZipCategory(ZipCategoryData zipCategory){
        zipCategoryDataList.add(zipCategory);
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

}
