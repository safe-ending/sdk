package com.nq.edusaas.hps.sdkcore.nqpensdk.bean;

import android.graphics.Paint;
import android.graphics.Path;

import com.eningqu.aipen.sdk.bean.DotType;
import com.eningqu.aipen.sdk.bean.NQDot;

import java.util.ArrayList;

/**
 * @Author: Qiu.Li
 * @Create Date: 2020/6/2 19:55
 * @Description: 笔画
 * @Email: liqiupost@163.com
 */
public class NQStroke {
    private ArrayList<NQDot> dots = null;
    public Paint paint = null;

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

    public NQStroke() {
        this.dots = new ArrayList<NQDot>();
        pts = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            pts.add(new CGPoint(0, 0));
        }
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

    public static CGPoint BLEPoint2Point(NQDot dot, int refW, int refH) {
//        float pw = 3307;
//        float ph = 4843;
//        return new CGPoint(pX >= pw ? pw : pX * (refW / pw), pY >= ph ? ph : pY * (refH / ph));

        int x = dot.x;
        int y = dot.y;

        int pageHeight = dot.book_height;
        int pageWidth = dot.book_width;
        int page = dot.page;

        //计算画布对宽高度
        float xRatio = (float) ((float) (refW) / (float) pageWidth);
//        float yRatio = (float) ((float) (mCanvasHeight) / (float) pageHeight);
        float pageWHRatio = (float) dot.book_width / dot.book_height;
        int canvasHeight = (int) ((float) refW / pageWHRatio);

        float yRatio = (float) ((float) (canvasHeight) / (float) pageHeight);

        //换算成屏幕对坐标
        x = (int) ((float) x * xRatio);
        y = (int) ((float) y * yRatio);

        return new CGPoint(x, y);
    }

    public void buildBezierPath(int frameW, int frameH, int fromIdx, int toIdx) {
        try {
            if (frameW != 0 && frameH != 0 && getDots() != null && getDots().size() > 0) {

                if (this.fullPath == null) {
                    this.fullPath = new Path();//[UIBezierPath bezierPath];
                    ctr = 0;
                }
                if (true) {
                    if (this.dots.size() < 4 && this.dots.get(this.dots.size() - 1).type == DotType.PEN_ACTION_UP) {
                        //如果少于4个点，且最后一个点是抬笔点
                        NQDot penPoint = getDots().get(0);
                        CGPoint p = BLEPoint2Point(penPoint, frameW, frameH);
                        //先移动到第一个点的位置
                        this.fullPath.moveTo(p.x, p.y);
                        this.fullPath.lineTo(p.x, p.y);
                        //依次连线余下各点，没有使用贝塞尔曲线
                        /*for (int i = 1; i < this.getDots().size(); i++) {
                            penPoint = getDots().get(i);
                            p = BLEPoint2Point(penPoint, frameW, frameH);
                            this.fullPath.lineTo(p.x, p.y);
                        }*/
                        return;
                    } else {
                        //不少于4个点时
                        for (int i = fromIdx; i < toIdx; i++) {
                            NQDot penPoint = getDots().get(i);
//                    RLMPenAction *penPoint=self.points[i];
//                    CGPoint p = [GlobalUtil BLEPoint2Point:CGPointMake(penPoint.sX, penPoint.sY) refSize:frameSize];
                            CGPoint p = BLEPoint2Point(penPoint, frameW, frameH);
                            if (i == 0) {
                                pts.set(0, p);
                                continue;
                            }
                            ctr++;

                            pts.set(ctr, p);
                            if (ctr == 3) {
                                pts.set(2, new CGPoint((float) ((pts.get(1).x + pts.get(3).x) / 2.0), (float) ((pts.get(1).y + pts.get(3).y) / 2.0)));
                                this.fullPath.moveTo(pts.get(0).x, pts.get(0).y);
                                this.fullPath.quadTo(pts.get(1).x, pts.get(1).y, pts.get(2).x, pts.get(2).y);
                                pts.set(0, pts.get(2));
                                pts.set(1, pts.get(3));
                                ctr = 1;
                            }
                            /*if (ctr == 3) {
                                pts.set(2, new CGPoint((float) ((pts.get(1).x + pts.get(3).x) / 2.0), (float) ((pts.get(1).y + pts.get(3).y) / 2.0)));
                                this.fullPath.moveTo(pts.get(0).x, pts.get(0).y);
                                CGPoint c1 = new CGPoint((pts.get(0).x + pts.get(2).x) / 2, pts.get(0).y);
                                CGPoint c2 = new CGPoint((pts.get(0).x + pts.get(2).x) / 2, pts.get(2).y);
                                this.fullPath.cubicTo(c1.x, c1.y, c2.x, c2.y, pts.get(2).x, pts.get(2).y);

                                pts.set(0, pts.get(2));
                                pts.set(1, pts.get(3));
                                ctr = 1;
                            }*/

                            if ((i == (toIdx - 1) && getDots().get(i).type == DotType.PEN_ACTION_UP) && (ctr > 0) && (ctr < 3)) {

                                CGPoint ctr1;
                                CGPoint ctr2;

                                if (ctr == 1)
                                    this.fullPath.lineTo(pts.get(ctr).x, pts.get(ctr).y);
                                else {
                                    ctr1 = ctr2 = pts.get(ctr - 1);
                                    this.fullPath.cubicTo(ctr1.x, ctr1.y, ctr2.x, ctr2.y, pts.get(ctr).x, pts.get(ctr).y);
                                }
                            }
                        }
                    }
                } else {
                    for (int i = fromIdx; i < toIdx; i++) {
                        NQDot penPoint = getDots().get(i);
                        CGPoint p = BLEPoint2Point(penPoint, frameW, frameH);

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

    public void buildBezierPath(int frameW, int frameH, Paint paint) {
        buildBezierPath(frameW, frameH, this.lastDrawIdx, dots.size());
        this.paint = paint;
    }
}
