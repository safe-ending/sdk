//package com.nq.edusaas.hps.model.command;
//
///**
// * author : LiQiu
// * e-mail : lq@eningqu.com
// * date   : 2019/2/25 9:47
// * desc   :
// * version: 1.0
// */
//public class CommandSize extends CommandBase {
//    /**
//     * 粗细功能代码
//     */
//    public static final int PEN_SIZE_THIN = 0;//细
//    public static final int PEN_SIZE_MID = 1;//中
//    public static final int PEN_SIZE_THICK = 2;//粗
//    public static final int PEN_SIZE_DEFAULT = PEN_SIZE_MID;//默认
//
//    public CommandSize(int code) {
//        super.setCode(code);
//        super.setType(COMMAND_TYPE_SIZE);
//    }
//
//    public static float getSizeByType(int type) {
//        switch (type) {
//            case PEN_SIZE_THIN:
//                return 2.0f;
//            case PEN_SIZE_MID:
//                return 4.0f;
//            case PEN_SIZE_THICK:
//                return 6.0f;
//        }
//        return 2.0f;
//    }
//
//    public static float getSizeByCleanType(int type) {
//        switch (type) {
//            case PEN_SIZE_THIN:
//                return 10.0f;
//            case PEN_SIZE_MID:
//                return 15.0f;
//            case PEN_SIZE_THICK:
//                return 20.0f;
//        }
//        return 10.0f;
//    }
//
//    public static int getTypeBySize(float size) {
//        switch ((int) size) {
//            case 2:
//                return PEN_SIZE_THIN;
//            case 4:
//                return PEN_SIZE_MID;
//            case 6:
//                return PEN_SIZE_THICK;
//        }
//        return PEN_SIZE_THIN;
//    }
//}
