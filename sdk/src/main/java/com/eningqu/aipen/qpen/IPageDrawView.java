package com.eningqu.aipen.qpen;

import android.view.SurfaceHolder;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/8/17 14:18
 * desc   :
 * version: 1.0
 */
public interface IPageDrawView {
    void onCreated(SurfaceHolder holder);
    void onChanged(SurfaceHolder holder, int format, int width, int height);
    void onDestroyed(SurfaceHolder holder);
}
