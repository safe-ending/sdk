package com.eningqu.aipen.qpen.bean;

import android.graphics.Path;

import com.eningqu.aipen.qpen.SDKUtil;
import com.eningqu.aipen.sdk.bean.DotType;
import com.eningqu.aipen.sdk.bean.NQDot;

import java.util.ArrayList;


public class AFStroke {
    private ArrayList<NQDot> dots = null;

    public static class CGPoint {
        public float x;
        public float y;

        public CGPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }


    public int lastDrawIdx;
    public Path fullPath;
    ArrayList<CGPoint> pts;
    int ctr;

    public AFStroke() {
        this.dots = new ArrayList<NQDot>();
        pts = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            pts.add(new CGPoint(0, 0));
        }
    }

    public void clearDot() {
        this.dots.clear();
    }

    public boolean add(NQDot dot) {
        this.dots.add(dot);
        return true;
    }

    public NQDot get(int index) {
        return dots.get(index);
    }

    public ArrayList<NQDot> getDots() {
        return dots;
    }

    public static CGPoint BLEPoint2Point(int pX, int pY, int refW, int refH) {
        float pw = SDKUtil.PAGER_WIDTH_A5;
        float ph = SDKUtil.PAGER_HEIGHT_A5;
        return new CGPoint(pX >= pw ? pw : pX * ((float) refW / pw), pY >= ph ? ph : pY * ((float) refH / ph));
//	    return new CGPoint(pX*(refW/(float) DPenCtrl.getInstance().getAFPenConnSetting().paperW), pY*(refH/(float)DPenCtrl.getInstance().getAFPenConnSetting().paperH));
//        return new CGPoint(pX*(refW/(float)69440), pY*(refH/(float)98560));

//        return new CGPoint(pX*(refW/(float)4960), pY*(refH/(float)7040));
    }

    NQDot lastDot = new NQDot();

    public void buildBezierPath(int frameW, int frameH, int fromIdx, int toIdx) {
        try {
            if (frameW != 0 && frameH != 0 && getDots() != null && getDots().size() > 0) {
                if (this.fullPath == null) {
                    this.fullPath = new Path();//[UIBezierPath bezierPath];
                    ctr = 0;
                }
                if (true) {
                    if (this.dots.size() < 4 && this.dots.get(this.dots.size() - 1).type == DotType.PEN_ACTION_UP) {
                        // 当前笔画末位点是抬笔的点，且数量少于4个点
                        NQDot penPoint = getDots().get(0);
                        CGPoint p = BLEPoint2Point(penPoint.x, penPoint.y, frameW, frameH);
                        fullPath.moveTo(p.x, p.y);

                        for (int i = 1; i < this.getDots().size(); i++) {
                            penPoint = getDots().get(i);
                            p = BLEPoint2Point(penPoint.x, penPoint.y, frameW, frameH);
                            this.fullPath.lineTo(p.x, p.y);
//                            L.error("1 lineTo x="+p.x + " y="+p.y);
                        }
                        return;
                    } else {
                        for (int i = fromIdx; i < toIdx; i++) {
                            //从当前笔画的点列表中取点
                            NQDot penPoint = getDots().get(i);
//                          RLMPenAction *penPoint=self.points[i];
//                          CGPoint p = [GlobalUtil BLEPoint2Point:CGPointMake(penPoint.sX, penPoint.sY) refSize:frameSize];
                            CGPoint p = BLEPoint2Point(penPoint.x, penPoint.y, frameW, frameH);
                            //过滤重复点
                            float xd = Math.abs(penPoint.x - lastDot.x);
                            float yd = Math.abs(penPoint.y - lastDot.y);
                            double dis = Math.sqrt(xd * xd + yd * yd);
                            if (dots.size() > 4 && lastDot.type == DotType.PEN_ACTION_MOVE && penPoint.type == DotType.PEN_ACTION_MOVE
                                    && ((lastDot.x == penPoint.x && lastDot.y == penPoint.y) ||
                                    (lastDot.x == penPoint.x && Math.abs(lastDot.y - penPoint.y) < 5) ||
                                    (lastDot.y == penPoint.y && Math.abs(lastDot.x - penPoint.x) < 5) || dis < 2)) {
                                continue;
                            }
                            lastDot.type = penPoint.type;
                            lastDot.x = penPoint.x;
                            lastDot.y = penPoint.y;

                            if (i == 0) {
                                //笔画的第一个点
                                pts.set(0, p);
                                continue;
                            }
                            ctr++;

                            pts.set(ctr, p);
                            if (ctr == 3) {//当前笔画第四个点

                                //第三个点去第二个点和第四个点的平均值
                                pts.set(2, new CGPoint((float) ((pts.get(1).x + pts.get(3).x) / 2.0), (float) ((pts.get(1).y + pts.get(3).y) / 2.0)));
                                //路径起点移到第一个点
                                this.fullPath.moveTo(pts.get(0).x, pts.get(0).y);
/*                                if (penPoint.type == DotType.PEN_ACTION_UP) {
                                    L.error("@@ lineTo x0=" + pts.get(0).x + " y0=" + pts.get(0).y +
                                            "x3=" + pts.get(3).x + " y3=" + pts.get(3).y);
                                }*/
                                //画贝塞尔曲线
                                this.fullPath.quadTo(pts.get(1).x, pts.get(1).y, pts.get(2).x, pts.get(2).y);
                                //为后面一个点画曲线做准备，从第三个点向前移一个点
                                pts.set(0, pts.get(2));
                                pts.set(1, pts.get(3));
                                ctr = 1;
                            }

                            if ((i == (toIdx - 1) && getDots().get(i).type == DotType.PEN_ACTION_UP) && (ctr > 0) && (ctr < 3)) {

                                CGPoint ctr1;
                                CGPoint ctr2;

                                if (ctr == 1) {
                                    this.fullPath.lineTo(pts.get(ctr).x, pts.get(ctr).y);
                                } else {
//                                    ctr1 = ctr2 = pts.get(ctr - 1);
//                                    this.fullPath.cubicTo(ctr1.x, ctr1.y, ctr2.x, ctr2.y, pts.get(ctr).x, pts.get(ctr).y);

                                    fullPath.setLastPoint(pts.get(ctr - 1).x, pts.get(ctr - 1).y);
                                    this.fullPath.lineTo(pts.get(ctr).x, pts.get(ctr).y);
                                }
                            }
                        }
                    }
                } else {
                    for (int i = fromIdx; i < toIdx; i++) {
                        NQDot penPoint = getDots().get(i);
                        CGPoint p = BLEPoint2Point(penPoint.x, penPoint.y, frameW, frameH);

                        if (i == 0)
                            fullPath.moveTo(p.x, p.y);
                        else
                            this.fullPath.lineTo(p.x, p.y);
                    }
                }
                this.lastDrawIdx = toIdx;
            }
//        return self;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buildBezierPath(int frameW, int frameH) {
        buildBezierPath(frameW, frameH, this.lastDrawIdx, dots.size());
    }
}
