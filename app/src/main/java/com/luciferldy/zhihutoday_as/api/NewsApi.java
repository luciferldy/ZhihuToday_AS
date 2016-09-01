package com.luciferldy.zhihutoday_as.api;

import com.luciferldy.zhihutoday_as.model.NewsGson;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Lucifer on 2016/8/30.
 */
public interface NewsApi {

    /**
     * 获得较早时间新闻
     * @param date
     * @return
     */
    @GET("news/latest/{date}")
    Observable<List<NewsGson.StoriesBean>> getEarlierNews(@Path("date") String date);

    /**
     * 获得最新的新闻
     * @return
     */
    @GET("news/latest")
    Observable<NewsGson> getLatestNews();

}
