package com.browser.dmzj.yinyangshi.minterface;

import com.browser.dmzj.yinyangshi.bean.DmzjBeanResp;

/**
 * Created by wuxiaojun on 17-1-10.
 */

public interface OnLoadDmzjHotDataListener {

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
