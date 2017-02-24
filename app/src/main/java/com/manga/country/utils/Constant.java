package com.manga.country.utils;

/**
 * Created by wuxiaojun on 16-10-2.
 *
 * http://open.dmzj.com/comic_info/all.php?token=c9fec612f669b52499315c4137b04ed7&order=hot&num=10&page=1
 */
public class Constant {

    //动漫之家链接http://manhua.dmzj.com/
    public static final String PATH = "http://m.dmzj.com/";
    //banner接口  http://ctrl.idourl.com:13006/server/dacon/list?packageName=com.idotools.browser&wzCode=browser00
    public static final String PATH_BANNER = "server/dacon/list";
    //动漫之家的数据接口 tagGroup.php?tag=4&order=hot&page=1&num=15
    public static final String PATH_DMZJ = "comic_info/all.php";
    //base url
    public static final String PATH_BASE_BANNER = "http://ctrl.idourl.com:13006/";
    public static final String PATH_BASE_DMZJ = "http://open.dmzj.com/";
    //动漫之家最新数据
    public static final String DMZJ_TYPE_UPDATE = "update";
    //热门推荐
    public static final String DMZJ_TYPE_HOT = "hot";
    //获取动漫之家数据的tag
    public static final int DMZJ_TAG = 4;
    //动漫之家的token  国漫 c9fec612f669b52499315c4137b04ed7  全部 2c112dff5be7cf360362a360b5843b42
    public static final String DMZJ_TOKEN = "c9fec612f669b52499315c4137b04ed7";


    // banner和首页的json数据保存到文件中
    public static final String FILE_BANNER = "guomanBanner";
    public static final String FILE_UPDATE_DATA = "guomanUpdateData";
    public static final String FILE_HOT_DATA = "guomanHotData";

    // facebook placement_id
    public static final String FACEBOOK_PLACEMENT_ID_LATEST_UPDATE = "644482302402641_644483652402506"; // 最新更新列表
    public static final String FACEBOOK_PLACEMENT_ID_HOT_SINGLE = "644482302402641_644483609069177"; // 首页热门
    public static final String FACEBOOK_PLACEMENT_ID_HOT_DMZJ = "644482302402641_644484029069135"; // 热门列表
    public static final String FACEBOOK_PLACEMENT_ID_BANNER = "644482302402641_644484212402450"; // 底部原生广告
    public static final String FACEBOOK_PLACEMENT_ID_TABLE_PLAQUE = "644482302402641_644484305735774"; // 插屏广告

    // webview 的解析协议等
    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    public static final String FILE = "file://";
    public static final String ABOUT = "about:";
    public static final String FOLDER = "folder://";
    public static final String INTENT_ORIGIN = "URL_INTENT_ORIGIN";

    // 搜索
    public static final String SEARCH_URL_GOOGLE = "https://www.google.com/search?nota=1&gws_rd=ssl&q=";
    public static final String SEARCH_URL_BAIDU = "http://www.baidu.com/#wd=";
    public static final String SEARCH_URL_DMZJ = "http://m.dmzj.com/search/";

    //
    public static final int LOAD_MORE_NO = 2;//没有更多
    public static final int LOAD_MORE_LOADING = 0;//正在加载
    public static final int LOAD_MORE_COMPILE = 1;//加载完成
    public static final int VIEW_TYPE_HEADER1 = 9998;//表示当前view类型为正常viewType
    public static final int VIEW_TYPE_HEADER2 = 9999;//表示当前view类型为正常viewType
    public static final int VIEW_TYPE_NORMAL = 10000;//表示当前view类型为正常viewType
    public static final int VIEW_TYPE_FOOTER = 10001;//表示当前view类型是footerView
    public static final int VIEW_TYPE_AD = 19999;//当前类型是10005

    // 有妖气 腾讯漫画  快看漫画  尚漫
    public static final String URL_COMICS_SECRET = "http://link.idourl.com:16030/159103505718497cad5d84a446d110b4";
    public static final String URL_COMICS_TENCENT = "http://link.idourl.com:16030/8c429ac011034f80b09a26937a6a709f";
    public static final String URL_COMICS_READ = "http://link.idourl.com:16030/05899029f4384171911f824a25a10baf";
    public static final String URL_COMICS_BOOK = "http://link.idourl.com:16030/eb5f1dfb2ed34b2788cd1af9556ede2a";

//    public static final String URL_COMICS_BOOK = "https://h5.manhua.163.com/?comic_site=MOBILE";


}
