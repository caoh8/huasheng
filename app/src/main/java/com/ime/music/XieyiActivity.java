package com.ime.music;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.TextView;

import com.tendcloud.tenddata.TCAgent;

public class XieyiActivity extends SlideBackActivity {

    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(XieyiActivity.this, "用户协议");
    }

    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(XieyiActivity.this, "用户协议");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除顶部标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setStatusBarColor(Color.parseColor("#F9F9F9"));
        setContentView(R.layout.activity_xieyi);

        TextView xyBack = findViewById(R.id.btn_xy_back);
        xyBack.setOnClickListener(view -> finish());

        WebView wvXieyi = findViewById(R.id.wv_xieyi);
        wvXieyi.loadUrl("file:///android_asset/xieyi.html");


    }
}
