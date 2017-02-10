package com.manga.browser.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.idotools.utils.SharedPreferencesHelper;
import com.manga.browser.R;
import com.manga.browser.utils.ActivitySlideAnim;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wuxiaojun on 16-12-20.
 */

public class SplashActivity extends Activity {

    @BindView(R.id.id_iv_start_page)
    ImageView id_iv_start_page;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            startActivity(new Intent(SplashActivity.this, DmzjActivity.class));
            ActivitySlideAnim.slideInAnim(SplashActivity.this);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        initNightMode();
        //获取推送数据
        getGeTuiMsg();
    }

    private void initNightMode() {
        boolean modeNight = SharedPreferencesHelper.getInstance(getApplicationContext()).getBoolean(SharedPreferencesHelper.SP_KEY_MODE_NIGHT, false);
        if (modeNight) { // 夜间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public void getGeTuiMsg() {
        String url = getIntent().getStringExtra("url");
        if (!TextUtils.isEmpty(url)) {
            Intent mIntent = new Intent(SplashActivity.this, DmzjActivity.class);
            mIntent.putExtra("url", url);
            startActivity(mIntent);
            ActivitySlideAnim.slideInAnim(SplashActivity.this);
            finish();
        } else {
            startPageAnim();
        }
    }

    private void startPageAnim() {
        mHandler.sendEmptyMessageDelayed(0, 2000);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeMessages(0);
            mHandler = null;
        }
    }

}