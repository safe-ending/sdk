package com.eningqu.aipen.qpen.bean;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/2/25 9:47
 * desc   :
 * version: 1.0
 */
public class CommandSize extends CommandBase {
    /**
     * 粗细功能代码
     */
    public static final int PEN_SIZE_ONE = 1;//细
    public static final int PEN_SIZE_TWO = 2;//中
    public static final int PEN_SIZE_THREE = 3;//粗1
    public static final int PEN_SIZE_FOUR = 4;//粗1
    public static final int PEN_SIZE_FIVE = 5;//粗1
    public static final int PEN_SIZE_SIX= 6;//粗1
    public static final int PEN_SIZE_SERVEN = 7;//粗1
    public static final int PEN_SIZE_EIGHT = 8;//粗1
    public static final int PEN_SIZE_NINE = 9;//粗1
    public static final int PEN_SIZE_TEN = 10;//粗1

    public CommandSize(int code) {
        super.setCode(code);
        super.setSizeLevel(COMMAND_TYPE_SIZE);
    }

    public static int getSizeByLevel(int sizeLevel) {
        switch (sizeLevel) {
            case PEN_SIZE_ONE:
                return 2;
            case PEN_SIZE_TWO:
                return 3;
            case PEN_SIZE_THREE:
                return 4;
            case PEN_SIZE_FOUR:
                return 5;
            case PEN_SIZE_FIVE:
                return 6;
            case PEN_SIZE_SIX:
                return 7;
            case PEN_SIZE_SERVEN:
                return 8;
            case PEN_SIZE_EIGHT:
                return 9;
            case PEN_SIZE_NINE:
                return 10;
            case PEN_SIZE_TEN:
                return 11;
        }
        return 2;
    }

    public static int getSizeLevelBySize(float size) {
        if (size == 2){
            return PEN_SIZE_ONE;
        }else if (size == 3){
            return PEN_SIZE_TWO;
        }else if (size == 4){
            return PEN_SIZE_THREE;
        }else if (size == 5){
            return PEN_SIZE_FOUR;
        }else if (size == 6){
            return PEN_SIZE_FIVE;
        }else if (size == 7){
            return PEN_SIZE_SIX;
        }else if (size == 8){
            return PEN_SIZE_SERVEN;
        }else if (size == 9){
            return PEN_SIZE_EIGHT;
        }else if (size == 10){
            return PEN_SIZE_NINE;
        }else if (size == 11){
            return PEN_SIZE_TEN;
        }
        return PEN_SIZE_ONE;
    }

}
