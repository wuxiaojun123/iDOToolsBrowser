package com.manga.browser.minterface;

import com.manga.browser.bean.BannerResp;
import com.manga.browser.bean.DmzjBeanResp;
import com.manga.browser.utils.Constant;

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

    //请求热门的数据
    @GET(Constant.PATH_DMZJ)
    Call<DmzjBeanResp> getDmzjHotBeanList(
            @Query("token") String token,
            @Query("order") String order,
            @Query("page") int page,
            @Query("num") int num
    );

    //获取最新更新的
    @GET(Constant.PATH_DMZJ)
    Call<DmzjBeanResp> getDmzjUpdateBeanList(
            @Query("token") String token,
            @Query("order") String order,
            @Query("page") int page,
            @Query("num") int num
    );

}
