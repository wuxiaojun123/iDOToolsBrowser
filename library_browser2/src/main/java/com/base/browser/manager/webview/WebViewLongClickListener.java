package com.base.browser.manager.webview;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;

import com.base.browser.manager.popupwindow.LongPressPopupWindow;


/**
 * webview long press save image
 * Created by wuxiaojun on 16-12-22.
 */
public class WebViewLongClickListener implements View.OnLongClickListener {

    public int downX;
    public int downY;

    public LongPressPopupWindow popupWindow;

    public WebViewLongClickListener(Context context) {
        popupWindow = new LongPressPopupWindow(context);
    }

    @Override
    public boolean onLongClick(View v) {
        if (v instanceof WebView) {
            WebView webview = (WebView) v;
            WebView.HitTestResult hitTestResult = ((WebView) v).getHitTestResult();
            int resultType = hitTestResult.getType();
            if (resultType == WebView.HitTestResult.IMAGE_TYPE || resultType == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                String imgUrl = hitTestResult.getExtra();//图片下载地址
                if (!TextUtils.isEmpty(imgUrl)) {
                    //显示popupwindow
                    popupWindow.snakeView = v;
                    popupWindow.imgUrl = imgUrl;
                    popupWindow.referer = webview.getUrl();
                    popupWindow.showAtLocation(v, Gravity.TOP | Gravity.LEFT, downX, downY + 10);
                }
            }
        }
        return false;
    }

    public void unRegisterReceiverDownload() {
        if (popupWindow != null) {
            popupWindow.unRegisterReceiverDownload();
        }
    }

}
