package cn.isheihei.chatroom.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class GroupRemoveRequestMessage extends Message{
    private String groupName;
    public GroupRemoveRequestMessage(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public int getMessageType() {
        return GroupRemoveRequestMessage;
    }
}
