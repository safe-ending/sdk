package com.nq.edusaas.hps.model.command;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/2/25 9:47
 * desc   :
 * version: 1.0
 */
public class CommandSound extends CommandBase {

    /**
     * 声音功能代码
     */
    public static final int SOUND_LOUD = 0;//大声
    public static final int SOUND_LOW = 1;//小声
    public static final int SOUND_SILENCE = 2;//静音
    public CommandSound(int code) {
        super.setCode(code);
        super.setType(COMMAND_TYPE_SOUND);
    }
}
