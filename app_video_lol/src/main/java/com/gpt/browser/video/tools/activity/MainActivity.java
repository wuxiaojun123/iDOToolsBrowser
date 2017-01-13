package com.gpt.browser.video.tools.activity;

import android.os.Bundle;
import android.view.View;

import com.gpt.browser.video.tools.R;
import com.idotools.utils.ToastUtils;

/**
 * Created by wuxiaojun on 17-1-11.
 */

public class MainActivity extends com.base.browser.activity.MainActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);

    }

    @Override
    public void onBackPressed() {
        if (id_fl_mask.getVisibility() == View.VISIBLE) {
            mSearchEditText.backKey();
        } else {
            if (mPopupWindow != null && mPopupWindow.isShow()) {
                mPopupWindow.exitStartAnim();
            } else if (mWebView != null && mWebView.canGoBack()) {
                back();
            } else {
                exit();
            }
        }
    }

    private long mExitTime;

    /**
     * 退出应用
     */
    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            ToastUtils.show(mContext, R.string.string_press_again);
            mExitTime = System.currentTimeMillis();
        } else {
            //System.exit(0);
            finish();
        }
    }

}
