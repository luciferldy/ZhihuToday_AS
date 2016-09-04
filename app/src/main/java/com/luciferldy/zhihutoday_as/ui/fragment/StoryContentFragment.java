package com.luciferldy.zhihutoday_as.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.luciferldy.zhihutoday_as.R;
import com.luciferldy.zhihutoday_as.utils.FragmentUtils;
import com.luciferldy.zhihutoday_as.utils.Logger;

/**
 * Created by Lucifer on 2016/9/4.
 */
public class StoryContentFragment extends Fragment implements BaseFragment {

    public static final String BUNDLE_URL = "story_url";

    private static final String LOG_TAG = StoryContentFragment.class.getSimpleName();
    private WebView mWebView;
    private String mUrl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.news_content, container, false);
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

    @Override
    public void fillData() {

    }

    private class CustomWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }
    }
}
