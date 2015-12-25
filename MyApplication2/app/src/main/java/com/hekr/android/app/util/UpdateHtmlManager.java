package com.hekr.android.app.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.hekr.android.app.entity.Global;
import com.hekr.android.app.entity.Template;
import com.hekr.android.app.entity.Value;
import com.hekr.android.app.interfaceImpl.HekrUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by hekr_xm on 2015/9/28.
 */
public class UpdateHtmlManager {

    private static final String TAG="UpdateHtmlManager";
    private Context mContext;
    private HekrUser hekrUser;
    private SQLiteDatabase db;
    private AssetsDatabaseManager mg;
    private static UpdateHtmlManager updateHtmlManager = null;
    private SimpleDateFormat sdf;
    private ArrayList<Value> valueArrayList=new ArrayList<Value>();

    //初始化数据库操作实例，网络操作实例
    public UpdateHtmlManager(Context mContext) {
        this.mContext = mContext;
        hekrUser=HekrUser.getInstance(MySettingsHelper.getCookieUser());
        mg= AssetsDatabaseManager.getManager();
        db = mg.getDatabase("db");
        sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    //单例模式获取页面更新管理模板实例
    public static synchronized UpdateHtmlManager getInstance(Context mContext) {
        if (updateHtmlManager == null) {
            updateHtmlManager = new UpdateHtmlManager(mContext);
        }
        return updateHtmlManager;
    }

    //是否需要更新(wifi网络/是否在更新中/距离上次是否超过24小时)
    public boolean isNeedUpdate(boolean netWorkState,boolean needUpdate,boolean surpassUpdateTime){
        if(netWorkState&&needUpdate&&surpassUpdateTime){
            return true;
        }
        return false;
    }

    //让外部启动获取更新接口线程
    public void startGetValueRunnable(){
        new Thread(getUpdateJsonRunnable).start();
    }

    //请求服务器更新接口线程
    public Runnable getUpdateJsonRunnable =new Runnable() {

        //Template template=new Template("1","1.1");
        //String template1=JSON.toJSONString(template);
        JSONObject jsonObject=null;

        @Override
        public void run() {
            Message msg=new Message();
            Bundle data=new Bundle();
            //String valueBackJson= hekrUser.getHtmlValue(MySettingsHelper.getCookieUser(), "abcd", template1, "android");
            //jsonObject=JSONObject.parseObject(getJsonStringText(MySettingsHelper.getCookieUser(),"abcd",getData(),"Android"));
            String runnableJsonText="";
            try {
                runnableJsonText=jsonText(hekrUser.getUserAccessKey(),"abcd",getData().toString(),"Android");
                if(!TextUtils.isEmpty(runnableJsonText)){
                    jsonObject=new JSONObject(runnableJsonText);
                }
                else{
                    Log.i(TAG,"模板更新线程:runnableJsonText:为空!");
                }

            } catch (JSONException e) {
                Log.i(TAG,"更新线程中：拼接的字符串转化成org.jsonObject失败!");
                e.printStackTrace();
            }
            String valueBackJson="";
            if(jsonObject!=null){
                valueBackJson=hekrUser.getHtmlValue(jsonObject);
            }
            else{
                Log.i(TAG,"runnable:jsonObject为空!");
            }

            if(TextUtils.isEmpty(valueBackJson)){
                Global.successGetValue=false;
            }
            else{
                Global.successGetValue=true;
            }
            Log.i(TAG,"Runnable中valueBackJson:"+valueBackJson);
            data.putString("valueBackJson", valueBackJson);
            msg.setData(data);
            valueHandler.sendMessage(msg);
        }
    };

    //处理请求服务器返回的json
    @SuppressLint("HandlerLeak")
    public  Handler valueHandler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String valueBackJson=data.getString("valueBackJson");
            if(!TextUtils.isEmpty(valueBackJson)){
                JSONObject jo = null;
                try {
                    jo = new JSONObject(valueBackJson);
                    int code = jo.optInt("code");
                    String message=jo.optString("message");
                    String value = jo.optString("value");

                    if(value!=null){
                        JSONArray jsonArray=new JSONArray(value);
                        for(int i=0;i<jsonArray.length();i++)
                        {
                            org.json.JSONObject item=jsonArray.getJSONObject(i);
                            Value valueEntity=new Value();
                            if (item.has("name")){
                                valueEntity.setName(item.getString("name"));
                            }
                            else{
                                valueEntity.setName("");
                            }
                            if(item.has("version")){
                                valueEntity.setVersion(item.getString("version"));
                            }
                            else{
                                valueEntity.setVersion("");
                            }
                            if(item.has("hash")){
                                valueEntity.setHash(item.getString("hash"));
                            }
                            else{
                                valueEntity.setHash("");
                            }
                            if(item.has("url")){
                                valueEntity.setUrl(item.getString("url"));
                            }
                            else{
                                valueEntity.setUrl("");
                            }
                            valueArrayList.add(valueEntity);
                        }
                    }
                    else{
                        Log.i(TAG,"value为空!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //ArrayList<Value> valueArrayList=jo.getJSONArray("value");
            }
            else{
                Log.i(TAG,"valueBackJson为空!");
            }

        }
    };

    //获取更新的信息
    public ArrayList<Value> getValueArrayList() {
        return valueArrayList;
    }

    //拼接转换成jsonObject的json字符串
    public String jsonText(String accesskey,String _csrftoken_,String localTemplateArray,String platform){
        String jsonTest="";
        jsonTest="{"+
                "\""+accesskey+"\""+"\""+accesskey+"\""+","+
                "\""+_csrftoken_+"\""+"\""+_csrftoken_+"\""+","+
                localTemplateArray+","+
                "\""+platform+"\""+"\""+platform+"\""+
                "}";
        Log.i(TAG,"更新线程提供的更新jsonText:"+jsonTest);
        if(!TextUtils.isEmpty(jsonTest)){
            return jsonTest;
        }
        else{
            return "";
        }

    }

    //得到本地列表版本信息（name,version）
    public ArrayList<Template> getData(){
        Cursor cursor=null;

        ArrayList<Template> data=new ArrayList<Template>();
        try{
            cursor=db.rawQuery("select * from page",null);
        }catch(Exception e){
            cursor=null;
        }

        if(cursor!=null&&cursor.getCount()>=1){
            while (cursor.moveToNext()){
                Template template=new Template(cursor.getString(0),cursor.getString(1));
                data.add(template);
            }
        }
        if(data.isEmpty()){
            Template template=new Template("1","1.1");
            data.add(template);
        }
        if(cursor!=null){
            cursor.close();
        }
        return data;
    }

    //拼接json文本
    public String getJsonStringText(String accesskey,String _csrftoken_,ArrayList<Template> data,String platform){
        String jsonText="";

        jsonText="\"accesskey\":"+"\""+accesskey+"\""+","
                +"\"_csrftoken_\":"+"\""+_csrftoken_+"\""+","
                +"\"template\":"+data.toString()+","
                +"\"platform\":"+"\""+"Android"+"\"";
        return jsonText;
    }

    //定时器，更新接口失败十分钟后重新访问更新接口
    public void getUpdateJsonAgain(){
        if(Global.netWorkState){
            new Handler().postDelayed(getUpdateJsonRunnable,10*60*1000);
        }
    }

    //距离上次更新时间是否超过24小时
    public boolean compareUpdateTime(long nowtime){
        String xztime=sdf.format(new Date(nowtime));
        String lastUpdatetime=null;
        Cursor cursor=null;
        cursor=db.rawQuery("select nowtime_value from timemanage where nowtime_key=?",new String[]{"last_updateTime"});

        if(cursor.getCount()==0){
            return true;
        }
        cursor.moveToNext();
        lastUpdatetime=cursor.getString(0);

        try {
            Date d1=sdf.parse(lastUpdatetime);
            Date d2=sdf.parse(xztime);

            long diff = d2.getTime() - d1.getTime();

            long days = diff / (1000 * 60 * 60 * 24);

            long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);

            long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);

            if(days>=1){
                return true;
            }
            else{
                return false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    //保存最后一次更新时间
    public void saveLastUpdateTime(long time){
        String needSaveTime=sdf.format(new Date(time));
        db.execSQL("INSERT INTO timemanage VALUES('last_updateTime','" + needSaveTime + "');");
    }
}
