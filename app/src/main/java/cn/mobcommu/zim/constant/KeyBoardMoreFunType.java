package cn.mobcommu.zim.constant;

/**
 * KeyBoard更多功能选项类型
 */
public enum KeyBoardMoreFunType {

    NONE(-1),
    /**
     * 选择图片
     */
    FUN_TYPE_IMAGE(0),
    /**
     * 拍照
     */
    FUN_TYPE_TAKE_PHOTO(1),
    /**
     * 选择文件
     */
    FUN_TYPE_FILE(2),
    /**
     * 开处方
     */
    FUN_TYPE_PRESCRIPTION(3),
    /**
     * 开检查单
     */
    FUN_TYPE_CHECKLIST(4);

    int value;

    KeyBoardMoreFunType(int value) {

        this.value = value;
    }

    public int value() {

        return value;
    }

    public static KeyBoardMoreFunType getFunType(int value) {

        if (value == FUN_TYPE_IMAGE.value()) {
            return FUN_TYPE_IMAGE;
        } else if(value == FUN_TYPE_TAKE_PHOTO.value()) {
            return FUN_TYPE_TAKE_PHOTO;
        } else if(value == FUN_TYPE_FILE.value()) {
            return FUN_TYPE_FILE;
        } else if(value == FUN_TYPE_PRESCRIPTION.value()) {
            return FUN_TYPE_PRESCRIPTION;
        } else if(value == FUN_TYPE_CHECKLIST.value()) {
            return FUN_TYPE_CHECKLIST;
        } else {
            return NONE;
        }
    }
}
