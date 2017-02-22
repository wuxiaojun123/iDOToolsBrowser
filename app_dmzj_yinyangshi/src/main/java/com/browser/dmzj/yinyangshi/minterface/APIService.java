package com.browser.dmzj.yinyangshi.minterface;

import com.browser.dmzj.yinyangshi.bean.BannerResp;
import com.browser.dmzj.yinyangshi.bean.DmzjBeanResp;
import com.browser.dmzj.yinyangshi.utils.Constant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by wuxiaojun on 16-11-9.
 */
public interface APIService {

    //请求banner页的数据 post请求
    @GET(Constant.PATH_BANNER)
    Call<BannerResp> getBannerBeanList(
            @Query("packageName") String packageName,
            @Query("wzCode") String wzCode
    );

    /*//请求banner页的数据 post请求
    @FormUrlEncoded
    @POST(Constant.PATH_BANNER)
    Call<BannerResp> getBannerBeanList(
            @Field("packageName") String packageName,
            @Field("wzCode") String wzCode
    );*/

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
