package com.hekr.android.app.entity;

/**
 * Created by hekr_xm on 2015/9/28.
 */
public class Template {
    private String name;
    private String version;

    public Template() {
    }

    public Template(String name, String version) {
        this.name = name;
        this.version = version;
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

    @Override
    public String toString() {
        return "{"
                +"\"name\""+":"+"\""+ name + "\"" +","
                +"\"version\""+":"+"\"" + version + "\""
                + "}";
    }
}
