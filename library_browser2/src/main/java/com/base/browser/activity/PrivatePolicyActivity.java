package com.base.browser.activity;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.base.browser.R;
import com.base.browser.utils.ActivitySlideAnim;

/**
 * google private policy隐私政策
 * Created by wuxiaojun on 17-2-20.
 */

public class PrivatePolicyActivity extends BaseActivity {

    ImageButton id_iv_right;
    TextView id_tv_title;
    TextView tv_private_policy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_policy);

        id_tv_title = (TextView) findViewById(R.id.id_tv_title);
        id_iv_right = (ImageButton) findViewById(R.id.id_iv_right);
        tv_private_policy = (TextView) findViewById(R.id.tv_private_policy);

        id_iv_right.setImageResource(R.mipmap.img_title_back);
        id_tv_title.setText(R.string.string_private_policy);

        /*String string_google_private_policy = getResources().getString(R.string.string_google_private_policy);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tv_private_policy.setText(Html.fromHtml(string_google_private_policy,Html.FROM_HTML_MODE_LEGACY));
        } else {
            tv_private_policy.setText(Html.fromHtml(string_google_private_policy));
        }*/


        id_iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                ActivitySlideAnim.slideOutAnim(PrivatePolicyActivity.this);
            }
        });
    }


}
