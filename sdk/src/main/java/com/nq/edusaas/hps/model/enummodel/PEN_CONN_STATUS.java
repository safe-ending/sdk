package com.nq.edusaas.hps.model.enummodel;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/3/13 17:43
 * desc   : 连接状态
 *     public static final int CONN_STATE_CLOSED = 0;
 *     public static final int CONN_STATE_CONNECTING = 1;
 *     public static final int CONN_STATE_CONNECTED = 2;
 *     public static final int CONN_STATE_DISCONNECTING = 3;
 * version: 1.0
 */
public enum PEN_CONN_STATUS {
    CLOSED(0), CONNECTING(1), CONNECTED(2),DISCONNECTED(3);

    private int state;
    PEN_CONN_STATUS(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public static PEN_CONN_STATUS getState(int state){
        switch(state){
            case 1:return CONNECTING;
            case 2:return CONNECTED;
            case 3:return DISCONNECTED;
        }
        return CLOSED;
    }
}
