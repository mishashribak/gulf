package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupData implements Serializable {

    private GroupInfo GroupInfo = new GroupInfo();
    private List<UserData> GroupMembers = new ArrayList<>();
    private LastMessage lastMsg = new LastMessage();

    public Model.GroupInfo getGroupInfo() {
        return GroupInfo;
    }

    public void setGroupInfo(Model.GroupInfo groupInfo) {
        GroupInfo = groupInfo;
    }

    public List<UserData> getGroupMembers() {
        return GroupMembers;
    }

    public void setGroupMembers(List<UserData> groupMembers) {
        GroupMembers = groupMembers;
    }


    public LastMessage getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(LastMessage lastMsg) {
        this.lastMsg = lastMsg;
    }
}
