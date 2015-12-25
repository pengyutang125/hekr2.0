package com.hekr.android.app.sdk;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import com.hekr.android.app.interfaceImpl.HekrUser;
import com.hekr.android.app.util.AssetsDatabaseManager;
import com.hekr.android.app.util.DetailCut;
import com.hekr.android.app.util.HekrWebSocket;
import com.hekr.android.app.util.MySettingsHelper;
import com.hekr.android.app.util.ThreadPool;
import com.hekr.android.app.util.WifiAdmin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by hekr_xm on 2015/10/13.
 */

/*
    sdk配置函数
    数据库初始化（需要context）
    模板加载函数
    登录管理（需要查看user例如uid返回给js一个object对象）
    设备管理函数
    获取设备
*/
public class SDK {

    public static final String TAG="SDK";
    private Context mContext;
    private AssetsDatabaseManager mg;
    private SQLiteDatabase db;
    private User user;
    private HekrUser hekrUser;
    private static SDK sdk = null;
    private HekrWebSocket hekr_wsc;
    private ThreadPool threadPool;
    //管理wifi
    private WifiManager wifiManager;
    //管理网络
    private ConnectivityManager connectManager;
    //初始页面string
    private String initUrl;

    public SDK() {
    }

    //在构造函数中将数据库初始化
    public SDK(Context mContext,String initUrl) {
        this.mContext = mContext;
        this.initUrl=initUrl;
        AssetsDatabaseManager.initManager(mContext);
        mg= AssetsDatabaseManager.getManager();
        db = mg.getDatabase("db");
        //threadPool=ThreadPool.getThreadPool();
    }

    //sdk单例模式
    public static synchronized SDK getInstance(Context mContext,String initUrl) {
        if (sdk == null) {
            sdk = new SDK(mContext.getApplicationContext(),initUrl);
        }
        return sdk;
    }

    //初始化用户回调接口
    public interface InitUserCallback {
        void callback(Object data);
    }

    //注销用户回调接口
    public interface DestoryUserCallback {
        void callback(Object data);
    }

    //得到设备列表回调接口
    public interface GetDeviceCallback {
        void callback(Object data);
    }

    //J3-5设备重命名回调接口
    public interface DeviceRenameCallback {
        void callback(boolean data);
    }

    //J3-6设备分组回调接口
    public interface DeviceSetGroupCallback {
        void callback(boolean data);
    }

    //J3-7获取所有分组回调接口
    public interface GetDeviceGroupCallback {
        void callback(Object data);
    }

    //J3-8创建分组回调接口
    public interface CreateGroupCallback {
        void callback(boolean data);
    }

    //J3-9重命名分组回调接口
    public interface RenameGroupCallback {
        void callback(boolean data);
    }

    //J3-10删除分组回调接口
    public interface DeleteGroupCallback {
        void callback(boolean data);
    }

    //J3-11一键配置回调接口
    public interface SmartConfigCallback {
        void callback(boolean data);
    }

    //J3-12手机是否已连接软AP回调接口
    public interface JudgeSoftAPConnectCallback {
        void callback(boolean data);
    }

    //J3-13获取AP列表回调接口
    public interface GetAPListCallback {
        void callback(Object data);
    }

    //get current ssid
    public interface GetCurrentSSIDCallback {
        void callback(Object data);
    }

    //J3-21 获取用户配置
    public interface GetPreferencesCallback{
        void callback(Object data);
    }

    //J3-22 设备解绑
    public interface RemoveDeviceCallback{
        void callback(boolean data);
    }

    //设备授权列表
    public interface GetDeviceAuthListCallback{
        void callback(Object data);
    }

    //将本地的user初始化
    public void initLocalUser() {
        Cursor cursor=null;
        cursor=db.rawQuery("select uid,userAccesskey,deviceAccessKey from user where id=?", new String[]{"1"});
        cursor.moveToNext();
        if(!TextUtils.isEmpty(cursor.getString(0))&&!TextUtils.isEmpty(cursor.getString(1))&&!TextUtils.isEmpty(cursor.getString(2)))
        {
            user=new User(cursor.getString(0),cursor.getString(1),cursor.getString(2));
        }
        else{
            user=new User("","","");
        }
        if(cursor!=null){
            cursor.close();
        }
    }

    //获取当前ssid
    public void getCurrentSSID(GetCurrentSSIDCallback getCurrentSSIDCallback){
        String nowWifi=null;
        wifiManager = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);
        connectManager = (ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
        nowWifi=WifiAdmin.clearSSID(wifiManager.getConnectionInfo().getSSID());
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("obj",nowWifi);
        } catch (JSONException e) {
        }
        getCurrentSSIDCallback.callback(jsonObject);
    }

    //获取设备列表
    public void getDevices(final String type, final GetDeviceCallback getDeviceCallback) {

        Runnable listRunnable =new Runnable() {
            @Override
            public void run() {
                Object obj=hekrUser.listDevice(type);
                JSONArray list=(JSONArray)obj;
                int length=list.length();
                for(int i=0;i<length;i++){
                    try {
                        JSONObject item=list.getJSONObject(i);
                        Map<String,Object> newItem=new HashMap<String,Object>();
                        if(item.has("uid")){
                            newItem.put("uid",item.getString("uid"));
                        }
                        if(item.has("tid")){
                            newItem.put("tid",item.getString("tid"));
                        }
                        if(item.has("online")){
                            newItem.put("online",item.getInt("online"));
                        }
                        if(item.has("time")){
                            newItem.put("time",item.getLong("time"));
                        }
                        if(item.has("name")){
                            newItem.put("name",item.getString("name"));
                        }
                        if(item.has("fname")){
                            newItem.put("fname",item.getString("fname"));
                        }

                        if(item.has("detail")){
                            JSONObject detailJson=new JSONObject(DetailCut.getDetailMap(item.getString("detail")));
                            newItem.put("detail",detailJson);
                        }
                        if(item.has("state")){
                            JSONObject stateJson=new JSONObject(DetailCut.getDetailMap(item.getString("state")));
                            newItem.put("state",stateJson);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                getDeviceCallback.callback(obj);
            }
        };
        new Thread(listRunnable).start();

    }

    //设备控制
    public void sendMessage(String tid,String msg,String type) {
        String reallyMsg="(@devcall \""+tid+"\""+msg +"(lambda x x))\n";
        if ((hekr_wsc != null) && hekr_wsc.checkWebSocketLinked()){
            hekr_wsc.sendDevCall(tid, reallyMsg);
        }
    }

    //设备状态回馈

    //设备重命名
    public void renameDevice(final String tid, final String name, final DeviceRenameCallback deviceRenameCallback) {

        Runnable renameDeviceRunnable =new Runnable() {
            @Override
            public void run() {
                boolean flag=hekrUser.renameDevice(tid, name);
                deviceRenameCallback.callback(flag);
            }
        };
        new Thread(renameDeviceRunnable).start();
    }

    //设备分组
    public void deviceSetGroup(final String tid, final String gid,String name, final DeviceSetGroupCallback deviceSetGroupCallback) {

        Runnable deviceSetGroupRunnable =new Runnable() {
            @Override
            public void run() {
                boolean flag=hekrUser.folderAdd(gid, tid);
                deviceSetGroupCallback.callback(flag);
            }
        };
        new Thread(deviceSetGroupRunnable).start();
    }

    //获取所有分组
    public void getGroups(final GetDeviceGroupCallback getDeviceGroupCallback) {

        Runnable getGroupsRunnable =new Runnable() {
            @Override
            public void run() {
                Object obj=hekrUser.folderList();
                getDeviceGroupCallback.callback(obj);
            }
        };
        new Thread(getGroupsRunnable).start();
    }

    //创建分组
    public void createGroup(final String name, final CreateGroupCallback createGroupCallback) {

        Runnable createGroupRunnable =new Runnable() {
            @Override
            public void run() {
                boolean flag=hekrUser.folderCreate(name);
                createGroupCallback.callback(flag);
            }
        };
        new Thread(createGroupRunnable).start();
    }

    //重命名分组
    public void renameGroup(final String gid, final String name, final RenameGroupCallback renameGroupCallback) {

        Runnable renameGroupRunnable =new Runnable() {
            @Override
            public void run() {
                boolean flag=hekrUser.folderRename(gid, name);
                renameGroupCallback.callback(flag);
            }
        };
        new Thread(renameGroupRunnable).start();
    }

    //删除分组
    public void removeGroup(final String gid, final DeleteGroupCallback deleteGroupCallback) {

        Runnable removeGroupRunnable =new Runnable() {
            @Override
            public void run() {
                boolean flag=hekrUser.folderDelete(gid);
                deleteGroupCallback.callback(flag);
            }
        };
        new Thread(removeGroupRunnable).start();
    }

    //一键配置
    public void smartConfig(final String deviceAccesskey,final String ssid, final String pwd, final SmartConfigCallback smartConfigCallback) {

        Runnable smartConfigRunnable =new Runnable() {
            @Override
            public void run() {
                boolean flag=hekrUser.smartConfig(deviceAccesskey, ssid, pwd);
                smartConfigCallback.callback(flag);
            }
        };
        if(!judgeConnectSoftAP()){
            new Thread(smartConfigRunnable).start();
        }
        else{
            new AsyncTask<Void,Void,Boolean>(){
            @Override
            protected Boolean doInBackground(Void... params) {
                boolean flag=false;
                flag=hekrUser.softapSetAccessKey(deviceAccesskey);
                if(flag){
                    return hekrUser.softapSetBridge(ssid,pwd);
                }
                else{
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                smartConfigCallback.callback(aBoolean);
            }
        }.execute();
        }

    }

    //手机是否已连接软AP(外部使用)
    public void judgeConnectSoftAP(final JudgeSoftAPConnectCallback judgeSoftAPConnectCallback) {
        wifiManager = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);
        connectManager = (ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
        String nowWifi=WifiAdmin.clearSSID(wifiManager.getConnectionInfo().getSSID());
        Log.i(TAG,"wifi名称："+nowWifi);
        boolean flag=false;
        if(!TextUtils.isEmpty(nowWifi)&&(nowWifi.startsWith("HEKR_")||nowWifi.startsWith("Hekr_"))){
            flag=true;
            judgeSoftAPConnectCallback.callback(flag);
        }
        else{
            flag=false;
            judgeSoftAPConnectCallback.callback(flag);
        }
    }

    //手机是否已连接软AP(内部使用)
    public boolean judgeConnectSoftAP() {
        wifiManager = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);
        connectManager = (ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
        String nowWifi=WifiAdmin.clearSSID(wifiManager.getConnectionInfo().getSSID());
        Log.i(TAG,"wifi名称："+nowWifi);
        if(!TextUtils.isEmpty(nowWifi)&&(nowWifi.startsWith("HEKR_")||nowWifi.startsWith("Hekr_"))){
            return true;
        }
        else{
            return false;
        }
    }

    //获取AP列表
    public void getAPList(final GetAPListCallback getAPListCallback) {

        Runnable getAPListRunnable =new Runnable() {
            @Override
            public void run() {
                Object obj=hekrUser.softapList();
                getAPListCallback.callback(obj);
            }
        };
        if(judgeConnectSoftAP()){
            new Thread(getAPListRunnable).start();
        }
    }

    //ap配置(取消一键配置)
    public void cancelConfig(){
        if(!judgeConnectSoftAP()){
            hekrUser.cancelConfig();
        }
        else {

        }
    }

    //获取用户配置
    public void getPreferences(final String userAccesskey, final GetPreferencesCallback getPreferencesCallback){

        Runnable getPreferencesRunnable=new Runnable() {
            @Override
            public void run() {
                Object obj=hekrUser.getPreferences(userAccesskey);
                getPreferencesCallback.callback(obj);
            }
        };
        new Thread(getPreferencesRunnable).start();
    }

    //设备解除绑定
    public void removeDevice(final String tid, final RemoveDeviceCallback removeDeviceCallback){

        Runnable removeDeviceRunnable=new Runnable() {
            @Override
            public void run() {
                boolean flag=hekrUser.removeDevice(tid);
                removeDeviceCallback.callback(flag);
            }
        };
        new Thread(removeDeviceRunnable).start();
    }

    public void deviceAuthList(final String tid, final GetDeviceAuthListCallback getDeviceAuthListCallback){
        Runnable deviceAuthListRunnable=new Runnable() {
            @Override
            public void run() {
                Object obj=hekrUser.getAuthDeviceList(tid);
                getDeviceAuthListCallback.callback(obj);
            }
        };
        new Thread(deviceAuthListRunnable).start();
    }

    //清空本地user信息
    public void ClearLocalUser(DestoryUserCallback destoryUserCallback) {
        db.execSQL("update user set uid=?,userAccesskey=?,deviceAccessKey=? where id=?", new String[]{"","","","1"});

        Log.i(TAG, "将数据库中的用户信息清空");
        Cursor cursor=null;
        cursor=db.rawQuery("select uid,userAccesskey,deviceAccessKey from user where id=?",
                new String[]{"1"});
        cursor.moveToNext();
        user=new User(cursor.getString(0),cursor.getString(1),cursor.getString(2));
        if(!TextUtils.isEmpty(user.getUid())){
            destoryUserCallback.callback(user.getUid());
        }
        else{
            destoryUserCallback.callback("");
        }
        if (cursor!=null){
            cursor.close();
        }
    }

    //清空本地user信息(无回调)
    public void ClearLocalUser() {
        db.execSQL("update user set uid=?,userAccesskey=?,deviceAccessKey=? where id=?", new String[]{"","","","1"});

        Log.i(TAG, "将数据库中的用户信息清空");
        Cursor cursor=null;
        cursor=db.rawQuery("select uid,userAccesskey,deviceAccessKey from user where id=?",
                new String[]{"1"});
        cursor.moveToNext();
        user=new User(cursor.getString(0),cursor.getString(1),cursor.getString(2));
        if (cursor!=null){
            cursor.close();
        }
    }

    //模板加载函数
    public String templateLoadManage(String url) {
        String templateName="";
        Cursor cursor=null;
        if(url.startsWith("http://www.hekr.me/templates/"))
        {
            int lastIndex=url.indexOf("/content.html");
            templateName=url.substring(19,lastIndex);
            cursor = db.rawQuery("select url from page where name=?",
                    new String[]{templateName});
            if(cursor.getCount()==0)
            {
                return url;
            }
            else{
                cursor.moveToNext();
                return cursor.getString(0);
            }
        }
        if(cursor!=null){
            cursor.close();
        }
        return url;
    }

    //初始化sdk(实际初始化数据库)
    public void setUp() {
        AssetsDatabaseManager.initManager(mContext);
        mg= AssetsDatabaseManager.getManager();
        db = mg.getDatabase("db");
    }

    //获取登录凭证
    public void getU(String url) {
        //String user_credential="user_credential";
        if(url.contains("success.htm"))
        {
            //Log.i(TAG,"截取的URL中包含success.htm");
            CookieManager cookieManager = CookieManager.getInstance();
            String cookiestr = cookieManager.getCookie(url);
            //Log.i(TAG,"cookiestr:"+cookiestr);
            HashMap<String,String> cookieMap = new HashMap<String, String>();
            if(!TextUtils.isEmpty(cookiestr))
            {
                String cookieParams[] = cookiestr.split(";");

                if(cookieParams.length>0)
                {
                    for(int i=0;i<cookieParams.length;i++)
                    {
                        String kvParam[] = cookieParams[i].split("=");

                        if(kvParam.length==2)
                        {
                            cookieMap.put(kvParam[0].toString().trim(),kvParam[1].toString().trim());
                        }
                    }
                }
                if(cookieMap.containsKey("u"))
                {
                    if(TextUtils.isEmpty(MySettingsHelper.getCookieUser())) {
                        db.execSQL("INSERT INTO settings VALUES('user_credential','" + cookieMap.get("u") + "');");
                        Log.i(TAG,"已经执行截取登陆cookie!");
                    }
                    //db.execSQL("update settings set setting_key=?,setting_value=? where setting_key='user_credential'",new String[]{user_credential,cookieMap.get("u").toString()});
                }
            }
        }
        else{
            Log.i("SDK","URL中并不包括success.htm");
        }
    }

    //判断登录凭证是否为空
    public boolean alreadyHaveU() {
        if(!TextUtils.isEmpty(MySettingsHelper.getCookieUser())){
            Log.i(TAG,"登陆cookie不为空！");
            return true;
        }
        else{
            Log.i(TAG, "登陆cookie为空！");
            return false;
        }
    }

    //将从服务端获取user信息
    public void initUser(final HekrUser hekrUser, final InitUserCallback initUserCallback) {
        new AsyncTask<Void,Void,Boolean>(){
            @Override
            protected Boolean doInBackground(Void... params) {
                Boolean flag=false;
                flag= hekrUser.generateAccessKey();
                Log.i(TAG,"获取用户模块的用户信息！");
                return flag;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if(aBoolean){
                    Log.i(TAG,"获取用户模块的用户信息成功！");
                    user=new User(hekrUser.getUid(),hekrUser.getUserAccessKey(),hekrUser.getDeviceAccessKey());
                    //db.execSQL("insert into user VALUES('" + hekrUser.getUid() + "','" + hekrUser.getUserAccessKey() + "');");
                    db.execSQL("update user set uid=?,userAccesskey=?,deviceAccessKey=? where id='1'", new String[]{hekrUser.getUid(), hekrUser.getUserAccessKey(),hekrUser.getDeviceAccessKey()});
                    Log.i(TAG, "获取user信息成功！");
                    Log.i(TAG, "将云端获取的用户信息存入数据库！");

                    if(isSdCardExist())
                    {
                        Log.i(TAG, "将云端获取的用户信息写到sd卡user.txt中!");
                        if(user!=null&&!TextUtils.isEmpty(user.toString())){
                            writeUser(user.toString());
                            Log.i(TAG, "云端获取的用户信息成功写到sd卡user.txt中!");
                        }
                    }
                }
                else{
                    Log.i(TAG,"获取用户模块的用户信息失败！");
                }
                if(!TextUtils.isEmpty(user.getUid())){
                    initUserCallback.callback(user.getUid());
                }
                else{
                    initUserCallback.callback("");
                }
            }
        }.execute();
    }

    //登出
    public String logOut(Context context,DestoryUserCallback destoryUserCallback) {
        //清除登陆凭证
        db.execSQL("delete from settings");
        Log.i(TAG, "将数据库中的登陆cookie清除");

        clearCookies(context);
        Log.i(TAG, "将CookieManager中的登陆cookie清除");

        //清除用户信息
        ClearLocalUser(destoryUserCallback);

        //将sd卡的用户信息修改成""
        writeUser("");

        return "";
    }

    public void logOut(Context context) {
        //清除登陆凭证
        db.execSQL("delete from settings");
        Log.i(TAG, "将数据库中的登陆cookie清除");

        clearCookies(context);
        Log.i(TAG, "将CookieManager中的登陆cookie清除");

        //清除用户信息
        ClearLocalUser();

        //将sd卡的用户信息修改成""
        writeUser("");
    }

    //清除cookieManager中cookie
    private void clearCookies(Context context) {
        CookieSyncManager cookieSyncMngr =
                CookieSyncManager.createInstance(context);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();
        cookieManager.removeAllCookie();
        cookieSyncMngr.sync();
    }

    //手机存储卡是否存在
    public boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    //将用户模块的用户信息写入到sd卡user.txt中
    public void writeUser(String userInfo) {
        Log.i(TAG,"写入sd卡内容:"+userInfo);
        FileOutputStream fos = null;
        String info = userInfo;
        try {
            fos = new FileOutputStream(Environment.getExternalStorageDirectory()+"/user.txt",false);
            OutputStreamWriter writer = new OutputStreamWriter(fos,"utf-8");
            writer.write(info);
            writer.flush();
            writer.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //从sd卡中获取用户信息
    public String readUser() {
        File file=new File(Environment.getExternalStorageDirectory(),"user.txt");

        FileInputStream is= null;
        try {
            is = new FileInputStream(file);

            byte[] b=new byte[1024];

            is.read(b);

            String result=new String(b);

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    //初始化设备管理类
    public void initHekrUser(InitUserCallback initUserCallback) {

        hekrUser=HekrUser.getInstance(MySettingsHelper.getCookieUser());
        Log.i(TAG,"初始化用户管理操作类！");
        initUser(hekrUser,initUserCallback);
    }

    public void initHekrUser() {
        hekrUser=HekrUser.getInstance(MySettingsHelper.getCookieUser());
        Log.i(TAG,"初始化用户管理操作类！");
    }

    public void initHekrWebSocket(Handler webSocketHandler,String uid,String type,String userAccesskey) {
        hekr_wsc = new HekrWebSocket(webSocketHandler, uid, "USER",userAccesskey);
    }

    public User getUser() {
        return user;
    }

    public String getInitUrl() {
        return initUrl;
    }

    public void setInitUrl(String initUrl) {
        this.initUrl = initUrl;
    }

    public HekrWebSocket getHekr_wsc() {
        return hekr_wsc;
    }

}