package cn.mobcommu.zim.activity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.json.JSONObject;

import android.os.Bundle;

import cn.mobcommu.zim.activity.base.BaseChatActivity;
import cn.mobcommu.zim.bean.ChatMessage;
import cn.mobcommu.zim.constant.KeyBoardMoreFunType;
import cn.mobcommu.zim.smack.SmackManager;
import cn.mobcommu.util.ValueUtil;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import com.orhanobut.logger.Logger;

import java.io.File;

/**
 * 多人聊天
 */
public class MultiChatActivity extends BaseChatActivity {
    /**
     * 多人聊天对象
     */
    private MultiUserChat mMultiUserChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(cn.mobcommu.zim.R.layout.activity_multichat_layout);
        mMultiUserChat = SmackManager.getInstance().getMultiChat(mChatUser.getFriendUsername());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveChatMessageEvent(ChatMessage message) {

        if(mChatUser.getFriendUsername().equals(message.getFriendUsername()) && message.isMulti()) {
            addChatMessageView(message);
        }
    }

    @Override
    public void send(String message) {

        if (ValueUtil.isEmpty(message)) {
            return;
        }
        Observable.just(message)
        .observeOn(Schedulers.io())
        .subscribe(new Action1<String>() {
            @Override
            public void call(String message) {
                try {
                    JSONObject json = new JSONObject();
                    json.put(ChatMessage.KEY_MESSAGE_CONTENT, message);
                    json.put(ChatMessage.KEY_MULTI_CHAT_SEND_USER, mChatUser.getMeUsername());
                    mMultiUserChat.sendMessage(json.toString());
                } catch (Exception e) {
                    Logger.e(e, "send message failure");
                }
            }
        });
    }

    @Override
    public void sendVoice(File audioFile) {

    }

    @Override
    public void functionClick(KeyBoardMoreFunType funType) {

    }
}
