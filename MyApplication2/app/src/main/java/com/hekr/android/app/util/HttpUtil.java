package com.hekr.android.app.util;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kj on 15/7/24.
 */
public class HttpUtil {

    public static String doGet(String url,String cookie) {
        String backCode="";
        url= url.replaceAll(" ", "%20");
        HttpGet http = new HttpGet(url);
        if(cookie != null) {
            http.addHeader("cookie", "u="+cookie);
        }
        Log.i("MyLog","HttpUtil:url:"+url+"||cookie:"+cookie);
        try{
            //Log.i("MyLog","Http:"+http);
            HttpResponse res = new DefaultHttpClient().execute(http);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                //Log.i("MyLog","返回code=200");
                HttpEntity entity = res.getEntity();
                //Log.i("HttpUtil","entity:"+entity);
                backCode=EntityUtils.toString(entity, HTTP.UTF_8);
                Log.i("MyLog","backCode:"+backCode);
                return backCode;
            }
        }catch (Exception e){
            Log.i("MyLog","抛出异常了");
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static Object doGet(String url,String cookie,String backTypeObject) {
        String backCode="";
        url= url.replaceAll(" ", "%20");

        HttpGet http = new HttpGet(url);

        if(cookie != null) {
            http.addHeader("cookie", "u="+cookie);
        }
        Log.i("HttpUtil","HttpUtil:URL:"+url+"||cookie:"+cookie);
        try{
            HttpResponse res = new DefaultHttpClient().execute(http);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = res.getEntity();
                backCode=EntityUtils.toString(entity, HTTP.UTF_8);
                try {
                    org.json.JSONArray jsonArray = new org.json.JSONArray(backCode);
                    return jsonArray;
                }
                catch (JSONException e){
                    try {
                        org.json.JSONObject jsonObject=new org.json.JSONObject(backCode);
                        return jsonObject;
                    } catch (JSONException ee){
                        return null;
                    }
                }
            }
        }catch (Exception e){
            Log.i("MyLog", "HttpUtil_doget_client_execute_result_fail");
            //e.printStackTrace();
            return null;
        }
        return null;
    }

    //使用表单提交
    public static String doPost(String url , String cookie , ArrayList<String> data) {
        String result="";
        HttpPost http = new HttpPost(url);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("accesskey", data.get(0)));
        params.add(new BasicNameValuePair("_csrftoken_", data.get(1)));
        params.add(new BasicNameValuePair("template", data.get(2)));
        params.add(new BasicNameValuePair("platform", data.get(3)));

        if(cookie != null) {
            http.addHeader("cookie", "u="+cookie);
        }
        try{
            DefaultHttpClient dhc = new DefaultHttpClient();
            //http.setEntity();
            http.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            // todo...
            HttpResponse res = dhc.execute(http);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = res.getEntity();
                result=EntityUtils.toString(entity, HTTP.UTF_8);
                return result;
            }
        }catch (Exception e){
            return null;
        }
        return null;
    }

    //使用post提交json参数
    public static String doPost(String url,String cookie,org.json.JSONObject jsonObject) {
        String result="";

        HttpPost httpPost=new HttpPost(url);

        if(cookie != null) {
            httpPost.addHeader("cookie", "u="+cookie);
        }
        DefaultHttpClient httpClient=new DefaultHttpClient();

        //JSONObject jsonParam=new JSONObject();

        //jsonParam.put("",jsonObject);

        StringEntity entity = null;//解决中文乱码问题
        try {
            entity = new StringEntity(jsonObject.toString(),"utf-8");
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);

            HttpResponse res = httpClient.execute(httpPost);
            if(res.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
                result=EntityUtils.toString(entity, HTTP.UTF_8);
                return result;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    //授权模块修改用户名信息
    public static String doPost(String url , String accesskey,String _csrftoken_,String preferences_json,String cookie) {
        HttpPost http = new HttpPost(url);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("accesskey", accesskey));
        params.add(new BasicNameValuePair("_csrftoken_", _csrftoken_));
        params.add(new BasicNameValuePair("preferences_json", preferences_json));
        if(cookie != null) {
            http.addHeader("cookie", "u="+cookie);
        }
        try{
            http.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
            DefaultHttpClient dhc = new DefaultHttpClient();

            HttpResponse res = dhc.execute(http);

            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = res.getEntity();
                return EntityUtils.toString(entity, HTTP.UTF_8);
            }
        }catch (Exception e){
            return null;
        }
        return null;
    }

}
