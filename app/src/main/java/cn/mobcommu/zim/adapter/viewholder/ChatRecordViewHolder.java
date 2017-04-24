package cn.mobcommu.zim.adapter.viewholder;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.mobcommu.zim.R;
import cn.mobcommu.zim.ui.recyclerview.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatRecordViewHolder extends ViewHolder {
    @BindView(R.id.chat_friend_avatar)
    public ImageView avatar;
    @BindView(R.id.chat_friend_nickname)
    public TextView nickName;
    @BindView(R.id.chat_message)
    public TextView message;
    @BindView(R.id.chat_time)
    public TextView chatTime;
    @BindView(R.id.chat_message_count)
    public TextView messageCount;

    public ChatRecordViewHolder(View itemView) {

        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
