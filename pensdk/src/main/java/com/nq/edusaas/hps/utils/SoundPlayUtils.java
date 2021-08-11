package com.nq.edusaas.hps.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.nq.edusaas.pensdk.R;


/**
 * 播放提示声音
 * Created by liuke on 2018/9/18.
 */
public class SoundPlayUtils {
    // SoundPool对象
    private static SoundPool mSoundPlayer = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
    private static SoundPlayUtils soundPlayUtils;

    public static final int SK_START = 1;
    public static final int SK_STOP = 2;
    public static final int SK_START_YSD = 3;

    private static int skStart = 1;
    private static int skStop = 2;
    private static int skStartYsd = 3;

    /**
     * 初始化
     * @param context 上下文
     */
    public static SoundPlayUtils init(Context context) {
        if (soundPlayUtils == null) {
            soundPlayUtils = new SoundPlayUtils();
        }
        // 初始化声音
//        skStart = mSoundPlayer.load(context, R.raw.sk_start, 1);// 1
        skStop = mSoundPlayer.load(context, R.raw.sk_stop, 1);// 2
//        skStartYsd = mSoundPlayer.load(context, R.raw.sk_start_ysd, 1);// 3
        return soundPlayUtils;
    }

    /**
     * 播放声音
     * @param soundID 声音ID
     */
    public static void play(int soundID) {
        int id = getId(soundID);
        mSoundPlayer.play(id, 1, 1, 1, 0, 1);
    }
    /**
     * 播放声音
     * @param soundID 声音ID
     */
    public static int playCircle(int soundID) {
        int id = getId(soundID);
        return mSoundPlayer.play(id, 1, 1, 0, -1, 1);
    }

    /**
     * 停止播放
     * @param soundID 声音ID
     */
    public static void stop(int soundID) {
        mSoundPlayer.stop(soundID);
    }

    private static int getId(int soundID) {
        int id;
        switch (soundID) {
            case SK_START:
                id = skStart;
                break;
            case SK_STOP:
                id = skStop;
                break;
            case SK_START_YSD:
                id = skStartYsd;
                break;
            default:
                id = skStart;
                break;
        }
        return id;
    }
}
