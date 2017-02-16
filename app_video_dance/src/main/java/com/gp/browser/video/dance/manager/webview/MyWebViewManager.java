package com.gp.browser.video.dance.manager.webview;

import android.app.Activity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.base.browser.manager.webview.WebViewManager;
import com.gp.browser.video.dance.activity.BrowserActivity;

/**
 * Created by wuxiaojun on 17-2-16.
 */

public class MyWebViewManager extends WebViewManager {

    private GestureDetector mGestureDetector;


    public MyWebViewManager(Activity mActivity) {
        super(mActivity);

        mGestureDetector = new GestureDetector(mContext, new CustomGestureListener());
        mWebView.setOnTouchListener(new CustomTouchListener());
    }


    private class CustomGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public void onLongPress(MotionEvent event) {
            mWebViewLongClickListener.downX = (int) event.getRawX();
            mWebViewLongClickListener.downY = (int) event.getRawY();
        }

    }

    private boolean isShowTitleAndBottom = true;

    private class CustomTouchListener implements View.OnTouchListener {

        private int downY;
        private int distance;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mWebViewLongClickListener.downX = (int) event.getX();
            mWebViewLongClickListener.downY = (int) event.getY();

            int y = (int) event.getY();
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    downY = y;

                    break;
                case MotionEvent.ACTION_UP:
                    distance = downY - y;
                    if (distance > 0 && isShowTitleAndBottom) {
                        //隐藏title和bottom----当滚动停止的时候才能执行隐藏动画
                        ((BrowserActivity) mContext).hideTitleAndBottom();
                        isShowTitleAndBottom = false;
                    } else if (distance < 0 && !isShowTitleAndBottom) {
                        //显示title和bottom
                        ((BrowserActivity) mContext).showTitleAndBottom();
                        isShowTitleAndBottom = true;
                    }
                    distance = 0;
                    break;
            }

            mGestureDetector.onTouchEvent(event);
            return false;
        }

    }

}
