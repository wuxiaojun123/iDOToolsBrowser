package com.idotools.browser.minterface;

import com.idotools.browser.bean.BannerResp;

import java.util.List;

/**
 * Created by wuxiaojun on 17-1-10.
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
