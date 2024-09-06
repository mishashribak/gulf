package Model;

import java.util.ArrayList;

public class CustomGroupData {
    private String strName;
    private ArrayList<String> listMember;

    public CustomGroupData(String name, ArrayList<String> listMember){
        strName = name;
        this.listMember = listMember;
    }
    public void setGroupTitle(String name){
        strName = name;
    }

    public String getGroupTitle(){
        return strName;
    }

    public void setGroupMember(ArrayList<String> list){
        this.listMember = list;
    }

    public ArrayList<String> getGroupMember(){
        return this.listMember;
    }
}
