package com.luciferldy.zhihutoday_as.ui.activity;

import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.MenuItem;
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

import java.lang.reflect.Field;
import java.util.List;

public class MainActivity extends BaseActivity implements BaseSwipeRefreshView{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRv;
    private Toolbar mToolbar;
    private MenuItem mMode;
    private View mVStatusBar;
    private MainRvAdapter mRvAdapter;
    private MainPresenter mPresenter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

//    private int mDayNightMode = AppCompatDelegate.MODE_NIGHT_AUTO; // useless

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i(LOG_TAG, "onCreate");

        setContentView(R.layout.activity_main);
        mVStatusBar = findViewById(R.id.virtual_status_bar);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        // When call Toolbar.setTitle(), in Toolbar inflate TextView and invoke addSystemView() method.
        // It will be the first child.
        mToolbar.setTitle(R.string.app_name);
        for (int i = 0; i < mToolbar.getChildCount(); i++) {
            Logger.i(LOG_TAG, "View " + mToolbar.getChildAt(i).toString());
        }
        View titleView = mToolbar.getChildAt(0);
        if (titleView instanceof TextView) {
            Logger.i(LOG_TAG, "child at 0 is title.");
            ((AppCompatTextView) titleView).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        }

        mToolbar.inflateMenu(R.menu.menu_main);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.change_mode:
                        Logger.i(LOG_TAG, "onMenuItemClick change mode.");
                        /**
                         * 可以使用 uiMode 更新夜间模式
                         * DisplayMetrics dm = sRes.getDisplayMetrics()
                         * Configuration config = sRes.getConfiguration()
                         * config.uiMode &= ~Configuration.UI_MODE_NIGHT_MASK
                         * config.uiMode | on ? Configuration.UI_MODE_NIGHT_YES : Configuration.UI_MODE_NIGHT_NO
                         * sRes.updateConfiguration(config, dm)
                         */
                        if (item.getTitle().equals(getResources().getString(R.string.mode_night_yes))) {
                            /**
                             * AppCompatDelegate.setDefaultNightMode 的设置是对整个 APP 的 theme 有效
                             * getDelegate.setLocalNightMode 的设置只是对于设置的地方有效
                             * getDelegate 设置的效果可以覆盖 AppCompatDelegate.setDefaultNightMode 效果
                             */
                            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            recreate();
                        } else {
                            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            recreate();
                        }
                        return true;
                    default:
                        Logger.i(LOG_TAG, "onMenuItemClick default.");
                        return false;
                }
            }
        });
        mMode = mToolbar.getMenu().findItem(R.id.change_mode);

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
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mVStatusBar.getLayoutParams();
            params.height = CommonUtils.getStatusBarHeight(getBaseContext());
            mVStatusBar.setLayoutParams(params);
            mVStatusBar.setVisibility(View.VISIBLE);
        }

        mRv = (RecyclerView) findViewById(R.id.main_rv);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        mRv.setLayoutManager(layoutManager);
        mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                Logger.i(LOG_TAG, "onScrollStateChanged. LayoutManager.findLastCompletelyVisibleItemPosition=" + layoutManager.findLastCompletelyVisibleItemPosition()
//                        + ", Item count=" + mRvAdapter.getItemCount());
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    boolean isBottom = layoutManager.findLastCompletelyVisibleItemPosition() > mRvAdapter.getItemCount() - 3;
                    if (prepareRefresh() && isBottom) {
                        Logger.i(LOG_TAG, "slide to the bottom, ready to load more data.");
                        mPresenter.getEarlierNews();
                    }
                }
                // TODO change the title when scroll to title
            }
        });
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
        //  Use reflection to get private variable mTitleTextView. It will take some time.
        try {
            Class<? extends Toolbar> cls = mToolbar.getClass();
            Field privateTvFiled = cls.getDeclaredField("mTitleTextView");
            if (privateTvFiled != null) {
                // 使用 private 类型的变量，就需要抑制 java 对权限的检查
                privateTvFiled.setAccessible(true);
                TextView tv = (TextView) privateTvFiled.get(mToolbar);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                Logger.i(LOG_TAG, "use reflection to change the title size.");
            }
        } catch (ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.i(LOG_TAG, "use reflection occur exception.");
        }

        if (prepareRefresh()) {
            mPresenter.getLatestNews();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int uiMode = getResources().getConfiguration().uiMode;
        Logger.i(LOG_TAG, "uiMode default is " + uiMode);
        int currentMode = uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (currentMode == Configuration.UI_MODE_NIGHT_NO) {
            Logger.i(LOG_TAG, "onResume mode is Configuration.UI_MODE_NIGHT_NO.");
//            mDayNightMode = AppCompatDelegate.MODE_NIGHT_NO;
            mMode.setTitle(R.string.mode_night_yes);
        } else if (currentMode == Configuration.UI_MODE_NIGHT_YES) {
            Logger.i(LOG_TAG, "onResume mode is Configuration.UI_MODE_NIGHT_YES.");
//            mDayNightMode = AppCompatDelegate.MODE_NIGHT_YES;
            mMode.setTitle(R.string.mode_night_no);
        } else {
            Logger.i(LOG_TAG, "onResume mode is Configuration.UI_MODE_NIGHT_UNDEFINED?");
//            mDayNightMode = AppCompatDelegate.MODE_NIGHT_AUTO;
            mMode.setTitle(R.string.mode_night_yes);
        }
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
        stopRefresh();
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
