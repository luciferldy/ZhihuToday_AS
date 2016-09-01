package com.luciferldy.zhihutoday_as;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by Lucifer on 2016/9/1.
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(getApplicationContext());
    }
}
