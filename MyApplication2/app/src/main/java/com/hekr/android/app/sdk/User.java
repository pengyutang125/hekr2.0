package com.hekr.android.app.sdk;

/**
 * Created by hekr_xm on 2015/10/13.
 */
public class User {
    private String uid;
    private String userAccessKey;
    private String deviceAccessKey;

    public User() {
    }

    public User(String uid, String userAccessKey,String deviceAccessKey) {
        this.uid = uid;
        this.userAccessKey = userAccessKey;
        this.deviceAccessKey=deviceAccessKey;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserAccessKey() {
        return userAccessKey;
    }

    public void setUserAccessKey(String userAccessKey) {
        this.userAccessKey = userAccessKey;
    }

    public String getDeviceAccessKey() {
        return deviceAccessKey;
    }

    public void setDeviceAccessKey(String deviceAccessKey) {
        this.deviceAccessKey = deviceAccessKey;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", userAccessKey='" + userAccessKey + '\'' +
                ", deviceAccessKey='" + deviceAccessKey + '\'' +
                '}';
    }
}
