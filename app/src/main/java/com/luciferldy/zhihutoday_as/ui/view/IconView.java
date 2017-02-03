package com.luciferldy.zhihutoday_as.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.luciferldy.zhihutoday_as.R;
import com.luciferldy.zhihutoday_as.utils.CommonUtils;
import com.luciferldy.zhihutoday_as.utils.Logger;

/**
 * Created by Lucifer on 2016/10/25.
 * 启动页知乎的3/4圆的图标
 */

public class IconView extends View {

    private static final String LOG_TAG = IconView.class.getSimpleName();

    private int width;
    private int height;
    private Paint mMainPaint;
    private Paint mPointPaint;
    private Path mMainPath;
    private Path mSumPath;
    private float radius; // d=28 dip
    private float f; //
    private PathMeasure measure;



    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mMainPaint = new Paint();
        mMainPaint.setStyle(Paint.Style.STROKE);
        mMainPaint.setAntiAlias(true); // 抗锯齿

        TypedArray t = context.getTheme().obtainStyledAttributes(attrs, R.styleable.IconView, 0, 0);
        try {
            float strokeWidth = t.getDimension(R.styleable.IconView_stroke_width, CommonUtils.dip2px(context, 5));
            mMainPaint.setStrokeWidth(strokeWidth);
            int color = t.getColor(R.styleable.IconView_stroke_color, getResources().getColor(R.color.md_grey_300));
            mMainPaint.setColor(color);
            radius = t.getDimension(R.styleable.IconView_radius, CommonUtils.dip2px(context, 14));
        } finally {
            t.recycle();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

        mSumPath = new Path();
        mSumPath.moveTo(width / 2, height / 2 + radius);
        mSumPath.addCircle(width / 2, height / 2, radius, Path.Direction.CW);

        mMainPath = new Path();

        measure = new PathMeasure(mSumPath, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawIcon(canvas);
    }

    private void drawIcon(Canvas canvas) {
        mMainPath.reset();
        measure.getSegment(measure.getLength() * 0.25f,  measure.getLength() * f, mMainPath, true);
        canvas.drawPath(mMainPath, mMainPaint);
    }

    public void startAnim(final AnimEndCallback callback) {
        Logger.i(LOG_TAG, "startAnim");

        ValueAnimator animator = ValueAnimator.ofFloat(0.25f, 1.0f).setDuration(2500);
        animator.setInterpolator(new DecelerateInterpolator());
        ValueAnimator.AnimatorUpdateListener updatelistener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                f = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        };
        ValueAnimator.AnimatorListener listener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                callback.end();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        };
        animator.addUpdateListener(updatelistener);
        animator.addListener(listener);
        animator.start();
    }

    public interface AnimEndCallback{
        void end();
    }
}
