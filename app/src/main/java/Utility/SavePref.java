package Utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.app.khaleeji.Response.GulfProfileResponse;
import com.app.khaleeji.Response.MeetupDetails;
import com.app.khaleeji.Response.NotficationsSettings;
import com.app.khaleeji.Response.UserDetails;
import com.google.gson.reflect.TypeToken;

import net.grandcentrix.tray.AppPreferences;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class SavePref {

    public static final String Key_MeetupDetails="MeetupDetails";
    public static final String Key_userdetail="Userdetail";
    public static final String Key_GulfProfileResponse = "GulfProfileResponse";
    public static final String NOTIFICATIONS="Notifications";
    public static final String pref_name="Gulflink";
    public static final String showmessageReminder="showmessageReminder";
    public static final String Key_deviceid="deviceid";
    private static final String pref_lang="lang";
    private static final String pref_Latitude="lat";
    private static final String pref_Longitude="lng";
    private static final String NOTIFICATIONCOUNT="notificationCount";
    private static final String ZipDownload = "zipDownload";

    public static  SavePref mSavePref;
    final AppPreferences appPreferences;
    private Context context;

    public SavePref(Context context){
        appPreferences = new AppPreferences(context);
        this.context = context;
    }

    public static SavePref getInstance(Context mcontext) {
        if(mSavePref==null){
            mSavePref=new SavePref(mcontext);
        }
        return mSavePref;
    }

    public  Double getLatitude() {
        SharedPreferences sharedPref = context.getSharedPreferences(pref_name, MODE_PRIVATE);
        return Double.parseDouble(sharedPref.getString(pref_Latitude,"1"));
    }

    public void  setLatitude(String latitude) {
        SharedPreferences sharedPref = context.getSharedPreferences(pref_name, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(pref_Latitude,latitude).commit();
    }

    public  Double getLongitude() {
        SharedPreferences sharedPref = context.getSharedPreferences(pref_name, MODE_PRIVATE);
        return Double.parseDouble(sharedPref.getString(pref_Longitude,"1"));
    }

    public void  setLongitude(String Longitude) {
        SharedPreferences sharedPref = context.getSharedPreferences(pref_name, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(pref_Longitude,Longitude).commit();
    }

    public void  setShowmessageReminder(boolean showReminder) {
        SharedPreferences sharedPref = context.getSharedPreferences(pref_name, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(showmessageReminder,showReminder).commit();
    }
    public boolean getShowRemider() {
        SharedPreferences sharedPref = context.getSharedPreferences(pref_name, MODE_PRIVATE);
        return sharedPref.getBoolean(showmessageReminder,false);
    }

    public void save_FirebaseDeviceKey(String Firebasetoken) {
        SharedPreferences sharedPref = context.getSharedPreferences(pref_name, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Key_deviceid, Firebasetoken);
        editor.commit();
    }

    public String getFirebase_DeviceKey(){
        SharedPreferences sharedPref = context.getSharedPreferences(pref_name, MODE_PRIVATE);
        return  sharedPref.getString(Key_deviceid,"");
    }

    public void  setLang(String lang) {
        SharedPreferences sharedPref = context.getSharedPreferences(pref_name, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(pref_lang,lang).commit();
    }
    public String getPref_lang() {
        SharedPreferences sharedPref = context.getSharedPreferences(pref_name, MODE_PRIVATE);

        return sharedPref.getString(pref_lang,"en");
    }


    public void saveMeetupdetail(MeetupDetails mMeetupDetails) {
        SharedPreferences sharedPref = context.getSharedPreferences(pref_name, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String mmeetupjs = gson.toJson(mMeetupDetails);
        editor.putString(Key_MeetupDetails, mmeetupjs);
        editor.commit();
    }

    public MeetupDetails getMeetupdetail() {
        SharedPreferences sharedPref = context.getSharedPreferences(pref_name, MODE_PRIVATE);
        Gson gson = new Gson();
        MeetupDetails mMeetupDetails = gson.fromJson(sharedPref.getString(Key_MeetupDetails,null),MeetupDetails.class);
        return  mMeetupDetails;
    }

    public void saveUserdetail(UserDetails mUserdetail) {
        SharedPreferences sharedPref = context.getSharedPreferences(pref_name, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String muserjson = gson.toJson(mUserdetail);
        editor.putString(Key_userdetail, muserjson);
        editor.commit();
    }

    public void saveGulfProfileResponse(GulfProfileResponse gulfProfileResponse) {
        SharedPreferences sharedPref = context.getSharedPreferences(pref_name, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String muserjson = gson.toJson(gulfProfileResponse);
        editor.putString(Key_GulfProfileResponse, muserjson);
        editor.commit();
    }

    public void setNewMsgNotification(boolean hasNew) {
        SharedPreferences sharedPref = context.getSharedPreferences(pref_name, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("new_notification", hasNew);
        editor.commit();
    }

    public boolean getNewMsgNotification() {
        SharedPreferences sharedPref = context.getSharedPreferences(pref_name, MODE_PRIVATE);
        return sharedPref.getBoolean("new_notification",false);
    }

    public void saveSettingsMessage(String message) {
        SharedPreferences sharedPref = context.getSharedPreferences(pref_name, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(NOTIFICATIONS, message);
        editor.commit();
    }


    public UserDetails getUserdetail() {
        SharedPreferences sharedPref = context.getSharedPreferences(pref_name, MODE_PRIVATE);
        Gson gson = new Gson();
        UserDetails mUserdetail = gson.fromJson(sharedPref.getString(Key_userdetail,null),UserDetails.class);
        return  mUserdetail;
    }

    public GulfProfileResponse getGulfProfileResponse() {
        SharedPreferences sharedPref = context.getSharedPreferences(pref_name, MODE_PRIVATE);
        Gson gson = new Gson();
        GulfProfileResponse gulfProfileResponse = gson.fromJson(sharedPref.getString(Key_GulfProfileResponse,null), GulfProfileResponse.class);
        return  gulfProfileResponse;
    }


    public void clearPref() {
        SharedPreferences sharedPref = context.getSharedPreferences(pref_name, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }

    public void setNotificationCount(int notificationCount) {

        SharedPreferences sharedPref = context.getSharedPreferences(pref_name, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt(NOTIFICATIONCOUNT, notificationCount);
        editor.commit();
    }

    public int getNotificationCount() {
        SharedPreferences sharedPref = context.getSharedPreferences(pref_name, MODE_PRIVATE);
        int message_count = sharedPref.getInt(NOTIFICATIONCOUNT,0);
        return  message_count;
    }

    public void saveToCache(HashMap map){
        Gson gson = new Gson();
        String hashMapString = gson.toJson(map);
        SharedPreferences prefs = context.getSharedPreferences(pref_name, MODE_PRIVATE);
        prefs.edit().putString("cache", hashMapString).apply();
    }

    public HashMap getCacheData(){
        Gson gson = new Gson();
        SharedPreferences prefs = context.getSharedPreferences(pref_name, MODE_PRIVATE);
        String storedHashMapString = prefs.getString("cache", "");
        java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>(){}.getType();
        HashMap<String, String> map = gson.fromJson(storedHashMapString, type);
        return map;
    }
}