package com.eningqu.aipen.common;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
* @Author: Qiu.Li
* @Create Date: 2021/3/12 9:29
* @Description: 功能业务工具
* @Email: liqiupost@163.com
*/
public class CommonBusUtils {
    /**
     * 电量水平值换算成百分比
     * @param level
     * @return
     */
    public static int bat2Percent(int level){
        int percent = 10;
        if(level>8){
            percent=100;
        } else if(level>7){
            percent = 70;
        } else if(level>2){
            percent = (level-2)*10;
        } else if(level>=0){
            percent = 10;
        }
        return percent;
    }

}
