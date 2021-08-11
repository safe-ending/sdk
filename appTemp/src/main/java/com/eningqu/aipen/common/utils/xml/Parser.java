package com.eningqu.aipen.common.utils.xml;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2021/7/7 14:35
 * desc   :
 * version: 1.0
 */
public interface Parser<T> {
    /**
     * 解析输入流 得到对象
     * @param is
     * @return
     * @throws Exception
     */
    T parse(InputStream is, Class<T> c) throws Exception;

    /**
     * 序列化对象 得到XML形式的字符串
     * @param t
     * @return
     * @throws Exception
     */
    String serialize(T t) throws Exception;
}
