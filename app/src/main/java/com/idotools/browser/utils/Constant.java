package com.idotools.browser.utils;

/**
 * Created by wuxiaojun on 16-10-2.
 */
public class Constant {

    //动漫之家链接http://manhua.dmzj.com/
    public static final String PATH = "http://m.dmzj.com/";
    //banner接口  http://ctrl.idourl.com:13006/server/dacon/list?packageName=com.idotools.browser&wzCode=browser00
    public static final String PATH_BANNER = "server/dacon/list";
    //动漫之家的数据接口 tagGroup.php?tag=4&order=hot&page=1&num=15
    public static final String PATH_DMZJ = "comic_info/all.php";
    //base url
    public static final String PATH_BASE_BANNER = "http://ctrl.idourl.com:13008/";
    public static final String PATH_BASE_DMZJ = "http://open.dmzj.com/";
    //动漫之家最新数据
    public static final String DMZJ_TYPE_UPDATE = "update";
    //热门推荐
    public static final String DMZJ_TYPE_HOT = "hot";
    //获取动漫之家数据的tag
    public static final int DMZJ_TAG = 4;
    //动漫之家的token
    public static final String DMZJ_TOKEN = "2c112dff5be7cf360362a360b5843b42";


    //banner和首页的json数据保存到文件中
    public static final String FILE_BANNER = "browserBanner";
    public static final String FILE_UPDATE_DATA = "browserUpdateData";
    public static final String FILE_HOT_DATA = "browserHotData";

    //facebook placement_id
    public static final String FACEBOOK_PLACEMENT_ID = "1837458113187587_1837461516520580";
    public static final String FACEBOOK_PLACEMENT_ID_BANNER = "1837458113187587_1845902922343106";


    //webview 的解析协议等
    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    public static final String FILE = "file://";
    public static final String ABOUT = "about:";
    public static final String FOLDER = "folder://";
    public static final String INTENT_ORIGIN = "URL_INTENT_ORIGIN";


    public static final String SEARCH_URL_GOOGLE = "https://www.google.com/search?nota=1&gws_rd=ssl&q=";
    public static final String SEARCH_URL_BAIDU = "http://www.baidu.com/#wd=";
    public static final String SEARCH_URL_DMZJ = "http://m.dmzj.com/search/";


    //表示当前是国内版本还是海外版本 true 为海外版本
//    public static final boolean VERSION_COUNTRY_GP = true;

}
