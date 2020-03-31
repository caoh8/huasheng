package com.ime.music;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.tendcloud.tenddata.TCAgent;

public class HelpActivity extends SlideBackActivity {

    private TextView mTvHelp;

    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(HelpActivity.this,"帮助页");
    }

    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(HelpActivity.this,"帮助页");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除顶部标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(Color.parseColor("#F9F9F9"));
        }
        setContentView(R.layout.activity_help);

        mTvHelp = findViewById(R.id.btn_help_back);
        mTvHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
