package com.app.khaleeji.chatModule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import Utility.SavePref;


public class DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VIRSION = 1;
    public static final String DATABASE_NAME = "Gulflink.sqlite";
    public static final String CONTACT_LIST_TABLE = "contactlist";
    public static final String _ID = "id";
    public static final String NAME = "name";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "username";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String COUNTRY_CODE = "country_code";
    public static final String IS_AVAILABLE = "is_available";
    public static final String THUMB = "thumb";
    public static final String PHONE = "phone";
    public static final String CHECKEDBOX = "checkedBox";
    public static final String EMAIL = "email";
    public static DbHelper dbHelper;
    public static String CHAT_TABLE = "tbl_Chat";
    public static String GROUP_CHAT_TABLE = "tbl_grp_Chat";
    public static String COMMON_CHAT_TABLE = "tbl_view_common";
    public static String MSG_ID = "msg_id";
    public static String FRIEND_ID = "friend_id";
    public static String MSG_BODY = "msg_body";
    public static String MSG_TIMESTAMP = "msg_timestamp";
    public static String MSG_TIME = "msg_time";
    public static String MSG_TYPE = "msg_type";
    public static String SENDING_TYPE = "sending_type";
    public static String MSG_STATUS = "msg_status";
    public static String MSG_THUMB_URL = "msg_thumb_url";
    public static String MSG_MAIN_URL = "msg_main_url";
    public static String MSG_SIZE = "msg_size";
    public static String MSG_DURATION = "msg_duration";
    public static String MSG_TEXT = "msg_text";
    public static String FRIEND_USERNAME = "friend_username";
    public static String ISSINGLETIME = "isSingleTime";
    public static String LOGIN_USER = "login_user";
    public static String GROUP_ID = "group_id";
    SQLiteDatabase database;
    Context context;
    String chatTable = "CREATE TABLE " + CHAT_TABLE + " (" + MSG_ID + " DOUBLE," + FRIEND_ID + " VARCHAR,"
            + MSG_BODY + " VARCHAR," + MSG_TIMESTAMP + " DOUBLE," + MSG_TIME + " VARCHAR," + MSG_TYPE
            + " VARCHAR," + SENDING_TYPE + " VARCHAR," + MSG_STATUS + " INTEGER, " + MSG_THUMB_URL + " VARCHAR, "
            + MSG_MAIN_URL + " VARCHAR, " + MSG_SIZE + " VARCHAR, " + MSG_DURATION + " VARCHAR, " + LOGIN_USER + " VARCHAR, " + MSG_TEXT + " VARCHAR, " + FRIEND_USERNAME + " VARCHAR," + ISSINGLETIME + " INTEGER)";
    String groupChatTable = "CREATE TABLE " + GROUP_CHAT_TABLE + " (" + MSG_ID + " DOUBLE," + FRIEND_ID + " VARCHAR," + MSG_BODY
            + " VARCHAR," + MSG_TIMESTAMP + " DOUBLE," + MSG_TIME + " VARCHAR," + MSG_TYPE + " VARCHAR," + SENDING_TYPE
            + " VARCHAR," + MSG_STATUS + " INTEGER, " + MSG_THUMB_URL + " VARCHAR, " + MSG_MAIN_URL + " VARCHAR, " + MSG_SIZE
            + " VARCHAR, " + MSG_DURATION + " VARCHAR, " + LOGIN_USER + " VARCHAR, " + GROUP_ID + " VARCHAR, " + MSG_TEXT + " VARCHAR, " + FRIEND_USERNAME + " VARCHAR) ";
    String contactTable = "create table " + CONTACT_LIST_TABLE +
            "( " + _ID + " integer primary key autoincrement," + NAME + "  text," +
            USER_ID + " text," +
            USER_NAME + " text," +
            FIRST_NAME + " text," +
            LAST_NAME + " text," +
            COUNTRY_CODE + " text," +
            IS_AVAILABLE + " boolean," +
            THUMB + " text," +
            PHONE + " text," +
            CHECKEDBOX + " boolean," +
            EMAIL + " text); ";
    String q1 = "select " + MSG_ID + "," + FRIEND_ID + "," + MSG_BODY + "," + MSG_TIMESTAMP + "," + MSG_TYPE + "," + SENDING_TYPE + "," + MSG_STATUS + "," +
            LOGIN_USER + ", '' as " + GROUP_ID + ", 'one' as TP" + " from " + CHAT_TABLE + " group by  " + FRIEND_ID;
    String q2 = "select " + MSG_ID + "," + FRIEND_ID + "," + MSG_BODY + "," + MSG_TIMESTAMP + "," + MSG_TYPE + "," + SENDING_TYPE + "," + MSG_STATUS + "," +
            LOGIN_USER + "," + MSG_TEXT + "," + GROUP_ID + ", 'GRP' as TP" + " from " + GROUP_CHAT_TABLE + " group by  " + GROUP_ID;
    String commonChatTable = "create view " + COMMON_CHAT_TABLE + " as " + q1 + " UNION ALL " + q2;
    private String DB_PATH = null;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VIRSION);

        DB_PATH = "/data/data/" + context.getPackageName().replace("/", "") + "/databases/";
        this.context = context;
    }

    public static DbHelper getIntance(Context context) {
        if (dbHelper == null) {
            dbHelper = new DbHelper(context);
        }
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(chatTable);
        db.execSQL(groupChatTable);
        db.execSQL(contactTable);
        db.execSQL(commonChatTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public SQLiteDatabase getWritableDatabase() {

        try {
            if (database == null) {
                database = super.getWritableDatabase();
            } else if (!database.isOpen()) {
                database = super.getWritableDatabase();
            }
        } catch (Exception e) {
            try {
                if (database != null) {
                    database.close();
                    database = super.getWritableDatabase();
                }
            } catch (Exception e1) {

            }
        }
        //database.close();
        return database;
    }

    public boolean openDataBase() throws SQLException {
        database = SQLiteDatabase.openDatabase(DB_PATH + DATABASE_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return database != null;
    }

    /*CHAT_TABLE + " (" + MSG_ID + " DOUBLE," + FRIEND_ID + " VARCHAR,"
                + MSG_BODY + " VARCHAR," + MSG_TIMESTAMP + " DOUBLE," + MSG_TIME + " VARCHAR," + MSG_TYPE
                + " VARCHAR," + SENDING_TYPE + " VARCHAR," + MSG_STATUS + " INTEGER, " + MSG_THUMB_URL + " VARCHAR, "
                + MSG_MAIN_URL + " VARCHAR, " + MSG_SIZE + " VARCHAR, " + MSG_DURATION + " VARCHAR, " + LOGIN_USER + " VARCHAR)";*/


    public void insertChat(ChatDbData chat) {
        try {
            database = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(MSG_ID, chat.MSG_ID);
            values.put(FRIEND_ID, chat.FRIEND_ID);
            values.put(MSG_BODY, chat.MSG_BODY);
            values.put(MSG_TIMESTAMP, chat.MSG_TIMESTAMP);
            values.put(MSG_TIME, chat.MSG_TIME);
            values.put(MSG_TYPE, chat.MSG_TYPE);
            values.put(SENDING_TYPE, chat.SENDING_TYPE);
            values.put(MSG_STATUS, chat.MSG_STATUS);
            values.put(MSG_THUMB_URL, chat.MSG_THUMB_URL);
            values.put(MSG_MAIN_URL, chat.MSG_MAIN_URL);
            values.put(MSG_SIZE, chat.MSG_SIZE);
            values.put(MSG_DURATION, chat.MSG_DURATION);
            values.put(MSG_TEXT, chat.MSG_TEXT);
            values.put(FRIEND_USERNAME, chat.FRIEND_USERNAME);
            values.put(ISSINGLETIME, chat.isSingleTime ? 1 : 0);
            String userId = getUserId();
            values.put(LOGIN_USER, userId);

            database.insert(CHAT_TABLE, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
    }

    public void insertGroupChat(ChatDbData chat) {
        try {
            database = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(MSG_ID, chat.MSG_ID);
            values.put(FRIEND_ID, chat.FRIEND_ID);
            values.put(MSG_BODY, chat.MSG_BODY);
            values.put(MSG_TIMESTAMP, chat.MSG_TIMESTAMP);
            values.put(MSG_TIME, chat.MSG_TIME);
            values.put(MSG_TYPE, chat.MSG_TYPE);
            values.put(SENDING_TYPE, chat.SENDING_TYPE);
            values.put(MSG_STATUS, chat.MSG_STATUS);
            values.put(MSG_THUMB_URL, chat.MSG_THUMB_URL);
            values.put(MSG_MAIN_URL, chat.MSG_MAIN_URL);
            values.put(MSG_SIZE, chat.MSG_SIZE);
            values.put(MSG_DURATION, chat.MSG_DURATION);
            values.put(GROUP_ID, chat.GROUP_ID);
            values.put(MSG_TEXT, chat.MSG_TEXT);
            values.put(FRIEND_USERNAME, chat.FRIEND_USERNAME);

            String userId = getUserId();
            values.put(LOGIN_USER, userId);

            database.insert(GROUP_CHAT_TABLE, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (database != null) {
                database.close();
            }
        }
    }


    public ArrayList<ChatDbData> getMessageList(String frnd_id, String message_type) {

        ArrayList<ChatDbData> chats = new ArrayList<>();
        try {

            database = this.getWritableDatabase();
            Cursor cSSID = database.rawQuery("SELECT * FROM " + CHAT_TABLE + " WHERE " + FRIEND_ID + "='" + frnd_id + "' AND " + LOGIN_USER + " = '" + getUserId() + "' AND " + MSG_TYPE + " = '" + message_type + "'", null);

            if (!database.isOpen()) {
                database = this.getWritableDatabase();
                cSSID = database.rawQuery("SELECT * FROM " + CHAT_TABLE + " WHERE " + FRIEND_ID + "='" + frnd_id + "' AND " + LOGIN_USER + " = '" + getUserId() + "' AND " + MSG_TYPE + " = '" + message_type + "'", null);
            }
            if (cSSID.moveToFirst()) {

                do {
                    ChatDbData chat = new ChatDbData();
                    chat.MSG_ID = cSSID.getDouble(cSSID.getColumnIndex(MSG_ID));
                    chat.MSG_BODY = cSSID.getString(cSSID.getColumnIndex(MSG_BODY));
                    chat.MSG_TIMESTAMP = cSSID.getDouble(cSSID.getColumnIndex(MSG_TIMESTAMP));
                    chat.MSG_TIME = cSSID.getString(cSSID.getColumnIndex(MSG_TIME));
                    chat.MSG_TYPE = cSSID.getString(cSSID.getColumnIndex(MSG_TYPE));
                    chat.SENDING_TYPE = cSSID.getString(cSSID.getColumnIndex(SENDING_TYPE));
                    chat.MSG_STATUS = cSSID.getInt(cSSID.getColumnIndex(MSG_STATUS));
                    chat.MSG_THUMB_URL = cSSID.getString(cSSID.getColumnIndex(MSG_THUMB_URL));
                    chat.MSG_MAIN_URL = cSSID.getString(cSSID.getColumnIndex(MSG_MAIN_URL));
                    chat.MSG_SIZE = cSSID.getString(cSSID.getColumnIndex(MSG_SIZE));
                    chat.MSG_DURATION = cSSID.getString(cSSID.getColumnIndex(MSG_DURATION));
                    chat.MSG_TEXT = cSSID.getString(cSSID.getColumnIndex(MSG_TEXT));
                    chat.FRIEND_USERNAME = cSSID.getString(cSSID.getColumnIndex(FRIEND_USERNAME));


                    chats.add(chat);
                } while (cSSID.moveToNext());
            }
            cSSID.close();

//        updateReadStatusFromUser(database, userid);
            return chats;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }

        }
        return chats;
    }

    public ArrayList<ChatDbData> getGroupMessageList(String group_id) {
        ArrayList<ChatDbData> chats = new ArrayList<>();
        try {

            database = this.getWritableDatabase();

            Cursor cSSID = database.rawQuery("SELECT * FROM " + GROUP_CHAT_TABLE + " WHERE " + GROUP_ID + "='" + group_id + "' AND " + LOGIN_USER + " = " + getUserId(), null);

            if (!database.isOpen()) {
                database = this.getWritableDatabase();
                cSSID = database.rawQuery("SELECT * FROM " + GROUP_CHAT_TABLE + " WHERE " + GROUP_ID + "='" + group_id + "' AND " + LOGIN_USER + " = " + getUserId(), null);
            }
            if (cSSID.moveToFirst()) {

                do {
                    ChatDbData chat = new ChatDbData();
                    chat.MSG_ID = cSSID.getDouble(cSSID.getColumnIndex(MSG_ID));
                    chat.MSG_BODY = cSSID.getString(cSSID.getColumnIndex(MSG_BODY));
                    chat.MSG_TIMESTAMP = cSSID.getDouble(cSSID.getColumnIndex(MSG_TIMESTAMP));
                    chat.MSG_TIME = cSSID.getString(cSSID.getColumnIndex(MSG_TIME));
                    chat.MSG_TYPE = cSSID.getString(cSSID.getColumnIndex(MSG_TYPE));
                    chat.SENDING_TYPE = cSSID.getString(cSSID.getColumnIndex(SENDING_TYPE));
                    chat.MSG_STATUS = cSSID.getInt(cSSID.getColumnIndex(MSG_STATUS));
                    chat.MSG_THUMB_URL = cSSID.getString(cSSID.getColumnIndex(MSG_THUMB_URL));
                    chat.MSG_MAIN_URL = cSSID.getString(cSSID.getColumnIndex(MSG_MAIN_URL));
                    chat.MSG_SIZE = cSSID.getString(cSSID.getColumnIndex(MSG_SIZE));
                    chat.MSG_DURATION = cSSID.getString(cSSID.getColumnIndex(MSG_DURATION));
                    chat.GROUP_ID = cSSID.getString(cSSID.getColumnIndex(GROUP_ID));
                    chat.MSG_TEXT = cSSID.getString(cSSID.getColumnIndex(MSG_TEXT));
                    chat.FRIEND_USERNAME = cSSID.getString(cSSID.getColumnIndex(FRIEND_USERNAME));

                    chats.add(chat);
                } while (cSSID.moveToNext());
            }
            cSSID.close();
            return chats;
//        updateReadStatusFromUser(database, userid);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return chats;
    }


    public ArrayList<ChatDbData> getChatList(String message_type) {

        ArrayList<ChatDbData> chats = new ArrayList<>();

        try {
            database = this.getWritableDatabase();
            String quary = "SELECT *" + " FROM " + CHAT_TABLE + " WHERE " + LOGIN_USER + " = '" + getUserId() + "' AND " + MSG_TYPE + "='" + message_type + "'" + " GROUP BY " + FRIEND_ID + " ORDER BY " + MSG_TIMESTAMP + " DESC";
            Log.e("query", quary + "");
            //Cursor cSSID = database.rawQuery("SELECT * FROM "+CHAT_TABLE+" group by "+FROM_USER_ID+" WHERE "+IS_SOCIAL+" = "+(isSocial?1:0)+" ORDER BY "+DATE+ " DESC", null);
            Cursor cSSID = database.rawQuery(quary, null);
            if (cSSID.moveToFirst()) {
                do {
                    ChatDbData chat = new ChatDbData();
                    chat.MSG_ID = cSSID.getDouble(cSSID.getColumnIndex(MSG_ID));
                    chat.FRIEND_ID = cSSID.getString(cSSID.getColumnIndex(FRIEND_ID));
                    chat.MSG_BODY = cSSID.getString(cSSID.getColumnIndex(MSG_BODY));
                    chat.MSG_TIMESTAMP = cSSID.getDouble(cSSID.getColumnIndex(MSG_TIMESTAMP));
                    chat.MSG_TIME = cSSID.getString(cSSID.getColumnIndex(MSG_TIME));
                    chat.MSG_TYPE = cSSID.getString(cSSID.getColumnIndex(MSG_TYPE));
                    chat.SENDING_TYPE = cSSID.getString(cSSID.getColumnIndex(SENDING_TYPE));
                    chat.MSG_STATUS = cSSID.getInt(cSSID.getColumnIndex(MSG_STATUS));
                    chat.MSG_THUMB_URL = cSSID.getString(cSSID.getColumnIndex(MSG_THUMB_URL));
                    chat.MSG_MAIN_URL = cSSID.getString(cSSID.getColumnIndex(MSG_MAIN_URL));
                    chat.MSG_SIZE = cSSID.getString(cSSID.getColumnIndex(MSG_SIZE));
                    chat.MSG_DURATION = cSSID.getString(cSSID.getColumnIndex(MSG_DURATION));
                    chat.LOGIN_USER = cSSID.getString(cSSID.getColumnIndex(LOGIN_USER));
                    chat.MSG_TEXT = cSSID.getString(cSSID.getColumnIndex(MSG_TEXT));
                    chat.FRIEND_USERNAME = cSSID.getString(cSSID.getColumnIndex(FRIEND_USERNAME));

                    chats.add(chat);
                } while (cSSID.moveToNext());
            }
            cSSID.close();

            database.close();
            return chats;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return chats;
    }

    public ChatDbData getGroupChatListBYGROUP(String groupId) {
        ChatDbData chat = null;

        try {
            database = this.getWritableDatabase();
            String quary = "SELECT *" + " FROM " + GROUP_CHAT_TABLE + " WHERE " + GROUP_ID + " ='" + groupId + "' AND " + LOGIN_USER + " =" + getUserId();
            //Cursor cSSID = database.rawQuery("SELECT * FROM "+CHAT_TABLE+" group by "+FROM_USER_ID+" WHERE "+IS_SOCIAL+" = "+(isSocial?1:0)+" ORDER BY "+DATE+ " DESC", null);
            Cursor cSSID = database.rawQuery("SELECT * FROM " + GROUP_CHAT_TABLE + " WHERE " + GROUP_ID + "='" + groupId + "' AND " + LOGIN_USER + " = " + getUserId(), null);
            if (cSSID.moveToFirst()) {
//            do {
                chat = new ChatDbData();
                chat.MSG_ID = cSSID.getDouble(cSSID.getColumnIndex(MSG_ID));
                chat.FRIEND_ID = cSSID.getString(cSSID.getColumnIndex(FRIEND_ID));
                chat.MSG_BODY = cSSID.getString(cSSID.getColumnIndex(MSG_BODY));
                chat.MSG_TIMESTAMP = cSSID.getDouble(cSSID.getColumnIndex(MSG_TIMESTAMP));
                chat.MSG_TIME = cSSID.getString(cSSID.getColumnIndex(MSG_TIME));
                chat.MSG_TYPE = cSSID.getString(cSSID.getColumnIndex(MSG_TYPE));
                chat.SENDING_TYPE = cSSID.getString(cSSID.getColumnIndex(SENDING_TYPE));
                chat.MSG_STATUS = cSSID.getInt(cSSID.getColumnIndex(MSG_STATUS));
                chat.MSG_THUMB_URL = cSSID.getString(cSSID.getColumnIndex(MSG_THUMB_URL));
                chat.MSG_MAIN_URL = cSSID.getString(cSSID.getColumnIndex(MSG_MAIN_URL));
                chat.MSG_SIZE = cSSID.getString(cSSID.getColumnIndex(MSG_SIZE));
                chat.MSG_DURATION = cSSID.getString(cSSID.getColumnIndex(MSG_DURATION));
                chat.LOGIN_USER = cSSID.getString(cSSID.getColumnIndex(LOGIN_USER));
                chat.GROUP_ID = cSSID.getString(cSSID.getColumnIndex(GROUP_ID));

//            } while (cSSID.moveToPrevious());
            }
            cSSID.close();

            database.close();
            return chat;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return chat;
    }

    public ArrayList<ChatDbData> getGroupChatList() {
        ArrayList<ChatDbData> chats = new ArrayList<>();

        database = this.getWritableDatabase();
        String quary = "SELECT *" + " FROM " + GROUP_CHAT_TABLE + " WHERE " + LOGIN_USER + " = " + getUserId() + " GROUP BY " + GROUP_ID + " ORDER BY " + MSG_TIMESTAMP + " DESC";
        //Cursor cSSID = database.rawQuery("SELECT * FROM "+CHAT_TABLE+" group by "+FROM_USER_ID+" WHERE "+IS_SOCIAL+" = "+(isSocial?1:0)+" ORDER BY "+DATE+ " DESC", null);
        Cursor cSSID = database.rawQuery(quary, null);
        if (cSSID.moveToFirst()) {
            do {
                ChatDbData chat = new ChatDbData();
                chat.MSG_ID = cSSID.getDouble(cSSID.getColumnIndex(MSG_ID));
                chat.FRIEND_ID = cSSID.getString(cSSID.getColumnIndex(FRIEND_ID));
                chat.MSG_BODY = cSSID.getString(cSSID.getColumnIndex(MSG_BODY));
                chat.MSG_TIMESTAMP = cSSID.getDouble(cSSID.getColumnIndex(MSG_TIMESTAMP));
                chat.MSG_TIME = cSSID.getString(cSSID.getColumnIndex(MSG_TIME));
                chat.MSG_TYPE = cSSID.getString(cSSID.getColumnIndex(MSG_TYPE));
                chat.SENDING_TYPE = cSSID.getString(cSSID.getColumnIndex(SENDING_TYPE));
                chat.MSG_STATUS = cSSID.getInt(cSSID.getColumnIndex(MSG_STATUS));
                chat.MSG_THUMB_URL = cSSID.getString(cSSID.getColumnIndex(MSG_THUMB_URL));
                chat.MSG_MAIN_URL = cSSID.getString(cSSID.getColumnIndex(MSG_MAIN_URL));
                chat.MSG_SIZE = cSSID.getString(cSSID.getColumnIndex(MSG_SIZE));
                chat.MSG_DURATION = cSSID.getString(cSSID.getColumnIndex(MSG_DURATION));
                chat.LOGIN_USER = cSSID.getString(cSSID.getColumnIndex(LOGIN_USER));
                chat.GROUP_ID = cSSID.getString(cSSID.getColumnIndex(GROUP_ID));

                chats.add(chat);
            } while (cSSID.moveToNext());
        }
        cSSID.close();

        database.close();
        return chats;
    }


    public ArrayList<ChatDbData> getCommonChatList() {
        ArrayList<ChatDbData> chats = new ArrayList<>();
        try {
            database = this.getWritableDatabase();
            String quary = "SELECT * FROM " + COMMON_CHAT_TABLE + " WHERE " + LOGIN_USER + " = " + getUserId() + " ORDER BY " + MSG_TIMESTAMP + " DESC";
            Cursor cSSID = database.rawQuery(quary, null);
            if (cSSID.moveToFirst()) {
                do {
                    ChatDbData chat = new ChatDbData();
                    chat.MSG_ID = cSSID.getDouble(cSSID.getColumnIndex(MSG_ID));
                    chat.FRIEND_ID = cSSID.getString(cSSID.getColumnIndex(FRIEND_ID));
                    chat.MSG_BODY = cSSID.getString(cSSID.getColumnIndex(MSG_BODY));
                    chat.MSG_TIMESTAMP = cSSID.getDouble(cSSID.getColumnIndex(MSG_TIMESTAMP));
                    chat.MSG_TYPE = cSSID.getString(cSSID.getColumnIndex(MSG_TYPE));
                    chat.SENDING_TYPE = cSSID.getString(cSSID.getColumnIndex(SENDING_TYPE));
                    chat.MSG_STATUS = cSSID.getInt(cSSID.getColumnIndex(MSG_STATUS));
                    chat.LOGIN_USER = cSSID.getString(cSSID.getColumnIndex(LOGIN_USER));
                    chat.GROUP_ID = cSSID.getString(cSSID.getColumnIndex(GROUP_ID));
                    chat.MSG_TEXT = cSSID.getString(cSSID.getColumnIndex(MSG_TEXT));
                    chat.CHAT_TYPE = cSSID.getString(cSSID.getColumnIndex("TP"));

                    chats.add(chat);
                } while (cSSID.moveToNext());
            }
            cSSID.close();

            database.close();
            return chats;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return chats;
    }


    public ArrayList<ChatDbData> searchInDBByName(String text, String message_type) {

        ArrayList<ChatDbData> chats = new ArrayList<>();
        text = "%" + text + "%";
        try {
            database = this.getWritableDatabase();
            String quary = "SELECT * FROM " + CHAT_TABLE + " WHERE " + LOGIN_USER + " = " + getUserId() + " AND (" + FRIEND_USERNAME + " LIKE " + " '" + text + "') " + " AND " + MSG_TYPE + " = '" + message_type + "'" + " GROUP BY " + FRIEND_ID + " ORDER BY " + MSG_TIMESTAMP + " DESC";
            Log.e("query", quary);
            Cursor cSSID = database.rawQuery(quary, null);
            if (cSSID.moveToFirst()) {
                do {
                    ChatDbData chat = new ChatDbData();
                    chat.MSG_ID = cSSID.getDouble(cSSID.getColumnIndex(MSG_ID));
                    chat.FRIEND_ID = cSSID.getString(cSSID.getColumnIndex(FRIEND_ID));
                    chat.MSG_BODY = cSSID.getString(cSSID.getColumnIndex(MSG_BODY));
                    chat.MSG_TIMESTAMP = cSSID.getDouble(cSSID.getColumnIndex(MSG_TIMESTAMP));
                    chat.MSG_TIME = cSSID.getString(cSSID.getColumnIndex(MSG_TIME));
                    chat.MSG_TYPE = cSSID.getString(cSSID.getColumnIndex(MSG_TYPE));
                    chat.SENDING_TYPE = cSSID.getString(cSSID.getColumnIndex(SENDING_TYPE));
                    chat.MSG_STATUS = cSSID.getInt(cSSID.getColumnIndex(MSG_STATUS));
                    chat.MSG_THUMB_URL = cSSID.getString(cSSID.getColumnIndex(MSG_THUMB_URL));
                    chat.MSG_MAIN_URL = cSSID.getString(cSSID.getColumnIndex(MSG_MAIN_URL));
                    chat.MSG_SIZE = cSSID.getString(cSSID.getColumnIndex(MSG_SIZE));
                    chat.MSG_DURATION = cSSID.getString(cSSID.getColumnIndex(MSG_DURATION));
                    chat.LOGIN_USER = cSSID.getString(cSSID.getColumnIndex(LOGIN_USER));
                    chat.FRIEND_USERNAME = cSSID.getString(cSSID.getColumnIndex(FRIEND_USERNAME));
                    chat.MSG_TEXT = cSSID.getString(cSSID.getColumnIndex(MSG_TEXT));

                    chats.add(chat);
                } while (cSSID.moveToNext());
            }
            cSSID.close();

            database.close();
            return chats;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return chats;

    }

    public ArrayList<ChatDbData> searchInDBByMSG(String text, String message_type) {

        ArrayList<ChatDbData> chats = new ArrayList<>();
        text = "%" + text + "%";
        try {
            database = this.getWritableDatabase();
            String quary = "SELECT * FROM " + CHAT_TABLE + " WHERE " + LOGIN_USER + " = " + getUserId() + " AND (" + MSG_TEXT + " LIKE " + "'" + text + "') " + " AND " + MSG_TYPE + " = '" + message_type + "'" + " GROUP BY " + FRIEND_ID + " ORDER BY " + MSG_TIMESTAMP + " DESC";
            Log.e("query", quary);
            Cursor cSSID = database.rawQuery(quary, null);
            if (cSSID.moveToFirst()) {
                do {
                    ChatDbData chat = new ChatDbData();
                    chat.MSG_ID = cSSID.getDouble(cSSID.getColumnIndex(MSG_ID));
                    chat.FRIEND_ID = cSSID.getString(cSSID.getColumnIndex(FRIEND_ID));
                    chat.MSG_BODY = cSSID.getString(cSSID.getColumnIndex(MSG_BODY));
                    chat.MSG_TIMESTAMP = cSSID.getDouble(cSSID.getColumnIndex(MSG_TIMESTAMP));
                    chat.MSG_TIME = cSSID.getString(cSSID.getColumnIndex(MSG_TIME));
                    chat.MSG_TYPE = cSSID.getString(cSSID.getColumnIndex(MSG_TYPE));
                    chat.SENDING_TYPE = cSSID.getString(cSSID.getColumnIndex(SENDING_TYPE));
                    chat.MSG_STATUS = cSSID.getInt(cSSID.getColumnIndex(MSG_STATUS));
                    chat.MSG_THUMB_URL = cSSID.getString(cSSID.getColumnIndex(MSG_THUMB_URL));
                    chat.MSG_MAIN_URL = cSSID.getString(cSSID.getColumnIndex(MSG_MAIN_URL));
                    chat.MSG_SIZE = cSSID.getString(cSSID.getColumnIndex(MSG_SIZE));
                    chat.MSG_DURATION = cSSID.getString(cSSID.getColumnIndex(MSG_DURATION));
                    chat.LOGIN_USER = cSSID.getString(cSSID.getColumnIndex(LOGIN_USER));
                    chat.FRIEND_USERNAME = cSSID.getString(cSSID.getColumnIndex(FRIEND_USERNAME));
                    chat.MSG_TEXT = cSSID.getString(cSSID.getColumnIndex(MSG_TEXT));

                    chats.add(chat);
                } while (cSSID.moveToNext());
            }
            cSSID.close();

            database.close();
            return chats;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return chats;

    }

    public void updateStatus(ChatDbData chat) {

        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MSG_STATUS, chat.MSG_STATUS);
        long count = database.update(CHAT_TABLE, values, MSG_ID + "=" + chat.MSG_ID, null);
    }

    public void updateGroupStatus(ChatDbData chat) {

        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MSG_STATUS, chat.MSG_STATUS);
        long count = database.update(GROUP_CHAT_TABLE, values, MSG_ID + "=" + chat.MSG_ID, null);

//        dbHelper.close();
    }

    public void updateMsgBody(ChatDbData chatData) {

        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MSG_BODY, chatData.MSG_BODY);
        values.put(MSG_MAIN_URL, chatData.MSG_MAIN_URL);
        values.put(MSG_THUMB_URL, chatData.MSG_THUMB_URL);
        values.put(MSG_DURATION, chatData.MSG_DURATION);
        values.put(MSG_SIZE, chatData.MSG_SIZE);
        long count = database.update(CHAT_TABLE, values, MSG_ID + "=" + chatData.MSG_ID, null);

//        dbHelper.close();
    }

    public void updateGroupMsgBody(ChatDbData chatData) {

        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MSG_BODY, chatData.MSG_BODY);
        values.put(MSG_MAIN_URL, chatData.MSG_MAIN_URL);
        values.put(MSG_THUMB_URL, chatData.MSG_THUMB_URL);
        values.put(MSG_DURATION, chatData.MSG_DURATION);
        values.put(MSG_SIZE, chatData.MSG_SIZE);
        long count = database.update(GROUP_CHAT_TABLE, values, MSG_ID + "=" + chatData.MSG_ID, null);

//        dbHelper.close();
    }

    public void renameGroupData(String id, String name, String image, String thumb_image) {

        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        ArrayList<ChatDbData> chatData = getGroupMessageList(id);

        for (int i = 0; i < chatData.size(); i++) {
            ChatMessage chatClass = new Gson().fromJson(chatData.get(i).MSG_BODY, ChatMessage.class);
            chatClass.name = name;
            chatClass.thumb_image = thumb_image;
            chatClass.image = image;
            chatData.get(i).MSG_BODY = new Gson().toJson(chatClass);
            values.put(MSG_BODY, chatData.get(i).MSG_BODY);

            long count = database.update(GROUP_CHAT_TABLE, values, MSG_ID + "=" + chatData.get(i).MSG_ID, null);
        }


//        dbHelper.close();
    }

    private ChatMessage getGroupNameFromGroup(String id) {

        return null;
    }


    public void deleteAllCommonChatList() {
        database = this.getWritableDatabase();
        String delete = "SELECT FROM " + COMMON_CHAT_TABLE;
        database.execSQL(delete);
//        database.close();
    }

    public void deleteGroupMsgRow(String groupid) {
        database = this.getWritableDatabase();
        String delete = "DELETE FROM " + GROUP_CHAT_TABLE + " where " + GROUP_ID + " = '" + groupid + "' AND " + MSG_TYPE + " = " + "'contact_group'";
        database.execSQL(delete);
//        database.close();
    }

    public void deleteAllGroupChatList() {
        database = this.getWritableDatabase();
        String delete = "DELETE FROM " + GROUP_CHAT_TABLE;
        database.execSQL(delete);
//        database.close();
    }

    public void deteteGroupChat(String id) {
        database = this.getWritableDatabase();
        String delete = "DELETE FROM " + GROUP_CHAT_TABLE + " where " + GROUP_ID + " = '" + id + "'";
        database.execSQL(delete);
//        database.close();
    }


    public void deteteSingleChat(String id, double messageid) {
        database = this.getWritableDatabase();
        String delete = "DELETE FROM " + CHAT_TABLE + " where " + FRIEND_ID + " = '" + id + "' AND " + LOGIN_USER + " = '" + getUserId() + "' AND " + MSG_ID + " = " + messageid;
        database.execSQL(delete);
//        database.close();
    }


    public void deteteChat(String id) {
        database = this.getWritableDatabase();
        String delete = "DELETE FROM " + CHAT_TABLE + " where " + FRIEND_ID + " = '" + id + "' AND " + LOGIN_USER + " = " + getUserId();
        database.execSQL(delete);
//        database.close();
    }



    public void deteteMedia(String id) {
        Log.e("DBhelper","FRIEND_ID : "+id);
        database = this.getWritableDatabase();

        String delete = "DELETE FROM " + CHAT_TABLE + " where " + FRIEND_ID + " = '" + id + "' AND " + LOGIN_USER + " = '" + getUserId() + "' AND " + ISSINGLETIME + " = 1";
       // String delete = "DELETE FROM " + CHAT_TABLE + " where " + FRIEND_ID + " = '" + id + "' AND " + LOGIN_USER + " = " + getUserId();
        database.execSQL(delete);
//        database.close();
    }

    private String getUserId() {
        String userId = SavePref.getInstance(context).getUserdetail().getId().toString();
        return userId;
    }


    public void deleteAllContactList() {
        database = this.getWritableDatabase();
        String delete = "DELETE FROM " + CONTACT_LIST_TABLE;
        database.execSQL(delete);
//        database.close();
    }

    private boolean databaseExists() {
        String dbFilePath = DB_PATH + DATABASE_NAME;
        File dbFile = new File(dbFilePath);


        return dbFile.exists();
    }

    public void backupToCache() throws IOException {


       /* this.getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + TABLE_RECEIVED);
        this.getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + TABLE_SENT);
        this.getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE_CHAT);*/


        if (databaseExists()) {
            context.deleteDatabase(DB_PATH + DATABASE_NAME);

            System.out.println("Database deleted Path" + context.getDatabasePath(DATABASE_NAME));
        } else {
            this.getWritableDatabase();

        }


        System.out.println("Database default_img Path" + context.getDatabasePath(DATABASE_NAME));

        DB_PATH = "/data/data/" + context.getPackageName().replace("/", "") + "/databases/";

        System.out.println("Database myPath" + DB_PATH + DATABASE_NAME);

        InputStream input = null;
        FileOutputStream output = null;

        byte[] buffer;
        try {
            File databaseFile = new File(DB_PATH + DATABASE_NAME);

            System.out.println("database  restore helper " + " database created" + databaseFile.getAbsolutePath());
            System.out.println("database restore  SD path " + Environment.getExternalStorageDirectory() + "/" + "Gulflink.sqlite");

            System.out.println("database  restore exist first " + databaseFile.exists());

            if (databaseFile.exists()) {
                System.out.println("database  restore exist " + databaseFile.exists());
                databaseFile.delete();
            }
            databaseFile.createNewFile();
            output = new FileOutputStream(databaseFile);
            input = new FileInputStream(new File(Environment.getExternalStorageDirectory() + "/" + "Mcloud.sqlite"));
            buffer = new byte[4096];
            int bytesReadCount;
            while ((bytesReadCount = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesReadCount);
            }
            output.flush();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {

            if (input != null) {

                input.close();

            }

            if (output != null) {

                output.close();

            }

        }
    }

    public void createContactTable() {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL(contactTable);
//        db.close();
    }


}
