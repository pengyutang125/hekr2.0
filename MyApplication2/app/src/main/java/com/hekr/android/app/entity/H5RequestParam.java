package com.hekr.android.app.entity;

import java.util.ArrayList;

/**
 * Created by hekr_xm on 2015/9/29.
 */
public class H5RequestParam {
    private String accesskey;
    private String _csrftoken_;
    private ArrayList<Template> template;
    private String platform;

    public H5RequestParam() {
    }

    public H5RequestParam(String accesskey, String platform, ArrayList<Template> templateList, String _csrftoken_) {
        this.accesskey = accesskey;
        this.platform = platform;
        this.template = templateList;
        this._csrftoken_ = _csrftoken_;
    }

    public String getAccesskey() {
        return accesskey;
    }

    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }

    public String get_csrftoken_() {
        return _csrftoken_;
    }

    public void set_csrftoken_(String _csrftoken_) {
        this._csrftoken_ = _csrftoken_;
    }

    public ArrayList<Template> getTemplate() {
        return template;
    }

    public void setTemplate(ArrayList<Template> template) {
        this.template = template;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @Override
    public String toString() {
        return "H5RequestParam{" +
                "accesskey='" + accesskey + '\'' +
                ", _csrftoken_='" + _csrftoken_ + '\'' +
                ", template=" + template +
                ", platform='" + platform + '\'' +
                '}';
    }
}
