package com.luciferldy.zhihutoday_as.utils;

import android.content.Context;
import android.content.Intent;

/**
 * Created by lian_ on 2016/9/7.
 */
public class ShareUtils  {

    private static final String LOG_TAG = ShareUtils.class.getSimpleName();

    /**
     * 分享文字
     * @param context
     * @param text
     */
    public static void shareText(Context context, String text) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.setType("name/plain");

        //设置分享列表的标题，并且每次都显示分享列表
        context.startActivity(Intent.createChooser(shareIntent, "分享到"));
    }
}
