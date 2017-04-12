package cn.ittiger.im.adapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.im.R;
import cn.ittiger.im.activity.EnlargeImage;
import cn.ittiger.im.bean.ChatMessage;
import cn.ittiger.im.constant.EmotionType;
import cn.ittiger.im.constant.FileLoadState;
import cn.ittiger.im.constant.MessageType;
import cn.ittiger.im.ui.recyclerview.HeaderAndFooterAdapter;
import cn.ittiger.im.ui.recyclerview.ViewHolder;
import cn.ittiger.im.util.ChatTimeUtil;
import cn.ittiger.im.util.EmotionUtil;
import cn.ittiger.im.util.ImageLoaderHelper;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 消息列表数据适配器
 */
public class ChatAdapter extends HeaderAndFooterAdapter<ChatMessage> {
    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_RECEIVER = 0;
    private Context mContext;
    private Context mbasesContext;
    /**
     * 音频播放器
     */
    private MediaPlayer mediaPlayer;

    public ChatAdapter(Context context, List<ChatMessage> list) {

        super(list);
        mContext = context;
    }

    public ChatAdapter(Context context, List<ChatMessage> list, Context baseContext) {

        super(list);
        mContext = context;
        mbasesContext = baseContext;
    }

    @Override
    public int getItemViewTypeForData(int position) {

        return getItem(position).isMeSend() ? VIEW_TYPE_ME : VIEW_TYPE_RECEIVER;
    }

    @Override
    public ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {

        View view;
        if(viewType == VIEW_TYPE_ME) {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_messgae_item_right_layout, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_messgae_item_left_layout, parent, false);
        }
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(ViewHolder holder, int position, final ChatMessage message) {

        final ChatViewHolder viewHolder = (ChatViewHolder) holder;
        if(message.isMeSend()) {
            viewHolder.chatNickname.setText(message.getMeNickname());
        } else {
            viewHolder.chatNickname.setText(message.getFriendNickname());
        }

        viewHolder.chatContentTime.setText(ChatTimeUtil.getFriendlyTimeSpanByNow(message.getDatetime()));
        setMessageViewVisible(message.getMessageType(), viewHolder); // 根据消息类型显示对应的消息展示控件

        if (message.getMessageType() == MessageType.MESSAGE_TYPE_TEXT.value()) { //文本消息
            //处理表情
            SpannableString content = EmotionUtil.getEmotionContent(mContext, EmotionType.EMOTION_TYPE_CLASSIC, message.getContent());
            viewHolder.chatContentText.setText(content);

        } else if (message.getMessageType() == MessageType.MESSAGE_TYPE_IMAGE.value()) { //图片消息

            final String url = "file://" + message.getFilePath();
            ImageLoaderHelper.displayImage(viewHolder.chatContentImage, url);

            viewHolder.chatContentImage.setOnClickListener(new View.OnClickListener(){ //图片点击
                @Override
                public void onClick(View v) {

                    Intent mIntent = new Intent(mbasesContext, EnlargeImage.class);
                    mIntent.putExtra("image_url", message.getFilePath());
                    mbasesContext.startActivity(mIntent);
                }
            });

            showLoading(viewHolder, message);

        } else if (message.getMessageType() == MessageType.MESSAGE_TYPE_VOICE.value()) { //语音消息

            viewHolder.chatContentVoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    playVoice(viewHolder.chatContentVoice, message);
                }
            });

            showLoading(viewHolder, message);

        } else if (message.getMessageType() == MessageType.MESSAGE_TYPE_FILE.value()) { //文件消息

            final String filePath = message.getFilePath();
            String fileName = filePath.substring(filePath.lastIndexOf("/")+1);

            TextView file_tag_tv = (TextView)viewHolder.chatContentFile.findViewById(R.id.file_message_tv_tab);
            if (message.isMeSend()) {
                file_tag_tv.setText("发送文件：");
                //viewHolder.chatContentFile.setText("发送文件: \n" + fileName.trim());
            } else {
                file_tag_tv.setText("接收文件：");
            }

            TextView file_name_tv = (TextView)viewHolder.chatContentFile.findViewById(R.id.file_message_tv_filename);
            file_name_tv.setText(fileName.trim());

            viewHolder.chatContentFile.setOnClickListener(new View.OnClickListener(){//文件点击
                @Override
                public void onClick(View v) {

                    File file = new File(filePath);
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Intent.ACTION_VIEW);

                    String type = getMimeType(filePath); // 获取文件类型
                    intent.setDataAndType(Uri.fromFile(file), type);
                    mbasesContext.startActivity(intent);

                }
            });

            showLoading(viewHolder, message);

        }
    }

    /**
     * 获取文件类型
     * @param url
     * @return
     */
    public static String getMimeType(String url) { // url = file path or whatever suitable URL you want.
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    private void showLoading(ChatViewHolder viewHolder, ChatMessage message) {

        if (message.getFileLoadState() == FileLoadState.STATE_LOAD_START.value()) {//加载开始
            viewHolder.chatContentLoading.setBackgroundResource(R.drawable.chat_file_content_loading_anim);
            final AnimationDrawable animationDrawable = (AnimationDrawable) viewHolder.chatContentLoading.getBackground();
            viewHolder.chatContentLoading.post(new Runnable() {
                @Override
                public void run() {

                    animationDrawable.start();
                }
            });
            viewHolder.chatContentLoading.setVisibility(View.VISIBLE);
        } else if(message.getFileLoadState() == FileLoadState.STATE_LOAD_SUCCESS.value()) {//加载完成
            viewHolder.chatContentLoading.setVisibility(View.GONE);
        } else if(message.getFileLoadState() == FileLoadState.STATE_LOAD_ERROR.value()) {
            viewHolder.chatContentLoading.setBackgroundResource(R.drawable.file_load_fail);
        }
    }

    /**
     * 根据消息类型显示对应的消息展示控件
     *
     * @param messageType
     * @param viewHolder
     */
    private void setMessageViewVisible(int messageType, ChatViewHolder viewHolder) {

        if (messageType == MessageType.MESSAGE_TYPE_TEXT.value()) {//文本消息
            viewHolder.chatContentText.setVisibility(View.VISIBLE);
            viewHolder.chatContentImage.setVisibility(View.GONE);
            viewHolder.chatContentFile.setVisibility(View.GONE);
            viewHolder.chatContentVoice.setVisibility(View.GONE);
        } else if (messageType == MessageType.MESSAGE_TYPE_IMAGE.value()) {//图片消息
            viewHolder.chatContentText.setVisibility(View.GONE);
            viewHolder.chatContentImage.setVisibility(View.VISIBLE);
            viewHolder.chatContentFile.setVisibility(View.GONE);
            viewHolder.chatContentVoice.setVisibility(View.GONE);
        } else if (messageType == MessageType.MESSAGE_TYPE_VOICE.value()) {//语音消息
            viewHolder.chatContentText.setVisibility(View.GONE);
            viewHolder.chatContentImage.setVisibility(View.GONE);
            viewHolder.chatContentFile.setVisibility(View.GONE);
            viewHolder.chatContentVoice.setVisibility(View.VISIBLE);
        } else if (messageType == MessageType.MESSAGE_TYPE_FILE.value()) { //文件消息
            viewHolder.chatContentText.setVisibility(View.GONE);
            viewHolder.chatContentImage.setVisibility(View.GONE);
            viewHolder.chatContentFile.setVisibility(View.VISIBLE);
            viewHolder.chatContentVoice.setVisibility(View.GONE);
        }
    }

    /**
     * 播放语音信息
     *
     * @param iv
     * @param message
     */
    private void playVoice(final ImageView iv, final ChatMessage message) {

        if (message.isMeSend()) {
            iv.setBackgroundResource(R.drawable.anim_chat_voice_right);
        } else {
            iv.setBackgroundResource(R.drawable.anim_chat_voice_left);
        }
        final AnimationDrawable animationDrawable = (AnimationDrawable) iv.getBackground();
        iv.post(new Runnable() {
            @Override
            public void run() {

                animationDrawable.start();
            }
        });
        if (mediaPlayer == null || !mediaPlayer.isPlaying()) {//点击播放，再次点击停止播放
            // 开始播放录音
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    animationDrawable.stop();
                    // 恢复语音消息图标背景
                    if (message.isMeSend()) {
                        iv.setBackgroundResource(R.drawable.gxu);
                    } else {
                        iv.setBackgroundResource(R.drawable.gxx);
                    }
                }
            });
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(message.getFilePath());
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            animationDrawable.stop();
            // 恢复语音消息图标背景
            if (message.isMeSend()) {
                iv.setBackgroundResource(R.drawable.gxu);
            } else {
                iv.setBackgroundResource(R.drawable.gxx);
            }
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    }

    public class ChatViewHolder extends ViewHolder {
        @BindView(R.id.tv_chat_msg_username)
        public TextView chatNickname;//消息来源人昵称
        @BindView(R.id.tv_chat_msg_time)
        public TextView chatContentTime;//消息时间
        @BindView(R.id.iv_chat_avatar)
        public ImageView chatUserAvatar;//用户头像
        @BindView(R.id.tv_chat_msg_content_text)
        public TextView chatContentText;//文本消息
        @BindView(R.id.iv_chat_msg_content_image)
        public ImageView chatContentImage;//图片消息

        @BindView(R.id.iv_chat_msg_content_file)
        public LinearLayout chatContentFile;//文件消息
//        public TextView chatContentFile;//文件消息

        @BindView(R.id.iv_chat_msg_content_voice)
        public ImageView chatContentVoice;//语音消息
        @BindView(R.id.iv_chat_msg_content_loading)
        public ImageView chatContentLoading;//发送接收文件时的进度条

        public ChatViewHolder(View itemView) {

            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
