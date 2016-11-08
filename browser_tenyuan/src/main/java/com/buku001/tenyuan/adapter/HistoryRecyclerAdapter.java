package com.buku001.tenyuan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.buku001.tenyuan.R;
import com.buku001.tenyuan.bean.CartoonDetailsBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wuxiaojun on 16-10-2.
 */
public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.RecyclerViewHolder> {

    private Context mContext;
    private List<CartoonDetailsBean> list;


    public HistoryRecyclerAdapter(Context context, List<CartoonDetailsBean> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_history, null);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        CartoonDetailsBean bean = list.get(position);
        if (bean != null) {
            if (bean.img != null)
                Glide.with(mContext).load(bean.img).centerCrop().crossFade().into(holder.iv_img);
            holder.tv_title.setText(bean.title);
            holder.tv_laster_chapter.setText(bean.url);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.id_ll_layout)
        public LinearLayout ll_layout;
        @BindView(R.id.id_iv_img)
        public ImageView iv_img;
        @BindView(R.id.id_tv_title)
        public TextView tv_title;
        @BindView(R.id.id_tv_laster_chapter)
        public TextView tv_laster_chapter;
        @BindView(R.id.item_delete)
        public TextView deleteTextView;
        @BindView(R.id.ll_text)
        public LinearLayout ll_text;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
