package com.eningqu.aipen.common.utils;

import java.lang.reflect.ParameterizedType;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/1/17 19:14
 */

public class ReflectHelper {

    private static String TGA = ReflectHelper.class.getSimpleName();

    public static <T> T getT(Object object, int i) {
        try {
            return ((Class<T>) ((ParameterizedType)
                    (object.getClass().getGenericSuperclass())).getActualTypeArguments()[i]).newInstance();
        } catch (Exception e) {
            L.error(TGA, e.getMessage());
        }
        return null;
    }
}
