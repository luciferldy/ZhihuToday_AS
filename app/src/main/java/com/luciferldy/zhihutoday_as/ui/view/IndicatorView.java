package com.luciferldy.zhihutoday_as.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.luciferldy.zhihutoday_as.R;
import com.luciferldy.zhihutoday_as.utils.Logger;

import java.util.ArrayList;

/**
 * Created by lian_ on 2016/12/6.
 *
 * IndicatorView ViewPager 指示器自定义 View
 */

public class IndicatorView extends View {

    private static final String LOG_TAG = IndicatorView.class.getSimpleName();
    private int mWidth;
    private int mHeight;
    private Paint mPaint;
    private ArrayList<PointF> points;
    private float dotRadius;
    private int dotDistance;
    private int dotCount;
    private int normalColor;
    private int selectedColor;
    private PointF cursorPointF;

    public IndicatorView(Context context) {
        this(context, null);
    }

    public IndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Logger.i(LOG_TAG, "IndicatorView constructor with two arguments.");
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.IndicatorView, 0, 0);
        try {
            dotRadius = a.getDimensionPixelOffset(R.styleable.IndicatorView_indicator_size, 20) * 0.5f;
            dotDistance = a.getDimensionPixelOffset(R.styleable.IndicatorView_indicator_distance, 20);
            dotCount = a.getInt(R.styleable.IndicatorView_indicator_count, 5);
            normalColor = a.getColor(R.styleable.IndicatorView_indicator_normal_color, getResources().getColor(R.color.md_white));
            selectedColor = a.getColor(R.styleable.IndicatorView_indicator_selected_color, getResources().getColor(R.color.md_grey_700));
        } finally {
            a.recycle();
        }

        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                initPoints();
                return true;
            }
        });

        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        Logger.i(LOG_TAG, "width: " + mWidth + ", height: " + mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPaint == null)
            return;
        mPaint.setColor(normalColor);
        for (PointF pointF : points) {
            canvas.drawCircle(pointF.x, pointF.y, dotRadius, mPaint);
        }
        mPaint.setColor(selectedColor);
        canvas.drawCircle(cursorPointF.x, cursorPointF.y, dotRadius, mPaint);
    }

    /**
     * 设置当前的位置
     * @param position 位置的索引
     */
    public void setCurrentPosition(int position) {
        if (position >= points.size())
            return;
        cursorPointF.x = points.get(position).x; // 变动 x 就好
        invalidate();
    }

    /**
     * 移动 indicator
     * @param position 位置的 index
     * @param positionOffset 偏移的比例
     * @param positionOffsetPixels 偏移的像素
     */
    public void moveIndicator(int position, float positionOffset, float positionOffsetPixels) {
        if (position >= points.size())
            return;
        cursorPointF.x = points.get(position).x + positionOffset * (dotDistance + dotRadius * 2);
        invalidate();
    }

    /**
     * 初始化
     */
    private void init() {
        points = new ArrayList<>();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
    }

    private void initPoints() {
        float left = (mWidth - dotRadius * 2 * dotCount - (dotCount - 1) * dotDistance) * 0.5f;
        for (int i = 0; i < dotCount; i++) {
            PointF pointF = new PointF();
            pointF.x = left + dotRadius * 2 * i + dotDistance * i + dotRadius;
            pointF.y = mHeight - dotRadius - 2; // 减去2 pixel
            points.add(pointF);
        }
        cursorPointF = new PointF();
        cursorPointF.x = points.get(0).x;
        cursorPointF.y = points.get(0).y;
    }
}
