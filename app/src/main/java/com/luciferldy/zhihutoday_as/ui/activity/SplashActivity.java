package com.luciferldy.zhihutoday_as.ui.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.luciferldy.zhihutoday_as.R;
import com.luciferldy.zhihutoday_as.api.NewsApi;
import com.luciferldy.zhihutoday_as.ui.view.LogoView;
import com.luciferldy.zhihutoday_as.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lian_ on 2016/9/10.
 */
public class SplashActivity extends AppCompatActivity {

    public static final String START_IMAGE = "http://news-at.zhihu.com/api/4/start-image/";

    private static final String LOG_TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final SimpleDraweeView iv = (SimpleDraweeView) findViewById(R.id.start_iv);
        final TextView tv = (TextView) findViewById(R.id.author_tv);

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
//                    iv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 设置 SimpleDrawee 为 GONE 看不到动画回弹
                iv.setVisibility(View.GONE);
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SplashActivity.this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    SplashActivity.this.startActivity(intent);
                } else {
                    SplashActivity.this.startActivity(intent);
                }
                SplashActivity.this.finish();
                SplashActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        // 使用 post 方法可以保证在 SimpleDrawee 完全被加载出来之后才会执行动画，有效减少卡顿?
//        iv.post(new Runnable() {
//            @Override
//            public void run() {
//                iv.startAnimation(animation);
//            }
//        });

        // 貌似好像只有 iv 设置为 visible 时才会回调这个 controller listener
        final ControllerListener cListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                if (imageInfo == null) {
                    return;
                }
                QualityInfo qualityInfo = imageInfo.getQualityInfo();
                FLog.d("Final image received! " +
                                "Size %d x %d",
                        "Quality level %d, good enough: %s, full quality: %s",
                        imageInfo.getWidth(),
                        imageInfo.getHeight(),
                        qualityInfo.getQuality(),
                        qualityInfo.isOfGoodEnoughQuality(),
                        qualityInfo.isOfFullQuality());
//                iv.startAnimation(animation);
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                Logger.i(LOG_TAG, "onFailure");
                loadDefaultStartImage(iv, animation);
                throwable.printStackTrace();
            }
        };

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(START_IMAGE)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        NewsApi service = retrofit.create(NewsApi.class);
        service.getStartImage("1080*1776").subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadDefaultStartImage(iv, animation);
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String body = responseBody.string();
                            JSONObject object = new JSONObject(body);
                            if (object.has("name")) {
                                String text = object.getString("name");
                                tv.setText(text);
                            }
                            String url = object.getString("img");
                            Logger.i(LOG_TAG, "start image url=" + url);
                            DraweeController controller = Fresco.newDraweeControllerBuilder()
                                    .setControllerListener(cListener)
                                    .setUri(Uri.parse(url))
                                    .build();
                            iv.setController(controller);
//                            iv.setImageURI(Uri.parse(url));
//                            iv.startAnimation(animation);
                        } catch (IOException|JSONException e) {
                            Logger.i(LOG_TAG, "e.getMessage " + e.getMessage());
                            e.printStackTrace();
                            // 错误处理
                            // 有一种比较麻烦的 drawable 转 uri 的方法
//                            Resources r = getResources();
//                            uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
//                                    + r.getResourcePackageName(R.drawable.start) + "/"
//                                    + r.getResourceTypeName(R.drawable.start) + "/"
//                                    + r.getResourceEntryName(R.drawable.start));
                            loadDefaultStartImage(iv, animation);
                        }
                    }
                });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.icon_layout);
        final LogoView icon = (LogoView) findViewById(R.id.icon_view);
        // 使用 layout.getHeight 获取的值不等与 layout 的高度
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) layout.getLayoutParams();
        // 使用 layout_marginBottom 不会出现动画效果
        ObjectAnimator animator = ObjectAnimator.ofFloat(layout, "translationY", params.height, 0);
        Logger.i(LOG_TAG, "icon layout params.height = " + params.height);
        animator.setDuration(1000);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                icon.startAnim(new LogoView.AnimEndCallback() {
                    @Override
                    public void end() {
                        intentToMainActivity();
                    }
                });
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    private void intentToMainActivity() {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out);
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        // ActivityCompat 有判断当前版本是否大于16
        ActivityCompat.startActivity(this, intent, options.toBundle());
        this.finish();
    }

    private void loadDefaultStartImage(SimpleDraweeView iv, Animation animation) {
        GenericDraweeHierarchy hierarchy = iv.getHierarchy();
        hierarchy.setPlaceholderImage(R.drawable.start);
//        iv.setImageDrawable(getResources().getDrawable(R.drawable.start));
//        iv.startAnimation(animation);
    }


}
