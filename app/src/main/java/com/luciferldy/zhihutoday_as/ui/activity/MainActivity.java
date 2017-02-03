package com.luciferldy.zhihutoday_as.ui.activity;

import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.luciferldy.zhihutoday_as.R;
import com.luciferldy.zhihutoday_as.adapter.DrawerRvAdapter;
import com.luciferldy.zhihutoday_as.model.ThemeListGson;
import com.luciferldy.zhihutoday_as.presenter.MainPresenter;
import com.luciferldy.zhihutoday_as.ui.fragment.BaseFragment;
import com.luciferldy.zhihutoday_as.ui.fragment.MainFragment;
import com.luciferldy.zhihutoday_as.ui.fragment.ThemeContentFragment;
import com.luciferldy.zhihutoday_as.utils.Logger;

import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private MainPresenter mPresenter;
    private DrawerRvAdapter mDrawerRvAdapter;

    private Toolbar mToolbar;
    private int mDayNightMode = AppCompatDelegate.MODE_NIGHT_AUTO; // useless
    private MenuItem mMode;
    private Fragment mCurFragment;
    private MainFragment mMainFragment;
    private ThemeContentFragment mThemeContentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i(LOG_TAG, "onCreate");

        setContentView(R.layout.activity_main);
        initPresenter();
        initView();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int uiMode = getResources().getConfiguration().uiMode;
        Logger.i(LOG_TAG, "uiMode default is " + uiMode);
        int currentMode = uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (currentMode == Configuration.UI_MODE_NIGHT_NO) {
            Logger.i(LOG_TAG, "onResume mode is Configuration.UI_MODE_NIGHT_NO.");
            mDayNightMode = AppCompatDelegate.MODE_NIGHT_NO;
            mMode.setTitle(R.string.mode_night_yes);
        } else if (currentMode == Configuration.UI_MODE_NIGHT_YES) {
            Logger.i(LOG_TAG, "onResume mode is Configuration.UI_MODE_NIGHT_YES.");
            mDayNightMode = AppCompatDelegate.MODE_NIGHT_YES;
            mMode.setTitle(R.string.mode_night_no);
        } else {
            Logger.i(LOG_TAG, "onResume mode is Configuration.UI_MODE_NIGHT_UNDEFINED?");
            mDayNightMode = AppCompatDelegate.MODE_NIGHT_AUTO;
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
    }

    @Override
    public void initPresenter() {
        mPresenter = new MainPresenter(this, this);
    }

    private void initView() {
        initToolbar();
        initDrawer();

        mMainFragment = new MainFragment();
        mMainFragment.setScrollCallback(new MainFragment.ScrollCallback() {
            @Override
            public void onTitleChanged(String title) {
                if (mCurFragment instanceof MainFragment)
                    mToolbar.setTitle(title);
            }
        });
        setCurrentFragment(mMainFragment);
        switchFragment(DrawerRvAdapter.TAG_MAIN, 0, "首页");
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//         When call Toolbar.setTitle(), in Toolbar inflate TextView and invoke addSystemView() method.
//         It will be the first child.
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
                    case R.id.action_change_mode:
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
        mMode = mToolbar.getMenu().findItem(R.id.action_change_mode);
    }

    private void initDrawer() {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer);

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.drawer_open, R.string.drawer_close){
            @Override
            public void syncState() {
                super.syncState();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Logger.i(LOG_TAG, "onDrawerOpened");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Logger.i(LOG_TAG, "onDrawerClosed");
            }
        };
        drawer.setDrawerListener(toggle);
        drawer.post(new Runnable() {
            @Override
            public void run() {
                toggle.syncState();
            }
        });
        RecyclerView drawerRv = (RecyclerView) findViewById(R.id.drawer_rv);
        drawerRv.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        drawerRv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mDrawerRvAdapter = new DrawerRvAdapter();
        mDrawerRvAdapter.setOnClickListener(new DrawerRvAdapter.OnClickListener() {
            @Override
            public void onClick(@DrawerRvAdapter.RvItemCategory int type, int themeId, String title) {
                // TODO: switch fragment
                switchFragment(type, themeId, title);
                drawer.closeDrawer(Gravity.LEFT);
            }
        });
        drawerRv.setAdapter(mDrawerRvAdapter);
        mPresenter.getThemeList();
    }

    /**
     *
     * @param type drawer item 的类型
     * @param themeId 如果是主题日报的话 需要 themeId
     * @param title 主题日报的名字
     */
    private void switchFragment(@DrawerRvAdapter.RvItemCategory int type, int themeId, String title) {
        if (type == DrawerRvAdapter.TAG_MAIN) {
            mToolbar.getMenu().findItem(R.id.action_add).setVisible(false);
            mToolbar.getMenu().findItem(R.id.action_change_mode).setVisible(true);
            toFragment(mMainFragment);
        } else if (type == DrawerRvAdapter.TAG_OTHERS) {
            mToolbar.getMenu().findItem(R.id.action_add).setVisible(true);
            mToolbar.getMenu().findItem(R.id.action_change_mode).setVisible(false);
            if (mThemeContentFragment == null) {
                mThemeContentFragment = new ThemeContentFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(ThemeContentFragment.THEME_ID, themeId);
                mThemeContentFragment.setArguments(bundle);
            } else {
                mThemeContentFragment.themeId = themeId;
            }
            toFragment(mThemeContentFragment);
            // 重新获取一下数据
            if (mThemeContentFragment.isAdded())
                mThemeContentFragment.getData();
        }
        mToolbar.setTitle(title);
    }

    private void setCurrentFragment(Fragment fragment) {
        this.mCurFragment = fragment;
    }

    private void toFragment(Fragment toFragment) {
        if (mCurFragment == null) {
            Logger.i(LOG_TAG, "mCurFragment is null.");
            return;
        }
        if (toFragment == null) {
            Logger.i(LOG_TAG, "toFragment is null");
            return;
        }
        if (toFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction()
                    .hide(mCurFragment)
                    .show(toFragment)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .hide(mCurFragment)
                    .add(R.id.container, toFragment)
                    .show(toFragment)
                    .commit();
        }
        mCurFragment = toFragment; // TODO 是否回导致问题
    }

    public void updateThemes(List<ThemeListGson.OthersBean> beans) {
        mDrawerRvAdapter.update(beans);
    }
}
