package com.eningqu.aipen.common;

import android.os.Environment;

import com.eningqu.aipen.R;

import java.io.File;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/1/17 20:16
 */

public class Constant {

    public final static String BASE_URL = "https://api.douban.com/v2/";
    public final static String LANGUAGE = "lang";

    public final static String BLE_NAME = "BLE_NAME";
    public final static String BLE_MAC_ADDRESS = "BLE_MAC_ADDRESS";
    //    public static final String SHARE_PATH_JPG = Environment.getExternalStorageDirectory().getPath() + File.separator + "aipen_share.png";
//    private static final String SHARE_PATH_PDF = Environment.getExternalStorageDirectory().getPath() + File.separator;
    public final static String NOTEBOOK_NUM_ID = "NOTEBOOK_NUM_ID";
    public final static String PAGE_NUM_ID = "PAGE_NUM_ID";

    public final static int BLE_SCAN_DUARTION = 30 * 1000;
    public final static int PEN_BATTERY_ALARM_VALUE = 30;
    public final static String DEF_SHORT_NAME = "zh_CN";

    /**
     * 画图
     */
    public final static int DRAW_CODE = 10001;
    /**
     * 长按
     */
    public final static int NOTE_LONG_CLICK_CODE = 10002;
    /**
     * 确认
     */
    public final static int CONFIRM_CODE = 10003;
    /**
     * 页码长按删除
     */
    public final static int PAGE_LONG_DELETE_CODE = 10004;
    /**
     * 页码点击加载
     */
    public static final int PAGE_CLICK_CODE = 10005;
    /**
     * 笔记本重命名
     */
    public static final int NOTE_RENAME_CODE = 10006;
    public static final int NOTE_RENAME_CODE_COLLECT = 10010;
    /**
     * 笔记本长按删除
     */
    public static final int NOTE_BOOK_LONG_DELETE_CODE = 10007;
    /**
     * 页签点击
     */
    public static final int PAGE_LABEL_CLICK_CODE = 10008;
    /**
     * 笔记本收藏
     */
    public static final int NOTE_COLLECT_CODE = 10009;
    /**
     * 有历史数据
     */
    public static final int HAS_HISTORY_CODE = 30001;
    /**
     * 接收历史数据完成
     */
    public static final int RECEIVE_HISTORY_FINISH_CODE = 30002;
    /**
     * 删除历史数据完成
     */
    public static final int DELETE_HISTORY_FINISH_CODE = 30003;
    /**
     * 获取电量
     */
    public static final int GET_BATTERY_CODE = 30004;
    /**
     * 电量
     */
    public final static int POWER_CODE = 30013;
    /**
     * 蓝牙连接中断
     */
    public static final int BLE_INTERRUPT_CODE = 30005;
    /**
     * 蓝牙连接成功
     */
    public static final int BLE_CONNECT_SUCCESS_CODE = 30006;
    /**
     * 关闭dialog
     */
    public static final int CLOSE_DIALOG_CODE = 30007;
    /**
     * 开启实时书写
     */
    public static final int START_REAL_WRITE_CODE = 30008;
    /**
     * 换页
     */
    public static final int SWITCH_PAGE_CODE = 30009;
    /**
     * 打开笔记本
     */
    public static final int OPEN_NOTEBOOK_CODE = 30010;
    /**
     * 打开笔记本
     */
    public static final int OPEN_NOTEBOOK_BY_SEARCH_CODE = 30011;
    /**
     * 功能指令
     */
    public static final int FUNCTION_COMMAND_CODE = 30012;
    /**
     * 退出
     */
    public static final int USER_LOGOUT = 40001;
    /**
     * 登录
     */
    public static final int USER_LOGIN = 40002;
    /**
     * 凝趣授权
     */
    public static final int NQ_SER_AUTH_SUCCESS = 40003;
    public static final int NQ_SER_AUTH_FAIL = 40004;

    public static final int ZIP_UPDATE = 200011;
    public static final int ZIP_UPDATE_SUCCESS = 20002;
    public static final int ZIP_DOWN = 20003;
    public static final int REFRESH_DATA = 400001;
    public static final int BLE_FOUND_DEVICE = 400002;
    public static final int BLE_START_FOUND_DEVICE = 400003;

    public static final int MIGRATION_SENT = 50001;
    public static final int GET_RECOGNIZE_RESULT = 60001;

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
     * SP key
     */

    public static final String SP_KEY_LANGUAGE = "sp_key_language";
    public static final String SP_KEY_TO_LANGUAGE = "sp_key_to_language";
    public static final String SP_KEY_RECO_LANGUAGE = "sp_key_reco_language";
    public static final String SP_KEY_OUTLINE_PROGRESS = "sp_key_outline_progress";
//    public static final String SP_KEY_RECO_SETTING = "sp_key_reco_setting";

    public static final String SP_KEY_SYNC = "sp_key_sync";
    public static final String SP_KEY_PERMISSION = "sp_key_permission";
    public static final String SP_KEY_MYSCRIPT_INSTALL = "sp_key_myscript_install";

    public static final String SP_KEY_INIT_PEN = "sp_key_init_pen";
    public static final String SP_KEY_AUTH_PEN = "sp_key_auth_pen";
    public static final String SP_KEY_AUTH_PEN_TIME = "sp_key_auth_pen_time";

    public static final String SP_KEY_USER_AGREE = "sp_key_user_agree";
    public final static String pkg = "com.eningqu.aipen";

    public static final int[] BOOK_COVERS = {
            R.drawable.notebook_1,
            R.drawable.notebook_2,
            R.drawable.notebook_3,
            R.drawable.notebook_4,
            R.drawable.notebook_5,
            R.drawable.notebook_6,
            R.drawable.notebook_7,
            R.drawable.notebook_8,
            R.drawable.notebook_9,
            R.drawable.notebook_10
    };

    public static final String[] RECORD_SHORT = {
            "af_ZA", "sq_AL", "hy_AM", "az_AZ", "eu_ES", "be_BY", "bg_BG", "ca_ES", "zh_CN", "zh_HK", "zh_TW", "hr_HR", "cs_CZ", "da_DK", "nl_BE", "nl_NL", "en_CA", "en_GB", "en_US", "et_EE",
            "fi_FI", "fr_CA", "fr_FR", "gl_ES", "ka_GE", "de_AT", "de_DE", "el_GR", "hu_HU", "is_IS", "id_ID", "ga_IE", "it_IT", "ja_JP", "kk_KZ", "ko_KR", "lv_LV", "lt_LT", "mk_MK", "ms_MY",
            "mn_MN", "no_NO", "pl_PL", "pt_BR", "pt_PT", "ro_RO", "ru_RU", "sr_Cyrl_RS", "sr_Latn_RS", "sk_SK", "sl_SI", "es_MX", "es_ES", "sv_SE", "tt_RU", "tr_TR", "uk_UA", "vi_VN"
    };

    public static final String[] RECORD_REAL_NAMES = {
            "IsiBhulu - eMzantsi Afrika", "Shqiptar - Shqiperi", "Հայ - Հայաստան", "Azəri - Azərbaycan", "Euskara - Espainia", "Па-беларуску - Беларусь",
            "Български - България", "Català - Espanya", "中文简体", "中文粵語", "中文繁體", "Hrvatski - Hrvatska", "Česko - Česká republika",
            "Dansk - Danmark", "Nederlands - België", "Nederlands - Nederland", "English - Canada", "English - United Kingdom", "English - United States", "Eesti keel - Eesti",
            "Suomi - Suomi", "Français - Canada", "Français - France", "Галисийски - Испания", "ქართული - საქართველო", "Deutsch - Österreich",
            "Deutsch - Deutschland", "Ελληνική - Ελλάδα", "Magyar - magyar", "Íslensk - Ísland","Indonesia - Indonesia", "Éire - Éire",  "Italiano - italia",
            "日本語-日本", "Қазақ - Қазақстан", "한국-한국", "Latvietis - Latvija", "Lietuvis - Lietuva", "Македонски - Македонија", "Melayu - Malaysia",
            "Монгол - Монгол", "Norsk - Norge", "Polski - polski", "Português - brasil", "Português - portugal", "Română - România", "Русский - россия",
            "Српски (ћирилица) - Србија", "Српски (латински) - Србија", "Slovensko - Slovensko", "Slovenščina - Slovenija", "Español - México",
            "Español - España", "Svenska - Sverige", "Татарча", "Türkçe - türkiye", "Українська - Україна", "Việt nam"
    };

    public static final String[] RECORD_SIZE = {
            "4.39MB", "3.99MB", "4.21MB", "3.81MB", "4.11MB", "3.76MB", "4.76MB", "3.62MB", "27.6MB", "26.3MB", "27.4MB", "4.30MB", "4.40MB", "3.67MD", "4.56MB", "4.21MB", "3.42MB",
            "5.82MB", "5.94MB", "4.55MB", "4.96MB", "3.80MB", "3.99MB", "4.30MB", "4.77MB", "4.28MB", "4.71MB", "5.34MB", "5.10MB", "4.52MB", "4.42MB", "3.46MB", "4.19MB", "17.8MB",
            "4.08MB", "17.5MB", "4.40MB", "3.87MB", "4.06MB", "4.66MB", "3.37MB", "4.54MB", "4.62MB", "3.89MB", "3.77MB", "3.50MB", "5.31MB", "4.39MB", "4.46MB", "4.76MB", "4.56MB",
            "4.28MB", "3.87MB", "4.46MB", "3.77MB", "4.25MB", "4.29MB", "3.71MB"
    };

}
