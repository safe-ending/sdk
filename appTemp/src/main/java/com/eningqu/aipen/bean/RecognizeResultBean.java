package com.eningqu.aipen.bean;

import java.util.List;

public class RecognizeResultBean {


    /**
     * msg : 请求成功
     * code : 1
     * data : [{"id":7,"pkgName":"com.eningqu.aipen","appVersion":"1.0.0","languageCode":"af_ZA","type":1,"myScriptVersion":"1.0.0","myScriptResource":"http://image.eningqu.com//207af84751074ec8831eef233284ff19.zip?e=1577601595&token=BAxoLPICcnXc_AFep9Aity4-1vnJh5EfiTQ0hYbs:KmDdZrw-V9-mokp20y9ZVdjxHt8=","createUserId":null,"updateUserId":null}]
     * success : true
     */

    private String msg;
    private int code;
    private boolean success;
    private List<DataBean> data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 7
         * pkgName : com.eningqu.aipen
         * appVersion : 1.0.0
         * languageCode : af_ZA
         * type : 1
         * myScriptVersion : 1.0.0
         * myScriptResource : http://image.eningqu.com//207af84751074ec8831eef233284ff19.zip?e=1577601595&token=BAxoLPICcnXc_AFep9Aity4-1vnJh5EfiTQ0hYbs:KmDdZrw-V9-mokp20y9ZVdjxHt8=
         * createUserId : null
         * updateUserId : null
         */

        private int id;
        private String pkgName;
        private String appVersion;
        private String languageCode;
        private int type;
        private String myScriptVersion;
        private String myScriptResource;
        private Object createUserId;
        private Object updateUserId;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPkgName() {
            return pkgName;
        }

        public void setPkgName(String pkgName) {
            this.pkgName = pkgName;
        }

        public String getAppVersion() {
            return appVersion;
        }

        public void setAppVersion(String appVersion) {
            this.appVersion = appVersion;
        }

        public String getLanguageCode() {
            return languageCode;
        }

        public void setLanguageCode(String languageCode) {
            this.languageCode = languageCode;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getMyScriptVersion() {
            return myScriptVersion;
        }

        public void setMyScriptVersion(String myScriptVersion) {
            this.myScriptVersion = myScriptVersion;
        }

        public String getMyScriptResource() {
            return myScriptResource;
        }

        public void setMyScriptResource(String myScriptResource) {
            this.myScriptResource = myScriptResource;
        }

        public Object getCreateUserId() {
            return createUserId;
        }

        public void setCreateUserId(Object createUserId) {
            this.createUserId = createUserId;
        }

        public Object getUpdateUserId() {
            return updateUserId;
        }

        public void setUpdateUserId(Object updateUserId) {
            this.updateUserId = updateUserId;
        }
    }
}
