package com.eningqu.aipen.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.bean.caiyun.AASLoginRsp;
import com.eningqu.aipen.common.bluetooth.BluetoothClient;
import com.eningqu.aipen.common.enums.NoteTypeEnum;
import com.eningqu.aipen.common.thread.ThreadPoolUtils;
import com.eningqu.aipen.common.utils.BitmapUtil;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.common.utils.SDCardHelper;
import com.eningqu.aipen.db.model.AASUserInfoData;
import com.eningqu.aipen.db.model.BluetoothData;
import com.eningqu.aipen.db.model.BluetoothDevice;
import com.eningqu.aipen.db.model.BluetoothDevice_Table;
import com.eningqu.aipen.db.model.NoteBookData;
import com.eningqu.aipen.db.model.NoteBookData_Table;
import com.eningqu.aipen.db.model.OseUserInfoData;
import com.eningqu.aipen.db.model.PageData;
import com.eningqu.aipen.db.model.PageData_Table;
import com.eningqu.aipen.db.model.PageLabelData;
import com.eningqu.aipen.db.model.PageLabelData_Table;
import com.eningqu.aipen.db.model.UserInfoData;
import com.eningqu.aipen.qpen.AFPenClientCtrl;
import com.eningqu.aipen.qpen.PAGE_OPEN_STATUS;
import com.eningqu.aipen.qpen.POLL_SWITCH_STATUS;
import com.eningqu.aipen.qpen.StrokesUtilForQpen;
import com.eningqu.aipen.qpen.bean.PageStrokesCacheBean;
import com.eningqu.aipen.qpen.listener.IQPenCollectNotebookListener;
import com.eningqu.aipen.qpen.listener.IQPenCreateNotebookListener;
import com.eningqu.aipen.qpen.listener.IQPenDeleteNotebookListener;
import com.eningqu.aipen.qpen.listener.IQPenLoadCurPageListener;
import com.eningqu.aipen.qpen.listener.IQPenRenameNotebookListener;
import com.eningqu.aipen.qpen.listener.IQPenSaveCurPageListener;
import com.nq.edusaas.hps.PenSdkCtrl;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/4/4 16:13
 */

public class AppCommon {
    public static final String TAG = AppCommon.class.getCanonicalName();

    private AppCommon() {
    }

    /**
     * 当前文件存放路径
     */
    public static String APP_NAME = "aipen";

    public static String PEN_P20_TEMP = "GM_HW";
    public static String PEN_P20 = "P20";
    public static String PEN_QPE = "60WS-";
    public static String PEN_QPEN = "-----------------------------------------------------";


    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
    public static final DateFormat DATE_FORMAT_MMSS = new SimpleDateFormat("mm:ss", Locale.getDefault());
    public static final String NQ_SAVE_ROOT_PATH = SDCardHelper.getRootPath();
    public static final String NQ_SAVE_SDCARD_PATH = SDCardHelper.getSDCardPath();
    public static final String FILE_SEPARATOR = File.separator;
    public static final String NQ_SAVE_ROOT_PATH_LOG_DIR = SDCardHelper.getRootPath() + "/log/";


    public static final String SUFFIX_NAME_JPG = ".jpg";
    public static final String SUFFIX_NAME_PCM = ".pcm";
    public static final String SUFFIX_NAME_TXT = ".txt";
    public static final String SUFFIX_NAME_PDF = ".pdf";
    public static final String SUFFIX_NAME_WORD = ".doc";
    public static final String SUFFIX_NAME_HWR = ".hwr";
    //    public static final String BASE_URL = "https://api.eningqu.com/";
    public static final String BASE_URL = "http://api.eningqu.com/";
    public static final String MYSCRIPT_DOWNLOAD_URL = "http://admin.eningqu.com/qpen/myscript/resource";
    public static final String UNBIND_URL = "http://api.eningqu.com/api/ble/unbind";

    private static Context mContext;

    private static int mDrawBroadWidth;
    private static int mDrawBroadHeight;
    private static int mNotebookModel;


    /**
     * 是否是历史数据
     */
    private static boolean isHistoryData = false;
    /**
     * 是否已经登录
     */
    public static boolean isLogout = false;
    /**
     * 当前笔记本
     */
    private static NoteBookData mCurrentNoteBookData;
    /**
     * 当前书写的ID
     */
    private static String mCurrentNotebookId;
    /**
     * 当前笔记本锁定状态
     */
    private static boolean mCurrentNotebookLocked;
    /**
     * 当前书写的页数
     */
    private static int mCurrentPage = -1;
    /**
     * 当前书写的笔记本类型  默认A5
     */
    private static int mCurrentNoteType = NoteTypeEnum.NOTE_TYPE_A5.getNoeType();
    /**
     * 当前笔记本的页面背景
     */
    private static int mCurrentNoteBookBG = 1;
    /**
     * 当前页笔画缓存
     */
    private static PageStrokesCacheBean mCurPageStrokesCache;

    /**
     * 笔记本数量是否有更新
     */
    private static boolean isNotebooksChange = false;

    /**
     * 绘图界面打开状态0关闭 1正在打开 2已打开
     */
    private static PAGE_OPEN_STATUS drawOpenState = PAGE_OPEN_STATUS.CLOSE;

    public static void init(Context context) {
        mContext = context.getApplicationContext();
    }

    public static int getCurrentNoteBookBG() {
        return mCurrentNoteBookBG;
    }

    public static void setCurrentNoteBookBG(int noteBookBG) {
        mCurrentNoteBookBG = noteBookBG;
    }

    public static PageStrokesCacheBean getCurPageStrokesCache() {
        if (null == mCurPageStrokesCache) {
            mCurPageStrokesCache = new PageStrokesCacheBean(AppCommon.getUserUID(), AppCommon.getCurrentNotebookId(),
                    AppCommon.getCurrentPage(), 1, "1");
        }
        return mCurPageStrokesCache;
    }

    public static PageStrokesCacheBean getCurPageStrokes() {
        return new PageStrokesCacheBean(AppCommon.getUserUID(), AppCommon.getCurrentNotebookId(),
                AppCommon.getCurrentPage(), 1, "1");
    }

    public static void setCurPageStrokesCache(PageStrokesCacheBean curPageStrokesCache) {
        mCurPageStrokesCache = curPageStrokesCache;
    }

    public static void setDrawBroadWidthHeight(int w, int h) {
        mDrawBroadWidth = w;
        mDrawBroadHeight = h;
    }

    /*** 是否是历史数据 主要用于接收蓝牙接收历史数据还是实时数据*/
    public static boolean isHistoryData() {
        return isHistoryData;
    }

    public static void setHistoryData(boolean isHistory) {
        isHistoryData = isHistory;
    }

    public static boolean isClickPageDraw = false;
    public static boolean isSharePageDraw = false;

    public static NoteBookData getCurrentNoteBookData() {
        return mCurrentNoteBookData;
    }

    public static void setCurrentNoteBookData(NoteBookData currentNoteBookData) {
        mCurrentNoteBookData = currentNoteBookData;

        if (null != mCurrentNoteBookData) {
            setCurrentNotebookLocked(mCurrentNoteBookData.isLock);
        } else {
            setCurrentNotebookLocked(false);
        }
    }

    /*** 获取当前笔记本ID*/
    public static String getCurrentNotebookId() {
        return mCurrentNotebookId;
    }

    /*** 缓存当前笔记本ID*/
    public static void setCurrentNotebookId(String currentNotebookId) {
        mCurrentNotebookId = currentNotebookId;
    }

    public static boolean isCurrentNotebookLocked() {
        return mCurrentNotebookLocked;
    }

    public static void setCurrentNotebookLocked(boolean locked) {
        mCurrentNotebookLocked = locked;
    }

    /*** 获取当前页数*/
    public static int getCurrentPage() {
        return mCurrentPage;
    }

    /*** 设置当前页数*/
    public static void setCurrentPage(int page) {
        L.info(TAG, "set current page=" + page);
        mCurrentPage = page;
    }

    public static boolean isNotebooksChange() {
        return isNotebooksChange;
    }

    public static void setNotebooksChange(boolean change) {
        AppCommon.isNotebooksChange = change;
    }

    /**
     * 重置当前的值
     */
    public static void resetCurrentData() {
        L.info(TAG, "reset current data");
        mCurrentPage = -1;
        mCurrentNotebookId = "";
        mCurPageStrokesCache = null;
        mCurrentNoteBookData = null;
    }

    /*** 获取当前笔记本类型*/
    public static int getCurrentNoteType() {
        return mCurrentNoteType;
    }

    /*** 设置当前笔记本类型*/
    public static void setCurrentNoteType(int noteType) {
        L.info(TAG, "set current noteType=" + noteType);
        mCurrentNoteType = noteType;
    }

    /*** 是否打开绘图界面*/
    public static PAGE_OPEN_STATUS getDrawOpenState() {
        return drawOpenState;
    }

    public static void setDrawOpenState(PAGE_OPEN_STATUS state) {
        L.info(TAG, "setDrawOpenState drawOpenState=" + state);
        drawOpenState = state;
    }

    private static IQPenLoadCurPageListener loadCurPageListener;

    /***
     * 切换页码数据
     */
    public static void switchPageData(final int noteType, final int page) {
        L.info(TAG, "switchPageData mCurrentNotebookId=" + mCurrentNotebookId + ", page=" + page + ", noteType=" + noteType);
        isClickPageDraw = true;

        boolean isTurnOff = false;
        if (AFPenClientCtrl.getInstance().getPollSwitchStatus() == POLL_SWITCH_STATUS.OPEN) {
            isTurnOff = true;
            AFPenClientCtrl.getInstance().setPollSwitchStatus(POLL_SWITCH_STATUS.CLOSE);
        }
        loadPageData(mCurrentNotebookId, noteType, page);
        if (isTurnOff) {
            AFPenClientCtrl.getInstance().setPollSwitchStatus(POLL_SWITCH_STATUS.OPEN);
        }

    }

    public static NoteBookData selectNotebook(String notebookId) {
        NoteBookData noteBookData = SQLite.select()
                .from(NoteBookData.class)
                .where(NoteBookData_Table.notebookId.eq(notebookId),
                        NoteBookData_Table.userUid.eq(getUserUID()))
                .querySingle();

        return noteBookData;
    }

    /***
     * 切换笔记本类型
     */
    /*private static synchronized void switchNoteBook(int noteType) {
        //根据笔记本类型 未收藏状态 用户UID 查询 无则创建一个笔记本
        if (mCurrentNoteType != noteType) {
            NoteBookData noteBookData = SQLite.select()
                    .from(NoteBookData.class)
                    .where(NoteBookData_Table.noteType.eq(noteType),
                            NoteBookData_Table.isLock.eq(false),
                            NoteBookData_Table.userUid.eq(getUserUID()))
                    .querySingle();

            if (noteBookData == null) {
                noteBookData = new NoteBookData();
                Date date = TimeUtils.getNowDate();
                noteBookData.noteName = "";
                noteBookData.noteType = noteType;
                noteBookData.createTime = TimeUtils.date2String(date);
                noteBookData.userUid = getUserUID();
                noteBookData.insert();

//                APP_NAME = TimeUtils.date2String(date, DATE_FORMAT) + "_" + noteType;
                //生成一个（.时间戳_笔记本类型）文件夹目录
                FileUtils.createOrExistsDir(getFileSavePath(getUserUID(), mCurrentNotebookId, SUFFIX_NAME_JPG));
            } else {
//                APP_NAME = TimeUtils.date2String(TimeUtils.string2Date(noteBookData.createTime), DATE_FORMAT) + "_" + noteType;

            }
            L.error("abc", "切换页面" + noteType);
        }
    }*/

    /***
     * 保存当前页数据
     */
    public static void saveCurrentPage(final Context context, final int curPage, final IQPenSaveCurPageListener listener) {
        //        L.info(TAG, "curPage = " + curPage + ":" + mCurrentNoteType);

        //        ThreadPoolUtils.getThreadPool().submit(new Runnable() {
        //            @Override
        //            public void run() {
        //保存笔迹
        if (curPage != -1) {
            boolean b = StrokesUtilForQpen.saveStrokes(mCurPageStrokesCache, getStrokesPath(getCurrentNotebookId(), curPage));
        }

        if (curPage != -1 && !TextUtils.isEmpty(mCurrentNotebookId)) {

            //根据当前笔记本ID、当前页码数，未锁状态 当前所属用户UID 查询当前的bitmap是否存在，存在则更新 不存在插入数据
            PageData currentPageData = SQLite.select()
                    .from(PageData.class)
                    .where(PageData_Table.pageNum.eq(curPage),
                            PageData_Table.isLock.eq(false),
                            PageData_Table.noteBookId.eq(mCurrentNotebookId),
                            PageData_Table.userUid.eq(getUserUID()))
                    .querySingle();

            //            byte[] data = BitmapUtil.bitmap2Bytes(bitmap);

            String pagePath = getFileSavePath(getUserUID(), mCurrentNotebookId, SUFFIX_NAME_JPG) +
                    FILE_SEPARATOR + com.eningqu.aipen.common.utils.FileUtils.changeStr2Zero(curPage + "") +
                    FILE_SEPARATOR + curPage + SUFFIX_NAME_JPG;

            File file1 = new File(getFileSavePath(getUserUID(), mCurrentNotebookId, SUFFIX_NAME_JPG));
            if (!file1.exists()) {
                file1.mkdirs();
            }

            if (currentPageData != null) {
                String name = !TextUtils.isEmpty(currentPageData.name) ? currentPageData.name : context.getString(R.string.label_text) + curPage;
                SQLite.update(PageData.class)
                        .set(/*PageData_Table.data.eq(data),*/
                                PageData_Table.picUrl.eq(pagePath),
                                PageData_Table.lastModifyTime.eq(TimeUtils.date2String(TimeUtils.getNowDate())),
                                PageData_Table.syncState.eq(0),
                                PageData_Table.syncVersion.eq(currentPageData.syncVersion + 1),
                                PageData_Table.name.eq(name))
                        .where(PageData_Table.pageNum.eq(curPage),
                                PageData_Table.isLock.eq(false),
                                PageData_Table.noteBookId.eq(mCurrentNotebookId),
                                PageData_Table.userUid.eq(getUserUID()))
                        .query();
            } else {
                PageData pageDataSave = new PageData();
                pageDataSave.pageNum = curPage;
                //                pageDataSave.data = data;
                pageDataSave.noteType = mCurrentNoteType;
                pageDataSave.noteBookId = mCurrentNotebookId;
                pageDataSave.lastModifyTime = TimeUtils.date2String(TimeUtils.getNowDate());
                pageDataSave.picUrl = pagePath;
                pageDataSave.syncVersion = 1;
                pageDataSave.syncState = 0;
                pageDataSave.name = context.getString(R.string.label_text) + curPage;
                pageDataSave.userUid = getUserUID();
                long insert = pageDataSave.insert();
            }

            if (null != listener) {
                listener.onSuccessful();
            }
            //            Log.w("abc", "图片路径 =" + pagePath);
            //保存当前的页码图片
            //            BitmapUtil.bitmap2File(bitmap, pagePath, 0);
            //            L.info(TAG, "save page=" + curPage);
        }
        //            }
        //        });
    }


    /**
     * 一个用户uid对应一个文件夹，uid文件夹下保存多本书的文件夹
     *
     * @param userId
     * @param notebookId
     * @param fileType
     * @return
     */
    public static String getFileSavePath(String userId, String notebookId, String fileType) {
        String currentSavePath = userId + FILE_SEPARATOR + notebookId;
        return NQ_SAVE_ROOT_PATH + FILE_SEPARATOR + currentSavePath;
//        if (SUFFIX_NAME_JPG.equals(fileType)) {
//            return NQ_SAVE_ROOT_PATH + FILE_SEPARATOR + currentSavePath + FILE_SEPARATOR + "_jpg";
//        } else if (SUFFIX_NAME_PCM.equals(fileType)) {
//            return NQ_SAVE_ROOT_PATH + FILE_SEPARATOR + currentSavePath + FILE_SEPARATOR + "_audio";
//        } else if (SUFFIX_NAME_PDF.equals(fileType)) {
//            return NQ_SAVE_ROOT_PATH + FILE_SEPARATOR + currentSavePath + FILE_SEPARATOR + "_pdf";
//        } else if (SUFFIX_NAME_HWR.equals(fileType)) {
//            return NQ_SAVE_ROOT_PATH + FILE_SEPARATOR + currentSavePath + FILE_SEPARATOR + "_hwr";
//        } else {
//            return NQ_SAVE_ROOT_PATH + FILE_SEPARATOR + currentSavePath + FILE_SEPARATOR + "_txt";
//        }
    }

    public static String getSharePath(String notebook_id, int pageNum, String fileType) {
        return getFileSavePath(getUserUID(), notebook_id, fileType) + "_" + notebook_id + "_" + pageNum + fileType;
    }

    private static String path;

    public static void setCurrentSharePath(String notebook_id, int pageNum, String fileType) {
        path = getFileSavePath(getUserUID(), notebook_id, fileType) + "_" + System.currentTimeMillis() + "_" + pageNum + fileType;
    }

    public static String getCurrentSharePath() {
        return path;
    }

    public static String getAudioPathDir(String userId, String notebookId, int page, String time) {

        String path = getFileSavePath(userId, notebookId, SUFFIX_NAME_PCM) +
                FILE_SEPARATOR + com.eningqu.aipen.common.utils.FileUtils.changeStr2Zero(page + "")
                + FILE_SEPARATOR + time + SUFFIX_NAME_PCM;
        if (null == time || "".equals(time)) {
            path = getFileSavePath(userId, notebookId, SUFFIX_NAME_PCM) +
                    FILE_SEPARATOR + com.eningqu.aipen.common.utils.FileUtils.changeStr2Zero(page + "")
                    + FILE_SEPARATOR;
        }
        return path;
    }

    public static String getStrokesPath(String noteBookId, int page) {
        String path = getFileSavePath(getUserUID(), noteBookId, SUFFIX_NAME_TXT) +
                FILE_SEPARATOR + com.eningqu.aipen.common.utils.FileUtils.changeStr2Zero(page + "") +
                FILE_SEPARATOR + page + SUFFIX_NAME_TXT;
        return path;
    }

    public static String getHwrFilePath(String noteBookId, int page) {
        String path = getFileSavePath(getUserUID(), noteBookId, SUFFIX_NAME_HWR) +
                FILE_SEPARATOR + com.eningqu.aipen.common.utils.FileUtils.changeStr2Zero(page + "") +
                FILE_SEPARATOR + page + SUFFIX_NAME_HWR;
        return path;
    }

    public static synchronized void saveCurrentPage(int j, Bitmap bitmap) {
        if (mCurrentPage != -1 && !TextUtils.isEmpty(mCurrentNotebookId) && bitmap != null) {
            //根据当前笔记本类型、当前页码数，未锁状态 当前所属用户UID 查询当前的bitmap是否存在，存在则更新 不存在插入数据
            PageData currentPageData = SQLite.select()
                    .from(PageData.class)
                    .where(PageData_Table.pageNum.eq(mCurrentPage),
                            PageData_Table.isLock.eq(false),
                            PageData_Table.noteBookId.eq(mCurrentNotebookId),
                            PageData_Table.userUid.eq(getUserUID()))
                    .querySingle();

            byte[] data = BitmapUtil.bitmap2Bytes(bitmap);


            String pagePath = getFileSavePath(getUserUID(), mCurrentNotebookId, SUFFIX_NAME_JPG) +
                    FILE_SEPARATOR + com.eningqu.aipen.common.utils.FileUtils.changeStr2Zero(mCurrentPage + "") +
                    FILE_SEPARATOR + mCurrentPage + SUFFIX_NAME_JPG;

            if (data.length > 0) {
                if (currentPageData != null) {
                    SQLite.update(PageData.class)
                            .set(PageData_Table.data.eq(data),
                                    PageData_Table.picUrl.eq(pagePath))
                            .where(PageData_Table.pageNum.eq(mCurrentPage),
                                    PageData_Table.isLock.eq(false),
                                    PageData_Table.noteBookId.eq(mCurrentNotebookId),
                                    PageData_Table.userUid.eq(getUserUID()))
                            .query();
                } else {
                    PageData pageDataSave = new PageData();
                    pageDataSave.pageNum = mCurrentPage;
                    pageDataSave.data = data;
                    pageDataSave.noteType = mCurrentNoteType;
                    pageDataSave.noteBookId = mCurrentNotebookId;
                    pageDataSave.lastModifyTime = TimeUtils.date2String(TimeUtils.getNowDate());
                    pageDataSave.picUrl = pagePath;
                    pageDataSave.userUid = getUserUID();
                    pageDataSave.insert();
                }
            }
            //保存当前的页码图片
            BitmapUtil.bitmap2File(bitmap, pagePath, j);
            //            Constant.SHARE_PATH_JPG = pagePath;
            //            L.error(TAG, "save page=" + mCurrentPage);
        }
    }

    /**
     * 创建新的页面
     *
     * @param notebookId
     * @param noteType
     * @param page
     */
    public static boolean createPageData(Context context, String notebookId, int noteType, int page) {
        //根据page（页数）参数查询bitmap数据
        PageData pageDataQuery = SQLite.select()
                .from(PageData.class)
                .where(PageData_Table.pageNum.eq(page),
                        PageData_Table.isLock.eq(false),
                        PageData_Table.noteBookId.eq(notebookId),
                        PageData_Table.userUid.eq(getUserUID()))
                .querySingle();

        if (pageDataQuery != null) {
            pageDataQuery.noteType = noteType;
            pageDataQuery.lastModifyTime = TimeUtils.date2String(TimeUtils.getNowDate());
            return pageDataQuery.update();
        } else {
            pageDataQuery = new PageData();
            pageDataQuery.name = context.getString(R.string.label_text) + page;
            pageDataQuery.pageNum = page;
            pageDataQuery.noteType = noteType;
            pageDataQuery.noteBookId = notebookId;
            pageDataQuery.lastModifyTime = TimeUtils.date2String(TimeUtils.getNowDate());
            pageDataQuery.userUid = getUserUID();
            return pageDataQuery.insert() > 0 ? true : false;
        }
    }

    public static boolean createPageData(Context context, String notebookId, int noteType, int page, String name) {
        //根据page（页数）参数查询bitmap数据
        PageData pageDataQuery = SQLite.select()
                .from(PageData.class)
                .where(PageData_Table.pageNum.eq(page),
                        PageData_Table.isLock.eq(false),
                        PageData_Table.noteBookId.eq(notebookId),
                        PageData_Table.userUid.eq(getUserUID()))
                .querySingle();

        if (pageDataQuery != null) {
            pageDataQuery.noteType = noteType;
            pageDataQuery.lastModifyTime = TimeUtils.date2String(TimeUtils.getNowDate());
            return pageDataQuery.update();
        } else {
            pageDataQuery = new PageData();
            pageDataQuery.name = name;
            pageDataQuery.pageNum = page;
            pageDataQuery.noteType = noteType;
            pageDataQuery.noteBookId = notebookId;
            pageDataQuery.lastModifyTime = TimeUtils.date2String(TimeUtils.getNowDate());
            pageDataQuery.userUid = getUserUID();
            return pageDataQuery.insert() > 0 ? true : false;
        }
    }

    /***
     * 加载某页数据
     */
    public static void loadPageData(String notebookId, int noteType, int page) {

        //        Bitmap bitmap=null;
        //从保存的笔画文件中加载当前页的笔画
        PageStrokesCacheBean pageStrokesCache = StrokesUtilForQpen.getStrokes(new File(AppCommon.getStrokesPath(notebookId, page)));
        if (null == pageStrokesCache) {
            L.info(TAG, "loadPageData() pageStrokesCache=" + pageStrokesCache + ", mCurPageStrokesCache=" + mCurPageStrokesCache);
            if (mCurPageStrokesCache == null) {
                mCurPageStrokesCache = getCurPageStrokesCache();
            } else {
                //清空上一页的笔迹
                mCurPageStrokesCache.getStrokesBeans().clear();
            }
        } else {
            if (null != mCurPageStrokesCache) {
                mCurPageStrokesCache.getStrokesBeans().clear();
            }
            L.info(TAG, "pageStrokesCache = " + pageStrokesCache);
            mCurPageStrokesCache = pageStrokesCache;
        }
        if (null != mCurPageStrokesCache) {
            mCurPageStrokesCache.setUserId(getUserUID());
            mCurPageStrokesCache.setNotebookId(getCurrentNotebookId());
            mCurPageStrokesCache.setPage(getCurrentPage());
            mCurPageStrokesCache.setBg(getCurrentNoteType() + "");
        }

        //根据page（页数）参数查询bitmap数据
        PageData pageDataQuery = SQLite.select()
                .from(PageData.class)
                .where(PageData_Table.pageNum.eq(page),
                        PageData_Table.isLock.eq(false),
                        PageData_Table.noteBookId.eq(notebookId),
                        PageData_Table.userUid.eq(getUserUID()))
                .querySingle();

        if (pageDataQuery != null) {
            if (pageDataQuery.data != null && pageDataQuery.data.length > 0) {
                //                bitmap = BitmapUtil.bytes2Bitmap(pageDataQuery.data);
                //                bitmap = BitmapUtil.createBitmap();
                //                Log.w("abc","mmm "+bitmap);
            }
            /*mCurrentBitmap = BitmapUtil.file2Bitmap(NQ_SAVE_ROOT_PATH + APP_NAME + FILE_SEPARATOR + pageDataQuery.getPageNum() + SUFFIX_NAME_JPG);
            if(mCurrentBitmap == null){
                mCurrentBitmap = BitmapUtil.createBitmap();
            }*/
        } else {
            //            bitmap = BitmapUtil.createBitmap();
            pageDataQuery = new PageData();
            pageDataQuery.pageNum = page;
            pageDataQuery.noteType = noteType;
            pageDataQuery.noteBookId = mCurrentNotebookId;
            pageDataQuery.lastModifyTime = TimeUtils.date2String(TimeUtils.getNowDate());
            pageDataQuery.userUid = getUserUID();
            pageDataQuery.insert();
        }
        L.info(TAG, "loadPageData page=" + page);
        //        return bitmap;
    }

    /***
     * 加载某页数据
     */
    public static PageData loadPageData(String notebookId, int page) {

        //根据page（页数）参数查询bitmap数据
        PageData pageDataQuery = SQLite.select()
                .from(PageData.class)
                .where(PageData_Table.pageNum.eq(page),
                        PageData_Table.isLock.eq(AppCommon.isCurrentNotebookLocked()),
                        PageData_Table.noteBookId.eq(notebookId),
                        PageData_Table.userUid.eq(getUserUID()))
                .querySingle();
        return pageDataQuery;
    }

    /***
     * 加载某个笔记本的页数据
     */
    public static List<PageData> loadPageDataList(String notebookId, boolean addNameKey) {
        List<PageData> pageDataQuery = null;
        if (addNameKey) {
            pageDataQuery = SQLite.select()
                    .from(PageData.class)
                    .where(PageData_Table.noteBookId.eq(notebookId),
                            PageData_Table.userUid.eq(getUserUID()),
                            PageData_Table.pageNum.between(1).and(186),
                            PageData_Table.name.notEq("")).orderBy(PageData_Table.pageNum, true)
                    .queryList();
        } else {
            pageDataQuery = SQLite.select().distinct()
                    .from(PageData.class)
                    .where(PageData_Table.noteBookId.eq(notebookId),
                            PageData_Table.pageNum.between(1).and(186),
                            PageData_Table.userUid.eq(getUserUID())).orderBy(PageData_Table.pageNum, true)
                    .queryList();

        }
        return pageDataQuery;
    }

    /***
     * 加载某个笔记本的页数据
     */
    public static List<PageData> loadPageDataLabelList(String notebookId) {
        List<PageData> pageDataQuery = null;

        pageDataQuery = SQLite.select().distinct()
                .from(PageData.class)
                .where(PageData_Table.noteBookId.eq(notebookId),
                        PageData_Table.userUid.eq(getUserUID()),
                        PageData_Table.name.notEq(""))/*.orderBy(PageData_Table.name, true)*/
                .queryList();

        return pageDataQuery;
    }

    /*** 当前登录用户*/
    private static UserInfoData userInfo;

    /*** 当前AAS登录用户*/
    private static AASUserInfoData aasUserInfo;

    /*** 当前彩云业务登录用户*/
    private static OseUserInfoData oseUserInfoData;
    public static String getUserUID() {
        if (null == userInfo) {
            L.error(TAG, "userInfo is null");
            return "";
        }
        return userInfo.userUid;
    }

    public static UserInfoData getUserInfo() {
        if (userInfo == null) {
            //这里放一下本地app选中的语言
            userInfo = new UserInfoData();
            userInfo.userUid = Constant.LANGUAGE;
        }
        return userInfo;
    }

    public static void setUserInfo(UserInfoData loginInfo) {
        userInfo = loginInfo;
        //        Gson gson = new Gson();
        //        userInfo = gson.fromJson(SpUtils.getString(mContext, SpUtils.LOGIN_INFO), UserInfoData.class);;
    }

    private static BluetoothData bluetooth;

    public static void setBluetooth(BluetoothData bluetoothData) {
        bluetooth = bluetoothData;
    }

//    /**
//     * 蓝牙搜索Activity是否打开
//     */
//    public static DeviceListActivity deviceListActivity;
//
//    public static void setBleDeviceActivity(DeviceListActivity ble) {
//        deviceListActivity = ble;
//    }

    /**
     * 登录检查
     *
     * @return
     */
    public static boolean checkLogin() {
        return loadUserInfo() == null ? false : true;
        //        String token = SpUtils.getString(mContext, SpUtils.LOGIN_TOKEN);
        //        if ("".equals(token)) {
        //            return true;
        //        }else{
        //            return true;
        //        }
    }

    /**
     * 登录用户信息
     *
     * @return
     */
    public static UserInfoData loadUserInfo() {
        if (userInfo == null) {
            try {
                userInfo = SQLite.select().from(UserInfoData.class).querySingle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return userInfo;
    }

    /**
     * 登录用户信息临时
     *
     * @return
     */
    public static UserInfoData getUser() {
        UserInfoData userInfo = loadUserInfo();
        if (userInfo == null) {
            userInfo = new UserInfoData();
        }
        return userInfo;
    }


    /**
     * 彩云登录用户信息
     *
     * @return
     */
    public static AASUserInfoData loadAASUserInfo() {
        if (aasUserInfo == null) {
            try {
                aasUserInfo = SQLite.select().from(AASUserInfoData.class).querySingle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(null== aasUserInfo){
            aasUserInfo = new AASUserInfoData();
        }
        return aasUserInfo;
    }

    /**
     * 彩云登录用户信息
     *
     * @return
     */
    public static OseUserInfoData loadOseUserInfo() {
        if (oseUserInfoData == null) {
            try {
                oseUserInfoData = SQLite.select().from(OseUserInfoData.class).querySingle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(null== oseUserInfoData){
            oseUserInfoData = new OseUserInfoData();
        }
        return oseUserInfoData;
    }

    /**
     * 保存彩云用户信息
     * @param mcLoginRsp
     * @return
     */
    public static boolean saveMCloudUserInfo(AASLoginRsp mcLoginRsp){
        if(null==mcLoginRsp){
            return false;
        }
        AASUserInfoData userInfoData = loadAASUserInfo();
        if(null==userInfoData){
            return false;
        }
        //清除缓存
        userInfoData.delete();
        //保存新数据
        if(null!=mcLoginRsp.getUserExtInfo()){
            userInfoData.accessToken = mcLoginRsp.getUserExtInfo().getAccessToken();
            userInfoData.isRegWeibo = mcLoginRsp.getUserExtInfo().getIsRegWeibo();
            userInfoData.passID = mcLoginRsp.getUserExtInfo().getPassID();
        }

        if(null!=mcLoginRsp.getServerinfo()){
            userInfoData.calURL = mcLoginRsp.getServerinfo().getCalURL();
            userInfoData.rifurl = mcLoginRsp.getServerinfo().getRifurl();
            userInfoData.testTermConnectURL = mcLoginRsp.getServerinfo().getTestTermConnectURL();
        }

        userInfoData.error = mcLoginRsp.getError();
        userInfoData.desc = mcLoginRsp.getDesc();
        userInfoData.imspwd = mcLoginRsp.getImspwd();
        userInfoData.sbc = mcLoginRsp.getSbc();
        userInfoData.domain = mcLoginRsp.getDomain();
        userInfoData.svnlist = mcLoginRsp.getSvnlist();
        userInfoData.svnuser = mcLoginRsp.getSvnuser();
        userInfoData.svnpwd = mcLoginRsp.getSvnpwd();
        userInfoData.htslist = mcLoginRsp.getHtslist();
        userInfoData.userType = mcLoginRsp.getUserType();
        userInfoData.userid = mcLoginRsp.getUserid();
        userInfoData.loginid = mcLoginRsp.getLoginid();
        userInfoData.heartime = mcLoginRsp.getHeartime();
        userInfoData.funcId = mcLoginRsp.getFuncId();
        userInfoData.token = mcLoginRsp.getToken();
        userInfoData.expiretime = mcLoginRsp.getExpiretime();
        userInfoData.authToken = mcLoginRsp.getAuthToken();
        userInfoData.atExpiretime = mcLoginRsp.getAtExpiretime();
        userInfoData.deviceid = mcLoginRsp.getDeviceid();
        userInfoData.account = mcLoginRsp.getAccount();
        userInfoData.expiryDate = mcLoginRsp.getExpiryDate();
        userInfoData.areaCode = mcLoginRsp.getAreaCode();
        userInfoData.provCode = mcLoginRsp.getProvCode();
        userInfoData.srvInfoVer = mcLoginRsp.getSrvInfoVer();

        return userInfoData.save();
    }

    /**
     * 保存彩云业务头
     * @param oseUserInfoData
     * @return
     */
    public static boolean saveOseUserInfo(OseUserInfoData oseUserInfoData){
        if(null==oseUserInfoData){
            return false;
        }
        OseUserInfoData userInfoData = loadOseUserInfo();
        if(null==userInfoData){
            return false;
        }
        //清除缓存
        userInfoData.delete();
        userInfoData.APP_NUMBER = oseUserInfoData.getAPP_NUMBER();
        userInfoData.ERRORCODE = oseUserInfoData.getERRORCODE();
        userInfoData.isSwitch = oseUserInfoData.getIsSwitch();
        userInfoData.NOTE_TOKEN = oseUserInfoData.getNOTE_TOKEN();
        userInfoData.APP_AUTH = oseUserInfoData.getAPP_AUTH();
        return userInfoData.save();
    }

    public static int convertPower(float battle) {
        int leavel = 10;
        if (battle > 4.1) {
            leavel = 10;
        } else if (battle <= 4.1 && battle > 3.96) {
            leavel = 9;
        } else if (battle <= 3.96 && battle > 3.9) {
            leavel = 8;
        } else if (battle <= 3.9 && battle > 3.86) {
            leavel = 7;
        } else if (battle <= 3.86 && battle > 3.84) {
            leavel = 6;
        } else if (battle <= 3.84 && battle > 3.8) {
            leavel = 5;
        } else if (battle <= 3.8 && battle > 3.76) {
            leavel = 4;
        } else if (battle <= 3.76 && battle > 3.6) {
            leavel = 3;
        } else if (battle <= 3.6 && battle > 3.48) {
            leavel = 2;
        } else if (battle <= 3.48 && battle > 3.12) {
            leavel = 1;
        } else if (battle <= 3.12) {
            leavel = 0;
        }
        return leavel;
    }

    public static int getPower(int power) {
        int drawableId = R.drawable.icon_bt_connected;
        switch (power) {
            case -1:
                drawableId = R.drawable.icon_bt_disconnected;
                break;
            case 0:
                drawableId = R.drawable.icon_bt_connected;
                break;
            case 1:
                drawableId = R.drawable.icon_bt_connected;
                break;
            case 2:
                drawableId = R.drawable.icon_bt_connected;
                break;
            case 3:
                drawableId = R.drawable.icon_bt_connected;
                break;
            case 4:
                drawableId = R.drawable.icon_bt_connected;
                break;
            case 5:
                drawableId = R.drawable.icon_bt_connected;
                break;
            case 6:
                drawableId = R.drawable.icon_bt_connected;
                break;
            case 7:
                drawableId = R.drawable.icon_bt_connected;
                break;
            case 8:
                drawableId = R.drawable.icon_bt_connected;
                break;
            case 9:
                drawableId = R.drawable.icon_bt_connected;
                break;
            case 10:
                drawableId = R.drawable.icon_bt_connected;
                break;
        }
        return drawableId;
    }


    /***
     * 加载已连接过的蓝牙信息  p20需要用到这个数据库存储
     * @return
     */
    public static BluetoothDevice loadBleInfo2() {
        BluetoothDevice bluetooth = SQLite.select()
                .from(BluetoothDevice.class)
                .where(BluetoothDevice_Table.userUid.eq(getUserUID()))
                .querySingle();
        return bluetooth;
    }

    public static void deleteBluetoothData() {
        SQLite.delete(BluetoothDevice.class).where(BluetoothDevice_Table.userUid.eq(getUserUID())).execute();
    }

    /**
     * 退出登录重置
     */
    public static void logoutReset(int flag) {
        switch (flag) {
            case 0: {
                if (BluetoothClient.getBle().isBleConnecting) {
                    BluetoothClient.getBle().disconnect(1);
                }

                AppCommon.isLogout = true;
                AppCommon.setCurrentNotebookId("");
                AppCommon.setCurrentNoteType(-1);
                AppCommon.setCurrentPage(-1);
                //                if (AppCommon.getCurrentBitmap() != null && !AppCommon.getCurrentBitmap().isRecycled()) {
                //                    AppCommon.getCurrentBitmap().recycle();
                //                    AppCommon.setCurrentBitmap(null);
                //                }
                System.gc();

                AppCommon.setUserInfo(null);
                AppCommon.setBluetooth(null);
                break;
            }
            case 1: {
//                AFPenClientCtrl.getInstance().disconnect();
                PenSdkCtrl.getInstance().disconnect();
                AppCommon.isLogout = true;
                AppCommon.setCurrentNotebookId("");
                AppCommon.setCurrentNoteType(-1);
                AppCommon.setCurrentPage(-1);
                //                AppCommon.setCurrentBitmap(null);
                AppCommon.setCurrentNoteBookData(null);
                AppCommon.setCurPageStrokesCache(null);
                AppCommon.setUserInfo(null);
                AppCommon.setBluetooth(null);
                break;
            }
        }
    }

    //绘画重置
    public static void drawReset() {
        /*AppCommon.setCurrentNoteType(-1);
        AppCommon.setCurrentPage(-1);
        AppCommon.setCurrentBitmap(null);*/
    }

    //-------------------------------------

    /**
     * 该名称是否已存在
     *
     * @param name
     * @return
     */
    public static boolean existNotebook(@NonNull String name) {
        List<NoteBookData> list = loadNoteBookData(2);
        for (NoteBookData noteBookData : list) {
            if (name.equals(noteBookData.noteName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 重命名笔记本
     */
    public static void renameNoteBook(String userUid, String notebookId, String newNoteName, IQPenRenameNotebookListener callback) {
        try {
            SQLite.update(NoteBookData.class)
                    .set(NoteBookData_Table.noteName.eq(newNoteName))
                    .where(NoteBookData_Table.isLock.eq(false),
                            NoteBookData_Table.notebookId.eq(notebookId),
                            NoteBookData_Table.userUid.eq(userUid))
                    .query();
            callback.onSuccessful();
        } catch (Exception e) {
            callback.onFail();
        }
    }

    public static void createMigrationNoteBook(int noteType, String noteCover, String userUid, String notebookName, String createTime, IQPenCollectNotebookListener callback) {
        //根据笔记本类型 未收藏状态 用户UID 查询 无则创建一个笔记本
        NoteBookData noteBookData = new NoteBookData();
        Date date = TimeUtils.getNowDate();
        if (null == notebookName) {
            noteBookData.noteName = "";
        } else {
            noteBookData.noteName = notebookName;
        }
        noteBookData.noteType = noteType;

        noteBookData.noteCover = (Integer.parseInt(noteCover) - 1) + "";
        noteBookData.createTime = TimeUtils.date2String(date);
        noteBookData.userUid = userUid;
        noteBookData.notebookId = createTime;
//        List<NoteBookData> list = SpinNotebookManager.getInstance().getAllNoteBookDatas();
//        boolean havaData = false;
//        for (NoteBookData bookData : list){
//            if (createTime.equals(bookData.createTime)){
//                havaData = true;
//                break;
//            }
//        }
//        if (!havaData){
//            long ret = noteBookData.insert();
//            Log.w("test", "ret = " + ret);
//        }else{
//            boolean ret = noteBookData.update();
//            Log.w("test", "ret = " + ret);
//        }
        long ret = noteBookData.insert();
        Log.w("test", "ret = " + ret);
    }

    public static void createNoteBook(int noteType, String noteCover, String userUid, String notebookName, IQPenCreateNotebookListener callback) {
        //根据笔记本类型 未收藏状态 用户UID 查询 无则创建一个笔记本
        NoteBookData noteBookData = new NoteBookData();
        Date date = TimeUtils.getNowDate();
        if (null == notebookName) {
            noteBookData.noteName = "";
        } else {
            noteBookData.noteName = notebookName;
        }
        noteBookData.noteType = noteType;
        noteBookData.noteCover = noteCover;
        noteBookData.createTime = TimeUtils.date2String(date);
        noteBookData.userUid = userUid;
        noteBookData.notebookId = TimeUtils.date2Millis(date) / 1000 + "";
        long ret = noteBookData.insert();

        if (ret > 0) {
            //创建文件夹,一个用户uid对应一个文件夹，uid文件夹下保存多本书的文件夹
            //生成一个（.时间戳_笔记本类型） 图片文件夹目录
            FileUtils.createOrExistsDir(getFileSavePath(getUserUID(), noteBookData.notebookId, SUFFIX_NAME_JPG));
//            //生成一个（.时间戳_笔记本类型） 音频文件夹目录
//            FileUtils.createOrExistsDir(getFileSavePath(getUserUID(), noteBookData.notebookId, SUFFIX_NAME_PCM));
//            //生成一个（.时间戳_笔记本类型） 文本文件夹目录
//            FileUtils.createOrExistsDir(getFileSavePath(getUserUID(), noteBookData.notebookId, SUFFIX_NAME_TXT));
//            //生成一个识别文件夹
//            FileUtils.createOrExistsDir(getFileSavePath(getUserUID(), noteBookData.notebookId, SUFFIX_NAME_HWR));

            callback.onSuccessful(noteBookData);
        } else {
            callback.onFail();
        }
    }

    /**
     * 收藏或解锁笔记本
     *
     * @param userUid
     * @param notebookId
     * @param callback
     * @param isLock     true 收藏 false解锁笔记本
     */
    public static void collectNoteBook(String userUid, String notebookId, boolean isLock, IQPenCollectNotebookListener callback) {
        try {
            NoteBookData noteBookData = SQLite.select().from(NoteBookData.class)
                    .where(NoteBookData_Table.isLock.eq(!isLock),
                            NoteBookData_Table.notebookId.eq(notebookId),
                            NoteBookData_Table.userUid.eq(userUid))
                    .querySingle();
            if (noteBookData != null) {
                SQLite.update(NoteBookData.class)
                        .set(NoteBookData_Table.isLock.eq(isLock))
                        .where(NoteBookData_Table.notebookId.eq(notebookId), NoteBookData_Table.isLock.eq(!isLock))
                        .query();

                SQLite.update(PageData.class)
                        .set(PageData_Table.isLock.eq(isLock), PageData_Table.noteBookId.eq(notebookId))
                        .where(PageData_Table.noteBookId.eq(notebookId), PageData_Table.isLock.eq(!isLock))
                        .query();

                callback.onSuccessful();
            } else {
                callback.onFail();
            }
            //            AppCommon.drawReset();
        } catch (Exception e) {
            L.error(TAG, e.getMessage());
            callback.onFail();
        }

    }

    /**
     * 收藏或解锁笔记本
     *
     * @param userUid
     * @param notebookIdList
     * @param callback
     * @param isLock         true 收藏 false解锁笔记本
     */
    public static void collectNoteBook(String userUid, List<String> notebookIdList, boolean isLock, IQPenCollectNotebookListener callback) {
        try {
            for (String notebookId : notebookIdList) {

                NoteBookData noteBookData = SQLite.select().from(NoteBookData.class)
                        .where(NoteBookData_Table.isLock.eq(!isLock),
                                NoteBookData_Table.notebookId.eq(notebookId),
                                NoteBookData_Table.userUid.eq(userUid))
                        .querySingle();
                if (noteBookData != null) {
                    SQLite.update(NoteBookData.class)
                            .set(NoteBookData_Table.isLock.eq(isLock))
                            .where(NoteBookData_Table.notebookId.eq(notebookId), NoteBookData_Table.isLock.eq(!isLock))
                            .query();

                    SQLite.update(PageData.class)
                            .set(PageData_Table.isLock.eq(isLock), PageData_Table.noteBookId.eq(notebookId))
                            .where(PageData_Table.noteBookId.eq(notebookId), PageData_Table.isLock.eq(!isLock))
                            .query();
                } else {
                    callback.onFail();
                    return;
                }
            }
            callback.onSuccessful();
        } catch (Exception e) {
            L.error(TAG, e.getMessage());
            callback.onFail();
        }

    }

    /**
     * 删除笔记本
     *
     * @param notebookId 笔记本ID
     */
    public static void deleteNoteBook(String userUid, final String notebookId, IQPenDeleteNotebookListener callback) {
        //查询未收藏的笔记本
        final NoteBookData noteBook = SQLite.select().
                from(NoteBookData.class)
                .where(NoteBookData_Table.notebookId.eq(notebookId),
                        NoteBookData_Table.isLock.eq(false),
                        NoteBookData_Table.userUid.eq(userUid))
                .querySingle();

        if (null != noteBook) {

            SQLite.delete()
                    .from(NoteBookData.class)
                    .where(NoteBookData_Table.isLock.eq(false),
                            NoteBookData_Table.notebookId.eq(notebookId),
                            NoteBookData_Table.userUid.eq(AppCommon.getUserUID()))
                    .query();

            SQLite.delete()
                    .from(PageData.class)
                    .where(PageData_Table.isLock.eq(false),
                            PageData_Table.noteBookId.eq(notebookId),
                            PageData_Table.userUid.eq(AppCommon.getUserUID()))
                    .query();

            //重置
            //        AppCommon.drawReset();
            callback.onSuccessful();
        } else {
            callback.onFail();
        }
    }

    /**
     * 删除笔记本
     *
     * @param notebookIdList 笔记本ID列表
     */
    public static void deleteNoteBook(String userUid, final List<String> notebookIdList, IQPenDeleteNotebookListener callback) {
        if (null == notebookIdList || notebookIdList.size() == 0) {
            callback.onFail();
            return;
        }
        //查询未收藏的笔记本
        for (final String notebookId : notebookIdList) {

            final NoteBookData noteBook = SQLite.select().
                    from(NoteBookData.class)
                    .where(NoteBookData_Table.notebookId.eq(notebookId),
                            NoteBookData_Table.isLock.eq(false),
                            NoteBookData_Table.userUid.eq(userUid))
                    .querySingle();

            if (null != noteBook) {

                SQLite.delete()
                        .from(NoteBookData.class)
                        .where(NoteBookData_Table.isLock.eq(false),
                                NoteBookData_Table.notebookId.eq(notebookId),
                                NoteBookData_Table.userUid.eq(AppCommon.getUserUID()))
                        .query();

                SQLite.delete()
                        .from(PageData.class)
                        .where(PageData_Table.isLock.eq(false),
                                PageData_Table.noteBookId.eq(notebookId),
                                PageData_Table.userUid.eq(AppCommon.getUserUID()))
                        .query();

                //重置
                //        AppCommon.drawReset();
                //删除本地笔记本下的所有图片
                ThreadPoolUtils.getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        FileUtils.delete(getFileSavePath(getUserUID(), notebookId, SUFFIX_NAME_JPG));
//                        FileUtils.deleteDir(getFileSavePath(getUserUID(), notebookId, SUFFIX_NAME_PCM));
//                        FileUtils.deleteDir(getFileSavePath(getUserUID(), notebookId, SUFFIX_NAME_TXT));
//                        FileUtils.deleteDir(getFileSavePath(getUserUID(), notebookId, SUFFIX_NAME_HWR));
                    }
                });
            }
        }

        callback.onSuccessful();
    }

    /**
     * 删除某页的笔画数据
     *
     * @param notebookId
     * @param page
     */
    public static void cleanPageData(@NonNull String notebookId, int page) {
        //清空缓存数据
        if (null != mCurPageStrokesCache && null != mCurPageStrokesCache.getStrokesBeans()) {
            if (mCurPageStrokesCache.getNotebookId().equals(notebookId) && mCurPageStrokesCache.getPage() == page) {
                mCurPageStrokesCache.getStrokesBeans().clear();
            }
        }

        //        List<NQDot> list = AFPenClientCtrl.getInstance().getCacheDots();
        //        if (null != list) {//清理上一页的缓存点
        //            list.clear();
        //        }

        //删除笔记文件内容
        File strokes_file = new File(getStrokesPath(notebookId, page));
        if (strokes_file.exists()) {
            strokes_file.delete();
        }
    }

    /**
     * 查询笔记本
     *
     * @param type 0 未锁定 1锁定 2全部
     * @return
     */
    public static List<NoteBookData> loadNoteBookData(int type) {
        //根据用户UID查询未收藏的书写本
        List<NoteBookData> noteBookDatas = null;
        if (null != getUserUID()) {

            if (type == 0) {
                noteBookDatas = SQLite.select()
                        .from(NoteBookData.class)
                        .where(NoteBookData_Table.isLock.eq(false),
                                NoteBookData_Table.userUid.eq(getUserUID()))
                        .queryList();
            } else if (type == 1) {
                noteBookDatas = SQLite.select()
                        .from(NoteBookData.class)
                        .where(NoteBookData_Table.isLock.eq(true),
                                NoteBookData_Table.userUid.eq(getUserUID()))
                        .queryList();
            } else if (type == 2) {
                noteBookDatas = SQLite.select()
                        .from(NoteBookData.class)
                        .where(NoteBookData_Table.userUid.eq(getUserUID()))
                        .queryList();
            }
        }
        return noteBookDatas;
    }

    /**
     * 查找页签
     *
     * @return
     */
    public static List<PageLabelData> loadPageLabels() {
        List<PageLabelData> pageLabels = SQLite.select().from(PageLabelData.class)
                .where(PageLabelData_Table.userUid.eq(getUserUID()))
                .queryList();
        return pageLabels;
    }

    /**
     * 查找页签
     *
     * @param notebookId
     * @return
     */
    public static List<PageLabelData> loadPageLabels(String notebookId) {
        List<PageLabelData> pageLabels = SQLite.select().from(PageLabelData.class)
                .where(PageLabelData_Table.userUid.eq(getUserUID()),
                        PageLabelData_Table.noteBookId.eq(notebookId))
                .queryList();
        return pageLabels;
    }

    /**
     * 查找页签
     *
     * @param notebookId
     * @param page
     * @return
     */
    public static PageLabelData loadPageLabels(String notebookId, String page) {
        PageLabelData pageLabel = SQLite.select().from(PageLabelData.class)
                .where(PageLabelData_Table.userUid.eq(getUserUID()),
                        PageLabelData_Table.noteBookId.eq(notebookId),
                        PageLabelData_Table.pageId.eq(page))
                .querySingle();
        return pageLabel;
    }

    public static void insertData(String notebookId, int page, int noteType) {
        if (page < 1) {
            L.error(TAG, "It's a error page number. curPageNum=" + page);
        }
        PageData pageData = SQLite.select()
                .from(PageData.class)
                .where(PageData_Table.noteBookId.eq(notebookId),
                        PageData_Table.pageNum.eq(page),
                        PageData_Table.isLock.eq(false),
                        PageData_Table.userUid.eq(AppCommon.getUserUID()))
                .querySingle();
        if (pageData == null) {
            pageData = new PageData();
            pageData.pageNum = page;
            pageData.noteType = noteType;
            pageData.noteBookId = notebookId;
            pageData.isLock = false;
            //            pageData.data = BitmapUtil.bitmap2Bytes(bitmap);
            pageData.lastModifyTime = TimeUtils.date2String(TimeUtils.getNowDate());
            pageData.userUid = AppCommon.getUserUID();
            pageData.insert();
        }
    }

    /**
     * 保存页签
     *
     * @param labelName 要保存的标签名字
     */
    public static boolean updatePage(String notebookId, int page, int noteType, String labelName) {
        if (page < 1) {
            L.error(TAG, "It's a error page number. curPageNum=" + page);
            return false;
        }
        PageData pageData = SQLite.select()
                .from(PageData.class)
                .where(PageData_Table.noteBookId.eq(notebookId),
                        PageData_Table.pageNum.eq(page),
                        PageData_Table.isLock.eq(false),
                        PageData_Table.userUid.eq(AppCommon.getUserUID()))
                .querySingle();
        //当前页没有保存  则先保存当前页 再保存页签
        if (pageData == null) {
            pageData = new PageData();
            pageData.pageNum = page;
            pageData.noteType = noteType;
            pageData.noteBookId = notebookId;
            pageData.isLock = false;
            //            pageData.data = BitmapUtil.bitmap2Bytes(bitmap);
            pageData.lastModifyTime = TimeUtils.date2String(TimeUtils.getNowDate());
            pageData.userUid = AppCommon.getUserUID();
            if (null != labelName && !"".equals(labelName)) {
                pageData.name = labelName;
            }
            pageData.insert();
        } else {
            if (null != labelName && !"".equals(labelName)) {
                pageData.name = labelName;
            }
            pageData.lastModifyTime = TimeUtils.date2String(TimeUtils.getNowDate());
            pageData.update();
        }

        PageData pageData2 = SQLite.select()
                .from(PageData.class)
                .where(PageData_Table.pageNum.eq(page),
                        PageData_Table.noteBookId.eq(notebookId),
                        PageData_Table.isLock.eq(false),
                        PageData_Table.userUid.eq(AppCommon.getUserUID()))
                .querySingle();

        if (null != pageData2 && null != pageData2.name) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 保存页签
     *
     * @param labelName 要保存的标签名字
     */
    public static boolean saveLabel(String notebookId, int page, int noteType, String labelName, Bitmap bitmap) {
        if (page < 1) {
            L.error(TAG, "It's a error page number. curPageNum=" + page);
            return false;
        }
        PageData pageData = SQLite.select()
                .from(PageData.class)
                .where(PageData_Table.noteBookId.eq(notebookId),
                        PageData_Table.pageNum.eq(page),
                        PageData_Table.isLock.eq(false),
                        PageData_Table.userUid.eq(AppCommon.getUserUID()))
                .querySingle();
        //当前页没有保存  则先保存当前页 再保存页签
        if (pageData == null) {
            pageData = new PageData();
            pageData.pageNum = page;
            pageData.noteType = noteType;
            pageData.noteBookId = notebookId;
            pageData.isLock = false;
            pageData.data = BitmapUtil.bitmap2Bytes(bitmap);
            pageData.lastModifyTime = TimeUtils.date2String(TimeUtils.getNowDate());
            pageData.userUid = AppCommon.getUserUID();
            pageData.name = labelName;
            pageData.insert();
        } else {
            pageData.name = labelName;
            pageData.lastModifyTime = TimeUtils.date2String(TimeUtils.getNowDate());
            pageData.update();
        }

        PageData pageData2 = SQLite.select()
                .from(PageData.class)
                .where(PageData_Table.pageNum.eq(page),
                        PageData_Table.noteBookId.eq(notebookId),
                        PageData_Table.isLock.eq(false),
                        PageData_Table.userUid.eq(AppCommon.getUserUID()))
                .querySingle();

        if (null != pageData2 && null != pageData2.name) {
            return true;
        } else {
            return false;
        }
    }

    public static String getCurPageKey(String key){
        return key+"_"+getUserUID()+"_"+getCurrentNotebookId()+"_"+getCurrentPage();
    }
}
