package com.base.browser.utils;

import android.app.Activity;

import com.base.browser.R;


/**
 * activity切换动画，必须在startActivity和finish之后调用
 * Created by wuxiaojun on 16-11-3.
 */
public class ActivitySlideAnim {

    public static void slideInAnim(Activity activity) {
        //enter是activity进入动画，exit是activity出去动画 anim_slide_right_out
        activity.overridePendingTransition(R.anim.anim_slide_right_in, R.anim.anim_slide_left_out);
    }


    public static void slideOutAnim(Activity activity) {
        //anim_slide_left_out
        activity.overridePendingTransition(R.anim.anim_slide_left_in, R.anim.anim_slide_right_out);
    }

}
