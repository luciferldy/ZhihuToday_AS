package com.luciferldy.zhihutoday_as.presenter;

import android.app.Activity;

import com.luciferldy.zhihutoday_as.ui.view.BaseView;

/**
 * Created by Lucifer on 2016/8/30.
 */
public class BasePresenter<BV extends BaseView> {

    protected BV mView;

    protected Activity mActivity;

    public BasePresenter(Activity activity, BV view) {
        this.mActivity = activity;
        this.mView = view;
    }
}
