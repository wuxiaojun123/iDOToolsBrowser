package com.idotools.browser;

import android.app.Application;
import android.content.Context;

import com.alibaba.sdk.android.FeedBackConstant;
import com.dot.analyticsone.AnalyticsOne;

/**
 * Created by wuxiaojun on 16-10-8.
 */
public class App extends Application {

    public static AnalyticsOne analytics;

    @Override
    public void onCreate() {
        super.onCreate();
        analytics = AnalyticsOne.getInstance(this);
        FeedBackConstant.initFeedBackAnnoy(this, FeedBackConstant.APPKEY_BROWSER);
    }

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

}
