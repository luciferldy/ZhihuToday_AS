package com.luciferldy.zhihutoday_as.presenter;

import android.app.Activity;

import com.luciferldy.zhihutoday_as.api.ThemeApi;
import com.luciferldy.zhihutoday_as.model.ThemeContentListGson;
import com.luciferldy.zhihutoday_as.ui.fragment.ThemeContentFragment;

import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Lucifer on 2017/1/25.
 */

public class ThemeContentFragPresenter extends BasePresenter<ThemeContentFragment> {

    public static final String URL_THEME_CONTENT = "http://news-at.zhihu.com/api/4/";
    private Retrofit mRetrofit;
    private Subscription mSubscription;
    private boolean isLoading = false;

    public ThemeContentFragPresenter(Activity activity, ThemeContentFragment view) {
        super(activity, view);
    }

    public synchronized void getThemeContent(int themeId) {
        isLoading = true;
        mRetrofit = new Retrofit.Builder()
                .baseUrl(URL_THEME_CONTENT)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ThemeApi themeApi = mRetrofit.create(ThemeApi.class);
        mSubscription = themeApi.getThemeContent(themeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ThemeContentListGson>() {
                    @Override
                    public void onCompleted() {
                        isLoading = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        onCompleted();
                    }

                    @Override
                    public void onNext(ThemeContentListGson themeContentListGson) {
                        if (mView == null)
                            return;
                        mView.updateData(themeContentListGson);
//                        mView.updateDescription(themeContentListGson.getBackground(), themeContentListGson.getDescription());
//                        mView.updateEditors(themeContentListGson.getEditors());
//                        mView.updateStories(themeContentListGson.getStories());
                        onCompleted();
                    }
                });
    }

    public synchronized boolean isLoading() {
        return isLoading;
    }

    public void unsubscribe() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }
}
