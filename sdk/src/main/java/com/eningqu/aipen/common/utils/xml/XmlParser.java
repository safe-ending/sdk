package com.eningqu.aipen.common.utils.xml;

import android.util.Base64;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2021/7/7 14:10
 * desc   :
 * version: 1.0
 */
public class XmlParser {
    /**
     * 分析XML将对应值赋予对象
     *
     * @param ins      输入流
     * @param encoding 字符编码
     * @param obj      对象
     */
    public void parseXml(InputStream ins, String encoding, Object obj)
            throws XmlPullParserException, IllegalArgumentException,
            UnsupportedEncodingException, IllegalAccessException, IOException {
        XmlPullParser parser = Xml.newPullParser(); // 创建一个Xml分析器
        parser.setInput(ins, encoding); // 设置输入流及字符编码
        parseXml(parser, obj); // 解析文档
        ins.close(); // 关闭输入流
    }

    /**
     * 分析XML详细步骤
     *
     * @param parser Xml分析器
     * @param obj    对象
     */
    private void parseXml(XmlPullParser parser, Object obj)
            throws XmlPullParserException, IllegalArgumentException,
            UnsupportedEncodingException, IllegalAccessException, IOException {

        final String tag_value = "value";

        Class<?> cls = obj.getClass(); // 获取对象运行时类
        String clsName = cls.getSimpleName(); // 获取对象名称
        Field[] fields = cls.getDeclaredFields(); // 返回类声明的所有字段

        Field field = null;

        int tagDepth; // 标签深度
        String tagName; // 标签名称
        int eventType = parser.getEventType(); // 获取事件类型
        while (true) {
            if (eventType == XmlPullParser.START_DOCUMENT) {
                // Log.i("START_DOCUMENT", "=START_DOCUMENT");
            } else if (eventType == XmlPullParser.START_TAG) {
                // Log.i("START_TAG", "=START_TAG");

                tagDepth = parser.getDepth(); // 获取标签深度
                tagName = parser.getName(); // 获取标签名称

                if (tagDepth == 1) { // 一级标签
                    if (!tagName.equals(clsName)) { // 与对象名称不一致时
                        throw new XmlPullParserException("XML首标签名与对象名称不一致");
                    }
                } else if (tagDepth == 2) { // 二级标签
                    // 判断标签名称是否符合类的某一字段名称
                    field = hasField(tagName, fields);
                } else if (tagDepth == 3 && field != null) { // 三级标签
                    if (tagName.equals(tag_value)) { // 如果是value标签
                        setValue(obj, field, parser.nextText());
                        field = null;
                    }
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                // Log.i("END_TAG", "=END_TAG");
            } else if (eventType == XmlPullParser.TEXT) {
                // Log.i("TEXT", "=TEXT");
            } else if (eventType == XmlPullParser.END_DOCUMENT) {
                // Log.i("END_DOCUMENT", "=END_DOCUMENT");
                break;
            }
            eventType = parser.next(); // 下一解析事件
        }
    }

    /**
     * 判断标签名称是否符合类的某一字段名称
     *
     * @param tagName 标签名称
     * @param fields  类字段集合
     * @return 符合：Field；否则：null
     */
    private Field hasField(String tagName, Field[] fields) {
        for (Field field : fields) {
            if (tagName.equals(field.getName())) {
                return field;
            }
        }
        return null;
    }

    /**
     * 给字段设值
     *
     * @param obj   对象
     * @param field 字段
     * @param value 值
     */
    private void setValue(Object obj, Field field, String value)
            throws IllegalArgumentException, IllegalAccessException,
            UnsupportedEncodingException {
        Class<?> type = field.getType(); // 变量类型
        String typeName = type.getName(); // 类型名
        field.setAccessible(true); // 设置变量可访问
        if (type.isPrimitive()) { // 是否为基本类型
            if (typeName.equals("boolean")) {
                field.setBoolean(obj, Boolean.parseBoolean(value));
            } else if (typeName.equals("char")) {
                if (!value.equals("null")) {
                    field.setChar(obj, value.charAt(0));
                }
            } else if (typeName.equals("byte")) {
                field.setByte(obj, Byte.parseByte(value));
            } else if (typeName.equals("short")) {
                field.setShort(obj, Short.parseShort(value));
            } else if (typeName.equals("int")) {
                field.setInt(obj, Integer.parseInt(value));
            } else if (typeName.equals("long")) {
                field.setLong(obj, Long.parseLong(value));
            } else if (typeName.equals("float")) {
                field.setFloat(obj, Float.parseFloat(value));
            } else if (typeName.equals("double")) {
                field.setDouble(obj, Double.parseDouble(value));
            }
        } else if (typeName.equals("java.lang.String")) { // 是否为String
            if (!value.equals("null")) {
                field.set(obj, value);
            }
        } else if (typeName.equals("[B")) { // 是否为byte[]
            if (!value.equals("null")) {
                // 用Base64将字符串转成byte[]（SDK 2.2之后自带）
                field.set(obj, Base64.decode(value, Base64.DEFAULT));
            }
        } // 其他类型暂不需要
    }
}
