package com.eningqu.aipen.common.utils.xml;

import android.util.Base64;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2021/7/7 14:19
 * desc   :
 * version: 1.0
 */
public class XmlBuilder {
    /**
     * 由对象属性生成XML
     *
     * @param outs     输出流
     * @param encoding 字符编码
     * @param obj      对象
     */
    public void buildXml(OutputStream outs, String encoding, Object obj)
            throws IllegalArgumentException, IllegalStateException,
            IOException, IllegalAccessException {
        XmlSerializer serializer = Xml.newSerializer(); // 创建一个Xml序列化
        serializer.setOutput(outs, encoding); // 设置输出流及字符编码
        serializer.startDocument(encoding, true); // 开始文档
        writeXml(serializer, obj); // 写文档
        serializer.endDocument(); // 结束文档
        outs.close(); // 关闭输出流
    }

    /**
     * 将对象属性写入XML
     *
     * @param serializer XML序列化
     * @param obj        对象
     */
    private void writeXml(XmlSerializer serializer, Object obj)
            throws IllegalArgumentException, IllegalStateException,
            IOException, IllegalAccessException {

        final String tag_type = "type";
        final String tag_value = "value";

        Class<?> cls = obj.getClass(); // 获取对象运行时类
        String clsName = cls.getName(); // 获取对象名称

        serializer.startTag(null, clsName);

        Field[] fields = cls.getDeclaredFields(); // 返回类声明的所有字段

        boolean isWrite;
        Class<?> type; // 变量类型
        String varName, typeName, value = null; // 变量名、类型名、变量值
        for (Field field : fields) {
            isWrite = true;
            type = field.getType(); // 获取变量类型
            typeName = type.getName(); // 获取类型名
            field.setAccessible(true); // 设置变量可访问
            if (type.isPrimitive()) { // 是否为基本类型
                if (typeName.equals("boolean")) {
                    value = String.valueOf(field.getBoolean(obj));
                } else if (typeName.equals("char")) {
                    // char型未赋值，为默认'\u0000'时，会影响parser.next()
                    char c = field.getChar(obj);
                    value = c == '\u0000' ? "null" : String.valueOf(field
                            .getChar(obj));
                } else if (typeName.equals("byte")) {
                    value = String.valueOf(field.getByte(obj));
                } else if (typeName.equals("short")) {
                    value = String.valueOf(field.getShort(obj));
                } else if (typeName.equals("int")) {
                    value = String.valueOf(field.getInt(obj));
                } else if (typeName.equals("long")) {
                    value = String.valueOf(field.getLong(obj));
                } else if (typeName.equals("float")) {
                    value = String.valueOf(field.getFloat(obj));
                } else if (typeName.equals("double")) {
                    value = String.valueOf(field.getDouble(obj));
                }
            } else if (typeName.equals("java.lang.String")) { // 是否为String
                value = field.get(obj) == null ? "null" : field.get(obj)
                        .toString();
            } else if (typeName.equals("[B")) { // 是否为byte[]
                typeName = getTypeHelper(type); // 字符代码->[]形式
                // 用Base64将byte[]转成字符串（SDK 2.2之后自带）
                value = field.get(obj) == null ? "null"
                        : Base64.encodeToString((byte[]) field.get(obj),
                        Base64.DEFAULT);
            } else { // 其他类型暂不需要
                isWrite = false;
            }
            if (isWrite) {
                varName = field.getName(); // 获取变量名
                serializer.startTag(null, varName);
                writeTag(serializer, tag_type, typeName);
                writeTag(serializer, tag_value, value);
                serializer.endTag(null, varName);
            }
        }

        serializer.endTag(null, clsName);
    }

    /**
     * 字符代码->[]形式
     */
    private String getTypeHelper(Class<?> type) {
        if (type.isArray()) {
            Class<?> c = type.getComponentType();
            return getTypeHelper(c) + "[]";
        } else {
            return type.getName();
        }
    }

    /**
     * 写一个标签及值
     */
    private void writeTag(XmlSerializer serializer, String tag, String value)
            throws IllegalArgumentException, IllegalStateException, IOException {
        serializer.startTag(null, tag);
        serializer.text(value);
        serializer.endTag(null, tag);
    }
}
