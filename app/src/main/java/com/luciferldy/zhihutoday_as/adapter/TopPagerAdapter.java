package com.luciferldy.zhihutoday_as.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Lucifer on 2016/8/31.
 */
public class TopPagerAdapter extends PagerAdapter {

    private List<View> mList;

    public TopPagerAdapter(List<View> views) {
        super();

        mList = views;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager) container).addView(mList.get(position), 0);
        return mList.get(position);
    }
}
