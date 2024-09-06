package Model;

import java.util.ArrayList;

/**
 * Created by Dcube on 08-06-2018.
 */

public class NewChatModel {


    private String headerTitle;
    private ArrayList<String> allItemsInSection;

    public NewChatModel() {

    }
    public NewChatModel(String headerTitle, ArrayList<String> allItemsInSection) {
        this.headerTitle = headerTitle;
        this.allItemsInSection = allItemsInSection;
    }



    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public ArrayList<String> getAllItemsInSection() {
        return allItemsInSection;
    }

    public void setAllItemsInSection(ArrayList<String> allItemsInSection) {
        this.allItemsInSection = allItemsInSection;
    }


}
