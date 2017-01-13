package com.idotools.browser.manager.http;

import com.idotools.browser.bean.BannerResp;
import com.idotools.browser.bean.DmzjBeanResp;
import com.idotools.browser.minterface.APIService;
import com.idotools.browser.minterface.OnLoadBannerDataListener;
import com.idotools.browser.minterface.OnLoadDmzjHotDataListener;
import com.idotools.browser.minterface.OnLoadDmzjUpdateDataListener;
import com.idotools.browser.utils.Constant;
import com.idotools.utils.LogUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wuxiaojun on 16-11-9.
 */
public class AppHttpClient {


    /***
     * 加载首页banner数据
     *
     * @param packageName
     * @param versionCode
     */
    public void requestBannerPath(String packageName, int versionCode) {
        Retrofit mRetrofit = new Retrofit
                .Builder()
                .baseUrl(Constant.PATH_BASE_BANNER)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIService apiService = mRetrofit.create(APIService.class);
        Call<BannerResp> bannerBeanList1 = apiService.getBannerBeanList(packageName, "browser002");
        //异步请求网络信息
        bannerBeanList1.enqueue(new Callback<BannerResp>() {
            @Override
            public void onResponse(Call<BannerResp> call, Response<BannerResp> response) {
                List<BannerResp.BannerBean> cons = null;
                if (response.isSuccessful()) {
                    List<BannerResp.BannerData> data = response.body().data;
                    if (data != null && !data.isEmpty()) {
                        cons = data.get(0).cons;
                    }
                }
                loadBannerDataFailedListener(cons, true);
            }

            @Override
            public void onFailure(Call<BannerResp> call, Throwable t) {
                t.printStackTrace();
                loadBannerDataFailedListener(null, false);
            }
        });
    }

    private OnLoadBannerDataListener mOnLoadBannerDataListener;

    public void setOnLoadBannerDataListener(OnLoadBannerDataListener listener) {
        this.mOnLoadBannerDataListener = listener;
    }

    private void loadBannerDataFailedListener(List<BannerResp.BannerBean> cons, boolean result) {
        if (mOnLoadBannerDataListener != null) {
            if (result)
                mOnLoadBannerDataListener.loadBannerDataSuccessListener(cons);
            else
                mOnLoadBannerDataListener.loadBannerDataFailedListener();
        }
    }



    /***
     * 加载动漫之家的数据 最新更新的
     */
    public void requestDmzjUpdateBeanList(int page, int num) {
        Retrofit mRetrofit = new Retrofit.Builder().baseUrl(Constant.PATH_BASE_DMZJ).addConverterFactory(GsonConverterFactory.create()).build();
        APIService apiService = mRetrofit.create(APIService.class);
        //update 表示最新，hot表示最热
        Call<DmzjBeanResp> dmzjBeanResp = apiService.getDmzjUpdateBeanList(Constant.DMZJ_TOKEN, Constant.DMZJ_TYPE_UPDATE, page, num);
        dmzjBeanResp.enqueue(new Callback<DmzjBeanResp>() {
            @Override
            public void onResponse(Call<DmzjBeanResp> call, Response<DmzjBeanResp> response) {
                if (response.isSuccessful()) {
                    loadDmzhUpdateDataFailedListener(response.body(), true);
                }
            }

            @Override
            public void onFailure(Call<DmzjBeanResp> call, Throwable t) {
                t.printStackTrace();
                loadDmzhUpdateDataFailedListener(null, false);
            }
        });
    }

    private OnLoadDmzjUpdateDataListener mOnLoadDmzjDataListener;

    public void setOnLoadDmzjUpdateDataListener(OnLoadDmzjUpdateDataListener listener) {
        this.mOnLoadDmzjDataListener = listener;
    }

    private void loadDmzhUpdateDataFailedListener(DmzjBeanResp resp, boolean result) {
        if (mOnLoadDmzjDataListener != null) {
            if (result)
                mOnLoadDmzjDataListener.loadDmzjDataSuccessListener(resp);
            else
                mOnLoadDmzjDataListener.loadDmzjDataFailedListener();
        }
    }

    public void requestDmzjHotBeanList(int page,int num){
        Retrofit mRetrofit = new Retrofit.Builder().baseUrl(Constant.PATH_BASE_DMZJ).addConverterFactory(GsonConverterFactory.create()).build();
        APIService apiService = mRetrofit.create(APIService.class);
        //update 表示最新，hot表示最热
        Call<DmzjBeanResp> dmzjBeanResp = apiService.getDmzjHotBeanList(Constant.DMZJ_TOKEN, Constant.DMZJ_TYPE_HOT, page, num);
        dmzjBeanResp.enqueue(new Callback<DmzjBeanResp>() {
            @Override
            public void onResponse(Call<DmzjBeanResp> call, Response<DmzjBeanResp> response) {
                if (response.isSuccessful()) {
                    loadDmzhHotDataFailedListener(response.body(), true);
                }
            }

            @Override
            public void onFailure(Call<DmzjBeanResp> call, Throwable t) {
                t.printStackTrace();
                loadDmzhHotDataFailedListener(null, false);
            }
        });
    }

    private OnLoadDmzjHotDataListener mOnLoadDmzjHotDataListener;

    public void setOnLoadDmzjHotDataListener(OnLoadDmzjHotDataListener listener){
        this.mOnLoadDmzjHotDataListener = listener;
    }

    private void loadDmzhHotDataFailedListener(DmzjBeanResp resp, boolean result) {
        if (mOnLoadDmzjHotDataListener != null) {
            if (result)
                mOnLoadDmzjHotDataListener.loadDmzjDataSuccessListener(resp);
            else
                mOnLoadDmzjHotDataListener.loadDmzjDataFailedListener();
        }
    }

}
