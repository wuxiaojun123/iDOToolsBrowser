package com.idotools.browser.bean;

/**
 * Created by wuxiaojun on 16-11-9.
 */
public class DmzjBean {

    public String id;
    public String title;
    public String cover;
    public String description;
    public String url;
    public String tags;

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
