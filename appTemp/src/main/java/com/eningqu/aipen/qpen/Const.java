package com.eningqu.aipen.qpen;

public class Const {
    public class Broadcast {
        public static final String PEN_ADDRESS = "pen_address";
        public static final String PEN_NAME = "pen_NAME";
        public static final String ACTION_PEN_MESSAGE = "action_pen_message";
        public static final String MESSAGE_TYPE = "message_type";
        public static final String CONTENT = "content";

        public static final String ACTION_PEN_DOT = "action_pen_dot";
        public static final String EXTRA_DOT = "dot";

        public static final String ACTION_FIND_DEVICE = "ACTION_FIND_DEVICE";
    }

    public class PenMsgType{
        public static final int FIND_DEVICE = 5;
        public static final int PEN_CONNECTION_TRY = 1;
        public static final int PEN_CONNECTION_SUCCESS = 2;
        public static final int PEN_CONNECTION_FAILURE = 3;
        public static final int PEN_DISCONNECTED = 4;
        public static final int PEN_ALREADY_CONNECTED = 6;
        public static final int PEN_CUR_BATT = 7;
        public static final int PEN_FW_VER = 8;
        public static final int PEN_CUR_MEMOFFSET = 9;
        public static final int PEN_FLASH_USED_AMOUNT = 10;
        public static final int PEN_DELETE_OFFLINEDATA_FINISHED = 11;
        public static final int PEN_ERR_CODE12 = 12;
        public static final int PEN_CONNECTION_TIMEOUT = 176;
    }

    /**
     * 笔记本纸张规格
     */
    public enum  PageFormat{
        PAGE_A3(1, "A3", 7016, 9921), PAGE_A4(2, "A4", 4960, 7040), PAGE_A5(3, "A5", 3401, 4984), PAGE_A6(4, "A6", 0, 0);
        private int format;
        private String name;
        private int width;
        private int height;
        PageFormat(int format, String name, int width, int height){
            this.format = format;
            this.name = name;
            this.width = width;
            this.height = height;
        }

        public int getFormat(){
            return this.format;
        }

        public String getName(){
            return this.name;
        }

        public int getWidth(){
            return this.width;
        }

        public int getHeight(){
            return this.height;
        }
    }


    /**
     * 笔记本规格代码
     */
    public class Size {
        public static final int PAGE_TYPE_1_SIZE_X = 4800;//宽x
        public static final int PAGE_TYPE_1_SIZE_Y = 6800;//高y
        public static final int PAGE_TYPE_2_SIZE_X = 3100;//宽x
        public static final int PAGE_TYPE_2_SIZE_Y = 4500;//高y

        public static final int PAGE_SIZE_Y_OFFSET = 100;//高y

        /**
         * p20底部功能菜单坐标范围
         */
        public static final int PAGE_TYPE_2_BOTTOM_MENU_TOP_P20 = 1525;//y 1525
        public static final int PAGE_TYPE_2_BOTTOM_MENU_BOTTOM_P20 = 1572;//y 1570
        /**
         * 录音功能坐标范围
         */
        public static final int PAGE_TYPE_2_RECORD_TOP_P20 = 4330;//y
        public static final int PAGE_TYPE_2_RECORD_BOTTOM_P20 = 4490;//y

        public static final int PAGE_TYPE_2_RECORD_START_LEFT_P20 = 90;//x
        public static final int PAGE_TYPE_2_RECORD_PAUSE_LEFT_P20 = 145;//x
        public static final int PAGE_TYPE_2_RECORD_STOP_LEFT_P20 = 185;//x
        public static final int PAGE_TYPE_2_RECORD_STOP_RIGHT_P20 = 240;//x
        /**
         * 颜色功能坐标范围
         */
        public static final int PAGE_TYPE_2_COLOR_RED_LEFT_P20 = 270;//x
        public static final int PAGE_TYPE_2_COLOR_GREEN_LEFT_P20 = 345;//x
        public static final int PAGE_TYPE_2_COLOR_BLUE_LEFT_P20 = 435;//x
        public static final int PAGE_TYPE_2_COLOR_BLACK_LEFT_P20 = 515;//x
        public static final int PAGE_TYPE_2_COLOR_BLACK_RIGHT_P20 = 595;//x
        /**
         * 粗细功能坐标范围
         */
        public static final int PAGE_TYPE_2_SIZE_THIN_LEFT_P20 = 610;//x
        public static final int PAGE_TYPE_2_SIZE_THIN_RIGHT_P20 = 672;//x
        public static final int PAGE_TYPE_2_SIZE_MID_LEFT_P20 = 680;//x
        public static final int PAGE_TYPE_2_SIZE_MID_RIGHT_P20 = 742;//x
        public static final int PAGE_TYPE_2_SIZE_THICK_LEFT_P20 = 755;//x
        public static final int PAGE_TYPE_2_SIZE_THICK_RIGHT_P20 = 812;//x
        /**
         * 声音功能坐标范围
         */
        public static final int PAGE_TYPE_2_SOUND_LOUD_LEFT_P20 = 830;//x
        public static final int PAGE_TYPE_2_SOUND_LOW_LEFT_P20 = 910;//x
        public static final int PAGE_TYPE_2_SOUND_SILENCE_LEFT_P20 = 990;//x
        public static final int PAGE_TYPE_2_SOUND_SILENCE_RIGHT_P20 = 1060;//x

        //qpen底部功能菜单坐标范围
        public static final int PAGE_TYPE_2_BOTTOM_MENU_TOP = 4410;//y
        public static final int PAGE_TYPE_2_BOTTOM_MENU_BOTTOM = 4670;//y
        /**
         * 录音功能坐标范围
         */
        public static final int PAGE_TYPE_2_RECORD_TOP = 4330;//y
        public static final int PAGE_TYPE_2_RECORD_BOTTOM = 4490;//y

        public static final int PAGE_TYPE_2_RECORD_START_LEFT = 240;//x
        public static final int PAGE_TYPE_2_RECORD_PAUSE_LEFT = 400;//x
        public static final int PAGE_TYPE_2_RECORD_STOP_LEFT = 560;//x
        public static final int PAGE_TYPE_2_RECORD_STOP_RIGHT = 720;//x

        /**
         * 颜色功能坐标范围
         */
        public static final int PAGE_TYPE_2_COLOR_RED_LEFT = 780;//x
        public static final int PAGE_TYPE_2_COLOR_GREEN_LEFT = 960;//x
        public static final int PAGE_TYPE_2_COLOR_BLUE_LEFT = 1220;//x
        public static final int PAGE_TYPE_2_COLOR_BLACK_LEFT = 1430;//x
        public static final int PAGE_TYPE_2_COLOR_BLACK_RIGHT = 1650;//x
        /**
         * 粗细功能坐标范围
         */
        public static final int PAGE_TYPE_2_SIZE_THIN_LEFT = 1700;//x
        public static final int PAGE_TYPE_2_SIZE_MID_LEFT = 1900;//x
        public static final int PAGE_TYPE_2_SIZE_THICK_LEFT = 2120;//x
        public static final int PAGE_TYPE_2_SIZE_THICK_RIGHT = 2350;//x
        /**
         * 声音功能坐标范围
         */
        public static final int PAGE_TYPE_2_SOUND_LOUD_LEFT = 2430;//x
        public static final int PAGE_TYPE_2_SOUND_LOW_LEFT = 2620;//x
        public static final int PAGE_TYPE_2_SOUND_SILENCE_LEFT = 2830;//x
        public static final int PAGE_TYPE_2_SOUND_SILENCE_RIGHT = 3080;//x
    }

    /**
     * 页码
     */
    public class Page{
        public static final int PAGE_MAX_A5 = 300;
    }
}
