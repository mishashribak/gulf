package Model;

import android.content.Context;
import android.view.View;

import java.io.Serializable;

import Utility.SavePref;

public class UserData implements Serializable {
    public String email;
    public String deviceToken;
    public String isOnChatScreen;
    public String isOnline;
    public String profilePic;
    public String userId;
    public String username;
    public String isAdmin;

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getIsOnChatScreen() {
        return isOnChatScreen;
    }

    public void setIsOnChatScreen(String isOnChatScreen) {
        this.isOnChatScreen = isOnChatScreen;
    }

    public String getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(String isOnline) {
        this.isOnline = isOnline;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCurrentUser(Context context) {
        if (getUsername().toLowerCase().equals(SavePref.getInstance(context)
                .getUserdetail().getUsername().toLowerCase())) {
            return View.VISIBLE;
        }
        return View.GONE;
    }


}
