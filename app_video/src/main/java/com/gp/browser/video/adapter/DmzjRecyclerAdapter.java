package com.gp.browser.video.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.base.browser.bean.BannerResp;
import com.base.browser.minterface.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.NativeAd;
import com.gp.browser.video.R;
import com.gp.browser.video.adapter.viewHolder.DmzjViewHolderType2;
import com.gp.browser.video.adapter.viewHolder.DmzjViewHolderTypeAd;
import com.gp.browser.video.adapter.viewHolder.FooterViewHolder;
import com.gp.browser.video.adapter.viewHolder.Header2ViewHolder;
import com.gp.browser.video.adapter.viewHolder.HeaderViewHolder;
import com.gp.browser.video.bean.DmzjBean;
import com.gp.browser.video.manager.viewpager.ViewPagerManager;
import com.gp.browser.video.utils.Constant;
import com.gp.utils.LogUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wuxiaojun on 16-11-9.
 */
public class DmzjRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //头部view集合
    private ArrayList<FixedViewInfo> mHeaderViewInfos = new ArrayList<>();
    private static final int VIEW_TYPE_FOOTER = 10001;//表示当前view类型是footerView
    private static final int VIEW_TYPE_2 = 10003;//当前类型是10003
    private static final int VIEW_TYPE_ADMOB = 19999;//当前类型是10005

    public static final int LOAD_MORE_LOADING = 0;//正在加载
    public static final int LOAD_MORE_COMPILE = 1;//加载完成

    public Context mContext;
    private View footerView;//加载更多布局
    private int status_add_more;//加载更多状态
    private LayoutInflater inflater;
    public List<DmzjBean.PostsBean> mList;//数据集合
    public static final int LOAD_MORE_NO = 2;//没有更多
    public ViewPagerManager mViewPagerManager;//viewpager轮播图片管理类
    private OnItemClickListener mOnItemClickListener;
    public List<BannerResp.BannerBean> mBannerBeanList;//viewpager图片的集合

    private HashMap<String, NativeAd> nativeAdHashMap = new HashMap<>();
    private HashMap<String, List<View>> mNativeClickViewMap = new HashMap<>();
    private HashMap<String, AdChoicesView> mNativeAdChoicesMap = new HashMap<>();

    public DmzjRecyclerAdapter(Context context, List<DmzjBean.PostsBean> list) {
        inflater = LayoutInflater.from(context);
        mHeaderViewInfos.add(new FixedViewInfo(inflater.inflate(R.layout.item_dmzj_header, null), 0));
        mHeaderViewInfos.add(new FixedViewInfo(inflater.inflate(R.layout.item_dmzj_header2, null), 1));
        this.mContext = context.getApplicationContext();
        this.mList = list;
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
        } else if (VIEW_TYPE_2 == viewType) {
            return new DmzjViewHolderType2(inflater.inflate(R.layout.item_dmzj_type2, null));
        } else if (VIEW_TYPE_ADMOB == viewType) {
            return new DmzjViewHolderTypeAd(inflater.inflate(R.layout.item_dmzj_native_ad, null));
        }
        return new FooterViewHolder(footerView);
    }




    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (position >= 2) {
            if (position < (getItemCount() - 1)) {//判断是否需要显示底部加载更多布局
                final int currentPosition = position - mHeaderViewInfos.size() + 3;
                if (currentPosition >= mList.size()) {
                    return;
                }
                DmzjBean.PostsBean bean = mList.get(currentPosition);
                if (bean != null) {
                    List<DmzjBean.Tags> tags = bean.tags;
                    if (tags != null && !tags.isEmpty()) {
                        String slug = tags.get(0).slug;
                        if (Constant.ITEM_TYPE2.equals(slug)) {
                            DmzjViewHolderType2 dmzjViewHolder = (DmzjViewHolderType2) holder;
                            initViewHolderType2(dmzjViewHolder, currentPosition);
                        }
                    }
                } else {//加载广告,需要把没一个加载过的广告放到hashmap中，然后再去取每一条广告
                    final DmzjViewHolderTypeAd dmzjViewHolder = (DmzjViewHolderTypeAd) holder;
                    String currentPositionStr = currentPosition + "";

                    NativeAd mNativeAd = nativeAdHashMap.get(currentPositionStr);
                    if (mNativeAd == null) {//实例化广告
                        synchronized ("loadAd") {
                            AdSettings.addTestDevice("1ad450e28c8b3c9376e887f7a23c5d71");
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
            } else {
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
        } else if (position == 0) {//第一个头部布局
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            if (mViewPagerManager == null) {
                mViewPagerManager = new ViewPagerManager(mContext,
                        headerViewHolder.id_viewpager, headerViewHolder.id_ll_dot,
                        headerViewHolder.id_iv_one, mBannerBeanList, mOnItemClickListener);
            }
            mViewPagerManager.initViewPager();

        } else if (position == 1) {//第二个头部布局
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
     * 实例化viewHolderType2控件中的数据
     *
     * @param dmzjViewHolder
     * @param currentPosition
     */
    private void initViewHolderType2(DmzjViewHolderType2 dmzjViewHolder, int currentPosition) {
        final DmzjBean.PostsBean bean = mList.get(currentPosition);
        if (bean != null) {
            String imgUrl = getVideoFirstFrame(bean.content);
            loadImage(imgUrl, dmzjViewHolder.id_iv_img);
            //设置信息
            dmzjViewHolder.id_tv_title.setText(Html.fromHtml(bean.title));
            dmzjViewHolder.id_tv_tag.setText(bean.categories.get(0).title);
            setOnClickListener(dmzjViewHolder.id_ll_item, bean.url, bean.thumbnail, bean.title);
        }
    }

    /***
     * 给view设置点击事件
     *
     * @param view
     * @param url
     */
    private void setOnClickListener(View view, final String url, final String imgUrl, final String title) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClickListener(url, imgUrl, title);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size() - 2 + mHeaderViewInfos.size();
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

    /***
     * 根据position获取当前显示的类型
     *
     * @param position
     * @return
     */
    private int judgmentType(int position) {
        if (mList != null && !mList.isEmpty()) {
            int currentPosition = position - mHeaderViewInfos.size() + 3;
            DmzjBean.PostsBean bean = mList.get(currentPosition);
            if (bean != null) {
                List<DmzjBean.Tags> tags = bean.tags;
                if (tags != null && !tags.isEmpty()) {
                    String slug = tags.get(0).slug;
                    if (Constant.ITEM_TYPE2.equals(slug)) {
                        return VIEW_TYPE_2;
                    }
                }
            } else {
                return VIEW_TYPE_ADMOB;
            }
        }
        return VIEW_TYPE_2;
    }

    /***
     * 初始化头部布局样式2
     *
     * @param holder
     */
    private void initHeader2ViewHolder(RecyclerView.ViewHolder holder) {
        Header2ViewHolder header2ViewHolder = (Header2ViewHolder) holder;
        DmzjBean.PostsBean bean1 = mList.get(0);
        if (bean1 != null) {
            initHeader2Content(header2ViewHolder.id_tv_text_first, header2ViewHolder.id_iv_image_first, bean1.content, bean1.title, bean1.url);
        }
        DmzjBean.PostsBean bean2 = mList.get(1);
        if (bean2 != null) {
            initHeader2Content(header2ViewHolder.id_tv_text_second, header2ViewHolder.id_iv_image_second, bean2.content, bean2.title, bean2.url);
        }
        DmzjBean.PostsBean bean3 = mList.get(2);
        if (bean3 != null) {
            initHeader2Content(header2ViewHolder.id_tv_text_third, header2ViewHolder.id_iv_image_third, bean3.content, bean3.title, bean3.url);
        }
    }

    /***
     * 初始化头部布局样式2的控件内容
     */
    private void initHeader2Content(TextView textview, ImageView imageview, String content, String title, String url) {
        String imgUrl = getVideoFirstFrame(content);
        loadImage(imgUrl, imageview);
        textview.setText(Html.fromHtml(title));
        setOnClickListener(imageview, url, imgUrl, title);
    }

    /***
     * 显示视频第一帧
     *
     * @param content
     */
    private String getVideoFirstFrame(String content) {
        try {
            Document parse = Jsoup.parse(content);
            Elements imgElements = parse.select("iframe");
            if(imgElements !=null && !imgElements.isEmpty()){
                String videoUrl = imgElements.get(0).attr("src");
                if(!TextUtils.isEmpty(videoUrl)){
                    String imgUrl1 = videoUrl.substring(videoUrl.lastIndexOf("/") + 1);
                    StringBuilder sb = new StringBuilder("https://img.youtube.com/vi/");
                    sb.append(imgUrl1);
                    sb.append("/hqdefault.jpg");
                    return sb.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void loadImage(String imgUrl, ImageView imageview) {
        if (TextUtils.isEmpty(imgUrl)) {
            return;
        }
        Glide.with(mContext)
                .load(imgUrl)
                .centerCrop()
                .placeholder(R.mipmap.img_default)
                .into(imageview);
    }

    /***
     * 添加更多
     *
     * @param list
     */
    public void addMoreItem(List<DmzjBean.PostsBean> list, int status) {
        this.mList.addAll(list);
        this.status_add_more = status;
        notifyDataSetChanged();
    }

    /***
     * 重置数据
     *
     * @param list
     */
    public void resetAdapter(List<DmzjBean.PostsBean> list) {
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
     * 根据viewType是否为footerView
     *
     * @param viewType
     * @return
     */
    private boolean isFooter(int viewType) {
        if (viewType == VIEW_TYPE_FOOTER) {
            return true;
        }
        return false;
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
