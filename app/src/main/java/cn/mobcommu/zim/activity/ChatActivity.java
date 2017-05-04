package cn.mobcommu.zim.activity;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import cn.mobcommu.zim.R;
import cn.mobcommu.zim.activity.base.BaseChatActivity;
import cn.mobcommu.zim.bean.ChatMessage;
import cn.mobcommu.zim.constant.Constant;
import cn.mobcommu.zim.constant.FileLoadState;
import cn.mobcommu.zim.constant.KeyBoardMoreFunType;
import cn.mobcommu.zim.constant.MessageType;
import cn.mobcommu.zim.smack.SmackManager;
import cn.mobcommu.zim.util.AppFileHelper;
import cn.mobcommu.zim.util.DBHelper;
import cn.mobcommu.zim.util.Filter;
import cn.mobcommu.zim.util.PresenceUtil;
import cn.mobcommu.zim.util.ReceiveFileListenerUtil;
import cn.mobcommu.util.BitmapUtil;
import cn.mobcommu.util.DateUtil;
import cn.mobcommu.util.FileUtil;
import cn.mobcommu.util.UIUtil;
import cn.mobcommu.util.ValueUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import com.orhanobut.logger.Logger;

/**
 * 单聊窗口
 */
public class ChatActivity extends BaseChatActivity {

    /**
     * 聊天窗口对象
     */
    private Chat mChat;

    /**
     * 选择图片
     */
    private static final int REQUEST_CODE_GET_IMAGE = 1;

    /**
     * 拍照
     */
    private static final int REQUEST_CODE_TAKE_PHOTO = 2;

    /**
     * 选择文件
     */
    private static final int REQUEST_CODE_GET_FILE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_layout);

        mChat = SmackManager.getInstance().createChat(mChatUser.getChatJid());
        addReceiveFileListener();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveChatMessageEvent(ChatMessage message) { // 发送消息或接收消息时都执行

        // 当前聊天对象名称与消息好友名称一致时，添加消息
        if(mChatUser.getFriendUsername().equals(message.getFriendUsername())) {
            addChatMessageView(message);
        }
    }

    /**
     * 发送消息
     *
     * @param message
     */
    @Override
    public void send(final String message) {

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
                        json.put(ChatMessage.KEY_FROM_NICKNAME, mChatUser.getMeNickname());
                        json.put(ChatMessage.KEY_MESSAGE_CONTENT, message);
                        mChat.sendMessage(json.toString());

                        ChatMessage msg = new ChatMessage(MessageType.MESSAGE_TYPE_TEXT.value(), true);
                        msg.setFriendNickname(mChatUser.getFriendNickname());
                        msg.setFriendUsername(mChatUser.getFriendUsername());
                        msg.setMeUsername(mChatUser.getMeUsername());
                        msg.setMeNickname(mChatUser.getMeNickname());
                        msg.setContent(message);

                        DBHelper.getInstance().getSQLiteDB().save(msg);
                        EventBus.getDefault().post(msg);
                    } catch (Exception e) {
                        Logger.e(e, "send message failure");
                    }
                }
            });
    }

    /**
     * 发送文件
     *
     * @param file
     */
    public void sendFile(final File file, int messageType) {

        String FileJIDFilter = mChatUser.getFileJid();
        Filter filter = new Filter();

        final OutgoingFileTransfer transfer = SmackManager.getInstance().getSendFileTransfer(filter.filterFileJid(FileJIDFilter));
        try {
            transfer.sendFile(file, String.valueOf(messageType));
            checkTransferStatus(transfer, file, messageType, true, file.length());
        } catch (SmackException e) {
            Logger.e(e, "send file failure");
        }
    }

    /**
     * 接收文件
     */
    public void addReceiveFileListener() {

        FileTransferListener fileTransferListener = new FileTransferListener() {
            @Override
            public void fileTransferRequest(FileTransferRequest request) {
                String requestor = request.getRequestor(); // eg. 602@laboratory/Smack

                if (mChatUser.getFriendUsername().equals(requestor.substring(0, requestor.lastIndexOf('@')))) {
                    // Accept it
                    IncomingFileTransfer transfer = request.accept();

                    try {
                        int messageType = Integer.parseInt(request.getDescription());

                        File dir = AppFileHelper.getAppChatMessageDir(messageType);
                        File file = new File(dir, request.getFileName());
                        transfer.recieveFile(file);
                        checkTransferStatus(transfer, file, messageType, false, request.getFileSize());

                    } catch (SmackException | IOException e) {
                        Logger.e(e, "receive file failure");
                    }
                }
            }
        };

        String jid = mChatUser.getFriendUsername();
        if (ReceiveFileListenerUtil.hasJid(jid)) { // jid已存在,则删除用新的替换
            FileTransferListener previous  = ReceiveFileListenerUtil.getReceiveFileListener(jid);
            ReceiveFileListenerUtil.removeReceiveFileListener(jid);
            SmackManager.getInstance().removeFileTransferListener(previous);
        }

        SmackManager.getInstance().addFileTransferListener(fileTransferListener);
        ReceiveFileListenerUtil.addFileListener(jid, fileTransferListener);
    }

    /**
     * 检查发送文件、接收文件的状态
     *
     * @param transfer
     * @param file              发送或接收的文件
     * @param messageType       文件类型，语音或图片
     * @param isMeSend            是否为发送
     */
    private void checkTransferStatus(final FileTransfer transfer, final File file, final int messageType, final boolean isMeSend, long filesize){
        final ChatMessage msg = new ChatMessage(messageType, isMeSend);
        msg.setFriendNickname(mChatUser.getFriendNickname());
        msg.setFriendUsername(mChatUser.getFriendUsername());
        msg.setMeUsername(mChatUser.getMeUsername());
        msg.setMeNickname(mChatUser.getMeNickname());
        msg.setFilePath(file.getAbsolutePath());
        msg.setFileSize(filesize);
//        DBHelper.getInstance().getSQLiteDB().save(msg); // 此处注释掉4.13

        Observable.create(new Observable.OnSubscribe<ChatMessage>(){
            @Override
            public void call(Subscriber<? super ChatMessage> subscriber) {
                addChatMessageView(msg);
                subscriber.onNext(msg);
                subscriber.onCompleted();
            }
        })
        .subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(Schedulers.io())
        .map(new Func1<ChatMessage, ChatMessage>() {
            @Override
            public ChatMessage call(ChatMessage chatMessage) {

                while (!transfer.isDone()) {//判断传输是否完成
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return chatMessage;
            }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<ChatMessage>() {
            @Override
            public void call(ChatMessage chatMessage) {

                if (FileTransfer.Status.complete.toString().equals(transfer.getStatus().toString())) { //传输完成
                    chatMessage.setFileLoadState(FileLoadState.STATE_LOAD_SUCCESS.value());
                } else {
                    chatMessage.setFileLoadState(FileLoadState.STATE_LOAD_ERROR.value());
                }

                DBHelper.getInstance().getSQLiteDB().save(chatMessage); // 将加载状态写入数据库 0 -> 1 or 2
                mAdapter.update(chatMessage);
            }
        });
    }

    /**
     * 发送语音消息
     *
     * @param audioFile
     */
    @Override
    public void sendVoice(final File audioFile) {

        String friendUsername = mChatUser.getFriendUsername(); // 好友用户名
        final String strUrl = "http://" + Constant.SERVER_IP +
                ":9090/plugins/presence/status?jid=" +
                friendUsername + "@" + Constant.SERVER_NAME + "&type=xml";

        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                // 0 - 用户不存在; 1 - 用户在线; 2 - 用户离线
                int tmp_state = PresenceUtil.IsUserOnLine(strUrl);
                subscriber.onNext(new Integer(tmp_state));
                subscriber.onCompleted();
            }
        })
        .subscribeOn(Schedulers.io()) //指定上面的Subscriber线程
        .observeOn(AndroidSchedulers.mainThread()) //指定下面的回调线程
        .subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer state) {

                if (state != 1) {
                    UIUtil.showToast(ChatActivity.this, "好友离线，无法发送语音！");
                } else { // 好友在线
                    sendFile(audioFile, MessageType.MESSAGE_TYPE_VOICE.value());
                }
            }
        });
    }

    /**
     * 多种功能的点击处理
     * @param funType 功能类型
     */
    @Override
    public void functionClick(final KeyBoardMoreFunType funType) {

        String friendUsername = mChatUser.getFriendUsername();
        final String strUrl = "http://" + Constant.SERVER_IP +
                ":9090/plugins/presence/status?jid=" +
                friendUsername + "@" + Constant.SERVER_NAME + "&type=xml";

        if (funType == KeyBoardMoreFunType.FUN_TYPE_PRESCRIPTION) {

            Uri data = Uri.parse("xunyaowenyi://online_prescription/?doctor_name=" + mChatUser.getMeUsername() + "&patient_name=" + mChatUser.getFriendUsername());
            Intent intent = new Intent(Intent.ACTION_VIEW,data);

            //保证新启动的APP有单独的堆栈，如果希望新启动的APP和原有APP使用同一个堆栈则去掉该项
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            return;
        }

        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                // 0 - 用户不存在; 1 - 用户在线; 2 - 用户离线
                int tmp_state = PresenceUtil.IsUserOnLine(strUrl);
                subscriber.onNext(new Integer(tmp_state));
                subscriber.onCompleted();
            }
        })
        .subscribeOn(Schedulers.io()) //指定上面的Subscriber线程
        .observeOn(AndroidSchedulers.mainThread()) //指定下面的回调线程
        .subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer state) {

                if (state != 1) {
                    UIUtil.showToast(ChatActivity.this, "好友离线，无法发送图片和文件！");
                } else {
                    switch (funType) {
                        case FUN_TYPE_IMAGE://选择图片
                            selectImage();
                            break;
                        case FUN_TYPE_TAKE_PHOTO://拍照
                            takePhoto();
                            break;
                        case FUN_TYPE_FILE://选择文件
                            selectFile();
                            break;
                    }
                }
            }
        });

    }

    /**
     * 从图库选择图片
     */
    public void selectImage() {

        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"), REQUEST_CODE_GET_IMAGE);
        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"), REQUEST_CODE_GET_IMAGE);
        }

    }

    /**
     * 拍照--照片路径
     */
    private String mPicPath = "";

    /**
     * 拍照
     */
    public void takePhoto() {

        String dir = AppFileHelper.getAppChatMessageDir(MessageType.MESSAGE_TYPE_IMAGE.value()).getAbsolutePath();
        mPicPath = dir + "/" + DateUtil.formatDatetime(new Date(), "yyyyMMddHHmmss") + ".png";
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mPicPath)));
        startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);

    }

    /**
     * 从本机选择文件
     */
    public void selectFile() {

        Intent intent;

        if (Build.VERSION.SDK_INT < 19) { // 版本不同，参数不一样
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
        }

        try {
            startActivityForResult(Intent.createChooser(intent, "选择文件"), REQUEST_CODE_GET_FILE);
        } catch (android.content.ActivityNotFoundException ex) {
            UIUtil.showToast(this, "没有找到文件管理器，请安装一个文件管理软件！");
        }

    }

    /**
     * 执行拍照、图片选择、文件选择后的结果处理
     * @param requestCode 请求码
     * @param resultCode 返回结果码
     * @param data 返回数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_CODE_TAKE_PHOTO) {//拍照成功

                takePhotoSuccess();

            } else if (requestCode == REQUEST_CODE_GET_IMAGE) {//图片选择成功

                Uri dataUri = data.getData();
                if (dataUri != null) {
                    File file = FileUtil.uri2File(this, dataUri);
                    sendFile(file, MessageType.MESSAGE_TYPE_IMAGE.value());
                }

            } else if (requestCode == REQUEST_CODE_GET_FILE) { // 文件选择成功

                Uri dataUri = data.getData();
                String path = FileUtil.getPath(this,dataUri);
                if (dataUri != null) {

                    File file = new File(path);
                    // deal with null
                    if (file == null) {
                        UIUtil.showToast(this, "不支持您选择的文件管理器，\n请使用系统自带的文件管理器！");
                        return;
                    }
                    sendFile(file, MessageType.MESSAGE_TYPE_FILE.value());

                } else {
                    UIUtil.showToast(this, "文件获取失败！");
                }

            }
        }
    }

    /**
     * 照片拍摄成功
     */
    public void takePhotoSuccess() {

        Bitmap bitmap = BitmapUtil.createBitmapWithFile(mPicPath, 640);
        BitmapUtil.createPictureWithBitmap(mPicPath, bitmap, 80);

        if (!bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        sendFile(new File(mPicPath), MessageType.MESSAGE_TYPE_IMAGE.value());
    }
}
