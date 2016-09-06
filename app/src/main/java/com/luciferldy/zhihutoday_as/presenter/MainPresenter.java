package com.luciferldy.zhihutoday_as.presenter;

import android.app.Activity;

import com.luciferldy.zhihutoday_as.ui.activity.MainActivity;
import com.luciferldy.zhihutoday_as.api.NewsApi;
import com.luciferldy.zhihutoday_as.model.NewsGson;
import com.luciferldy.zhihutoday_as.utils.Logger;

import java.util.List;

import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Lucifer on 2016/8/30.
 */
public class MainPresenter extends BasePresenter<MainActivity> {

    private static final String LOG_TAG = MainPresenter.class.getSimpleName();

    public static final String URL_LATEST_NEWS = "http://news-at.zhihu.com/api/4/";
    public static final String URL_EARLIER_NEWS = "http://news.at.zhihu.com/api/4/";
    public static final String URL_DAILY_STORY = "http://daily.zhihu.com/story/";

    private Subscription mLatestSub;
    private Subscription mEarlierSub;

    private boolean isLoading = false;

    public MainPresenter(Activity activity, MainActivity view) {
        super(activity, view);
    }

    /**
     * 获得最新的新闻消息
     */
    public synchronized void getLatestNews() {

        isLoading = true;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_LATEST_NEWS)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        NewsApi service = retrofit.create(NewsApi.class);
        String date = "";
        mLatestSub = service.getLatestNews().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NewsGson>() {
                    @Override
                    public void onCompleted() {
                        isLoading = false;
                        Logger.i(LOG_TAG, "getLatestNews onCompleted.");
                    }

                    @Override
                    public void onError(Throwable e) {
                        isLoading = false;
                        e.printStackTrace();
                        Logger.i(LOG_TAG, "getLatestNews onError, e=" + e.getMessage());
                    }

                    @Override
                    public void onNext(NewsGson newsGson) {
                        Logger.i(LOG_TAG, "getLatestNews onNext");
                        if (mView != null) {
                            mView.fillData("今日热闻", newsGson);
                        }
                    }
                });
    }

    /**
     * 获得更早期的新闻内容
     * 应该由 MainPresenter 维护时间线
     */
    public synchronized void getEarlierNews() {
        isLoading = true;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_EARLIER_NEWS)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        final NewsApi service = retrofit.create(NewsApi.class);
        final String date = "";
        mEarlierSub = service.getEarlierNews(date).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<NewsGson.StoriesBean>>() {
                    @Override
                    public void onCompleted() {
                        isLoading = false;
                        Logger.i(LOG_TAG, "getEarlierNews onCompleted.");
                    }

                    @Override
                    public void onError(Throwable e) {
                        isLoading = false;
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<NewsGson.StoriesBean> storiesBeen) {
                        Logger.i(LOG_TAG, "getEarlierNews onNext");
                        if (mView != null) {
                            mView.appendMore(date, storiesBeen);
                        }
                    }
                });


    }

    /**
     * 是否正在加载 Story
     * @return
     */
    public boolean isLoading() {
        return isLoading;
    }

    /**
     * 取消订阅
     */
    public void unSubscribe() {
        if (mLatestSub != null && mLatestSub.isUnsubscribed()) {
            mLatestSub.unsubscribe();
        }
        if (mEarlierSub != null && mEarlierSub.isUnsubscribed()) {
            mEarlierSub.unsubscribe();
        }
    }
}
