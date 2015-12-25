package com.hekr.android.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import com.hekr.android.app.sdk.SdkOutCalled;


public class LoginActivity extends Activity {

    //主页面的layout
    private RelativeLayout layout;

    //SDK初始化
    private SdkOutCalled sdkOutCalled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layout=new RelativeLayout(LoginActivity.this);
        setContentView(layout);

        SdkOutCalled.setUpSDK(LoginActivity.this, LoginActivity.this, layout, "http://10.25.1.129:8080/HekrAPP2.0/html/index.html");
        sdkOutCalled=SdkOutCalled.getInstance(LoginActivity.this,LoginActivity.this,layout);
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        return sdkOutCalled.onKeyDown(keyCode,event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        sdkOutCalled.onDestroy();
    }
}
