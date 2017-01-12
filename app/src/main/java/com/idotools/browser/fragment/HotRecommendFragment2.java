package com.idotools.browser.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.idotools.browser.R;
import com.idotools.browser.activity.MainActivity;
import com.idotools.browser.bean.DmzjBeanResp;
import com.idotools.browser.event.DmzjListEvent;
import com.idotools.browser.utils.ActivitySlideAnim;
import com.idotools.browser.utils.GlideUtils;
import com.idotools.utils.LogUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wuxiaojun on 17-1-11.
 */

public class HotRecommendFragment2 extends BaseFragment implements View.OnClickListener {

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

    @BindView(R.id.id_iv_image_four)
    ImageView image_four;
    @BindView(R.id.id_tv_text_four)
    TextView text_four;

    @BindView(R.id.id_iv_image_five)
    ImageView image_five;
    @BindView(R.id.id_tv_text_five)
    TextView text_five;

    @BindView(R.id.id_iv_image_six)
    ImageView image_six;
    @BindView(R.id.id_tv_text_six)
    TextView text_six;

    private Context mContext;
    private int currentPage;
    private boolean isShowAd;
    private List<DmzjBeanResp.DmzjBean> mDmzjList;

    private synchronized void bindViewData() {
        if (mDmzjList != null && !mDmzjList.isEmpty()) {
            bindList(mDmzjList);
        }
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
        LogUtils.e("imageview是否为null=" + imageView);
        imageView.setTag(R.string.string_tag_0, url);
        imageView.setTag(R.string.string_tag_1, imgUrl);
        imageView.setTag(R.string.string_tag_2, title);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_dmzj_hot_recommend, null);
        }
        ButterKnife.bind(this, view);
        LogUtils.e("img_srx 是否为空=" + image_six);

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

    public void onItemClickListener(String url, String imgUrl, String title) {
        Intent mIntent = new Intent(getActivity(), MainActivity.class);
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

    public synchronized void setDmzjList(List<DmzjBeanResp.DmzjBean> list) {
        this.mDmzjList = list;
        if (list != null && !list.isEmpty()) {
            bindViewData();
        }
    }


    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}