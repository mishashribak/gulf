package Model;

/**
 * Created by user on 19/9/17.
 */

public class onHeaderBackground {
    int drawable;
    String title;
    int typeface;
    boolean show_backerrow;
    boolean RTl_status;

    public onHeaderBackground(int drawable, String title, int typeface, boolean show_backerrow,boolean rtl_status) {
        this.drawable = drawable;
        this.title = title;
        this.typeface = typeface;
        this.show_backerrow = show_backerrow;
        this.RTl_status=rtl_status;
    }

    public boolean isRTl_status() {
        return RTl_status;
    }

    public void setRTl_status(boolean RTl_status) {
        this.RTl_status = RTl_status;
    }

    public boolean isShow_backerrow() {
        return show_backerrow;
    }

    public void setShow_backerrow(boolean show_backerrow) {
        this.show_backerrow = show_backerrow;
    }

    public int getTypeface() {
        return typeface;
    }

    public void setTypeface(int typeface) {
        this.typeface = typeface;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }


}
