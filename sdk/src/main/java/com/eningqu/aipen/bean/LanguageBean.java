package com.eningqu.aipen.bean;

import android.graphics.drawable.Drawable;

public class LanguageBean {
    private String name;
    private String name0;
    private Drawable flag;
    private int code;

    public String getName0() {
        return name0;
    }

    public void setName0(String name0) {
        this.name0 = name0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Drawable getFlag() {
        return flag;
    }

    public void setFlag(Drawable flag) {
        this.flag = flag;
    }
}
