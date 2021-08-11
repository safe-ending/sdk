package com.eningqu.aipen.bean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.eningqu.aipen.qpen.CanvasFrame;
import com.eningqu.aipen.qpen.SignatureView;
import com.eningqu.aipen.qpen.bean.PageStrokesCacheBean;

import java.lang.ref.WeakReference;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/4/29 15:08
 * desc   : 离线数据预览页面的列表数据类型
 * version: 1.0
 */
public class OfflinePageItemData extends PageStrokesCacheBean {
    private boolean check;
    private WeakReference<Bitmap> drawable;
//    protected SignatureView bDrawl;
//    protected CanvasFrame canvasFrame;
//
//    public CanvasFrame getCanvasFrame() {
//        return canvasFrame;
//    }
//
//    public void setCanvasFrame(CanvasFrame canvasFrame) {
//        this.canvasFrame = canvasFrame;
//    }


    public Bitmap getDrawable() {
        if (drawable != null)
            return drawable.get();
        else
            return null;
    }

    public void setDrawable(WeakReference<Bitmap> drawable) {
        this.drawable = drawable;
    }

    public OfflinePageItemData(Context context, String userId, String notebookId, int page, int ver, String bg) {
        super(userId, notebookId, page, ver, bg);
        this.check = false;
//        bDrawl = new SignatureView(context);
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

//    public SignatureView getbDrawl() {
//        return bDrawl;
//    }

//    public void setbDrawl(SignatureView bDrawl) {
//        this.bDrawl = bDrawl;
//    }
}
