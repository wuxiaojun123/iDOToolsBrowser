package com.base.browser.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.browser.R;
import com.base.browser.utils.ActivitySlideAnim;
import com.gp.utils.DeviceUtil;


/**
 * Created by wuxiaojun on 16-10-8.
 */
public class AboutActivity extends BaseActivity implements View.OnClickListener {

    ImageButton id_iv_right;
    TextView id_tv_title;
    RelativeLayout id_rl_praize;
    TextView tv_app_name_version;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initView();

        id_iv_right.setImageResource(R.mipmap.img_title_back);
        id_tv_title.setText(R.string.string_about);
        tv_app_name_version.setText(getResources().getString(R.string.app_name) + DeviceUtil.getVersionName(mContext));
    }

    private void initView() {
        id_tv_title = (TextView) findViewById(R.id.id_tv_title);
        id_iv_right = (ImageButton) findViewById(R.id.id_iv_right);
        id_rl_praize = (RelativeLayout) findViewById(R.id.id_rl_praize);
        tv_app_name_version = (TextView) findViewById(R.id.id_tv_app_name_version);

        id_iv_right.setOnClickListener(this);
        id_rl_praize.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.id_iv_right) {
            finish();
            ActivitySlideAnim.slideOutAnim(AboutActivity.this);

        } else if (id == R.id.id_rl_praize) {//给个好评
            praise();

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
