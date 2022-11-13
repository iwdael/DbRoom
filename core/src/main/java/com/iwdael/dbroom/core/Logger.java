package com.iwdael.dbroom.core;

import android.util.Log;

/**
 * @author : iwdael
 * @mail : iwdael@outlook.com
 * @project : https://github.com/iwdael/DbRoom
 */
public class Logger {
    public static boolean enable = false;

    public static void v(String tag, String content) {
        if (!enable) return;
        Log.v(tag, content);
    }

    public static void i(String tag, String content) {
        if (!enable) return;
        Log.i(tag, content);
    }

    public static void d(String tag, String content) {
        if (!enable) return;
        Log.d(tag, content);
    }

    public static void w(String tag, String content) {
        if (!enable) return;
        Log.w(tag, content);
    }

    public static void e(String tag, String content) {
        if (!enable) return;
        Log.e(tag, content);
    }

}
