package com.eningqu.aipen.common.enums;
/**
* @Author: Qiu.Li
* @Create Date: 2019/5/9 18:28
* @Description: 笔记本规格
* @Email: liqiupost@163.com
*/

public enum NoteTypeEnum {
//    A5 1-300
//    A4 301-600
//    A6 601-900
//    A3 901-1200

    NOTE_TYPE_A5(1),
    NOTE_TYPE_A4(2),
    NOTE_TYPE_A6(3),
    NOTE_TYPE_A3(4);

    private int noeType;
    NoteTypeEnum(int noeType) {
        this.noeType = noeType;
    }

    public int getNoeType() {
        return noeType;
    }
}
