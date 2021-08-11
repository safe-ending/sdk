package com.eningqu.aipen.qpen;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.blankj.utilcode.util.LogUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.common.utils.SpUtils;
import com.eningqu.aipen.qpen.bean.AFStroke;
import com.eningqu.aipen.qpen.bean.AFStrokeAndPaint;
import com.eningqu.aipen.sdk.bean.DotType;
import com.eningqu.aipen.sdk.bean.NQDot;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignatureView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    public final static String TAG = SignatureView.class.getSimpleName();

    private SurfaceHolder surfaceHolder;
    private Canvas canvas;
    //子线程绘制标记
    private volatile boolean isDrawing;
    //    private float mSignatureWidth = 2.5f/14f;
    private float mSignatureWidth = 2f;
    private Bitmap mSignature = null;
    private Bitmap.Config bmConfig = Bitmap.Config.ARGB_8888;

    private static final boolean GESTURE_RENDERING_ANTIALIAS = true;
    private static final boolean DITHER_FLAG = true;

    private Paint mPaint = new Paint();
    private Path mPath = new Path();
    /**
     * 画背景图笔刷
     */
    private Paint mBgPaint = new Paint();
    //当前笔画
    private AFStroke curStroke = new AFStroke();
    private AFStroke curStroke2 = new AFStroke();
    //笔画列表
    private List<AFStroke> strokes = new ArrayList<>();

    //当前笔画和颜色
    private AFStrokeAndPaint curStrokeAndPaint = new AFStrokeAndPaint();
    private AFStrokeAndPaint curStrokeAndPaint2 = new AFStrokeAndPaint();
    private List<AFStrokeAndPaint> strokeAndPaints = new ArrayList<>();
    private List<AFStrokeAndPaint> strokeAndPaints2 = new ArrayList<>();

    Matrix matrix = new Matrix();

    //背景线条左上角的坐标
    private PointF bgTopLeftPoint = new PointF(240, 480);
    //行距
    private float bgLineSpacing = 188f;
    //行宽度
    private float bgRightX = 3380.0f;
    //行数量
    private int bgLineRows = 22;
    //背景行的Y
    private float lineY;
    private NQDot lastDot;
    private float scaleY;

    @Override
    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public void clearPaint() {
        strokeAndPaints.clear();
        strokeAndPaints2.clear();
        invalidate();
    }

    public List<AFStrokeAndPaint> getStrokeAndPaints() {
        return this.strokeAndPaints;
    }

    public List<AFStrokeAndPaint> getStrokeAndPaints2() {
        return this.strokeAndPaints2;
    }

    private float hwRate = 0.0f;//页面宽高比
    private NQDot lastDot2;

    /**
     * 先添加已有的点
     *
     * @param dot
     */
    public void addDot(NQDot dot, boolean invalidateRightNow) {
        NQDot nqDot = SDKUtil.conversionADot(dot, getWidth(), getHeight());
        //        hwRate = (float) dot.book_height / (float) dot.book_width;
        //        NQDot nqDot = SDKUtil.conversionADot(dot, getWidth(), (int) (getWidth() * hwRate));

        if (lastDot2 != null) {
            double a = Math.pow(Math.abs(dot.x - lastDot2.x), 2);
            double b = Math.pow(Math.abs(dot.y - lastDot2.y), 2);
            double dist = Math.sqrt(a + b);
            //过滤飞点
            if (dist > 250 && lastDot2.page == dot.page) {
                LogUtils.e("dot exception dist=" + dist + ", lastDot2.x="
                        + lastDot2.x + ", lastDot2.y=" + lastDot2.y + ", current dot.x=" + dot.x + ", dot.y=" + dot.y);
                if (dot.type == DotType.PEN_ACTION_DOWN && lastDot2.type != DotType.PEN_ACTION_UP) {
                    lastDot2.type = DotType.PEN_ACTION_UP;
                    //缓存当前笔画的点到列表
                    curStroke.add(lastDot2);
                    //画贝塞尔曲线
                    curStroke.buildBezierPath(getWidth(), getHeight());
                    //缓存当前笔画的颜色粗细
                    curStrokeAndPaint.setPaint(mPaint);
                    L.error("mpaint color = " + mPaint.getColor());
                    //缓存当前笔画
                    curStrokeAndPaint.setAfStroke(curStroke);
                    //缓存当前笔画颜到列表
                    strokeAndPaints.add(curStrokeAndPaint);
                    curStrokeAndPaint = new AFStrokeAndPaint();
                    curStroke = new AFStroke();
                    if (invalidateRightNow) {
                        invalidate();
                    }
                } else {
                    return;
                }
            }

            if (lastDot2.page != dot.page) {
                dot.x = lastDot2.x;
                dot.y = lastDot2.y;
                dot.page = lastDot2.page;
                dot.type = DotType.PEN_ACTION_UP;
            }
            if (dot.type == DotType.PEN_ACTION_DOWN && lastDot2.type != DotType.PEN_ACTION_UP) {
                lastDot2.type = DotType.PEN_ACTION_UP;
                //缓存当前笔画的点到列表
                curStroke.add(lastDot2);
                //画贝塞尔曲线
                curStroke.buildBezierPath(getWidth(), getHeight());
                //缓存当前笔画的颜色粗细
                curStrokeAndPaint.setPaint(mPaint);
                L.error("mpaint color = " + mPaint.getColor());
                //缓存当前笔画
                curStrokeAndPaint.setAfStroke(curStroke);
                //缓存当前笔画颜到列表
                strokeAndPaints.add(curStrokeAndPaint);
                curStrokeAndPaint = new AFStrokeAndPaint();
                curStroke = new AFStroke();
                if (invalidateRightNow) {
                    invalidate();
                }
            }
        } else {
            //lastDot2 is null
            if (dot.type == DotType.PEN_ACTION_MOVE) {
                dot.type = DotType.PEN_ACTION_DOWN;
            }
        }


        lastDot2 = dot;
        //缓存当前笔画的点到列表
        curStroke.add(nqDot);
        //        curStroke.add(dot);
        //画贝塞尔曲线
        curStroke.buildBezierPath(getWidth(), getHeight());
        //        curStroke.buildBezierPath(getWidth(), (int) (getWidth() * hwRate));
        //        curStroke.buildBezierPath(dot.book_width, dot.book_height);
        //如果是笔画的终点
        if (dot.type == DotType.PEN_ACTION_UP) {
            lastDot2 = null;
            //缓存当前笔画的颜色粗细
            curStrokeAndPaint.setPaint(mPaint);
            //缓存当前笔画
            curStrokeAndPaint.setAfStroke(curStroke);
            //缓存当前笔画颜到列表
            strokeAndPaints.add(curStrokeAndPaint);
            curStrokeAndPaint = new AFStrokeAndPaint();
            curStroke = new AFStroke();
            if (!invalidateRightNow) {
                invalidate();
            }
        }
        if (invalidateRightNow) {
            invalidate();
        }
        //        L.info("draw invalidate curStroke = " + curStroke);
    }


    private final long NO_TIMESTAMP = -1;
    private final float NO_PRESSURE = 0.0f;
    int pointerId = 1;


    /**
     * 后添加的点
     *
     * @param dot
     */
    public void addDot2(NQDot dot, boolean invalidateRightNow) {
        L.error("on draw dot : type = " + dot.type + ":x = " + dot.x + ":y = " + dot.y);
        NQDot nqDot = SDKUtil.conversionADot(dot, getWidth(), getHeight());

        if (lastDot != null) {

            double a = Math.pow(Math.abs(dot.x - lastDot.x), 2);
            double b = Math.pow(Math.abs(dot.y - lastDot.y), 2);
            double dist = Math.sqrt(a + b);
            //过滤飞点
            if (dist > 250 && lastDot.page == dot.page) {
                LogUtils.e("dot exception dist=" + dist + ", lastDot.x="
                        + lastDot.x + ", lastDot.y=" + lastDot.y + ", current dot.x=" + dot.x + ", dot.y=" + dot.y);
                if (dot.type == DotType.PEN_ACTION_DOWN && lastDot.type != DotType.PEN_ACTION_UP) {
                    lastDot.type = DotType.PEN_ACTION_UP;
                    //缓存当前笔画的点到列表
                    curStroke2.add(lastDot);
                    //画贝塞尔曲线
                    curStroke2.buildBezierPath(getWidth(), getHeight());
                    //缓存当前笔画的颜色粗细
                    curStrokeAndPaint2.setPaint(mPaint);
                    L.error("mpaint color = " + mPaint.getColor());
                    //缓存当前笔画
                    curStrokeAndPaint2.setAfStroke(curStroke2);
                    //缓存当前笔画颜到列表
                    strokeAndPaints2.add(curStrokeAndPaint2);
                    curStrokeAndPaint2 = new AFStrokeAndPaint();
                    curStroke2 = new AFStroke();
                    pointerId++;
                    if (invalidateRightNow) {
                        invalidate();
                    }
                } else {
                    return;
                }
            }

            if (lastDot.page != dot.page) {
                dot.x = lastDot.x;
                dot.y = lastDot.y;
                dot.page = lastDot.page;
                dot.type = DotType.PEN_ACTION_UP;
            }
            if (dot.type == DotType.PEN_ACTION_DOWN && lastDot.type != DotType.PEN_ACTION_UP) {
                lastDot.type = DotType.PEN_ACTION_UP;
                //缓存当前笔画的点到列表
                curStroke2.add(lastDot);
                //画贝塞尔曲线
                curStroke2.buildBezierPath(getWidth(), getHeight());
                //缓存当前笔画的颜色粗细
                curStrokeAndPaint2.setPaint(mPaint);
                L.error("mpaint color = " + mPaint.getColor());
                //缓存当前笔画
                curStrokeAndPaint2.setAfStroke(curStroke2);
                //缓存当前笔画颜到列表
                strokeAndPaints2.add(curStrokeAndPaint2);
                curStrokeAndPaint2 = new AFStrokeAndPaint();
                curStroke2 = new AFStroke();
                pointerId++;
                if (invalidateRightNow) {
                    invalidate();
                }
            }
        } else {
            //lastDot is null
            if (dot.type == DotType.PEN_ACTION_MOVE) {
                dot.type = DotType.PEN_ACTION_DOWN;
            }
        }


        lastDot = dot;
        //缓存当前笔画的点到列表
        curStroke2.add(nqDot);
        //画贝塞尔曲线
        curStroke2.buildBezierPath(getWidth(), getHeight());
        //        curStroke.buildBezierPath(getWidth(), (int) (getWidth() * hwRate));
        //        curStroke.buildBezierPath(dot.book_width, dot.book_height);
        //如果是笔画的终点
        if (dot.type == DotType.PEN_ACTION_UP) {
            lastDot = null;
            //缓存当前笔画的颜色粗细
            curStrokeAndPaint2.setPaint(mPaint);
            L.error("mpaint color = " + mPaint.getColor());
            //缓存当前笔画
            curStrokeAndPaint2.setAfStroke(curStroke2);
            //缓存当前笔画颜到列表
            strokeAndPaints2.add(curStrokeAndPaint2);
            curStrokeAndPaint2 = new AFStrokeAndPaint();
            curStroke2 = new AFStroke();
            pointerId++;
            if (!invalidateRightNow) {
                invalidate();
            }
        }
        if (invalidateRightNow) {
            invalidate();
        }
    }

    /**
     * 后添加的点
     *
     * @param dot
     */
    public void addDot3(NQDot dot, boolean invalidateRightNow) {
        L.error("on draw dot : type = " + dot.type + ":x = " + dot.x + ":y = " + dot.y);
        NQDot nqDot = SDKUtil.conversionADot(dot, getWidth(), getHeight());
        //        hwRate = (float) dot.book_height / (float) dot.book_width;
        //        NQDot nqDot = SDKUtil.conversionADot(dot, getWidth(), (int) (getWidth() * hwRate));

        if (null != lastDot && lastDot.type == DotType.PEN_ACTION_UP &&
                dot.type == DotType.PEN_ACTION_UP) {
            lastDot = null;
            return;
        }
        try {
            if (lastDot != null) {
                if (Math.abs(lastDot.x - dot.x) > 500 || Math.abs(lastDot.y - dot.y) > 500) {
                    L.debug("测试", "math x = " + Math.abs(lastDot.x - dot.x) + ", y = " + Math.abs(lastDot.y - dot.y));
                    L.debug("测试1", "math x = " + lastDot.x + ", y = " + lastDot.y);
                    L.debug("测试2", "math x = " + dot.x + ", y = " + dot.y);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        lastDot = dot;
        //缓存当前笔画的点到列表
        curStroke2.add(nqDot);
        //画贝塞尔曲线
        curStroke2.buildBezierPath(getWidth(), getHeight());
        //        curStroke.buildBezierPath(getWidth(), (int) (getWidth() * hwRate));
        //        curStroke.buildBezierPath(dot.book_width, dot.book_height);
        //如果是笔画的终点
        if (dot.type == DotType.PEN_ACTION_UP) {
            lastDot = null;
            //缓存当前笔画的颜色粗细
            curStrokeAndPaint2.setPaint(mPaint);
            L.error("mpaint color = " + mPaint.getColor());
            //缓存当前笔画
            curStrokeAndPaint2.setAfStroke(curStroke2);
            //缓存当前笔画颜到列表
            strokeAndPaints2.add(curStrokeAndPaint2);
            curStrokeAndPaint2 = new AFStrokeAndPaint();
            curStroke2 = new AFStroke();
            pointerId++;
        }

        if (invalidateRightNow) {
            invalidate();
        }
    }

    public void clearLastDot() {
//       lastDot = null;
//       //一直存在飞笔是这里这个东西的列表没有清空历史点
        curStroke2 = new AFStroke();
    }

    public NQDot getStrokeLastDot() {
        if (curStroke2 != null && curStroke2.getDots().size() > 0) {
            return curStroke2.get(curStroke2.getDots().size() - 1);
        }
        return null;
    }

    /**
     * 添加笔画
     *
     * @param dots
     */
    public void addDots(List<NQDot> dots) {
        curStroke = new AFStroke();
        //        List<NQDot> list = new ArrayList<>();
        //        list.addAll(dots);
        if (null != dots && dots.size() > 0) {
            for (NQDot dot : dots) {
                curStroke.add(dot);
                curStroke.buildBezierPath(getWidth(), getHeight());
                //                curStroke.buildBezierPath(getWidth(), (int) (getWidth() * hwRate));
                if (dot.type == DotType.PEN_ACTION_UP) {
                    //缓存当前笔画
                    strokes.add(curStroke);
                    //缓存当前笔画的颜色粗细
                    curStrokeAndPaint.setPaint(mPaint);
                    //缓存当前笔画
                    curStrokeAndPaint.setAfStroke(curStroke);
                    //缓存当前笔画颜色
                    strokeAndPaints.add(curStrokeAndPaint);
                    curStrokeAndPaint = new AFStrokeAndPaint();
                    //新建笔画
                    curStroke = new AFStroke();
                }
            }
            invalidate();
        }
    }

    public void setAFStrokeAndPaint(List<AFStrokeAndPaint> stroke_paints) {
        strokeAndPaints = stroke_paints;
        invalidate();
    }

    public SignatureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void setSignatureBitmap(Bitmap signature) {
        mSignature = signature;
        invalidate();
    }

    public SignatureView(Context context) {
        super(context);
        init(context);
    }

    public SignatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);

        mPaint.setAntiAlias(GESTURE_RENDERING_ANTIALIAS);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mSignatureWidth);
        mPaint.setDither(DITHER_FLAG);
        mPath.reset();

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    /**
     * 设置颜色
     *
     * @param color
     */
    public synchronized void setPenColor(int color) {
        mPaint = new Paint(mPaint);
        mPaint.setColor(color);
    }

    /**
     * 设置线宽
     *
     * @param w
     */
    public synchronized void setPenSize(float w) {
        mPaint = new Paint(mPaint);
        mPaint.setStrokeWidth(w); //设置线宽
    }

    public Paint getPaint() {
        return mPaint;
    }

    //    @Override
    public void onMyDraw(Canvas canvas) {
        if (null == canvas) {
            return;
        }
        AFPenClientCtrl.getInstance().setDrawNow(true);
        canvas.setMatrix(matrix);
        canvas.drawColor(Color.WHITE);
        //设置背景图

        if (AppCommon.getCurrentNoteBookBG() == 1) {
            //画线
            canvas.drawColor(Color.WHITE);
            for (int i = 0; i < bgLineRows; i++) {
                lineY = bgTopLeftPoint.y + (i * bgLineSpacing) - i * scaleY;
                canvas.drawLine(bgTopLeftPoint.x, lineY,
                        bgRightX, lineY, mBgPaint);
//                    }
            }
        } else {
            if (mSignature != null) {
                canvas.drawBitmap(mSignature, null, new Rect(0, 0, getWidth(), getHeight()), null);
            }
        }

        // L.error(TAG, "strokeAndPaints size="+strokeAndPaints.size() + ", strokeAndPaints2 size="+strokeAndPaints2.size());
        //循环画笔画
        for (int i = 0; i < strokeAndPaints.size(); i++) {
            AFStrokeAndPaint afStrokeAndPaint = strokeAndPaints.get(i);
            AFStroke afStroke = afStrokeAndPaint.getAfStroke();
            if (null != afStroke && afStroke.fullPath != null) {
                canvas.drawPath(afStroke.fullPath, afStrokeAndPaint.getPaint());
            }
        }
        //画当前笔画
//        if (null != curStroke && curStroke.fullPath != null)
//            canvas.drawPath(curStroke.fullPath, mPaint);

        int count = canvas.saveLayerAlpha(0, 0, getWidth(), getHeight(), 255, Canvas.ALL_SAVE_FLAG);
        //循环画笔画
        for (int i = 0; i < strokeAndPaints2.size(); i++) {
            AFStrokeAndPaint afStrokeAndPaint = strokeAndPaints2.get(i);
            AFStroke afStroke = afStrokeAndPaint.getAfStroke();
            if (null != afStroke && afStroke.fullPath != null) {
                canvas.drawPath(afStroke.fullPath, afStrokeAndPaint.getPaint());
            }
        }
        if (null != curStroke2 && curStroke2.fullPath != null)
            canvas.drawPath(curStroke2.fullPath, mPaint);

        canvas.restoreToCount(count);
        AFPenClientCtrl.getInstance().setDrawNow(false);
    }

    public Bitmap getSignatureBitmap() {
        WeakReference<Bitmap> createBitmap;

        if (getWidth() < 1 || getHeight() < 1) {
            return null;
        }

        try {
            if (this.mSignature != null && !this.mSignature.isRecycled()) {
                createBitmap = new WeakReference<>(Bitmap.createBitmap(getWidth(), getHeight(), bmConfig));
                //            createBitmap = Bitmap.createBitmap(getWidth(), (int) (getWidth() * hwRate), bmConfig);
                Canvas canvas = new Canvas(createBitmap.get());
                if (AppCommon.getCurrentNoteBookBG() == 1) {
                    //画线
                    canvas.drawColor(Color.WHITE);
                    for (int i = 0; i < bgLineRows; i++) {
                        lineY = bgTopLeftPoint.y + (i * bgLineSpacing) - i * scaleY;
                        canvas.drawLine(bgTopLeftPoint.x, lineY,
                                bgRightX, lineY, mBgPaint);
                    }
                } else {
                    if(null!=mSignature){
                        canvas.drawBitmap(mSignature, null, new Rect(0, 0, getWidth(), getHeight()), null);
                    }
                }
                for (int i = 0; i < strokeAndPaints.size(); i++) {
                    AFStrokeAndPaint afStrokeAndPaint = strokeAndPaints.get(i);
                    AFStroke afStroke = afStrokeAndPaint.getAfStroke();
                    if (null != afStroke && afStroke.fullPath != null)
                        canvas.drawPath(afStroke.fullPath, afStrokeAndPaint.getPaint());
                }
                if (null != curStroke && curStroke.fullPath != null)
                    canvas.drawPath(curStroke.fullPath, mPaint);
                int count = canvas.saveLayerAlpha(0, 0, getWidth(), getHeight(), 255, Canvas.ALL_SAVE_FLAG);
                for (int i = 0; i < strokeAndPaints2.size(); i++) {
                    AFStrokeAndPaint afStrokeAndPaint = strokeAndPaints2.get(i);
                    AFStroke afStroke = afStrokeAndPaint.getAfStroke();
                    if (null != afStroke && afStroke.fullPath != null)
                        canvas.drawPath(afStroke.fullPath, afStrokeAndPaint.getPaint());
                }
                if (null != curStroke2 && curStroke2.fullPath != null)
                    canvas.drawPath(curStroke2.fullPath, mPaint);

                canvas.restoreToCount(count);
                return createBitmap.get();
            } else {
                if (strokeAndPaints.size() != 0 && null != curStroke && curStroke.fullPath != null) {
                    createBitmap = new WeakReference<>(Bitmap.createBitmap(getWidth(), getHeight(), bmConfig));
                    //                createBitmap = Bitmap.createBitmap(getWidth(), (int) (getWidth() * hwRate), bmConfig);
                    Canvas canvas = new Canvas(createBitmap.get());
                    for (int i = 0; i < strokeAndPaints.size(); i++) {
                        AFStrokeAndPaint afStrokeAndPaint = strokeAndPaints.get(i);
                        AFStroke afStroke = afStrokeAndPaint.getAfStroke();
                        if (null != afStroke && afStroke.fullPath != null)
                            canvas.drawPath(afStroke.fullPath, afStrokeAndPaint.getPaint());
                    }
                    int count = canvas.saveLayerAlpha(0, 0, getWidth(), getHeight(), 255, Canvas.ALL_SAVE_FLAG);
                    for (int i = 0; i < strokeAndPaints2.size(); i++) {
                        AFStrokeAndPaint afStrokeAndPaint = strokeAndPaints2.get(i);
                        AFStroke afStroke = afStrokeAndPaint.getAfStroke();
                        if (null != afStroke && afStroke.fullPath != null)
                            canvas.drawPath(afStroke.fullPath, afStrokeAndPaint.getPaint());
                    }
                    if (null != curStroke && curStroke.fullPath != null)
                        canvas.drawPath(curStroke.fullPath, mPaint);
                    canvas.restoreToCount(count);
                    return createBitmap.get();
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

    private IPageDrawView pageDrawView;

    public void setPageDrawView(IPageDrawView callback) {
        this.pageDrawView = callback;
    }

    //当SurfaceView被创建的时候被调用
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        L.info(TAG, "Signature surfaceCreated");
        isDrawing = true;
        singleThreadExecutor.execute(new Thread(this));

        mBgPaint.setStrokeWidth(2);
        mBgPaint.setColor(Color.LTGRAY);

        //以下参数修改可以适配背景线条的长度和行距
        bgTopLeftPoint.x = getWidth() * ((float) 230 / Const.PageFormat.PAGE_A5.getWidth());
        bgTopLeftPoint.y = getHeight() * ((float) 488 / Const.PageFormat.PAGE_A5.getHeight());
        bgLineSpacing = getHeight() * ((float) 189 / Const.PageFormat.PAGE_A5.getHeight());
//        scaleY = bgLineSpacing * 0.01f;
        if (isOffline) {
            scaleY = scaleY * 3.7f;
        }
        bgRightX = getWidth() * ((float) 3100 / Const.PageFormat.PAGE_A5.getWidth());
        if (null != pageDrawView) {
            pageDrawView.onCreated(holder);
        }

        //设置页面背景图
        if (mSignature == null) {
            switch (SpUtils.getInt(getContext(), "DrawBg", 1)) {
                case 1:
                    mSignature = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.page_bg);
                    break;
                case 3:
                    mSignature = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.page_bg1);
                    break;
                case 2:
                    mSignature = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.dot_main_bg);
                    break;
            }
        }
    }

    //当SurfaceView的视图发生改变，比如横竖屏切换时，这个方法被调用
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        L.info(TAG, "Signature surfaceChanged");
        if (null != pageDrawView) {
            pageDrawView.onChanged(holder, format, width, height);
        }
        draw();
    }

    //当SurfaceView被销毁的时候，比如不可见了，会被调用
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        L.info(TAG, "Signature surfaceDestroyed");
        synchronized (this) {
            isDrawing = false;
        }
        if (mSignature != null)
            mSignature.recycle();
        mSignature = null;
        strokeAndPaints.clear();
        strokeAndPaints2.clear();
        if (null != pageDrawView) {
            pageDrawView.onDestroyed(holder);
        }
    }

    @Override
    public void run() {
        while (isDrawing) {
            draw();

            try {
                Thread.sleep(80);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void setDrawing(boolean drawing) {
        this.isDrawing = drawing;
    }

    public void removeCallback() {
        surfaceHolder.removeCallback(this);
    }

    private void draw() {
        synchronized (this) {
            if (isDrawing) {
                try {
                    canvas = surfaceHolder.lockCanvas();
                    //执行具体的绘制操作
                    onMyDraw(canvas);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

    boolean isOffline = false;

    public void setOffline() {
        isOffline = true;
    }

}