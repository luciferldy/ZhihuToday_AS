package com.luciferldy.zhihutoday_as.ui.activity;

import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.luciferldy.zhihutoday_as.R;
import com.luciferldy.zhihutoday_as.adapter.MainRvAdapter;
import com.luciferldy.zhihutoday_as.model.NewsGson;
import com.luciferldy.zhihutoday_as.presenter.MainPresenter;
import com.luciferldy.zhihutoday_as.ui.fragment.BaseFragment;
import com.luciferldy.zhihutoday_as.ui.fragment.StoryContentFragment;
import com.luciferldy.zhihutoday_as.ui.view.BaseSwipeRefreshView;
import com.luciferldy.zhihutoday_as.utils.CommonUtils;
import com.luciferldy.zhihutoday_as.utils.FragmentUtils;
import com.luciferldy.zhihutoday_as.utils.Logger;

import java.util.List;

public class MainActivity extends BaseActivity implements BaseSwipeRefreshView{

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
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Logger.i(LOG_TAG, "onRefresh");
                if (prepareRefresh()) {
                    onRefreshStart();
                } else {
                    stopRefresh();
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View view = findViewById(R.id.virtual_status_bar);
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) view.getLayoutParams();
            params.height = CommonUtils.getStatusBarHeight(getBaseContext());
            view.setLayoutParams(params);
            view.setVisibility(View.VISIBLE);
        }

        mRv = (RecyclerView) findViewById(R.id.main_rv);
        mRv.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        mRvAdapter = new MainRvAdapter();
        mRvAdapter.setClickItem(new MainRvAdapter.ClickItem() {
            @Override
            public void onClick(String url) {
                if (!TextUtils.isEmpty(url)) {
                    Bundle bundle = new Bundle();
                    bundle.putString(StoryContentFragment.BUNDLE_URL, url);
                    StoryContentFragment fragment = new StoryContentFragment();
                    FragmentUtils.addFragment(fragment, getSupportFragmentManager(), bundle, true);
                } else {
                    Logger.i(LOG_TAG, "url is empty.");
                }
            }
        });
        mRv.setAdapter(mRvAdapter);
        initPresenter();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mPresenter.getLatestNews();
    }

    @Override
    public void onBackPressed() {

        FragmentManager manager = getSupportFragmentManager();
        int count = manager.getBackStackEntryCount();
        if (count > 0) {
            String fName = manager.getBackStackEntryAt(count - 1).getName();
            Fragment fragment = manager.findFragmentByTag(fName);
            if (null != fragment && fragment instanceof BaseFragment) {
                ((BaseFragment) fragment).onBackPressed();
                return;
            }
        }
        super.onBackPressed();
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
        Logger.i(LOG_TAG, "fillData data=" + date);
        mRvAdapter.updateData(date, data);
    }

    public void appendMore(String date, List<NewsGson.StoriesBean> data) {
        Logger.i(LOG_TAG, "appendMore date=" + date);
        mRvAdapter.appendMoreData(date, data);
    }

    @Override
    public void startRefresh() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    public void stopRefresh() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public boolean prepareRefresh() {
        return !mPresenter.isLoading();
    }

    @Override
    public void onRefreshStart() {
        mPresenter.getLatestNews();
    }
}
