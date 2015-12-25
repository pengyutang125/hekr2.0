package com.hekr.android.app.entity;

import com.hekr.android.app.sdk.User;

/**
 * Created by hekr_xm on 2015/9/28.
 */
public class Global {

    //用户key
    public static String userAccesskey=null;
    //设备key
    public static String deviceAccesskey=null;

    //update
    //页面模板是否在更新中,默认不在更新中
    public static boolean unUpdating=true;

    //距离上次更新是否超过24小时,默认没超过24小时
    public static boolean SurpassUpdateTime=false;

    //当前网络状态，默认无网
    public static boolean netWorkState=false;

    //访问服务器更新接口是否成功,默认成功
    public static boolean successGetValue=true;

    //从服务器端获取的列表与数据库中列表进行对比判断是否要更新,默认不需要
    public static boolean compareUpdate=false;

    public static User user=null;

    //download
    public static boolean downloadSuccess=false;//下载更新的内容是否成功，默认不成功

}
