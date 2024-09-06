package Model;

import java.io.Serializable;

/**
 * Created by nine on 21/9/17.
 */

public class ItemObjects implements Serializable {

    private String topText;
    private String bottomText;
    private int backImg;

    public ItemObjects(String topText, String bottomText, int backImg) {
        this.topText = topText;
        this.bottomText = bottomText;
        this.backImg = backImg;

    }

    public String getTopText() {
        return topText;
    }

    public void setTopText(String topText) {
        this.topText = topText;
    }

    public String getBottomText() {
        return bottomText;
    }

    public void setBottomText(String bottomText) {
        this.bottomText = bottomText;
    }

    public int getBackImg() {
        return backImg;
    }

    public void setBackImg(int backImg) {
        this.backImg = backImg;
    }
}
