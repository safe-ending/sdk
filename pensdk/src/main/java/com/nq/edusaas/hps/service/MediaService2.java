package com.nq.edusaas.hps.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.IOException;

/**
* @Author: Qiu.Li
* @Create Date: 2019/5/11 14:07
* @Description: 
* @Email: liqiupost@163.com
*/
public class MediaService2 extends Service {

    private static final String TAG = "MediaService";

    //初始化MediaPlayer
    public MediaPlayer mMediaPlayer;
    private static MediaService2 mediaService;
    private MediaPlayer.OnCompletionListener mCompletionListener;
    private MediaPlayer.OnErrorListener mOnErrorListener;

    public MediaService2() {

    }

    public static MediaService2 getInstance() {

        if (mediaService == null) {
            synchronized (MediaService2.class) {
                if (mediaService == null) {
                    mediaService = new MediaService2();
                }
            }
        }
        return mediaService;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //        /**
    //         *  获取MediaService.this（方便在ServiceConnection中）
    //         *
    //         * *//*
    //        public MediaService getInstance() {
    //            return MediaService.this;
    //        }*/

    /**
     * 播放音乐
     */
    public void play() {
        if (!isPlaying()) {
            //如果还没开始播放，就开始
            mMediaPlayer.start();
        }
    }

    /**
     * 暂停播放
     */
    public void pause() {
        if (isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (isPlaying()) {
            mMediaPlayer.stop();
        }
    }

    /**
     * reset
     */
    public void resetMusic() {
        if (!isPlaying()) {
            //如果还没开始播放，就开始
            mMediaPlayer.reset();
        }
    }

    public boolean isPlaying() {
        boolean ret = false;
        try {
            if (null == mMediaPlayer) {
                mMediaPlayer = new MediaPlayer();
            }
            return mMediaPlayer.isPlaying();
        } catch (Exception e) {

        }
        return ret;
    }

    /**
     * 关闭播放器
     */
    public void closeMedia() {
        try {
            if (mMediaPlayer != null) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 获取歌曲长度
     **/
    public int getProgress() {

        return mMediaPlayer.getDuration();
    }

    /**
     * 获取播放位置
     */
    public int getPlayPosition() {
        if(mMediaPlayer == null){
            return 0;
        }
        return mMediaPlayer.getCurrentPosition() / 1000;
    }

    public long getCurrentPosition() {
        if(mMediaPlayer == null){
            return 0;
        }
        return mMediaPlayer.getCurrentPosition();
    }

    /**
     * 播放指定位置
     */
    public void seekToPositon(int msec) {
        mMediaPlayer.seekTo(msec);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setMediaPlayerSpeed(float speed) {
        if (null != mMediaPlayer) {
            PlaybackParams playbackParams = new PlaybackParams();
            playbackParams.setSpeed(speed);
            mMediaPlayer.setPlaybackParams(playbackParams);
        }
    }

    /**
     * 播放网络音乐
     *
     * @param url
     */
    public long play(String url) {
        long duration = 0;
        try {
            if (null != mMediaPlayer) {
                closeMedia();
            }
            mMediaPlayer = new MediaPlayer();
            setCompletionListener();
            setOnErrorlistener();
            //此处的两个方法需要捕获IO异常
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(url);
            //让MediaPlayer对象准备
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(onPreparedListener);
            //            duration = mMediaPlayer.getDuration();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return duration;
    }

    MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            play();
        }
    };

    /**
     * 播放网络音乐
     *
     * @param url
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public long play(String url, float speed) {
        long duration = 0;
        try {
            if (null != mMediaPlayer) {
                closeMedia();
            }
            mMediaPlayer = new MediaPlayer();
            setCompletionListener();
            setOnErrorlistener();
            //此处的两个方法需要捕获IO异常
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(url);
            //set speed
            setMediaPlayerSpeed(speed);
            //让MediaPlayer对象准备
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(onPreparedListener);
            //            duration = mMediaPlayer.getDuration();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return duration;
    }

    private void setCompletionListener() {
        if (null != mMediaPlayer) {
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (null != mCompletionListener) {
                        mCompletionListener.onCompletion(mediaPlayer);
                    }
                    //                    mediaPlayer.stop();
                }
            });
        }
    }

    private void setOnErrorlistener() {
        if (null != mMediaPlayer) {
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    if (null != mOnErrorListener) {
                        mOnErrorListener.onError(mp, what, extra);
                    }
                    return true;
                }
            });
        }
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        this.mCompletionListener = listener;
    }

    public void setOnErrorListener(MediaPlayer.OnErrorListener listener) {
        this.mOnErrorListener = listener;
    }
}
