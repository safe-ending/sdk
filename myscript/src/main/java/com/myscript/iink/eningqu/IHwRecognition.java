package com.myscript.iink.eningqu;

import com.myscript.iink.eningqu.bean.MyScriptRealBean;

import java.util.List;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/7/4 20:24
 * desc   :
 * version: 1.0
 */
public interface IHwRecognition {
    void onHwReco(String result,MyScriptRealBean myScriptRealBean);
    void onError(String error);
}
