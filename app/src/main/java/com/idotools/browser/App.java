package com.idotools.browser;

import android.app.Application;
import android.content.Context;

import com.idotools.browser.utils.Constant;

/**
 * Created by wuxiaojun on 16-10-8.
 */
public class App extends Application {

    public static String cookie = null;
    public static String referer = null;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

}
