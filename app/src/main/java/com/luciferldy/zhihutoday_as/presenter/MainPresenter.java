package com.luciferldy.zhihutoday_as.presenter;

import android.app.Activity;

import com.luciferldy.zhihutoday_as.R;
import com.luciferldy.zhihutoday_as.api.ThemeApi;
import com.luciferldy.zhihutoday_as.model.ThemeListGson;
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

    public static final String BASE_URL = "http://news-at.zhihu.com/api/4/";
    public static final String URL_DAILY_STORY = "http://daily.zhihu.com/story/";

    private Retrofit mRetrofit;
    private Subscription mThemeListSub;

    public MainPresenter(Activity activity, MainActivity view) {
        super(activity, view);
    }

    /**
     * 获得主题列表
     */
    public synchronized void getThemeList() {
        if (mRetrofit == null)
            mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        ThemeApi themeApi = mRetrofit.create(ThemeApi.class);
        mThemeListSub = themeApi.getThemeList().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ThemeListGson>() {
                    @Override
                    public void onCompleted() {
                        Logger.i(LOG_TAG, "getThemeList onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.i(LOG_TAG, "getThemeList onError, " + e.getMessage());
                    }

                    @Override
                    public void onNext(ThemeListGson themeListGson) {
                        if (mView == null)
                            return;
                        Logger.i(LOG_TAG, "getThemeList onNext updateThemes " + themeListGson.getOthers().size());
                        mView.updateThemes(themeListGson.getOthers());
                    }
                });
    }


    /**
     * 取消订阅
     */
    public void unSubscribe() {
        if (mThemeListSub != null && !mThemeListSub.isUnsubscribed()) {
            mThemeListSub.unsubscribe();
        }
    }
}
