package com.base.browser.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.browser.R;
import com.base.browser.bean.CartoonDetailsBean;
import com.bumptech.glide.Glide;

import java.util.List;

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

        public LinearLayout ll_layout;
        public ImageView iv_img;
        public TextView tv_title;
        public TextView tv_laster_chapter;
        public TextView deleteTextView;
        public LinearLayout ll_text;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            ll_layout = (LinearLayout) itemView.findViewById(R.id.id_ll_layout);
            iv_img = (ImageView) itemView.findViewById(R.id.id_iv_img);
            tv_title = (TextView) itemView.findViewById(R.id.id_tv_title);
            tv_laster_chapter = (TextView) itemView.findViewById(R.id.id_tv_laster_chapter);
            deleteTextView = (TextView) itemView.findViewById(R.id.item_delete);
            ll_text = (LinearLayout) itemView.findViewById(R.id.ll_text);
        }
    }

}
