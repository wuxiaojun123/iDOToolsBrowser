package com.gp.browser.manager.webview;

import android.os.Message;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.gp.utils.LogUtils;

/**
 * Created by wuxiaojun on 16-10-8.
 */
public class BrowserWebChromeClient extends WebChromeClient {

    private WebviewInteface mWebViewInteface;

    //    private StringBuilder bottomSb = new StringBuilder();
    private StringBuilder bottomSb2 = new StringBuilder("javascript:var ad = document.getElementById('khdDown');if(ad != null){ad.parentNode.removeChild(ad)};");

    public BrowserWebChromeClient(WebviewInteface mWebViewInteface) {
        this.mWebViewInteface = mWebViewInteface;
        /*bottomSb.append("javascript:function in_home{\n" +
                "        $(\"#app_home_ad\").hide();\n" +
                "        $.cookie(\"app_home_ad\",0,{path:'/'});\n" +
                "        app_ad.app_ad_cookie();\n" +
                "        $('html,body').removeClass('ovfHiden');\n" +
                "\n" +
                "    };in_home();");*/
        bottomSb2.append("var e = document.createEvent(\"MouseEvents\");\n" +
                "\t\te.initEvent(\"click\", true, true);\n" +
                "\t\tdocument.getElementById(\"in_home\").dispatchEvent(e);");
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if (mWebViewInteface != null) {
            mWebViewInteface.showProgress(newProgress);
        }
        view.loadUrl(bottomSb2.toString());
    }


}
