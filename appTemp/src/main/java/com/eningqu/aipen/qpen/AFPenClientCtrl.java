package com.eningqu.aipen.qpen;

import android.content.Context;
import android.text.TextUtils;

import com.eningqu.aipen.R;
import com.eningqu.aipen.SmartPenApp;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.EventBusCarrier;
import com.eningqu.aipen.common.HwrEngineEnum;
import com.eningqu.aipen.common.enums.NoteTypeEnum;
import com.eningqu.aipen.common.thread.SingleThreadExecutorUtils;
import com.eningqu.aipen.common.utils.EventBusUtil;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.common.utils.TimeUtil;
import com.eningqu.aipen.db.model.NoteBookData;
import com.eningqu.aipen.manager.SpinNotebookManager;
import com.eningqu.aipen.qpen.bean.CommandBase;
import com.eningqu.aipen.qpen.bean.CommandSize;
import com.eningqu.aipen.qpen.bean.StrokesBean;
import com.eningqu.aipen.qpen.listener.IQPenCreateNotebookListener;
import com.eningqu.aipen.sdk.bean.DotType;
import com.eningqu.aipen.sdk.bean.NQDot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.eningqu.aipen.common.AppCommon.getUserUID;

//import com.myscript.iink.PointerType;

public class AFPenClientCtrl{
    private final static String TAG = AFPenClientCtrl.class.getSimpleName();

    private static AFPenClientCtrl myInstance;
    private Context context;
    private BtScanCallback btScanCallback;
    private PEN_CONN_STATUS connStatus = PEN_CONN_STATUS.DISCONNECTED;
    private PEN_SYNC_STATUS syncStatus;
    private PEN_RECO_STATUS recoStatus;
    public String lastTryConnectAddr;
    public String lastTryConnectName;
    private int lastDotsCount;
    private NQDot lastDot;
    private NQDot strokeFirstDot;//笔画的第一个落点
    private long lastDotTime;
    private int lastDotPage;
    private int lastDotX, lastDotY;
    private boolean createNotebook = false;
    private boolean oneCheckPerStroke = true;//每一笔画检查一次
    private final int MSG_WHAT_BT_CONN_RETRY = 2;//重连
    private final int MSG_WHAT_BT_CONN_TIME_OUT = 3;//超时
    private static final int TIME_OUT_VALUE = 30 * 1000;//超时值

    private int connRetryTimes = 0;//重连次数
    private boolean connRetry = false;//是否重连
    private boolean clickMenu = false;
    private String recognFile;
    private boolean iinkInitSuccess;
    private Queue<NQDot> mIInkQueue = new ConcurrentLinkedQueue<>();

    public boolean isbSyncBackground() {
        return bSyncBackground;
    }

    public void setbSyncBackground(boolean bSyncBackground) {
        this.bSyncBackground = bSyncBackground;
    }

    private boolean bSyncBackground = false;//是否后台同步完成

    private boolean disconnManual = false;//是否手动断开
    /**
     * 从队列中取点的开关
     */
    private POLL_SWITCH_STATUS pollDotSwitch = POLL_SWITCH_STATUS.OPEN;
    private boolean isDrawNow = false;

    private IPenOfflineDataSyncListener penOfflineDataListener;
    /**
     * 缓存所有接收到的点到队列
     */
    private Queue<NQDot> mDotQueueForBroadcast = null;
    /**
     * 创建暂时缓存当前笔画的队列
     */
    private Queue<NQDot> mStrokeDotQueue;
//    private Queue<NQDot> mStrokeDotCacheQueue;
    /**
     * 待保存的笔画队列
     */
    private Queue<StrokesBean> mStrokesCacheQueue;
    /**
     * 循环取点的线程
     */
    private DotConsumerForBroadcastThread mBroadcastThread = null;
    /**
     * 循环保存笔画的线程
     */
    private SaveStrokesThread mSaveStrokesThread = null;
    /**
     * 离线数据缓存
     */
    private List<NQDot> mOfflineDataDots = new ArrayList<>();

    //    public List<NQDot> getCacheDots() {
    //        return mPagesCacheDots.get(String.valueOf(AppCommon.getCurrentPage()));
    //    }


    public void setOneCheckPerStroke(boolean oneCheckPerStroke) {
        this.oneCheckPerStroke = oneCheckPerStroke;
    }

    /**
     * 不在书写页面时的点
     */
    //    private List<NQDot> mCacheDots = Collections.synchronizedList(new ArrayList<NQDot>());
    //    private Map<String, List<NQDot>> mNotebooksCacheDots = new HashMap<>();
    //    private Map<String, List<NQDot>> mPagesCacheDots = new HashMap<>();
    private AFPenClientCtrl() {
    }

    public static synchronized AFPenClientCtrl getInstance() {
        if (myInstance == null) {
            synchronized (AFPenClientCtrl.class) {
                if (null == myInstance) {
                    myInstance = new AFPenClientCtrl();
                }
            }
        }
        return myInstance;
    }

    /**
     * 初始化
     *
     * @param context 上下文
     */
    public void init(Context context) {
        this.context = context.getApplicationContext();


        setConnStatus(PEN_CONN_STATUS.DISCONNECTED);
        //创建接收所有点的队列
        mDotQueueForBroadcast = new ConcurrentLinkedQueue<>();
        //创建暂时缓存当前笔画的队列
        mStrokeDotQueue = new ConcurrentLinkedQueue<>();
        //创建不打开笔记本时暂时缓存当前笔画的队列
//        mStrokeDotCacheQueue = new ConcurrentLinkedQueue<>();
        //待保存的笔画队列
        mStrokesCacheQueue = new ConcurrentLinkedQueue<>();
        //创建循环取点的线程
        mBroadcastThread = new DotConsumerForBroadcastThread();
        //        mBroadcastThread.setDaemon(true);
        //        mBroadcastThread.start();

        //创建循环保存笔画的线程
        mSaveStrokesThread = new SaveStrokesThread();
        mSaveStrokesThread.setDaemon(true);
        mSaveStrokesThread.start();

        recoStatus = PEN_RECO_STATUS.NONE;
    }

    public boolean isDisconnManual() {
        return disconnManual;
    }

    public void setDisconnManual(boolean disconnManual) {
        this.disconnManual = disconnManual;
    }

    public String getLastTryConnectAddr() {
        return lastTryConnectAddr;
    }

    public String getLastTryConnectName() {
        if (TextUtils.isEmpty(lastTryConnectName)){
            return "";
        }
        return lastTryConnectName;
    }

    public void cleanBluetoothInfo() {
        lastTryConnectAddr = "";
        lastTryConnectName = "";
        AppCommon.deleteBluetoothData();
    }

    public PEN_CONN_STATUS getConnStatus() {
        return connStatus;
    }

    public void setConnStatus(PEN_CONN_STATUS connStatus) {
        L.info(TAG, "set conn status=" + connStatus);
        this.connStatus = connStatus;
    }

    public PEN_SYNC_STATUS getSyncStatus() {
        return this.syncStatus;
    }

    public void setSyncStatus(PEN_SYNC_STATUS syncStatus) {
        L.info(TAG, "set sync status=" + syncStatus);
        this.syncStatus = syncStatus;
    }

    public PEN_RECO_STATUS getRecoStatus() {
        return recoStatus;
    }

    public void setRecoStatus(PEN_RECO_STATUS recoStatus) {
        L.info(TAG, "set reco status=" + recoStatus);
        this.recoStatus = recoStatus;
    }

    private HashSet<Integer> set = new HashSet();

    public HashSet<Integer> getSet() {
        return set;
    }

    public List<NQDot> getOfflineDataDots() {
        return mOfflineDataDots;
    }


//    public NQDot praseToNqdot(NQDot nqDot){
//        NQDot dot = new NQDot();
//        dot.book_height = nqDot.book_height;
//        dot.book_width = nqDot.book_width;
//        dot.page = nqDot.page;
//        dot.type = nqDot.type;
//        dot.time_stamp =  nqDot.mOffset;
//        dot.bookNum = NoteTypeEnum.NOTE_TYPE_A5.getNoeType();
//        dot.x = nqDot.x + 0;
//        dot.x = nqDot.x - 32;
//        return dot;
//    }

    public void drawDotQueue() {
        SingleThreadExecutorUtils.getThreadPool().submit(mBroadcastThread);
    }

    public POLL_SWITCH_STATUS getPollSwitchStatus() {
        return pollDotSwitch;
    }

//    public boolean isDrawNow() {
//        return isDrawNow;
//    }

    public void setDrawNow(boolean drawNow) {
        isDrawNow = drawNow;
    }

    public void setPollSwitchStatus(POLL_SWITCH_STATUS pollDotSwitch) {
        L.info(TAG, "setPollSwitchStatus pollDotSwitch=" + pollDotSwitch);
        this.pollDotSwitch = pollDotSwitch;
    }

    public void cleanQueenDatas() {
        mDotQueueForBroadcast.clear();
    }

    private final class DotConsumerForBroadcastThread extends Thread {
        @Override
        public void run() {

            setName(this.getClass().getSimpleName());

            //            while (true) {
            while (!mDotQueueForBroadcast.isEmpty()) {
                if (getPollSwitchStatus() == POLL_SWITCH_STATUS.OPEN &&
                        getSyncStatus() == PEN_SYNC_STATUS.NONE &&
                        getRecoStatus() == PEN_RECO_STATUS.NONE) {// 开关打开且不在同步离线数据的状态下

                    //当前笔记本是否为空
//                    if (isNoneCurNotebook()) {
//                        continue;
//                    }
                    //取队列缓存的点
                    NQDot dot = mDotQueueForBroadcast.poll();
                    if (dot == null) {
                        return;
                    }
                    //已收藏的笔记本不能编辑
                    if (isCurNotebookLocked()) {
                        //每一笔画抬笔重置，以便控制每一笔画判断一次
                        if (dot.type == DotType.PEN_ACTION_UP) {
                            oneCheckPerStroke = true;
                        }
                        continue;
                    }

                    //如果无效的点则丢弃
                    if (!isValidDot(dot)) {
                        continue;
                    }
//                    L.error("check dot ok page = " + dot.page + ",x =" + dot.x + ", y =" + dot.y + ", type =" + dot.type);
                    //检查每一个点
                    checkEveryDot(dot);
                }
            }

            //                //检查队列的线程休眠
            //                try {
            //                    synchronized (mDotQueueForBroadcast) {
            //                        //                        L.info(TAG, "DotConsumerForBroadcastThread is waiting");
            //                        mDotQueueForBroadcast.wait();
            //                    }
            //                } catch (InterruptedException e) {
            //                    e.printStackTrace();
            //                }
            //            }
        }
    }

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
                    StrokesBean strokesBean = mStrokesCacheQueue.poll();
                    StrokesUtilForQpen.saveNewStrokes(strokesBean, AppCommon.getStrokesPath(AppCommon.getCurrentNotebookId(),
                            AppCommon.getCurrentPage()), context.getString(R.string.label_text) + AppCommon.getCurrentPage());
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

    /**
     * 重置缓存的上一个点
     *
     * @param dot 点
     */
    private void resetLastDot(NQDot dot) {
        lastDot = dot;
    }

    private boolean overLimit(NQDot lastDot, NQDot dot) {
        if (lastDot == null) {
            return false;
        }
        int width = lastDot.x - dot.x;
        int height = lastDot.y - dot.y;
        return Math.abs(width) > 500 || Math.abs(height) > 500;
    }


    /**
     * 判断点的有效性
     *
     * @param dot 点
     */
    private boolean isValidDot(NQDot dot) {

        if (null == dot) {
            L.error(TAG, "dot=null");
            lastDot = null;
            return false;
        }

        //点类型异常，丢弃
        if (dot.type < DotType.PEN_ACTION_DOWN || dot.type > DotType.PEN_ACTION_UP) {
            //只接收type为1和2的点
            L.error(TAG, "return dot.type=" + dot.type);
            //如果抬笔了，把上一个缓存点置空
            resetLastDot(null);
            return false;
        }

        //页码异常,丢弃//注释掉页码限制20210315
        if (dot.page < 1 /*|| dot.page > 300*/ || dot.x > dot.book_width || dot.y > dot.book_height) {
            L.error(TAG, "dot.page is exception");
            if (dot.type == DotType.PEN_ACTION_UP && lastDot != null) {
                L.info(TAG, "correct the dot");
                dot.page = lastDot.page;
                dot.book_width = lastDot.book_width;
                dot.book_height = lastDot.book_height;
                dot.bookNum = lastDot.bookNum;
                return true;
            }
            return false;
        }

        //坐标值异常,丢弃
        //        if (dot.x > dot.book_width || dot.y > dot.book_height) {
        //            L.error(TAG, "dot X or Y is exception");
        //            if (dot.type == DotType.PEN_ACTION_UP && lastDot != null) {
        //                L.info(TAG, "correct the dot");
        //                dot.page = lastDot.page;
        //                dot.book_width = lastDot.book_width;
        //                dot.book_height = lastDot.book_height;
        //                dot.bookNum = lastDot.book_no;
        //                return true;
        //            }
        //            return false;
        //        }

        //如果上一个缓存的点不为空
        if (null != lastDot) {
            //跳页时
            if (dot.page != lastDot.page) {
//                if (lastDot.type == DotType.PEN_ACTION_DOWN) {
//                    //跳页时，如果上一点不是结束点，丢弃
//                    L.error(TAG, "return dot.page=" + dot.page + ",dot.type" + dot.type +
//                            ", lastDot.page=" + lastDot.page + ",lastDot.type=" + lastDot.type);
//                    return false;
//                } else {
                //如果抬笔了，把上一个缓存点置空
                resetLastDot(null);
//                }
            }
            boolean overLimit = overLimit(lastDot, dot);
            if (overLimit) {
                dot.x = lastDot.x;
                dot.y = lastDot.y;
                dot.type = DotType.PEN_ACTION_UP;//处理异常情况，增加断行，处理由于sdk偶尔丢失了type=2的笔迹导致飞笔的问题
            }
        }

        //如果10毫秒内换页的点，改为上一页的点
        if (System.currentTimeMillis() - lastDotTime < 10 && Math.abs(dot.x - lastDotX) < 100 &&
                Math.abs(dot.y - lastDotY) < 100) {
            dot.page = lastDotPage;
        }

        lastDot = dot;
        lastDotPage = dot.page;
        lastDotX = dot.x;
        lastDotY = dot.y;
        lastDotTime = System.currentTimeMillis();
        //如果抬笔了，把上一个缓存点置空
        if (dot.type == DotType.PEN_ACTION_UP) {
            L.error("last dot set null");
            lastDot = null;
        }
        return true;
    }

    /**
     * 判断当前笔记本是否已收藏锁住
     *
     * @return true上锁
     */
    private boolean isCurNotebookLocked() {
        //当前笔记本已上锁
        if (AppCommon.isCurrentNotebookLocked()) {
            L.error(TAG, "cur notebook is locked");
            if (oneCheckPerStroke) {
                oneCheckPerStroke = false;
                EventBusCarrier eventBusCarrier = new EventBusCarrier();
                eventBusCarrier.setEventType(Constant.ERROR_LOCKED);
                EventBusUtil.post(eventBusCarrier);
            }
            return true;
        }
        return false;
    }

    int size;
    /**
     * 当前笔记本是否为空
     *
     * @return true 空
     */
    public synchronized boolean isNoneCurNotebook() {
        //当前笔记本为空
        if (TextUtils.isEmpty(AppCommon.getCurrentNotebookId())) {
            L.error(TAG, "cur notebook is none");
            //缓存数据
            //如果未锁住的笔记本为空,则创建新笔记本为默认的笔记本
            List<NoteBookData> noteBookDatas = AppCommon.loadNoteBookData(0);
            if (null == noteBookDatas || noteBookDatas.size() == 0) {
                if (oneCheckPerStroke && !createNotebook) {
                    oneCheckPerStroke = false;
                    createNotebook = true;

                    EventBusCarrier eventBusCarrier = new EventBusCarrier();
                    eventBusCarrier.setEventType(Constant.ERROR_NONE_NOTEBOOK);//ERROR_NONE_SELECT_NOTEBOOK
                    EventBusUtil.post(eventBusCarrier);

                    size = com.nq.edusaas.hps.utils.SpUtils.getInt(context, AppCommon.getUserUID()+"bookSize", -1);
                    if (size == -1){
                        size = 0;
                    }
                    String bookName = context.getString(R.string.new_notebook_name) + (com.nq.edusaas.hps.utils.SpUtils.getInt(context, AppCommon.getUserUID()+"bookSize", 0) + 1);

                    AppCommon.createNoteBook(NoteTypeEnum.NOTE_TYPE_A5.getNoeType(), String.valueOf(size), getUserUID(), bookName, new IQPenCreateNotebookListener() {
                        @Override
                        public void onSuccessful(NoteBookData noteBookData) {
                            com.nq.edusaas.hps.utils.SpUtils.putInt(context, AppCommon.getUserUID() + "bookSize", com.nq.edusaas.hps.utils.SpUtils.getInt(context, AppCommon.getUserUID()+"bookSize", 0) + 1);

                            int coverIndex = size % Constant.BOOK_COVERS.length;//取余可循环使用封皮
                            noteBookData.noteCover = String.valueOf(coverIndex);
                            AppCommon.setCurrentNoteBookData(noteBookData);
                            AppCommon.setCurrentNotebookId(noteBookData.notebookId);
                            SpinNotebookManager.getInstance().addNotebook(noteBookData);
                            //创建完成
                            //                            setPollSwitchStatus(true);
                            AppCommon.setNotebooksChange(true);
                            createNotebook = false;
                        }

                        @Override
                        public void onFail() {
                            //创建完成
                            createNotebook = false;
                        }
                    });
                }
            } else {
                if (oneCheckPerStroke) {
                    oneCheckPerStroke = false;
                    EventBusCarrier eventBusCarrier = new EventBusCarrier();
                    eventBusCarrier.setEventType(Constant.ERROR_NONE_SELECT_NOTEBOOK);
                    EventBusUtil.post(eventBusCarrier);
                }
            }

            return true;
        }
        return false;
    }

    private final long NO_TIMESTAMP = -1;
    private final float NO_PRESSURE = 0.0f;
    int pointerId = (int) (System.currentTimeMillis() / 1000);
    NQDot iinklastDot = null;

    /**
     * 1、判断收到的点是否需要切换页码
     * 2、判断收到的点是否触发了功能
     *
     * @param dot
     */
    private void checkEveryDot(NQDot dot) {
        //暂时缓存到笔画队列
        addDotToTempQueue(dot);
        //检查是否需要切换页码
        checkSwitchPage(dot);
        if (QPenManager.getInstance().getHwrEngineEnum() == HwrEngineEnum.MY_SCRIPT) {
            if (iinkInitSuccess) {
                //识别
                while (!mIInkQueue.isEmpty()) {
                    NQDot poll = mIInkQueue.poll();
                    toRecorgnize(poll);
                }
                toRecorgnize(dot);
            } else {
                mIInkQueue.add(dot);
            }
        }

        //        touchFunction = checkTouchFunction(dot);
        //抬笔时保存笔画
        int function = checkTouchFunction(dot);
        if (function == 0 && dot.type == DotType.PEN_ACTION_UP) {
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
        if (SmartPenApp.getApp().isForeground()) {
            L.error("send to draw : type = " + dot.type + ":x = " + dot.x+ ":y = " + dot.y);
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
                if (AppCommon.getCurrentPage() == dot.page && dot.type == DotType.PEN_ACTION_UP && function != 2) {
                    EventBusCarrier eventBusCarrier = new EventBusCarrier();
                    eventBusCarrier.setEventType(Constant.OPEN_NOTEBOOK_CODE);
                    EventBusUtil.post(eventBusCarrier);
                }
            }
        } else {
            if (AppCommon.getCurrentPage() == dot.page && dot.type == DotType.PEN_ACTION_UP) {
                L.error("write on background");
            }
        }

    }

    private void toRecorgnize(NQDot dot) {
        try {
            /*if (iinklastDot == null) {
                iinklastDot = dot;
                IInkSdkManager.getInstance().pointerDown(dot.x, dot.y, NO_TIMESTAMP, NO_PRESSURE, PointerType.PEN, pointerId);
            } else {
                if (dot.type == 2) {
                    IInkSdkManager.getInstance().pointerUp(dot.x, dot.y, NO_TIMESTAMP, NO_PRESSURE, PointerType.PEN, pointerId);
                    pointerId++;
                    iinklastDot = null;
                } else {
                    IInkSdkManager.getInstance().pointerMove(dot.x, dot.y, NO_TIMESTAMP, NO_PRESSURE, PointerType.PEN, pointerId);
                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                if (dot.type == DotType.PEN_ACTION_UP) {
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
            if (dot.type == DotType.PEN_ACTION_UP) {
                strokeFirstDot = null;
            }
        }
        return 0;
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

//    private void addDotToTempCacheQueue(NQDot dot) {
//        //暂时缓存
//        mStrokeDotCacheQueue.offer(dot);
//    }

    /**
     * 清除暂时缓存的笔画
     */
    private void cleanTempQueue() {
        mStrokeDotQueue.clear();
    }

    /**
     * 保存笔画到笔画缓存队列
     */
    private synchronized void saveStroke() {
        StrokesBean strokesBean = new StrokesBean();
        while (true) {
            NQDot dot = mStrokeDotQueue.poll();
            strokesBean.addDot(dot.x, dot.y);
            if (dot.type == DotType.PEN_ACTION_UP) {
                strokesBean.setColor(QPenManager.getInstance().getPaintColor());
                strokesBean.setSizeLevel(CommandSize.getSizeLevelBySize(QPenManager.getInstance().getPaintSize()));
                strokesBean.setCreateTime(System.currentTimeMillis());
                mStrokesCacheQueue.add(strokesBean);
                L.info(TAG, "add stroke time=" + TimeUtil.convertToTime(TimeUtil.FORMAT_TIME, strokesBean.getCreateTime()) +
                        "  " + strokesBean.getCreateTime());
                break;
            }
        }
    }

    /**
     * 实时传点渲染
     *
     * @param dot
     */
    private void sendNQDot(NQDot dot) {
        EventBusCarrier eventBusCarrier = new EventBusCarrier();
        eventBusCarrier.setEventType(Constant.DRAW_CODE);
        eventBusCarrier.setObject(dot);
        EventBusUtil.post(eventBusCarrier);
    }

    public boolean isClickMenu() {
        return clickMenu;
    }

    public void setClickMenu(boolean clickMenu) {
        this.clickMenu = clickMenu;
    }

    /**
     * 去初始化手写识别SDK
     */
    private void toInitHwrSdk() {
        if (QPenManager.getInstance().getHwrEngineEnum() == HwrEngineEnum.MY_SCRIPT) {
            //如果是MyScript手写识别则去初始化
            /*if (!IInkSdkManager.getInstance().isInitSuccess()) {
                try {
                    IInkSdkManager.getInstance().init(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/
        }
    }

    private void setIInkSDK() {
        if (QPenManager.getInstance().getHwrEngineEnum() == HwrEngineEnum.MY_SCRIPT) {
            try {
//            IInkSdkManager.getInstance().unInit();
                /*synchronized (AFPenClientCtrl.class) {
                    String recoLang = SpUtils.getString(context, Constant.SP_KEY_RECO_LANGUAGE, Constant.DEF_SHORT_NAME);
                    IInkSdkManager.getInstance().editorClean();
                    IInkSdkManager.getInstance().setPackageName(AppCommon.getCurrentNotebookId() + "_" + AppCommon.getCurrentPage() + ".iink");
                    IInkSdkManager.getInstance().setLanguage(context, recoLang);
                    iinkInitSuccess = true;
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
