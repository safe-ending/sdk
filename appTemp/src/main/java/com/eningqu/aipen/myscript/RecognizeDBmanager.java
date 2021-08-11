package com.eningqu.aipen.myscript;

import android.graphics.Point;
import android.text.TextUtils;

import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.eningqu.aipen.BuildConfig;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.utils.NingQuLog;
import com.eningqu.aipen.db.model.RecognizeBean;
import com.eningqu.aipen.db.model.RecognizeBean_Table;
import com.eningqu.aipen.db.model.RecognizeData;
import com.eningqu.aipen.db.model.RecognizeData_Table;
import com.eningqu.aipen.qpen.StrokesUtilForQpen;
import com.eningqu.aipen.qpen.bean.PageStrokesCacheBean;
import com.eningqu.aipen.qpen.bean.StrokesBean;
import com.myscript.iink.eningqu.IHwRecognition;
import com.myscript.iink.eningqu.IInkSdkManager;
import com.myscript.iink.eningqu.IInkSdkUtils;
import com.myscript.iink.eningqu.bean.MyScriptRealBean;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.blankj.utilcode.util.ThreadUtils.runOnUiThread;

public class RecognizeDBmanager {

    private static String APP_NAME = "aitop";

    public static String getAppName() {
        return APP_NAME;
    }

    private static RecognizeDBmanager sInstance;

    public static RecognizeDBmanager getInstance() {
        if (null == sInstance) {
            synchronized (RecognizeDBmanager.class) {
                if (null == sInstance) {
                    sInstance = new RecognizeDBmanager();
                }
            }
        }
        return sInstance;
    }


    /***
     * 加载某页
     */
    public RecognizeData getRecognizeData(String notebookId, int page) {
        RecognizeData pageDataQuery = null;
        try {
            //根据page（页数）参数查询bitmap数据
            pageDataQuery = SQLite.select()
                    .from(RecognizeData.class)
                    .where(RecognizeData_Table.pageId.eq(page),
                            RecognizeData_Table.noteBookId.eq(notebookId),
                            RecognizeData_Table.userUid.eq(AppCommon.getUserUID()))
                    .querySingle();
        } catch (Exception e) {
        }
        return pageDataQuery;
    }

    /**
     * 更新某页
     */
    public void updateRecognizeData(String userUid, String notebookId, int page, String resultList) {
        try {
            SQLite.update(RecognizeData.class)
                    .set(RecognizeData_Table.resultList.eq(resultList))
                    .where(RecognizeData_Table.pageId.eq(page),
                            RecognizeData_Table.noteBookId.eq(notebookId),
                            RecognizeData_Table.userUid.eq(userUid))
                    .query();
        } catch (Exception e) {
        }
    }

    /**
     * 添加
     */
    public void addRecognizeData(String userUid, String notebookId, int page, String resultList) {
        RecognizeData recognizeData = new RecognizeData();
        Date date = TimeUtils.getNowDate();
        recognizeData.userUid = userUid;
        recognizeData.noteBookId = notebookId;
        recognizeData.pageId = page;
        recognizeData.resultList = resultList;
        recognizeData.insert();

    }

    /**
     * 添加每次的识别区域及结果
     */
    public void addRecognizeBean(String userUid, String notebookId, int page, long timestamp,String result, double x, double y, double w, double h,boolean isAuto) {
        RecognizeBean recognizeBean = new RecognizeBean();
        recognizeBean.userUid = userUid;
        recognizeBean.noteBookId = notebookId;
        recognizeBean.pageId = page;
        recognizeBean.recognizeResult = result;
        recognizeBean.timestamp = timestamp;
        recognizeBean.x = x;
        recognizeBean.y = y;
        recognizeBean.w = w;
        recognizeBean.h = h;
//        recognizeBean.isAuto = isAuto;

        recognizeBean.insert();
    }

    //删除指定页识别区域及结果 集合
    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "recognizeBean")
    public void deleteRecognizeListByPage(String userUid, String notebookId, int page) {
        SQLite.delete()
                .from(RecognizeBean.class)
                .where(RecognizeBean_Table.pageId.eq(page),
                        RecognizeBean_Table.noteBookId.eq(notebookId),
                        RecognizeBean_Table.userUid.eq(userUid))
                .query();
    }

    //删除指定页识别区域及结果 集合
    @OneToMany(methods = {OneToMany.Method.ALL})
    public void deleteRecognizeDataByPage(String userUid, String notebookId, int page) {
        SQLite.delete()
                .from(RecognizeData.class)
                .where(RecognizeData_Table.pageId.eq(page),
                        RecognizeData_Table.noteBookId.eq(notebookId),
                        RecognizeData_Table.userUid.eq(userUid))
                .query();
    }

    //指定页识别区域及结果 集合
    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "recognizeBean")
    public List<RecognizeBean> getRecognizeListByPage(String userUid, String notebookId, int page) {
        List<RecognizeBean> recognizeBean = SQLite.select()
                .from(RecognizeBean.class)
                .where(RecognizeBean_Table.pageId.eq(page),
                        RecognizeBean_Table.noteBookId.eq(notebookId),
                        RecognizeBean_Table.userUid.eq(userUid))
                .queryList();

        return recognizeBean;
    }
}
