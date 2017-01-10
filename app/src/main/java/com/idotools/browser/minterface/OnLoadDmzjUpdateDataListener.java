package com.idotools.browser.minterface;

import com.idotools.browser.bean.DmzjBean;

import java.util.List;

/**
 * Created by wuxiaojun on 17-1-10.
 */

public interface OnLoadDmzjUpdateDataListener {

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
