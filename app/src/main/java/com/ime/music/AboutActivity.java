package com.ime.music;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.tendcloud.tenddata.TCAgent;

public class AboutActivity extends SlideBackActivity {

    private TextView mBtnABack;
    private Drawable back;
    private Button mBtnAgreement;
    private Button mBtnPolicy;
    private Drawable jianyou_you;

    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(AboutActivity.this,"关于");
    }

    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(AboutActivity.this,"关于");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除顶部标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(Color.parseColor("#F9F9F9"));
        }
        setContentView(R.layout.activity_about);

        mBtnABack = findViewById(R.id.btn_about_back);
        Drawable jiantou_zuo = ContextCompat.getDrawable(AboutActivity.this, R.drawable.jiantou_zuo_std);
        jiantou_zuo.setBounds(0,0,50,50);
        mBtnABack.setCompoundDrawables(jiantou_zuo,null,null,null);
        mBtnABack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

//        back = getResources().getDrawable(R.drawable.jiantou_zuo);
//        back.setBounds(0,0,60,60);
//        mBtnABack.setCompoundDrawables(back,null,null,null);



        jianyou_you = ContextCompat.getDrawable(getApplicationContext(), R.drawable.jiantou);
        jianyou_you.setBounds(0,0,50,50);
        mBtnAgreement = findViewById(R.id.btn_user_agreement);
        mBtnAgreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AboutActivity.this,XieyiActivity.class);
                startActivity(intent);
            }
        });
        mBtnAgreement.setCompoundDrawables(null,null,jianyou_you,null);
        mBtnPolicy = findViewById(R.id.btn_privacy_policy);
        mBtnPolicy.setCompoundDrawables(null,null,jianyou_you,null);
        mBtnPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AboutActivity.this,YinsiActivity.class);
                startActivity(intent);
            }
        });



    }


}
