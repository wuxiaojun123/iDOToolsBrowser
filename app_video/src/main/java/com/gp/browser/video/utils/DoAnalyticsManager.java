package com.gp.browser.video.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;


/**
 * Created by wuxiaojun on 16-11-7.
 */
public class DoAnalyticsManager {

    private static DoAnalyticsManager mDoAnalyticsManager;
    private FirebaseAnalytics mFirebaseAnalytics;

    /*public static void pageResume(Activity activity) {
    }
    public static void pagePause(Activity activity) {
    }*/

    private DoAnalyticsManager(Context context){
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public static DoAnalyticsManager getInstance(Context context){
        synchronized (DoAnalyticsManager.class){
            if(mDoAnalyticsManager == null){
                synchronized (DoAnalyticsManager.class){
                    mDoAnalyticsManager = new DoAnalyticsManager(context);
                }
            }
        }
        return mDoAnalyticsManager;
    }

    public void pageEvent(String eventId) {
        mFirebaseAnalytics.logEvent(eventId,new Bundle());
    }

}
