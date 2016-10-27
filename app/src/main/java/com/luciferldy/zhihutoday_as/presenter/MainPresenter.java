package com.luciferldy.zhihutoday_as.presenter;

import android.app.Activity;

import com.luciferldy.zhihutoday_as.R;
import com.luciferldy.zhihutoday_as.ui.activity.MainActivity;
import com.luciferldy.zhihutoday_as.api.NewsApi;
import com.luciferldy.zhihutoday_as.model.NewsGson;
import com.luciferldy.zhihutoday_as.utils.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Lucifer on 2016/8/30.
 */
public class MainPresenter extends BasePresenter<MainActivity> {

    private static final String LOG_TAG = MainPresenter.class.getSimpleName();

    public static final String URL_START_IMAGE = "http://news-at.zhihu.com/api/4/start-image/1080*1776";
    public static final String URL_LATEST_NEWS = "http://news-at.zhihu.com/api/4/";
    public static final String URL_EARLIER_NEWS = "http://news.at.zhihu.com/api/4/";
    public static final String URL_DAILY_STORY = "http://daily.zhihu.com/story/";

    private Subscription mLatestSub;
    private Subscription mEarlierSub;
    private Calendar mCalendar;

    private boolean isLoading = false;

    public MainPresenter(Activity activity, MainActivity view) {
        super(activity, view);
        mCalendar = Calendar.getInstance();
        mCalendar.setTime(new Date());
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
                            mView.fillData(mActivity.getString(R.string.daily_news), newsGson);
                        }
                    }
                });
    }

    /**
     * 获得更早期的新闻内容
     * 应该由 MainPresenter 维护时间线
     * 我的理解是 subscribeOn 是影响生产者（Observable）生产数据的线程的，
     * 通常我们只需要指定生产者在某一个特定的线程生产数据就可以满足我们的需求，
     * 至少我还没遇到过需要在生产数据的过程中去切换生产者所在的线程的情况。绝大多数我们需要变化线程的场景都是在数据生产之后，
     * Rx里面就使用 observeOn 来指定各种 operator 和 subscriber 的线程，因为这些本质上都是数据的消费者。
     * 消费者可以任意切换自己接受处理数据的线程，足以满足我们的需求。
     * 作者：hi大头鬼hi
     */
    public synchronized void getEarlierNews() {
        isLoading = true;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_EARLIER_NEWS)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        NewsApi service = retrofit.create(NewsApi.class);
        Date date = mCalendar.getTime();
        DateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        String dateStr = format.format(date);
        Logger.i(LOG_TAG, "dateStr=" + dateStr);
        mEarlierSub = service.getEarlyNews(dateStr).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Func1<NewsGson, List<NewsGson.StoriesBean>>() {
                    @Override
                    public List<NewsGson.StoriesBean> call(NewsGson newsGson) {
                        return newsGson.getStories();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<NewsGson.StoriesBean>>() {
                    @Override
                    public void onCompleted() {
                        isLoading = false;
                        Logger.i(LOG_TAG, "getEarlyNews onCompleted.");
                    }

                    @Override
                    public void onError(Throwable e) {
                        isLoading = false;
                        Logger.i(LOG_TAG, "getEarlyNews onError, msg=" + e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<NewsGson.StoriesBean> storiesBeen) {
                        Logger.i(LOG_TAG, "getEarlyNews onNext");
                        if (mView != null) {
                            mCalendar.add(Calendar.DATE, -1);
                            Date afterDate = mCalendar.getTime();
                            DateFormat afterDateFormat = new SimpleDateFormat("yyyy年MM月dd日 E");
                            String afterDateStr = afterDateFormat.format(afterDate);
                            mView.appendMore(afterDateStr, storiesBeen);

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
