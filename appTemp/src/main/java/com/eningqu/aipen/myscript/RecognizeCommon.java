package com.eningqu.aipen.myscript;

import android.graphics.Point;
import android.text.TextUtils;

import com.eningqu.aipen.common.ToastUtils;
import com.eningqu.aipen.BuildConfig;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.EventBusCarrier;
import com.eningqu.aipen.common.utils.EventBusUtil;
import com.eningqu.aipen.common.utils.NingQuLog;
import com.eningqu.aipen.db.model.RecognizeBean;
import com.eningqu.aipen.db.model.RecognizeData;
import com.eningqu.aipen.qpen.StrokesUtilForQpen;
import com.eningqu.aipen.qpen.bean.PageStrokesCacheBean;
import com.eningqu.aipen.qpen.bean.StrokesBean;
import com.myscript.iink.eningqu.IHwRecognition;
import com.myscript.iink.eningqu.IInkSdkManager;
import com.myscript.iink.eningqu.IInkSdkUtils;
import com.myscript.iink.eningqu.bean.MyScriptRealBean;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.blankj.utilcode.util.ThreadUtils.runOnUiThread;

public class RecognizeCommon {


    public static String getAppName() {
        return AppCommon.APP_NAME;
    }

    private static RecognizeCommon sInstance;

    public static RecognizeCommon getInstance() {
        if (null == sInstance) {
            synchronized (RecognizeCommon.class) {
                if (null == sInstance) {
                    sInstance = new RecognizeCommon();
                }
            }
        }
        return sInstance;
    }

    private boolean isRecogning = false;

    public boolean isRecogning() {
        return isRecogning;
    }

    public void setRecogning(boolean recogning) {
        isRecogning = recogning;
    }

    public void recognizeAfterChange(RecognizeCallback recognizeCallback, boolean reset) {
        NingQuLog.debug("myscript in RecognizeCommon", "实时识别" + AppCommon.getCurrentNotebookId() + "--" + AppCommon.getCurrentPage() + "页的部分笔记");
        //当页全部数据
        List<RecognizeBean> recognizeBeans = RecognizeDBmanager.getInstance().getRecognizeListByPage(AppCommon.getUserUID(), AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage());
        PageStrokesCacheBean mCurPageStrokesCache = StrokesUtilForQpen.getStrokes(new File(AppCommon.getStrokesPath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage())));
        if (mCurPageStrokesCache == null) {
            if (recognizeCallback != null) {
                recognizeCallback.onError("3.没有新的笔记");
            }
            return;
        }
        if (recognizeBeans == null || recognizeBeans.isEmpty()) {
            recognize(recognizeCallback, AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), mCurPageStrokesCache.getStrokesBeans(), false);
        } else {
            List<StrokesBean> recognizeList = new ArrayList<>();
            for (StrokesBean strokesBean : mCurPageStrokesCache.getStrokesBeans()) {
                if (strokesBean.getCreateTime() > recognizeBeans.get(recognizeBeans.size() - 1).timestamp) {
                    recognizeList.add(strokesBean);//1624431493759    1624431490568
                }
            }
            if (recognizeList.size() > 0) {
                recognize(recognizeCallback, AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), recognizeList, reset);
            } else {
//                ToastUtils.showShort("没有新的笔记");
                if (recognizeCallback != null) {
                    recognizeCallback.onError("3.没有新的笔记");
                }
            }
        }

    }

    public void recognizeAll(RecognizeCallback recognizeCallback) {
        NingQuLog.debug("myscript in RecognizeCommon", "识别" + AppCommon.getCurrentNotebookId() + "--" + AppCommon.getCurrentPage() + "页的全部笔记");
        PageStrokesCacheBean mCurPageStrokesCache = StrokesUtilForQpen.getStrokes(new File(AppCommon.getStrokesPath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage())));

        List<StrokesBean> recognizeList = new ArrayList<>(mCurPageStrokesCache.getStrokesBeans());
        if (recognizeList.size() > 0) {
            recognize(recognizeCallback, AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), recognizeList, false);
        } else {
            if (recognizeCallback != null) {
                recognizeCallback.onError("5.当前页没有笔记");
            }
        }

    }

    public synchronized void recognize(RecognizeCallback recognizeCallback, String noteBookId, int page, List<StrokesBean> strokesBeans, boolean reset) {
        if ("MY_SCRIPT".equals(BuildConfig.HWR_PROVIDER)) {
            if (isRecogning) {
                return;
            }
            isRecogning = true;
            IInkSdkManager.getInstance().editorClean();
            List<com.myscript.iink.eningqu.bean.StrokesBean> recoList = new ArrayList<>();
            float x1 = -1, y1 = -1, x2 = -1, y2 = -1;
            for (StrokesBean strokesBean : strokesBeans) {
                for (Point point : strokesBean.getDots()) {
                    if (x1 == -1) {
                        x1 = point.x;
                    } else {
                        x1 = Math.min(point.x, x1);
                    }

                    if (y1 == -1) {
                        y1 = point.y;
                    } else {
                        y1 = Math.min(point.y, y1);
                    }

                    if (x2 == -1) {
                        x2 = point.x;
                    } else {
                        x2 = Math.max(point.x, x2);
                    }

                    if (y2 == -1) {
                        y2 = point.y;
                    } else {
                        y2 = Math.max(point.y, y2);
                    }


                }
                recoList.add(new com.myscript.iink.eningqu.bean.StrokesBean(strokesBean.getCreateTime(), strokesBean.getDots()));
            }

            if (recoList.size() == 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isRecogning = false;
                        if (recognizeCallback != null) {
                            recognizeCallback.onError("6.笔画数据为空");
                        }
                    }
                });
                return;
            }
            if (!IInkSdkManager.getInstance().isInitSuccess()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showShort("证书错误...");
                        isRecogning = false;
                        if (recognizeCallback != null) {
                            recognizeCallback.onError("1.证书错误");
                        }
                    }
                });
                return;
            }

            IInkSdkUtils.getInstance().recognizeStrokesByMyScript(recoList, new IHwRecognition() {
                @Override
                public void onHwReco(String result, MyScriptRealBean myScriptRealBean) {
                    if (TextUtils.isEmpty(result)) {
//                        ToastUtils.showShort("识别失败");
                        if (recognizeCallback != null) {
                            recognizeCallback.onError("2.识别到的数据为空");
                            isRecogning = false;
                        }
                        return;
                    }
                    if (result.endsWith("\n")) {
                        result = result.substring(0, result.length() - 1);
                    }
//                    ToastUtils.showShort("识别到：" + result);
                    NingQuLog.debug("myscript in RecognizeCommon", "识别到：" + result);
                    if (myScriptRealBean.getElements() != null) {
                        if (reset) {
                            for (MyScriptRealBean.ElementsBean elementsBean : myScriptRealBean.getElements()) {
                                if (elementsBean != null && elementsBean.getWords() != null) {

                                    for (MyScriptRealBean.ElementsBean.WordsBean wordsBean : elementsBean.getWords()) {
                                        if (!TextUtils.isEmpty(wordsBean.getLabel()) && "\n".equals(wordsBean.getLabel())) {
                                            continue;
                                        }
                                        if (!TextUtils.isEmpty(wordsBean.getLabel()) && wordsBean.getLabel().endsWith("\n")) {
                                            wordsBean.setLabel(wordsBean.getLabel().substring(0, result.length() - 1));
                                        }
                                        if (wordsBean.getBoundingbox() == null) {
                                            continue;
                                        }
                                        RecognizeDBmanager.getInstance().addRecognizeBean(AppCommon.getUserUID(), noteBookId, page, recoList.get(recoList.size() - 1).getCreateTime(), wordsBean.getLabel(),
                                                wordsBean.getBoundingbox().getX(),
                                                wordsBean.getBoundingbox().getY(),
                                                wordsBean.getBoundingbox().getX() + wordsBean.getBoundingbox().getWidth(),
                                                wordsBean.getBoundingbox().getY() + wordsBean.getBoundingbox().getHeight(), true);
                                    }

                                }

                            }
                        } else {
                            RecognizeDBmanager.getInstance().addRecognizeBean(AppCommon.getUserUID(), noteBookId, page, recoList.get(recoList.size() - 1).getCreateTime(), result,
                                    myScriptRealBean.getBoundingbox().getX(),
                                    myScriptRealBean.getBoundingbox().getY(),
                                    myScriptRealBean.getBoundingbox().getX() + myScriptRealBean.getBoundingbox().getWidth(),
                                    myScriptRealBean.getBoundingbox().getY() + myScriptRealBean.getBoundingbox().getHeight(), false);
                        }
                    } else {
                        if (recognizeCallback != null) {
                            recognizeCallback.onError("7.识别到的区域为空");
                            isRecogning = false;
                        }
                        return;
                    }
                    resetLoc(recognizeCallback, noteBookId, page, reset);

                }

                @Override
                public void onError(String error) {
                    if (recognizeCallback != null) {
                        recognizeCallback.onError(error);
                    }
                }
            });
        }
    }

    //重新排序
    public void resetLoc(RecognizeCallback recognizeCallback, String noteBookId, int page, boolean reset) {
        //当页全部数据
        List<RecognizeBean> recognizeBeans = RecognizeDBmanager.getInstance().getRecognizeListByPage(AppCommon.getUserUID(), noteBookId, page);

        //代表着识别结果中的多行多列识别结果，完全拼凑其中的识别结果外层按下标换行得到整页的结果
        List<List<RecognizeBean>> realPageDataList = new ArrayList<>();
        //行数据
        List<RecognizeBean> rawData = new ArrayList<>();
        //对起始区域x排序
        Collections.sort(recognizeBeans, new Comparator<RecognizeBean>() {
            @Override
            public int compare(RecognizeBean o1, RecognizeBean o2) {
                return o1.x >= o2.x ? 1 : -1;
            }
        });
        List<RecognizeBean> recognizeBeansByX = new ArrayList<>(recognizeBeans);
        //对起始区域y排序
        Collections.sort(recognizeBeans, new Comparator<RecognizeBean>() {
            @Override
            public int compare(RecognizeBean o1, RecognizeBean o2) {
                return o1.y >= o2.y ? 1 : -1;
            }
        });
        List<RecognizeBean> recognizeBeansByY = new ArrayList<>(recognizeBeans);
        double maxY = 0, minY = 0;//每行的底部 Y
        double centerY = 0;//每行的center Y
        if (recognizeBeansByY != null && recognizeBeansByY.size() == 1) {
            realPageDataList.add(new ArrayList<>(recognizeBeansByY));
        } else {
            for (int i = 0; i < recognizeBeansByY.size() - 1; i++) {
                //判断换行
                boolean flag1 = (recognizeBeansByY.get(i).y + recognizeBeansByY.get(i).h) / 2 - centerY > 2;
                boolean flag2 = (recognizeBeansByY.get(i).y - centerY >= 2);
                boolean flag3 = (recognizeBeansByY.get(i).y - maxY > -20);
                boolean flag4 = centerY != 0 && (recognizeBeansByY.get(i + 1).y + recognizeBeansByY.get(i + 1).h) / 2 - centerY > 0;
                double percenterY = 0;//平均差
                double percenterY2 = 0;//方差
                //获取待换行列表的平均差和方差
                if (rawData != null && rawData.size() > 0) {
                    for (RecognizeBean bean : rawData) {
                        percenterY = percenterY + (bean.y + bean.h) / 2;
                    }
                    percenterY = percenterY / rawData.size();
                    for (RecognizeBean bean : rawData) {
                        percenterY2 = percenterY2 + (percenterY - ((bean.y + bean.h) / 2)) * (percenterY - ((bean.y + bean.h) / 2));
                    }
                    percenterY2 = percenterY2 / rawData.size();
                }
                //添加一次方差及平均差判断
                boolean flagA = percenterY != 0 && percenterY2 > 0.7 || ((recognizeBeansByY.get(i).y + recognizeBeansByY.get(i).h) / 2) - percenterY > 2;
                if (((flag1 || flag2 || flagA) && flag3 && flag4)) {
                    maxY = Math.max(maxY, recognizeBeansByY.get(i).h);
                    centerY = (recognizeBeansByY.get(i).y + recognizeBeansByY.get(i).h) / 2;

                    //有就先加到新的行
                    if (!rawData.isEmpty()) {
                        realPageDataList.add(new ArrayList<>(rawData));
                        rawData.clear();
                        maxY = recognizeBeansByY.get(i).h;
                        minY = recognizeBeansByY.get(i).y;
                        centerY = (minY + maxY) / 2;
                        if (i + 1 == recognizeBeansByY.size() - 1) {
                            rawData.add(recognizeBeansByY.get(i));
                            realPageDataList.add(new ArrayList<>(rawData));
                            rawData.clear();
                            boolean flag5 = (recognizeBeansByY.get(i + 1).y - maxY > -20);
                            boolean flag6 = centerY != 0 && (recognizeBeansByY.get(i + 1).y + recognizeBeansByY.get(i + 1).h) / 2 - centerY > 0;
                            boolean flag7 = (recognizeBeansByY.get(i + 1).y + recognizeBeansByY.get(i + 1).h) / 2 - centerY > 2;

                            if (flag5 && flag6 && flag7) {
                                //换行
                                rawData.add(recognizeBeansByY.get(i + 1));
                                realPageDataList.add(new ArrayList<>(rawData));
                                rawData.clear();
                            } else {//补足
                                rawData = realPageDataList.get(realPageDataList.size() - 1);
                                rawData.add(recognizeBeansByY.get(i + 1));
                                realPageDataList.set(realPageDataList.size() - 1, new ArrayList<>(rawData));
                                rawData.clear();
                            }

                        } else {
                            maxY = recognizeBeansByY.get(i).h;
                            centerY = (recognizeBeansByY.get(i).h + recognizeBeansByY.get(i).y) / 2;
                            rawData.add(recognizeBeansByY.get(i));
                        }
                        continue;
                    }
                    if (realPageDataList.isEmpty() && recognizeBeansByY.size() > 2) {
                        rawData.add(recognizeBeansByY.get(i));
                        continue;
                    }
                    rawData.add(recognizeBeansByY.get(i));
                    realPageDataList.add(new ArrayList<>(rawData));
                    rawData.clear();


                    if (i + 1 == recognizeBeansByY.size() - 1) {
                        boolean flag5 = (recognizeBeansByY.get(i + 1).y - maxY > -20);
                        boolean flag6 = centerY != 0 && (recognizeBeansByY.get(i + 1).y + recognizeBeansByY.get(i + 1).h) / 2 - centerY > 0;
                        boolean flag7 = (recognizeBeansByY.get(i + 1).y + recognizeBeansByY.get(i + 1).h) / 2 - centerY > 2;
                        if (flag5 && flag6 && flag7) {
                            //换行
                            rawData.add(recognizeBeansByY.get(i + 1));
                            realPageDataList.add(new ArrayList<>(rawData));
                            rawData.clear();
                        } else {//补足
                            rawData = realPageDataList.get(realPageDataList.size() - 1);
                            rawData.add(recognizeBeansByY.get(i + 1));
                            realPageDataList.set(realPageDataList.size() - 1, new ArrayList<>(rawData));
                            rawData.clear();
                        }

                    }
                } else {
                    maxY = recognizeBeansByY.get(i).h;
                    minY = recognizeBeansByY.get(i).y;
                    centerY = (minY + maxY) / 2;

                    rawData.add(recognizeBeansByY.get(i));
                    //最后一个认为是新的行
                    if (i + 1 == recognizeBeansByY.size() - 1) {
                        realPageDataList.add(new ArrayList<>(rawData));
                        rawData.clear();
                        boolean flag5 = (recognizeBeansByY.get(i + 1).y - maxY > -20);
                        boolean flag6 = centerY != 0 && (recognizeBeansByY.get(i + 1).y + recognizeBeansByY.get(i + 1).h) / 2 - centerY > 0;
                        boolean flag7 = (recognizeBeansByY.get(i + 1).y + recognizeBeansByY.get(i + 1).h) / 2 - centerY > 2;
                        if (flag5 && flag6 && flag7) {
                            //换行
                            rawData.add(recognizeBeansByY.get(i + 1));
                            realPageDataList.add(new ArrayList<>(rawData));
                            rawData.clear();
                        } else {//补足
                            rawData = realPageDataList.get(realPageDataList.size() - 1);
                            rawData.add(recognizeBeansByY.get(i + 1));
                            realPageDataList.set(realPageDataList.size() - 1, new ArrayList<>(rawData));
                            rawData.clear();
                        }
                    }
                }

            }
        }
        StringBuilder realResult = new StringBuilder();
        //对不同行的数据进行x排序
        for (List<RecognizeBean> rawList : realPageDataList) {
            //对起始区域y排序
            Collections.sort(rawList, new Comparator<RecognizeBean>() {
                @Override
                public int compare(RecognizeBean o1, RecognizeBean o2) {
                    return o1.x >= o2.x ? 1 : -1;
                }
            });

            for (int i = 0; i < rawList.size(); i++) {
                RecognizeBean recognizeBean = rawList.get(i);
                realResult.append(recognizeBean.recognizeResult);
                if (i - 1 > 0) {
                    //右边的x大于左边的w 时加空格
                    if (rawList.get(i).x - rawList.get(i - 1).w > 0) {
                        realResult.append(" ");
                    }


                } else if (i + 1 < rawList.size()) {
                    //右边的x大于左边的w 5时加空格
                    if (rawList.get(i + 1).x - rawList.get(i).w > 0) {
                        realResult.append(" ");
                    }

                }
            }
            realResult.append("\n");
        }
        RecognizeData recognizeData = RecognizeDBmanager.getInstance().getRecognizeData(noteBookId, page);
        if (recognizeData != null) {
            RecognizeDBmanager.getInstance().updateRecognizeData(AppCommon.getUserUID(), noteBookId, page, realResult.toString());
        } else {
            RecognizeDBmanager.getInstance().addRecognizeData(AppCommon.getUserUID(), noteBookId, page, realResult.toString());
        }

        if (recognizeCallback != null) {
            recognizeCallback.getResult(realResult.toString());
            EventBusCarrier eventBusCarrier = new EventBusCarrier();
            eventBusCarrier.setEventType(Constant.GET_RECOGNIZE_RESULT);
            eventBusCarrier.setObject(realResult.toString());
            EventBusUtil.post(eventBusCarrier);
        }
        isRecogning = false;

    }

}
