package com.ime.music;

import android.util.Log;

public class CLog {
    static public int d(String msg) {
        return Log.d("clog_d", msg);
    }
    static public int e(String msg) {
        return Log.e("clog_e", msg);
    }
    static public int i(String msg) {
        return Log.i("clog_i", msg);
    }
    static public int f(String msg) {
        return Log.e("clog_f", msg);
    }
}
