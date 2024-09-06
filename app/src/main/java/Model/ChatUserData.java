package Model;

public class ChatUserData {
    private UserData User1 = new UserData();
    private UserData User2 = new UserData();
    private String roomId;
    private boolean isChecked = false;
    private boolean isFromGroup = false;
    private LastMessage lastMessage = new LastMessage();
    private String createdAt;

    public boolean isFromGroup() {
        return isFromGroup;
    }

    public void setFromGroup(boolean fromGroup) {
        isFromGroup = fromGroup;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public LastMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(LastMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    public UserData getUser1() {
        return User1;
    }

    public void setUser1(UserData user1) {
        User1 = user1;
    }

    public UserData getUser2() {
        return User2;
    }

    public void setUser2(UserData user2) {
        User2 = user2;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
