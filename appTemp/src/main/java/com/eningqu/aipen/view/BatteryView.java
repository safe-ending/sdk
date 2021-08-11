package com.eningqu.aipen.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.eningqu.aipen.R;

/**
 * 说明：电量
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/3/6 11:23
 */

public class BatteryView extends View {

    private int mWidth;
    private int mHeight;

    //电池内芯与边框的距离
    private int mSpace;
    //电池外框的宽带
    private int mBorderWidth;
    private int mBorderColor;

    private int mHeadWidth;
    private int mHeadHeight;

    private float mRadius;

    //最大值为1 表示满电量
    private float mPower;
    private int mPowerLowColor;
    private int mPowerMidColor;
    private int mPowerHighColor;

    private RectF mMainRect;
    private RectF mHeadRect;

    public BatteryView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BatteryView(Context context) {
        this(context, null);
    }

    public BatteryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //获取自定义属性的值
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomBattery, defStyleAttr, 0);

        mSpace = a.getInt(R.styleable.CustomBattery_mSpace, 6);
        mBorderWidth = a.getInt(R.styleable.CustomBattery_mBorderWidth, 8);
        mBorderColor = a.getColor(R.styleable.CustomBattery_mBorderColor, Color.BLACK);

        mHeadWidth = a.getInt(R.styleable.CustomBattery_mHeadWidth, 40);
        mHeadHeight = a.getInt(R.styleable.CustomBattery_mHeadHeight, 30);

        mRadius = a.getFloat(R.styleable.CustomBattery_mRadius, 6f);

        mPower = a.getFloat(R.styleable.CustomBattery_mPower, 0.2f);
        mPowerLowColor = a.getColor(R.styleable.CustomBattery_mPowerLowColor, Color.RED);
        mPowerMidColor = a.getColor(R.styleable.CustomBattery_mPowerMidColor, Color.YELLOW);
        mPowerHighColor = a.getColor(R.styleable.CustomBattery_mPowerHighColor, Color.GREEN);

        a.recycle();
    }

    private void init() {
        mWidth = getWidth();
        mHeight = getHeight();
        mHeadRect = new RectF((mWidth - mHeadWidth) / 2, 0, (mWidth + mHeadWidth) / 2, mHeadHeight);
        mMainRect = new RectF(0, mHeadHeight, mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        init();

        Paint paint = new Paint();

        //画电池头
        paint.setStyle(Paint.Style.FILL_AND_STROKE);  //实心
        paint.setStrokeWidth(mBorderWidth);
        paint.setColor(mBorderColor);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawRoundRect(mHeadRect, mRadius, mRadius, paint);

        //画外框
        paint.reset();
        paint.setStyle(Paint.Style.STROKE);    //设置空心矩形
        paint.setStrokeWidth(mBorderWidth);    //设置边框宽度
        paint.setColor(mBorderColor);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawRoundRect(mMainRect, mRadius, mRadius, paint);

        //画电池芯
        paint.reset();
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        if (mPower <= 0.3) {
            paint.setColor(mPowerLowColor);
        } else if (mPower > 0.3 && mPower <= 0.6) {
            paint.setColor(mPowerMidColor);
        } else {
            paint.setColor(mPowerHighColor);
        }

        int mainHeight = mHeight - mHeadHeight - mSpace * 2;
        int drawHeight = (int) (mainHeight * mPower);
        int left = (int) (mMainRect.left + mSpace);
        int top = (mainHeight - drawHeight + mHeadHeight + mSpace);
        int right = (int) (mMainRect.right - mSpace);
        int bottom = (int) (mMainRect.bottom - mSpace);

        Rect rect = new Rect(left, top, right, bottom);
        canvas.drawRect(rect, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    public void setPower(float power) {
        mPower = power;
        invalidate();
    }
}
