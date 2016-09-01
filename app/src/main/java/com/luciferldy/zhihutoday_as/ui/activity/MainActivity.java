package com.luciferldy.zhihutoday_as.ui.activity;

import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.luciferldy.zhihutoday_as.R;
import com.luciferldy.zhihutoday_as.adapter.MainRvAdapter;
import com.luciferldy.zhihutoday_as.model.NewsGson;
import com.luciferldy.zhihutoday_as.presenter.MainPresenter;
import com.luciferldy.zhihutoday_as.ui.view.BaseView;
import com.luciferldy.zhihutoday_as.utils.CommonUtils;
import com.luciferldy.zhihutoday_as.utils.Logger;

import java.util.List;

public class MainActivity extends BaseActivity{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRv;
    private MainRvAdapter mRvAdapter;
    private MainPresenter mPresenter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        View titleView = toolbar.getChildAt(0);
        if (titleView instanceof TextView) {
            Logger.i(LOG_TAG, "child at 0 is instance of TextView");
            ((AppCompatTextView) titleView).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View view = findViewById(R.id.virtual_status_bar);
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) view.getLayoutParams();
            params.height = CommonUtils.getStatusbarHeight(getBaseContext());
            view.setLayoutParams(params);
            view.setVisibility(View.VISIBLE);
        }

        mRv = (RecyclerView) findViewById(R.id.main_rv);
        mRv.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        mRvAdapter = new MainRvAdapter();
        mRv.setAdapter(mRvAdapter);
        initPresenter();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mPresenter.getLatestNews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unSubscribe();
    }

    @Override
    public void initPresenter() {
        mPresenter = new MainPresenter(this, this);
    }

    public void fillData(String date, NewsGson data) {
        Logger.i(LOG_TAG, "fillData");
        mRvAdapter.updateData(date, data);
    }

    public void appendMore(String date, List<NewsGson.StoriesBean> data) {
        Logger.i(LOG_TAG, "appendMore date=" + date);
    }


}
