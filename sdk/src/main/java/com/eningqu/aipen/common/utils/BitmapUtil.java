package com.eningqu.aipen.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.view.View;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.Utils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.activity.MainActivity;
import com.eningqu.aipen.common.AppCommon;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/2/5 19:48
 */

public class BitmapUtil {

    private final static String TAG = BitmapUtil.class.getSimpleName();

    /***
     * 创建一个bitmap
     * @return
     */
    public static Bitmap createBitmap() {
        Bitmap createBitmap = Bitmap.createBitmap(Math.round(ScreenUtils.getScreenWidth()), Math.round(ScreenUtils.getScreenHeight()),
                Bitmap.Config.RGB_565);
        Bitmap bgBitmap = null;
        if (MainActivity.isBuss){
            bgBitmap =BitmapFactory.decodeResource(Utils.getApp().getResources(), R.drawable.page_bg1);
        }else {
            bgBitmap =BitmapFactory.decodeResource(Utils.getApp().getResources(), R.drawable.page_bg1);
        }
//        Bitmap bgBitmap = BitmapFactory.decodeResource(Utils.getApp().getResources(), R.drawable.page_bg);
        /**创建带有位图imageBitmap的画布*/
        Canvas canvas = new Canvas(createBitmap);
        canvas.drawColor(Color.WHITE);
        /**创建画笔*/
        Paint paint = new Paint();
        /**去锯齿*/
        paint.setAntiAlias(true);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bgBitmap, ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight(), true);
        canvas.drawBitmap(scaledBitmap, 0, 0, null);
        return createBitmap;
    }

    /**
     * 将布局转化为bitmap
     * 这里传入的是你要截的布局的根View
     *
     * @param mView
     * @return
     */
    public static Bitmap getBitmapByView(View mView) {
        Bitmap bitmap = Bitmap.createBitmap(mView.getWidth(), mView.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        mView.draw(canvas);
        return bitmap;
    }

    /**
     * Bitmap convert to Drawable
     * @param context
     * @param bitmap
     * @return
     */
    public static Drawable bitmap2Drawable(Context context, Bitmap bitmap) {
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    /**
     * bitmap转jpg
     * @param bitmap
     * @param path
     * @param i
     * @return
     */
    public static File bitmap2File(Bitmap bitmap, String path, int i) {
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        BufferedOutputStream bos = null;
        try {
            out = new FileOutputStream(f);
            bos = new BufferedOutputStream(out);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(bos != null){
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    /*        if (i!=1) {
                if (AppCommon.getCurrentBitmap() != null && !AppCommon.getCurrentBitmap().isRecycled()) {
                    AppCommon.getCurrentBitmap().recycle();
                    AppCommon.setCurrentBitmap(null);
                    L.error("com.eningqu.aipen");
                }
                System.gc();
            }*/
            return null;
        }

    }

    /**
     * file转bitmap
     * @param path
     * @return
     */
    public static Bitmap file2Bitmap(String path){
        Bitmap bitmapTemp = BitmapFactory.decodeFile(path);
        if(bitmapTemp == null){
            return null;
        }
        /**创建一个和原始图片一样大小位图*/
        Bitmap imageBitmap = Bitmap.createBitmap(bitmapTemp.getWidth(),
                bitmapTemp.getHeight(), bitmapTemp.getConfig());
        /**创建带有位图imageBitmap的画布*/
        Canvas canvas = new Canvas(imageBitmap);
        canvas.drawColor(Color.WHITE);
        /**创建画笔*/
        Paint paint = new Paint();
        /**创建一个和原始图片一样大小的矩形*/
        Rect rect = new Rect(0, 0, bitmapTemp.getWidth(), bitmapTemp.getHeight());
        RectF rectF = new RectF(rect);
        /**去锯齿*/
        paint.setAntiAlias(true);
        /**把图片画到矩形去*/
        canvas.drawBitmap(bitmapTemp, rect, rectF, paint);
        return imageBitmap;
    }

    /**
     * @param data
     * @return
     */
    public static Bitmap bytes2Bitmap(byte[] data) {


        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;

        opt.inInputShareable = true;
        InputStream input = new ByteArrayInputStream(data);
        SoftReference softRef = new SoftReference(BitmapFactory.decodeStream(input, null, opt));
        Bitmap bitmapTemp  = (Bitmap)softRef.get();;


//        Bitmap bitmapTemp = BitmapFactory.decodeByteArray(data, 0, data.length);
        /**创建一个和原始图片一样大小位图*/
        Bitmap imageBitmap = Bitmap.createBitmap(bitmapTemp.getWidth(),
                bitmapTemp.getHeight(), bitmapTemp.getConfig());
        /**创建带有位图imageBitmap的画布*/
        Canvas canvas = new Canvas(imageBitmap);
        /**创建画笔*/
        Paint paint = new Paint();
        /**创建一个和原始图片一样大小的矩形*/
        Rect rect = new Rect(0, 0, bitmapTemp.getWidth(), bitmapTemp.getHeight());
        RectF rectF = new RectF(rect);
        /**去锯齿*/
        paint.setAntiAlias(true);
        /**把图片画到矩形去*/
        canvas.drawBitmap(bitmapTemp, rect, rectF, paint);
        if (!bitmapTemp.isRecycled()){
            bitmapTemp.recycle();
            bitmapTemp = null;
            System.gc();
        }
        return imageBitmap;
    }


    /**
     * bitmap转byte[]
     * @param bitmap
     * @return
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = null;
        byte[] data = null;
        try {
            if (bitmap != null) {
                stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
                data = stream.toByteArray();
                stream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }


    /**
     * 获取缩略图
     *
     * @param data:二进制数据
     * @param width:缩略图宽度
     * @param height:缩略图高度
     * @return
     */
    public static Bitmap getImageThumbnail(byte[] data, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap tmpBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        int h = options.outHeight;
        int w = options.outWidth;
        int scaleWidth = w / width;
        int scaleHeight = h / height;
        int scale = 1;
        if (scaleWidth < scaleHeight) {
            scale = scaleWidth;
        } else {
            scale = scaleHeight;
        }
        if (scale <= 0) {//判断缩放比是否符合条件
            scale = 1;
        }
        options.inSampleSize = scale;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把inJustDecodeBounds 设为 false
        options.inJustDecodeBounds = false;
        tmpBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        tmpBitmap = ThumbnailUtils.extractThumbnail(tmpBitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return tmpBitmap;
    }

    /**
     * 获取缩略图
     *
     * @param data:二进制数据
     * @param rate:缩略比例
     * @return
     */
    public static Bitmap getImageThumbnail(byte[] data, int rate) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap tmpBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        int h = options.outHeight;
        int w = options.outWidth;

        int rw = (int)(w * ((float)rate/100));
        int scaleWidth = w/rw;
        int rh = (int)(((float)h/w)*rw);
        int scaleHeight = h/rh;

        int scale = 1;
        if (scaleWidth < scaleHeight) {
            scale = scaleWidth;
        } else {
            scale = scaleHeight;
        }
        if (scale <= 0) {//判断缩放比是否符合条件
            scale = 1;
        }
        options.inSampleSize = scale;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把inJustDecodeBounds 设为 false
        options.inJustDecodeBounds = false;
        tmpBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        tmpBitmap = ThumbnailUtils.extractThumbnail(tmpBitmap, rw, rh, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return tmpBitmap;
    }
}
