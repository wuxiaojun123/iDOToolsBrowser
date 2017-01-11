package com.idotools.browser.event;

import com.idotools.browser.bean.DmzjBeanResp;

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
