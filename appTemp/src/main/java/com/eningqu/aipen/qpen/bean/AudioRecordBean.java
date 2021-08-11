package com.eningqu.aipen.qpen.bean;

import java.util.Objects;

/**
 * @author Zhenglijia
 * @filename AudioRecordBean
 * @date 2019/4/22
 * @email zlj@eningqu.com
 **/
public class AudioRecordBean {
    public String name;//文件名称
    public String filePath;//文件名称
    public long duration;//录音时长
    public long curTime;//当前播放时间
    public long createTime;//创建时间
    public int state;//播放状态 停止0 播放中1 暂停2
    public int postion;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AudioRecordBean that = (AudioRecordBean) o;
        return postion == that.postion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(postion);
    }
}
