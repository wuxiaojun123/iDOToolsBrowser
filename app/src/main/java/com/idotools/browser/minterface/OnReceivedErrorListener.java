package com.idotools.browser.minterface;

import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

/**
 * Created by wuxiaojun on 16-10-19.
 */
public interface OnReceivedErrorListener {

    void onReceivedError(WebView view, int errorCode);

}
