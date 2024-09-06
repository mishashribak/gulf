package Model;

/**
 * Created by user on 13/10/17.
 */

public class FriendModel {
    String username;
    String discription;
    String imageurl;

    public FriendModel(String username, String discription, String imageurl) {
        this.username = username;
        this.discription = discription;
        this.imageurl = imageurl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
