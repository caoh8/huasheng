package com.ime.music;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.TextView;

import com.tendcloud.tenddata.TCAgent;

public class YinsiActivity extends SlideBackActivity {

    private WebView wvYinsi;
    private TextView ysBack;

    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(YinsiActivity.this, "隐私政策");
    }

    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(YinsiActivity.this, "隐私政策");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除顶部标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#F9F9F9"));
        }
        setContentView(R.layout.activity_yinsi);

        ysBack = findViewById(R.id.btn_ys_back);
        ysBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        wvYinsi = findViewById(R.id.wv_yinsi);
        wvYinsi.loadUrl("file:///android_asset/yinsi.html");
    }
}
