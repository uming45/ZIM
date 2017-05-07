package cn.mobcommu.zim.adapter;

import cn.mobcommu.zim.R;
import cn.mobcommu.zim.adapter.viewholder.ChatRecordViewHolder;
import cn.mobcommu.zim.bean.ChatRecord;
import cn.mobcommu.zim.constant.EmotionType;
import cn.mobcommu.zim.ui.recyclerview.HeaderAndFooterAdapter;
import cn.mobcommu.zim.ui.recyclerview.ViewHolder;
import cn.mobcommu.zim.util.ChatTimeUtil;
import cn.mobcommu.zim.util.EmotionUtil;
import cn.mobcommu.zim.util.ImageLoaderHelper;
import cn.mobcommu.util.ValueUtil;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;

import java.util.List;


/**
 * 聊天记录列表适配器
 */
public class ChatRecordAdapter extends HeaderAndFooterAdapter<ChatRecord> {
    private Context mContext;

    public ChatRecordAdapter(Context context, List<ChatRecord> list) {

        super(list);
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_record_item_layout, parent, false);
        return new ChatRecordViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(ViewHolder holder, int position, ChatRecord item) {

        ChatRecordViewHolder viewHolder = (ChatRecordViewHolder) holder;

        if(!ValueUtil.isEmpty(item.getFriendAvatar())) {
            ImageLoaderHelper.displayImage(viewHolder.avatar, item.getFriendAvatar());
        }

        // 依据es_member表，消息列表联系人名字格式：name (uname)
        viewHolder.nickName.setText(item.getFriendNickname() + " (" +
                item.getFriendUsername() + ")");

        if(!ValueUtil.isEmpty(item.getLastMessage())) {
            if(viewHolder.message.getVisibility() == View.GONE) {
                viewHolder.message.setVisibility(View.VISIBLE);
            }
            SpannableString content = EmotionUtil.getInputEmotionContent(mContext, EmotionType.EMOTION_TYPE_CLASSIC, viewHolder.message, item.getLastMessage());
            viewHolder.message.setText(content);
        }
        viewHolder.chatTime.setText(ChatTimeUtil.getFriendlyTimeSpanByNow(item.getChatTime()));
        String messageCount = item.getUnReadMessageCount() > 0 ? String.valueOf(item.getUnReadMessageCount()) : "";
        viewHolder.messageCount.setText(messageCount);
    }
}
