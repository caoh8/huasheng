package com.ime.music.util;

import android.app.AlertDialog;
import android.content.Context;

import com.ime.music.FloatManagerMusic;

import java.util.ArrayList;
import java.util.Map;

public class ConstantUtil {
    static private final String host = "http://srf.kcapp.cn";
//    static private final String host = "http://172.19.10.112";
//    static private final String port = ":8090";
    static private final String port = "";

//    static public final String http_search_song = "http://172.19.37.128/song/search/?";
//    static public final String http_song_source = "http://172.19.37.128/song/download?";
    static public final String pot_audio_behavior = host + port + "/audio/update";
    static public final String post_report = host + port + "/report";
    static public final String post_category = host + port + "/audio/category";
//    static public final String post_category = "http://172.19.10.112:8090/audio/category";
    static public final String http_search_song = host + port + "/song/search/?";
    static public final String http_song_source = host + port + "/song/download?";
    static public final String http_song_tip = host + port + "/keywords/search?";
//    static public final String http_song_tip = "http://searchsuggest.kugou.com/v2/word_recommend?";
    static public final String http_audio_search = host + port + "/audio/search?";
    static public final String http_audio_filter = host + port + "/audio/filter?";
    static public final String http_audio_source = host + port + "/audio/download?";
    static public final String http_audio_hot = host + port + "/audio/hot";

    static public final String imageUrl = host + "/audios/test.png";

    static public final String shareSoftUrl = host + port + "/share/url";
    static public final String checkSoftUrl = host + port + "/version/check?";
//    static public String softHash = "756829dda53dcc964b7481bbf1a";
    static public String softHash = "3a783136483fbc7db9e492ad5c7d91b0";
    static public String softDownloadAddress;
    static public boolean hasNew = false;
    static public String version;
    static public final String staticSoftUrl = host + "/index";//分享软件请求失败，使用默认url
    static public final String webUrl = "www.kugou.com";


    static public String userDataDir;
    static public String sharedDataDir;
    static public String sharedDataDirSimple;

    static public final String MUSICIME = "musicime";

    static public int keyBoardDefaultHeight = -1;
    static public int keyBoardKeyWidth = -1;
    static public int keyBoardHorizontalGap = -1;

    //应用
    public enum APP_ENUM {
        QQ,
        WEIXIN,
        ORTHER
    }
    static public APP_ENUM currentApp = APP_ENUM.ORTHER;
    static public String weixin = "com.tencent.mm";
    static public String qq = "com.tencent.mobileqq";

    static public int imeOption = 0;

    // 悬浮窗
    public static void init(Context appContext) {
        ConstantUtil.appContext = appContext;
    }
    public static Context getAppContext() {
        return appContext;
    }

    static private Context appContext;
    static private FloatManagerMusic floatManagerMusic;

    static public FloatManagerMusic getFloatManagerMusic() {
        if (null == floatManagerMusic) floatManagerMusic = new FloatManagerMusic(appContext);
        return floatManagerMusic;
    };

    // 播放延时
    public static int PlayDelay = 2;
    public static int PlayDelayCount = 0;

    // 分类结果
    public static ArrayList<Map<String, Object>> CategoryResult;
}
