package com.luciferldy.zhihutoday_as.ui.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.luciferldy.zhihutoday_as.R;
import com.luciferldy.zhihutoday_as.adapter.BillBoardViewAdapter;
import com.luciferldy.zhihutoday_as.utils.CommonUtils;
import com.luciferldy.zhihutoday_as.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucifer on 2016/9/2.
 * 展示 top stories 的广告牌
 */
public class BillBoardView extends FrameLayout {

    private static final String LOG_TAG = BillBoardView.class.getSimpleName();
    private static final int MESSAGE_SCROLL = 123;
    private int dotScrollInterval = 3; // unit s
    private ViewPager viewPager;
    private Context mContext;

    // 弃用的 indicator
    private int dotCount;
    private float mAnimDistance;
    private View dotCursor;
    private List<View> dotGroup;
    private FrameLayout.LayoutParams cursorParams;
    private List<FrameLayout.LayoutParams> dotParams;

    private IndicatorView mIdView;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_SCROLL) {
                if (viewPager != null) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    startAutoPlay();
                }
            }
        }
    };

    /**
     * 开启自动滚动
     */
    public void startAutoPlay() {
        stopAutoPlay();
        handler.sendEmptyMessageDelayed(MESSAGE_SCROLL, dotScrollInterval * 1000);
    }

    /**
     * 停止自动滚动
     */
    public void stopAutoPlay() {
        handler.removeMessages(MESSAGE_SCROLL);
    }

    public BillBoardView(Context context) {
        super(context);
        this.mContext = context;
    }

    public void start(List<View> views, int interval, int animationDuration) {
        init(views, interval, animationDuration);
        invalidate();
    }

    private void init(List<View> views, final int interval, int animationDuration) {
        inflate(mContext, R.layout.news_item_top_stories, this);

        viewPager = (ViewPager) findViewById(R.id.pager);
        mIdView = (IndicatorView) findViewById(R.id.indicator_view);

//        dotCursor = findViewById(R.id.dot_cursor);

//        dotCount = views.size();
//        dotGroup = new ArrayList<>();
//
//        // add views
//        for (int i = 0; i < dotCount; i++) {
//            View dot = new View(getContext());
//            dot.setBackgroundDrawable(getResources().getDrawable(R.drawable.dot_normal));
//            addView(dot);
//            dotGroup.add(dot);
//        }
//
//        removeView(dotCursor);
//        addView(dotCursor);
//
//        this.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                BillBoardView.this.getViewTreeObserver().removeOnPreDrawListener(this);
//                initDotsAndCursorLayout();
//                return true;
//            }
//        });

        BillBoardViewAdapter adapter = new BillBoardViewAdapter(views);
        viewPager.setAdapter(adapter);
        viewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//                        stopAutoPlay();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
//                        startAutoPlay();
                        break;
                }
                return false;
            }
        });
        
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            /***
             * 当 0 向 1 的位置滑动时， position 为 0 ，当 1 向 0 的位置滑动时， position 为 0
             * @param position
             * @param positionOffset
             * @param positionOffsetPixels
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Logger.i(LOG_TAG, "onPageScrolled position=" + position + ", positionOffset=" + positionOffset + ", positionOffsetPixels=" + positionOffsetPixels);
                mIdView.moveIndicator(position, positionOffset, positionOffsetPixels);
//                if (dotParams != null && position < dotParams.size()) {
//                    cursorParams.leftMargin = (int) (dotParams.get(position).leftMargin + mAnimDistance * positionOffset);
//                    dotCursor.setLayoutParams(cursorParams);
//                }
            }

            @Override
            public void onPageSelected(int position) {
                mIdView.setCurrentPosition(position);
//                if (dotParams != null && position < dotParams.size()) {
//                    cursorParams.leftMargin = dotParams.get(position).leftMargin;
//                    dotCursor.setLayoutParams(cursorParams);
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(0);
    }

    @Deprecated
    private void initDotsAndCursorLayout() {

        int parentWidth = this.getMeasuredWidth();
        int parentHeight = this.getMeasuredHeight();

        // set the bottom margin of dot cursor
        cursorParams = (FrameLayout.LayoutParams) dotCursor.getLayoutParams();
        cursorParams.bottomMargin = CommonUtils.dip2px(getContext(), 10);
        dotCursor.setLayoutParams(cursorParams);

        // get the cursor property and set the property for dots
        int width = cursorParams.width;
        int height = cursorParams.height;
        int distance = CommonUtils.dip2px(getContext(), 3);
        mAnimDistance = width + distance; // anim distance

        dotParams = new ArrayList<>();
        for (int i = 0; i < dotGroup.size(); i++) {
            View dot = dotGroup.get(i);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) dot.getLayoutParams();
            params.leftMargin = (parentWidth - dotCount * width - (dotCount - 1) * distance ) / 2 + i * (width + distance);
            params.topMargin = parentHeight - CommonUtils.dip2px(getContext(), 10) - height;
            params.height = height;
            params.width = width;
            dot.setLayoutParams(params);
            dotParams.add(params);
            if (i == 0) {
                cursorParams.leftMargin = params.leftMargin;
                cursorParams.topMargin = params.topMargin;
                dotCursor.setLayoutParams(cursorParams);
            }
        }
    }
}

