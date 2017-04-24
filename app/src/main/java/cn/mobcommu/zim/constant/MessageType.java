package cn.mobcommu.zim.constant;


/**
 * 消息类型
 */
public enum MessageType {
    /**
     * 文本消息类型
     */
    MESSAGE_TYPE_TEXT(0),
    /**
     * 图片消息类型
     */
    MESSAGE_TYPE_IMAGE(1),
    /**
     * 语音消息类型
     */
    MESSAGE_TYPE_VOICE(2),
    /**
     * 文件消息类型
     */
    MESSAGE_TYPE_FILE(3);

    int value;
    MessageType(int value) {

        this.value = value;
    }

    public int value() {

        return value;
    }

    public static MessageType getMessageType(int value) {

        if (value == MESSAGE_TYPE_IMAGE.value()) {
            return MESSAGE_TYPE_IMAGE;
        } else if(value == MESSAGE_TYPE_TEXT.value()) {
            return MESSAGE_TYPE_TEXT;
        } else if (value == MESSAGE_TYPE_VOICE.value()){
            return MESSAGE_TYPE_VOICE;
        } else if (value == MESSAGE_TYPE_FILE.value()) {
            return MESSAGE_TYPE_FILE;
        } else {
            return null;
        }
    }
}
