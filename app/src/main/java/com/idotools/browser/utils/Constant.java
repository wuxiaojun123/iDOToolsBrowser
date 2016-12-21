package com.idotools.browser.utils;

/**
 * Created by wuxiaojun on 16-10-2.
 */
public class Constant {

    //动漫之家链接http://manhua.dmzj.com/       http://nativead.baidu.com:8080/native_ad/demo/versite/versite_2.0.html
    public static final String PATH = "http://m.dmzj.com/";
    //banner接口  http://ctrl.idourl.com:13006/server/dacon/list?packageName=com.idotools.browser&wzCode=browser00
    public static final String PATH_BANNER = "server/dacon/list";
    //动漫之家的数据接口 tagGroup.php?tag=4&order=hot&page=1&num=15
    public static final String PATH_DMZJ = "tagGroup/tagGroup.php";
    //base url
    public static final String PATH_BASE_BANNER = "http://ctrl.idourl.com:13006/";
    public static final String PATH_BASE_DMZJ = "http://open.dmzj.com/";
    //动漫之家最新数据
    public static final String DMZJ_TYPE_UPDATE = "update";
    //获取动漫之家数据的tag
    public static final int DMZJ_TAG = 4;

    //banner和首页的json数据保存到文件中
    public static final String FILE_BANNER = "browserBanner";
    public static final String FILE_MAIN_DATA = "browserMainData";

    //facebook placement_id
    public static final String FACEBOOK_PLACEMENT_ID = "1837458113187587_1837461516520580";
    //
    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    public static final String FILE = "file://";
    public static final String ABOUT = "about:";
    public static final String FOLDER = "folder://";

    public static final String INTENT_ORIGIN = "URL_INTENT_ORIGIN";

    public static final String SEARCH_URL_GOOGLE = "https://www.google.com/search?nota=1&gws_rd=ssl&q=";
    public static final String SEARCH_URL_BAIDU = "http://www.baidu.com/#wd=";
    //表示当前是国内版本还是海外版本 true 为海外版本
    public static final boolean VERSION_COUNTRY_GP = true;

}
