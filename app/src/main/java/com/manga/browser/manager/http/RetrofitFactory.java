package com.manga.browser.manager.http;

import com.manga.browser.utils.Constant;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wuxiaojun on 16-12-22.
 */

public class RetrofitFactory {

    private static RetrofitFactory mRetrofitFactory;

    private Retrofit mRetrofit;

    private RetrofitFactory() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constant.PATH_BASE_DMZJ)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static RetrofitFactory getInstance() {
        if (mRetrofitFactory == null) {
            synchronized (RetrofitFactory.class) {
                if (mRetrofitFactory == null) {
                    mRetrofitFactory = new RetrofitFactory();
                }
            }
        }
        return mRetrofitFactory;
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

}
