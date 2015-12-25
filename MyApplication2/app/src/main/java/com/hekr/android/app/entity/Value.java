package com.hekr.android.app.entity;

/**
 * Created by hekr_xm on 2015/9/28.
 */
public class Value {
    private String name;
    private String version;
    private String hash;
    private String url;

    public Value() {
    }

    public Value(String name, String version, String hash, String url) {
        this.name = name;
        this.version = version;
        this.hash = hash;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Value{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", hash='" + hash + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
