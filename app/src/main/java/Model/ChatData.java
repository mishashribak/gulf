package Model;

public class ChatData {

    private String mediaType;
    private String thumbImage;
    private String bodyText;
    private String date;
    private String isRead;
    private String oneTimeMediaStatus;
    private String profilePic;
    private String senderId;
    private String senderName;

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getBodyText() {
        return bodyText;
    }

    public String getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(String thumbImage) {
        this.thumbImage = thumbImage;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getOneTimeMediaStatus() {
        return oneTimeMediaStatus;
    }

    public void setOneTimeMediaStatus(String oneTimeMediaStatus) {
        this.oneTimeMediaStatus = oneTimeMediaStatus;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
