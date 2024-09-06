package Model;

/**
 * Created by user on 22/9/17.
 */

public class MessageModel {

    private String musername;
    private String lastmessage;
    private String duration;
    private String user_imageurl;

    public String getUser_imageurl() {
        return user_imageurl;
    }

    public void setUser_imageurl(String user_imageurl) {
        this.user_imageurl = user_imageurl;
    }

    public String getMusername() {
        return musername;
    }

    public void setMusername(String musername) {
        this.musername = musername;
    }

    public String getLastmessage() {
        return lastmessage;
    }

    public void setLastmessage(String lastmessage) {
        this.lastmessage = lastmessage;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
