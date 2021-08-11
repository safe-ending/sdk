package com.nq.edusaas.hps.model.command;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/2/25 9:47
 * desc   :
 * version: 1.0
 */
public class CommandColor extends CommandBase {
    /**
     * 颜色功能代码
     */
    public static final int PEN_COLOR_RED = 0;//红色
    public static final int PEN_COLOR_GREEN = 1;//绿色
    public static final int PEN_COLOR_BLUE = 2;//蓝色
    public static final int PEN_COLOR_BLACK = 3;//黑色
    public CommandColor(int code) {
        super.setCode(code);
        super.setType(COMMAND_TYPE_COLOR);
    }
}
