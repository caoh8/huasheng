package com.ime.music;

import android.app.Application;
import android.content.Context;

import com.ime.music.util.ConstantUtil;
import com.tendcloud.tenddata.TCAgent;

public class MainApplication extends Application {
    public void onCreate() {
        super.onCreate();
        TCAgent.LOG_ON=true;
        // App ID: 在TalkingData创建应用后，进入数据报表页中，在“系统设置”-“编辑应用”页面里查看App ID。
        // 渠道 ID: 是渠道标识符，可通过不同渠道单独追踪数据。
        TCAgent.init(this, "2FF046F5CF214681A69DB1F2C9936A78", "Your_channel_id");
        // 如果已经在AndroidManifest.xml配置了App ID和渠道ID，调用TCAgent.init(this)即可；或与AndroidManifest.xml中的对应参数保持一致。
        TCAgent.setReportUncaughtExceptions(true);

        //初始化context
        ConstantUtil.init(getApplicationContext());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        androidx.multidex.MultiDex.install(this);
    }
}
