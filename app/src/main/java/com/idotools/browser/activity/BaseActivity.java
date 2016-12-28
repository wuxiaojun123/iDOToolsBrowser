package com.idotools.browser.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.idotools.browser.utils.ActivitySlideAnim;
import com.idotools.browser.utils.ActivityUtils;
import com.idotools.browser.utils.DoAnalyticsManager;

import nsu.edu.com.library.SwipeBackActivity;

/**
 * Created by wuxiaojun on 16-10-2.
 */
public class BaseActivity extends SwipeBackActivity {

    protected Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        ActivityUtils.addActivitys(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DoAnalyticsManager.pageResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DoAnalyticsManager.pagePause(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivitySlideAnim.slideOutAnim(this);
    }

    @Override
    protected void onDestroy() {
        ActivityUtils.removeActivity(this);
        super.onDestroy();
    }
}
