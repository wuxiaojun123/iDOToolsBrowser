package com.idotools.browser.manager.http;

import com.idotools.browser.bean.BannerResp;
import com.idotools.browser.bean.DmzjBean;
import com.idotools.browser.minterface.APIService;
import com.idotools.browser.utils.Constant;
import com.idotools.utils.DeviceUtil;

import java.util.List;

import okhttp3.HttpUrl;
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
        Call<BannerResp> bannerBeanList1 = apiService.getBannerBeanList(packageName, "browser00");
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
     * 加载banner数据
     */
    public interface OnLoadBannerDataListener {
        /***
         * 获取数据成功，list为返回数据
         *
         * @param list
         */
        void loadBannerDataSuccessListener(List<BannerResp.BannerBean> list);

        /***
         * 加载数据失败
         */
        void loadBannerDataFailedListener();
    }


    /***
     * 加载动漫之家的数据
     */
    public void requestDmzjBeanList(int tag, String dataType, int page, int num) {
        Retrofit mRetrofit = new Retrofit.Builder().baseUrl(Constant.PATH_BASE_DMZJ).addConverterFactory(GsonConverterFactory.create()).build();
        APIService apiService = mRetrofit.create(APIService.class);
        //update 表示最新，hot表示最热
        Call<List<DmzjBean>> dmzjBeanList = apiService.getDmzjBeanList(tag, dataType, page, num);
        dmzjBeanList.enqueue(new Callback<List<DmzjBean>>() {
            @Override
            public void onResponse(Call<List<DmzjBean>> call, Response<List<DmzjBean>> response) {
                if (response.isSuccessful()) {
                    loadDmzhDataFailedListener(response.body(), true);
                }
            }

            @Override
            public void onFailure(Call<List<DmzjBean>> call, Throwable t) {
                t.printStackTrace();
                loadDmzhDataFailedListener(null, false);
            }
        });
    }

    private OnLoadDmzjDataListener mOnLoadDmzjDataListener;

    public void setOnLoadDmzjDataListener(OnLoadDmzjDataListener listener) {
        this.mOnLoadDmzjDataListener = listener;
    }

    private void loadDmzhDataFailedListener(List<DmzjBean> cons, boolean result) {
        if (mOnLoadDmzjDataListener != null) {
            if (result)
                mOnLoadDmzjDataListener.loadDmzjDataSuccessListener(cons);
            else
                mOnLoadDmzjDataListener.loadDmzjDataFailedListener();
        }
    }

    public interface OnLoadDmzjDataListener {
        /***
         * 获取数据成功，list为返回数据
         *
         * @param list
         */
        void loadDmzjDataSuccessListener(List<DmzjBean> list);

        /***
         * 加载数据失败
         */
        void loadDmzjDataFailedListener();
    }


}
