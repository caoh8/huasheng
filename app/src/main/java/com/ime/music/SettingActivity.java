package com.ime.music;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mob.MobSDK;
import com.tendcloud.tenddata.TCAgent;

import java.util.Timer;
import java.util.TimerTask;

import static com.ime.music.prepare.Permission.IME.isDefault;
import static com.ime.music.prepare.Permission.IME.isEnabled;


public class SettingActivity extends Activity {
    private Button mBtnEnable;
    private Button mBtnDefual;
    private Status status;

    private String currentInputmethod;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private LinearLayout loadingLayout;

    private enum Status {
        picking,
        non,
        chosen,
        wait
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //去除顶部标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
        }
        setContentView(R.layout.activity_setting);

        preferences = getSharedPreferences("GuildActivity", MODE_PRIVATE);

        if (preferences.getBoolean("firstStart", true)){
            //跳转到引导页
            Intent intent = new Intent();
            intent.setClass(this, GuildActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
        }

        MobSDK.init(this);
        mBtnDefual = findViewById(R.id.btn_setdefault);
        mBtnEnable = findViewById(R.id.btn_startim);

        loadingLayout = findViewById(R.id.setting_loading);
        loadingLayout.setVisibility(View.GONE);
        init();

        //实现自动跳转
        Timer timer=new Timer();
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                //通过Intent实现跳转
                if (isDone()) {
                    startActivity(new Intent(SettingActivity.this, StartSuccess.class));
                    SettingActivity.this.finish();
                }
            }
        };
        //延迟2秒后跳转，注意单位是毫秒
        timer.schedule(timerTask,50);
    }

    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(SettingActivity.this,"设置输入法页");
    }

    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(SettingActivity.this,"设置输入法页");
//        Permission.Storage.verifyStoragePermissions(SettingActivity.this);
//
//        if(!Permission.Float.verifyFloatPermission(getApplicationContext())) {
//            startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                    Uri.parse("package:" + getPackageName())));
//        }

        freshBtn();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        whatDefault();
    }

    private void showLoading() {
        if (null == currentInputmethod || currentInputmethod.equals(
                Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.DEFAULT_INPUT_METHOD))) {
            loadingLayout.post(new Runnable() {
                @Override
                public void run() {
                    if (loadingLayout.getVisibility() != View.VISIBLE)
                        loadingLayout.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void showPicker() {
//        loadingLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                loadingLayout.setVisibility(View.GONE);
//            }
//        });
        currentInputmethod = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.DEFAULT_INPUT_METHOD);
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showInputMethodPicker();
    }

    private void whatDefault() {
        switch (status) {
            case non:
                break;
            case chosen:
                CLog.d("chosen");

//                showLoading();

                Timer timer=new Timer();
                TimerTask timerTask=new TimerTask() {
                    @Override
                    public void run() {
                        if (status == Status.chosen) {
                            if (!isDefault(SettingActivity.this)) {
                                status = Status.picking;
                                showPicker();
                            } else {
                                status = Status.non;
                                startStartSuccess();
                            }
                        }
                    }
                };
                timer.schedule(timerTask,20);
                break;
            case picking:
                CLog.d("picking");
                status = Status.chosen;
                if (isDefault(SettingActivity.this)) {
                    status = Status.non;
                    startStartSuccess();
                }
                break;
            case wait:
                status = Status.non;
                break;
        }
    }

    private boolean isDone() {
        return isEnabled(SettingActivity.this) && isDefault(SettingActivity.this);
    }

    void init() {
        mBtnEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEnabled(SettingActivity.this)) startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
            }
        });

        //点击跳转启动成功
        mBtnDefual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getSharedPreferences("setDefault", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("do_set_default", true);
                editor.apply();
                loadingLayout.setVisibility(View.VISIBLE);
                if (!isDefault(SettingActivity.this)) {
                    status = Status.picking;
                    showLoading();
                    showPicker();
                } else {
                    startStartSuccess();
                }
            }
        });
        status = Status.non;
        freshBtn();
    }

    private void startStartSuccess() {
        Intent intent = new Intent(SettingActivity.this, StartSuccess.class);
        startActivity(intent);
        SettingActivity.this.finish();
    }

    private void freshBtn() {
        if (isEnabled(SettingActivity.this)) {
            mBtnEnable.setBackgroundColor(Color.parseColor("#ffffff"));
            mBtnEnable.setTextColor(Color.parseColor("#CCCCCC"));
            mBtnEnable.setEnabled(false);
            if (isDefault(SettingActivity.this)) {
                mBtnDefual.setBackgroundColor(Color.parseColor("#ffffff"));
                mBtnDefual.setTextColor(Color.parseColor("#CCCCCC"));
                mBtnDefual.setEnabled(false);
            } else {
                mBtnDefual.setBackgroundColor(Color.parseColor("#4466EE"));
                mBtnDefual.setBackground(getDrawable(R.drawable.btn_blue) );
                mBtnDefual.setTextColor(Color.parseColor("#ffffff"));
                mBtnDefual.setEnabled(true);
            }
        } else {
            mBtnEnable.setBackgroundColor(Color.parseColor("#4466EE"));
            mBtnEnable.setBackground(getDrawable(R.drawable.btn_blue) );
            mBtnEnable.setTextColor(Color.parseColor("#ffffff"));
            mBtnEnable.setEnabled(true);
            mBtnDefual.setBackgroundColor(Color.parseColor("#ffffff"));
            mBtnDefual.setTextColor(Color.parseColor("#CCCCCC"));
            mBtnDefual.setEnabled(false);
        }
    }
}
