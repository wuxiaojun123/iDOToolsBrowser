package com.buku001.tenyuan.activity;

import android.os.Bundle;

import com.buku001.tenyuan.utils.DoAnalyticsManager;
import com.igexin.sdk.PushManager;

public class MainActivity extends com.base.browser.activity.MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PushManager.getInstance().initialize(this.getApplicationContext());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DoAnalyticsManager.pageResume(this, "MainActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        DoAnalyticsManager.pagePause(this, "MainActivity");
    }


}