package com.manga.browser;

import android.app.Application;
import android.content.Context;

/**
 * Created by wuxiaojun on 16-10-8.
 */
public class App extends Application {

    public static String cookie = null;
    public static String referer = null;

    public static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public static App get(Context context) {
        if (context != null) {
            return (App) context.getApplicationContext();
        }
        return app;
    }

}
