package com.hekr.android.app.interfaceImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hekr.android.app.Interface.DeviceManage;
import com.hekr.android.app.util.HekrConfig;
import com.hekr.android.app.util.HttpUtil;

import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by kj on 15/7/24.
 */
public class HekrUser{

    private static final String TAG="HekrUser";

    private Random r = new Random();
    private String ucookie;
    private String _csrftoken_ = "_csrftoken_=abcd";
    private String cookie;
    public  String httphostcn = "http://user.hekr.me";
    public  String httphost = null;
    private static HekrUser hekrUser = null;
    private HekrConfig hc;

    public interface HekrCallBackHandle {
        public void callback(long randomid, Object obj);
    }

    public static synchronized HekrUser getInstance(String ucookie) {
        if (hekrUser == null) {
            hekrUser = new HekrUser(ucookie);
        }
        return hekrUser;
    }

    public HekrUser(String ucookie) {
        r.setSeed(System.currentTimeMillis());
        this.httphost = httphostcn;
        this.ucookie = ucookie;
        this.cookie = ucookie+";"+_csrftoken_;
    }

    private String usertid;
    private String userAccessKey;
    private String deviceAccessKey;
    private String uid;
    private String host = "device.hekr.me";
    private int port = 9999;

    public void setTid(String tid){
        this.usertid = tid;
    }

    //c1-2获取用户key以及设备key
    public boolean generateAccessKey() {
        try {
            String respstr = HttpUtil.doGet(httphost + "/token/generate.json?type=DEVICE&" + _csrftoken_, cookie);
            JSONObject jo = JSONObject.parseObject(respstr);

            deviceAccessKey = jo.get("token") + "";
            respstr = HttpUtil.doGet( httphost+"/token/generate.json?type=USER&" + _csrftoken_, cookie);

            jo = JSONObject.parseObject(respstr);
            userAccessKey = jo.get("token") + "";
            uid=jo.get("uid")+"";

            return true;
        }catch (Exception ex){
            return false;
        }
    }

    //获取模板更新页面
//    public String getHtmlValue(String accesskey,String _csrftoken_,String template,String platform){
//        ArrayList<String> data=new ArrayList<String>();
//        data.add(accesskey);
//        data.add(_csrftoken_);
//        data.add(template);
//        data.add(platform);
//        String value=HttpUtil.doPost("http://posedion.hekr.me/template/update.json?",cookie,data);
//        return value;
//    }

    //获取模板更新页面
    public String getHtmlValue(org.json.JSONObject data) {
        String value=HttpUtil.doPost("http://posedion.hekr.me/template/update.json", cookie, data);
        return value;
    }

    //c1-3设备列表查询
    public List listDevice() {
        String respstr = HttpUtil.doGet(httphost + "/device/list.json?" + _csrftoken_, cookie);
        if(respstr != null) {
            JSONArray j = JSON.parseArray(respstr);
            return j;
        }else {
            return null;
        }
    }

    //c1-3列举设备返回值为json字符串
    public Object listDevice(String type) {
        Object obj = HttpUtil.doGet( httphost+"/device/list.json?" + _csrftoken_, cookie,"overload_back_deviceList_obj");
        return obj;
    }

    //c1-3带回调的设备列表查询
    public void list(HekrCallBackHandle callback) {
        String respstr = HttpUtil.doGet(httphost + "/device/list.json?" + _csrftoken_, cookie);
        if(respstr != null) {
            JSONArray j = JSON.parseArray(respstr);
            callback.callback( r.nextLong() , j );
        }else {
            callback.callback( r.nextLong() , null );
        }
    }

    //c1-18在线设备删除(设备解除绑定)
    public boolean removeDevice(String tid) {
        String respstr = HttpUtil.doGet( httphost+"/device/clearAccesskey.json?tid="+tid +"&" + _csrftoken_ , cookie);
        return isSuccess(respstr);
    }

    //c1-4离线设备删除
    public boolean deleteDevice(String tid) {
        String respstr = HttpUtil.doGet( httphost+"/device/delete.json?tid="+tid +"&" + _csrftoken_ , cookie);
        return isSuccess(respstr);
    }

    //c1-5设备改名
    public boolean renameDevice(String tid,String name) {
        String respstr = HttpUtil.doGet( httphost+"/device/rename/"+tid+".json?"+"_csrftoken_=abcd"+"&name="+name, cookie);
        return isSuccess(respstr);
    }

    //设备授权
    public boolean deviceAuth(String tid,String grant_uid,String desc){
        String respstr=HttpUtil.doGet(httphost+"/auth/authDevice.json?tid="+tid+"&"+_csrftoken_+"&grant_uid="+grant_uid+"&desc="+desc, cookie);
        return isSuccess(respstr);
    }

    //获取当前用户名称
    public String getUserName(String uid){
        String UserNameJson=HttpUtil.doGet(httphost+"/user/getUserProfile.json?uid="+uid+"&"+_csrftoken_, cookie);
        return UserNameJson;
    }
    //保存个人信息配置信息(修改用户名)
    public boolean UpdateUserName(String accesskey,String _csrftoken_,String preferences_json){
        String UserNameUpdateJson=HttpUtil.doPost(httphost + "/user/setPreferences.json?", accesskey, _csrftoken_, preferences_json, cookie);
        return isSuccess(UserNameUpdateJson);
    }
    //获取授权设备列表
    public Object getAuthDeviceList(String tid){
        Object obj=HttpUtil.doGet(httphost + "/auth/listAuth.json?tid=" + tid + "&" + _csrftoken_, cookie,"overload_back_AuthDeviceList_obj");
        return obj;
    }
    //用户解除授权设备关系
    public boolean userRemoveAuthDevice(String from_uid,String tid,String grant_uid){
        String respstr=HttpUtil.doGet(httphost+"/auth/deauthDevice.json?from_uid="+from_uid+"&tid="+tid+"&"+_csrftoken_+"&grant_uid="+grant_uid, cookie);
        return isSuccess(respstr);
    }
    //C1-16 获取个人配置信息
    public Object getPreferences(String userAccessKey){
        Object data=null;
        org.json.JSONObject obj= (org.json.JSONObject) HttpUtil.doGet(httphost+"/getPreferences.json?accesskey="+userAccessKey+"&"+_csrftoken_,cookie,"overload_back_preferences_obj");
        try {
            data=new org.json.JSONObject(obj.getString("preferences_json"));
        } catch (JSONException e) {

        }
        return data;
    }

    //c1-7激活设备(无属主登录情况下需要主动认领设备)
    public boolean activateDevice(String encryptkey,String ver,long time) {
        String respstr = HttpUtil.doGet( httphost+"/device/activate.json?encryptkey="+encryptkey+"&ver="+ver+"&time="+time+"&" + _csrftoken_ , cookie);
        return isSuccess(respstr);
    }

    //c1-8新增目录
    public boolean folderCreate(String name) {
        String respstr = HttpUtil.doGet( httphost+"/folder/create.json?name="+name
                +"&" + _csrftoken_ , cookie);
        return isSuccess(respstr);
    }

    //c1-9列举目录
    public Object folderList() {
        Object obj = HttpUtil.doGet( httphost+"/folder/list.json?" + _csrftoken_, cookie,"overload_back_folderList_obj");
        return obj;
    }

    //c1-10删除目录
    public boolean folderDelete(String fid) {
        String respstr = HttpUtil.doGet( httphost+"/folder/delete/"+fid+".json?"
                +"&" + _csrftoken_ , cookie);
        return isSuccess(respstr);
    }

    //c1-11重命名目录
    public boolean folderRename(String fid,String fname) {
        String respstr = HttpUtil.doGet( httphost+"/folder/rename/"+fid+".json?name="+fname
                +"&" + _csrftoken_ , cookie);
        return isSuccess(respstr);
    }

    //c1-12把设备添加到目录
    public boolean folderAdd(String fid,String tid) {
        String respstr = HttpUtil.doGet( httphost+"/folder/"+fid+"/add/"+tid+".json?"
                +"&" + _csrftoken_ , cookie);
        return isSuccess(respstr);
    }

    //c1-13把设备从子目录移到根目录
    public boolean folderRemove(String fid,String tid) {
        String respstr = HttpUtil.doGet(httphost + "/folder/" + fid + "/remove/" + tid + ".json?"
                + "&" + _csrftoken_, cookie);
        return isSuccess(respstr);
    }

    //一键配置
    public boolean smartConfig(String deviceAccessKey,final String ssid, final String password){
        hc = new HekrConfig(deviceAccessKey);
        Object ret = hc.config(ssid, password);
        if(ret!=null){
            return true;
        }
        else {
            return false;
        }
    }

    public void cancelConfig(){
        if(hc!=null){
            hc.stop();
        }
    }

    public boolean softapSetAccessKey(String deviceAccessKey){
        String respstr = HttpUtil.doGet( "http://192.168.10.1/t/set_ak?ak="+deviceAccessKey , null);
        return isSuccess(respstr);
    }

    public boolean softapSetBridge(String ssid,String passwd){
        String respstr = HttpUtil.doGet("http://192.168.10.1/t/set_bridge?ssid=" + ssid + "&key=" + passwd, null);
        return isSuccess(respstr);
    }

    public Object softapList(){
        Object obj = HttpUtil.doGet("http://192.168.10.1/t/get_aplist", null,"overload_back_APList_obj");
        return obj;
    }

    public String getCsrftoken(){
        return _csrftoken_;
    }

    public String getUserAccessKey(){
        return userAccessKey;
    }

    public String getDeviceAccessKey(){
        return deviceAccessKey;
    }

    public String getCookie(){
        return cookie;
    }

    public Random getR() {
        return r;
    }

    public String getUid() {
        return uid;
    }

    public boolean devcall(String tid,String code) {
        String c = "(@devcall \""+tid+"\"  "+code+" (lambda x x) )";
        //  todo
        return false;
    }

    public boolean devcallUartData(String tid,String uartdata) {
        String code = "(@devcall \""+tid+"\" (uartdata \""+uartdata+"\") (lambda x x) )";
        // todo
        return true;
    }

    public boolean isSuccess(String respstr){
        try {
            if (respstr != null) {
                JSONObject jo = (JSONObject) JSONObject.parse(respstr);
                int code = jo.getInteger("code");
                if (code == 200) {
                    return true;
                }
            }
            return false;
        }catch (Exception ex){
            Log.d("MyLog","调用删除异常");
            return false;
        }
    }
}
