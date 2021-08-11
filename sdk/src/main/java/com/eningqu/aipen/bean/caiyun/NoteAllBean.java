package com.eningqu.aipen.bean.caiyun;

import java.util.List;

public class NoteAllBean {
    public List<NoteBean> normalList;//正常笔记列表，即不在回收站的笔记的列表
    public List<NoteBean> recycleBinList;//回收站列表，在回收站中的笔记的列表
}
