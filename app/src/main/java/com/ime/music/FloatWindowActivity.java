package com.ime.music;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.ime.music.prepare.FloatWindowManager;
import com.ime.music.util.ConstantUtil;

public class FloatWindowActivity extends Activity {

    private Button btnFloat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除顶部标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));
        }
        setContentView(R.layout.activity_float_window);

        btnFloat = findViewById(R.id.float_window_btn_float);

        btnFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FloatWindowManager.getInstance().checkPermission(FloatWindowActivity.this)) {
                    Intent intent = new Intent(FloatWindowActivity.this, StartSuccess.class);
                    startActivity(intent);
                    FloatWindowActivity.this.finish();
                } else {
                    FloatWindowManager.getInstance().apply(FloatWindowActivity.this);
                }
            }
        });
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (FloatWindowManager.getInstance().checkPermission(FloatWindowActivity.this)) {
                Intent intent = new Intent(FloatWindowActivity.this, StartSuccess.class);
                startActivity(intent);
                FloatWindowActivity.this.finish();
            } else {
                handler.postDelayed(runnable, 5);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        if (FloatWindowManager.getInstance().checkPermission(FloatWindowActivity.this)) {
            Intent intent = new Intent(FloatWindowActivity.this, StartSuccess.class);
            startActivity(intent);
            this.finish();
        }

        handler.post(runnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
