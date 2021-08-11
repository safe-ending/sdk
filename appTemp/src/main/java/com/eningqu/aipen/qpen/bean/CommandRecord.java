package com.eningqu.aipen.qpen.bean;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/2/25 9:47
 * desc   :
 * version: 1.0
 */
public class CommandRecord extends CommandBase {
    /**
     * 录音功能代码
     */
    public static final int RECORD_START = 0;//录音
    public static final int RECORD_PAUSE = 1;//暂停
    public static final int RECORD_STOP = 2;//停止
    public CommandRecord(int code) {
        super.setCode(code);
        super.setSizeLevel(COMMAND_TYPE_RECORD);
    }
}
