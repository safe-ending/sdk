package com.eningqu.aipen.qpen.listener;

import com.eningqu.aipen.db.model.NoteBookData;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/5/29 21:34
 * desc   :
 * version: 1.0
 */
public interface IQPenCreateNotebookListener {
    void onSuccessful(NoteBookData noteBookData);
    void onFail();
}
