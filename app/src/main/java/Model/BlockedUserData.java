package Model;


public class BlockedUserData {
    private String strName;
    private boolean isChecked;

    public BlockedUserData(String name, boolean isChecked){
        strName = name;
        this.isChecked = isChecked;
    }
    public void setName(String name){
        strName = name;
    }

    public String getName(){
        return strName;
    }

    public void setChecked(boolean isChecked){
        this.isChecked = isChecked;
    }

    public boolean isChecked(){
        return this.isChecked;
    }
}
