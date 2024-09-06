package com.app.khaleeji.chatModule;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by user4 on 9/8/16.
 */
public class ChatMessage implements Serializable {

    @SerializedName("message")
    public String message;

    @SerializedName("ReceiverName")
    public String ReceiverName;

    @SerializedName("ReceiverJID")
    public String ReceiverJID;

    @SerializedName("ReceiverPhoto")
    public String ReceiverPhoto;

    @SerializedName("ReceiverId")
    public String ReceiverId;

    @SerializedName("SenderId")
    public String SenderId;

    @SerializedName("SenderJID")
    public String SenderJID;

    @SerializedName("SenderName")
    public String SenderName;

    @SerializedName("SenderPhoto")
    public String SenderPhoto;

    @SerializedName("message_type")
    public String message_type;

    @SerializedName("timestamp")
    public String timestamp;

    @SerializedName("SenderPrivacy")
    public String SenderPrivacy;

    @SerializedName("ReceiverAddress")
    public String ReceiverAddress;

    @SerializedName("ReceiverPrivacy")
    public String ReceiverPrivacy;

    @SerializedName("SenderAddress")
    public String SenderAddress;

    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("thumb_image")
    public String thumb_image;

    @SerializedName("image")
    public String image;

    @SerializedName("isSingleTime")
    public boolean isSingleTime;

    @SerializedName("Sender_request_accept")
    public String Sender_request_accept;
    @SerializedName("Sender_all_other_to_seeprofile")
    public String Sender_all_other_to_seeprofile;
    @SerializedName("Receiver_request_accept")
    public String Receiver_request_accept;
    @SerializedName("Receiver_all_other_to_seeprofile")
    public String Receiver_all_other_to_seeprofile;


}