package com.idotools.browser;

import android.app.Application;
import android.content.Context;

import com.idotools.browser.utils.Constant;

/**
 * Created by wuxiaojun on 16-10-8.
 */
public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
//        FeedBackConstant.initFeedBackAnnoy(this, FeedBackConstant.APPKEY_BROWSER);
    }

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

}
