package com.eningqu.aipen.qpen.bean;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/4/19 16:55
 * desc   : 笔画数据
 * version: 1.0
 */
public class StrokesBean {
    private long createTime;//笔画时间戳
    private int color;//笔画颜色
    private int sizeLevel;//笔画粗细
    private boolean hide;//是否隐藏，默认不隐藏
    private List<Point> dots = new ArrayList<>();

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getSizeLevel() {
        return sizeLevel;
    }

    public void setSizeLevel(int sizeLevel) {
        this.sizeLevel = sizeLevel;
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public List<Point> getDots() {
        return dots;
    }

    public void setDots(List<Point> dots) {
        this.dots = dots;
    }

    public void addDot(Point pt){
        dots.add(pt);
    }

    public void addDot(int x, int y){
        dots.add(new Point(x, y));
    }
}
