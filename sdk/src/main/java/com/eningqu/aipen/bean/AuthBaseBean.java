package com.eningqu.aipen.bean;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/7/6 14:13
 * desc   : 授权返回结果基类
 * version: 1.0
 */
public class AuthBaseBean extends ApiBaseBean{

    /**
     * msg : 设备可用
     * code : 0
     * data : {"engine":true,"url":"[{\"domain\":\"speech1.dstsoft.net\",\"type\":2,\"version\":3},{\"domain\":\"speech1.dstsoft.net\",\"type\":3,\"version\":3},{\"domain\":\"speech1.dstsoft.net\",\"type\":1,\"version\":3}]"}
     * success : true
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * engine : true
         * url : [{"domain":"speech1.dstsoft.net","type":2,"version":3},{"domain":"speech1.dstsoft.net","type":3,"version":3},{"domain":"speech1.dstsoft.net","type":1,"version":3}]
         */

        private boolean engine;
        private String url;

        public boolean isEngine() {
            return engine;
        }

        public void setEngine(boolean engine) {
            this.engine = engine;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
