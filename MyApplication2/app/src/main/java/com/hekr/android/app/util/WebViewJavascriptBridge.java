package com.hekr.android.app.util;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.*;
import android.widget.Toast;

import com.hekr.android.app.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * implements Serializable in case of javascript interface will be removed in obfuscated code.
 *
 * User: jack_fang
 * Date: 13-8-15
 * Time: 下午6:08
 */
public class WebViewJavascriptBridge implements Serializable {

    private static final String TAG="WebViewJavascriptBridge";
    WebView mWebView;
    Activity mContext;
    WVJBHandler _messageHandler;
    Map<String,WVJBHandler> _messageHandlers;
    Map<String,WVJBResponseCallback> _responseCallbacks;
    long _uniqueId;
    WebViewClient mCustomWebClient;
    WebChromeClient mCustomWebChromeClient;

    public WebViewJavascriptBridge(Activity context,WebView webview,WVJBHandler handler) {
        this.mContext=context;
        this.mWebView=webview;
        this._messageHandler=handler;
        _messageHandlers=new HashMap<String,WVJBHandler>();
        _responseCallbacks=new HashMap<String, WVJBResponseCallback>();
        _uniqueId=0;
        WebSettings webSettings = mWebView.getSettings();
        if(Build.VERSION.SDK_INT >= 19) {
            mWebView.getSettings().setLoadsImagesAutomatically(true);
        } else {
            mWebView.getSettings().setLoadsImagesAutomatically(false);
        }
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setDomStorageEnabled(true);
        //webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mWebView.addJavascriptInterface(this, "_WebViewJavascriptBridge");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());     //optional, for show console and alert
    }

    private void loadWebViewJavascriptBridgeJs(WebView webView) {
        InputStream is=mContext.getResources().openRawResource(R.raw.webviewjavascriptbridge);
        String script=convertStreamToString(is);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // In KitKat+ you should use the evaluateJavascript method
            webView.evaluateJavascript(script, new ValueCallback<String>() {

                @Override
                public void onReceiveValue(String s) {
                    Log.v("", "");
                }
            });
        } else {
            /**
             * For pre-KitKat+ you should use loadUrl("javascript:<JS Code Here>");
             * To then call back to Java you would need to use addJavascriptInterface()
             * and have your JS call the interface
             **/
            webView.loadUrl("javascript:"+script);
        }
    }

    public static String convertStreamToString(InputStream is) {
        String s="";
        try{
            Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
            if (scanner.hasNext()) s= scanner.next();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }


    private class MyWebViewClient extends WebViewClient {


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(mCustomWebClient != null)
                return mCustomWebClient.shouldOverrideUrlLoading(view, url);
            Log.i(TAG, "WebViewJavascriptBridge:shouldOverrideUrlLoading:URL:" + url);
            return super.shouldOverrideUrlLoading(view, url);

        }
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url)
        {
            if(mCustomWebClient != null)
                return mCustomWebClient.shouldInterceptRequest(view, url);
            Log.i(TAG, "WebViewJavascriptBridge:shouldInterceptRequest:URL:" + url);
            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //super.onPageStarted(view, url, favicon);
            if(mCustomWebClient != null)
                mCustomWebClient.onPageStarted(view, url, favicon);
            Log.i(TAG, "WebViewJavascriptBridge:onPageStarted:URL:" + url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
            if(mCustomWebClient != null)
                mCustomWebClient.onReceivedSslError(view, handler, error);
        }

        @Override
        public void onPageFinished(WebView webView, String url) {
            Log.d("test", "onPageFinished");
//            if(!webView.getSettings().getLoadsImagesAutomatically()) {
//                webView.getSettings().setLoadsImagesAutomatically(true);
//            }
            loadWebViewJavascriptBridgeJs(webView);
            if(mCustomWebClient != null)
                mCustomWebClient.onPageFinished(webView, url);
            Log.i(TAG, "WebViewJavascriptBridge:onPageFinished:URL:" + url);
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onConsoleMessage(ConsoleMessage cm) {
            Log.d("test", cm.message()
                            +" line:"+ cm.lineNumber()
            );
            return true;
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            // if don't cancel the alert, webview after onJsAlert not responding taps
            // you can check this :
            // http://stackoverflow.com/questions/15892644/android-webview-after-onjsalert-not-responding-taps
            result.cancel();
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public void setCustomWebViewClient(WebViewClient webViewClient){
        mCustomWebClient = webViewClient;
    }

    public void setCustomWebChromeClient(WebChromeClient webChromeClient) {
        this.mCustomWebChromeClient = webChromeClient;
    }

    public interface WVJBHandler{
        public void handle(Object data, WVJBResponseCallback jsCallback);
    }

    public interface WVJBResponseCallback{
        public void callback(Object data);
    }

    public void registerHandler(String handlerName,WVJBHandler handler) {
        _messageHandlers.put(handlerName, handler);
    }

    private class CallbackJs implements WVJBResponseCallback{
        private final String callbackIdJs;

        public  CallbackJs(String callbackIdJs){
            this.callbackIdJs=callbackIdJs;
        }
        @Override
        public void callback(Object data) {
            _callbackJs(callbackIdJs,data);
        }
    }


    private void _callbackJs(String callbackIdJs,Object data) {
        //TODO: CALL js to call back;
        Map<String,Object> message=new HashMap<String, Object>();
        message.put("responseId",callbackIdJs);
        message.put("responseData",data);
        _dispatchMessage(message);
    }

    @JavascriptInterface
    public void _handleMessageFromJs(final String data,String responseId,
                                     String responseData,String callbackId,String handlerName){
        if (null!=responseId) {
            WVJBResponseCallback responseCallback = _responseCallbacks.get(responseId);
            try {
                responseCallback.callback(new JSONObject(responseData));
            } catch (JSONException e) {
                //e.printStackTrace();
            }
            _responseCallbacks.remove(responseId);
        } else {
            WVJBResponseCallback responseCallback = null;
            if (null!=callbackId) {
                responseCallback=new CallbackJs(callbackId);
            }
            final WVJBHandler handler;
            if (null!=handlerName) {
                handler = _messageHandlers.get(handlerName);
                if (null==handler) {
                    Log.e("test","WVJB Warning: No handler for "+handlerName);
                    return ;
                }
            } else {
                handler = _messageHandler;
            }
            try {
                final WVJBResponseCallback finalResponseCallback = responseCallback;
                //Log.i("test","data:"+data+"finalResponseCallback:"+finalResponseCallback);
                mContext.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        JSONObject jsonObject=null;
                        try {
                            jsonObject = new JSONObject(data);
                        }
                        catch (JSONException e){

                        }
                        try {
                            handler.handle(jsonObject, finalResponseCallback);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }catch (Exception exception) {
                Log.e("test","WebViewJavascriptBridge: WARNING: java handler threw. "+exception.getMessage());
                exception.printStackTrace();
            }
        }
    }

    public void send(Object data) {
        send(data,null);
    }

    public void send(Object data ,WVJBResponseCallback responseCallback) {
        _sendData(data,responseCallback,null);
    }

    private void _sendData(Object data,WVJBResponseCallback responseCallback,String  handlerName){
        Map <String, Object> message=new HashMap<String,Object>();
        message.put("data",data);
        if (null!=responseCallback) {
            String callbackId = "java_cb_"+ (++_uniqueId);
            _responseCallbacks.put(callbackId,responseCallback);
            message.put("callbackId",callbackId);
        }
        if (null!=handlerName) {
            message.put("handlerName", handlerName);
        }
        _dispatchMessage(message);
    }

    private void _dispatchMessage(Map <String, Object> message){
        JSONObject jsonObject=new JSONObject();

        for (Map.Entry<String, Object> entry : message.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            //Log.i("test","key:"+key+"|||"+"vaule:"+value);

            try {
                jsonObject.put(key,value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String messageJSON = jsonObject.toString();

        Log.d("test","sending:"+messageJSON);

        final  String javascriptCommand = String.format("javascript:WebViewJavascriptBridge._handleMessageFromJava('%s');",doubleEscapeString(messageJSON));
        mContext.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                mWebView.loadUrl(javascriptCommand);
            }
        });
    }

    public  void callHandler(String handlerName) {
        callHandler(handlerName, null, null);
    }

    public void callHandler(String handlerName,Object data) {
        callHandler(handlerName, data,null);
    }

    public void callHandler(String handlerName,Object data,WVJBResponseCallback responseCallback){
        _sendData(data, responseCallback,handlerName);
    }

    /*
      * you must escape the char \ and  char ", or you will not recevie a correct json object in
      * your javascript which will cause a exception in chrome.
      *
      * please check this and you will know why.
      * http://stackoverflow.com/questions/5569794/escape-nsstring-for-javascript-input
      * http://www.json.org/
    */
    private String doubleEscapeString(String javascript) {
        String result;
        result = javascript.replace("\\", "\\\\");
        result = result.replace("\"", "\\\"");
        result = result.replace("\'", "\\\'");
        result = result.replace("\n", "\\n");
        result = result.replace("\r", "\\r");
        result = result.replace("\f", "\\f");
        return result;
    }

}
