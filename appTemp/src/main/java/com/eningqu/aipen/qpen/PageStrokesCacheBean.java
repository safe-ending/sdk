//package com.eningqu.aipen.qpen;
//
//import android.graphics.Point;
//
//import com.eningqu.aipen.p20.StrokesBean;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
///**
// * author : LiQiu
// * e-mail : lq@eningqu.com
// * date   : 2019/4/19 16:52
// * desc   : 页面数据
// * version: 1.0
// */
//public class PageStrokesCacheBean {
//    private String userId;
//    private String notebookId;
//    private int page;
//    private int ver;
//    private String bg;
//    private List<StrokesBean> strokesBeans = Collections.synchronizedList(new ArrayList<StrokesBean>());//笔画列表
//    private volatile StrokesBean mStrokesBean;
//
//    public PageStrokesCacheBean() {
//    }
//
//    public PageStrokesCacheBean(String userId, String notebookId, int page, int ver, String bg) {
//        this.userId = userId;
//        this.notebookId = notebookId;
//        this.page = page;
//        this.ver = ver;
//        this.bg = bg;
//    }
//
//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
//
//    public String getNotebookId() {
//        return notebookId;
//    }
//
//    public void setNotebookId(String notebookId) {
//        this.notebookId = notebookId;
//    }
//
//    public int getPage() {
//        return page;
//    }
//
//    public void setPage(int page) {
//        this.page = page;
//    }
//
//    public int getVer() {
//        return ver;
//    }
//
//    public void setVer(int ver) {
//        this.ver = ver;
//    }
//
//    public String getBg() {
//        return bg;
//    }
//
//    public void setBg(String bg) {
//        this.bg = bg;
//    }
//
//    public List<StrokesBean> getStrokesBeans() {
//        return strokesBeans;
//    }
//
//    public void setStrokesBeans(List<StrokesBean> strokesBeans) {
//        this.strokesBeans = strokesBeans;
//    }
//
//    public void addStrokes(StrokesBean strokesBean) {
//        this.strokesBeans.add(strokesBean);
//    }
//
//    /**
//     * 添加笔画
//     *
//     * @param pt    坐标点
//     * @param type  点类型 1落笔和写，2起笔
//     * @param size  粗细
//     * @param color 颜色
//     */
//    public synchronized void addStrokes(Point pt, int type, float size, int color) {
//        if (null == mStrokesBean) {
//            mStrokesBean = new StrokesBean();
//        }
//        if (type == 1) {
//            //落笔和写
//            mStrokesBean.addDot(pt);
//            mStrokesBean.setSize(size);
//            mStrokesBean.setCreateTime(System.currentTimeMillis());
//            mStrokesBean.setColor(color);
//        } else if (type == 2) {
//            //起笔，添加一笔画
//            mStrokesBean.addDot(pt);
//            mStrokesBean.setSize(size);
//            mStrokesBean.setCreateTime(System.currentTimeMillis());
//            mStrokesBean.setColor(color);
//            this.strokesBeans.add(mStrokesBean);
//            //创建新的笔画
//            mStrokesBean = new StrokesBean();
//        }
////        L.info("save dot type = " + type + " : size = " + strokesBeans.size() + " : mStrokesBean = " + mStrokesBean);
//    }
//}
