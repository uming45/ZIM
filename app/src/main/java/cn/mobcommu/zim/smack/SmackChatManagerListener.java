package cn.mobcommu.zim.smack;

import cn.mobcommu.zim.bean.ChatMessage;
import cn.mobcommu.zim.constant.Constant;
import cn.mobcommu.zim.constant.MessageType;
import cn.mobcommu.zim.util.DBHelper;
import cn.mobcommu.zim.util.LoginHelper;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Smack普通消息监听处理
 */
public class SmackChatManagerListener implements ChatManagerListener {
    private static final String PATTERN = "[a-zA-Z0-9_-]+@";
    private String mMeNickName = LoginHelper.getUser().getNickname();

    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {

        chat.addMessageListener(new ChatMessageListener() {

            @Override
            public void processMessage(Chat chat, Message message) {
                //不会收到自己发送过来的消息
                Logger.d(message.toString());
                String from = message.getFrom(); //消息发送人，格式:xx@xx.xx.xx.xx/Smack
                if(from.contains(Constant.MULTI_CHAT_ADDRESS_SPLIT)) {
                    return;
                }

                String to = message.getTo(); //消息接收人(当前登陆用户)，格式:xx@xx.xx.xx.xx/Smack
                Matcher matcherFrom = Pattern.compile(PATTERN).matcher(from);
                Matcher matcherTo = Pattern.compile(PATTERN).matcher(to);

                if(matcherFrom.find() && matcherTo.find()) {
                    try {
                        String fromUser = matcherFrom.group(0);
                        fromUser = fromUser.substring(0, fromUser.length() - 1);//去掉@
                        String toUser = matcherTo.group(0);
                        toUser = toUser.substring(0, toUser.length() - 1);//去掉@
                        JSONObject json = new JSONObject(message.getBody());

                        ChatMessage chatMessage = new ChatMessage(MessageType.MESSAGE_TYPE_TEXT.value(), false);
                        chatMessage.setFriendUsername(fromUser);
                        chatMessage.setFriendNickname(json.optString(ChatMessage.KEY_FROM_NICKNAME));
                        chatMessage.setMeUsername(toUser);
                        chatMessage.setMeNickname(mMeNickName);
                        chatMessage.setContent(json.optString(ChatMessage.KEY_MESSAGE_CONTENT));
                        chatMessage.setMulti(false);

                        DBHelper.getInstance().getSQLiteDB().save(chatMessage);
                        EventBus.getDefault().post(chatMessage);
                    } catch (Exception e) {
                        Logger.e(e, "发送的消息格式不正确");
                    }
                } else {
                    Logger.e("发送人格式不正确");
                }
            }
        });
    }
}
