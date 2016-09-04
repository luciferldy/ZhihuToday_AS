package com.luciferldy.zhihutoday_as.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by Lucifer on 2016/9/4.
 * Fragment 工具类
 */
public class FragmentUtils {

    private static final String LOG_TAG = FragmentUtils.class.getSimpleName();

    public static Fragment addFragment(Fragment fragment, FragmentManager manager, Bundle bundle, boolean addToBackStack) {
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(android.R.id.content, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getSimpleName() + System.currentTimeMillis());
        }
        Logger.i(LOG_TAG, "add Fragment " + fragment.getClass().getSimpleName());
        transaction.commit();
        return fragment;
    }

    /**
     * 弹出 popStack 的 Fragment
     * @param manager
     */
    public static void popBackStack(FragmentManager manager) {
        manager.popBackStack();
    }
}
