package cn.mobcommu.zim.smack;

import cn.mobcommu.zim.bean.ChatUser;
import cn.mobcommu.zim.constant.Constant;
import cn.mobcommu.zim.util.DBHelper;
import cn.mobcommu.zim.util.LoginHelper;

import com.orhanobut.logger.Logger;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;

/**
 * 多人聊天邀请监听
 */
public class MultiChatInvitationListener implements InvitationListener {

    @Override
    public void invitationReceived(XMPPConnection conn, MultiUserChat room, String inviter,
                                   String reason, String password, Message message) {

        try {
            room.join(LoginHelper.getUser().getNickname());
            SmackMultiChatManager.saveMultiChat(room);
            SmackListenerManager.addMultiChatMessageListener(room);

            String friendUserName = room.getRoom();
            int idx = friendUserName.indexOf(Constant.MULTI_CHAT_ADDRESS_SPLIT);
            String friendNickName = friendUserName.substring(0, idx);
            ChatUser chatUser = new ChatUser(friendUserName, friendNickName, true);
            DBHelper.getInstance().getSQLiteDB().save(chatUser);
        } catch (Exception e) {
            Logger.e(e, "join multiChat failure on invitationReceived");
        }
    }
}
