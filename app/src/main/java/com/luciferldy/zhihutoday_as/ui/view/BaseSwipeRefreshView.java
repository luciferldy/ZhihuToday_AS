package com.luciferldy.zhihutoday_as.ui.view;

/**
 * Created by Lucifer on 2016/9/5.
 * SwipeRefreshView 基础类
 */
public interface BaseSwipeRefreshView {
    boolean prepareRefresh();
    void onRefreshStart();
    void startRefresh();
    void stopRefresh();
}
