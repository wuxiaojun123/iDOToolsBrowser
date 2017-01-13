package com.idotools.browser.manager.webview;

import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.idotools.utils.LogUtils;

/**
 * Created by wuxiaojun on 16-10-8.
 */
public class BrowserWebChromeClient extends WebChromeClient {

    private WebviewInteface mWebViewInteface;

    //屏蔽首页的图像
    private StringBuilder bottomSb = new StringBuilder("javascript:var ad = document.getElementById('app_home_ad');if(ad != null){ad.parentNode.removeChild(ad)};");
    //屏蔽下方的ad
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
        /*bottomSb2.append("var e = document.createEvent(\"MouseEvents\");\n" +
                "\t\te.initEvent(\"click\", true, true);\n" +
                "\t\tdocument.getElementById(\"in_home\").dispatchEvent(e);");*/
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if (mWebViewInteface != null) {
            mWebViewInteface.showProgress(newProgress);
        }
        String url = view.getUrl();
        if (!TextUtils.isEmpty(url)) {
            if ("http://m.dmzj.com/".equals(url)) {
                view.loadUrl(bottomSb.toString());
                view.loadUrl(bottomSb2.toString());
            } else if (url.contains("dmzj")) {
                view.loadUrl(bottomSb2.toString());
            }
        }
    }


}
