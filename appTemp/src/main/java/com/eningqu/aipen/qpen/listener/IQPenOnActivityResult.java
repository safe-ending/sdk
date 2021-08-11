package com.eningqu.aipen.qpen.listener;

import android.content.Intent;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/8/6 18:06
 * desc   :
 * version: 1.0
 */
public interface IQPenOnActivityResult {
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
