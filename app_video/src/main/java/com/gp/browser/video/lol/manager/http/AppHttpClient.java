package com.gp.browser.video.lol.manager.http;

import com.gp.browser.video.lol.bean.BannerResp;
import com.gp.browser.video.lol.bean.DmzjBean;
import com.gp.browser.video.lol.minterface.APIService;
import com.gp.browser.video.lol.utils.Constant;

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
        Call<BannerResp> bannerBeanList1 = apiService.getBannerBeanList(packageName, "lol001");
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
     * 加载wordpress的数据
     */
    public void requestDmzjBeanList(String cagetory, String slug, int page, int num) {
        Retrofit mRetrofit = new Retrofit
                .Builder()
                .baseUrl(Constant.PATH_WORDPRESS_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIService apiService = mRetrofit.create(APIService.class);
        //update 表示最新，hot表示最热
        Call<DmzjBean> dmzjBeanList = apiService.getDmzjBeanList(cagetory, slug, page, num);
        dmzjBeanList.enqueue(new Callback<DmzjBean>() {
            @Override
            public void onResponse(Call<DmzjBean> call, Response<DmzjBean> response) {
                if (response.isSuccessful()) {
                    //解析json数据
                    loadDmzhDataFailedListener(response.body(), true);
                }
            }

            @Override
            public void onFailure(Call<DmzjBean> call, Throwable t) {
                t.printStackTrace();
                loadDmzhDataFailedListener(null, false);
            }
        });
    }

    private OnLoadDmzjDataListener mOnLoadDmzjDataListener;

    public void setOnLoadDmzjDataListener(OnLoadDmzjDataListener listener) {
        this.mOnLoadDmzjDataListener = listener;
    }

    private void loadDmzhDataFailedListener(DmzjBean cons, boolean result) {
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
         * @param bean
         */
        void loadDmzjDataSuccessListener(DmzjBean bean);

        /***
         * 加载数据失败
         */
        void loadDmzjDataFailedListener();
    }


}
