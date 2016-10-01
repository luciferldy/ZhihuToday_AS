package com.luciferldy.zhihutoday_as.ui.activity;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.facebook.drawee.view.SimpleDraweeView;
import com.luciferldy.zhihutoday_as.R;
import com.luciferldy.zhihutoday_as.utils.Logger;

/**
 * Created by lian_ on 2016/9/10.
 */
public class SplashActivity extends AppCompatActivity {

    private static final String LOG_TAG = SplashActivity.class.getSimpleName();
    private final int SPLASH_DELAY_LENGTH = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final SimpleDraweeView iv = (SimpleDraweeView) findViewById(R.id.start_iv);

        // 4.4 translucent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        // 5.0 transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE );
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        final Animation animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.splash_start_iv);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Logger.i(LOG_TAG, "onAnimationStart");
                iv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 设置 SimpleDrawee 为 GONE 看不到动画回弹
                iv.setVisibility(View.GONE);
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SplashActivity.this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    SplashActivity.this.startActivity(intent);
                else
                    SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
                SplashActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        // 使用 post 方法可以保证在 SimpleDrawee 完全被加载出来之后才会执行动画，有效减少卡顿?
        iv.post(new Runnable() {
            @Override
            public void run() {
                iv.startAnimation(animation);
            }
        });

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        super.overridePendingTransition(enterAnim, exitAnim);
    }
}
