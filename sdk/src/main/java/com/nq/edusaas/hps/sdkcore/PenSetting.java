package com.nq.edusaas.hps.sdkcore;//package com.nq.edusaas.hps.sdkcore;
//
//import android.graphics.Color;
//import android.graphics.Paint;
//
//import com.blankj.utilcode.util.ScreenUtils;
//import com.nq.edusaas.hps.model.command.CommandSize;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
//* @Author: Qiu.Li
//* @Create Date: 2021/1/9 10:30
//* @Description: 笔属性设置,包括类型、粗细、颜色
//* @Email: liqiupost@163.com
//*/
//public class PenSetting {
//
//    public static final String TAG = "PenSetting";
//    private static PenSetting sInstance;
//
//    static class PenPaintBean{
//        public Paint mPaint;
//        public float mPaintSizeCache;
//        public int mPaintColorCache;
//        public int mCurrentPenSizeType = CommandSize.PEN_SIZE_THIN;
//        public int mCurrentCleanSizeType = CommandSize.PEN_SIZE_THIN;
//        public boolean isClean = false;
//    }
//
//    Map<String, PenPaintBean> userPaintMap = new HashMap<>();
//    private Paint mPaint;
////    private float mPaintSizeCache;
////    private int mPaintColorCache;
//    /**
//     * 默认的笔大小规格
//     */
////    private int mCurrentPenSizeType = CommandSize.PEN_SIZE_THIN;
////    private int mCurrentCleanSizeType = CommandSize.PEN_SIZE_THIN;
//
//    private boolean isClean = false;
//
//    public static PenSetting getInstance() {
//        if (null == sInstance) {
//            synchronized (PenSetting.class) {
//                if (null == sInstance) {
//                    sInstance = new PenSetting();
//                }
//            }
//        }
//        return sInstance;
//    }
//
//    public PenSetting(){
//    }
//
//    private PenPaintBean createPaint(String uuid) {
//        //判断是否已经存在
//        boolean exist = userPaintMap.containsKey(uuid);
//        if(!exist){
//            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
//            float density = ScreenUtils.getScreenDensity();
//            if (density < 2.0f) {
//                mPaint.setStrokeWidth(0.8f);
//            } else if (density < 3.0f) {
//                mPaint.setStrokeWidth(1.2f);
//            } else if (density < 4.0f) {
//                mPaint.setStrokeWidth(1.6f);
//            } else {
//            }
//            mPaint.setStrokeWidth(2.0f);
//            mPaint.setAlpha(220);
//            mPaint.setColor(Color.parseColor("#000000"));
//            mPaint.setStyle(Paint.Style.STROKE);
//            mPaint.setStrokeJoin(Paint.Join.ROUND);
//            mPaint.setStrokeCap(Paint.Cap.ROUND);
//            mPaint.setAntiAlias(true);
//            mPaint.setDither(true);
//            PenPaintBean penPaintBean = new PenPaintBean();
//            penPaintBean.mPaint = mPaint;
//            userPaintMap.put(uuid, penPaintBean);
//        }
//        PenPaintBean penPaintBean = userPaintMap.get(uuid);
//        mPaint = penPaintBean.mPaint;
//        return penPaintBean;
//    }
//
//
//    public Paint getPaint(String uuid) {
//        createPaint(uuid);
//        return mPaint;
//    }
//
//    public void setPaintSize(String uuid, float size) {
//        createPaint(uuid);
//        if (mPaint != null) {
//            mPaint.setStrokeWidth(size);
//        }
//        createPaint(uuid).mPaintSizeCache = size;
//    }
//
//    public float getPaintSize(String uuid) {
//        createPaint(uuid);
//        if (mPaint == null) {
//            return 2.0f;
//        }
//        return mPaint.getStrokeWidth();
//    }
//
//    public float getPaintSizeCache(String uuid) {
//        createPaint(uuid);
//        return createPaint(uuid).mPaintSizeCache;
//    }
//
//    /**
//     * 设置画笔颜色
//     */
//    public void setPaintColor(String uuid, int colorId) {
//        createPaint(uuid);
//        if (mPaint != null) {
//            mPaint.setColor(colorId);
//        }
//        createPaint(uuid). mPaintColorCache = colorId;
//    }
//
//    /**
//     * 获取画笔颜色
//     */
//    public int getPaintColor(String uuid) {
//        createPaint(uuid);
//        if (mPaint != null) {
//            return mPaint.getColor();
//        }
//        return Color.BLACK;
//    }
//
//    public int getPaintColorCache(String uuid) {
//        createPaint(uuid);
//        return createPaint(uuid).mPaintColorCache;
//    }
//
//    public int getPenSizeType(String uuid) {
//        createPaint(uuid);
//        return createPaint(uuid).mCurrentPenSizeType;
//    }
//
//    public void setPenSizeType(String uuid, int penSizeType) {
//        createPaint(uuid);
//        createPaint(uuid).mCurrentPenSizeType = penSizeType;
//    }
//
//    public int getCurrentCleanSizeType(String uuid) {
//        createPaint(uuid);
//        return createPaint(uuid).mCurrentCleanSizeType;
//    }
//
//    public void setCurrentCleanSizeType(String uuid, int mCurrentCleanSizeType) {
//        createPaint(uuid);
//        createPaint(uuid).mCurrentCleanSizeType = mCurrentCleanSizeType;
//    }
//
//    public boolean isClean(String uuid) {
//        createPaint(uuid);
//        return createPaint(uuid).isClean;
//    }
//
//    public void setClean(String uuid, boolean clean) {
//        createPaint(uuid);
//        createPaint(uuid).isClean = clean;
//    }
//}
