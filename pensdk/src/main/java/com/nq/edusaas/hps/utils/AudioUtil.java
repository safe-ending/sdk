package com.nq.edusaas.hps.utils;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.text.TextUtils;
import android.util.Log;

import com.nq.edusaas.hps.PenSdkCtrl;
import com.nq.edusaas.hps.service.MediaService2;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class AudioUtil {

    public static final String TAG = AudioUtil.class.getSimpleName();
    public enum REC_STATUS {
        //未开始
        STATUS_NO_READY,
        //预备
        STATUS_READY,
        //录音
        STATUS_START,
        //暂停
        STATUS_PAUSE,
        //停止
        STATUS_STOP,
        //数据读取完毕
        STATUS_REC_FINISH,
        //数据计算完毕
        STATUS_PUT_FINISH
    }

    //status of the recording
    private REC_STATUS mCurStatus = REC_STATUS.STATUS_NO_READY;
    private REC_STATUS mLastStatus = REC_STATUS.STATUS_NO_READY;

    private static AudioUtil mInstance;
    private AudioRecord recorder;
    //录音源
    private static int audioSource = MediaRecorder.AudioSource.MIC;
    //录音的采样频率
    private static int audioRate = 16000;
    //录音的声道，单声道
    private static int audioChannel = AudioFormat.CHANNEL_IN_MONO;
    //量化的深度
    private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    //缓存的大小
    private static int bufferSize = AudioRecord.getMinBufferSize(audioRate, audioChannel, audioFormat);
    //记录播放状态
    //    private boolean isRecording = false;
    //数字信号数组
    private byte[] noteArray;
    //文件夹名称
    public static final String folderName = "aipen";
    //PCM文件
    private File pcmFile;
    //文件输出流
    private OutputStream os;
    //录音时长
    private long recordTime;
    //开始录音时间
    private long recordTimeStart;
    //暂停录音时间
    private long recordTimePauseStart;
    private long recordTimePause;
    MediaPlayer.OnCompletionListener audioCompletionListener = null;

    //    private boolean isPause2Start;

    public interface IRecordListener {
        void onRecordStart();
        void onRecordReStart();
        void onRecordPause();
        void onRecordStop();
        void onRecordProgress(long milliseconds);
    }

    private IRecordListener recordListener;

    public void setAudioCompletionListener(MediaPlayer.OnCompletionListener audioCompletionListener) {
        this.audioCompletionListener = audioCompletionListener;
    }

    public void setRecordListener(IRecordListener listener) {
        this.recordListener = listener;
    }

    private AudioUtil() {
        recorder = new AudioRecord(audioSource, audioRate, audioChannel, audioFormat, bufferSize);
    }

    public synchronized static AudioUtil getInstance() {
        if (mInstance == null) {
            mInstance = new AudioUtil();
        }
        return mInstance;
    }

    //读取录音数字数据线程
    class WriteThread implements Runnable {
        public void run() {
            writeData();
        }
    }

    private void setStatus(REC_STATUS status) {
        this.mLastStatus = this.mCurStatus;
        this.mCurStatus = status;
        Log.d(TAG, "set status=" + status + ", last status=" + mLastStatus);
    }

    public REC_STATUS getRecStatus() {
        return this.mCurStatus;
    }

    public REC_STATUS getLastRecStatus() {
        return this.mLastStatus;
    }

    public void createDefaultAudio() {
        recorder = new AudioRecord(audioSource, audioRate, audioChannel, audioFormat, bufferSize);
        int state = recorder.getState();
        Log.d(TAG, "Audio state=" + state);
        //status set to READY for recording
        setStatus(REC_STATUS.STATUS_READY);
    }

    //开始录音
    public void startRecord(String fileName) {
        Log.d(TAG, "Start Recorder, with status=" + recorder.getState());
        PenSdkCtrl.getInstance().setCanDraw(true);

        if (mCurStatus == REC_STATUS.STATUS_NO_READY || TextUtils.isEmpty(fileName)) {
            throw new IllegalStateException("录音尚未初始化,请检查是否禁止了录音权限~");
        }
        if (mCurStatus == REC_STATUS.STATUS_START) {
            throw new IllegalStateException("正在录音");
        }

        //get to know the first start of the record
        boolean init_start = (mCurStatus == REC_STATUS.STATUS_READY);

        //        createFile(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/" + folderName, fileName);//创建文件
        //createFile(Common.getAudioPathDir(Common.getUserId(), Common.getCurrentNotebookId(), Common.getCurrentPage(), ""), fileName);//创建文件
        createFile(Common.getAudioPathDir(), fileName);

        recorder.startRecording();
        setStatus(REC_STATUS.STATUS_START);
        if (init_start) {
            recordData();
        }
    }

    //停止录音
    public void stopRecord() {
//        Log.e(TAG, "停止录音 "+TimeUtil.convertToTime(TimeUtil.FORMAT_DAY_CN, System.currentTimeMillis()));
        if (mCurStatus == REC_STATUS.STATUS_START) {

            if (null != recorder) {
                recorder.stop();
                setStatus(REC_STATUS.STATUS_STOP);
            }
            release();

            if (null != recordListener) {
                recordListener.onRecordStop();
            }
        } else if (mCurStatus == REC_STATUS.STATUS_PAUSE) {
            setStatus(REC_STATUS.STATUS_STOP);
            release();
            if(mLastStatus == REC_STATUS.STATUS_STOP){
                if (null != recordListener) {
                    recordListener.onRecordStop();
                }
            }
        }
        PenSdkCtrl.getInstance().setCanDraw(false);
    }

    public void pauseRecord() {
        if (mCurStatus == REC_STATUS.STATUS_START) {
            //            throw new IllegalStateException("没有在录音");
            if (null != recorder) {
                recorder.stop();
                setStatus(REC_STATUS.STATUS_PAUSE);
                PenSdkCtrl.getInstance().setCanDraw(false);
                recordTimePauseStart = System.currentTimeMillis();
//                Log.e(TAG, "暂停录音 "+TimeUtil.convertToTime(TimeUtil.FORMAT_DAY_CN, System.currentTimeMillis()));
                if(null!=recordListener){
                    recordListener.onRecordPause();
                }
            }
        } else if (mCurStatus == REC_STATUS.STATUS_PAUSE) {
            reStartRecord();
        }
    }

    public void reStartRecord() {
        if (null != recorder) {
            //            isPause2Start = true;
            recorder.startRecording();
            setStatus(REC_STATUS.STATUS_START);
            PenSdkCtrl.getInstance().setCanDraw(true);
            recordTimePause = System.currentTimeMillis() - recordTimePauseStart + recordTimePause;
            Common.setPauseTime(recordTimePause);
//            Log.e(TAG, "恢复录音 "+TimeUtil.convertToTime(TimeUtil.FORMAT_DAY_CN, System.currentTimeMillis()));
//            Log.e(TAG, "暂停时长 "+recordTimePause/1000+"秒");
            if(null!=recordListener){
                recordListener.onRecordReStart();
            }
        }
    }

    public void release() {
        Log.i(TAG, "Release: releasing audio recorder");
        recordTimePause = 0;
        recordTimePauseStart = 0;
        //release the record
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
        setStatus(REC_STATUS.STATUS_NO_READY);
    }

    //录音时长
    public long getRecordTime(){
//        Log.e(TAG, "录音时长 "+recordTime/1000+"秒");
        return recordTime;
    }

    /**
     * 返回当前录音pcm文件
     * @return
     */
    public File getPcmFile(){
        return this.pcmFile;
    }

    //将数据写入文件夹,文件的写入没有做优化
    public void writeData() {
        noteArray = new byte[bufferSize];
        //建立文件输出流
        try {
            os = new BufferedOutputStream(new FileOutputStream(pcmFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        recordTime = 0;
        recordTimeStart = System.currentTimeMillis();
        Common.setStartTime(recordTimeStart);
//        Log.e(TAG, "开始录音 "+TimeUtil.convertToTime(TimeUtil.FORMAT_DAY_CN, recordTimeStart));
        recordTimePause = 0;

        while (mCurStatus == REC_STATUS.STATUS_START ||
                mCurStatus == REC_STATUS.STATUS_PAUSE) {
            if (mCurStatus == REC_STATUS.STATUS_START) {
                int recordSize = recorder.read(noteArray, 0, bufferSize);
                if (recordSize > 0) {
                    try {
                        os.write(noteArray);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                recordTime = System.currentTimeMillis() - recordTimeStart - recordTimePause;
                if(null!=recordListener){
                    recordListener.onRecordProgress(recordTime);
                }
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface OnConvert {
        void onFinish();
    }
//
//    public void play(final String filePath, String wavPath, OnConvert onConvert) {
////        new PlayAsyncTask().execute(filePath, wavPath);
//        RxHelper.runOnBackground(new RxTaskCall<String>() {
//            @Override
//            public String doInBackground() {
//                convertWaveFile(filePath, wavPath);
//                return wavPath;
//            }
//
//            @Override
//            public void onResult(String result) {
//                if (result != null && !TextUtils.isEmpty(result)) {
//                    toPlay(result);
//                    onConvert.onFinish();
//                }
//            }
//        });
//    }

//    class PlayAsyncTask extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... params) {
//            convertWaveFile(params[0], params[1]);
//            return params[1];
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            toPlay(s);
//        }
//    }

    public void toPlay(String url) {
        this.releaseAudioTrack();
        MediaService2.getInstance().play(url);

        if (audioCompletionListener != null)
            MediaService2.getInstance().setOnCompletionListener(audioCompletionListener);
    }

    public void stopPlay() {
        MediaService2.getInstance().stop();
    }

    private void releaseAudioTrack() {
        MediaService2.getInstance().stop();
    }

    // 这里得到可播放的音频文件
    public void convertWaveFile(String inFileName, String wavFile) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = AudioUtil.audioRate;
        int channels = 1;
        long byteRate = 16 * AudioUtil.audioRate * channels / 8;
        byte[] data = new byte[bufferSize];
        try {
            in = new FileInputStream(inFileName);
            out = new FileOutputStream(wavFile);
            totalAudioLen = in.getChannel().size();
            //由于不包括RIFF和WAV
            totalDataLen = totalAudioLen + 36;
            writeWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
            while (in.read(data) != -1) {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* 任何一种文件在头部添加相应的头文件才能够确定的表示这种文件的格式，wave是RIFF文件结构，每一部分为一个chunk，
    其中有RIFF WAVE chunk， FMT Chunk，Fact chunk,Data chunk,其中Fact chunk是可以选择的， */
    private void writeWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate,
                                     int channels, long byteRate) throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);//数据大小
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';//WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        //FMT Chunk
        header[12] = 'f'; // 'fmt '
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';//过渡字节
        //数据大小
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        //编码方式 10H为PCM编码格式
        header[20] = 1; // format = 1
        header[21] = 0;
        //通道数
        header[22] = (byte) channels;
        header[23] = 0;
        //采样率，每个通道的播放速度
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        //音频数据传送速率,采样率*通道数*采样深度/8
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
        header[32] = (byte) (1 * 16 / 8);
        header[33] = 0;
        //每个样本的数据位数
        header[34] = 16;
        header[35] = 0;
        //Data chunk
        header[36] = 'd';//data
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }

    //创建文件夹,首先创建目录，然后创建对应的文件
    public void createFile(String basePath) {
        File baseFile = new File(basePath);
        if (!baseFile.exists())
            baseFile.mkdirs();
        pcmFile = new File(basePath + "/yinfu.pcm");
        if (pcmFile.exists()) {
            pcmFile.delete();
        }
        try {
            pcmFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFile(String basePath, String fileName) {
        File baseFile = new File(basePath);
        if (!baseFile.exists())
            baseFile.mkdirs();
        pcmFile = new File(basePath, fileName);
        if (pcmFile.exists()) {
            pcmFile.delete();
        }
        try {
            pcmFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //记录数据
    public void recordData() {
        if(null!=recordListener){
            recordListener.onRecordStart();
        }
        new Thread(new WriteThread()).start();
    }

    public void setSoundLoud(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //        currentVolume = currentVolume + 10;
        //        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, AudioManager.FLAG_SHOW_UI);
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI | AudioManager.FLAG_PLAY_SOUND);
    }

    public void setSoundLow(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //        currentVolume = currentVolume - 10;
        //        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, AudioManager.FLAG_SHOW_UI);
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI | AudioManager.FLAG_PLAY_SOUND);
    }

    public void setSilence(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_SHOW_UI);
    }


    public long getAudioRecordTime(String path) {
        File file = new File(path);
        if (!file.exists()) return 0;
        long blockSize = 0;
        try {
            FileInputStream fis = new FileInputStream(file);
            blockSize = fis.available();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //数据量=（采样频率×采样位数×声道数×时间）/8
        int parameter = audioRate * audioChannel / 8;
        return (long) Math.ceil(blockSize * 1000 / (double) parameter);
    }
}
