package cn.mobcommu.zim.smack;

import cn.mobcommu.zim.bean.LoginResult;
import cn.mobcommu.zim.bean.User;
import cn.mobcommu.zim.constant.Constant;
import cn.mobcommu.zim.util.LoginHelper;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.offline.OfflineMessageManager;

public class SmackManager {
    private static final String TAG = "SmackManager";
    /**
     * Xmpp服务器地址
     */
    public static final String SERVER_IP = Constant.SERVER_IP;
    /**
     * Xmpp 服务器端口
     */
    private static final int PORT = Constant.SERVER_PORT;
    /**
     * 服务器名称
     */
    public static final String SERVER_NAME = Constant.SERVER_NAME;
    /**
     *
     */
    public static final String XMPP_CLIENT = "Smack";

    private static volatile SmackManager sSmackManager;
    /**
     * 连接
     */
    private XMPPTCPConnection mConnection;

    /**
     * 离线消息
     */
    List<Message> offlineMessagesList;

    private SmackManager() {

        this.mConnection = connect();
    }

    /**
     * 获取操作实例
     *
     * @return
     */
    public static SmackManager getInstance() {

        if (sSmackManager == null) {
            synchronized (SmackManager.class) {
                if (sSmackManager == null) {
                    sSmackManager = new SmackManager();
                }
            }
        }
        return sSmackManager;
    }

    /**
     * 连接服务器
     *
     * @return
     */
    private XMPPTCPConnection connect() {

        try {
            SmackConfiguration.setDefaultPacketReplyTimeout(10000);
            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    //是否开启安全模式
                    .setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.disabled)
                    //服务器名称
                    .setServiceName(SERVER_NAME)
                    .setHost(SERVER_IP)//服务器IP地址
                    //服务器端口
                    .setPort(PORT)
                    //是否开启压缩
                    .setCompressionEnabled(true)
                    //设置用户是否上线
                    .setSendPresence(false)
                    //开启调试模式
                    .setDebuggerEnabled(true).build();

            XMPPTCPConnection connection = new XMPPTCPConnection(config);
            ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(connection);
            reconnectionManager.enableAutomaticReconnection();//允许自动重连
            reconnectionManager.setFixedDelay(2);//重连间隔时间
            connection.addConnectionListener(new SmackConnectionListener());//连接监听
            connection.connect();
            return connection;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取离线消息
     */
    public void getOfflineMessage() {

        OfflineMessageManager offlineMessageManager = new OfflineMessageManager(getConnection());

        try {
            offlineMessagesList = offlineMessageManager.getMessages();

            // 删除离线消息
            offlineMessageManager.deleteMessages();

            // 将状态设置成在线
            Presence presence = new Presence(Presence.Type.available);
            getConnection().sendStanza(presence);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取离线消息List
     */
    public List<Message> getOfflineMessagesList() {
        return offlineMessagesList;
    }

    /**
     * 登陆
     *
     * @param username     用户账号
     * @param password     用户密码
     * @return
     * @throws Exception
     */
    public LoginResult login(String username, String password) {

        try {

            LoginResult loginResult = LoginHelper.getLoginResult();
            if (loginResult != null && loginResult.isSuccess()) {
                if (!isConnected()) {
                    throw new IllegalStateException("服务器断开，请先连接服务器");
                }
                return loginResult;
            }

            disconnect(); // 先断开连接
            if (!isConnected()) {
                throw new IllegalStateException("服务器断开，请先连接服务器");
            }

            SASLAuthentication.blacklistSASLMechanism("SHA-1");

            mConnection.login(username, password);

            User user = new User(username, password);
            user.setNickname(getAccountName());

            // TEST
            getOfflineMessage(); // 获取离线消息

            LoginResult lr = new LoginResult(user, true);
            LoginHelper.setLoginResult(lr);

            return lr;
        } catch (Exception e) {
            Logger.e(TAG, e, "login failure");
            return new LoginResult(false, e.getMessage());
        }
    }

    /**
     * 获取连接
     * @return
     */
    public XMPPTCPConnection getConnection() {

        if (!isConnected() || mConnection == null) {
            throw new IllegalStateException("服务器断开，请先连接服务器");
        }
        return mConnection;
    }

    /**
     * 注销
     *
     * @return
     */
    public boolean logout() {

        if (!isConnected()) {
            return false;
        }
        try {
            mConnection.instantShutdown();
            return true;
        } catch (Exception e) {
            Logger.e(TAG, e, "logout failure");
            return false;
        }
    }

    /**
     * 删除当前登录的用户信息(从服务器上删除当前用户账号)
     *
     * @return
     */
    public boolean deleteUser() {

        if (!isConnected()) {
            return false;
        }
        try {
            AccountManager.getInstance(mConnection).deleteAccount();//删除该账号
            return true;
        } catch (NoResponseException | XMPPErrorException
                | NotConnectedException e) {
            return false;
        }
    }

    /**
     * 注册用户信息
     *
     * @param username   账号
     * @param password   账号密码
     * @param attributes 账号其他属性，参考AccountManager.getAccountAttributes()的属性介绍
     * @return
     */
    public boolean registerUser(String username, String password, Map<String, String> attributes) {

        if (!isConnected()) {
            return false;
        }
        try {
            AccountManager.getInstance(mConnection).createAccount(username, password, attributes);
            return true;
        } catch (NoResponseException | XMPPErrorException
                | NotConnectedException e) {
            Logger.e(TAG, "register failure", e);
            return false;
        }
    }

    /**
     * 修改密码
     *
     * @param newpassword 新密码
     * @return
     */
    public boolean changePassword(String newpassword) {

        if (!isConnected()) {
            return false;
        }
        try {
            AccountManager.getInstance(mConnection).changePassword(newpassword);
            return true;
        } catch (NoResponseException | XMPPErrorException | NotConnectedException e) {
            Logger.e(TAG, "change password failure", e);
            return false;
        }
    }

    /**
     * 断开连接，注销
     *
     * @return
     */
    public boolean disconnect() {

        if (!isConnected()) {
            return false;
        }
        mConnection.disconnect();
        return true;
    }

    /**
     * 更新用户状态
     *
     * @param code
     * @return
     */
    public boolean updateUserState(int code) {

        if (!isConnected()) {
            throw new NullPointerException("服务器连接失败，请先连接服务器");
        }
        try {
            Presence presence;
            switch (code) {
                case 0://设置在线
                    presence = new Presence(Presence.Type.available);
                    presence.setMode(Presence.Mode.available);
                    mConnection.sendStanza(presence);
                    break;
                case 1://设置Q我吧
                    presence = new Presence(Presence.Type.available);
                    presence.setMode(Presence.Mode.chat);
                    mConnection.sendStanza(presence);
                    break;
                case 2://设置忙碌
                    presence = new Presence(Presence.Type.available);
                    presence.setMode(Presence.Mode.dnd);
                    mConnection.sendStanza(presence);
                    break;
                case 3://设置离开
                    presence = new Presence(Presence.Type.available);
                    presence.setMode(Presence.Mode.away);
                    mConnection.sendStanza(presence);
                    break;
                case 4://设置隐身
                    break;
                case 5://设置离线
                    presence = new Presence(Presence.Type.unavailable);
                    mConnection.sendStanza(presence);
                    break;
                default:
                    break;
            }
            return true;
        } catch (NotConnectedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 是否连接成功
     *
     * @return
     */
    private boolean isConnected() {

        if (mConnection == null) {
            sSmackManager = new SmackManager();
        }
        if(mConnection == null) {
            return false;
        }
        if (!mConnection.isConnected()) {
            try {
                mConnection.connect();
                return true;
            } catch (SmackException | IOException | XMPPException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取账户昵称
     *
     * @return
     */
    public String getAccountName() {

        if (isConnected()) {
            try {
                return AccountManager.getInstance(mConnection).getAccountAttribute("name");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        throw new NullPointerException("服务器连接失败，请先连接服务器");
    }

    /**
     * 获取账户所有属性信息
     *
     * @return
     */
    public Set<String> getAccountAttributes() {

        if (isConnected()) {
            try {
                return AccountManager.getInstance(mConnection).getAccountAttributes();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        throw new NullPointerException("服务器连接失败，请先连接服务器");
    }

    /**
     * 创建聊天窗口
     *
     * @param jid 好友的JID
     * @return
     */
    public Chat createChat(String jid) {

        if (isConnected()) {
            ChatManager chatManager = ChatManager.getInstanceFor(mConnection);
            return chatManager.createChat(jid);
        }
        throw new NullPointerException("服务器连接失败，请先连接服务器");
    }

    /**
     * 获取聊天对象管理器
     *
     * @return
     */
    public ChatManager getChatManager() {

        if (isConnected()) {
            ChatManager chatManager = ChatManager.getInstanceFor(mConnection);
            return chatManager;
        }
        throw new NullPointerException("服务器连接失败，请先连接服务器");
    }

    /**
     * 获取当前登录用户的所有好友信息
     *
     * @return
     */
    public Set<RosterEntry> getAllFriends() {

        if (isConnected()) {
            return Roster.getInstanceFor(mConnection).getEntries();
        }
        throw new NullPointerException("服务器连接失败，请先连接服务器");
    }

    /**
     * 当前用户的某个好友的状态
     */
    public Presence getFriendPresence(String user) {  // not work now
        if (isConnected()) {
            return Roster.getInstanceFor(mConnection).getPresence(user + "@" + Constant.SERVER_NAME);
        }
        throw new NullPointerException("服务器连接失败，请先连接服务器");
    }

    /**
     * 获取指定账号的好友信息
     *
     * @param user 账号
     * @return
     */
    public RosterEntry getFriend(String user) {

        if (isConnected()) {
            return Roster.getInstanceFor(mConnection).getEntry(user);
        }
        throw new NullPointerException("服务器连接失败，请先连接服务器");
    }

    /**
     * 添加好友
     *
     * @param user      用户账号
     * @param nickName  用户昵称
     * @param groupName 所属组名
     * @return
     */
    public boolean addFriend(String user, String nickName, String groupName) {

        if (isConnected()) {
            try {
                Roster.getInstanceFor(mConnection).createEntry(user, nickName, new String[]{groupName});
                return true;
            } catch (NotLoggedInException | NoResponseException
                    | XMPPErrorException | NotConnectedException e) {
                return false;
            }
        }
        throw new NullPointerException("服务器连接失败，请先连接服务器");
    }

    /**
     * 获取聊天对象的Fully的jid值
     *
     * @param userName 用户账号
     * @return
     */
    public String getChatJid(String userName) {

        if (!isConnected()) {
            throw new NullPointerException("服务器连接失败，请先连接服务器");
        }
        return userName + "@" + mConnection.getServiceName();
    }

    /**
     * Jid The fully qualified jabber ID (i.e. full JID) with resource of the user
     *
     * @param userName
     * @return
     */
    public String getFullJid(String userName) {

        if (!isConnected()) {
            throw new NullPointerException("服务器连接失败，请先连接服务器");
        }
        return userName + "@" + mConnection.getServiceName() + "/" + XMPP_CLIENT;
    }

    /**
     * 获取文件传输的完全限定Jid The fully qualified jabber ID (i.e. full JID) with resource of the user to
     * send the file to.
     *
     * @param userName 用户名，也就是RosterEntry中的user
     * @return
     */
    public String getFileTransferJid(String userName) {

        return getFullJid(userName);
    }

    /**
     * 获取发送文件的发送器
     *
     * @param jid 一个完整的jid(如：xx@xx.xx.xx.xx/Smack，后面的Smack应该客户端类型，不加这个会出错)
     * @return
     */
    public OutgoingFileTransfer getSendFileTransfer(String jid) {

        if (isConnected()) {
            return FileTransferManager.getInstanceFor(mConnection).createOutgoingFileTransfer(jid);
        }
        throw new NullPointerException("服务器连接失败，请先连接服务器");
    }

    /**
     * 添加文件接收的监听
     *
     * @param fileTransferListener
     */
    public void addFileTransferListener(FileTransferListener fileTransferListener) {

        if (isConnected()) {
            FileTransferManager.getInstanceFor(mConnection).addFileTransferListener(fileTransferListener);
            return;
        }
        throw new NullPointerException("服务器连接失败，请先连接服务器");
    }

    /**
     * 删除文件接收的监听
     *
     * @param fileTransferListener
     */
    public void removeFileTransferListener(FileTransferListener fileTransferListener) {

        if (isConnected()) {
            FileTransferManager.getInstanceFor(mConnection).removeFileTransferListener(fileTransferListener);
            return;
        }
        throw new NullPointerException("服务器连接失败，请先连接服务器");
    }

    public ServiceDiscoveryManager getServiceDiscoveryManager() {

        if (!isConnected()) {
            throw new NullPointerException("服务器连接失败，请先连接服务器");
        }
        return ServiceDiscoveryManager.getInstanceFor(mConnection);
    }
}
