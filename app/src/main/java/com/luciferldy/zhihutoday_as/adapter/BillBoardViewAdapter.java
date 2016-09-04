package com.luciferldy.zhihutoday_as.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.luciferldy.zhihutoday_as.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucifer on 2016/9/2.
 */
public class BillBoardViewAdapter extends PagerAdapter{

    private static final String LOG_TAG = BillBoardViewAdapter.class.getSimpleName();

    private List<View> mList;

    public BillBoardViewAdapter(List<View> views) {
        super();
        mList = new ArrayList<>();
        mList.addAll(views);
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
        Logger.i(LOG_TAG, "instantiateItem container=" + container.toString() + ", position=" + position);
        container.addView(mList.get(position));
        return mList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Logger.i(LOG_TAG, "destroyItem container=" + container + ", position=" + position);
        container.removeView(mList.get(position));
    }
}
