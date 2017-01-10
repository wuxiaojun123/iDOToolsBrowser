package com.idotools.browser.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.idotools.browser.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wuxiaojun on 17-1-10.
 */

public class HotRecommendFragment extends BaseFragment implements View.OnClickListener{

    private View view;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.fragment_dmzj_hot_recommend,null);
        }
        ButterKnife.bind(this,view);

        return view;
    }


    @OnClick({R.id.id_iv_image_first,R.id.id_iv_image_second,R.id.id_iv_image_third,
            R.id.id_iv_image_four,R.id.id_iv_image_five,R.id.id_iv_image_six})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.id_iv_image_first:

                break;
            case R.id.id_iv_image_second:

                break;
            case R.id.id_iv_image_third:

                break;
            case R.id.id_iv_image_four:

                break;
            case R.id.id_iv_image_five:

                break;
            case R.id.id_iv_image_six:

                break;
        }
    }


}
