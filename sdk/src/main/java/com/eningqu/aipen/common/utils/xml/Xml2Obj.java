package com.eningqu.aipen.common.utils.xml;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2021/7/7 14:38
 * desc   :
 * version: 1.0
 */
public class Xml2Obj {
    public static <T> T fromXml(String xml, Class<T> c) throws Exception {

        return new PullParser<T>().parse(new ByteArrayInputStream(xml.getBytes("UTF-8")), c);

    }
}
