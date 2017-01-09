package com.gp.browser.pink.book;

import android.app.Application;
import android.content.Context;

import com.base.browser.utils.Constant;

/**
 * Created by wuxiaojun on 16-10-8.
 */
public class App extends Application {

//    public static AnalyticsOne analytics;

    @Override
    public void onCreate() {
        super.onCreate();
//        analytics = AnalyticsOne.getInstance(this);
        Constant.PATH = "http://m1book.com:16040/a04410fe00d748c6b68f595656577851";
    }

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

}
