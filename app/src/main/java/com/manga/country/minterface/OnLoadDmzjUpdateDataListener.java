package com.manga.country.minterface;

import com.manga.country.bean.DmzjBeanResp;

/**
 * Created by wuxiaojun on 17-1-10.
 */

public interface OnLoadDmzjUpdateDataListener {

    /***
     * 获取数据成功，list为返回数据
     *
     * @param resp
     */
    void loadDmzjDataSuccessListener(DmzjBeanResp resp);

    /***
     * 加载数据失败
     */
    void loadDmzjDataFailedListener();

}
