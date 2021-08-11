package com.eningqu.aipen.common.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * 说明：
 * 作者：WangYabin
 * 邮箱：wyb@eningqu.com
 * 时间：13:02
 */
public class GetDeviceWidthAndHeight {
    public static int[] getDeviceWidthAndHeight(Context context){
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        float density = dm.density;
        int[] widthAndHeight = new int[2];
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        widthAndHeight[0] = width;
        widthAndHeight[1] = height;
        return widthAndHeight;
    }
}
