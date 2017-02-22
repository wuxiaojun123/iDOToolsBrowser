package com.browser.dmzj.yinyangshi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.NativeAd;
import com.browser.dmzj.yinyangshi.R;
import com.browser.dmzj.yinyangshi.adapter.viewHolder.DmzjViewHolder;
import com.browser.dmzj.yinyangshi.adapter.viewHolder.DmzjViewHolderTypeAd;
import com.browser.dmzj.yinyangshi.adapter.viewHolder.FooterViewHolder;
import com.browser.dmzj.yinyangshi.adapter.viewHolder.Header2ViewHolder;
import com.browser.dmzj.yinyangshi.adapter.viewHolder.HeaderViewHolder;
import com.browser.dmzj.yinyangshi.bean.BannerResp;
import com.browser.dmzj.yinyangshi.bean.DmzjBeanResp;
import com.browser.dmzj.yinyangshi.manager.viewpager.FragmentViewPagerManger;
import com.browser.dmzj.yinyangshi.manager.viewpager.ViewPagerManager;
import com.browser.dmzj.yinyangshi.minterface.OnItemClickListener;
import com.browser.dmzj.yinyangshi.utils.Constant;
import com.browser.dmzj.yinyangshi.utils.DoAnalyticsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wuxiaojun on 16-11-9.
 */
public class DmzjRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public Context mContext;
    private View footerView;//加载更多布局
    private int status_add_more;//加载更多状态
    public List<DmzjBeanResp.DmzjBean> mList;
    private LayoutInflater inflater;
    private String classificationStr;//分类
    private String briefIntroductionStr;//简介
    private OnItemClickListener mOnItemClickListener;

    private View headView1;//head view 1
    private View headView2;//head view 2

    // viewpager banner head1
    private ViewPagerManager mBannerViewPagerManager;
    private List<BannerResp.BannerBean> mBannerBeanList;

    // viewpager fragment head2
    private FragmentManager mFragmentManager;
    private FragmentViewPagerManger mFragmentVPManager;

    private HashMap<String, NativeAd> nativeAdHashMap = new HashMap<>();
    private HashMap<String, List<View>> mNativeClickViewMap = new HashMap<>();
    private HashMap<String, AdChoicesView> mNativeAdChoicesMap = new HashMap<>();


    public DmzjRecyclerAdapter(Context context, List<DmzjBeanResp.DmzjBean> list) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.mList = list;
        headView1 = inflater.inflate(R.layout.item_dmzj_header, null);
        headView2 = inflater.inflate(R.layout.item_dmzj_header2, null);
        classificationStr = context.getString(R.string.string_classification) + ":";
        briefIntroductionStr = context.getString(R.string.string_brief_introduction) + ":";
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Constant.VIEW_TYPE_FOOTER) {
            return new FooterViewHolder(footerView);
        } else if (viewType == Constant.VIEW_TYPE_HEADER1) {
            return new HeaderViewHolder(headView1);
        } else if (viewType == Constant.VIEW_TYPE_HEADER2) {
            return new Header2ViewHolder(headView2);
        } else if (viewType == Constant.VIEW_TYPE_AD) {
            return new DmzjViewHolderTypeAd(inflater.inflate(R.layout.item_dmzj_native_ad,parent,false));
        }
        return new DmzjViewHolder(inflater.inflate(R.layout.item_dmzj, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DmzjViewHolder) {
            //设置图片
            position = position - 2;
            final DmzjBeanResp.DmzjBean bean = mList.get(position);
            if (bean != null) {
                DmzjViewHolder dmzjViewHolder = (DmzjViewHolder) holder;
                bindDmzjViewHolder(dmzjViewHolder, bean);
                toogleNightModeByDmzj(dmzjViewHolder);
            }

        } else if (holder instanceof DmzjViewHolderTypeAd) {
            final DmzjViewHolderTypeAd adViewHolder = (DmzjViewHolderTypeAd) holder;
            bindNativeAdViewHolder(adViewHolder, position);

        } else if (holder instanceof Header2ViewHolder) {
            //第二个head
            Header2ViewHolder mHeaderViewHolder2 = (Header2ViewHolder) holder;
            bindHeadViewHolder2(mHeaderViewHolder2);
            toogleNightModeByHeader2(mHeaderViewHolder2);

        } else if (holder instanceof HeaderViewHolder) {
            //第一个head
            HeaderViewHolder mHeaderViewHolder1 = (HeaderViewHolder) holder;
            initBannerAdapter(mHeaderViewHolder1);
            toogleNightModeByHeader(mHeaderViewHolder1);

        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            bindFooterViewHolder(footerViewHolder);
        }
    }

    private void toogleNightModeByDmzj(DmzjViewHolder dmzjViewHolder) {
        if (modeNightFlag > 0) {
            if (modeNightFlag == 1) { // 白天模式
                dmzjViewHolder.id_tv_title.setTextColor(ContextCompat.getColor(mContext, R.color.color_about_text));
                dmzjViewHolder.id_tv_tag.setTextColor(ContextCompat.getColor(mContext, R.color.color_46));
                dmzjViewHolder.view_line.setBackgroundResource(R.color.color_e8);
            } else { // 夜间模式
                dmzjViewHolder.id_tv_title.setTextColor(ContextCompat.getColor(mContext, R.color.color_9));
                dmzjViewHolder.id_tv_tag.setTextColor(ContextCompat.getColor(mContext, R.color.color_c8));
                dmzjViewHolder.view_line.setBackgroundResource(R.color.color_63);
            }
        }
    }

    private void toogleNightModeByHeader(HeaderViewHolder mHeaderViewHolder1) {
        if (modeNightFlag > 0) {
            if (modeNightFlag == 1) { // 白天模式
                mHeaderViewHolder1.tv_wave.setImageResource(R.drawable.img_banner_wave_day);
            } else { // 夜间模式
                mHeaderViewHolder1.tv_wave.setImageResource(R.drawable.img_banner_wave_night);
            }
        }
    }

    private void toogleNightModeByHeader2(Header2ViewHolder mHeaderViewHolder2) {
        if (modeNightFlag > 0) {
            if (modeNightFlag == 1) { // 白天模式
                mHeaderViewHolder2.tv_new_update.setTextColor(Color.BLACK);
                mHeaderViewHolder2.tv_hot_recommend.setTextColor(Color.BLACK);
                mHeaderViewHolder2.tv_adorable_station.setTextColor(Color.BLACK);

                mHeaderViewHolder2.view_space1.setBackgroundResource(R.color.color_e8);
                mHeaderViewHolder2.view_space2.setBackgroundResource(R.color.color_e8);
            } else { // 夜间模式
                mHeaderViewHolder2.tv_new_update.setTextColor(Color.WHITE);
                mHeaderViewHolder2.tv_hot_recommend.setTextColor(Color.WHITE);
                mHeaderViewHolder2.tv_adorable_station.setTextColor(Color.WHITE);

                mHeaderViewHolder2.view_space1.setBackgroundResource(R.color.color_63);
                mHeaderViewHolder2.view_space2.setBackgroundResource(R.color.color_63);
            }
        }
    }

    private boolean isLoadedHeadView2 = false;

    private void bindHeadViewHolder2(Header2ViewHolder mHeaderViewHolder2) {
        if (mFragmentVPManager == null) {
            mFragmentVPManager = new FragmentViewPagerManger(mHeaderViewHolder2, mFragmentManager, mContext);
            mFragmentVPManager.initFragment();
            mFragmentVPManager.initEvent();
        } else {
            if (!isLoadedHeadView2 && hotRecommendList != null) {
                mFragmentVPManager.setFragmentDmzjBeanList(hotRecommendList);
                isLoadedHeadView2 = true;
            }
        }
    }

    /***
     * 初始化banner
     */
    private void initBannerAdapter(HeaderViewHolder mHeaderViewHolder) {
        if (mBannerViewPagerManager == null) {
            mBannerViewPagerManager = new ViewPagerManager(mContext,
                    mHeaderViewHolder.id_viewpager, mHeaderViewHolder.id_ll_dot,
                    mHeaderViewHolder.id_iv_one, mBannerBeanList);
            mBannerViewPagerManager.initViewPager();
        } else {
            mBannerViewPagerManager.refreshAdapter(mBannerBeanList);
        }
    }


    private void bindDmzjViewHolder(DmzjViewHolder dmzjViewHolder, final DmzjBeanResp.DmzjBean bean) {
        //设置图片 android:background="@mipmap/img_default"
        glideLoadImg(bean.cover, dmzjViewHolder.id_iv_img);
        //设置信息
        dmzjViewHolder.id_tv_title.setText(bean.title);
        dmzjViewHolder.id_tv_tag.setText(classificationStr + getTags(bean.tags));
        dmzjViewHolder.id_tv_description.setText(briefIntroductionStr + bean.description);
        dmzjViewHolder.id_ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClickListener(bean.mobileUrl, bean.cover, bean.title);
                }
            }
        });
    }

    /***
     * 绑定广告view
     *
     * @param dmzjViewHolder
     * @param position
     */
    private void bindNativeAdViewHolder(DmzjViewHolderTypeAd dmzjViewHolder, int position) {
        String currentPositionStr = position + "";
        dmzjViewHolder.native_ad_unit.setVisibility(View.GONE);

        NativeAd mNativeAd = nativeAdHashMap.get(currentPositionStr);
        if (mNativeAd == null) {//实例化广告
            synchronized ("loadAd") {
                mNativeAd = new NativeAd(mContext, Constant.FACEBOOK_PLACEMENT_ID_LATEST_UPDATE);
                mNativeAd.setAdListener(new NativeAdListener(dmzjViewHolder, mNativeAd, currentPositionStr));
                mNativeAd.loadAd(NativeAd.MediaCacheFlag.ALL);
                nativeAdHashMap.put(currentPositionStr, mNativeAd);
            }
        } else {
            //设置view上的内容
            setNativeAdView(dmzjViewHolder, mNativeAd, currentPositionStr);
        }
    }

    /***
     * 绑定footerViewHolder
     *
     * @param footerViewHolder
     */
    private void bindFooterViewHolder(FooterViewHolder footerViewHolder) {
        switch (status_add_more) {
            case Constant.LOAD_MORE_LOADING:
                footerViewHolder.progressBar.showNow();
                footerViewHolder.id_ll_footer.setVisibility(View.VISIBLE);
                break;
            case Constant.LOAD_MORE_COMPILE:
                footerViewHolder.progressBar.hideNow();
                footerViewHolder.id_ll_footer.setVisibility(View.GONE);
                break;
            case Constant.LOAD_MORE_NO:
                footerViewHolder.progressBar.hideNow();
                footerViewHolder.id_ll_footer.setVisibility(View.GONE);
                break;
        }
    }

    private void setNativeAdView(DmzjViewHolderTypeAd dmzjViewHolder, NativeAd mNativeAd, String currentPosition) {
        try {
            if (dmzjViewHolder.native_ad_unit.getVisibility() == View.GONE) {
                if (!TextUtils.isEmpty(mNativeAd.getAdTitle())) {
                    dmzjViewHolder.native_ad_unit.setVisibility(View.VISIBLE);
                }
            }

            mNativeAd.unregisterView();
            // Set the Text.
            dmzjViewHolder.nativeAdTitle.setText(mNativeAd.getAdTitle());
            dmzjViewHolder.nativeAdBody.setText(mNativeAd.getAdBody());
            dmzjViewHolder.nativeAdCallToAction.setText(mNativeAd.getAdCallToAction());
            dmzjViewHolder.nativeAdMedia.setNativeAd(mNativeAd);
            /*AdChoicesView adChoicesView = mNativeAdChoicesMap.get(currentPosition);
            if (adChoicesView != null) {
                if (adChoicesView.getParent() != null) {
                    ((ViewGroup) adChoicesView.getParent()).removeView(adChoicesView);
                }
                dmzjViewHolder.adChoicesContainer.removeAllViews();
                dmzjViewHolder.adChoicesContainer.addView(adChoicesView);
            }*/

            // Register the Title and CTA button to listen for clicks.
            List<View> clickableViews = mNativeClickViewMap.get(currentPosition);
            if (clickableViews != null) {
                clickableViews.add(dmzjViewHolder.native_ad_unit);
                clickableViews.add(dmzjViewHolder.nativeAdCallToAction);
                mNativeAd.registerViewForInteraction(dmzjViewHolder.native_ad_unit, clickableViews);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 广告回调内部类
     */
    private class NativeAdListener implements AdListener {

        DmzjViewHolderTypeAd dmzjViewHolder;
        NativeAd mNativeAd;
        String currentPosition;

        public NativeAdListener(DmzjViewHolderTypeAd dmzjViewHolder, NativeAd mNativeAd, String currentPosition) {
            this.dmzjViewHolder = dmzjViewHolder;
            this.mNativeAd = mNativeAd;
            this.currentPosition = currentPosition;
        }

        @Override
        public void onError(Ad ad, AdError error) {
            dmzjViewHolder.native_ad_unit.setVisibility(View.GONE);
        }

        @Override
        public void onAdLoaded(Ad ad) {
            // Set the Text.  显示广告
            if (!TextUtils.isEmpty(mNativeAd.getAdTitle())) {
                dmzjViewHolder.native_ad_unit.setVisibility(View.VISIBLE);
            }

            dmzjViewHolder.nativeAdTitle.setText(mNativeAd.getAdTitle());
            dmzjViewHolder.nativeAdBody.setText(mNativeAd.getAdBody());
            dmzjViewHolder.nativeAdCallToAction.setText(mNativeAd.getAdCallToAction());

            dmzjViewHolder.nativeAdMedia.setNativeAd(mNativeAd);

//            AdChoicesView adChoicesView = new AdChoicesView(mContext, mNativeAd, true);
//            dmzjViewHolder.adChoicesContainer.addView(adChoicesView);
//            mNativeAdChoicesMap.put(currentPosition, adChoicesView);

            // Register the Title and CTA button to listen for clicks.
            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(dmzjViewHolder.native_ad_unit);
            clickableViews.add(dmzjViewHolder.nativeAdCallToAction);
            mNativeAd.registerViewForInteraction(dmzjViewHolder.native_ad_unit, clickableViews);
            mNativeClickViewMap.put(currentPosition, clickableViews);
        }

        @Override
        public void onAdClicked(Ad ad) {
            // Ad clicked callback
            DoAnalyticsManager.event(mContext, DoAnalyticsManager.DOT_KEY_UPDATE_AD_CLICK);
        }
    }

    private String getTags(String[] tags) {
        String str = null;
        if (tags != null && tags.length > 0) {
            StringBuilder sb = new StringBuilder();
            int length = tags.length;
            for (int i = 0; i < length; i++) {
                sb.append(tags[i] + ",");
            }
            str = sb.substring(0, sb.length() - 1);
        }
        return str;
    }

    /***
     * 给view设置点击事件
     *
     * @param imageView
     * @param url
     */
    private void setOnClickListener(ImageView imageView, final String url, final String imgUrl, final String title) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClickListener(url, imgUrl, title);
                }
            }
        });
    }

    /***
     * 加载图片
     *
     * @param cover
     * @param imageView
     */
    private void glideLoadImg(String cover, ImageView imageView) {
        Glide.with(mContext)
                .load(cover)
                .error(R.mipmap.img_default)
                .placeholder(R.mipmap.img_default)
                .centerCrop()
                .crossFade()
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return mList.size() + 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (isFooterPosition(position)) {
            return Constant.VIEW_TYPE_FOOTER;
        } else if (position == 0) {
            return Constant.VIEW_TYPE_HEADER1;
        } else if (position == 1) {
            return Constant.VIEW_TYPE_HEADER2;
        } else {
            return judgmentType(position);
        }
    }

    private int judgmentType(int position) {
        if (mList.get(position - 2) != null) {
            return Constant.VIEW_TYPE_NORMAL;
        } else {
            return Constant.VIEW_TYPE_AD;
        }
    }

    /***
     * 添加更多
     *
     * @param list
     */
    public void addMoreItem(List<DmzjBeanResp.DmzjBean> list, int status) {
        this.mList.addAll(list);
        this.status_add_more = status;
        notifyDataSetChanged();
    }

    /***
     * 重置数据
     *
     * @param list
     */
    public void resetAdapter(List<DmzjBeanResp.DmzjBean> list) {
        if (mList != null) {
            mList.clear();
        }
        mList.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 修改加载更多状态
     */
    public void changeAddMoreStatus(int status) {
        this.status_add_more = status;
        notifyDataSetChanged();
    }

    /***
     * 判断是否为加载更多view类型
     *
     * @param position
     * @return
     */
    private boolean isFooterPosition(int position) {
        if (position >= (getItemCount() - 1)) {
            return true;
        }
        return false;
    }

    private List<DmzjBeanResp.DmzjBean> hotRecommendList;

    /***
     * 设置fragment里面的数据
     *
     * @param list
     */
    public void setHeadView2Data(List<DmzjBeanResp.DmzjBean> list) {
        this.hotRecommendList = list;
        if (mFragmentVPManager != null) {
            mFragmentVPManager.setFragmentDmzjBeanList(list);
        }
        notifyDataSetChanged();
    }

    private int modeNightFlag;

    public void refreshNightMode(int modeNight) {
        this.modeNightFlag = modeNight;
        notifyDataSetChanged();
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setBannerBeanList(List<BannerResp.BannerBean> list) {
        this.mBannerBeanList = list;
    }

    public void setFragmentManager(FragmentManager fm) {
        this.mFragmentManager = fm;
    }

    public void setFragmentVPManager(FragmentViewPagerManger fragmentVPManager) {
        this.mFragmentVPManager = fragmentVPManager;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

}
