package cn.mobcommu.zim.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.orhanobut.logger.Logger;

import java.util.HashMap;

import cn.mobcommu.zim.R;

/**
 * 接收消息通知类
 */

public class NotificationHelper {

    public static final int KEY_SOUND_A1 = 1;
    HashMap<Integer, Integer> soundPoolMap = new HashMap<Integer, Integer>();

    public void playNotificationSound(Context context) {

        SoundPool soundPool;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
        }
        soundPoolMap.put(KEY_SOUND_A1, soundPool.load(context, R.raw.message_notification, 1));
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(soundPoolMap.get(KEY_SOUND_A1), 1, 1, 0, 0, 1);
            }
        });


    }

}
