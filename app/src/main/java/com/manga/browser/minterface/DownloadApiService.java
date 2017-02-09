package com.manga.browser.minterface;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by wuxiaojun on 16-12-22.
 */

public interface DownloadApiService {

    //下载图片
    @GET
    Call<ResponseBody> downloadImage(@Url String imgUrl);

}
