
package com.app.khaleeji.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotficationsSettings {

    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message_original")
    @Expose
    private String messageOriginal;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessageOriginal() {
        return messageOriginal;
    }

    public void setMessageOriginal(String messageOriginal) {
        this.messageOriginal = messageOriginal;
    }
    public class Data {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("user_id")
        @Expose
        private Integer userId;
        @SerializedName("gulflink_notification")
        @Expose
        private Integer gulflinkNotification;
        @SerializedName("g_snap_notification")
        @Expose
        private Integer gSnapNotification;
        @SerializedName("live_notification")
        @Expose
        private Integer liveNotification;
        @SerializedName("friend_request_notification")
        @Expose
        private Integer friendRequestNotification;
        @SerializedName("chat_notification")
        @Expose
        private Integer chatNotification;
        @SerializedName("allowFrnd_groupMessages")
        @Expose
        private Integer allowFrnd_groupMessages;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("created_at")
        @Expose
        private String createdAt;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getGulflinkNotification() {
            return gulflinkNotification;
        }

        public void setGulflinkNotification(Integer gulflinkNotification) {
            this.gulflinkNotification = gulflinkNotification;
        }

        public Integer getGSnapNotification() {
            return gSnapNotification;
        }

        public void setGSnapNotification(Integer gSnapNotification) {
            this.gSnapNotification = gSnapNotification;
        }

        public Integer getLiveNotification() {
            return liveNotification;
        }

        public void setLiveNotification(Integer liveNotification) {
            this.liveNotification = liveNotification;
        }

        public Integer getFriendRequestNotification() {
            return friendRequestNotification;
        }

        public void setFriendRequestNotification(Integer friendRequestNotification) {
            this.friendRequestNotification = friendRequestNotification;
        }

        public Integer getChatNotification() {
            return chatNotification;
        }

        public void setChatNotification(Integer chatNotification) {
            this.chatNotification = chatNotification;
        }

        public Integer getAllowFrnd_groupMessages() {
            return allowFrnd_groupMessages;
        }

        public void setAllowFrnd_groupMessages(Integer allowFrnd_groupMessages) {
            this.allowFrnd_groupMessages = allowFrnd_groupMessages;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

    }
}
