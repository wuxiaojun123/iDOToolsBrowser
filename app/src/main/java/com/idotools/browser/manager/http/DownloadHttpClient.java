package com.idotools.browser.manager.http;

import com.idotools.browser.minterface.DownloadApiService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wuxiaojun on 16-12-22.
 */

public class DownloadHttpClient {


    public void requestDownloadImage(String imgUrl) {

        DownloadApiService downloadApiService = RetrofitFactory.getInstance().getRetrofit().create(DownloadApiService.class);
        Call<ResponseBody> responseBodyCall = downloadApiService.downloadImage(imgUrl);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (mOnDownloadListener != null) {
                        mOnDownloadListener.onDownloadSuccess(response.body());
                    }
                } else {
                    callOnDownloadError();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                callOnDownloadError();
            }
        });

    }

    private void callOnDownloadError() {
        if (mOnDownloadListener != null) {
            mOnDownloadListener.onDownloadError();
        }
    }

    private OnDownloadListener mOnDownloadListener;

    public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
        this.mOnDownloadListener = onDownloadListener;
    }

    public interface OnDownloadListener {
        void onDownloadSuccess(ResponseBody responseBody);

        void onDownloadError();
    }

}
