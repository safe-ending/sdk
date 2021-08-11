package com.eningqu.aipen.common.utils.xml;

import com.eningqu.aipen.common.utils.L;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2021/6/25 11:28
 * desc   :
 * version: 1.0
 */
class SaxHelper extends DefaultHandler {

    //当前解析的元素标签
    private String tagName = null;

    /**
     * 当读取到文档开始标志是触发，通常在这里完成一些初始化操作
     */
    @Override
    public void startDocument() throws SAXException {

        L.debug("SAX", "读取到文档头,开始解析xml");
    }

    /**
     * 读到一个开始标签时调用,第二个参数为标签名,最后一个参数为属性数组
     */
    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        L.debug("SAX", "localName " + localName + ",  <" + qName + ">");//开始处理节点
        if (localName.equalsIgnoreCase("root")) {
//            C = new SMSResponse();
        }
        this.tagName = localName;
    }

    /**
     * 读到到内容,第一个参数为字符串内容,后面依次为起始位置与长度
     */

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        //判断当前标签是否有效
        if (this.tagName != null) {
            String value = new String(ch, start, length);
            L.debug("SAX", tagName + "节点内容: " + value);
            //读取标签中的内容
            if (this.tagName.equalsIgnoreCase("return")) {

            } else if (this.tagName.equalsIgnoreCase("mode")) {

            }

        }

    }

    /**
     * 处理节点结束时触发,这里将对象添加到结合中
     */
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        L.debug("SAX", "localName " + localName + ",  </" + qName + ">");//完成处理节点


        if (localName.equalsIgnoreCase("root")) {
        }

        this.tagName = null;
    }

    /**
     * 读取到文档结尾时触发，
     */
    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        L.debug("SAX", "读取到文档尾,xml解析结束");
    }

    public Object getResponse() {
        return null;
    }
}
