package com.app.khaleeji.chatModule;

/**
 * Created by Dcube on 04-06-2018.
 */

public class SlctdChatDummyModel {

    private String frnd_id;
    private String frnd_name;
    private String frnd_img ;
    private String frnd_address;
    private String frnd_privacy;

    ChatDbData chatDbData;

    public ChatDbData getChatDbData() {
        return chatDbData;
    }

    public void setChatDbData(ChatDbData chatDbData) {
        this.chatDbData = chatDbData;
    }

    public String getFrnd_id() {
        return frnd_id;
    }

    public void setFrnd_id(String frnd_id) {
        this.frnd_id = frnd_id;
    }

    public String getFrnd_name() {
        return frnd_name;
    }

    public void setFrnd_name(String frnd_name) {
        this.frnd_name = frnd_name;
    }

    public String getFrnd_img() {
        return frnd_img;
    }

    public void setFrnd_img(String frnd_img) {
        this.frnd_img = frnd_img;
    }

    public String getFrnd_address() {
        return frnd_address;
    }

    public void setFrnd_address(String frnd_address) {
        this.frnd_address = frnd_address;
    }

    public String getFrnd_privacy() {
        return frnd_privacy;
    }

    public void setFrnd_privacy(String frnd_privacy) {
        this.frnd_privacy = frnd_privacy;
    }
}
