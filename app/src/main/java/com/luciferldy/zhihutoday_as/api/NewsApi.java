package com.luciferldy.zhihutoday_as.api;

import com.luciferldy.zhihutoday_as.model.NewsGson;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Lucifer on 2016/8/30.
 */
public interface NewsApi {

    /**
     * 获得较早时间新闻，前面使用 Observable<List<NewsGson.StoriesBean> 返回出现解析错误
     * 只能使用 NewsGson
     * @param date
     * @return
     */
    @GET("news/before/{date}")
    Observable<NewsGson> getEarlierNews(@Path("date") String date);

    /**
     * 获得最新的新闻
     * @return
     */
    @GET("news/latest")
    Observable<NewsGson> getLatestNews();

}
