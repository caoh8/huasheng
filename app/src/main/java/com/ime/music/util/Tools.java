package com.ime.music.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

import com.ime.music.CLog;
import com.ime.music.R;
import com.ime.music.StartSuccess;
import com.ime.music.info.AudioInfo;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {
    static public String ModifyStr(String s) {
        String ret = null;
        String regex = "[A-Za-z0-9\\u4e00-\\u9fa5]+";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);

        while (matcher.find()) {
            ret = matcher.group();
        }
        return ret;
    }

    static public int ModifyNum(String s) {
        String ret = null;
        String regex = "[0-9]+|[0-9]";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);

        while (matcher.find()) {
            ret = matcher.group();
        }

        int num = 0;
        if (ret != null) {
            num = Integer.parseInt(ret);
        }
        return num;
    }

    //定位数字开始的位置
    static public int posNum(String s) {
        String regex = "[0-9]+[0-9']*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {
            return matcher.start();
        }
        return -1;
    }

    static public int posComposing(String s) {
        String regex = "[0-9a-z]+[0-9'a-z]*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {
            return matcher.start();
        }
        return -1;
    }

    public static int dip2px(float dpValue) {
        final float scale = ConstantUtil.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static boolean isHeadSetON() {
        AudioManager localAudioManager = (AudioManager) ConstantUtil.getAppContext().getSystemService(Context.AUDIO_SERVICE);
        return localAudioManager.isWiredHeadsetOn();
    }

    public static boolean isMusicVolumeZero() {
        AudioManager localAudioManager = (AudioManager) ConstantUtil.getAppContext().getSystemService(Context.AUDIO_SERVICE);
        if (localAudioManager != null) {
            return localAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) <= 1;
        } else {
            return false;
        }
    }

    public static void vibrate() {
        Vibrator vibrator = (Vibrator) ConstantUtil.getAppContext().getSystemService(Service.VIBRATOR_SERVICE);
        long[] pattern = {100, 500};   // 停止 开启 停止 开启
        if (vibrator != null) {
            vibrator.vibrate(pattern, -1);
        }
    }

    private static final String CHANNEL_ID = "channel_id";   //通道渠道id
    public static final String CHANEL_NAME = "chanel_name"; //通道渠道名称

    public static void notice() {
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //创建 通知通道  channelid和channelname是必须的（自己命名就好）
            channel = new NotificationChannel(CHANNEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
//            channel.enableLights(true);//是否在桌面icon右上角展示小红点
            channel.setLightColor(Color.GREEN);//小红点颜色
            channel.setShowBadge(false); //是否在久按桌面图标时显示此渠道的通知
        }
        Notification notification;
        final String str = "打开悬浮窗，用音频加点料";
        //获取Notification实例   获取Notification实例有很多方法处理    在此我只展示通用的方法（虽然这种方式是属于api16以上，但是已经可以了，毕竟16以下的Android机很少了，如果非要全面兼容可以用）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //向上兼容 用Notification.Builder构造notification对象
            notification = new Notification.Builder(ConstantUtil.getAppContext(), CHANNEL_ID)
                    .setContentTitle(str)
//                    .setContentText("这是消息通过通知栏的内容")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.qiu)
//                    .setColor(Color.parseColor("#FEDA26"))
                    .setLargeIcon(BitmapFactory.decodeResource(ConstantUtil.getAppContext().getResources(), R.drawable.qiu))
//                    .setTicker("巴士门")
                    .build();
        } else {
            //向下兼容 用NotificationCompat.Builder构造notification对象
            notification = new NotificationCompat.Builder(ConstantUtil.getAppContext())
                    .setContentTitle(str)
//                    .setContentText("这是消息通过通知栏的内容")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.qiu)
//                    .setColor(Color.parseColor("#FEDA26"))
                    .setLargeIcon(BitmapFactory.decodeResource(ConstantUtil.getAppContext().getResources(), R.drawable.qiu))
//                    .setTicker("巴士门")
                    .build();
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(ConstantUtil.getAppContext(), StartSuccess.class);
        intent.setPackage("com.ime.music");
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        notification.contentIntent = PendingIntent.getActivity(ConstantUtil.getAppContext(), 1, intent, 0);

        //发送通知
        int notifiId = 1;
        //创建一个通知管理器
        NotificationManager notificationManager = (NotificationManager) ConstantUtil.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        if (notificationManager != null) {
            notificationManager.notify(notifiId, notification);
        }
    }

    public static void hotAdd(ArrayList<Map<String, Object>> result, ArrayList<AudioInfo> data) {
        for (int i = 0; i < result.size() && i < 32; ++i) {
            AudioInfo info = new AudioInfo();
            info.setDuration((int) result.get(i).get("Duration"));
            info.setAudioName((String) result.get(i).get("AudioName"));
            info.setFileHash((String) result.get(i).get("FileHash"));
            String id = (String) result.get(i).get("ID");
            info.setId(id);
            info.setSource((String) result.get(i).get("Source"));
            if (id.equals("1982217794400110") || id.equals("1982217756651374")) continue;
            data.add(info);
        }
    }
}
