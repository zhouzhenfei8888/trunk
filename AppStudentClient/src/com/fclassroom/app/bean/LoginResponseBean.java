package com.fclassroom.app.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2015/3/23.
 */
public class LoginResponseBean implements Serializable {
    int id;
    String name;
    String nickname;
    String jikeNum;
    String loginEmail;
    String loginAccount;
    String loginPhone;
    //    String loginPwd;
    int accountType;
    String studentRoll;
    String studentNo;
    String sex;
    //    String barcodePic;
//    int sequence;
    int status;
    //    int isLock;
    Date lastLoginTime;
    //    String lastLoginIp;
//    int loginCount;
//    String accessTokenApp;
//    String accessTokenWeb;
//    String accessTokenNet;
    String accessToken;
    //    Date expiresIn;
    String expiresInDate;
    //    String homedirectory;
//    Date createTime;
//    Date updateTime;
//    int createUserId;
    String openIdQQ;
    //    String accountInfoList;
    int subscribe;
    String openIdWX;
    String city;
    String country;
    String province;
    String language;
    String headimgurl;
    String latitude;
    String longitude;
    String precision;
    String scale;
    String label;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getJikeNum() {
        return jikeNum;
    }

    public void setJikeNum(String jikeNum) {
        this.jikeNum = jikeNum;
    }

    public String getLoginEmail() {
        return loginEmail;
    }

    public void setLoginEmail(String loginEmail) {
        this.loginEmail = loginEmail;
    }

    public String getLoginAccount() {
        return loginAccount;
    }

    public void setLoginAccount(String loginAccount) {
        this.loginAccount = loginAccount;
    }

    public String getLoginPhone() {
        return loginPhone;
    }

    public void setLoginPhone(String loginPhone) {
        this.loginPhone = loginPhone;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public String getStudentRoll() {
        return studentRoll;
    }

    public void setStudentRoll(String studentRoll) {
        this.studentRoll = studentRoll;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getOpenIdQQ() {
        return openIdQQ;
    }

    public void setOpenIdQQ(String openIdQQ) {
        this.openIdQQ = openIdQQ;
    }

    public String getExpiresInDate() {
        return expiresInDate;
    }

    public void setExpiresInDate(String expiresInDate) {
        this.expiresInDate = expiresInDate;
    }

    public int getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(int subscribe) {
        this.subscribe = subscribe;
    }

    public String getOpenIdWX() {
        return openIdWX;
    }

    public void setOpenIdWX(String openIdWX) {
        this.openIdWX = openIdWX;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPrecision() {
        return precision;
    }

    public void setPrecision(String precision) {
        this.precision = precision;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
}
