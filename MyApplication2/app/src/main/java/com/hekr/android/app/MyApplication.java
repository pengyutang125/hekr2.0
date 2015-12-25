package com.hekr.android.app;

import android.app.Application;
import android.content.Context;


/**
 * Created by hekr_xm on 2015/9/28.
 */
public class MyApplication extends Application {

    //用户唯一标识
    private String userAccesskey;
    //设备唯一标识
    private String deviceAccesskey;
    //是否在更新中
    private boolean isUpdating;
    //距离上次更新是否超过24小时
    private boolean isSurpassUpdateTime;
    //当前网络状态
    private boolean nowInternet;

    private Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        //LeakCanary.install(this);
    }

}
