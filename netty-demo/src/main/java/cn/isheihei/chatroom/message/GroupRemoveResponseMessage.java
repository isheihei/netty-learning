package cn.isheihei.chatroom.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class GroupRemoveResponseMessage extends AbstractResponseMessage{
    public GroupRemoveResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    @Override
    public int getMessageType() {
        return GroupRemoveResponseMessage;
    }
}
