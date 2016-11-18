package com.buku001.tenyuan.activity;

import android.os.Bundle;

import com.base.browser.manager.popupwindow.MainPopupWindow;
import com.buku001.tenyuan.utils.DoAnalyticsManager;
import com.igexin.sdk.PushManager;

public class MainActivity extends com.base.browser.activity.MainActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PushManager.getInstance().initialize(this.getApplicationContext());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initPopupwindow() {
        if (mPopupWindow == null) {
            mPopupWindow = new MainPopupWindow(MainActivity.this, "com.buku001.tenyuan.activity.MainActivity");
        }
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