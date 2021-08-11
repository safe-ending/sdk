package com.eningqu.aipen.p20;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.SDKManager;
import com.eningqu.aipen.activity.MainActivity;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.EventBusCarrier;
import com.eningqu.aipen.common.bluetooth.BluetoothClient;
import com.eningqu.aipen.common.enums.NoteTypeEnum;
import com.eningqu.aipen.common.utils.EventBusUtil;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.common.utils.LocationUtils;
import com.eningqu.aipen.common.utils.NingQuLog;
import com.eningqu.aipen.common.utils.SpUtils;
import com.eningqu.aipen.common.utils.TimeUtil;
import com.eningqu.aipen.common.utils.ToastUtils;
import com.eningqu.aipen.common.utils.UserManager;
import com.eningqu.aipen.db.model.BluetoothDevice;
import com.eningqu.aipen.db.model.UserInfoData;
import com.eningqu.aipen.qpen.AFPenClientCtrl;
import com.eningqu.aipen.qpen.IPenOfflineDataSyncListener;
import com.eningqu.aipen.qpen.PAGE_OPEN_STATUS;
import com.eningqu.aipen.qpen.PEN_CONN_STATUS;
import com.eningqu.aipen.qpen.PEN_SYNC_STATUS;
import com.eningqu.aipen.qpen.POLL_SWITCH_STATUS;
import com.eningqu.aipen.qpen.QPenManager;
import com.eningqu.aipen.qpen.SDKUtil;
import com.eningqu.aipen.qpen.StrokesUtilForQpen;
import com.eningqu.aipen.qpen.bean.CommandBase;
import com.eningqu.aipen.qpen.bean.CommandSize;
import com.eningqu.aipen.sdk.bean.DotType;
import com.eningqu.aipen.sdk.bean.NQDot;
import com.eningqu.aipen.sdk.bean.device.NQBtDevice;
import com.eningqu.aipen.sdk.bean.device.NQDeviceBase;
import com.eningqu.aipen.sdk.comm.JsonTag;
import com.eningqu.aipen.sdk.comm.ScanListener;
import com.eningqu.aipen.sdk.listener.PenDotListener;
import com.eningqu.aipen.sdk.listener.PenMsgListener;
import com.eningqu.aipen.sdk.listener.PenOfflineDataListener;
import com.nq.edusaas.hps.PenSdkCtrl;
import com.nq.edusaas.hps.sdkcore.afpensdk.Const;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DotListenerService extends Service {
    public static final String TAG = "DotListenerService";
    private final InnerHandler mHandler = new InnerHandler(DotListenerService.this);

    public boolean isOperate = false;//是否在此页面操作笔记
    private final int offsetY = 0;//Y偏移量
    private final int offsetX = 0;//X偏移量
    public PenDotListener penDotListener;
    private UserInfoData userInfo;
    public PenOfflineDataListener offlineDataListener;
    private static DotListenerService myInstance;

    public static DotListenerService getInstance() {
        if (myInstance == null) {
            synchronized (DotListenerService.class) {
                if (null == myInstance) {
                    myInstance = new DotListenerService();
                }
            }
        }
        return myInstance;
    }


    /**
     * 声明静态Handler内部类，并持有外部类弱引用，避免内存泄漏的handler正确使用方式。
     */
    private static class InnerHandler extends Handler {
        private final WeakReference<DotListenerService> dotListenerService;

        public InnerHandler(DotListenerService context) {
            dotListenerService = new WeakReference<DotListenerService>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            DotListenerService context = dotListenerService.get();
            if (context != null) {
                switch (msg.what) {
                    case 1:
                        context.isOperate = false;
                        break;
                    case 200:
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                context.isOperate = false;
                            }
                        }, 300);
                        break;

                    default:
                        break;
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {

        initView();
        return null;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private long progress;
    private long lastTime;

    private void initView() {
        if (userInfo == null) {
            userInfo = UserManager.loadUserInfo();
        }

        if (mStrokesCacheQueue == null) {
            //待保存的笔画队列
            mStrokesCacheQueue = new ConcurrentLinkedQueue<>();
        }

        if (mStrokeDotQueue == null) {
            //创建暂时缓存当前笔画的队列
            mStrokeDotQueue = new ConcurrentLinkedQueue<>();
        }

        if (mSaveStrokesThread == null) {
            //创建循环保存笔画的线程
            mSaveStrokesThread = new SaveStrokesThread();
        }
        mSaveStrokesThread.setDaemon(true);
        mSaveStrokesThread.start();


        if (penDotListener == null) {
            initListener();
        }
        PenSdkCtrl.getInstance().setDotListener(penDotListener);
        if (offlineDataListener == null) {
            offlineDataListener = new PenOfflineDataListener() {
                @Override
                public void offlineDataSyncStart(long l) {
                    L.debug("离线笔记数量", "offlineDataSyncStart>" + l);
                    Log.e("测试方法","offlineDataSyncStop");
                    if (penOfflineDataListener != null) {
                        penOfflineDataListener.onSyncBegin();
                    }
                    AFPenClientCtrl.getInstance().setSyncStatus(PEN_SYNC_STATUS.SYNCHRONIZING);
                }

                @Override
                public void offlineDataSyncStop() {
//                  L.debug("离线笔记", "");
                    AFPenClientCtrl.getInstance().setSyncStatus(PEN_SYNC_STATUS.NONE);
                    if (penOfflineDataListener != null) {
                        penOfflineDataListener.onSyncEnd();
                    }
                }

                @Override
                public void offlineDataDidReceivePenData(NQDot nqDot) {
                    Log.e("测试方法","offlineDataDidReceivePenData");

//                    AFPenClientCtrl.getInstance().setSyncStatus(PEN_SYNC_STATUS.SYNCHRONIZING);
//                  L.debug("离线笔记",">"+nqDot.x+"--"+nqDot.y);
                    NQDot dot = new NQDot();
                    dot.book_height = nqDot.book_height;
                    dot.book_width = nqDot.book_width;
                    //过滤笔返回的异常页面
                    if (nqDot.page > 186 || nqDot.page < 1) {
                        dot.page = AppCommon.getCurrentPage();
                    } else {
                        dot.page = nqDot.page;
                    }                    dot.type = nqDot.type;
                    dot.bookNum = NoteTypeEnum.NOTE_TYPE_A5.getNoeType();
                    dot.x = nqDot.x + offsetX;
                    dot.y = nqDot.y + offsetY;
                    AFPenClientCtrl.getInstance().getOfflineDataDots().add(dot);
                    if (dot.type == com.eningqu.aipen.sdk.bean.DotType.PEN_ACTION_UP) {
                        AFPenClientCtrl.getInstance().getSet().add(dot.page);
                    }
                    final long now = System.currentTimeMillis();
                    if (penOfflineDataListener != null && progress != 0 && now - lastTime > 1500) {
                        lastTime = now;
                        double pr = div(AFPenClientCtrl.getInstance().getOfflineDataDots().size() * 10, progress, 2);
                        penOfflineDataListener.onSyncProgress((int) (pr));
                    }

                }

                @Override
                public void offlineDataDel() {
                    L.debug("离线笔记", "offlineDataDel");
                }

                @Override
                public void offlineDataLength(int l) {
                    if (l > 0) {
                        Intent intent = new Intent(Const.Broadcast.ACTION_PEN_MESSAGE);
                        intent.putExtra(Const.Broadcast.MESSAGE_TYPE, Const.PenMsgType.PEN_CUR_MEMOFFSET);
                        intent.putExtra(JsonTag.INT_DOTS_MEMORY_OFFSET, (int) l);
                        Context context = SDKManager.getInstance().getApplication();
                        if (context != null){
                            context.sendBroadcast(intent);
                        }
                    }
                    L.debug("离线笔记", "offlineDataLength" + ">" + l);
                }

            };
            PenSdkCtrl.getInstance().setPenOfflineDataListener(offlineDataListener);
        }

        PenSdkCtrl.getInstance().setPenOfflineDataListener(offlineDataListener);

        PenSdkCtrl.getInstance().setPenMsgListener(new PenMsgListener() {
            @Override
            public void onEditName(String s) {

            }

            @Override
            public void onFWVer(String s) {

            }

            @Override
            public void onMCUVer(String s) {

            }

            @Override
            public void onBatInfo(float v) {
                EventBusCarrier eventBusCarrier = new EventBusCarrier();
                eventBusCarrier.setEventType(Constant.POWER_CODE);
                eventBusCarrier.setObject(AppCommon.convertPower(v));
                EventBusUtil.post(eventBusCarrier);
                PenSdkCtrl.getInstance().setLastPower(AppCommon.convertPower(v));
            }

            @Override
            public void onFlashUsedAmount(int i) {

            }

            @Override
            public void onSerialNumber(String s) {

            }

            @Override
            public void onSerialDevId(String s) {

            }

            @Override
            public void onSerialDevFlash(byte[] bytes) {

            }

            @Override
            public void onSerialDevSetFlash(int i) {

            }

            @Override
            public void onSerialDevSetId(int i) {

            }

            @Override
            public void onSerialDevVer(String s) {

            }
        });
        mHandler.post(runnable);
        if (AFPenClientCtrl.getInstance().getSyncStatus() != PEN_SYNC_STATUS.SYNCHRONIZING) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    PenSdkCtrl.getInstance().requestBatInfo();
                }
            }, 2000);
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (AFPenClientCtrl.getInstance().getSyncStatus() != PEN_SYNC_STATUS.SYNCHRONIZING) {
                PenSdkCtrl.getInstance().requestBatInfo();
            }
            mHandler.postDelayed(this, 20 * 1000);
        }
    };

    private boolean iinkInitSuccess;
    private Queue<NQDot> mIInkQueue = new ConcurrentLinkedQueue<>();

    private IPenOfflineDataSyncListener penOfflineDataListener;

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        BigDecimal temp = b1.divide(b2, scale, BigDecimal.ROUND_DOWN);
        return temp.multiply(new BigDecimal(100)).doubleValue();
    }

    //剔除0  为整数添加俩位0，为钱保留8位，为币保留4位
    public static String clearZero(double number, int scale) {
        int b = (int) number;
        if (number == b) {
            DecimalFormat df = new DecimalFormat("0.00");
            String ss = df.format(b);
            return df.format(b);
        } else {

            DecimalFormat df = new DecimalFormat("0.00");
            if (scale == 0) {
                df = new DecimalFormat("0");
            } else if (scale == 8) {
                df = new DecimalFormat("0.00000000");
            } else if (scale == 6) {
                df = new DecimalFormat("0.000000");
            } else if (scale == 2) {
                df = new DecimalFormat("0.00");
            } else if (scale == 4) {
                df = new DecimalFormat("0.0000");
            } else if (scale == 5) {
                df = new DecimalFormat("0.00000");
            }
            String ss = df.format(number);
            return tipZero(df.format(number));
        }
    }

    //剔除无效的0  但保留2位0
    public static String tipZero(String number) {

        if (number.indexOf(".") > 0) {
            //正则表达
            number = number.replaceAll("0+?$", "");//去掉后面无用的零
            number = number.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
            String aa = number.substring(number.indexOf(".") + 1, number.length());
            if (aa.length() < 2) {
                String value = new BigDecimal(number).setScale(2).toString();
                return value;
            }
        }
        return number;
    }


    /**
     * 请求获取离线数据
     * (需要先请求requestOfflineDataInfo，返回笔画数后再请求requestOfflineDataWithRange)
     */
    public void requestOfflineDataWithRange(IPenOfflineDataSyncListener iPenOfflineData,int dotSize) {
        AFPenClientCtrl.getInstance().getSet().clear();
        AFPenClientCtrl.getInstance().getOfflineDataDots().clear();
        penOfflineDataListener = iPenOfflineData;
        PenSdkCtrl.getInstance().requestOfflineDataWithRange(0,dotSize);

        if (offlineDataListener == null) {
            offlineDataListener = new PenOfflineDataListener() {
                @Override
                public void offlineDataSyncStart(long l) {
                    L.debug("离线笔记数量", "offlineDataSyncStart>" + l);
                    Log.e("测试方法","offlineDataSyncStop");

                    if (penOfflineDataListener != null) {
                        penOfflineDataListener.onSyncBegin();
                    }
                    progress = l;
                    AFPenClientCtrl.getInstance().setSyncStatus(PEN_SYNC_STATUS.SYNCHRONIZING);
                }

                @Override
                public void offlineDataSyncStop() {
                    L.debug("离线笔记", "");
                    AFPenClientCtrl.getInstance().setSyncStatus(PEN_SYNC_STATUS.NONE);
                    if (penOfflineDataListener != null) {
                        penOfflineDataListener.onSyncEnd();
                    }
                }

                @Override
                public void offlineDataDidReceivePenData(NQDot nqDot) {
//                    Log.e("测试方法","offlineDataDidReceivePenData");

//                    AFPenClientCtrl.getInstance().setSyncStatus(PEN_SYNC_STATUS.SYNCHRONIZING);

//                    Log.e("离线笔记",">"+nqDot.x+"--"+nqDot.y);

                    NQDot dot = new NQDot();
                    dot.book_height = nqDot.book_height;
                    dot.book_width = nqDot.book_width;
                    //过滤笔返回的异常页面
                    if (nqDot.page > 186 || nqDot.page < 1) {
                        dot.page = AppCommon.getCurrentPage();
                    } else {
                        dot.page = nqDot.page;
                    }
                    dot.type = nqDot.type;
                    dot.bookNum = NoteTypeEnum.NOTE_TYPE_A5.getNoeType();
                    dot.x = nqDot.x + offsetX;
                    dot.y = nqDot.y + offsetY;
                    AFPenClientCtrl.getInstance().getOfflineDataDots().add(dot);
                    if (dot.type == com.eningqu.aipen.sdk.bean.DotType.PEN_ACTION_UP) {
                        AFPenClientCtrl.getInstance().getSet().add(dot.page);
                    }
                    final long now = System.currentTimeMillis();

                    if (penOfflineDataListener != null && progress != 0 && now - lastTime > 1500) {
                        lastTime = now;
                        double pr = div(AFPenClientCtrl.getInstance().getOfflineDataDots().size() * 10, progress, 2);
                        penOfflineDataListener.onSyncProgress((int) (pr));
                    }

                }

                @Override
                public void offlineDataDel() {
                    Log.e("离线笔记", "offlineDataDel");

                }

                @Override
                public void offlineDataLength(int l) {
                    if (l > 0) {
                        Intent intent = new Intent(Const.Broadcast.ACTION_PEN_MESSAGE);
                        intent.putExtra(Const.Broadcast.MESSAGE_TYPE, Const.PenMsgType.PEN_CUR_MEMOFFSET);
                        intent.putExtra(JsonTag.INT_DOTS_MEMORY_OFFSET, (int) l);
                        Context context = SDKManager.getInstance().getApplication();
                        if (context != null){
                            context.sendBroadcast(intent);
                        }
                    }
                }

            };
            PenSdkCtrl.getInstance().setPenOfflineDataListener(offlineDataListener);
        } else {
            PenSdkCtrl.getInstance().setPenOfflineDataListener(offlineDataListener);
        }
    }

    /**
     * 接收当前登录用户的笔记数据
     */
    void initListener() {
        penDotListener = new PenDotListener() {
            @Override
            public void onReceiveDot(NQDot nqDot) {
                /*NingQuLog.info("initListener本地SDk解析: DotType=" + nqDot.type
                        + ", X=" + nqDot.x + ", Y=" + nqDot.y + ", Page=" + nqDot.page
                        + ", book_width=" + nqDot.book_width + ", book_height=" + nqDot.book_height);*/


//                NQPenClientCtrl.getInstance().addPageSizeByPaperSize(nqDot.book_width, nqDot.book_height, (int) nqDot.bookNum,
//                        0, 300);

                int anInt = SpUtils.getInt(SDKManager.getInstance().getApplication(), Constant.SP_KEY_INIT_PEN, 0);
                if (anInt != 1) {
                    ToastUtils.showShort(R.string.server_status_tips5);
                    return;
                }
                //判断当前笔记本是否已经收藏，收藏不能书写
                if (null != AppCommon.getCurrentNoteBookData() && AppCommon.isCurrentNotebookLocked()) {
                    if (nqDot.type == DotType.PEN_ACTION_DOWN ||
                            nqDot.type == DotType.PEN_ACTION_UP) {

                        L.error(TAG, "cur notebook is locked");
                        EventBusCarrier eventBusCarrier = new EventBusCarrier();
                        eventBusCarrier.setEventType(Constant.ERROR_LOCKED);
                        EventBusUtil.post(eventBusCarrier);
                    }
                    return;
                }

                NQDot dot = new NQDot();
                dot.book_height = nqDot.book_height;
                dot.book_width = nqDot.book_width;
                //过滤笔返回的异常页面
                if (nqDot.page > 186 || nqDot.page < 1) {
                    dot.page = AppCommon.getCurrentPage();
                } else {
                    dot.page = nqDot.page;
                }
                dot.type = nqDot.type;
                dot.time_stamp = nqDot.time_stamp;
                dot.bookNum = NoteTypeEnum.NOTE_TYPE_A5.getNoeType();
                dot.x = nqDot.x + offsetX;
                dot.y = nqDot.y + offsetY;

                //抬笔时保存笔画
                int function = checkTouchFunction(dot);
                if (function == 0) {
                    if (lastDot != null) {
                        if (lastDot.page != dot.page) {
                            if (dot.type != DotType.PEN_ACTION_DOWN) {
                                NingQuLog.debug("testDot - " + lastDot.page + "-dot.page" + dot.page);

                                dot.x = lastDot.x;
                                dot.y = lastDot.y;
                                dot.page = lastDot.page;
                                dot.type = DotType.PEN_ACTION_UP;
                            }

                        }
                        if (dot.type == DotType.PEN_ACTION_DOWN) {
                            lastDot.type = DotType.PEN_ACTION_UP;
                        }
                    } else {
                        //lastDot is null
                        NingQuLog.debug("testDot - lastDot is null");

                        if (dot.type == DotType.PEN_ACTION_MOVE) {
                            dot.type = DotType.PEN_ACTION_DOWN;
                        }
                    }
                }
                //暂时缓存到笔画队列
                addDotToTempQueue(dot);
                //检查是否需要切换页码
                checkSwitchPage(dot);
                lastDot = dot;

                if (function == 0 && dot.type == com.eningqu.aipen.sdk.bean.DotType.PEN_ACTION_UP) {
                    lastDot = null;
                    //检查是否触发了功能
                    saveStroke();
                    mSaveStrokesThread.resumeThread();
                    //保存后清除暂时缓存的笔画
                    cleanTempQueue();
                    if (getPollSwitchStatus() == POLL_SWITCH_STATUS.CLOSE) {
                        setPollSwitchStatus(POLL_SWITCH_STATUS.OPEN);
                    }
                }

                // 书写时，判断界面是否处于前台
                if (AppUtils.isAppForeground()) {
                    L.error("send to draw : type = " + dot.type + ":x = " + dot.x + ":y = " + dot.y);
                    enqueueDotForBroadcast(dot);
                    sendNQDot(dot);
                    //APP处于前台
                    //是否书写页面已经打开
                    if (AppCommon.getDrawOpenState() == PAGE_OPEN_STATUS.OPEN) {
                        //                if (!touchFunction) {//不是功能区，把缓存中的点发出来，绘制，最后发送最后一个点
                        //                    while (!mStrokeDotCacheQueue.isEmpty()) {
                        //                        NQDot dotTemp = mStrokeDotCacheQueue.poll();
                        //                        sendNQDot(dotTemp);
                        //                    }
                        //                    sendNQDot(dot);
                        //                } else {
                        //                    //缓存需要重绘的笔画点
                        //                    addDotToTempCacheQueue(dot);
                        //                }

                        //                while (!mStrokeDotCacheQueue.isEmpty()) {
                        //                    //如果有缓存的笔画先发送去绘制
                        //                    NQDot dotTemp = mStrokeDotCacheQueue.poll();
                        //                    sendNQDot(dotTemp);
                        //                }
                        //                sendNQDot(dot);
                        //                if(dot.type == DotType.PEN_ACTION_UP){
                        //                    cleanTempQueue();
                        //                }
                    } else if (AppCommon.getDrawOpenState() == PAGE_OPEN_STATUS.CLOSE) {
                        //关闭笔记本时第一笔重绘
                        //                reDrawStroke = true;
                        if (AppCommon.getCurrentPage() == dot.page && dot.type == com.eningqu.aipen.sdk.bean.DotType.PEN_ACTION_UP && function != 2) {
                            EventBusCarrier eventBusCarrier = new EventBusCarrier();
                            eventBusCarrier.setEventType(Constant.OPEN_NOTEBOOK_CODE);
                            EventBusUtil.post(eventBusCarrier);
                        }
                    }
                } else {
                    if (AppCommon.getCurrentPage() == dot.page && dot.type == com.eningqu.aipen.sdk.bean.DotType.PEN_ACTION_UP) {
                        L.error("write on background");
                    }
                }

            }

            @Override
            public void onError(int i, String s) {
                isOperate = false;
                NingQuLog.error("error=" + i + ", message=" + s);
            }
        };
    }

    /***
     * @param dot 点
     */
    private void enqueueDotForBroadcast(NQDot dot) {
        Activity activity = ActivityUtils.getTopActivity();
        if (activity instanceof MainActivity) {
            int anInt = SpUtils.getInt(activity, Constant.SP_KEY_INIT_PEN, 0);
//            if (dot.type == com.eningqu.aipen.sdk.bean.DotType.PEN_ACTION_UP) {
            AFPenClientCtrl.getInstance().setOneCheckPerStroke(true);
//            }
            if (anInt == 1) {
                //授权的笔书写才自动创建第一本
                if (!AFPenClientCtrl.getInstance().isNoneCurNotebook()) {
                }
            }
        }
    }


    /**
     * 实时传点渲染
     *
     * @param dot
     */
    private void sendNQDot(NQDot dot) {
        int defaultBg;
        if (dot.page <= 128) {
            //偶数
            if (dot.page % 2 == 0) {
                defaultBg = 3;
            } else {
                defaultBg = 1;
            }
        } else {
            //康奈尔样式
            defaultBg = 2;
        }
        if (defaultBg != SpUtils.getInt(SDKManager.getInstance().getApplication(), "DrawBg")) {
            SpUtils.putInt(SDKManager.getInstance().getApplication(), "DrawBg", defaultBg);
        }
        EventBusCarrier eventBusCarrier = new EventBusCarrier();
        eventBusCarrier.setEventType(Constant.DRAW_CODE);
        eventBusCarrier.setObject(dot);
        EventBusUtil.post(eventBusCarrier);
    }

    public void setPollSwitchStatus(POLL_SWITCH_STATUS pollDotSwitch) {
        L.info(TAG, "setPollSwitchStatus pollDotSwitch=" + pollDotSwitch);
        this.pollDotSwitch = pollDotSwitch;
    }

    public POLL_SWITCH_STATUS getPollSwitchStatus() {
        return pollDotSwitch;
    }

    /**
     * 从队列中取点的开关
     */
    private POLL_SWITCH_STATUS pollDotSwitch = POLL_SWITCH_STATUS.OPEN;
    /**
     * 待保存的笔画队列
     */
    private Queue<com.eningqu.aipen.qpen.bean.StrokesBean> mStrokesCacheQueue;

    com.eningqu.aipen.qpen.bean.StrokesBean newStrokesBean;
    /**
     * 保存笔画到笔画缓存队列
     */
    private synchronized void saveStroke() {

        while (!mStrokeDotQueue.isEmpty()) {
            if(null==newStrokesBean){
                newStrokesBean = new com.eningqu.aipen.qpen.bean.StrokesBean();
            }
            NQDot dot = mStrokeDotQueue.poll();
            newStrokesBean.addDot(dot.x, dot.y);
            if (dot.type == com.eningqu.aipen.sdk.bean.DotType.PEN_ACTION_UP) {
                newStrokesBean.setColor(QPenManager.getInstance().getPaintColor());
                newStrokesBean.setSizeLevel(CommandSize.getSizeLevelBySize(QPenManager.getInstance().getPaintSize()));
                newStrokesBean.setCreateTime(System.currentTimeMillis());
                mStrokesCacheQueue.add(newStrokesBean);
                L.info(TAG, "add stroke time=" + TimeUtil.convertToTime(TimeUtil.FORMAT_TIME, newStrokesBean.getCreateTime()) +
                        "  " + newStrokesBean.getCreateTime());
                newStrokesBean = null;
            }
        }
    }

    /**
     * 添加点到暂时缓存的笔画中
     *
     * @param dot
     */
    private void addDotToTempQueue(NQDot dot) {
        //暂时缓存
        mStrokeDotQueue.offer(dot);
    }

    /**
     * 检查是否需要换页
     */
    private void checkSwitchPage(NQDot dot) {
        if (AppCommon.getCurrentPage() != dot.page) {
            AppCommon.setCurrentPage(dot.page);
            //通知换页
            EventBusCarrier eventBusCarrier = new EventBusCarrier();
            if (AppCommon.getDrawOpenState() == PAGE_OPEN_STATUS.OPEN) {
                eventBusCarrier.setEventType(Constant.SWITCH_PAGE_CODE);
            } else if (AppCommon.getDrawOpenState() == PAGE_OPEN_STATUS.CLOSE) {
                eventBusCarrier.setEventType(Constant.OPEN_NOTEBOOK_CODE);
            }
            EventBusUtil.post(eventBusCarrier);

            iinkInitSuccess = false;
            /*new Thread() {
                @Override
                public void run() {
                    super.run();
                    if (IInkSdkManager.getInstance().isInitSuccess()) {
                        setIInkSDK();
                    }
                }
            }.start();*/

        }
    }

    /**
     * 检查是否触发了功能
     */
    private int checkTouchFunction(NQDot dot) {
        //检查是否命令区域的点
        CommandBase commandBase1 = null;
        if (strokeFirstDot == null) {
            strokeFirstDot = dot;
        }
        commandBase1 = SDKUtil.calculateADot(strokeFirstDot.page, strokeFirstDot.type, strokeFirstDot.x, strokeFirstDot.y);

        if (commandBase1 != null) {//落笔在功能区
            CommandBase commandBase2 = SDKUtil.calculateADot(dot.page, dot.type, dot.x, dot.y);
            //            L.info("handle dot commandBase2 = " + commandBase2);
            if (commandBase2 == null || commandBase1.getSizeLevel() != commandBase2.getSizeLevel() ||
                    commandBase1.getCode() != commandBase2.getCode()) {//当前笔画的其他点不在功能区或不是同一个功能区
                return 0;
            } else {
                //落笔和抬笔都在同一个功能区内
                if (dot.type == com.eningqu.aipen.sdk.bean.DotType.PEN_ACTION_UP) {
                    // 清空队列，功能区内的笔画，删除掉不需要保存
                    cleanTempQueue();
                    strokeFirstDot = null;
                    if (AppCommon.isCurrentNotebookLocked()) {
                        L.error(TAG, "cur notebook is locked");
                        EventBusCarrier eventBusCarrier = new EventBusCarrier();
                        eventBusCarrier.setEventType(Constant.ERROR_LOCKED);
                        EventBusUtil.post(eventBusCarrier);
                    } else {
                        //触发了功能
                        EventBusCarrier eventBusCarrier = new EventBusCarrier();
                        eventBusCarrier.setEventType(Constant.FUNCTION_COMMAND_CODE);
                        eventBusCarrier.setObject(commandBase2);
                        EventBusUtil.post(eventBusCarrier);
                        //执行
                        QPenManager.getInstance().onCommand(commandBase2);
                    }
                    if (commandBase2.getSizeLevel() == CommandBase.COMMAND_TYPE_SOUND) {
                        return 2;
                    }
                    return 1;
                }
            }
        } else {
            if (dot.type == com.eningqu.aipen.sdk.bean.DotType.PEN_ACTION_UP) {
                strokeFirstDot = null;
            }
        }
        return 0;
    }

    /**
     * 清除暂时缓存的笔画
     */
    private void cleanTempQueue() {
        mStrokeDotQueue.clear();
    }

    /**
     * 创建暂时缓存当前笔画的队列
     */
    private Queue<NQDot> mStrokeDotQueue;
    /**
     * 循环保存笔画的线程
     */
    private SaveStrokesThread mSaveStrokesThread = null;
    private NQDot lastDot;
    private NQDot strokeFirstDot;//笔画的第一个落点

    private class SaveStrokesThread extends Thread {
        private final Object lock = new Object();
//        private boolean pause = false;

        /**
         * 调用该方法实现线程的暂停
         */
//        void pauseThread() {
//            pause = true;
//        }

        /**
         * 调用该方法实现恢复线程的运行0
         */
        void resumeThread() {
//            pause = false;
            synchronized (lock) {
                L.info(TAG, "SaveStrokesThread notify");
                lock.notify();
            }
        }

        /**
         * 这个方法只能在run 方法中实现，不然会阻塞主线程，导致页面无响应
         */
        void onPause() {
            synchronized (lock) {
                try {
                    L.info(TAG, "SaveStrokesThread is waiting");
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void run() {

            while (true) {
                while (!mStrokesCacheQueue.isEmpty()) {
                    com.eningqu.aipen.qpen.bean.StrokesBean strokesBean = mStrokesCacheQueue.poll();

                    String lang = SPUtils.getInstance(Constant.LANGUAGE).getString(Constant.LANGUAGE);
//                    if (TYPE_EN.equals(lang)) {
//                        StrokesUtilForQpen.saveNewStrokes(strokesBean, AppCommon.getStrokesPath(AppCommon.getCurrentNotebookId(),
//                                AppCommon.getCurrentPage()), "Page sign" + AppCommon.getCurrentPage());
//                    } else {
                        StrokesUtilForQpen.saveNewStrokes(strokesBean, AppCommon.getStrokesPath(AppCommon.getCurrentNotebookId(),
                                AppCommon.getCurrentPage()), "页签" + AppCommon.getCurrentPage());
//                    }

                    AppCommon.updatePage(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), AppCommon.getCurrentNoteType(), "");
                    //                    try {
                    //                        Thread.sleep(1000);
                    //                    } catch (InterruptedException e) {
                    //                        e.printStackTrace();
                    //                    }
                    L.error(TAG, "save new stroke time=" + TimeUtil.convertToTime(TimeUtil.FORMAT_TIME, strokesBean.getCreateTime()) +
                            "  " + strokesBean.getCreateTime());
                }

                onPause();
                //                while (pause) {
                //                    onPause();
                //                }
            }
        }
    }

    private BluetoothDevice mBoundBtData;//绑定的设备
    private int tryCount = 0;

    public void releaseReConnect() {
        tryCount = 0;
        mHandler.removeCallbacks(tryConnectRunnable);
        PenSdkCtrl.getInstance().stopScan();
        mBoundBtData = null;
    }

    public synchronized void reConnectBle() {
        mBoundBtData = AppCommon.loadBleInfo2();
        if (mBoundBtData != null) {
            mHandler.post(tryConnectRunnable);
        }
    }

    private Runnable tryConnectRunnable = new Runnable() {
        @Override
        public void run() {
//            if (mBoundBtData != null && !TextUtils.isEmpty(mBoundBtData.bleName) && (!mBoundBtData.bleName.startsWith(AppCommon.PEN_QPEN)) &&
//                    AFPenClientCtrl.getInstance().getConnStatus() == PEN_CONN_STATUS.DISCONNECTED) {
            if (BluetoothClient.getBle().getBlueToothStatus()) {
                //开启定位
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!LocationUtils.isLocationEnabled(SDKManager.getInstance().getApplication())) {
                        return;
                    }
                }
                Log.e("重连", tryCount + "--");
                NQBtDevice btDevice = new NQBtDevice();
                btDevice.name = mBoundBtData.bleName;
                btDevice.mac = mBoundBtData.bleMac;
                PenSdkCtrl.getInstance().setScanListener(new ScanListener() {
                    @Override
                    public void onScanStart() {

                    }

                    @Override
                    public void onScanStop() {
                        if (mBoundBtData != null && AFPenClientCtrl.getInstance().getConnStatus() == PEN_CONN_STATUS.DISCONNECTED) {
                            PenSdkCtrl.getInstance().startScanDevice();
                        }
                    }

                    @Override
                    public void onReceiveException(int i, String s) {

                    }

                    @Override
                    public void onScanResult(NQDeviceBase nqDeviceBase) {
                        NQBtDevice scanDevice = (NQBtDevice) nqDeviceBase;
                        if (mBoundBtData != null && !TextUtils.isEmpty(mBoundBtData.bleName) &&
                                AFPenClientCtrl.getInstance().getConnStatus() == PEN_CONN_STATUS.DISCONNECTED) {
                            if (btDevice.mac.equals(scanDevice.mac)) {
                                PenSdkCtrl.getInstance().connect(nqDeviceBase);
                                PenSdkCtrl.getInstance().stopScan();
                            }
                        }
                    }
                });
                PenSdkCtrl.getInstance().startScanDevice();

//                }

            }
        }
    };

    @Override
    public void onDestroy() {
        penDotListener = null;
        mSaveStrokesThread = null;
        mHandler.removeCallbacks(runnable);
        PenSdkCtrl.getInstance().setDotListener(null);
        super.onDestroy();
    }
}
