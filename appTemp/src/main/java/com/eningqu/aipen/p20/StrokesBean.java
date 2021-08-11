//package com.eningqu.aipen.p20;
//
//import android.graphics.Point;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * author : LiQiu
// * e-mail : lq@eningqu.com
// * date   : 2019/4/19 16:55
// * desc   : 笔画数据
// * version: 1.0
// */
//public class StrokesBean {
//    private long createTime;//笔画时间戳
//    private String uuid;//用户Id
//    private int color;//笔画颜色
//    private float size;//笔画粗细
//    private boolean hide;//是否隐藏，默认不隐藏
//    private List<Point> dots = new ArrayList<>();
//    private int page;//页码
//    private boolean clear = false;
//    private long timestamp ;
//
//    public long getCreateTime() {
//        return createTime;
//    }
//
//    public void setCreateTime(long createTime) {
//        this.createTime = createTime;
//    }
//
//    public String getUuid() {
//        return uuid;
//    }
//
//    public void setUuid(String uuid) {
//        this.uuid = uuid;
//    }
//
//    public int getColor() {
//        return color;
//    }
//
//    public void setColor(int color) {
//        this.color = color;
//    }
//
//    public float getSize() {
//        return size;
//    }
//
//    public void setSize(float size) {
//        this.size = size;
//    }
//
//    public boolean isHide() {
//        return hide;
//    }
//
//    public void setHide(boolean hide) {
//        this.hide = hide;
//    }
//
//    public List<Point> getDots() {
//        return dots;
//    }
//
//    public void setDots(List<Point> dots) {
//        this.dots = dots;
//    }
//
//    public void addDot(Point pt){
//        dots.add(pt);
//    }
//
//    public void addDot(int x, int y){
//        dots.add(new Point(x, y));
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
//    public boolean isClear() {
//        return clear;
//    }
//
//    public void setClear(boolean clear) {
//        this.clear = clear;
//    }
//
//    public long getTimestamp() {
//        return timestamp;
//    }
//
//    public void setTimestamp(long timestamp) {
//        this.timestamp = timestamp;
//    }
//}
