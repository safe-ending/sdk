package com.eningqu.aipen.common.utils;

import com.eningqu.aipen.db.model.UserInfoData;
import com.raizlabs.android.dbflow.sql.language.SQLite;

public class UserManager {

    private static UserInfoData userInfo;

    /**
     * 登录用户信息
     *
     * @return
     */
    public static UserInfoData loadUserInfo() {
        if (userInfo == null) {
            userInfo = SQLite.select().from(UserInfoData.class).querySingle();
        }
        return userInfo;
    }

    public static void exitUser() {
        userInfo = null;
    }
}
