package cn.mobcommu.zim.fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.mobcommu.base.BaseFragment;
import cn.mobcommu.zim.adapter.ChatRecordAdapter;
import cn.mobcommu.zim.constant.MessageType;
import cn.mobcommu.zim.decoration.CommonItemDecoration;
import cn.mobcommu.zim.bean.ChatMessage;
import cn.mobcommu.zim.bean.ChatRecord;
import cn.mobcommu.zim.smack.SmackManager;
import cn.mobcommu.zim.ui.recyclerview.CommonRecyclerView;
import cn.mobcommu.zim.ui.recyclerview.HeaderAndFooterAdapter;
import cn.mobcommu.zim.util.DBHelper;
import cn.mobcommu.zim.util.DBQueryHelper;
import cn.mobcommu.zim.util.IMUtil;
import cn.mobcommu.zim.util.LoginHelper;
import cn.mobcommu.zim.util.NotificationHelper;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.packet.Message;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import java.util.HashMap;
import java.util.List;

/**
 * 聊天消息列表
 */
public class MessageFragment extends BaseFragment implements CommonRecyclerView.OnItemClickListener {
    @BindView(cn.mobcommu.zim.R.id.recycler_message_record)
    CommonRecyclerView mRecyclerView;

    private LinearLayoutManager mLayoutManager;
    private ChatRecordAdapter mAdapter;
    private HashMap<String, Integer> mMap = new HashMap<>(); // 聊天用户的用户名与用户聊天记录Position的映射关系
    private List<Message> offlineMessagesList;

    public MessageFragment() {

    }

    public String getProperUsername(String inputName) { // 613@derc5/Smack -> 613
        return inputName.substring(0, inputName.indexOf('@'));
    }

    @Override
    public View getContentView(LayoutInflater inflater, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(cn.mobcommu.zim.R.layout.fragment_message, null);
        ButterKnife.bind(this, view);
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new CommonItemDecoration());
        mRecyclerView.setOnItemClickListener(this);
        return view;
    }

    /**
     * 处理离线消息
     */
    public void dealOfflineMessage() {
        offlineMessagesList = SmackManager.getInstance().getOfflineMessagesList();
        try {
            for (Message message : offlineMessagesList) {
                JSONObject json = new JSONObject(message.getBody());
                ChatMessage chatMessage = new ChatMessage(MessageType.MESSAGE_TYPE_TEXT.value(), false);

                chatMessage.setFriendUsername(getProperUsername(message.getFrom()));
                chatMessage.setFriendNickname(json.optString(ChatMessage.KEY_FROM_NICKNAME));
                chatMessage.setMeUsername(getProperUsername(message.getTo()));
                chatMessage.setMeNickname(LoginHelper.getUser().getNickname());
                chatMessage.setContent(json.optString(ChatMessage.KEY_MESSAGE_CONTENT));
                chatMessage.setMulti(false);

                DBHelper.getInstance().getSQLiteDB().save(chatMessage); // 保存消息到数据库
                EventBus.getDefault().post(chatMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refreshData() {

        Observable.create(new Observable.OnSubscribe<List<ChatRecord>>() {
            @Override
            public void call(Subscriber<? super List<ChatRecord>> subscriber) {

                List<ChatRecord> list = DBQueryHelper.queryChatRecord();
                subscriber.onNext(list);
                subscriber.onCompleted();
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {

                refreshFailed();
                Logger.e(throwable, "get chat record failure");
            }
        })
        .subscribe(new Action1<List<ChatRecord>>() {
            @Override
            public void call(List<ChatRecord> chatRecords) {

                mAdapter = new ChatRecordAdapter(mContext, chatRecords);
                dealOfflineMessage(); // 处理离线消息
                mRecyclerView.setAdapter(mAdapter);
                refreshSuccess();
            }
        });
    }

    @Override
    public void onItemClick(HeaderAndFooterAdapter adapter, int position, View itemView) {

        ChatRecord chatRecord = mAdapter.getItem(position);
        // 未读消息数置0
        chatRecord.resetUnReadMessageCount();

        // 更新数据库
        mAdapter.update(chatRecord);
        DBHelper.getInstance().getSQLiteDB().update(chatRecord);//更新数据库中的记录

        IMUtil.startChatActivity(mContext, mAdapter.getItem(position));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        mAdapter = null;
        mMap.clear();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatRecordEvent(ChatRecord event) {
        //向其他人发起聊天时接收到的事件
        if(isRemoving() || mAdapter == null) {
            return;
        }
        if(mAdapter.getData().indexOf(event) > -1) {
            return;//已经存在此人的聊天窗口记录
        }
        addChatRecord(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveChatMessageEvent(ChatMessage message) {

        if (message.isMeSend() == false) {
            new NotificationHelper().notifyUser(mContext);
        }

        //收到发送的消息时接收到的事件(包括别人发送的和自己发送的消息)
        if(isRemoving() || mAdapter == null) {
            return;
        }
        ChatRecord chatRecord = getChatRecord(message);
        if(chatRecord == null) {//还没有创建此朋友的聊天记录
            chatRecord = new ChatRecord(message);
            addChatRecord(chatRecord);
        } else {
            chatRecord.setChatTime(message.getDatetime());
            chatRecord.setLastMessage(message.getContent());
            if (!message.isMeSend()) { // 接收到消息，未读数加一
                chatRecord.updateUnReadMessageCount();
            }
            mAdapter.update(chatRecord);
            DBHelper.getInstance().getSQLiteDB().update(chatRecord);//更新数据库中的记录
        }
    }

    private void addChatRecord(ChatRecord chatRecord) {

        mAdapter.add(chatRecord, 0);
        DBHelper.getInstance().getSQLiteDB().save(chatRecord);
        mLayoutManager.scrollToPosition(0);
        for(String key : mMap.keySet()) {//创建新的聊天记录之后，需要将之前的映射关系进行更新
            mMap.put(key, mMap.get(key) + 1);
        }
    }

    /**
     * 根据消息获取聊天记录窗口对象
     *
     * @param message
     * @return
     */
    private ChatRecord getChatRecord(ChatMessage message) {

        ChatRecord chatRecord = null;
        if(mMap.containsKey(message.getFriendUsername())) {
            chatRecord = mAdapter.getData().get(mMap.get(message.getFriendUsername()));
        } else {
            for(int i = 0; i < mAdapter.getData().size(); i++) {
                chatRecord = mAdapter.getData().get(i);
                if(chatRecord.getMeUsername().equals(message.getMeUsername()) &&
                        chatRecord.getFriendUsername().equals(message.getFriendUsername())) {
                    mMap.put(message.getFriendUsername(), i);
                    break;
                } else {
                    chatRecord = null;
                }
            }
        }
        return chatRecord;
    }

    @Override
    public String getTitle() {

        return getString(cn.mobcommu.zim.R.string.text_message);
    }
}
