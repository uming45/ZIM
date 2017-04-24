package cn.mobcommu.zim.util;

import cn.mobcommu.zim.constant.EmotionType;

import android.support.v4.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;

/**
 * 表情数据帮助类
 */
public class EmotionDataHelper {
    /**
     * key -- 表情名称
     * value -- 表情图片resId
     */
    private static ArrayMap<String, Integer> sEmotionClassicMap;
    /**
     * 表情页底部的表情类型Tab数据
     */
    private static List<EmotionType> sEmotionTabList;

    static {
        sEmotionClassicMap = new ArrayMap<>();

        sEmotionTabList = new ArrayList<>();
        sEmotionTabList.add(EmotionType.EMOTION_TYPE_CLASSIC);
        sEmotionTabList.add(EmotionType.EMOTION_TYPE_MORE);

        sEmotionClassicMap.put("[呵呵]", cn.mobcommu.zim.R.drawable.d_hehe);
        sEmotionClassicMap.put("[嘻嘻]", cn.mobcommu.zim.R.drawable.d_xixi);
        sEmotionClassicMap.put("[哈哈]", cn.mobcommu.zim.R.drawable.d_haha);
        sEmotionClassicMap.put("[爱你]", cn.mobcommu.zim.R.drawable.d_aini);
        sEmotionClassicMap.put("[挖鼻屎]", cn.mobcommu.zim.R.drawable.d_wabishi);
        sEmotionClassicMap.put("[吃惊]", cn.mobcommu.zim.R.drawable.d_chijing);
        sEmotionClassicMap.put("[晕]", cn.mobcommu.zim.R.drawable.d_yun);
        sEmotionClassicMap.put("[泪]", cn.mobcommu.zim.R.drawable.d_lei);
        sEmotionClassicMap.put("[馋嘴]", cn.mobcommu.zim.R.drawable.d_chanzui);
        sEmotionClassicMap.put("[抓狂]", cn.mobcommu.zim.R.drawable.d_zhuakuang);
        sEmotionClassicMap.put("[哼]", cn.mobcommu.zim.R.drawable.d_heng);
        sEmotionClassicMap.put("[可爱]", cn.mobcommu.zim.R.drawable.d_keai);
        sEmotionClassicMap.put("[怒]", cn.mobcommu.zim.R.drawable.d_nu);
        sEmotionClassicMap.put("[汗]", cn.mobcommu.zim.R.drawable.d_han);
        sEmotionClassicMap.put("[害羞]", cn.mobcommu.zim.R.drawable.d_haixiu);
        sEmotionClassicMap.put("[睡觉]", cn.mobcommu.zim.R.drawable.d_shuijiao);
        sEmotionClassicMap.put("[钱]", cn.mobcommu.zim.R.drawable.d_qian);
        sEmotionClassicMap.put("[偷笑]", cn.mobcommu.zim.R.drawable.d_touxiao);
        sEmotionClassicMap.put("[笑cry]", cn.mobcommu.zim.R.drawable.d_xiaoku);
        sEmotionClassicMap.put("[吐]", cn.mobcommu.zim.R.drawable.d_tu);
//        sEmotionClassicMap.put("[喵喵]", R.drawable.d_miao);
        sEmotionClassicMap.put("[酷]", cn.mobcommu.zim.R.drawable.d_ku);
        sEmotionClassicMap.put("[衰]", cn.mobcommu.zim.R.drawable.d_shuai);
        sEmotionClassicMap.put("[闭嘴]", cn.mobcommu.zim.R.drawable.d_bizui);
        sEmotionClassicMap.put("[鄙视]", cn.mobcommu.zim.R.drawable.d_bishi);
        sEmotionClassicMap.put("[花心]", cn.mobcommu.zim.R.drawable.d_huaxin);
        sEmotionClassicMap.put("[鼓掌]", cn.mobcommu.zim.R.drawable.d_guzhang);
        sEmotionClassicMap.put("[悲伤]", cn.mobcommu.zim.R.drawable.d_beishang);
        sEmotionClassicMap.put("[思考]", cn.mobcommu.zim.R.drawable.d_sikao);
        sEmotionClassicMap.put("[生病]", cn.mobcommu.zim.R.drawable.d_shengbing);
        sEmotionClassicMap.put("[亲亲]", cn.mobcommu.zim.R.drawable.d_qinqin);
        sEmotionClassicMap.put("[怒骂]", cn.mobcommu.zim.R.drawable.d_numa);
        sEmotionClassicMap.put("[太开心]", cn.mobcommu.zim.R.drawable.d_taikaixin);
        sEmotionClassicMap.put("[懒得理你]", cn.mobcommu.zim.R.drawable.d_landelini);
        sEmotionClassicMap.put("[右哼哼]", cn.mobcommu.zim.R.drawable.d_youhengheng);
        sEmotionClassicMap.put("[左哼哼]", cn.mobcommu.zim.R.drawable.d_zuohengheng);
        sEmotionClassicMap.put("[嘘]", cn.mobcommu.zim.R.drawable.d_xu);
        sEmotionClassicMap.put("[委屈]", cn.mobcommu.zim.R.drawable.d_weiqu);
//        sEmotionClassicMap.put("[doge]", R.drawable.d_doge);
        sEmotionClassicMap.put("[可怜]", cn.mobcommu.zim.R.drawable.d_kelian);
        sEmotionClassicMap.put("[打哈气]", cn.mobcommu.zim.R.drawable.d_dahaqi);
        sEmotionClassicMap.put("[挤眼]", cn.mobcommu.zim.R.drawable.d_jiyan);
        sEmotionClassicMap.put("[失望]", cn.mobcommu.zim.R.drawable.d_shiwang);
        sEmotionClassicMap.put("[顶]", cn.mobcommu.zim.R.drawable.d_ding);
        sEmotionClassicMap.put("[疑问]", cn.mobcommu.zim.R.drawable.d_yiwen);
        sEmotionClassicMap.put("[困]", cn.mobcommu.zim.R.drawable.d_kun);
        sEmotionClassicMap.put("[感冒]", cn.mobcommu.zim.R.drawable.d_ganmao);
        sEmotionClassicMap.put("[拜拜]", cn.mobcommu.zim.R.drawable.d_baibai);
        sEmotionClassicMap.put("[黑线]", cn.mobcommu.zim.R.drawable.d_heixian);
        sEmotionClassicMap.put("[阴险]", cn.mobcommu.zim.R.drawable.d_yinxian);
        sEmotionClassicMap.put("[打脸]", cn.mobcommu.zim.R.drawable.d_dalian);
        sEmotionClassicMap.put("[傻眼]", cn.mobcommu.zim.R.drawable.d_shayan);
//        sEmotionClassicMap.put("[猪头]", R.drawable.d_zhutou);
//        sEmotionClassicMap.put("[熊猫]", R.drawable.d_xiongmao);
//        sEmotionClassicMap.put("[兔子]", R.drawable.d_tuzi);
    }

    /**
     * 根据表情名称获取当前表情图标R值
     *
     * @param emotionName 表情名称
     * @return
     */
    public static int getEmotionForName(EmotionType emotionType, String emotionName) {

        ArrayMap<String, Integer> emotionMap = getEmotionsForType(emotionType);
        Integer emotionId = emotionMap.get(emotionName);
        return emotionId == null ? cn.mobcommu.zim.R.drawable.vector_default_image : emotionId.intValue();
    }

    public static List<EmotionType> getEmotionTabList() {

        return sEmotionTabList;
    }

    /**
     * 根据表情类型获取对应的表情列表
     *
     * @param emotionType
     * @return
     */
    public static ArrayMap<String, Integer> getEmotionsForType(EmotionType emotionType) {

        switch (emotionType) {
            case EMOTION_TYPE_CLASSIC:
                return sEmotionClassicMap;
            case EMOTION_TYPE_MORE:
                return new ArrayMap<>(0);
            default:
                return new ArrayMap<>(0);
        }
    }
}
