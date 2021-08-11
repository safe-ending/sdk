package com.nq.edusaas.hps.model.command;

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
    public static final int COMMAND_TYPE_BOTTOM = 4;//底部
    public static final int COMMAND_TYPE_AREA =5;

    private String name;
    private int type;
    private int code;

    public CommandBase(int type) {
        this.type = type;
    }

    public CommandBase() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
