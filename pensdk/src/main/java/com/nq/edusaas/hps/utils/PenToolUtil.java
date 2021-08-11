package com.nq.edusaas.hps.utils;

import android.content.Context;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * author : Sivan
 * e-mail : hsy@eningqu.com
 * date   : 2020/7/21
 * desc   :
 * version: 1.0
 */
public class PenToolUtil {

    public static boolean isEmpty(Collection<?> datas) {
        return datas == null || datas.size() <= 0;
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    public static boolean indexInList(List<?> target, int index) {
        if (target == null) return false;
        return index >= 0 && index < target.size();
    }


    public static <I, O> O cast(I input, Class<O> outClass, O... defaultValue) {
        if (input != null && outClass.isAssignableFrom(input.getClass())) {
            try {
                return outClass.cast(input);
            } catch (ClassCastException e) {
                PenNQLog.error(e.toString());
            }
        }
        if (defaultValue != null && defaultValue.length > 0) {
            return defaultValue[0];
        }
        return null;
    }

    /**
     * 字符串转换为16进制字符串
     *
     * @param s 字符串
     * @return  16进制字符串
     */
    public static String stringToHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        PenNQLog.debug("str = " + str);
        return str;
    }

    /**
     * 16进制字符串转换为字符串
     *
     * @param s 16进制字符串
     * @return  字符串
     */
    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(
                        s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, StandardCharsets.UTF_8);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        PenNQLog.debug("str = " + s);
        return s;
    }

    /**
     * 获取状态栏的高度
     *
     * @param  context context
     * @return 状态栏高度
     */
    public static int getStatusHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
//        int statusHeight = -1;
//        try {
//            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
//            Object object = clazz.newInstance();
//            int height = Integer.parseInt(clazz.getField("status_bar_height")
//                    .get(object).toString());
//            statusHeight = context.getApplicationContext().getResources().getDimensionPixelSize(height);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return statusHeight;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
