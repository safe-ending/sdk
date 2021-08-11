package com.eningqu.aipen.common;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/9/9 11:35
 * desc   : 手写识别引擎
 * version: 1.0
 */
public enum HwrEngineEnum {
    MY_SCRIPT(0), MS(1), HANVON(2);

    private int value;

    HwrEngineEnum(int value){
        this.value = value;
    }

    public String toString() {
        String str = "";
        switch (value) {
            case 0:
                str = "MY_SCRIPT";
                break;
            case 1:
                str = "MS";
                break;
            case 2:
                str = "HANVON";
                break;
        }
        return str;
    }

    public static HwrEngineEnum getEnum(String str){
        if("MY_SCRIPT".equals(str)){
            return MY_SCRIPT;
        } else if("MS".equals(str)){
            return MS;
        } else {
            return HANVON;
        }
    }
}
