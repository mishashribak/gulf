package Constants;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import Model.ChatDetails;
import Model.ChatUserData;
import Model.LocalData;
import Model.UploadInfoModel;

import com.google.gson.Gson;
import com.app.khaleeji.Response.FriendData;

import java.util.ArrayList;
import java.util.List;

public class SessionData {

    public static final String LOCAL_DATA_VERSION = "2";
    public static final int PREF_SAVED_DATA = 99;
    public static final int PREF_SAVED_DATA2 = 100;
    public static final int PREF_KEY_VERSION = 21;
    public List<UploadInfoModel> uploadInfoModelList = new ArrayList<>();
    static SessionData instance;
    public LocalData localData;
    protected String fileName;
    protected SharedPreferences preferences;
    String version = "0";
    Gson gson = new Gson();
    private Context context;
    private ChatUserData chatUserData = new ChatUserData();
    private ChatDetails chatDetails = new ChatDetails();
    public List<ChatUserData> chatUserList = new ArrayList<>();
    public List<FriendData> friendDataList = new ArrayList<>();
    public boolean isFromGroup = false;
    public boolean isUserBlocked = false;


    public List<ChatUserData> getChatUserList() {
        return chatUserList;
    }

    public void setChatUserList(List<ChatUserData> chatUserList) {
        this.chatUserList = chatUserList;
    }

    public ChatDetails getChatDetails() {
        return chatDetails;
    }

    public void setChatDetails(ChatDetails chatDetails) {
        this.chatDetails = chatDetails;
    }

    public SessionData() {

    }

    public ChatUserData getChatUserData() {
        return chatUserData;
    }

    public void setChatUserData(ChatUserData chatUserData) {
        this.chatUserData = chatUserData;
    }

    public static void makeIntentAsClearHistory(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    public static SessionData I() {
        if (instance == null) {
            instance = new SessionData();
        }
        return instance;
    }

    public LocalData getLocalData() {
        return localData;
    }

    public void setLocalData(LocalData localData) {
        this.localData = localData;
    }

    public void init(Context context) {
        this.context = context;
        fileName = context.getPackageName() + ".prefFile";
        preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        version = readString(PREF_KEY_VERSION);
        String data = readString(PREF_SAVED_DATA2);
        Log.d("TAG", "SessionData: " + data);


        if (!version.equals(LOCAL_DATA_VERSION) || data.length() <= 1) {
            version = LOCAL_DATA_VERSION;
            localData = new LocalData();
            saveLocalData();
        } else {
            if (data.length() > 1) {
                try {
                    this.localData = (gson.fromJson(data, LocalData.class));
                } catch (Exception e) {
                    localData = new LocalData();
                }
            }
        }
    }

    public void saveLocalData() {
        writeString(PREF_KEY_VERSION, version);
        String data = gson.toJson(localData, LocalData.class);
        Log.d("TAG", "saveData: " + data);
        writeString(PREF_SAVED_DATA2, data);
    }

    public void writeString(Integer key, String value) {
        preferences.edit().putString(key.toString(), value).apply();
    }

    public void writeBoolean(Integer key, Boolean bool) {
        preferences.edit().putBoolean(key.toString(), bool).apply();
    }

    public Boolean readBoolean(Integer key) {
        return preferences.getBoolean(key.toString(), false);
    }

    public String readString(Integer key) {
        switch (key) {
            case PREF_SAVED_DATA:
                return preferences.getString(key.toString(), "");
        }
        return preferences.getString(key.toString(), "");
    }

    public void clearLocalData() {
        localData = new LocalData();
        saveLocalData();
    }

}
