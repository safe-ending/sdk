package com.nq.edusaas.hps.utils;

import java.math.BigDecimal;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/4/11 20:06
 * desc   :
 * version: 1.0
 */
public class NumberUtil {
    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
//System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String mathLength(long length) {
        if (length < 1024) {
            return length + "B";
        } else if (length < 1024 * 1024) {
            BigDecimal bg = new BigDecimal((float) length / 1024);
            return bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "KB";
        } else if (length < 1024 * 1024 * 1024) {
            BigDecimal bg = new BigDecimal((float) length / 1024 / 1024);
            return bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "MB";
        } else {
            BigDecimal bg = new BigDecimal((float) length / 1024 / 1024 / 1024);
            return bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "GB";
        }


    }
}
