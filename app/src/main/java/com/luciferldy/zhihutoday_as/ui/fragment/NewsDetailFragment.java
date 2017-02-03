package com.luciferldy.zhihutoday_as.ui.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.luciferldy.zhihutoday_as.R;
import com.luciferldy.zhihutoday_as.ui.view.BaseSwipeRefreshView;
import com.luciferldy.zhihutoday_as.ui.view.BaseView;
import com.luciferldy.zhihutoday_as.utils.CommonUtils;
import com.luciferldy.zhihutoday_as.utils.FragmentUtils;
import com.luciferldy.zhihutoday_as.utils.Logger;
import com.luciferldy.zhihutoday_as.utils.ShareUtils;

/**
 * Created by Lucifer on 2016/9/4.
 * 新闻内容详情
 */
public class NewsDetailFragment extends Fragment implements BaseFragment, BaseSwipeRefreshView, BaseView {

    public static final String BUNDLE_URL = "story_url";
    private static final String LOG_TAG = NewsDetailFragment.class.getSimpleName();

    private WebView mWebView;
    private View mVStatusBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private String mUrl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.news_content, container, false);
        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mVStatusBar = root.findViewById(R.id.virtual_status_bar);
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mVStatusBar.getLayoutParams();
            params.height = CommonUtils.getStatusBarHeight(getContext());
            mVStatusBar.setLayoutParams(params);
            mVStatusBar.setVisibility(View.VISIBLE);
        }

        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_news_content);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.open_in_browser:
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
                        startActivity(intent);
                        break;
                    case R.id.share:
                        ShareUtils.shareText(getContext(), mUrl);
                        break;
                    case R.id.copy_url:
                        CommonUtils.copyText(getContext(), NewsDetailFragment.BUNDLE_URL, mUrl);
                        Toast.makeText(getContext(), "已经复制到剪贴板", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Logger.i(LOG_TAG, "MenuItem click default value.");
                }
                return true;
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.i(LOG_TAG, "Navigation onClick.");
                onBackPressed();
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshStart();
            }
        });

        mWebView = (WebView) root.findViewById(R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new CustomWebViewClient());
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null && !bundle.isEmpty()) {
            mUrl = bundle.getString(BUNDLE_URL);
            mWebView.loadUrl(mUrl);
        } else {
            Logger.i(LOG_TAG, "bundle is null or bundle is empty.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        int uiMode = getResources().getConfiguration().uiMode;
        int dayNightUiMode = uiMode & Configuration.UI_MODE_NIGHT_MASK;

//        if (dayNightUiMode == Configuration.UI_MODE_NIGHT_NO) {
//            mVStatusBar.setBackgroundResource(R.color.colorPrimaryDark);
//        } else if (dayNightUiMode == Configuration.UI_MODE_NIGHT_YES) {
//            mVStatusBar.setBackgroundResource(R.color.colorPrimaryDarkNight);
//        } else {
//            Logger.i(LOG_TAG, "onResume mode is AppCompatDelegate.MODE_NIGHT_AUTO");
//        }
    }

    @Override
    public void initPresenter() {
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            FragmentUtils.popBackStack(getFragmentManager());
        }
    }

    private class CustomWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            startRefresh();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            stopRefresh();
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            stopRefresh();
        }
    }

    @Override
    public boolean prepareRefresh() {
        return false;
    }

    @Override
    public void onRefreshStart() {
        mWebView.reload();
    }

    @Override
    public void startRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void stopRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
