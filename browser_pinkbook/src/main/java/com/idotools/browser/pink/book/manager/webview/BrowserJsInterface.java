package com.idotools.browser.pink.book.manager.webview;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.idotools.browser.pink.book.sqlite.SqliteManager;

/**
 * Created by wuxiaojun on 16-10-9.
 */
public class BrowserJsInterface {

    private Context mContext;
    private SqliteManager mSqliteManager;

    public BrowserJsInterface(Context context) {
        this.mContext = context;
        mSqliteManager = new SqliteManager(context);
    }

    @JavascriptInterface
    public boolean setNightMode(boolean isNightMode) {
        return isNightMode;
    }

    @JavascriptInterface
    public void showResource(String resource, String url) {
        //解析html代码

    }

    /***
     * 获取并且解析历史记录
     *
     * @param resource
     * @param url
     */
    /*private void saveOrUpdateHistory(String resource, String url) {
        try {
            if (!TextUtils.isEmpty(resource)) {
                Document parse = Jsoup.parse(resource);
                if (parse != null) {
                    //首先获取动漫的图片和标题
                    Element child = parse.select("#Cover").first().child(0);
                    if (child != null) {
                        String imgLink = child.attr("src");
                        String dmTitle = child.attr("title");
                        //获取最新的连载
                        Element elementsByClass = parse.select("#list").first().child(1);
                        String lasterChapter = elementsByClass.child(0).text();
                        //保存到数据库中
                        String sqliteTitle = mSqliteManager.selectByTitle(dmTitle);
                        if (TextUtils.isEmpty(sqliteTitle)) {
                            mSqliteManager.insert(new CartoonDetailsBean(dmTitle, imgLink, lasterChapter, url));
                        } else if (!TextUtils.equals(dmTitle, sqliteTitle)) {
                            mSqliteManager.update(dmTitle, lasterChapter);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

}
