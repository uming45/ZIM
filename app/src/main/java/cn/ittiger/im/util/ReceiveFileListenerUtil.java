package cn.ittiger.im.util;


import org.jivesoftware.smackx.filetransfer.FileTransferListener;

import java.util.Map;
import java.util.HashMap;

/**
 * 接收文件监听器工具类
 */
public class ReceiveFileListenerUtil {

    private static Map<String, FileTransferListener> fileListeners = new HashMap<String, FileTransferListener>();

    public static void addFileListener(String jid, FileTransferListener listener) {
        if (!fileListeners.containsKey(jid)) {
            fileListeners.put(jid, listener);
        }
    }

    public static boolean hasJid(String jid) {
        if (fileListeners.containsKey(jid)) {
            return true;
        } else {
            return false;
        }
    }

    public static Map<String, FileTransferListener> getReceiveFileListeners() {
        return fileListeners;
    }

    public static FileTransferListener getReceiveFileListener(String jid) {
        return fileListeners.get(jid);
    }

    public static FileTransferListener removeReceiveFileListener(String jid) {
        return fileListeners.remove(jid);
    }
}
