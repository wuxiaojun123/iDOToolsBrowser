package com.wxj.demo;

import android.app.Application;
import android.content.Context;

import com.base.browser.utils.Constant;
import com.dot.analyticsone.AnalyticsOne;

/**
 * Created by wuxiaojun on 16-11-8.
 */
public class App extends Application {

    public static AnalyticsOne analytics;

    @Override
    public void onCreate() {
        super.onCreate();

//        Constant.PATH = "https://www.baidu.com/";
        Constant.PATH  = "http://wp.cgameclub.com/2016/11/24/the-boy-will-be-a-real-man/";
    }

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

}
