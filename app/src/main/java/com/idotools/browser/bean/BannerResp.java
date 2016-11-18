package com.idotools.browser.bean;

import java.util.List;

/**
 * Created by wuxiaojun on 16-11-9.
 */
public class BannerResp {

    public List<BannerData> data;

    public class BannerBean {

        public String openType;//打开类型
        public String h5url;
        public String adType;//图片类型
        public String iconPath;
        public int isgif;//是否为动态图

        /*public BannerBean() {
        }

        public BannerBean(String openType, String h5url, String adType, String iconPath, int isgif) {
            this.openType = openType;
            this.h5url = h5url;
            this.adType = adType;
            this.iconPath = iconPath;
            this.isgif = isgif;
        }*/
    }

    public class BannerData {
        public String name;
        public String code;
        public String adType;
        public List<BannerBean> cons;

        /*public BannerData(String name, String code, String adType, List<BannerBean> cons) {
            this.name = name;
            this.code = code;
            this.adType = adType;
            this.cons = cons;
        }*/
    }

}
