package com.eningqu.aipen.qpen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class CanvasFrame extends LinearLayout {
    public LinearLayout layout;

    public SignatureView bDrawl;

    Matrix matrix = new Matrix();

    public CanvasFrame(Context context) {
        super(context);
        addWordView();

      /* WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);*/

        // setLayoutParams(new LayoutParams(metrics.widthPixels, (int)(metrics.widthPixels*1.414)));
        setWillNotDraw(false);
    }

    public CanvasFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
        addWordView();
        setWillNotDraw(false);
    }

    public CanvasFrame(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addWordView();
        setWillNotDraw(false);
    }

    private void addWordView() {
        bDrawl = new SignatureView(getContext());
//        bDrawl.setZOrderOnTop(true);
        this.addView(bDrawl);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //        canvas.concat(matrix);
        setVisibility(INVISIBLE);
        this.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        canvas.concat(matrix);
        this.setLayerType(View.LAYER_TYPE_NONE, null);
        setVisibility(VISIBLE);
    }

    @Override
    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                postInvalidate();
            }
        });
    }

    public void setCanvasBackground(Context context, String str) {
        context.getExternalFilesDir(null).getAbsolutePath();
        Bitmap readBitMap = readBitMap(context, str);
        Log.e("加载", str);
        this.layout.setBackground(new BitmapDrawable(readBitMap));
    }

    public static Bitmap readBitMap(Context context, String str) {
        InputStream fileInputStream;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        options.inPurgeable = true;
        options.inInputShareable = true;
        try {
            fileInputStream = new FileInputStream(str);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fileInputStream = null;
        }
        return BitmapFactory.decodeStream(fileInputStream, null, options);
    }
}
