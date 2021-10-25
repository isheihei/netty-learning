package cn.isheihei.chatroom.message;

import lombok.Data;
import lombok.ToString;

import java.util.Set;

@Data
@ToString(callSuper = true)
public class GroupMembersResponseMessage extends Message {

    private Set<String> members;
    private boolean success;
    private String reason;

    public GroupMembersResponseMessage(boolean success, String reason, Set<String> members) {
        this.success = success;
        this.reason = reason;
        this.members = members;
    }

    @Override
    public int getMessageType() {
        return GroupMembersResponseMessage;
    }
}
