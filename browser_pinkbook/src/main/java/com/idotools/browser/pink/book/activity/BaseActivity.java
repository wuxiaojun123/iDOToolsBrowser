package com.idotools.browser.pink.book.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.idotools.browser.pink.book.R;
import com.idotools.browser.pink.book.utils.ActivitySlideAnim;
import com.idotools.browser.pink.book.utils.StatusBarUtils;

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
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setStatusColor();
    }

    protected void setStatusColor(){
        StatusBarUtils.setColor(this,R.color.color_main_title);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivitySlideAnim.slideOutAnim(this);
    }

}
