package Constants;

import com.app.khaleeji.R;



public class AppConstants {

    public static String strAllTab = "all";
    public static String strFriendTab = "friends";
    public static boolean loadedHomeFriendTab;
    public static boolean loadedHomeAllTab;
    public static boolean loadedProfileTab;
    public static boolean loadedNotificationTab;

    public static final String MEDIA_TYPE_TEXT = "TEXT";
    public static final String MEDIA_TYPE_ONE_TIME_IMAGE = "ONETIMEIMAGE";
    public static final String MEDIA_TYPE_ONE_TIME_VIDEO = "ONETIMEVIDEO";
    public static final String MEDIA_TYPE_PHOTO = "PHOTO";
    public static final String MEDIA_TYPE_AUDIO = "AUDIO";
    public static final String MEDIA_TYPE_VIDEO = "VIDEO";
    public static final String MEDIA_TYPE_COMMENT = "COMMENT";
    public static final String TYPE_PHOTO = "##media##image##";
    public static final String TYPE_VIDEO = "##media##video##";
    public static final String TYPE_COMMENT_PHOTO = "##comment##media##image##";
    public static final String TYPE_COMMENT_VIDEO = "##comment##media##video##";
    public static final String TYPE_AUDIO =  "##media##audio##";


    public static final String ON_LONG_CLICK = "long_click";

    public static final int MEDIA_TYPE_SENDER_TEXT = 1;
    public static final int MEDIA_TYPE_RECEIVER_TEXT = 2;
    public static final int MEDIA_TYPE_SENDER_ONE_TIME_IMAGE = 3;
    public static final int MEDIA_TYPE_RECEIVER_ONE_TIME_IMAGE = 4;
    public static final int MEDIA_TYPE_SENDER_ONE_TIME_VIDEO = 5;
    public static final int MEDIA_TYPE_RECEIVER_ONE_TIME_VIDEO = 6;
    public static final int MEDIA_TYPE_SENDER_PHOTO = 7;
    public static final int MEDIA_TYPE_RECEIVER_PHOTO = 8;
    public static final int MEDIA_TYPE_SENDER_AUDIO = 9;
    public static final int MEDIA_TYPE_RECEIVER_AUDIO = 10;
    public static final int MEDIA_TYPE_SENDER_VIDEO = 11;
    public static final int MEDIA_TYPE_RECEIVER_VIDEO = 12;
    public static final int MEDIA_TYPE_SENDER_COMMENT = 31;
    public static final int MEDIA_TYPE_RECEIVER_COMMENT = 32;


    public static final int ITEM_TYPE_DAILY = 11;
    public static final int ITEM_TYPE_HOTSPOT = 12;
    public static final int ITEM_TYPE_MEMORY = 13;
    public static final String DEFAULT_IMAGE = "https://thegulflink.com/public/default_img_img.png";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_LOCATION= "location";
    public static final String ACTION_DELETE= "delete";
    public static final String ACTION_VIEWS= "views";

    public static final int TYPE_LIKES = 15;
    public static final int TYPE_COMMENTS = 16;
    public static final int TYPE_VIEWS = 17;
    public static final int TYPE_CIRCLE_IMG = 18;
    public static final int TYPE_POST_IMG = 19;
    public static final int TYPE_ADD_FRIEND = 20;
    public static final int TYPE_UNFRIEND = 21;
    public static final int TYPE_CANCELFRIEND = 22;
    public static final int TYPE_MORE_REMOVE = 23;
    public static final int TYPE_MORE_REPORT = 24;
    public static final int TYPE_CLOSE = 25;
    public static final int TYPE_PROFILE = 26;

    public static final String[] COUNTRY_CODE = {"+973", "+964", "+965", "+968", "+974", "+966", "+971","+967"};
    public  static final int[] FLAGS = {R.drawable.bahrain_flag, R.drawable.iraq_flag, R.drawable.kuwait_flag, R.drawable.oman_flag, R.drawable.qatar_flag,
            R.drawable.saudi_flag, R.drawable.ua_flag, R.drawable.yemen_flag};
    public static final int[] COUNTRY_NAME = {R.string.bahrain, R.string.iraq , R.string.kuwait, R.string.oman, R.string.qatar, R.string.saudi, R.string.united_arab,R.string.yemen};
    public static final String  SEPERATOR = "|$@#GULFLINK-NHZ#@$|";

    public static final int RESTAURANT = 1;
    public static final int CAFE = 2;
    public static final int SHOPPING = 4;
    public static final int HOTEL = 5;
    public static final int OUTDOOR = 6;
    public static final int FLARE = 3; // dummy

}
