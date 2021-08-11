package com.myscript.iink.eningqu;

import android.graphics.Point;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.SPUtils;
import com.myscript.iink.PointerType;
import com.myscript.iink.eningqu.bean.StrokesBean;

import java.util.List;

public class IInkSdkUtils {
    private static IInkSdkUtils instance = null;
    private static final String TAG = IInkSdkUtils.class.getSimpleName();

    public static IInkSdkUtils getInstance() {
        if (null == instance) {
            synchronized (IInkSdkUtils.class) {
                if (null == instance) {
                    instance = new IInkSdkUtils();
                }
            }
        }
        return instance;
    }


    private int pointerId;
    private final long NO_TIMESTAMP = -1;
    private final float NO_PRESSURE = 0.0f;

    /**
     * 识别笔画
     */
    public void recognizeStrokesByMyScript(@NonNull List<StrokesBean> strokesBeanList, @NonNull IHwRecognition iHwRecognition) {
        IInkSdkManager.getInstance().editorClean();
        IInkSdkManager.getInstance().setIHwRecognition(iHwRecognition);
        //读取当前笔记本当前页缓存的识别内容
        String recognFile = IInkSdkManager.getInstance().getRecognFile(AppCommon.getHwrFilePath());

        boolean isReRecogine = false;
        long lastTime = SPUtils.getInstance().getLong("SP_LAST_RECOGNIZE_TIME", 0);

        if (TextUtils.isEmpty(recognFile)) {
            //缓存的识别内容为空
            lastTime = 0;
        }

        if (lastTime == 0) {
            isReRecogine = true;
            recognFile = "";
        } else {
            isReRecogine = false;
        }



        IInkSdkManager.getInstance().editorClean();
        //从保存的笔画文件中加载当前页的笔画

        List<StrokesBean> strokesBeans = strokesBeanList;
        try {
            int i = 0;
            if (null != strokesBeans && strokesBeans.size() > 0) {

                for (StrokesBean strokesBean : strokesBeans) {

//                    if (lastTime >= strokesBean.getCreateTime()) {
//                        continue;
//                    }
                    isReRecogine = true;
                    List<Point> dots = strokesBean.getDots();
                    i = 0;
                    if (null != dots && dots.size() > 2) {
                        pointerId++;
                        //events.add(new PointerEvent().down(dots.get(0).x, dots.get(i).y));
                        IInkSdkManager.getInstance().pointerDown(dots.get(0).x, dots.get(0).y, NO_TIMESTAMP, NO_PRESSURE, PointerType.PEN, pointerId);
                        while (i++ < dots.size() - 2) {
                            //events.add(new PointerEvent().move(dots.get(i).x, dots.get(i).y));
                            IInkSdkManager.getInstance().pointerMove(dots.get(i).x, dots.get(i).y, NO_TIMESTAMP, NO_PRESSURE, PointerType.PEN, pointerId);
                        }
                        Log.i(TAG, "strokes i=" + i + ", up");
                        IInkSdkManager.getInstance().pointerUp(dots.get(i).x, dots.get(i).y, NO_TIMESTAMP, NO_PRESSURE, PointerType.PEN, pointerId);
//                            if (i % 200 == 0) {//一次传入太多会影响到识别时间，停一下先返回部分数据
//                                Thread.sleep(1500);
//                            }
                    }
                    lastTime = strokesBean.getCreateTime();
                }
                SPUtils.getInstance().put("SP_LAST_RECOGNIZE_TIME", lastTime);
                // Feed the editor
//                    IInkSdkManager.getInstance().getEditor().pointerEvents(events.toArray(new PointerEvent[0]), false);
//                    return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        //如果内容为空
//        if (null != iHwRecognition) {
//            if (TextUtils.isEmpty(recognFile) && (null == strokesBeans ||
//                    strokesBeans.size() == 0)) {
//                ToastUtils.showShort("空空如也");
//                iHwRecognition.onHwReco("",null);
//
//            } else if (!isReRecogine) {
//                iHwRecognition.onHwReco("",null);
//
//            }
//        }
    }

//    /**
//     * 识别笔画
//     */
//    private void recognizeStrokesByMyScript(@NonNull List<StrokesBean> strokesBeanList, @NonNull IHwRecognition iHwRecognition) {
//        IInkSdkManager.getInstance().editorClean();
//        IInkSdkManager.getInstance().setIHwRecognition(iHwRecognition);
//        //读取当前笔记本当前页缓存的识别内容
//        String recognFile = IInkSdkManager.getInstance().getRecognFile(AppCommon.getHwrFilePath());
//
//        boolean isReRecogine = false;
//        long lastTime = SPUtils.getInstance().getLong("SP_LAST_RECOGNIZE_TIME", 0);
//
//        if (TextUtils.isEmpty(recognFile)) {
//            //缓存的识别内容为空
//            lastTime = 0;
//        }
//
//        if (lastTime == 0) {
//            isReRecogine = true;
//            recognFile = "";
//        } else {
//            isReRecogine = false;
//        }
//
//        if (null == recoResultSB) {
//            recoResultSB = new StringBuilder();
//        }
//
//        if (!TextUtils.isEmpty(recognFile) && lastTime != 0) {
////            mHandler.removeMessages(MSG_TIME_OUT);
//            Log.i(TAG, "file iHwRecognition:" + recognFile);
//            recoResultSB.append(recognFile.trim());
//            contentHwr = recoResultSB.toString();
//        } else {
//            recoResultSB.delete(0, recoResultSB.length());
//            contentHwr = recoResultSB.toString();
////            runOnUiThread(new Runnable() {
////                @Override
////                public void run() {
////                    mBinding.etHwrContent.setText(contentHwr);
////                    setSwitchTextColor(R.id.tv_tran_original);
////                }
////            });
//        }
//
//
//        IInkSdkManager.getInstance().editorClean();
//        //从保存的笔画文件中加载当前页的笔画
//
//        List<StrokesBean> strokesBeans = strokesBeanList;
//        try {
//            int i = 0;
//            if (null != strokesBeans && strokesBeans.size() > 0) {
//
//                for (StrokesBean strokesBean : strokesBeans) {
//
//                    if (lastTime >= strokesBean.getCreateTime()) {
//                        continue;
//                    }
//                    isReRecogine = true;
//                    List<Point> dots = strokesBean.getDots();
//                    i = 0;
//                    if (null != dots && dots.size() > 2) {
//                        pointerId++;
//                        //events.add(new PointerEvent().down(dots.get(0).x, dots.get(i).y));
//                        IInkSdkManager.getInstance().pointerDown(dots.get(0).x, dots.get(0).y, NO_TIMESTAMP, NO_PRESSURE, PointerType.PEN, pointerId);
//                        while (i++ < dots.size() - 2) {
//                            //events.add(new PointerEvent().move(dots.get(i).x, dots.get(i).y));
//                            IInkSdkManager.getInstance().pointerMove(dots.get(i).x, dots.get(i).y, NO_TIMESTAMP, NO_PRESSURE, PointerType.PEN, pointerId);
//                        }
//                        Log.i(TAG, "strokes i=" + i + ", up");
//                        IInkSdkManager.getInstance().pointerUp(dots.get(i).x, dots.get(i).y, NO_TIMESTAMP, NO_PRESSURE, PointerType.PEN, pointerId);
////                            if (i % 200 == 0) {//一次传入太多会影响到识别时间，停一下先返回部分数据
////                                Thread.sleep(1500);
////                            }
//                    }
//                    lastTime = strokesBean.getCreateTime();
//                }
//                SPUtils.getInstance().put("SP_LAST_RECOGNIZE_TIME", lastTime);
//                // Feed the editor
////                    IInkSdkManager.getInstance().getEditor().pointerEvents(events.toArray(new PointerEvent[0]), false);
////                    return;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        //如果内容为空
//        if (null != iHwRecognition) {
//            if (TextUtils.isEmpty(recognFile) && (null == strokesBeans ||
//                    strokesBeans.size() == 0)) {
//                ToastUtils.showShort("空空如也");
//                iHwRecognition.onHwReco("");
//
//            } else if (!isReRecogine) {
//                iHwRecognition.onHwReco("");
//
//            }
//        }
//    }
//    /**
//     * MyScript 手写识别回调
//     */
//    IHwRecognition iHwRecognition = new IHwRecognition() {
//        @Override
//        public void onHwReco(final String result) {
//            mHandler.removeMessages(MSG_TIME_OUT);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.i(TAG, "back iHwRecognition:" + result);
//                    if (!TextUtils.isEmpty(result)) {
//                        if (null == recoResultSB) {
//                            recoResultSB = new StringBuilder();
//                        }
//                        recoResultSB.append(result);
//                        contentHwr = recoResultSB.toString();
//                        mBinding.etHwrContent.setText(contentHwr);
////                        recoResultSB.delete(0, recoResultSB.length() - 1);
//                    }
//                    setSwitchTextColor(R.id.tv_tran_original);
//                    dismissDialog();
//                }
//            });
//            IInkSdkManager.getInstance().saveRecogn(AppCommon.getHwrFilePath(), contentHwr);
//
//        }
//
//        @Override
//        public void onError(String error) {
//            AFPenClientCtrl.getInstance().setRecoStatus(PEN_RECO_STATUS.NONE);
//            mHandler.removeMessages(MSG_TIME_OUT);
//            contentHwr = "";
//            dismissDialog();
//            dialog = DialogHelper.showErrorMessage(getSupportFragmentManager(), new ConfirmListener() {
//                @Override
//                public void confirm(View view) {
//                    dismissDialog();
//                }
//
//                @Override
//                public void cancel() {
//                    dismissDialog();
//                }
//            }, getString(R.string.hwr_reco_fail) + "(" + error + ",mac=" + AFPenClientCtrl.getInstance().getLastTryConnectAddr() + ")");
//
//            if (error.contains("IO_FAILURE: error: AddResource")) {//识别资源出错，删除错误资源，改为识别语种为默认的英语，如需要再次进入资源界面下载
//                String lang = SpUtils.getString(HwrRecognizeActivity.this, Constant.SP_KEY_RECO_LANGUAGE, Constant.DEF_SHORT_NAME);
//                File file = new File(AppCommon.NQ_SAVE_SDCARD_PATH_ASSETS + "/conf", lang + ".conf");
//                FileUtils.delete(file);
//                File file1 = new File(AppCommon.NQ_SAVE_SDCARD_PATH_ASSETS + "/resources/" + lang);
//                FileUtils.delete(file1);
//
//                SpUtils.putString(HwrRecognizeActivity.this, Constant.SP_KEY_RECO_LANGUAGE, Constant.DEF_SHORT_NAME);
//                tranSrcLanguage = 9;
//                SpUtils.putInt(HwrRecognizeActivity.this, Constant.SP_KEY_LANGUAGE, tranSrcLanguage);
//            IInkSdkManager.getInstance().saveRecogn(AppCommon.getHwrFilePath(), "");
//            }
//
//        }
//    };

}
