package com.manga.browser.bean;

import java.util.List;

/**
 * Created by wuxiaojun on 16-11-9.
 */
public class DmzjBeanResp {

    public int pages;//总页数
    public List<DmzjBean> comics;

    public class DmzjBean{
        public String id;
        public String title;//标题
        public String cover;//图片路径
        public String description;//描述
        public String mobileUrl;//打开的网页
        public String[] tags;//类型

        public DmzjBean(String id, String title, String cover, String description, String mobileUrl, String[] tags) {
            this.id = id;
            this.title = title;
            this.cover = cover;
            this.description = description;
            this.mobileUrl = mobileUrl;
            this.tags = tags;
        }
    }

}
