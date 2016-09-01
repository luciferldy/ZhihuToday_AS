package com.luciferldy.zhihutoday_as.utils;

import android.util.Log;

/**
 * Created by Lucifer on 2016/8/30.
 * 自定义打印日志类
 */
public class Logger {

    private static boolean isDebug = true;

    public static void setIsDebug(boolean arg) {
        isDebug = arg;
    }

    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, msg);
        }
    }
}
