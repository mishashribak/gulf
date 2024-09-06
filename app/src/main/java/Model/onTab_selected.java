package Model;

/**
 * Created by user on 19/9/17.
 */

public class onTab_selected {
    int tab_pos;
    boolean isfriendtab;

    public onTab_selected(boolean isfriendtab, int tab_pos) {
        this.tab_pos = tab_pos;
        this.isfriendtab = isfriendtab;
    }

    public boolean isfriendtab() {
        return isfriendtab;
    }

    public void setIsfriendtab(boolean isfriendtab) {
        this.isfriendtab = isfriendtab;
    }

    public int getTabpos() {
        return tab_pos;
    }

    public void setTabpos(int tab_pos) {
        this.tab_pos = tab_pos;
    }
}
