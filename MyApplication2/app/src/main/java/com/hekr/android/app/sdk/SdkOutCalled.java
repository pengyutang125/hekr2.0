package com.hekr.android.app.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.hekr.android.app.util.DetailCut;
import com.hekr.android.app.util.HekrWebSocket;
import com.hekr.android.app.util.TaskExecutor;
import com.hekr.android.app.util.WebViewJavascriptBridge;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by hekr_xm on 2015/11/9.
 */
public class SdkOutCalled {

    private static final String TAG="SdkOutCalled";
    //初始webview
    private WebView loginView;
    private static SdkOutCalled sdkOutCalled = null;
    private Context mContext;
    private SDK sdk=null;
    private WebViewJavascriptBridge bridge;

    private boolean flag=true;

    private boolean initWebsocketFlag=true;

    private boolean initHekrUserFlag=true;

    //主页面的layout
    private RelativeLayout layout;
    //当前top_webview
    private WebView currentView;
    //所有webview
    private LinkedList<WebView> windows = new LinkedList<WebView>();

    private MyHandler websocketHandler;

    private DetailCut detailCut;

    private Activity activity;

    public SdkOutCalled() {
    }

    public SdkOutCalled(Context mContext,Activity activity,RelativeLayout layout) {
        this.mContext = mContext;
        this.activity=activity;
        this.layout=layout;
    }

    //SdkOutCalled单例模式(applicationContext防止oom)
    public static synchronized SdkOutCalled getInstance(Context mContext,Activity activity,RelativeLayout layout) {
        if (sdkOutCalled == null) {
            sdkOutCalled = new SdkOutCalled(mContext,activity,layout);
        }
        return sdkOutCalled;
    }

    //初始化sdk
    private void initSDK(Context mContext,String initUrl) {
        sdk=new SDK(mContext,initUrl);
        sdk.initLocalUser();
    }

    public static void setUpSDK(Context mContext,Activity activity,RelativeLayout layout,String initUrl){
        sdkOutCalled=new SdkOutCalled(mContext,activity,layout);
        sdkOutCalled.initSDK(mContext, initUrl);
        sdkOutCalled.initViews(layout);
        sdkOutCalled.initData(activity, activity);
    }

    //初始webview
    private void initViews(RelativeLayout layout) {

        loginView=new WebView(mContext);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        bridge=getBridge(loginView);

        if(!TextUtils.isEmpty(sdk.getInitUrl())){
            loginView.loadUrl(sdk.getInitUrl());
        }
        else{
            //loginView.loadUrl("http://www.hekr.me/templates/1/content.html");
            //loginView.loadUrl("file:///android_asset/humidifier/html/index.html");
            loginView.loadUrl("http://app.hekr.me/templates/home/index.html?");
        }

        layout.addView(loginView,lp);

        currentView=loginView;

        windows.add(loginView);

    }

    private void initData(Context mContext,Activity activity) {
        detailCut= DetailCut.getInstance(mContext);
        websocketHandler=new MyHandler(activity);
    }

    //打开一个新的webview
    private void openNewWindow(String url) {

        //Log.i("SdkOutCalled", "openNewWindow:URL:" + url.substring(0, url.length() - 13));
        WebView window = new WebView(mContext);
        bridge=getBridge(window);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        if(!TextUtils.isEmpty(url)&&url.length()>13){
            window.loadUrl(url.substring(0, url.length() - 13));
        }
        layout.addView(window, lp);

        if (currentView != null) {
            currentView.setVisibility(View.INVISIBLE);
        }
        currentView = window;
        windows.add(window);

    }

    //关闭当前的webview
    private void closeWindow() {
        currentView.setVisibility(View.GONE);
        WebView view = null;
        if (windows.size() > 1) {
            view = windows.get(windows.size() - 2);
            view.setVisibility(View.VISIBLE);
        }
        else{
            if(!windows.isEmpty()&&windows.get(0)!=null){
                windows.get(0).destroy();
                activity.finish();
            } else{

            }
        }
        windows.remove(currentView);
        if(currentView!=null){
            currentView.destroy();
        }
        currentView = view;
    }

    private void removeAllWebview() {
        currentView.setVisibility(View.GONE);
        Iterator<WebView> it=windows.iterator();
        while (it.hasNext()){
            WebView webView=it.next();
            it.remove();
            if(webView!=null){
                webView.destroy();
            }
        }
        currentView=loginView;
        windows.add(currentView);
        currentView.setVisibility(View.VISIBLE);
    }

    //js桥接
    private WebViewJavascriptBridge getBridge(WebView webView) {
        bridge=new WebViewJavascriptBridge(activity,webView,new UserServerHandler());
        bridge.setCustomWebViewClient(new myWebClient());
        bridge.setCustomWebChromeClient(new MyWebChromeClient());

        bridge.registerHandler("currentUser", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, final WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (null != jsCallback) {
                    final JSONObject jsonObject = new JSONObject();
                    if (!TextUtils.isEmpty(sdk.getUser().getUid())) {
                        try {
                            JSONObject uidObject = new JSONObject();
                            uidObject.put("uid", sdk.getUser().getUid());

                            jsonObject.put("obj", uidObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {

                    }
                    TaskExecutor.scheduleTaskOnUiThread(0, new Runnable() {
                        @Override
                        public void run() {
                            jsCallback.callback(jsonObject);
                        }
                    });
                }
            }
        });

        bridge.registerHandler("logout", new WebViewJavascriptBridge.WVJBHandler()
        {
            @Override
            public void handle(Object data, WebViewJavascriptBridge.WVJBResponseCallback jsCallback)
            {
                if (null != jsCallback)
                {
                    jsCallback.callback("");
                    sdk.logOut(activity);
                    if(!windows.isEmpty())
                    {
                        for(int i=0;i<windows.size();i++)
                        {
                            bridge=getBridge(windows.get(i));
                            if (bridge != null){
                                bridge.callHandler("onUserChange", new JSONObject(), new WebViewJavascriptBridge.WVJBResponseCallback() {
                                    @Override
                                    public void callback(Object data) {
                                        flag = true;
                                        initHekrUserFlag = true;
                                    }
                                });
                            }
                        }
                    }
                    else{
                        Log.i(TAG,"logout时windows为空！");
                    }
                }

            }
        });

        bridge.registerHandler("getDevices", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, final WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (null != jsCallback) {
                    sdk.getDevices("success", new SDK.GetDeviceCallback() {
                        @Override
                        public void callback(Object data) {

                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("obj", data);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            jsCallback.callback(jsonObject);
                        }
                    });
                }
            }
        });

        bridge.registerHandler("currentSSID", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, final WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (null != jsCallback) {
                    sdk.getCurrentSSID(new SDK.GetCurrentSSIDCallback() {
                        @Override
                        public void callback(Object data) {
                            jsCallback.callback(data);
                        }
                    });
                }
            }
        });

        bridge.registerHandler("sendMsg", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                //Log.i(TAG,"data:"+data.toString());
                if (null != jsCallback) {
                    jsCallback.callback("");
                    Log.i(TAG, "data:" + data.toString());
                    if(!TextUtils.isEmpty(data.toString())){
                        try {
                            JSONObject obj=new JSONObject(data.toString());
                            sdk.sendMessage(obj.getString("tid"), obj.getString("msg"), obj.getString("type"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        bridge.registerHandler("renameDevice", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, final WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (null != jsCallback) {
                    Log.i(TAG, "data:" + data.toString());
                    if(!TextUtils.isEmpty(data.toString())){
                        try {
                            JSONObject obj=new JSONObject(data.toString());
                            sdk.renameDevice(obj.getString("tid"), obj.getString("name"), new SDK.DeviceRenameCallback() {
                                @Override
                                public void callback(boolean data) {
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("obj", data);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    jsCallback.callback(jsonObject);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });

        bridge.registerHandler("setGroup", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, final WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (null != jsCallback) {
                    Log.i(TAG,"data:"+data.toString());
                    if(!TextUtils.isEmpty(data.toString())){
                        try {
                            JSONObject obj=new JSONObject(data.toString());
                            sdk.deviceSetGroup(obj.getString("tid"), obj.getString("gid"), obj.getString("name"), new SDK.DeviceSetGroupCallback() {
                                @Override
                                public void callback(boolean data) {
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("obj", data);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    jsCallback.callback(jsonObject);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });

        bridge.registerHandler("getGroups", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, final WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (null != jsCallback) {
                    sdk.getGroups(new SDK.GetDeviceGroupCallback() {
                        @Override
                        public void callback(Object data) {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("obj", data);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            jsCallback.callback(jsonObject);
                        }
                    });
                }
            }
        });

        bridge.registerHandler("createGroup", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, final WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (null != jsCallback) {
                    Log.i(TAG, "data:" + data.toString());
                    if(!TextUtils.isEmpty(data.toString())){
                        try {
                            JSONObject obj=new JSONObject(data.toString());
                            sdk.createGroup(obj.getString("name"), new SDK.CreateGroupCallback() {
                                @Override
                                public void callback(boolean data) {
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("obj", data);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    jsCallback.callback(jsonObject);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });

        bridge.registerHandler("renameGroup", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, final WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (null != jsCallback) {
                    Log.i(TAG,"data:"+data.toString());
                    if(!TextUtils.isEmpty(data.toString())){
                        try {
                            JSONObject obj=new JSONObject(data.toString());
                            sdk.renameGroup(obj.getString("gid"), obj.getString("name"), new SDK.RenameGroupCallback() {
                                @Override
                                public void callback(boolean data) {
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("obj", data);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    jsCallback.callback(jsonObject);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });

        bridge.registerHandler("removeGroup", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, final WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (null != jsCallback) {
                    Log.i(TAG,"data:"+data.toString());
                    if(!TextUtils.isEmpty(data.toString())){
                        try {
                            JSONObject obj=new JSONObject(data.toString());
                            sdk.removeGroup(obj.getString("gid"), new SDK.DeleteGroupCallback() {
                                @Override
                                public void callback(boolean data) {
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("obj", data);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    jsCallback.callback(jsonObject);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });

        bridge.registerHandler("config", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, final WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (null != jsCallback) {
                    Log.i(TAG, "data:" + data.toString());
                    if(!TextUtils.isEmpty(data.toString())){
                        try {
                            JSONObject obj=new JSONObject(data.toString());
                            sdk.smartConfig(sdk.getUser().getDeviceAccessKey(), obj.getString("ssid"), obj.getString("pwd"), new SDK.SmartConfigCallback() {
                                @Override
                                public void callback(boolean data) {
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("obj", data);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    jsCallback.callback(jsonObject);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });

        bridge.registerHandler("cancelConfig", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (null != jsCallback) {
                    jsCallback.callback("");
                    sdk.cancelConfig();
                }
            }
        });

        bridge.registerHandler("isConnectSoftAP", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, final WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (null != jsCallback) {
                    sdk.judgeConnectSoftAP(new SDK.JudgeSoftAPConnectCallback() {
                        @Override
                        public void callback(boolean data) {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("obj", data);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            jsCallback.callback(data);
                        }
                    });

                }
            }
        });

        bridge.registerHandler("getAPList", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, final WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (null != jsCallback) {
                    sdk.getAPList(new SDK.GetAPListCallback() {
                        @Override
                        public void callback(Object data) {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("obj", data);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            jsCallback.callback(jsonObject);
                        }
                    });
                }
            }
        });

        bridge.registerHandler("close", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (null != jsCallback) {
                    jsCallback.callback("");
                    closeWindow();
                }
            }
        });

        bridge.registerHandler("closeAll", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (null != jsCallback) {
                    jsCallback.callback("");
                    removeAllWebview();
                }
            }
        });

        bridge.registerHandler("getPreferences", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, final WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (null != jsCallback) {
                    sdk.getPreferences(sdk.getUser().getUserAccessKey(), new SDK.GetPreferencesCallback() {
                        @Override
                        public void callback(Object data) {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("obj", data);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            jsCallback.callback(jsonObject);
                        }
                    });
                }
            }
        });

        bridge.registerHandler("open", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, final WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (null != jsCallback) {
                    if(!TextUtils.isEmpty(data.toString())){
                        try {
                            JSONObject obj=new JSONObject(data.toString());
                            Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" + obj.getString("schemeurl")));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        bridge.registerHandler("removeDevice", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, final WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (null != jsCallback) {
                    if(!TextUtils.isEmpty(data.toString())){
                        try {
                            JSONObject obj=new JSONObject(data.toString());
                            sdk.removeDevice(obj.getString("tid"), new SDK.RemoveDeviceCallback() {
                                @Override
                                public void callback(boolean data) {
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("obj", data);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    jsCallback.callback(jsonObject);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        bridge.registerHandler("deviceAuthList", new WebViewJavascriptBridge.WVJBHandler() {
            @Override
            public void handle(Object data, final WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
                if (null != jsCallback) {
                    if(!TextUtils.isEmpty(data.toString())){
                        try {
                            JSONObject obj=new JSONObject(data.toString());
                            sdk.deviceAuthList(obj.getString("tid"), new SDK.GetDeviceAuthListCallback() {
                                @Override
                                public void callback(Object data) {
                                    jsCallback.callback(data);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        return bridge;
    }

    class myWebClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //Log.i(TAG,"SdkOutCalled:onPageStarted:URL:"+url);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            //Log.i(TAG, "SdkOutCalled:shouldOverrideUrlLoading:URL:" + url);
            if(url.endsWith("openType=push"))
            {
                Log.i(TAG,"创建新webview：URL:"+url);
                openNewWindow(url);
                return true;
            }
            else{
                view.loadUrl(url);
                return true;
            }
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            //Log.i(TAG, "shouldInterceptRequest:URL:" + url);
            return super.shouldInterceptRequest(view, url);
            //if(url.startsWith("http://www.hekr.me/templates/"))
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageFinished(final WebView view, String url) {
            Log.i(TAG,"SdkOutCalled:onPageFinished:URL:"+url);
            if(!view.getSettings().getLoadsImagesAutomatically()) {
                view.getSettings().setLoadsImagesAutomatically(true);
            }
            sdk.getU(url);

            if(sdk.alreadyHaveU()&&initHekrUserFlag)
            {
                sdk.initHekrUser(new SDK.InitUserCallback() {
                    @Override
                    public void callback(Object data) {

                        if (!TextUtils.isEmpty(data.toString())) {
                            if (!TextUtils.isEmpty(sdk.getUser().getUid()) && !TextUtils.isEmpty(sdk.getUser().getUserAccessKey())&&initWebsocketFlag) {
                                Log.i(TAG, "sdk.getUser().getUid():" + sdk.getUser().getUid());
                                sdk.initHekrWebSocket(websocketHandler, sdk.getUser().getUid(), "USER", sdk.getUser().getUserAccessKey());
                                initWebsocketFlag=false;
                            }
                        }
                        bridge = getBridge(windows.get(0));

                        if (bridge != null && flag) {
                            Log.i(TAG, "pagefinsh:onUserChange:uid:" + sdk.getUser().getUid());

                            JSONObject jsonObject = new JSONObject();
                            if (!TextUtils.isEmpty(sdk.getUser().getUid())) {
                                try {
                                    JSONObject uidObject=new JSONObject();
                                    uidObject.put("uid",sdk.getUser().getUid());

                                    jsonObject.put("obj", uidObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } else {

                            }

                            bridge.callHandler("onUserChange", jsonObject, new WebViewJavascriptBridge.WVJBResponseCallback() {
                                @Override
                                public void callback(Object data) {
                                    flag = false;
                                    initHekrUserFlag=false;
                                }
                            });
                            if (!windows.isEmpty()) {
                                Log.i(TAG, "当前webview个数：" + windows.size());
                            }
                            bridge = getBridge(windows.get(windows.size() - 1));
                        }

                    }
                });
            }
            super.onPageFinished(view, url);
        }

    }

    class MyWebChromeClient extends WebChromeClient {
        public MyWebChromeClient() {
            super();
        }

        @Override
        public void onCloseWindow(WebView window) {
            super.onCloseWindow(window);
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        }
    }

    class UserServerHandler implements WebViewJavascriptBridge.WVJBHandler {

        @Override
        public void handle(Object data, WebViewJavascriptBridge.WVJBResponseCallback jsCallback) {
            if (null !=jsCallback) {
                jsCallback.callback("Java said:Right back atcha");
            }
        }
    }

    class MyHandler extends Handler {
        WeakReference<Activity > mActivityReference;

        public MyHandler(Activity activity) {
            mActivityReference= new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final Activity activity = mActivityReference.get();
            Bundle data = msg.getData();
            switch(msg.what) {
                case HekrWebSocket.MSG_REC_UART_DATA:
                    String payload = data.getString("payload");
                    Log.i(TAG, "payload:" + payload);
                    if(!TextUtils.isEmpty(payload)&&payload.startsWith("(onmessage")) {
                        String tid=null;
                        if(!DetailCut.getDetailList(payload).isEmpty()&&DetailCut.getDetailList(payload).size()>2){
                            if("null".equals(tid)){
                                tid=null;
                            }
                            else{
                                tid=DetailCut.getDetailList(payload).get(2).toString();
                            }
                        }
                        JSONObject jsonObject=new JSONObject();
                        try {
                            jsonObject.put("msg",payload);
                            jsonObject.put("tid",tid);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        bridge.callHandler("onMessage", jsonObject, new WebViewJavascriptBridge.WVJBResponseCallback() {
                            @Override
                            public void callback(Object data) {

                            }
                        });
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public boolean onKeyDown (int keyCode, KeyEvent event) {
        if (!windows.isEmpty()&&windows.size()>1&&currentView != null)
        {
            if ((keyCode == KeyEvent.KEYCODE_BACK) && currentView.canGoBack()) {
                currentView.goBack();
                Log.i(TAG,"多个webview并且当前webview中存在多个url页面！");
                return true;
            }
            else{
                Log.i(TAG,"多个webview并且当前webview中只剩下一个页面！");
                closeWindow();
                return true;
            }
        }
        else{
            if ((keyCode == KeyEvent.KEYCODE_BACK&& currentView.canGoBack())){
                Log.i(TAG,"一个webview并且当前webview中存在多个url页面！");
                currentView.canGoBack();
                return true;
            }
            else{
                Log.i(TAG,"一个webview并且当前webview中只剩下一个页面！");
                activity.finish();
                return true;
            }
        }
    }

    public void onDestroy() {
        Log.i(TAG, "loginView--onDestroy()");
        if(loginView!=null){
            loginView.destroy();
        }
    }
}
