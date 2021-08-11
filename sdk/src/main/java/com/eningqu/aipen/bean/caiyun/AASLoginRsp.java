package com.eningqu.aipen.bean.caiyun;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2021/7/8 13:38
 * desc   :
 * version: 1.0
 */
public class AASLoginRsp implements Serializable {

    /**
     * return : 0
     * imspwd : 0C69B2A95ED477206081809A2D8E428F
     * sbc : 120.197.30.17:5060
     * domain : 139.gd.chinamobile.com
     * svnlist : 218.204.253.99:443
     * svnuser : test
     * svnpwd : huawei
     * htslist : 58.67.220.244:80,58.67.220.244:8080
     * userType : 1
     * userid : 1611TWM8Y2t6
     * loginid : 162571729856234
     * heartime : 4
     * funcId : 1000000000000000
     * token : pBcV8Orl|1|RCS|1628309298734|l6aGYkUd_EY9pq0Y9tMBql8mgbo9xSR8Drcl3w7Bti48R0jwXHP4IgSXCLfzDr40ybrQMVs.daZakTrYXqHGiYiay.DE.7RTEjBKar69vFltD0Vp_5qjU8yA3YjimVG8v._1a5TstEIpEBXVnADahdbk1LNtv4YB3nD4d4oeQvM-
     * expiretime : 2592000
     * authToken : pBcV8Orl|1|RCS|1628309298734|l6aGYkUd_EY9pq0Y9tMBql8mgbo9xSR8Drcl3w7Bti48R0jwXHP4IgSXCLfzDr40ybrQMVs.daZakTrYXqHGiYiay.DE.7RTEjBKar69vFltD0Vp_5qjU8yA3YjimVG8v._1a5TstEIpEBXVnADahdbk1LNtv4YB3nD4d4oeQvM-
     * atExpiretime : 2592000
     * deviceid : 105895BDBDFA4B78A2DA497E8F7C9E2B
     * serverinfo : {"rifurl":"http://ose.caiyun.feixin.10086.cn:80/richlifeApp","calURL":"http://ose.caiyun.feixin.10086.cn/richlifeApp","testTermConnectURL":"http://aas.caiyun.feixin.10086.cn/tellin/usr/puc/ispace/testTermConnect.do"}
     * account : 13417524335
     * expiryDate : -1
     * areaCode : 755
     * provCode : 20
     * userExtInfo : {"passID":"616850707","isRegWeibo":"-1","accessToken":"MzA5OEE1MzI4RkE2ODQwQkVGQ0Y1NTNGQzZBQ0MwOUI0NzJGQkVBMUI0MDQ0OTY1NTY1QTM4MDE4MkNGODM3NTo0NTIzMTY="}
     * srvInfoVer : 4FAB44DF38773C9FD603CAC7B51F79B3
     */

    @SerializedName("return")
    private String error;
    private String desc;
    private String imspwd;
    private String sbc;
    private String domain;
    private String svnlist;
    private String svnuser;
    private String svnpwd;
    private String htslist;
    private String userType;
    private String userid;
    private String loginid;
    private String heartime;
    private String funcId;
    private String token;
    private String expiretime;
    private String authToken;
    private String atExpiretime;
    private String deviceid;
    private String account;
    private String expiryDate;
    private String areaCode;
    private String provCode;
    private String srvInfoVer;
    private ServerinfoBean serverinfo = new ServerinfoBean();
    private UserExtInfoBean userExtInfo = new UserExtInfoBean();

    public String getError() {
        return error;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getImspwd() {
        return imspwd;
    }

    public void setImspwd(String imspwd) {
        this.imspwd = imspwd;
    }

    public String getSbc() {
        return sbc;
    }

    public void setSbc(String sbc) {
        this.sbc = sbc;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSvnlist() {
        return svnlist;
    }

    public void setSvnlist(String svnlist) {
        this.svnlist = svnlist;
    }

    public String getSvnuser() {
        return svnuser;
    }

    public void setSvnuser(String svnuser) {
        this.svnuser = svnuser;
    }

    public String getSvnpwd() {
        return svnpwd;
    }

    public void setSvnpwd(String svnpwd) {
        this.svnpwd = svnpwd;
    }

    public String getHtslist() {
        return htslist;
    }

    public void setHtslist(String htslist) {
        this.htslist = htslist;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getLoginid() {
        return loginid;
    }

    public void setLoginid(String loginid) {
        this.loginid = loginid;
    }

    public String getHeartime() {
        return heartime;
    }

    public void setHeartime(String heartime) {
        this.heartime = heartime;
    }

    public String getFuncId() {
        return funcId;
    }

    public void setFuncId(String funcId) {
        this.funcId = funcId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpiretime() {
        return expiretime;
    }

    public void setExpiretime(String expiretime) {
        this.expiretime = expiretime;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAtExpiretime() {
        return atExpiretime;
    }

    public void setAtExpiretime(String atExpiretime) {
        this.atExpiretime = atExpiretime;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public ServerinfoBean getServerinfo() {
        return serverinfo;
    }

    public void setServerinfo(ServerinfoBean serverinfo) {
        this.serverinfo = serverinfo;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getProvCode() {
        return provCode;
    }

    public void setProvCode(String provCode) {
        this.provCode = provCode;
    }

    public UserExtInfoBean getUserExtInfo() {
        return userExtInfo;
    }

    public void setUserExtInfo(UserExtInfoBean userExtInfo) {
        this.userExtInfo = userExtInfo;
    }

    public String getSrvInfoVer() {
        return srvInfoVer;
    }

    public void setSrvInfoVer(String srvInfoVer) {
        this.srvInfoVer = srvInfoVer;
    }

    public static class ServerinfoBean implements Serializable {
        /**
         * rifurl : http://ose.caiyun.feixin.10086.cn:80/richlifeApp
         * calURL : http://ose.caiyun.feixin.10086.cn/richlifeApp
         * testTermConnectURL : http://aas.caiyun.feixin.10086.cn/tellin/usr/puc/ispace/testTermConnect.do
         */

        private String rifurl;
        private String calURL;
        private String testTermConnectURL;

        public String getRifurl() {
            return rifurl;
        }

        public void setRifurl(String rifurl) {
            this.rifurl = rifurl;
        }

        public String getCalURL() {
            return calURL;
        }

        public void setCalURL(String calURL) {
            this.calURL = calURL;
        }

        public String getTestTermConnectURL() {
            return testTermConnectURL;
        }

        public void setTestTermConnectURL(String testTermConnectURL) {
            this.testTermConnectURL = testTermConnectURL;
        }
    }

    public static class UserExtInfoBean implements Serializable {
        /**
         * passID : 616850707
         * isRegWeibo : -1
         * accessToken : MzA5OEE1MzI4RkE2ODQwQkVGQ0Y1NTNGQzZBQ0MwOUI0NzJGQkVBMUI0MDQ0OTY1NTY1QTM4MDE4MkNGODM3NTo0NTIzMTY=
         */

        private String passID;
        private String isRegWeibo;
        private String accessToken;

        public String getPassID() {
            return passID;
        }

        public void setPassID(String passID) {
            this.passID = passID;
        }

        public String getIsRegWeibo() {
            return isRegWeibo;
        }

        public void setIsRegWeibo(String isRegWeibo) {
            this.isRegWeibo = isRegWeibo;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
    }
}
