package cn.mobcommu.zim.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Vibrator;

import java.util.HashMap;

import cn.mobcommu.zim.R;

/**
 * 接收消息通知类
 */

public class NotificationHelper {

    public static final int KEY_SOUND_A1 = 1;
    // 一般把多个声音放到HashMap中去
    HashMap<Integer, Integer> soundPoolMap = new HashMap<Integer, Integer>();

    public void playNotificationSound(Context context) {

        // 可以用SoundPool播放一些短的反应速度要求高的声音。
        // 具有 “池”的能力（缓存），它先加载声音文件到内存，以支持多次播放声音文件。
        SoundPool soundPool;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
        }

        // 加载声音文件
        soundPoolMap.put(KEY_SOUND_A1, soundPool.load(context, R.raw.message_notification, 1));

        // 注册一个监听器，在加载声音完毕后再播放
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(soundPoolMap.get(KEY_SOUND_A1), 1, 1, 0, 0, 1);
            }
        });
    }

    /**
     * 通知用户，无提示、震动或声音
     */
    public void notifyUser(Context context) {

        // 获取情景模式类型
        AudioManager audioService = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = audioService.getRingerMode();

        if (ringerMode == AudioManager.RINGER_MODE_NORMAL) { // 正常模式,有声,是否震动取决于原来系统声音设置中振动的设置
            playNotificationSound(context);
        } else if (ringerMode == AudioManager.RINGER_MODE_SILENT) { // 静音模式,无声不震
            return;
        } else if (ringerMode == AudioManager.RINGER_MODE_VIBRATE) { // 震动模式,无声,震动
            vibrator(context);
        }
    }

    /**
     * 震动
     * @param context
     */
    public void vibrator(Context context) {
        // 获得震动服务
        Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        //震动
        long [] pattern = {100,250,100,150}; // 停止 开启 停止 开启
        vibrator.vibrate(pattern, -1); //震动一次，index设为-1
    }

}
