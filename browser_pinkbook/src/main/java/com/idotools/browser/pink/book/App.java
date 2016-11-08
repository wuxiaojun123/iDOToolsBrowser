package com.idotools.browser.pink.book;

import android.app.Application;
import android.content.Context;

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
    }

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

}
