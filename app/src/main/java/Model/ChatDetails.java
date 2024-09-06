package Model;

public class ChatDetails {

    private String friendId;
    private String friendName;
    private String nodeName;
    private Boolean isFromGroup;
    private Boolean isPhotoClicked;

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Boolean getFromGroup() {
        return isFromGroup;
    }

    public void setFromGroup(Boolean fromGroup) {
        isFromGroup = fromGroup;
    }

    public Boolean getPhotoClicked() {
        return isPhotoClicked;
    }

    public void setPhotoClicked(Boolean photoClicked) {
        isPhotoClicked = photoClicked;
    }
}
