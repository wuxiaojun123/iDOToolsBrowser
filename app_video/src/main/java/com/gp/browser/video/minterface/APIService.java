package com.gp.browser.video.minterface;

import com.base.browser.bean.BannerResp;
import com.google.gson.JsonObject;
import com.gp.browser.video.bean.DmzjBean;
import com.gp.browser.video.utils.Constant;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by wuxiaojun on 16-11-9.
 */
public interface APIService {

    //请求banner页的数据
    @GET(Constant.PATH_BANNER)
    Call<BannerResp> getBannerBeanList(
            @Query("packageName") String packageName,
            @Query("wzCode") String wzCode
    );


    //请求动漫之家的数据
    @GET(Constant.PATH_WORDPRESS_BASE)
    Call<DmzjBean> getDmzjBeanList(
            @Query("json") String json,
            @Query("slug") String slug,
            @Query("page") int page,
            @Query("count") int count
    );

}
