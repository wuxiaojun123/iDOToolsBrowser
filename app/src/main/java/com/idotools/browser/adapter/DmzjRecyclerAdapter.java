package com.idotools.browser.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.idotools.browser.R;
import com.idotools.browser.bean.BannerResp;
import com.idotools.browser.bean.DmzjBean;
import com.idotools.browser.minterface.OnItemClickListener;
import com.idotools.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wuxiaojun on 16-11-9.
 */
public class DmzjRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //头部view集合
    private ArrayList<FixedViewInfo> mHeaderViewInfos = new ArrayList<>();
    //表示当前view类型为正常viewType
    private static final int VIEW_TYPE_NORMAL = 10000;
    //表示当前view类型是footerView
    private static final int VIEW_TYPE_FOOTER = 10001;
    public static final int LOAD_MORE_LOADING = 0;//正在加载
    public static final int LOAD_MORE_COMPILE = 1;//加载完成
    public static final int LOAD_MORE_NO = 2;//没有更多
    private int footerCount = 1;//底部加载更多的个数
    //加载更多布局
    private View footerView;
    //加载更多状态
    private int status_add_more;
    public List<DmzjBean> mList;
    public Context mContext;

    public DmzjRecyclerAdapter(Context context, List<DmzjBean> list) {
        mHeaderViewInfos.add(new FixedViewInfo(LayoutInflater.from(context).inflate(R.layout.item_dmzj_header, null), 0));
        mHeaderViewInfos.add(new FixedViewInfo(LayoutInflater.from(context).inflate(R.layout.item_dmzj_header2, null), 1));
        this.mContext = context;
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
        } else if (isFooter(viewType)) {
            return new FooterViewHolder(footerView);
        }
        return new DmzjViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_dmzj, null));
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof DmzjViewHolder) {
            //设置图片
            DmzjViewHolder dmzjViewHolder = (DmzjViewHolder) holder;
            int currentPosition = position - mHeaderViewInfos.size() + 6;
            if (currentPosition >= mList.size()) {
                return;
            }
            final DmzjBean bean = mList.get(currentPosition);
            if (bean != null) {
                //设置图片 android:background="@mipmap/img_default"
                glideLoadImg(bean.cover, dmzjViewHolder.id_iv_img);
                //设置信息
                dmzjViewHolder.id_tv_title.setText(bean.title);
                dmzjViewHolder.id_tv_tag.setText("分类:" + bean.tags);
                dmzjViewHolder.id_tv_description.setText("简介:" + bean.description);
                dmzjViewHolder.id_ll_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClickListener(bean.url);
                        }
                    }
                });
            }

        } else if (holder instanceof HeaderViewHolder) {
            //第一个头部布局
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            viewPager = headerViewHolder.id_viewpager;
            dotView = headerViewHolder.id_ll_dot;
            id_iv_one = headerViewHolder.id_iv_one;
            initViewPager();

        } else if (holder instanceof Header2ViewHolder) {
            //第二个头部布局
            if (mList == null || mList.isEmpty()) {
                return;
            }
            Header2ViewHolder header2ViewHolder = (Header2ViewHolder) holder;
            DmzjBean bean1 = mList.get(0);
            if (bean1 != null) {
                glideLoadImg(bean1.cover, header2ViewHolder.id_iv_image_first);
                header2ViewHolder.id_tv_text_first.setText(bean1.title);
                setOnClickListener(header2ViewHolder.id_iv_image_first, bean1.url);
            }

            DmzjBean bean2 = mList.get(1);
            glideLoadImg(bean2.cover, header2ViewHolder.id_iv_image_second);
            header2ViewHolder.id_tv_text_second.setText(bean2.title);
            setOnClickListener(header2ViewHolder.id_iv_image_second, bean2.url);

            DmzjBean bean3 = mList.get(2);
            glideLoadImg(bean3.cover, header2ViewHolder.id_iv_image_third);
            header2ViewHolder.id_tv_text_third.setText(bean3.title);
            setOnClickListener(header2ViewHolder.id_iv_image_third, bean3.url);

            DmzjBean bean4 = mList.get(3);
            glideLoadImg(bean4.cover, header2ViewHolder.id_iv_image_four);
            header2ViewHolder.id_tv_text_four.setText(bean4.title);
            setOnClickListener(header2ViewHolder.id_iv_image_four, bean4.url);

            DmzjBean bean5 = mList.get(4);
            glideLoadImg(bean5.cover, header2ViewHolder.id_iv_image_five);
            header2ViewHolder.id_tv_text_five.setText(bean5.title);
            setOnClickListener(header2ViewHolder.id_iv_image_five, bean5.url);

            DmzjBean bean6 = mList.get(5);
            glideLoadImg(bean6.cover, header2ViewHolder.id_iv_image_six);
            header2ViewHolder.id_tv_text_six.setText(bean6.title);
            setOnClickListener(header2ViewHolder.id_iv_image_six, bean6.url);

        } else if (holder instanceof FooterViewHolder) {
            //加载更多
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            switch (status_add_more) {
                case LOAD_MORE_LOADING:
                    footerViewHolder.id_ll_footer.setVisibility(View.VISIBLE);
                    break;
                case LOAD_MORE_COMPILE:
                    footerViewHolder.id_ll_footer.setVisibility(View.GONE);
                    break;
                case LOAD_MORE_NO:
                    footerViewHolder.id_ll_footer.setVisibility(View.GONE);
                    break;
            }
        }
    }

    /***
     * 给view设置点击事件
     *
     * @param imageView
     * @param url
     */
    private void setOnClickListener(ImageView imageView, final String url) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClickListener(url);
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
        return mHeaderViewInfos.size() + mList.size() + footerCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderPosition(position)) {
            return mHeaderViewInfos.get(position).viewType;
        } else if (isFooterPosition(position)) {
            return VIEW_TYPE_FOOTER;
        }
        return VIEW_TYPE_NORMAL;
    }

    private int currentPosition;
    private List<ImageView> mImageViewList;
    private ImageView[] indicators;// 小圆点数组
    public List<BannerResp.BannerBean> mBannerBeanList;
    private ViewPager viewPager;
    private LinearLayout dotView;
    private ImageView id_iv_one;//只有一张图片的情况，显示

    private void initViewPager() {
        if (mBannerBeanList == null || mBannerBeanList.isEmpty()) {
            return;
        }
        final int size = mBannerBeanList.size();
        if (size == 1) {
            if (id_iv_one.getVisibility() == View.VISIBLE) {
                return;
            }
            id_iv_one.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
            //只需要显示imageview一张图片即可
            glideLoadImg(mBannerBeanList.get(0).iconPath, id_iv_one);
            setOnClickListener(id_iv_one, mBannerBeanList.get(0).h5url);
        } else {
            if (viewPager.getAdapter() != null) {
                return;
            }
            mImageViewList = new ArrayList<ImageView>();
            indicators = new ImageView[mBannerBeanList.size()];

            for (int i = 0; i < size; i++) {
                ImageView iv = new ImageView(mContext);
                glideLoadImg(mBannerBeanList.get(i).iconPath, iv);
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                mImageViewList.add(iv);

                ImageView ivIndicator = new ImageView(mContext);
                if (i != 0) {
                    ivIndicator.setImageResource(R.mipmap.choose_false);
                } else {
                    ivIndicator.setImageResource(R.mipmap.choose_true);
                }
                ivIndicator.setPadding(0, 0, 20, 0);
                dotView.addView(ivIndicator);
                indicators[i] = ivIndicator;
            }

            // 如果图片集合等于2，那么添加两倍图片,防止左滑动出现空白页，自行优化是一张图片的时候就不让左右滑动和轮播
            if (size == 2) {
                for (int i = size; i < size * 2; i++) {
                    ImageView iv = new ImageView(mContext);
                    glideLoadImg(mBannerBeanList.get(i % size).iconPath, iv);
                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                    mImageViewList.add(iv);
                }
            }

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int arg0) {
                    //设置小圆点
                    setCurrentIndicator(arg0 % size);
                    currentPosition = arg0;
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                }
            });
            BannerPageAdapter adapter = new BannerPageAdapter(mImageViewList, mImageViewList.size());
            viewPager.setAdapter(adapter);
            if (size >= 2) {
                viewPager.setCurrentItem(5000);
                mHandler.sendEmptyMessageDelayed(1, 3000);
            }
            if (mImageViewList != null && !mImageViewList.isEmpty()) {
                for (int i = 0; i < mImageViewList.size(); i++) {
                    setOnClickListener(mImageViewList.get(i), mBannerBeanList.get(i % size).h5url);
                }
            }
        }
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                currentPosition++;
                viewPager.setCurrentItem(currentPosition);
            }
            mHandler.sendEmptyMessageDelayed(1, 3000);
        }
    };

    /***
     * 设置选中小圆点
     */
    protected void setCurrentIndicator(int currentPosition) {
        for (int i = 0; i < indicators.length; i++) {
            if (i != currentPosition) {
                indicators[i].setImageResource(R.mipmap.choose_false);
            } else {
                indicators[i].setImageResource(R.mipmap.choose_true);
            }
        }
    }

    /***
     * 创建头部viewHolder
     */
    protected class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.id_viewpager)
        ViewPager id_viewpager;
        @BindView(R.id.id_ll_dot)
        LinearLayout id_ll_dot;
        @BindView(R.id.id_iv_one)
        ImageView id_iv_one;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /***
     * 第二个头部viewHolder
     */
    protected class Header2ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.id_iv_image_first)
        ImageView id_iv_image_first;
        @BindView(R.id.id_tv_text_first)
        TextView id_tv_text_first;

        @BindView(R.id.id_iv_image_second)
        ImageView id_iv_image_second;
        @BindView(R.id.id_tv_text_second)
        TextView id_tv_text_second;

        @BindView(R.id.id_iv_image_third)
        ImageView id_iv_image_third;
        @BindView(R.id.id_tv_text_third)
        TextView id_tv_text_third;

        @BindView(R.id.id_iv_image_four)
        ImageView id_iv_image_four;
        @BindView(R.id.id_tv_text_four)
        TextView id_tv_text_four;

        @BindView(R.id.id_iv_image_five)
        ImageView id_iv_image_five;
        @BindView(R.id.id_tv_text_five)
        TextView id_tv_text_five;

        @BindView(R.id.id_iv_image_six)
        ImageView id_iv_image_six;
        @BindView(R.id.id_tv_text_six)
        TextView id_tv_text_six;


        public Header2ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    /***
     * 动漫之家的viewHolder
     */
    protected class DmzjViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.id_iv_img)
        ImageView id_iv_img;
        @BindView(R.id.id_tv_title)
        TextView id_tv_title;
        @BindView(R.id.id_tv_tag)
        TextView id_tv_tag;
        @BindView(R.id.id_tv_description)
        TextView id_tv_description;
        @BindView(R.id.id_ll_item)
        LinearLayout id_ll_item;

        public DmzjViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /***
     * 尾部加载更多
     */
    protected class FooterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.id_ll_footer)
        LinearLayout id_ll_footer;
        @BindView(R.id.id_tv_tips)
        TextView id_tv_tips;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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
        if (status_add_more == LOAD_MORE_NO) {
            //如果没有更多数据，则不显示底部布局
            footerCount = 0;
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
        if (position > (mHeaderViewInfos.size() + mList.size() - 1)) {
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
