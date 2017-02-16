package com.gp.browser.video.dance;

import android.app.Application;
import android.content.Context;
//import com.facebook.FacebookSdk;

/**
 * Created by wuxiaojun on 16-10-8.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

}
