package com.ime.music.util;

import android.content.Context;

public class Notice {

    public static void toastShort(Context context, String text) {
//        try {
//            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            //解决在子线程中调用Toast的异常情况处理
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Looper.prepare();
//                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
//                    Looper.loop();
//                }
//            }).start();
//        }
    }
}
