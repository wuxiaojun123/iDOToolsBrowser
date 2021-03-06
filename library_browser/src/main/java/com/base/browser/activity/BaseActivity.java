package com.base.browser.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.base.browser.utils.ActivitySlideAnim;

/**
 * Created by wuxiaojun on 16-10-2.
 */
public class BaseActivity extends AppCompatActivity {

    protected Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivitySlideAnim.slideOutAnim(this);
    }

}
