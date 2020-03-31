package com.ime.music;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.ime.music.prepare.Permission;
import com.ime.music.service.LianXiangService;

public class LianXiangActivity extends SlideBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除顶部标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
        }
        setContentView(R.layout.activity_lian_xiang);

        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LianXiangActivity.this.finish();
            }
        });

        findViewById(R.id.activity_lian_xiang_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
                Toast.makeText(LianXiangActivity.this, "请开启花生语音包 - 无障碍权限", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Permission.isAccessibilitySettingsOn(LianXiangActivity.this, LianXiangService.class.getCanonicalName())) {
            Intent intent = new Intent(LianXiangActivity.this, StartSuccess.class);
            startActivity(intent);
            LianXiangActivity.this.finish();
        }
    }
}
