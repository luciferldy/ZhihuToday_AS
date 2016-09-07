package com.luciferldy.zhihutoday_as.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import java.lang.reflect.Field;

/**
 * Created by Lucifer on 2016/8/30.
 */
public class CommonUtils {

    private static int statusBarHeight = 0;
    private static float destiny = 0;

    /**
     * get the StatusBar height
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = context.getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

    /**
     * dip to px
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        if (destiny == 0) {
            destiny = context.getResources().getDisplayMetrics().density;
        }
        return (int) (dipValue * destiny + 0.5f);
    }

    /**
     * 复制内容到粘贴板
     * @param context
     * @param key
     * @param value
     */
    public static void copyText(Context context, String key, String value) {
        ClipboardManager cbm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(key, value);
        cbm.setPrimaryClip(clipData);
    }
}
