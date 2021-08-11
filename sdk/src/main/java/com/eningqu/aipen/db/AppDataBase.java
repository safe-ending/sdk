package com.eningqu.aipen.db;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/2/26 15:25
 */

@Database(name = AppDataBase.NAME, version = AppDataBase.VERSION)
public class AppDataBase {

    public static final String NAME = "AiPenDB";

    public static final int VERSION = 6;
}
