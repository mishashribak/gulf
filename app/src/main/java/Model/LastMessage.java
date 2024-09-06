package Model;

import android.view.View;

import java.io.Serializable;

public class LastMessage implements Serializable {

    private String date;
    private String isRead;
    private String message;
    private String username;


    public String getDate() {
        return date;
    }


    public void setDate(String date) {
        this.date = date;
    }


    public String getIsRead() {
        return isRead;
    }


    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }


    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int readStatus() {
        if (getIsRead().equals("1")) {
        return View.GONE;
        }
        return View.VISIBLE;
    }


}
