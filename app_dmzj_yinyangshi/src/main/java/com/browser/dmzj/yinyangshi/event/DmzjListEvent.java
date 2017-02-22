package com.browser.dmzj.yinyangshi.event;

import com.browser.dmzj.yinyangshi.bean.DmzjBeanResp;

import java.util.List;

/**
 * Created by wuxiaojun on 17-1-11.
 */

public class DmzjListEvent {

    private List<DmzjBeanResp> list;

    public DmzjListEvent(List<DmzjBeanResp> list){
        this.list = list;
    }

    public List<DmzjBeanResp> getDmzjList(){
        return list;
    }

}
