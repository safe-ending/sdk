package com.eningqu.aipen.common.utils.xml;

import android.util.Xml;

import com.eningqu.aipen.common.utils.L;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.lang.reflect.Field;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2021/7/7 14:37
 * desc   :
 * version: 1.0
 */
public class PullParser<T> implements Parser<T> {
    @Override
    public T parse(InputStream in, Class<T> c) throws Exception {
//        ArrayList<T> objList = null;

        T t = c.newInstance();
        Class weaClass = t.getClass();
        Field[] fs = weaClass.getDeclaredFields();
        XmlPullParser parser = Xml.newPullParser(); //由android.util.Xml创建一个XmlPullParser实例
        parser.setInput(in, "UTF-8");               //设置输入流 并指明编码方式
        int eventType = parser.getEventType();

        int tagDepth; // 标签深度
        String tagName; // 标签名称
        int lastTag = -1;
        String starTagName = "";
        String endTagName = "";
        String lastTagName = "";
        String text = "";

//        StringBuilder sb = new StringBuilder();
//        for(Field field:fs){
//            sb.append(field.getName()).append("\n");
//        }
//        L.debug(sb.toString());

        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    // 文档开始事件
//                    objList = new ArrayList<T>(); //初始化beanlist
                    break;
                case XmlPullParser.TEXT:
                    text = parser.getText();
                    break;
                case XmlPullParser.START_TAG:
                    // 开始标签事件
                    tagDepth = parser.getDepth(); // 获取标签深度
                    tagName = parser.getName(); // 获取标签名称

                    starTagName = tagName;
//                    L.info(tagDepth + "<"+tagName+">-" + lastTag + "-"+lastTagName);
                    for (int i = 0; i < fs.length; i++) {
                        Field f = fs[i];
                        f.setAccessible(true); //设置些属性是可以访问的

                        if (("return".equals(tagName) && fs[i].getName().equals("error"))) {
                            eventType = parser.next();
                            if (null != parser.getText()) {
                                f.set(t, parser.getText());
                            }
                            break;
                        } else if (tagName.equals(fs[i].getName())) {
                            eventType = parser.next();
                            String value = parser.getText();
                            if (null != value && !"".equals(value.trim().replace("\n", ""))) {
                                lastTag = eventType;
                                f.set(t, value);
                            }
                            lastTagName = tagName;
                            break;
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    // 结束标签事件
                    //end of xml
                    tagName = parser.getName(); // 获取标签名称
                    endTagName = tagName;
                    String value = parser.getText();
                    if (!"".equals(lastTagName)&&!lastTagName.equals(endTagName) &&  starTagName.equals(endTagName)) {
                        L.info(" starTagName=" + starTagName + " endTagName=" + endTagName + " lastTagName=" + lastTagName + " text=" + text);

                        Object obj = null;
                        for (int i = 0; i < fs.length; i++) {
                            Field f = fs[i];
                            f.setAccessible(true); //设置些属性是可以访问的
                            if(f.getName().equals(lastTagName)){
                                obj = f.get(t);
                                break;
                            }
                        }

                        if(null!=obj){
//                            if(lastTagName.equals("serverinfo")){
//                            }
                            Field[] fs2 = obj.getClass().getDeclaredFields();
                            for (int i = 0; i < fs2.length; i++) {
                                Field f = fs2[i];
                                f.setAccessible(true); //设置些属性是可以访问的

                                if (tagName.equals(fs2[i].getName())) {
                                    f.set(obj, text);
                                }
                            }
                        }
                    }
//                    L.info("</"+tagName+">-" + lastTag + "-"+lastTagName);
                    break;
            }
            // 切换到下一个解析事件
            eventType = parser.next();
        }
        return t;
    }

    @Override
    public String serialize(T t) throws Exception {
        return null;
    }
}
