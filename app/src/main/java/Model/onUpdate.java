package Model;

/**
 * Created by user on 8/12/17.
 */

public class onUpdate {
    boolean isShow_tab;
    public onUpdate(boolean isShow_tab){
        this.isShow_tab=isShow_tab;
    }
    public boolean isShow_tab() {
        return isShow_tab;
    }

    public void setShow_tab(boolean show_tab) {
        isShow_tab = show_tab;
    }
}
