package com.eningqu.aipen.common;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/7/6 10:37
 * desc   : 语种
 * version: 1.0
 */
public enum LanguageNQEnum {
    EN_USA(9, "str_en_us", "English - United States", "USA","en"),
    EN_EN(8, "str_en_en", "English - UK", "GBR","en"),
    JP_JP(22, "str_ja", "日本語", "JPN","ja"),
    KR_KR(23, "str_ko", "한국어", "KOR","ko"),
    CN_ZH(36, "str_zh_cn", "中文 - 简体", "CHN","zh"),
    CN_HK(37, "str_zh_hk", "中文 - 繁体", "CHN","cht"),
    DE_DE(6, "str_de", "Deutsch", "DEU","de"),
    FR_FR(15, "str_fr_fr", "Français - France", "FRA","fr"),
    RU_RU(30, "str_ru", "Русский", "RUS","ru"),
    ES_SP(11, "str_es_es", "Español - españa", "ESP","es"),
    IT_ITA(20, "str_yidali", "lingua italiana", "ITA","it"),
    PT_PT(28, "str_pt", "Português - Portugal", "PRT","pt"),
    AR_EG(1, "str_alabo_aiji", "العربية - مصر", "EGY","ar"),
    SE_SE(32, "str_sv", "Svenska", "SWE","sv"),
    TH_TH(33, "str_thai", "ไทย", "THA","th"),
    VN_VN(35, "str_vi", "Tiếng Việt", "VNM","vi"),
    NL_NL(24, "str_helan", "Nederlands", "NLD","nl"),
    NO_NO(25, "str_no", "norsk språk", "NOR","no"),
    PL_PL(26, "str_pl", "Polski", "POL","pl"),
    FI_FI(13, "str_fin", "Suomi", "FIN","fi"),
    HU_HU(18, "str_hu", "Magyar", "HUN","hu"),
    TR_TR(34, "str_tur", "Türkçe", "TUR","tr"),
    DK_DK(39, "str_dm", "Dansk", "DNK","da"),
    GR_GR(40, "str_el", "Ελληνικά ", "GRC","el"),
    MY_MY(42, "str_my", "Melayu", "MYS","ms"),
    UK_UK(41, "str_uk", "Українська", "UKR","uk"),
    ID_ID(19, "str_yindunixiya", "Indonesia", "IDN","id"),
    CA_AES(4, "str_jiatailuoniya", "Català", "ESP2","ca"),
    CS_CZ(5, "str_jianke", "Česky", "CZE","cs"),
    IN_IN(16, "str_yindi", "हिन्दी", "IND","hi"),
    HR_HR(17, "str_keluodiya", "Hrvatski", "HRV","hr"),
    IL_IL(21, "str_xibolai", "עברית", "ISR","he"),
    RO_RO(29, "str_pt_luoma", "românesc", "ROU","ro"),
    SK_SK(31, "str_sk", "Slovenčina", "SVK","sk"),;

    /*AR_SA(2, "str_alabo_shate", "العربية - السعودية", "SAU","ar"),
    AR_UAE(3, "str_alabo_alianqiu", "العربية - الدولية", "XWW","ar"),
    AR_AIL(43, "str_alabo_yiselie", "العربية - إسرائيل", "ISR","ar"),
    AR_ARJ(44, "str_alabo_yuedan", "العربية - الاردن", "ARJ","ar"),
    AR_ARB(45, "str_alabo_balin", "جزيره العرب - البحرين", "ARB","ar"),
    AR_AR0(46, "str_alabo_aman", "جزيره العرب - عمان", "ARO","ar"),
    AR_ARD(47, "str_alabo_aerjiliya", "العربية - الجزائر", "ARD","ar"),
    AR_ARP(48, "str_alabo_balesitan", "العربية - الفلسطينية", "ARP","ar"),
    AR_ARI(49, "str_alabo_yilake", "العربية - العراق", "ARI","ar"),
    AR_ARK(50, "str_alabo_keweite", "العربية - الكويت", "ARK","ar"),
    AR_ARQ(51, "str_alabo_kataer", "العربية - قطر", "ARQ","ar"),
    AR_ARM(52, "str_alabo_mologe", "العربية - المغرب", "ARM","ar"),
    AR_ART(53, "str_alabo_tunisi", "العربي - تونس", "ART","ar"),
    AR_ARL(54, "str_alabo_libanen", "العربية - لبنان", "ARL","ar"),*/
//    EN_AU(7, "str_en_au", "English - Australia", "AUS","en"),
/*    EN_IND(10, "str_en_yin", "English - India", "IND","en"),
    EN_CA(55, "str_en_canada", "English - Canada", "CAN","en"),
    EN_NZ(56, "str_en_nzl", "English - new Zealand", "NZL","en"),
    EN_GH(57, "str_en_gn", "English - Ghana", "EGH","en"),
    EN_IE(58, "str_en_aerlan", "English - Ireland", "EIE","en"),
    EN_KE(59, "str_en_kenniya", "English - Kenya", "EKE","en"),
    EN_NG(60, "str_en_niriliya", "English - Nigeria", "ENG","en"),
    EN_PH(61, "str_en_feiliben", "English - Philippines", "EPH","en"),
    EN_ZA(62, "str_en_nanfei", "English - South Africa", "EZA","en"),
    EN_TZ(63, "str_en_tansan", "English - Tanzania", "ETZ","en"),*/
/*    ES_LT(12, "str_es_lading", "Español - latinoamerica", "ESP","es"),
    ES_AR(64, "str_es_agenting", "Español - Argentina", "EAR","es"),
    ES_BO(65, "str_es_boliweiya", "Español - Bolivia", "EBO","es"),
    ES_CL(66, "str_es_zhili", "Español - Chile", "ECL","es"),
    ES_SV(67, "str_es_saerwaduo", "Español - Salvador", "ESV","es"),
    ES_GT(68, "str_es_weidimala", "Español - Guatemala", "EGT","es"),
    ES_CO(69, "str_es_gelunbiya", "Español - Colombia", "ECO","es"),
    ES_CR(70, "str_es_gesidalijia", "Español - Costa rica", "ECR","es"),
    ES_EC(71, "str_es_erguaduoer", "Español - Ecuador", "EEC","es"),
    ES_US(72, "str_es_usa", "Español - Estados Unidos", "USA","es"),
    ES_HN(73, "str_es_hongdu", "Español - Honduras", "EHN","es"),
    ES_MX(74, "str_es_mxg", "Español - Mexico", "EMX","es"),
    ES_NI(75, "str_es_njlg", "Español - Nicaragua", "ENI","es"),
    ES_PA(76, "str_es_banama", "Español - Panama", "EPA","es"),
    ES_PY(77, "str_es_balagui", "Español - Paraguay", "EPY","es"),
    ES_PE(78, "str_es_bilu", "Español - Peru", "EPE","es"),
    ES_PR(79, "str_es_boduolige", "Español - Puerto Rico", "EPR","es"),*/
//    FR_CA(14, "str_fr_ca", "Français - Canada", "CAN","fr"),
//    NL_ZA(80, "str_helan_za", "Nederlands-Zuid-Afrika", "EZA","nl"),
//    PT_BR(27, "str_pt_ba", "Português - Brasil", "BRA","pt"),
//    CN_YUE(38, "str_zh_yue", "中文 - 粤语", "CHN","yue"),;

    private int code;      //凝趣标准语言数字码
    private String name;      //语种描述
    private String name0;     //名称
    private String png;       //图片
    private String short_name;//语言码

    LanguageNQEnum(int code, String name, String name0, String png, String short_name) {
        this.code = code;
        this.name = name;
        this.name0 = name0;
        this.png = png;
        this.short_name = short_name;
    }

    public static LanguageNQEnum get(int code) {
        for (LanguageNQEnum microsoft : values()) {
            if (microsoft.code == code) {
                return microsoft;
            }
        }
        return null;
    }

    /**
     * TODO 语种码是否存在
     *
     * @param code
     * @return
     */
    public static boolean isContains(int code) {
        for (LanguageNQEnum microsoft : values()) {
            if (microsoft.getCode() == code) {
                return true;
            }
        }
        return false;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getName0() {
        return name0;
    }

    public String getPng() {
        return png;
    }

    public String getShort_name() {
        return short_name;
    }

}
