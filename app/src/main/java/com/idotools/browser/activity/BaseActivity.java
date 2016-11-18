package com.idotools.browser.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.idotools.browser.R;
import com.idotools.browser.utils.ActivitySlideAnim;
import com.idotools.browser.utils.ActivityUtils;

/**
 * Created by wuxiaojun on 16-10-2.
 */
public class BaseActivity extends AppCompatActivity {

    protected Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        ActivityUtils.addActivitys(this);
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
