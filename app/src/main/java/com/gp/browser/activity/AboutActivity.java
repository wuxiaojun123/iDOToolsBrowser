package com.gp.browser.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gp.browser.R;
import com.gp.browser.utils.ActivitySlideAnim;
import com.gp.utils.DeviceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wuxiaojun on 16-10-8.
 */
public class AboutActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.id_iv_right)
    ImageButton id_iv_right;
    @BindView(R.id.id_tv_title)
    TextView id_tv_title;
    @BindView(R.id.id_rl_praize)
    RelativeLayout id_rl_praize;
    @BindView(R.id.id_tv_app_name_version)
    TextView tv_app_name_version;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        id_iv_right.setImageResource(R.mipmap.img_title_back);
        id_tv_title.setText(R.string.string_about);
        tv_app_name_version.setText(getResources().getString(R.string.app_name)+ DeviceUtil.getVersionName(mContext));
    }

    @OnClick({R.id.id_iv_right, R.id.id_rl_praize})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_iv_right:
                finish();
                ActivitySlideAnim.slideOutAnim(AboutActivity.this);
                break;
            case R.id.id_rl_praize:
                //给个好评
                praise();
                break;
        }
    }

    private void praise() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + getPackageName()));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
            startActivity(intent);
        }
    }


}
