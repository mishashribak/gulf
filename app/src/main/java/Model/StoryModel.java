package Model;

/**
 * Created by user on 19/9/17.
 */

public class StoryModel {

    private String url = "";
    private String user_image = "";
    private String username;
    private String posted_time;
    private int type;  //1 image ,2 video

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPosted_time() {
        return posted_time;
    }

    public void setPosted_time(String posted_time) {
        this.posted_time = posted_time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
