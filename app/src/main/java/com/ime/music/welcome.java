package com.ime.music;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ime.music.prepare.FloatWindowManager;
import com.ime.music.util.DeviceInfo;
import com.tendcloud.tenddata.TCAgent;

import java.util.HashMap;
import java.util.Map;

public class welcome extends Activity {

    protected void onPause() {
        super.onPause();

        TCAgent.onPageEnd(welcome.this, "开机启动页");
    }

    protected void onResume() {
        super.onResume();

        TCAgent.onPageStart(welcome.this, "开机启动页");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取当前窗口，并调用windowsManager来控制窗口
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //去除顶部标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
        setContentView(R.layout.activity_welcome);

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("permission", Context.MODE_PRIVATE);
        if (preferences.getBoolean("q", true)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("q", false);
            editor.apply();
            Map<Object, Object> kv = new HashMap<>();
            kv.put("品牌", DeviceInfo.getDeviceBrand());
            kv.put("型号", DeviceInfo.getDeviceModel());
            TCAgent.onEvent(getApplicationContext(), "品牌信息", "", kv);
        }

        TextView version = findViewById(R.id.welcome_tv_version);
        version.setText(String.format("V%s", DeviceInfo.getClientver()));

        Thread myThread = new Thread() {//创建子线程
            public void run() {
                try {
                    sleep(1500);//使程序休眠1.5秒
                    if (!FloatWindowManager.getInstance().checkPermission(welcome.this)) {
                        Intent intent = new Intent(welcome.this, FloatWindowActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(welcome.this, StartSuccess.class);
                        startActivity(intent);
                    }
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();//启动线程
    }


}
