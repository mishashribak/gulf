package Utility;

import android.content.Context;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import Constants.Bundle_Identifier;


public class ApiClass {

    public static final String GooglePlaceKey = "AIzaSyClAnc__rQnEoH75Zs2sbWKj06tAYlSQus";

    //production ip
    public static final String BASE_URL = "https://gulflinksa.com/api/";
    public static final String ImageBaseUrl = "https://gulflinksa.com";

    //test ip
//    public static final String BASE_URL = "http://178.128.19.129/api/";
//    public static final String ImageBaseUrl = "http://178.128.19.129";

    public static final String GULF_TERMS_URL = "https://khaleejiapp.com/terms-and-conditions";
    public static final String S3_BUCKET_NAME = "meetupmedia";          //meetupmedia/images  //gulflink-userfiles-mobilehub-688419521
    public static final String S3_COGNITO_POOL_ID = "ap-southeast-1:ad7c1726-0647-4bad-9fdd-f1b89c2cac80";

    public static final String RATING = "rating";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String MOBILE_NUMBER = "mobile_number";
    public static final String COUNTRY_ID = "country_id";
    public static final String DEVICE_TYPE = "device_type";
    public static final String FIREBASE_TOKEN = "txFirebaseToken";
    public static final String DEVICE_ID = "device_id";
    public static final String USER_ID = "user_id";
    public static final String PROFILE_PICTURE = "profile_picture";
    public static final String FORGOT_TYPE = "forgot_type";
    public static final String TYPE = "type";
    public static final String VALUE = "value";
    public static final String QUESTION = "question";
    public static final String RADIUS = "radius";
    public static final String TYPES = "types";
    public static final String Lat = "lat";
    public static final String LNG = "lng";
    public static final String LOCATION_NAME = "location_name";
    public static final String PLACE_ID = "place_id";
    public static final String HOTSPOT_ID = "hotspot_id";
    public static final String IMAGE = "image";
    public static final String GROUP_USERS = "group_users";
    public static final String FROM_USER = "from_user";
    public static final String TO_USER = "to_user";
    public static final String USERID = "userid";
    public static final String TOKEN = "api_token";
    public static final String OTHER_ID = "other_id";
    public static final String TO_ANS = "answer";
    public static final String GROUP_NAME = "group_name";
    public static final String CREATED_BY = "created_by";
    public static final String url = "url";
    public static final String content_type = "content_type";
    public static final String media_type = "media_type";
    public static final String caption = "caption";
    public static final String thumbnail = "thumbnail";
    public static final String media_link = "media_link";
    public static final String media_time = "media_time";
    public static final String hotspot_id = "hotspot_id";
    public static String CATEGORY_ID = "category_id";

    //Add Memories API KEYS
    public static ApiClass mApiClass;
    public static String NEWPASSWORD = "new_password";
    public static String COUNTRY_CODE = "country_code";
    public static String OLDPASSWORD = "old_password";
    public static String LANG = "lang";
    public static String REQUEST_TYPE = "request_type";


    //***************************** CHAT CONSTANT KEYS *****************************
    public static String PAGE = "page";
    public static String OLDEmail = "old_email";
    public static String OTP = "otp";

    //*****************************

    public static ApiClass getmApiClass() {
        if (mApiClass == null) {
            mApiClass = new ApiClass();
        }
        return mApiClass;
    }

    public Map buildDefaultParams(Context mcontext) {
        Map defaults = new HashMap();
        defaults.put(DEVICE_TYPE, "ANDROID");
        defaults.put(DEVICE_ID, SavePref.getInstance(mcontext).getFirebase_DeviceKey());
        defaults.put(ApiClass.getmApiClass().FIREBASE_TOKEN,SavePref.getInstance(mcontext).getFirebase_DeviceKey());

        if (Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(Bundle_Identifier.ArabicLang)) {
            //if language is arabian
            defaults.put(LANG, "ar");
        } else {
            defaults.put(LANG, "en");
        }
        return defaults;
    }

}
