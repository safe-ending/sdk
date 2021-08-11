package com.nq.edusaas.hps.sdkcore.afpensdk;

public class Const {

    public static class Broadcast {
        public static final String PEN_ADDRESS = "pen_address";
        public static final String PEN_NAME = "pen_NAME";
        public static final String ACTION_PEN_MESSAGE = "action_pen_message";
        public static final String MESSAGE_TYPE = "message_type";
        public static final String CONTENT = "content";

        public static final String ACTION_PEN_DOT = "action_pen_dot";
        public static final String EXTRA_DOT = "dot";

        public static final String ACTION_FIND_DEVICE = "ACTION_FIND_DEVICE";

        public static final String ACTION_FIND_START = "ACTION_FIND_START";
        public static final String ACTION_FIND_STOP = "ACTION_FIND_STOP";
        public static final String ACTION_FIND_ERROR = "ACTION_FIND_ERROR";
        public static final String ACTION_USB_DEVICE_ATTACHED = "ACTION_USB_DEVICE_ATTACHED";
        public static final String ACTION_USB_DEVICE_DETACHED = "ACTION_USB_DEVICE_DETACHED";

        public static final int ACTION_START_MICRO_LESSON = 1003;

        //tcp连接状态
        public static final String TCP_CONNECT = "tcp_connect";
        public static final String TCP_UNCONNECT = "tcp_unconnect";

        //笔绑定状态
        public static final String PEN_CONNECT = "pen_connect";
        public static final String PEN_UNCONNECT = "pen_unconnect";
    }

    public static class PenMsgType {
        public static final int PEN_CONNECTION_TRY = 1;
        public static final int PEN_CONNECTION_SUCCESS = 2;
        public static final int PEN_CONNECTION_FAILURE = 3;
        public static final int PEN_DISCONNECTED = 4;
        public static final int FIND_DEVICE = 5;
        public static final int PEN_ALREADY_CONNECTED = 6;
        public static final int PEN_CUR_BATT = 7;
        public static final int PEN_FW_VER = 8;
        public static final int PEN_CUR_MEMOFFSET = 9;
        public static final int PEN_FLASH_USED_AMOUNT = 10;
        public static final int PEN_DELETE_OFFLINEDATA_FINISHED = 11;
        public static final int PEN_ERR_CODE12 = 12;
        public static final int PEN_CONNECTION_TIMEOUT = 176;
    }

    public static class IntentData {
        public static final String INTENT_EXTRA_USER_ID = "userId";
        public final static String ROLE_TYPE = "roleType";
        public final static String CHANNEL_ID = "channelId";
        public final static String QUESTION_PATH = "questionPath";
        public final static String QUESTION_ID = "questionId";
        public final static String ANSWER_PATH = "answerPath";
        public final static String HAS_ANSWER = "hasAnswer";
        public final static String CLICK_QUESTION = "clickQuestion";
        public final static String PHONE_NUM = "phoneNum";
        public final static String QUESTION_CODE = "qCode";
        public final static String TEACHER_CODE = "teaCode";
        public final static String TEACHER_NAME = "teaName";
        public final static String USER_UUID = "uuid";

    }

    public static class Constant {
        /**
         * 画图
         */
        public final static int DRAW_CODE = 10001;

        /**
         * 蓝牙连接成功
         */
        public static final int BLE_CONNECT_SUCCESS_CODE = 30006;
        /**
         * 换页
         */
        public static final int SWITCH_PAGE_CODE = 30009;
        /**
         * 打开笔记本
         */
        public static final int OPEN_NOTEBOOK_CODE = 30010;
        /**
         * 功能指令
         */
        public static final int FUNCTION_COMMAND_CODE = 30012;

        /**
         * 凝趣授权
         */
        public static final int NQ_SER_AUTH_SUCCESS = 40003;
        public static final int NQ_SER_AUTH_FAIL = 40004;


        public static final int BLE_FOUND_DEVICE = 400002;
        public static final int BLE_START_FOUND_DEVICE = 400003;

        /**
         * 收藏
         */
        public static final int ERROR_LOCKED = 1001;
        /**
         * 空笔记本
         */
        public static final int ERROR_NONE_NOTEBOOK = 1002;
        /**
         * 没有选择笔记本
         */
        public static final int ERROR_NONE_SELECT_NOTEBOOK = 1003;

        /**
         * 图片获取方式
         */
        public static final int IMAGE_RESULT_BY_CAPTURE = 2000;
        public static final int IMAGE_RESULT_BY_PHOTO = 2001;

        /**
         * 退出
         */
        public static final int USER_LOGOUT = 140001;
        public static final int TCP_STATE = 400004;
        public static final int BIND_STATE = 400005;
    }

    public static class UserInfoConstant {
        public static final int USER_INFO_NICK = 4350;
        public static final int USER_INFO_MOTTO = 4351;
        public static final int USER_INFO_CLASS = 4352;
        public static final int USER_INFO_NUMBER = 4353;
        public static final int USER_INFO_GENDER = 4354;
        public static final int USER_INFO_GRADE = 4355;
        public static final int USER_INFO_AWARD_EXPERIENCE =  4356;
        public static final int USER_INFO_TEACHING_YEARS =  4357;

        public static final int USER_INFO_PHONE = 4360;

        public static final int USER_AREA_CHANGE = 9001;
        public static final int USER_AREA_LOCATION = 9002;

        public static final int AUTH_SUBMIT = 10101;

        public static final int SELECT_SUBJECT = 10201;

        public static final int SEND_QUESTION = 11101;
        public static final int TEACHER_GET = 11102;
        public static final int STU_REFUSED = 11103;
        public static final int STU_ACCEPT = 11104;
        public static final int TEACHER_HANG = 11105;
        public static final int REFUSE_QUESTION = 11103;
        public static final int ACCEPT_QUESTION = 11104;
        public static final int FINISH_QUESTION = 11105;
        public static final int ORDER_FINISH = 11106;
        public static final int ORDER_DIALOG_DISMISS = 11107;
        public static final int HANG_POPUP_TIMER_OUT = 11108;

        public static final int EVALUATION_SUBMIT = 11201;
        public static final int DRAW_MESSAGE = 11301;
    }

    /**
     * 笔记本纸张规格
     */
    public enum PageFormat {
        PAGE_A3(1, "A3", 7016, 9921),
        PAGE_A4(2, "A4", 4960, 7040),
        PAGE_A5(3, "A5", 3126, 4673),
        PAGE_A6(4, "A6", 0, 0);

        private int format;
        private String name;
        private int width;
        private int height;

        PageFormat(int format, String name, int width, int height) {
            this.format = format;
            this.name = name;
            this.width = width;
            this.height = height;
        }

        public int getFormat() {
            return this.format;
        }

        public String getName() {
            return this.name;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }
    }


    /**
     * 笔记本规格代码
     */
    public static class Size {
        public static final int PAGE_TYPE_1_SIZE_X = 4800;//宽x
        public static final int PAGE_TYPE_1_SIZE_Y = 6800;//高y
        public static final int PAGE_TYPE_2_SIZE_X = 3100;//宽x
        public static final int PAGE_TYPE_2_SIZE_Y = 4500;//高y

        public static final int PAGE_SIZE_Y_OFFSET = 100;//高y

        //底部功能菜单坐标范围
        public static final int PAGE_TYPE_2_BOTTOM_MENU_TOP = 4410;//y
        public static final int PAGE_TYPE_2_BOTTOM_MENU_BOTTOM = 4570;//y
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
    public static class Page {
        public static final int PAGE_MAX_A5 = 300;
    }
}
