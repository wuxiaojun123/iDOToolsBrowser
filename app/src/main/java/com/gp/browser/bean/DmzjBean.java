package com.gp.browser.bean;

/**
 * Created by wuxiaojun on 16-11-9.
 */
public class DmzjBean {

    public String id;
    public String title;//标题
    public String cover;//图片路径
    public String description;//描述
    public String url;//打开的网页
    public String tags;//类型

    public DmzjBean() {
    }

    public DmzjBean(String id, String title, String cover, String description, String url, String tags) {
        this.id = id;
        this.title = title;
        this.cover = cover;
        this.description = description;
        this.url = url;
        this.tags = tags;
    }

}
