package com.luciferldy.zhihutoday_as.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.luciferldy.zhihutoday_as.R;
import com.luciferldy.zhihutoday_as.adapter.MainRvAdapter;
import com.luciferldy.zhihutoday_as.model.NewsGson;
import com.luciferldy.zhihutoday_as.presenter.MainFragPresenter;
import com.luciferldy.zhihutoday_as.ui.view.BaseSwipeRefreshView;
import com.luciferldy.zhihutoday_as.ui.view.BaseView;
import com.luciferldy.zhihutoday_as.utils.FragmentUtils;
import com.luciferldy.zhihutoday_as.utils.Logger;

import java.util.List;

/**
 * Created by Lucifer on 2017/1/22.
 */

public class MainFragment extends Fragment implements BaseFragment, BaseSwipeRefreshView, BaseView {

    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    private MainFragPresenter mPresenter;
    private RecyclerView mRv;
    private LinearLayoutManager mLayoutManager;
    private MainRvAdapter mRvAdapter;

    private View mVStatusBar;
    private Toolbar mToolbar;
    private MenuItem mMode;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ScrollCallback mScrollCallback;
    private boolean isHidden = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);


//        mVStatusBar = root.findViewById(R.id.virtual_status_bar);
//        mToolbar = (Toolbar) root.findViewById(R.id.toolbar);
        // When call Toolbar.setTitle(), in Toolbar inflate TextView and invoke addSystemView() method.
        // It will be the first child.
//        mToolbar.setTitle(R.string.app_name);
//        for (int i = 0; i < mToolbar.getChildCount(); i++) {
//            Logger.i(LOG_TAG, "View " + mToolbar.getChildAt(i).toString());
//        }
//        mToolbar = (Toolbar) root.findViewById(R.id.toolbar);
//        mToolbar.setTitle(R.string.des_main);
//        View titleView = mToolbar.getChildAt(0);
//        if (titleView instanceof TextView) {
//            Logger.i(LOG_TAG, "child at 0 is title.");
//            ((AppCompatTextView) titleView).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
//        }
//
//        mToolbar.inflateMenu(R.menu.menu_main);
//        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.change_mode:
//                        Logger.i(LOG_TAG, "onMenuItemClick change mode.");
//                        /**
//                         * 可以使用 uiMode 更新夜间模式
//                         * DisplayMetrics dm = sRes.getDisplayMetrics()
//                         * Configuration config = sRes.getConfiguration()
//                         * config.uiMode &= ~Configuration.UI_MODE_NIGHT_MASK
//                         * config.uiMode | on ? Configuration.UI_MODE_NIGHT_YES : Configuration.UI_MODE_NIGHT_NO
//                         * sRes.updateConfiguration(config, dm)
//                         */
//                        if (item.getTitle().equals(getResources().getString(R.string.mode_night_yes))) {
//                            /**
//                             * AppCompatDelegate.setDefaultNightMode 的设置是对整个 APP 的 theme 有效
//                             * getDelegate.setLocalNightMode 的设置只是对于设置的地方有效
//                             * getDelegate 设置的效果可以覆盖 AppCompatDelegate.setDefaultNightMode 效果
//                             */
//                            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                            getActivity().recreate();
//                        } else {
//                            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                            getActivity().recreate();
//                        }
//                        return true;
//                    default:
//                        Logger.i(LOG_TAG, "onMenuItemClick default.");
//                        return false;
//                }
//            }
//        });
//        setSupportActionBar(mToolbar);
//        mMode = mToolbar.getMenu().findItem(R.id.change_mode);

        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_layout);
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

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mVStatusBar.getLayoutParams();
//            params.height = CommonUtils.getStatusBarHeight(getBaseContext());
//            mVStatusBar.setLayoutParams(params);
//            mVStatusBar.setVisibility(View.VISIBLE);
//        }

        mRv = (RecyclerView) root.findViewById(R.id.main_rv);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRv.setLayoutManager(mLayoutManager);
        mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                Logger.i(LOG_TAG, "onScrollStateChanged. LayoutManager.findLastCompletelyVisibleItemPosition=" + mLayoutManager.findLastCompletelyVisibleItemPosition()
//                        + ", Item count=" + mRvAdapter.getItemCount());
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    boolean isBottom = mLayoutManager.findLastCompletelyVisibleItemPosition() > mRvAdapter.getItemCount() - 3;
                    if (prepareRefresh() && isBottom) {
                        Logger.i(LOG_TAG, "slide to the bottom, ready to load more data.");
                        mPresenter.getBeforeNews();
                    }
                }
                // TODO change the title when scroll to title
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                String des = mRvAdapter.getDescription(mLayoutManager.findFirstVisibleItemPosition());
                des = TextUtils.isEmpty(des) ? getString(R.string.app_name) : des;
//                mToolbar.setTitle(des);
                if (!isHidden && mScrollCallback != null) {
                    mScrollCallback.onTitleChanged(des);
                }
            }
        });
//        mRv.setItemAnimator(new DefaultItemAnimator());
        mRvAdapter = new MainRvAdapter();
        mRvAdapter.setClickItem(new MainRvAdapter.ClickItem() {
            @Override
            public void onClick(String url) {
                if (!TextUtils.isEmpty(url)) {
                    Bundle bundle = new Bundle();
                    bundle.putString(NewsDetailFragment.BUNDLE_URL, url);
                    NewsDetailFragment fragment = new NewsDetailFragment();
                    FragmentUtils.addFragment(fragment, getFragmentManager(), bundle, true);
                } else {
                    Logger.i(LOG_TAG, "url is empty.");
                }
            }
        });
        mRv.setAdapter(mRvAdapter);
        initPresenter();
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //  Use reflection to get private variable mTitleTextView. It will take some time.
//        try {
//            Class<? extends Toolbar> cls = mToolbar.getClass();
//            Field privateTvFiled = cls.getDeclaredField("mTitleTextView");
//            if (privateTvFiled != null) {
                // 使用 private 类型的变量，就需要抑制 java 对权限的检查
//                privateTvFiled.setAccessible(true);
//                TextView tv = (TextView) privateTvFiled.get(mToolbar);
//                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
//                Logger.i(LOG_TAG, "use reflection to change the title size.");
//            }
//        } catch (ClassCastException | NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Logger.i(LOG_TAG, "use reflection occur exception.");
//        }

        Logger.i(LOG_TAG, "prepareRefresh ? " + prepareRefresh());
        if (prepareRefresh()) {
            mPresenter.getLatestNews();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Logger.i(LOG_TAG, "onHiddenChanged hidden: " + hidden);
        isHidden = hidden;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unSubscribe();
    }

    @Override
    public void initPresenter() {
        mPresenter = new MainFragPresenter(getActivity(), this);
    }

    @Override
    public void onBackPressed() {

    }

    public void fillData(String date, NewsGson newsGson) {
        Logger.i(LOG_TAG, "fillData data=" + date);
        mRvAdapter.updateData(date, newsGson);
        stopRefresh();
    }

    public void appendMore(String date, List<NewsGson.StoriesBean> storiesBean) {
        Logger.i(LOG_TAG, "appendMore date = " + date);
        mRvAdapter.appendMoreData(date, storiesBean);
    }

    @Override
    public boolean prepareRefresh() {
        return !mPresenter.isLoading();
    }

    @Override
    public void onRefreshStart() {
        mPresenter.getLatestNews();
    }

    @Override
    public void startRefresh() {
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void stopRefresh() {
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(false);
    }

    public void setScrollCallback(ScrollCallback callback) {
        this.mScrollCallback = callback;
    }

    public interface ScrollCallback {
        void onTitleChanged(String title);
    }
}
