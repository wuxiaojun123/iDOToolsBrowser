package com.idotools.browser.minterface;

import com.idotools.browser.bean.BannerResp;
import com.idotools.browser.bean.DmzjBean;
import com.idotools.browser.utils.Constant;

import java.util.List;

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
    @GET(Constant.PATH_DMZJ)
    Call<List<DmzjBean>> getDmzjBeanList(
            @Query("tag") int tag,
            @Query("order") String order,
            @Query("page") int page,
            @Query("num") int num
    );

}
