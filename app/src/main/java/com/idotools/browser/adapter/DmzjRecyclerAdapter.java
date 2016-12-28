package com.idotools.browser.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.NativeAd;
import com.idotools.browser.R;
import com.idotools.browser.adapter.viewHolder.DmzjViewHolder;
import com.idotools.browser.adapter.viewHolder.DmzjViewHolderTypeAd;
import com.idotools.browser.adapter.viewHolder.FooterViewHolder;
import com.idotools.browser.adapter.viewHolder.Header2ViewHolder;
import com.idotools.browser.adapter.viewHolder.HeaderViewHolder;
import com.idotools.browser.bean.BannerResp;
import com.idotools.browser.bean.DmzjBean;
import com.idotools.browser.manager.viewpager.ViewPagerManager;
import com.idotools.browser.minterface.OnItemClickListener;
import com.idotools.browser.utils.Constant;
import com.idotools.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wuxiaojun on 16-11-9.
 */
public class DmzjRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int LOAD_MORE_NO = 2;//没有更多
    public static final int LOAD_MORE_LOADING = 0;//正在加载
    public static final int LOAD_MORE_COMPILE = 1;//加载完成
    private static final int VIEW_TYPE_NORMAL = 10000;//表示当前view类型为正常viewType
    private static final int VIEW_TYPE_FOOTER = 10001;//表示当前view类型是footerView
    private static final int VIEW_TYPE_AD = 19999;//当前类型是10005
    private ArrayList<FixedViewInfo> mHeaderViewInfos = new ArrayList<>();//头部view集合

    public Context mContext;
    private View footerView;//加载更多布局
    private int status_add_more;//加载更多状态
    public List<DmzjBean> mList;
    private LayoutInflater inflater;
    private String classificationStr;//分类
    private String briefIntroductionStr;//简介
    public ViewPagerManager mViewPagerManager;
    private OnItemClickListener mOnItemClickListener;
    public List<BannerResp.BannerBean> mBannerBeanList;//viewpager图片的集合

    private HashMap<String, NativeAd> nativeAdHashMap = new HashMap<>();
    private HashMap<String, List<View>> mNativeClickViewMap = new HashMap<>();
    private HashMap<String, AdChoicesView> mNativeAdChoicesMap = new HashMap<>();


    public DmzjRecyclerAdapter(Context context, List<DmzjBean> list) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        mHeaderViewInfos.add(new FixedViewInfo(inflater.inflate(R.layout.item_dmzj_header, null), 0));
        mHeaderViewInfos.add(new FixedViewInfo(inflater.inflate(R.layout.item_dmzj_header2, null), 1));
        this.mList = list;
        classificationStr = context.getString(R.string.string_classification) + ":";
        briefIntroductionStr = context.getString(R.string.string_brief_introduction) + ":";
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isHeader(viewType)) {
            int headerPosition = getHeaderPositionByViewType(viewType);
            if (headerPosition == 0) {
                return new HeaderViewHolder(mHeaderViewInfos.get(0).view);
            } else if (headerPosition == 1) {
                return new Header2ViewHolder(mHeaderViewInfos.get(1).view);
            }
        } else if (viewType == VIEW_TYPE_FOOTER) {
            return new FooterViewHolder(footerView);
        } else if (viewType == VIEW_TYPE_AD) {
            return new DmzjViewHolderTypeAd(inflater.inflate(R.layout.item_dmzj_native_ad, null));
        }
        return new DmzjViewHolder(inflater.inflate(R.layout.item_dmzj, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position >= 2) {
            if (position < (getItemCount() - 1)) {
                //设置图片
                int currentPosition = position + 4;
                if (currentPosition >= mList.size()) {
                    return;
                }
                final DmzjBean bean = mList.get(currentPosition);
                if (bean != null) {
                    DmzjViewHolder dmzjViewHolder = (DmzjViewHolder) holder;
                    //设置图片 android:background="@mipmap/img_default"
                    glideLoadImg(bean.cover, dmzjViewHolder.id_iv_img);
                    //设置信息
                    dmzjViewHolder.id_tv_title.setText(bean.title);
                    dmzjViewHolder.id_tv_tag.setText(classificationStr + bean.tags);
                    dmzjViewHolder.id_tv_description.setText(briefIntroductionStr + bean.description);
                    dmzjViewHolder.id_ll_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mOnItemClickListener != null) {
                                mOnItemClickListener.onItemClickListener(bean.url, bean.cover, bean.title);
                            }
                        }
                    });
                } else {
                    final DmzjViewHolderTypeAd dmzjViewHolder = (DmzjViewHolderTypeAd) holder;
                    String currentPositionStr = currentPosition + "";

                    NativeAd mNativeAd = nativeAdHashMap.get(currentPositionStr);
                    if (mNativeAd == null) {//实例化广告
                        synchronized ("loadAd") {
                            mNativeAd = new NativeAd(mContext, Constant.FACEBOOK_PLACEMENT_ID);
                            mNativeAd.setAdListener(new NativeAdListener(dmzjViewHolder, mNativeAd, currentPositionStr));
                            mNativeAd.loadAd(NativeAd.MediaCacheFlag.ALL);
                            nativeAdHashMap.put(currentPositionStr, mNativeAd);
                        }
                    } else {
                        //设置view上的内容
                        setNativeAdView(dmzjViewHolder, mNativeAd, currentPositionStr);
                    }
                }
            } else {//加载更多
                FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
                switch (status_add_more) {
                    case LOAD_MORE_LOADING:
                        footerViewHolder.progressBar.showNow();
                        footerViewHolder.id_ll_footer.setVisibility(View.VISIBLE);
                        break;
                    case LOAD_MORE_COMPILE:
                        footerViewHolder.progressBar.hideNow();
                        footerViewHolder.id_ll_footer.setVisibility(View.GONE);
                        break;
                    case LOAD_MORE_NO:
                        footerViewHolder.progressBar.hideNow();
                        footerViewHolder.id_ll_footer.setVisibility(View.GONE);
                        break;
                }
            }
        } else if (position == 0) {
            //第一个头部布局
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            if (mViewPagerManager == null) {
                mViewPagerManager = new ViewPagerManager(mContext,
                        headerViewHolder.id_viewpager, headerViewHolder.id_ll_dot,
                        headerViewHolder.id_iv_one, mBannerBeanList, mOnItemClickListener);
            }
            mViewPagerManager.initViewPager();

        } else if (position == 1) {
            //第二个头部布局
            if (mList == null || mList.isEmpty()) {
                return;
            }
            initHeader2ViewHolder(holder);

        }
    }

    private void setNativeAdView(DmzjViewHolderTypeAd dmzjViewHolder, NativeAd mNativeAd, String currentPosition) {
        mNativeAd.unregisterView();
        // Set the Text.
        dmzjViewHolder.nativeAdTitle.setText(mNativeAd.getAdTitle());
        dmzjViewHolder.nativeAdBody.setText(mNativeAd.getAdBody());
        dmzjViewHolder.nativeAdCallToAction.setText(mNativeAd.getAdCallToAction());
        dmzjViewHolder.nativeAdMedia.setNativeAd(mNativeAd);
        AdChoicesView adChoicesView = mNativeAdChoicesMap.get(currentPosition);
        if (adChoicesView != null) {
            /*if (adChoicesView.getParent() != null) {
                ((ViewGroup) adChoicesView.getParent()).removeView(adChoicesView);
            }*/
            dmzjViewHolder.adChoicesContainer.removeAllViews();
            LogUtils.e("缓存广告中：adChoicesView的个数：" + adChoicesView.getChildCount());
            dmzjViewHolder.adChoicesContainer.addView(adChoicesView);
        }

        // Register the Title and CTA button to listen for clicks.
        List<View> clickableViews = mNativeClickViewMap.get(currentPosition);
        if (clickableViews != null) {
            clickableViews.add(dmzjViewHolder.nativeAdTitle);
            clickableViews.add(dmzjViewHolder.nativeAdCallToAction);
            mNativeAd.registerViewForInteraction(dmzjViewHolder.native_ad_unit, clickableViews);
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
            LogUtils.e("加载失败:" + error.getErrorCode() + "=" + error.getErrorMessage());
        }

        @Override
        public void onAdLoaded(Ad ad) {
            // Set the Text.
            dmzjViewHolder.nativeAdTitle.setText(mNativeAd.getAdTitle());
            dmzjViewHolder.nativeAdBody.setText(mNativeAd.getAdBody());
            dmzjViewHolder.nativeAdCallToAction.setText(mNativeAd.getAdCallToAction());

            LogUtils.e("获取广告图片路径" + mNativeAd.getAdCoverImage().getUrl());
            dmzjViewHolder.nativeAdMedia.setNativeAd(mNativeAd);

            AdChoicesView adChoicesView = new AdChoicesView(mContext, mNativeAd, true);
            dmzjViewHolder.adChoicesContainer.addView(adChoicesView);
            mNativeAdChoicesMap.put(currentPosition, adChoicesView);

            // Register the Title and CTA button to listen for clicks.
            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(dmzjViewHolder.nativeAdTitle);
            clickableViews.add(dmzjViewHolder.nativeAdCallToAction);
            mNativeAd.registerViewForInteraction(dmzjViewHolder.native_ad_unit, clickableViews);
            mNativeClickViewMap.put(currentPosition, clickableViews);
        }

        @Override
        public void onAdClicked(Ad ad) {
            // Ad clicked callback
        }
    }


    /***
     * 初始化头部布局样式2
     *
     * @param holder
     */
    private void initHeader2ViewHolder(RecyclerView.ViewHolder holder) {
        Header2ViewHolder header2ViewHolder = (Header2ViewHolder) holder;
        DmzjBean bean1 = mList.get(0);
        if (bean1 != null) {
            glideLoadImg(bean1.cover, header2ViewHolder.id_iv_image_first);
            header2ViewHolder.id_tv_text_first.setText(bean1.title);
            setOnClickListener(header2ViewHolder.id_iv_image_first, bean1.url, bean1.cover, bean1.title);
        }

        DmzjBean bean2 = mList.get(1);
        glideLoadImg(bean2.cover, header2ViewHolder.id_iv_image_second);
        header2ViewHolder.id_tv_text_second.setText(bean2.title);
        setOnClickListener(header2ViewHolder.id_iv_image_second, bean2.url, bean2.cover, bean2.title);

        DmzjBean bean3 = mList.get(2);
        glideLoadImg(bean3.cover, header2ViewHolder.id_iv_image_third);
        header2ViewHolder.id_tv_text_third.setText(bean3.title);
        setOnClickListener(header2ViewHolder.id_iv_image_third, bean3.url, bean3.cover, bean3.title);

        DmzjBean bean4 = mList.get(3);
        glideLoadImg(bean4.cover, header2ViewHolder.id_iv_image_four);
        header2ViewHolder.id_tv_text_four.setText(bean4.title);
        setOnClickListener(header2ViewHolder.id_iv_image_four, bean4.url, bean4.cover, bean4.title);

        DmzjBean bean5 = mList.get(4);
        glideLoadImg(bean5.cover, header2ViewHolder.id_iv_image_five);
        header2ViewHolder.id_tv_text_five.setText(bean5.title);
        setOnClickListener(header2ViewHolder.id_iv_image_five, bean5.url, bean5.cover, bean5.title);

        DmzjBean bean6 = mList.get(5);
        glideLoadImg(bean6.cover, header2ViewHolder.id_iv_image_six);
        header2ViewHolder.id_tv_text_six.setText(bean6.title);
        setOnClickListener(header2ViewHolder.id_iv_image_six, bean6.url, bean6.cover, bean6.title);
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
        //减3是因为加载更多需要一个显示
        return mList.size() - 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderPosition(position)) {
            return mHeaderViewInfos.get(position).viewType;
        } else if (isFooterPosition(position)) {
            return VIEW_TYPE_FOOTER;
        } else {
            return judgmentType(position);
        }
    }

    private int judgmentType(int position) {
        int currentPosition = position + 4;
        if (mList.get(currentPosition) != null) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_AD;
        }
    }

    /***
     * 添加更多
     *
     * @param list
     */
    public void addMoreItem(List<DmzjBean> list, int status) {
        this.mList.addAll(list);
        this.status_add_more = status;
        notifyDataSetChanged();
    }

    /***
     * 重置数据
     *
     * @param list
     */
    public void resetAdapter(List<DmzjBean> list) {
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
        if (status_add_more == LOAD_MORE_LOADING) {
        }
        notifyDataSetChanged();
    }

    /***
     * 根据viewType获取position
     *
     * @param viewType
     * @return
     */
    private int getHeaderPositionByViewType(int viewType) {
        int size = mHeaderViewInfos.size();
        for (int i = 0; i < size; i++) {
            if (viewType == mHeaderViewInfos.get(i).viewType) {
                return i;
            }
        }
        return -1;
    }

    /***
     * 判断当前类型是否为头部
     *
     * @param viewType
     * @return
     */
    private boolean isHeader(int viewType) {
        for (FixedViewInfo mInfo : mHeaderViewInfos) {
            if (mInfo.viewType == viewType) {
                return true;
            }
        }
        return false;
    }

    /***
     * 判断是否为头部位置
     *
     * @param position
     * @return
     */
    private boolean isHeaderPosition(int position) {
        return position < mHeaderViewInfos.size();
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

    /***
     * 设置尾部view
     *
     * @param footerView
     */
    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    /***
     * 存储头部和尾部的信息
     */
    public class FixedViewInfo {

        public View view;
        public int viewType;

        public FixedViewInfo(View view, int viewType) {
            this.view = view;
            this.viewType = viewType;
        }
    }

}
