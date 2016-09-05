package com.luciferldy.zhihutoday_as.adapter;

import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.luciferldy.zhihutoday_as.R;
import com.luciferldy.zhihutoday_as.model.NewsGson;
import com.luciferldy.zhihutoday_as.ui.view.BillBoardView;
import com.luciferldy.zhihutoday_as.utils.CommonUtils;
import com.luciferldy.zhihutoday_as.utils.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Lucifer on 2016/8/30.
 */
public class MainRvAdapter extends RecyclerView.Adapter<MainRvAdapter.BaseViewHolder> {

    private static final String LOG_TAG = MainRvAdapter.class.getSimpleName();

    private List<DataWrapper> mList;
    private static List<NewsGson.TopStoriesBean> mTopList;
    private static ClickItem listener;

    public MainRvAdapter() {
        mList = new ArrayList<>();
        mTopList = new ArrayList<>();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder viewHolder;
        if (viewType == ViewType.ITEM_TOP_STORIES.ordinal()) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_top_stories, parent, false);
//            viewHolder = new TopStoriesViewHolder(view);
            BillBoardView billboard = new BillBoardView(parent.getContext());
            parent.addView(billboard);
            viewHolder = new TopStoriesViewHolder(billboard);
        } else if (viewType == ViewType.ITEM_TITLE.ordinal()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_title, parent, false);
            viewHolder = new TitleViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_story, parent,false);
            viewHolder = new StoriesViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.bindItem(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).viewType.ordinal();
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 清空原有的数据并添加新的数据
     * @param date
     * @param newsBody
     */
    public void updateData(String date, NewsGson newsBody) {
        mList.clear();

        mTopList.clear();
        mTopList.addAll(newsBody.getTop_stories());
        // 占位
        DataWrapper topData = new DataWrapper();
        topData.viewType = ViewType.ITEM_TOP_STORIES;
        mList.add(topData);

        DataWrapper titleData = new DataWrapper();
        titleData.viewType = ViewType.ITEM_TITLE;
        titleData.title = date;
        mList.add(titleData);

        DataWrapper storyData;
        for (NewsGson.StoriesBean story : newsBody.getStories()) {
            storyData = new DataWrapper();
            storyData.viewType = ViewType.ITEM_STORIES;
            storyData.id = story.getId();
            storyData.type = story.getType();
            storyData.title = story.getTitle();
            storyData.gaPrefix = story.getGa_prefix();
            storyData.images = story.getImages();
            mList.add(storyData);
        }

        notifyDataSetChanged();

    }

    /**
     * 添加新的数据
     * @param date
     * @param data
     */
    public void appendMoreData(String date, List<NewsGson.StoriesBean> data) {
        // title
        DataWrapper titleData = new DataWrapper();
        titleData.viewType = ViewType.ITEM_TITLE;
        titleData.title = date;
        mList.add(titleData);

        // normal stories
        DataWrapper storyData;
        for (NewsGson.StoriesBean story : data) {
            storyData = new DataWrapper();
            storyData.viewType = ViewType.ITEM_STORIES;
            storyData.id = story.getId();
            storyData.type = story.getType();
            storyData.title = story.getTitle();
            storyData.gaPrefix = story.getGa_prefix();
            storyData.images = story.getImages();
            mList.add(storyData);
        }

        notifyDataSetChanged();
    }

    public void setClickItem(ClickItem listener) {
        this.listener = listener;
    }

    /**
     * RecycleView 的基础 ViewHolder
     */
    abstract static class BaseViewHolder extends RecyclerView.ViewHolder{

        protected View parent;

        public BaseViewHolder(View itemView) {
            super(itemView);
            this.parent = itemView;
        }

        public void bindItem(DataWrapper data){}
    }

    /**
     * 热门新闻
     */
    static class TopStoriesViewHolder extends BaseViewHolder {

        private View parent;

        public TopStoriesViewHolder(View itemView) {
            super(itemView);
            Logger.i(LOG_TAG, "TopStoriesViewHolder constructor called.");
            parent = itemView;
            // set the width and height
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) parent.getLayoutParams();
            params.height = CommonUtils.dip2px(parent.getContext(), 225);
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            parent.setLayoutParams(params);
        }

        @Override
        public void bindItem(DataWrapper data) {
            super.bindItem(data);

            List<View> views = new ArrayList<>();
            for (NewsGson.TopStoriesBean top : mTopList) {
                Logger.i(LOG_TAG, "top stories = " + top.toString());
                View page = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_story_pager, null);
                SimpleDraweeView image = (SimpleDraweeView) page.findViewById(R.id.image);
                image.setImageURI(Uri.parse(top.getImage()));
                TextView text = (TextView) page.findViewById(R.id.text);
                text.setText(top.getTitle());
                views.add(page);
            }

            if (parent instanceof BillBoardView) {
                ((BillBoardView) parent).removeAllViewsInLayout();
                ((BillBoardView) parent).start(views, 3, 1400);
            } else {
                Logger.i(LOG_TAG, "parent is not instance of BillBoardView.");
            }
        }
    }

    /**
     * 普通新闻
     */
    static class StoriesViewHolder extends BaseViewHolder {

        private SimpleDraweeView image;
        private TextView text;

        public StoriesViewHolder(View itemView) {
            super(itemView);
            Logger.i(LOG_TAG, "StoriesViewHolder constructor called.");
            image = (SimpleDraweeView) itemView.findViewById(R.id.image);
            text = (TextView) itemView.findViewById(R.id.title);
        }

        @Override
        public void bindItem(final DataWrapper data) {
            super.bindItem(data);
            Logger.i(LOG_TAG, "StoriesViewHolder bindItem called.");
            if (data.images.size() > 0) {
                image.setImageURI(Uri.parse(data.images.get(0)));
            }
            text.setText(data.title);
            parent.setOnClickListener(MainRvAdapter.this);
        }
    }

    /**
     * 日期
     */
    static class TitleViewHolder extends BaseViewHolder {

        private TextView text;

        public TitleViewHolder(View itemView) {
            super(itemView);
            Logger.i(LOG_TAG, "TitleViewHolder constructor called.");
            text = (TextView) itemView.findViewById(R.id.title);
        }

        @Override
        public void bindItem(DataWrapper data) {
            super.bindItem(data);
            Logger.i(LOG_TAG, "TitleViewHolder bindItem called.");
            text.setText(data.title);
        }
    }

    /**
     * 几种 item 的类型
     */
    private enum ViewType {
        ITEM_TOP_STORIES,
        ITEM_TITLE,
        ITEM_STORIES
    }

    /**
     * 封装数据类
     */
    private class DataWrapper {
        ViewType viewType;
        int id;
        int type;
        String gaPrefix;
        String title;
        List<String> images;
    }

    public interface ClickItem {
        void onClick(String url);
    }
}
