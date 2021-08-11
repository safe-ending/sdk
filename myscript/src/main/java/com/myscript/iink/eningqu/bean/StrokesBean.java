package com.myscript.iink.eningqu.bean;

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
    private List<Point> dots = new ArrayList<>();

    public StrokesBean() {
    }

    public StrokesBean(long createTime, List<Point> dots) {
        this.createTime = createTime;
        this.dots = dots;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
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
