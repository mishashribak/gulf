package com.app.khaleeji.chatModule;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by user47 on 7/7/17.
 */
public class ChatDbData implements Serializable {

    @SerializedName("msg_id")
    public double MSG_ID;

    @SerializedName("friend_id")
    public String FRIEND_ID;

    @SerializedName("msg_body")
    public String MSG_BODY;

    @SerializedName("msg_timestamp")
    public double MSG_TIMESTAMP;

    @SerializedName("msg_time")
    public String MSG_TIME;

    @SerializedName("message_type")
    public String MSG_TYPE;

    @SerializedName("sending_type")
    public String SENDING_TYPE;

    @SerializedName("msg_status")
    public int MSG_STATUS;
    //MSG_STATUS_CREATED=0;
//    MSG_STATUS_UPLOADING=1;
//    MSG_STATUS_UPLOADED=2;
//    MSG_STATUS_SENT=3;
//    MSG_STATUS_RECEIVED=4;
//    MSG_STATUS_READ=5;


    @SerializedName("msg_thumb_url")
    public String MSG_THUMB_URL;

    @SerializedName("msg_main_url")
    public String MSG_MAIN_URL;

    @SerializedName("msg_size")
    public String MSG_SIZE;

    @SerializedName("msg_duration")
    public String MSG_DURATION;

    @SerializedName("login_user")
    public String LOGIN_USER;

    @SerializedName("group_id")
    public String GROUP_ID;
    @SerializedName("msg_text")
    public String MSG_TEXT;
    @SerializedName("friend_username")
    public String FRIEND_USERNAME;

    @SerializedName("isSingleTime")
    public boolean isSingleTime;

    public boolean is_audio_playing;
    public int audio_progress = 0;
    public boolean is_Show_number;
    public String CHAT_TYPE;

    @Override
    public boolean equals(Object obj) {
        boolean sameSame = false;

        if (obj != null && obj instanceof ChatDbData) {
            //for match only student rollno
            //sameSame = this.s_rollNo == ((Student) object).s_rollNo;

            //for match only student first name
            sameSame = this.MSG_ID == ((ChatDbData) obj).MSG_ID;

        }

        return sameSame;
    }

}
