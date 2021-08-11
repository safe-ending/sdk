package com.eningqu.aipen.bean;

public class RecogLanguageBean {

    private String name;
    private String name0;
    private String size;
    private String shortName;
    private int langCode;

    private boolean isDown;
    private long downLoadSize;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName0() {
        return name0;
    }

    public void setName0(String name0) {
        this.name0 = name0;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public int getLangCode() {
        return langCode;
    }

    public void setLangCode(int langCode) {
        this.langCode = langCode;
    }

    public boolean isDown() {
        return isDown;
    }

    public void setDown(boolean down) {
        isDown = down;
    }

    public long getDownLoadSize() {
        return downLoadSize;
    }

    public void setDownLoadSize(long downLoadSize) {
        this.downLoadSize = downLoadSize;
    }
}
