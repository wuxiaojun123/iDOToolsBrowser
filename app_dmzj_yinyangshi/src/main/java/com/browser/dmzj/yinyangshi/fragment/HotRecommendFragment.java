package com.browser.dmzj.yinyangshi.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.browser.utils.GlideUtils;
import com.browser.dmzj.yinyangshi.activity.BrowserActivity;
import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.idotools.utils.LogUtils;
import com.browser.dmzj.yinyangshi.R;
import com.browser.dmzj.yinyangshi.bean.DmzjBeanResp;
import com.browser.dmzj.yinyangshi.utils.ActivitySlideAnim;
import com.browser.dmzj.yinyangshi.utils.Constant;
import com.browser.dmzj.yinyangshi.utils.DoAnalyticsManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wuxiaojun on 17-1-10.
 */

public class HotRecommendFragment extends BaseFragment implements View.OnClickListener {

    private View view;

    @BindView(R.id.id_iv_image_first)
    ImageView image_first;
    @BindView(R.id.id_tv_text_first)
    TextView text_first;

    @BindView(R.id.id_iv_image_second)
    ImageView image_second;
    @BindView(R.id.id_tv_text_second)
    TextView text_second;

    @BindView(R.id.id_iv_image_third)
    ImageView image_third;
    @BindView(R.id.id_tv_text_third)
    TextView text_third;

    @BindView(R.id.ll_four)
    LinearLayout ll_four;
    @BindView(R.id.id_iv_image_four)
    ImageView image_four;
    @BindView(R.id.id_tv_text_four)
    TextView text_four;

    //ad
    @BindView(R.id.ll_ad)
    LinearLayout ll_ad;

    @BindView(R.id.id_iv_image_five)
    ImageView image_five;
    @BindView(R.id.id_tv_text_five)
    TextView text_five;

    @BindView(R.id.id_iv_image_six)
    ImageView image_six;
    @BindView(R.id.id_tv_text_six)
    TextView text_six;

    // ad view
    @BindView(R.id.native_ad_unit)
    RelativeLayout native_ad_unit;
    @BindView(R.id.native_ad_media)
    MediaView mediaView;
    @BindView(R.id.ad_choices_container)
    LinearLayout ad_choices_container;
    @BindView(R.id.native_ad_call_to_action)
    Button native_ad_call_to_action;
    @BindView(R.id.tv_ad_title)
    TextView tv_ad_title;

    private Context mContext;
    private boolean isShowAd;
    private NativeAd mNativeAd;//ad
    private List<DmzjBeanResp.DmzjBean> mDmzjList;

    protected boolean isVisible; // 是否显示
    protected boolean isPrepared; // 是否初始化

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()){ // 当前fragment正在显示
            isVisible = true;
            onVisible();
        }else {
            isVisible = false;
            onInVisible();
        }
    }

    protected void onVisible(){
        lazyLoad();
    }

    protected void lazyLoad(){
        if(!isVisible || !isPrepared){
            return;
        }
        // 加载数据
    }

    protected void onInVisible(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        Bundle arguments = getArguments();
        if (arguments != null) {
            isShowAd = arguments.getBoolean("isShowAd");
            if (isShowAd) {
                initAd();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_dmzj_hot_recommend, null);
        }
        ButterKnife.bind(this, view);

        isPrepared = true;
        lazyLoad();

        return view;
    }


    @OnClick({R.id.id_iv_image_first, R.id.id_iv_image_second, R.id.id_iv_image_third,
            R.id.id_iv_image_four, R.id.id_iv_image_five, R.id.id_iv_image_six})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_iv_image_first:
                setOnClickListener(image_first);

                break;
            case R.id.id_iv_image_second:
                setOnClickListener(image_second);

                break;
            case R.id.id_iv_image_third:
                setOnClickListener(image_third);

                break;
            case R.id.id_iv_image_four:
                setOnClickListener(image_four);

                break;
            case R.id.id_iv_image_five:
                setOnClickListener(image_five);

                break;
            case R.id.id_iv_image_six:
                setOnClickListener(image_six);

                break;
        }
    }

    private void bindViewData() {
        if (isShowAd) {
            if (mDmzjList != null && !mDmzjList.isEmpty()) {
                DmzjBeanResp.DmzjBean dmzjBean0 = mDmzjList.get(0);
                dmzjBeanBindView(dmzjBean0, image_first, text_first);

                DmzjBeanResp.DmzjBean dmzjBean1 = mDmzjList.get(1);
                dmzjBeanBindView(dmzjBean1, image_second, text_second);

                DmzjBeanResp.DmzjBean dmzjBean2 = mDmzjList.get(2);
                dmzjBeanBindView(dmzjBean2, image_third, text_third);
                //隐藏第四个，显示facebook广告
                DmzjBeanResp.DmzjBean dmzjBean3 = mDmzjList.get(3);
                dmzjBeanBindView(dmzjBean3, image_four, text_four);

                DmzjBeanResp.DmzjBean dmzjBean4 = mDmzjList.get(4);
                dmzjBeanBindView(dmzjBean4, image_five, text_five);

                DmzjBeanResp.DmzjBean dmzjBean5 = mDmzjList.get(5);
                dmzjBeanBindView(dmzjBean5, image_six, text_six);
            }
        } else {
            if (mDmzjList != null && !mDmzjList.isEmpty()) {
                bindList(mDmzjList);
            }
        }
    }

    private void initAd() {
        mNativeAd = new NativeAd(mContext, Constant.FACEBOOK_PLACEMENT_ID_HOT_SINGLE);
        mNativeAd.loadAd(NativeAd.MediaCacheFlag.ALL);
        mNativeAd.setAdListener(new AdListener() {

            @Override
            public void onError(Ad ad, AdError adError) {
                LogUtils.e("加载失败：aderror" + adError.getErrorMessage() + "=====" + adError.getErrorCode());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // 隐藏漫画，显示广告
                image_four.setVisibility(View.GONE);
                text_four.setVisibility(View.GONE);
                ll_ad.setVisibility(View.VISIBLE);

                tv_ad_title.setText(mNativeAd.getAdTitle());
                native_ad_call_to_action.setText(mNativeAd.getAdCallToAction());
                native_ad_call_to_action.getBackground().setAlpha(20);

                mediaView.setNativeAd(mNativeAd);

                AdChoicesView adChoicesView = new AdChoicesView(mContext, mNativeAd, true);
                ad_choices_container.addView(adChoicesView);
                ad_choices_container.setAlpha(0.3f);

                // Register the Title and CTA button to listen for clicks.
                List<View> clickableViews = new ArrayList<>();
                clickableViews.add(ll_ad);
                clickableViews.add(native_ad_call_to_action);
                clickableViews.add(tv_ad_title);
                mNativeAd.registerViewForInteraction(native_ad_unit, clickableViews);
            }

            @Override
            public void onAdClicked(Ad ad) {
                DoAnalyticsManager.event(mContext, DoAnalyticsManager.DOT_KEY_MAIN_HOT_AD_CLICK);
            }
        });
    }

    private void bindList(List<DmzjBeanResp.DmzjBean> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            DmzjBeanResp.DmzjBean dmzjBean = list.get(i);
            if (i == 0) {
                dmzjBeanBindView(dmzjBean, image_first, text_first);
            } else if (i == 1) {
                dmzjBeanBindView(dmzjBean, image_second, text_second);
            } else if (i == 2) {
                dmzjBeanBindView(dmzjBean, image_third, text_third);
            } else if (i == 3) {
                dmzjBeanBindView(dmzjBean, image_four, text_four);
            } else if (i == 4) {
                dmzjBeanBindView(dmzjBean, image_five, text_five);
            } else if (i == 5) {
                dmzjBeanBindView(dmzjBean, image_six, text_six);
            }
        }
    }

    private void dmzjBeanBindView(DmzjBeanResp.DmzjBean bean, ImageView imageView, TextView textView) {
        if (bean != null) {
            if (getActivity() != null) {
                GlideUtils.loadImage(getActivity(), bean.cover, imageView);
            }
            if (textView != null)
                textView.setText(bean.title);
            setImageViewTag(imageView, bean.mobileUrl, bean.cover, bean.title);
        }
    }

    /***
     * 设置imageview 的tag
     *
     * @param imageView
     * @param url
     * @param imgUrl
     * @param title
     */
    private void setImageViewTag(ImageView imageView, String url, String imgUrl, String title) {
        if (imageView != null) {
            imageView.setTag(R.string.string_tag_0, url);
            imageView.setTag(R.string.string_tag_1, imgUrl);
            imageView.setTag(R.string.string_tag_2, title);
        }
    }

    public void onItemClickListener(String url, String imgUrl, String title) {
        Intent mIntent = new Intent(getActivity(), BrowserActivity.class);
        mIntent.putExtra("url", url);
        mIntent.putExtra("imgUrl", imgUrl);
        mIntent.putExtra("title", title);
        startActivity(mIntent);
        ActivitySlideAnim.slideInAnim(getActivity());
    }

    private void setOnClickListener(ImageView imageView) {
        String url = (String) imageView.getTag(R.string.string_tag_0);
        String imgUrl = (String) imageView.getTag(R.string.string_tag_1);
        String title = (String) imageView.getTag(R.string.string_tag_2);
        onItemClickListener(url, imgUrl, title);
    }

    public void setDmzjList(List<DmzjBeanResp.DmzjBean> list) {
        if (mDmzjList != null && !mDmzjList.isEmpty()) {
            mDmzjList.clear();
        }
        this.mDmzjList = list;
        if (list != null && !list.isEmpty()) {
            bindViewData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
