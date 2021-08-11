package com.eningqu.aipen.common;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2018/12/12 10:52
 * desc   : EventBus消息载体
 * version: 1.0
 */
public class EventBusCarrier {
    private int eventType; //区分事件的类型
    private Object object;  //事件的实体类

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
