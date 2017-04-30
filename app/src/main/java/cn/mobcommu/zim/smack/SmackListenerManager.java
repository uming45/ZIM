package cn.mobcommu.zim.smack;

/**
 * Smack全局监听器管理
 */
public class SmackListenerManager {
    private static volatile SmackListenerManager sSmackListenerManager;
    /**
     * 单聊消息监听管理器
     */
    private SmackChatManagerListener mChatManagerListener;

    private SmackListenerManager() {

        mChatManagerListener = new SmackChatManagerListener();
    }

    public static SmackListenerManager getInstance() {

        if(sSmackListenerManager == null) {
            synchronized (SmackListenerManager.class) {
                if(sSmackListenerManager == null) {
                    sSmackListenerManager = new SmackListenerManager();
                }
            }
        }
        return sSmackListenerManager;
    }

    public static void addGlobalListener() {

        addMessageListener();
    }

    /**
     * 添加单聊消息全局监听
     */
    static void addMessageListener() {

        SmackManager.getInstance().getChatManager().addChatListener(getInstance().mChatManagerListener);
    }

    public void destroy() {

        SmackManager.getInstance().getChatManager().removeChatListener(mChatManagerListener);
        mChatManagerListener = null;
    }
}
