package com.eningqu.aipen.qpen;

import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.qpen.bean.CommandBase;
import com.eningqu.aipen.qpen.bean.CommandColor;
import com.eningqu.aipen.qpen.bean.CommandRecord;
import com.eningqu.aipen.qpen.bean.CommandSize;
import com.eningqu.aipen.qpen.bean.CommandSound;
import com.eningqu.aipen.sdk.bean.DotType;
import com.eningqu.aipen.sdk.bean.NQDot;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/2/25 9:38
 * desc   :
 * version: 1.0
 */
public class SDKUtil {

    private static final int SPACE_SIZE = 20;

    public static final float PAGER_WIDTH_1 = 4960.0f;
    public static final float PAGER_HEIGHT_1 = 7040.0f;

    public static final float PAGER_WIDTH_2 = 3126.0f;
    public static final float PAGER_HEIGHT_2 = 4693.0f;

    public static final float PAGER_WIDTH_A3 = 7016.0f;
    public static final float PAGER_HEIGHT_A3 = 9921.0f;

    public static final float PAGER_WIDTH_A4 = 4690.0f;
    public static final float PAGER_HEIGHT_A4 = 7040.0f;

        //这里为了方便沿用原来的数据直接改A5的数值
//    public static final float PAGER_WIDTH_A5 = 3496.0f;
//    public static final float PAGER_HEIGHT_A5 = 4961.0f;
    public static final float PAGER_WIDTH_A5 = 1165.0f;
    public static final float PAGER_HEIGHT_A5 = 1661.0f;

    public static final float PAGER_WIDTH_A6 = 2680.0f;
    public static final float PAGER_HEIGHT_A6 = 3496.0f;


    public static final float fx = 3307.0f;
    public static final float fy = 4843.0f;

    public static final float WIDTH_HEIGHT_RATE_A3 = PAGER_WIDTH_A3 / PAGER_HEIGHT_A3;
    public static final float WIDTH_HEIGHT_RATE_A4 = PAGER_WIDTH_A4 / PAGER_HEIGHT_A4;
    public static final float WIDTH_HEIGHT_RATE_A5 = PAGER_WIDTH_A5 / PAGER_HEIGHT_A5;
    public static final float WIDTH_HEIGHT_RATE_A6 = PAGER_WIDTH_A3 / PAGER_HEIGHT_A6;

    public static NQDot conversionADot(NQDot nqDot, int frameW, int frameH) {
        NQDot dot = new NQDot();
        dot.type = nqDot.type;
        dot.page = nqDot.page;
        dot.bookNum = nqDot.bookNum;
        dot.book_width = (int) PAGER_WIDTH_A5;
        dot.book_height = (int) PAGER_HEIGHT_A5;

        /*if(0!=nqDot.book_width){
            dot.x = Math.round((nqDot.x*nqDot.book_width)/frameW);
        }else {
            dot.x = nqDot.x;
        }

        if(0!=nqDot.book_height){
            dot.y = Math.round((nqDot.y*nqDot.book_height)/frameH);
        }else {
            dot.y = nqDot.y;
        }*/
        //        dot.x = nqDot.x;
        //        dot.y = nqDot.y;
/*        float xf1 = (float) PAGER_WIDTH_1/frameW;
        float xf2 = (float)frameW/PAGER_WIDTH_2;
        float yf1 = (float) PAGER_HEIGHT_1/frameH;
        float yf2 = (float)frameH/PAGER_HEIGHT_2;
        dot.x = Math.round(((float)nqDot.x *xf1) * xf2);
        dot.y = Math.round(((float)nqDot.y *yf1) * yf2);*/
        //        dot.x = Math.round((nqDot.x  * frameW)/ nqDot.book_width);
        //        dot.y = Math.round((nqDot.y * frameH)/ nqDot.book_height);

        float xf1 = 1.0f;
        float yf1 = 1.0f;

        if (nqDot.bookNum == 1) {
            //A5
            xf1 = PAGER_WIDTH_A5 / nqDot.book_width;
            yf1 = PAGER_HEIGHT_A5 / nqDot.book_width;
        } else if (nqDot.bookNum == 2) {
            //A4
            xf1 = fx / nqDot.book_width;
            yf1 = fy / nqDot.book_height;
        } else if (nqDot.bookNum == 3) {
            //A6
            xf1 = fx / nqDot.book_width;
            yf1 = fx / nqDot.book_height;
        } else if (nqDot.bookNum == 4) {
            //A3
            xf1 = fx / nqDot.book_width;
            yf1 = fx / nqDot.book_height;
        } else {
            //A5
            xf1 = fx / nqDot.book_width;
            yf1 = fx / nqDot.book_height;
        }

        dot.x = nqDot.x;
        dot.y = nqDot.y;

        return dot;
    }

    public static CommandBase calculateADot(int page, int type, int x, int y) {
        if (AFPenClientCtrl.getInstance().getLastTryConnectName().startsWith(AppCommon.PEN_QPEN)){
            //是否是小本子
            if (y <= (Const.Size.PAGE_TYPE_2_SIZE_Y + Const.Size.PAGE_SIZE_Y_OFFSET)) {
                //是否在底部菜单栏内
                if (y > (Const.Size.PAGE_TYPE_2_BOTTOM_MENU_TOP ) && y < (Const.Size.PAGE_TYPE_2_BOTTOM_MENU_BOTTOM)) {

                    if (x > (Const.Size.PAGE_TYPE_2_RECORD_START_LEFT) && x < (Const.Size.PAGE_TYPE_2_RECORD_PAUSE_LEFT - SPACE_SIZE)) {
                        //开始录音区域
                        CommandRecord command = new CommandRecord(CommandRecord.RECORD_START);
                        return command;
                    } else if (x > (Const.Size.PAGE_TYPE_2_RECORD_PAUSE_LEFT + SPACE_SIZE) && x < (Const.Size.PAGE_TYPE_2_RECORD_STOP_LEFT - SPACE_SIZE)) {
                        //暂停录音区域
                        CommandRecord command = new CommandRecord(CommandRecord.RECORD_PAUSE);
                        return command;
                    } else if (x > (Const.Size.PAGE_TYPE_2_RECORD_STOP_LEFT + SPACE_SIZE) && x < (Const.Size.PAGE_TYPE_2_RECORD_STOP_RIGHT - SPACE_SIZE)) {
                        //停止录音区域
                        CommandRecord command = new CommandRecord(CommandRecord.RECORD_STOP);
                        return command;
                    } else if (x > (Const.Size.PAGE_TYPE_2_COLOR_RED_LEFT ) && x <= (Const.Size.PAGE_TYPE_2_COLOR_GREEN_LEFT - SPACE_SIZE *2)) {
                        // 画笔红色区域
                        CommandColor command = new CommandColor(CommandColor.PEN_COLOR_RED);
                        return command;
                    } else if (x > (Const.Size.PAGE_TYPE_2_COLOR_GREEN_LEFT + SPACE_SIZE*2 ) && x < (Const.Size.PAGE_TYPE_2_COLOR_BLUE_LEFT - SPACE_SIZE*2)) {
                        // 画笔绿色区域
                        CommandColor command = new CommandColor(CommandColor.PEN_COLOR_GREEN);
                        return command;
                    } else if (x > (Const.Size.PAGE_TYPE_2_COLOR_BLUE_LEFT + SPACE_SIZE *2) && x < (Const.Size.PAGE_TYPE_2_COLOR_BLACK_LEFT - SPACE_SIZE*2)) {
                        // 画笔蓝色区域
                        CommandColor command = new CommandColor(CommandColor.PEN_COLOR_BLUE);
                        return command;
                    } else if (x > (Const.Size.PAGE_TYPE_2_COLOR_BLACK_LEFT + SPACE_SIZE *2) && x < (Const.Size.PAGE_TYPE_2_COLOR_BLACK_RIGHT)) {
                        // 画笔蓝色区域
                        CommandColor command = new CommandColor(CommandColor.PEN_COLOR_BLACK);
                        return command;
                    } else if (x > (Const.Size.PAGE_TYPE_2_SIZE_THIN_LEFT) && x < (Const.Size.PAGE_TYPE_2_SIZE_MID_LEFT - 30)) {
                        // 画笔细线区域
                        CommandSize command = new CommandSize(CommandSize.PEN_SIZE_ONE);
                        return command;
                    } else if (x > (Const.Size.PAGE_TYPE_2_SIZE_MID_LEFT + 30) && x < (Const.Size.PAGE_TYPE_2_SIZE_THICK_LEFT +30)) {
                        // 画笔中线区域
                        CommandSize command = new CommandSize(CommandSize.PEN_SIZE_TWO);
                        return command;
                    } else if (x > (Const.Size.PAGE_TYPE_2_SIZE_THICK_LEFT + 30) && x < (Const.Size.PAGE_TYPE_2_SIZE_THICK_RIGHT )) {
                        // 画笔粗线区域
                        CommandSize command = new CommandSize(CommandSize.PEN_SIZE_THREE);
                        return command;
                    } else if (x > (Const.Size.PAGE_TYPE_2_SOUND_LOUD_LEFT ) && x < (Const.Size.PAGE_TYPE_2_SOUND_LOW_LEFT )) {
                        // 设置大声区域
                        CommandSound command = new CommandSound(CommandSound.SOUND_LOUD);
                        return command;
                    } else if (x > (Const.Size.PAGE_TYPE_2_SOUND_LOW_LEFT + 50) && x < (Const.Size.PAGE_TYPE_2_SOUND_SILENCE_LEFT )) {
                        // 设置小声区域
                        CommandSound command = new CommandSound(CommandSound.SOUND_LOW);
                        return command;
                    } else if (x > (Const.Size.PAGE_TYPE_2_SOUND_SILENCE_LEFT + 60) && x < (Const.Size.PAGE_TYPE_2_SOUND_SILENCE_RIGHT)) {
                        // 设置静音区域
                        CommandSound command = new CommandSound(CommandSound.SOUND_SILENCE);
                        return command;
                    }
                }
            } else {

            }
        }else{

                //是否在底部菜单栏内
                if (y > (Const.Size.PAGE_TYPE_2_BOTTOM_MENU_TOP_P20 ) && y < (Const.Size.PAGE_TYPE_2_BOTTOM_MENU_BOTTOM_P20)) {

                    if (x > (Const.Size.PAGE_TYPE_2_RECORD_START_LEFT_P20) && x < (Const.Size.PAGE_TYPE_2_RECORD_PAUSE_LEFT_P20 )) {
                        //开始录音区域
                        CommandRecord command = new CommandRecord(CommandRecord.RECORD_START);
                        return command;
                    } else if (x > (Const.Size.PAGE_TYPE_2_RECORD_PAUSE_LEFT_P20 ) && x < (Const.Size.PAGE_TYPE_2_RECORD_STOP_LEFT_P20)) {
                        //暂停录音区域
                        CommandRecord command = new CommandRecord(CommandRecord.RECORD_PAUSE);
                        return command;
                    } else if (x > (Const.Size.PAGE_TYPE_2_RECORD_STOP_LEFT_P20) && x < (Const.Size.PAGE_TYPE_2_RECORD_STOP_RIGHT_P20 )) {
                        //停止录音区域
                        CommandRecord command = new CommandRecord(CommandRecord.RECORD_STOP);
                        return command;
                    } else if (x > (Const.Size.PAGE_TYPE_2_COLOR_RED_LEFT_P20 ) && x <= (Const.Size.PAGE_TYPE_2_COLOR_GREEN_LEFT_P20)) {
                        // 画笔红色区域
                        CommandColor command = new CommandColor(CommandColor.PEN_COLOR_RED);
                        return command;
                    } else if (x > (Const.Size.PAGE_TYPE_2_COLOR_GREEN_LEFT_P20 ) && x < (Const.Size.PAGE_TYPE_2_COLOR_BLUE_LEFT_P20)) {
                        // 画笔绿色区域
                        CommandColor command = new CommandColor(CommandColor.PEN_COLOR_GREEN);
                        return command;
                    } else if (x > (Const.Size.PAGE_TYPE_2_COLOR_BLUE_LEFT_P20 ) && x < (Const.Size.PAGE_TYPE_2_COLOR_BLACK_LEFT_P20 )) {
                        // 画笔蓝色区域
                        CommandColor command = new CommandColor(CommandColor.PEN_COLOR_BLUE);
                        return command;
                    } else if (x > (Const.Size.PAGE_TYPE_2_COLOR_BLACK_LEFT_P20) && x < (Const.Size.PAGE_TYPE_2_COLOR_BLACK_RIGHT_P20)) {
                        // 画笔蓝色区域
                        CommandColor command = new CommandColor(CommandColor.PEN_COLOR_BLACK);
                        return command;
                    } else if (x > (Const.Size.PAGE_TYPE_2_SIZE_THIN_LEFT_P20) && x < (Const.Size.PAGE_TYPE_2_SIZE_THIN_RIGHT_P20 )) {
                        // 画笔细线区域
                        CommandSize command = new CommandSize(CommandSize.PEN_SIZE_ONE);
                        return command;
                    } else if (x > (Const.Size.PAGE_TYPE_2_SIZE_MID_LEFT_P20) && x < (Const.Size.PAGE_TYPE_2_SIZE_MID_RIGHT_P20)) {
                        // 画笔中线区域
                        CommandSize command = new CommandSize(CommandSize.PEN_SIZE_TWO);
                        return command;
                    } else if (x > (Const.Size.PAGE_TYPE_2_SIZE_THICK_LEFT_P20 ) && x < (Const.Size.PAGE_TYPE_2_SIZE_THICK_RIGHT_P20 )) {
                        // 画笔粗线区域
                        CommandSize command = new CommandSize(CommandSize.PEN_SIZE_THREE);
                        return command;
                    } else if (x > (Const.Size.PAGE_TYPE_2_SOUND_LOUD_LEFT_P20 ) && x < (Const.Size.PAGE_TYPE_2_SOUND_LOW_LEFT_P20 )) {
                        // 设置大声区域
                        CommandSound command = new CommandSound(CommandSound.SOUND_LOUD);
                        return command;
                    } else if (x > (Const.Size.PAGE_TYPE_2_SOUND_LOW_LEFT_P20 ) && x < (Const.Size.PAGE_TYPE_2_SOUND_SILENCE_LEFT_P20 )) {
                        // 设置小声区域
                        CommandSound command = new CommandSound(CommandSound.SOUND_LOW);
                        return command;
                    } else if (x > (Const.Size.PAGE_TYPE_2_SOUND_SILENCE_LEFT_P20 ) && x < (Const.Size.PAGE_TYPE_2_SOUND_SILENCE_RIGHT_P20)) {
                        // 设置静音区域
                        CommandSound command = new CommandSound(CommandSound.SOUND_SILENCE);
                        return command;
                    }
                }

        }


        return null;
    }

    public static boolean calculateADot(NQDot dot, IPenTouchFunctionListener functionListener) {
        boolean isCommand = false;//是否命令区的点
        if (dot.type == DotType.PEN_ACTION_UP) {
            CommandBase commandBase = SDKUtil.calculateADot(dot.page, dot.type, dot.x, dot.y);
            if (null != commandBase) {
                isCommand = true;
                functionListener.onCommand(commandBase);
                switch (commandBase.getSizeLevel()) {
                    case CommandBase.COMMAND_TYPE_RECORD:
                        CommandRecord commandRecord = (CommandRecord) commandBase;
                        if (null != commandRecord) {
                            switch (commandRecord.getCode()) {
                                case CommandRecord.RECORD_START:
                                    break;
                                case CommandRecord.RECORD_PAUSE:
                                    break;
                                case CommandRecord.RECORD_STOP:
                                    break;
                            }
                        }
                        break;
                    case CommandBase.COMMAND_TYPE_COLOR:
                        CommandColor commandColor = (CommandColor) commandBase;
                        if (null != commandColor) {
                            switch (commandColor.getCode()) {
                                case CommandColor.PEN_COLOR_RED:
                                    break;
                                case CommandColor.PEN_COLOR_GREEN:
                                    break;
                                case CommandColor.PEN_COLOR_BLUE:
                                    break;
                                case CommandColor.PEN_COLOR_BLACK:
                                    break;
                            }
                        }
                        break;
                    case CommandBase.COMMAND_TYPE_SIZE:
                        CommandSize command = (CommandSize) commandBase;
                        if (null != command) {
                        }
                        break;
                    case CommandBase.COMMAND_TYPE_SOUND:
                        CommandSound commandSound = (CommandSound) commandBase;
                        switch (commandSound.getCode()) {
                            case CommandSound.SOUND_LOUD:
                                break;
                            case CommandSound.SOUND_LOW:
                                break;
                            case CommandSound.SOUND_SILENCE:
                                break;
                        }
                        break;
                }
            }
        } /*else if (dot.type == DotType.PEN_ACTION_DOWN) {
            CommandBase commandBase = SDKUtil.calculateADot(dot.page, dot.type, dot.x, dot.y);
            if (null != commandBase) {
                isCommand = true;
            }
        }*/
        return isCommand;
    }

    public static boolean isFunctionDot(NQDot dot) {
        boolean isCommand = false;//是否命令区的点
        if (dot.type == DotType.PEN_ACTION_UP) {
            CommandBase commandBase = SDKUtil.calculateADot(dot.page, dot.type, dot.x, dot.y);
            if (null != commandBase) {
                isCommand = true;
                //                functionListener.onCommand(commandBase);
                /*switch (commandBase.getType()) {
                    case CommandBase.COMMAND_TYPE_RECORD:
                        CommandRecord commandRecord = (CommandRecord) commandBase;
                        if (null != commandRecord) {
                            switch (commandRecord.getCode()) {
                                case CommandRecord.RECORD_START:
                                    break;
                                case CommandRecord.RECORD_PAUSE:
                                    break;
                                case CommandRecord.RECORD_STOP:
                                    break;
                            }
                        }
                        break;
                    case CommandBase.COMMAND_TYPE_COLOR:
                        CommandColor commandColor = (CommandColor) commandBase;
                        if (null != commandColor) {
                            switch (commandColor.getCode()) {
                                case CommandColor.PEN_COLOR_RED:
                                    break;
                                case CommandColor.PEN_COLOR_GREEN:
                                    break;
                                case CommandColor.PEN_COLOR_BLUE:
                                    break;
                                case CommandColor.PEN_COLOR_BLACK:
                                    break;
                            }
                        }
                        break;
                    case CommandBase.COMMAND_TYPE_SIZE:
                        CommandSize command = (CommandSize) commandBase;
                        if (null != command) {
                        }
                        break;
                    case CommandBase.COMMAND_TYPE_SOUND:
                        CommandSound commandSound = (CommandSound) commandBase;
                        switch (commandSound.getCode()) {
                            case CommandSound.SOUND_LOUD:
                                break;
                            case CommandSound.SOUND_LOW:
                                break;
                            case CommandSound.SOUND_SILENCE:
                                break;
                        }
                        break;
                }*/
            }
        } /*else if (dot.type == DotType.PEN_ACTION_DOWN) {
            CommandBase commandBase = SDKUtil.calculateADot(dot.page, dot.type, dot.x, dot.y);
            if (null != commandBase) {
                isCommand = true;
            }
        }*/
        return isCommand;
    }
}
