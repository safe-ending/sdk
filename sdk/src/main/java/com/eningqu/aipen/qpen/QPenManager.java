package com.eningqu.aipen.qpen;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.os.IBinder;

import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.ScreenUtils;
import com.eningqu.aipen.BuildConfig;
import com.eningqu.aipen.R;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.HwrEngineEnum;
import com.eningqu.aipen.common.enums.NoteTypeEnum;
import com.eningqu.aipen.common.utils.AudioUtil;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.qpen.bean.AFStrokeAndPaint;
import com.eningqu.aipen.qpen.bean.CommandBase;
import com.eningqu.aipen.qpen.bean.CommandRecord;
import com.eningqu.aipen.qpen.bean.CommandSize;
import com.eningqu.aipen.qpen.bean.CommandSound;
import com.eningqu.aipen.qpen.service.BgWriterService;
import com.nq.edusaas.hps.PenSdkCtrl;

import java.util.ArrayList;
import java.util.List;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/5/29 15:42
 * desc   :
 * version: 1.0
 */
public class QPenManager {

    public static final String TAG = QPenManager.class.getSimpleName();

    private static QPenManager instance;

    public static QPenManager getInstance() {
        if (null == instance) {
            synchronized (QPenManager.class) {
                if (null == instance) {
                    instance = new QPenManager();
                }
            }
        }
        return instance;
    }

    private Canvas mCanvas;
    private Paint mPaint;
//    private float paintSizeCache;
    private int paintColorCache;
    protected String curRecordFileName;//当前录音文件
    private HwrEngineEnum hwrEngineEnum;

    private boolean needInit = true;//是否需要初始化，默认需要

    private boolean isNQAuth = false;//是否已设备授权

    /**
     * 当前Bitmap
     */
    private Bitmap mCurrentBitmap;
    private Context mContext;

    private List<AFStrokeAndPaint> mCurStrokeAndPaint = new ArrayList<>();
    /**
     * 默认的笔大小规格
     */
    private int mCurrentPenSizeType = CommandSize.PEN_SIZE_ONE;

    public void init(Context context) {
        this.mContext = context.getApplicationContext();
        initDraw();
//        if ("MY_SCRIPT".equals(HwrEngineEnum.MY_SCRIPT.toString()))
//            setHwrEngineEnum(HwrEngineEnum.MY_SCRIPT);
//        else

        setHwrEngineEnum(HwrEngineEnum.getEnum("MY_SCRIPT"));
        bindService(mContext);
    }

    public void unInit() {
        unbindService(mContext);
    }

    private void initDraw() {
        mCanvas = new Canvas();
        mCanvas.drawColor(Color.WHITE);
        mCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        float density = ScreenUtils.getScreenDensity();
        if (density < 2.0f) {
            mPaint.setStrokeWidth(0.8f);
        } else if (density < 3.0f) {
            mPaint.setStrokeWidth(1.2f);
        } else if (density < 4.0f) {
            mPaint.setStrokeWidth(1.6f);
        } else {
            mPaint.setStrokeWidth(2.0f);
        }
        mPaint.setAlpha(220);
        mPaint.setColor(ContextCompat.getColor(mContext, R.color.app_login_text_black));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        //        paintSizeCache = mPaint.getStrokeWidth();
        //        paintColorCache = mPaint.getColor();
    }

    public List<AFStrokeAndPaint> getCurStrokeAndPaint() {
        return mCurStrokeAndPaint;
    }

    /*** 获取canvas*/
    public Canvas getCanvas() {
        mCanvas.setBitmap(mCurrentBitmap);
        return mCanvas;
    }

    public Paint getPaint() {
        return mPaint;
    }

    public int getPaintCacheColor() {
        return this.paintColorCache;
    }

    public boolean isNeedInit() {
        return needInit;
    }

    public void setNeedInit(boolean needInit) {
        this.needInit = needInit;
    }

    /*** 获取当前bitmap*/
    public Bitmap getCurrentBitmap() {
        return mCurrentBitmap;
    }

    /**
     * 设置当前bitmap
     */
    public void setCurrentBitmap(Bitmap bitmap) {
        mCurrentBitmap = bitmap;
    }


    public HwrEngineEnum getHwrEngineEnum() {
        return hwrEngineEnum;
    }

    public void setHwrEngineEnum(HwrEngineEnum hwrEngineEnum) {
        this.hwrEngineEnum = hwrEngineEnum;
    }

    /*** 设置画笔颜色*/
    public void setPaintColor(int colorId) {
        if (mPaint != null) {
            mPaint.setColor(colorId);
        }
        paintColorCache = colorId;
    }

    /*** 获取画笔颜色*/
    public int getPaintColor() {
        if (mPaint != null) {
            return mPaint.getColor();
        }
        return Color.BLACK;
    }

    public void setPaintSize(float size) {
        if (mPaint != null) {
            mPaint.setStrokeWidth(size);
        }
    }

    public float getPaintSize() {
        if (mPaint == null) {
            return 2;
        }
        float size = mPaint.getStrokeWidth();
        return size;
    }

    public int getPenSizeType() {
        return mCurrentPenSizeType;
    }

    public void setPenSizeType(int penSizeType) {
        mCurrentPenSizeType = penSizeType;
    }


    protected BgWriterService.BgWriterBinder bgWriterBinder;

    protected ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bgWriterBinder = (BgWriterService.BgWriterBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (null != bgWriterBinder) {
                bgWriterBinder.getInstance().cancelNotification();
                bgWriterBinder = null;
            }
//            AFPenClientCtrl.getInstance().disconnect();
            PenSdkCtrl.getInstance().disconnect();
        }
    };

    public void showNotification() {
        if (null != bgWriterBinder) {
            bgWriterBinder.getInstance().showNotification();
        }
    }

    public void cancelNotification() {
        if (null != bgWriterBinder) {
            bgWriterBinder.getInstance().cancelNotification();
        }
    }

    /**
     * 去获取授权
     */
    public void toAuth() {
        if (null != bgWriterBinder) {
            bgWriterBinder.getInstance().toCheckAuth();
        }
    }

//    public boolean isNQAuth() {
//        return isNQAuth;
//    }
//
//    public void setNQAuth(boolean NQAuth) {
//        isNQAuth = NQAuth;
//    }

    /**
     * 绑定后台服务
     */
    protected void bindService(Context context) {
        Intent intent = new Intent(context, BgWriterService.class);
        if (context != null) {
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * 解绑后台服务
     */
    protected void unbindService(Context context) {
        try{
            if (context != null) {
                context.unbindService(serviceConnection);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 功能指令处理，不做界面提示
     *
     * @param commandBase
     */
    public void onCommand(CommandBase commandBase) {
        if (null != commandBase) {
            switch (commandBase.getSizeLevel()) {
                case CommandBase.COMMAND_TYPE_RECORD:
                    CommandRecord commandRecord = (CommandRecord) commandBase;
                    switch (commandRecord.getCode()) {
                        case CommandRecord.RECORD_START:
                            if (AudioUtil.getInstance().getLastRecStatus() == AudioUtil.REC_STATUS.STATUS_START &&
                                    AudioUtil.getInstance().getRecStatus() == AudioUtil.REC_STATUS.STATUS_PAUSE) {
                                AudioUtil.getInstance().pauseRecord();
                            } else {
                                if (AudioUtil.getInstance().getRecStatus() == AudioUtil.REC_STATUS.STATUS_START) {
                                    return;
                                }
                                curRecordFileName = System.currentTimeMillis() / 1000 + AppCommon.SUFFIX_NAME_PCM;
                                AudioUtil.getInstance().stopRecord();
                                AudioUtil.getInstance().createDefaultAudio();
                                AudioUtil.getInstance().startRecord(mContext, AppCommon.getAudioPathDir(AppCommon.getUserUID(),
                                        AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), ""), curRecordFileName);//AudioUtil.folderName
                                L.error(TAG, "start record pcm=" + AudioUtil.getInstance().getPcmFile().getPath());
                                //点击录音时，尝试创捷页签
                                AppCommon.insertData(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), NoteTypeEnum.NOTE_TYPE_A5.getNoeType());
                            }
                            break;
                        case CommandRecord.RECORD_PAUSE:
                            AudioUtil.getInstance().pauseRecord();
                            break;
                        case CommandRecord.RECORD_STOP:
                            //录音文件重命名，添加录音时长_开始录音时间
//                            String oldName = curRecordFileName;
//                            curRecordFileName = AudioUtil.getInstance().getRecordTime() + "_" + curRecordFileName;
//                            File pcm = AudioUtil.getInstance().getPcmFile();
//                            if (null != pcm && pcm.exists()) {
//                                String filePath = pcm.getPath().replace(oldName, curRecordFileName);
//                                L.error(TAG, "rename before pcm=" + pcm.getPath());
//                                L.error(TAG, "filePath=" + filePath);
//                                L.error(TAG, "getAbsolutePath=" + pcm.getAbsolutePath());
//                                if (!pcm.renameTo(new File(filePath))) {
//                                    L.error(TAG, "Audio file rename fail rename=" + curRecordFileName);
//                                }
//                                L.error(TAG, "rename after pcm=" + pcm.getPath());
//                            }
                            AudioUtil.getInstance().stopRecord();
                            break;
                    }
                    break;
                case CommandBase.COMMAND_TYPE_COLOR:
                    //颜色选择  页面中有了再写会重复
//                    CommandColor commandColor = (CommandColor) commandBase;
//                    switch (commandColor.getCode()) {
//                        case CommandColor.PEN_COLOR_RED:
//                            setPaintColor(ContextCompat.getColor(mContext, R.color.colors_menu_red));
//                            break;
//                        case CommandColor.PEN_COLOR_GREEN:
//                            setPaintColor(ContextCompat.getColor(mContext, R.color.colors_menu_green));
//                            break;
//                        case CommandColor.PEN_COLOR_BLUE:
//                            setPaintColor(ContextCompat.getColor(mContext, R.color.colors_menu_blue));
//                            break;
//                        case CommandColor.PEN_COLOR_BLACK:
//                            setPaintColor(ContextCompat.getColor(mContext, R.color.colors_menu_black));
//                            break;
//                    }
                    break;
                case CommandBase.COMMAND_TYPE_SIZE:
                    // 页面中有了再写会重复
//                    CommandSize command = (CommandSize) commandBase;
//                    QPenManager.getInstance().setPenSizeType(command.getCode());
                    break;
                case CommandBase.COMMAND_TYPE_SOUND:
                    CommandSound commandSound = (CommandSound) commandBase;
                    switch (commandSound.getCode()) {
                        case CommandSound.SOUND_LOUD:
                            AudioUtil.getInstance().setSoundLoud(mContext);
                            break;
                        case CommandSound.SOUND_LOW:
                            AudioUtil.getInstance().setSoundLow(mContext);
                            break;
                        case CommandSound.SOUND_SILENCE:
                            AudioUtil.getInstance().setSilence(mContext);
                            break;
                    }
                    break;
            }
        }
    }
}
