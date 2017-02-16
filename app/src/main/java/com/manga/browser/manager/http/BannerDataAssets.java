package com.manga.browser.manager.http;

import android.content.Context;
import android.text.TextUtils;

import com.manga.browser.bean.BannerResp;
import com.manga.browser.utils.JsonUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by wuxiaojun on 17-2-13.
 */

public class BannerDataAssets {


    /***
     * 获取
     * @param context
     * @return
     */
    public List<BannerResp.BannerBean> parseJsonToBanner(Context context){
        String bannerJson = loadFromAssetsTitleJson(context);
        if(!TextUtils.isEmpty(bannerJson)){
            BannerResp bannerResp = (BannerResp) JsonUtils.toObject(bannerJson,BannerResp.class);
            if(bannerResp != null){
                return bannerResp.data.get(0).cons;
            }
        }
        return null;
    }

    /***
     * 从assets中获取json数据
     * @param context
     * @return
     */
    public String loadFromAssetsTitleJson(Context context) {
        String result = null;
        InputStream is = null;
        ByteArrayOutputStream outStream = null;
        try {
            is = context.getResources().getAssets().open("banner.json");
            int BUFFER_SIZE = is.available();
            outStream = new ByteArrayOutputStream();
            byte[] data = new byte[BUFFER_SIZE];
            int count = -1;
            while ((count = is.read(data, 0, BUFFER_SIZE)) != -1) {
                outStream.write(data, 0, count);
            }
            data = null;
            result = new String(outStream.toByteArray(), "utf-8");
        } catch (Exception e) {
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                    outStream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                    is = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

}
