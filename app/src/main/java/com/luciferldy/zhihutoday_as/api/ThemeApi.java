package com.luciferldy.zhihutoday_as.api;

import com.luciferldy.zhihutoday_as.model.ThemeContentListGson;
import com.luciferldy.zhihutoday_as.model.ThemeListGson;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Lucifer on 2017/1/22.
 */

public interface ThemeApi {

    /**
     * 主题列表日报查看
     * http://news-at.zhihu.com/api/4/themes
     * @return
     */
    @GET("themes")
    Observable<ThemeListGson> getThemeList();

    /**
     * 主题日报内容查看
     * @param themeId
     * @return
     */
    @GET("theme/{themeId}")
    Observable<ThemeContentListGson> getThemeContent(@Path("themeId") int themeId);
}
