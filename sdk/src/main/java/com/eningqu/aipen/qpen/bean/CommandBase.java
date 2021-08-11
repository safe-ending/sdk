package com.eningqu.aipen.qpen.bean;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/2/25 9:44
 * desc   :
 * version: 1.0
 */
public class CommandBase {

    /**
     * 命令类型代码
     */
    public static final int COMMAND_TYPE_RECORD = 0;//录音
    public static final int COMMAND_TYPE_COLOR = 1;//颜色
    public static final int COMMAND_TYPE_SIZE = 2;//粗细
    public static final int COMMAND_TYPE_SOUND = 3;//声音

    private String name;
    private int sizeLevel;
    private int code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSizeLevel() {
        return sizeLevel;
    }

    public void setSizeLevel(int sizeLevel) {
        this.sizeLevel = sizeLevel;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
