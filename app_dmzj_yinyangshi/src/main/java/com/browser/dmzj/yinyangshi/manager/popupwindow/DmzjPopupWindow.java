package com.browser.dmzj.yinyangshi.manager.popupwindow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.base.browser.activity.AboutActivity;
import com.base.browser.manager.dialog.AlertDialog;
import com.base.browser.sqlite.RecordsSqliteManager;
import com.base.browser.utils.GooglePlayUtils;
import com.base.browser.view.ImageTextViewGroup;
import com.idotools.utils.MetricsUtils;
import com.idotools.utils.MobileScreenUtils;
import com.idotools.utils.SharedPreferencesHelper;
import com.browser.dmzj.yinyangshi.R;
import com.browser.dmzj.yinyangshi.activity.DmzjActivity;
import com.browser.dmzj.yinyangshi.utils.ActivitySlideAnim;
import com.browser.dmzj.yinyangshi.utils.ActivityUtils;
import com.browser.dmzj.yinyangshi.utils.DoAnalyticsManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wuxiaojun on 16-10-2.
 */
public class DmzjPopupWindow implements View.OnClickListener {

    public static final int DURATION_START = 200;
    public static final int DURATION_END = 200;
    private Context mContext;
    public PopupWindow popupWindow;
    private View contentView;
    @BindView(R.id.id_ll_empty)
    LinearLayout id_ll_empty;
    @BindView(R.id.id_ll_content)
    LinearLayout id_ll_content;
    @BindView(R.id.id_share)
    ImageTextViewGroup id_share;
    @BindView(R.id.id_records)
    ImageTextViewGroup id_records;

    @BindView(R.id.id_add_shortcut)
    ImageTextViewGroup id_add_shortcut;
    @BindView(R.id.id_night_mode)
    ImageTextViewGroup id_night_mode;
    @BindView(R.id.id_check_update)
    ImageTextViewGroup id_check_update;
    @BindView(R.id.id_feedback)
    ImageTextViewGroup id_feedback;
    @BindView(R.id.id_about)
    ImageTextViewGroup id_about;
    @BindView(R.id.id_exit)
    ImageTextViewGroup id_exit;


    private int translationY = 0;//平移的高度
    private int bottomHeight = 0;//地步控制布局的高度
    private ObjectAnimator mContentHiddrenAnim;//退出动画
    private ObjectAnimator mHeadHiddrenAnim;
    private ObjectAnimator mContentStartAnim;//开始动画
    private ObjectAnimator mHeadStartAnim;
    //    private AutoUpdate mAutoUpdate;//检查更新
    public int isDayNightModeToogle;//是否开始夜间模式的切换动画 0 默认　１从白天到夜间模式切换  2从夜间到白天模式切换
    private boolean currentUrlExitSqlite = false;//当前url是否存在数据库
    private RecordsSqliteManager mRecordsSqliteManager;//收藏数据库管理类

    public DmzjPopupWindow(Context context) {
        this.mContext = context;
        translationY = MetricsUtils.dipToPx(180);
        bottomHeight = MetricsUtils.dipToPx(45.0f);
        init();
        startAnim();
        exitAnim();
    }

    private void init() {
        contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_home_popupwindow, null);
        ButterKnife.bind(this, contentView);
        //-emptyHeight-MobileScreenUtils.getStatusHeight(mContext)
        popupWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, MobileScreenUtils.getScreenHeight(mContext) - bottomHeight - MobileScreenUtils.getStatusHeight(mContext));
        popupWindow.setOutsideTouchable(true);//设置点击其他区域也会消失
        boolean modeNight = SharedPreferencesHelper.getInstance(mContext).getBoolean(SharedPreferencesHelper.SP_KEY_MODE_NIGHT, false);
        toogleNightMode(modeNight);
    }


    /***
     * 判断url 是否存在收藏数据库中
     *
     * @param url
     */
    private void urlIsExitSqlite(String url) {
        if (mRecordsSqliteManager == null) {
            mRecordsSqliteManager = new RecordsSqliteManager(mContext);
        }
        currentUrlExitSqlite = mRecordsSqliteManager.selectByUrl(url);
        if (currentUrlExitSqlite) {
            id_records.setImageResource(R.mipmap.img_already_records);
            id_records.setTextResource(R.string.string_alread_records);
        }
    }

    private boolean isShowing = false;

    public void showPopupWindow(View rootView) {
        if (popupWindow != null) {
            if (isShowing) {
                exitStartAnim();
                isShowing = false;
            } else {
                enterStartAnim();
                // 显示 -emptyHeight
                popupWindow.showAsDropDown(rootView, 0, 0);
                isShowing = true;
            }
        }
    }

    public void setIsShowing(){
        this.isShowing = !isShowing;
    }

    /**
     * 判断是否正在显示
     *
     * @return
     */
    public boolean isShow() {
        if (popupWindow != null) {
            return popupWindow.isShowing();
        }
        return false;
    }

    @OnClick({R.id.id_ll_empty, R.id.id_share, R.id.id_records,
            R.id.id_add_shortcut, R.id.id_night_mode, R.id.id_check_update,
            R.id.id_about, R.id.id_exit})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_ll_empty:
                exitStartAnim();

                break;
            case R.id.id_share:
                //分享
                /*ShareUtils.shareText((Activity) mContext, " " + Constant.PATH);
                DoAnalyticsManager.event(mContext, DoAnalyticsManager.DOT_KEY_SHARE_CLICK);*/

                break;
            case R.id.id_records:
                //收藏
                /*exitStartAnim();
                if (currentUrlExitSqlite) {
                    //取消收藏
                    if (!TextUtils.isEmpty(mWebViewManager.getCurrentUrl())) {
                        mRecordsSqliteManager.delete(mWebViewManager.getCurrentUrl());
                        //设置图标和图片
                        id_records.setImageResource(R.mipmap.img_records);
                        id_records.setTextResource(R.string.string_records);
                        ToastUtils.show(mContext, R.string.string_cancel_records);
                        currentUrlExitSqlite = false;
                    }
                } else {
                    recordsPage();
                }
                DoAnalyticsManager.event(mContext, DoAnalyticsManager.DOT_KEY_RECORDS_CLICK);*/

                break;
            case R.id.id_add_shortcut:
                //添加桌面快捷方式
                /*addShortcut(mWebViewManager.getCurrentTitle(), mWebViewManager.getCurrentUrl());
                DoAnalyticsManager.event(mContext, DoAnalyticsManager.DOT_KEY_SHORTCUT_CLICK);*/

                break;
            case R.id.id_night_mode:
                //夜间模式
                exitStartAnim();
                isDayNightModeToogle = 1;
                DoAnalyticsManager.event(mContext, DoAnalyticsManager.DOT_KEY_NIGHT_MODE_CLICK);

                break;
            case R.id.id_check_update:
                //检查更新
                checkUpdate();

                break;
            case R.id.id_feedback:
                //意见反馈
                //  FeedBackConstant.openFeedBackActivity(mContext);

                break;
            case R.id.id_about:
                exitStartAnim();
                mContext.startActivity(new Intent(mContext, AboutActivity.class));
                ActivitySlideAnim.slideInAnim((Activity) mContext);

                break;
            case R.id.id_exit:
                //退出
                exitApp();

                break;
        }
    }

    /***
     * 更新
     */
    private void checkUpdate() {
        try {
            GooglePlayUtils.openGooglePlayByPkg(mContext.getApplicationContext(), mContext.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 夜间模式切换
     *
     * @param isNightMode
     */
    public void toogleNightMode(boolean isNightMode) {
        if (isNightMode) { // 夜间模式
            id_night_mode.setImageResource(R.mipmap.img_day_normal);
            id_night_mode.setTextResource(R.string.string_night_day_mode);
            id_night_mode.setTag("night");

            id_night_mode.setBackgroundResource(R.drawable.selector_bg_popupwindow_night);
            id_share.setBackgroundResource(R.drawable.selector_bg_popupwindow_night);
            id_records.setBackgroundResource(R.drawable.selector_bg_popupwindow_night);
            id_add_shortcut.setBackgroundResource(R.drawable.selector_bg_popupwindow_night);
            id_check_update.setBackgroundResource(R.drawable.selector_bg_popupwindow_night);
            id_feedback.setBackgroundResource(R.drawable.selector_bg_popupwindow_night);
            id_about.setBackgroundResource(R.drawable.selector_bg_popupwindow_night);
            id_exit.setBackgroundResource(R.drawable.selector_bg_popupwindow_night);
        } else { // 白天模式
            id_night_mode.setImageResource(R.mipmap.img_night_normal);
            id_night_mode.setTextResource(R.string.string_night_mode);
            id_night_mode.setTag("day");

            id_night_mode.setBackgroundResource(R.drawable.selector_bg_popupwindow);
            id_share.setBackgroundResource(R.drawable.selector_bg_popupwindow);
            id_records.setBackgroundResource(R.drawable.selector_bg_popupwindow);
            id_add_shortcut.setBackgroundResource(R.drawable.selector_bg_popupwindow);
            id_check_update.setBackgroundResource(R.drawable.selector_bg_popupwindow);
            id_feedback.setBackgroundResource(R.drawable.selector_bg_popupwindow);
            id_about.setBackgroundResource(R.drawable.selector_bg_popupwindow);
            id_exit.setBackgroundResource(R.drawable.selector_bg_popupwindow);
        }
    }

    /***
     * 退出
     */
    private void exitApp() {
        new AlertDialog((Activity) mContext).builder()
                .setMsg(R.string.string_confirm_exit_app)
                .setPositiveButton(R.string.string_confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        exitStartAnim();
                        ActivityUtils.finishAll();
                    }
                }).setNegativeButton(R.string.string_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).show();
    }

    /**
     * 开始退出动画
     */
    public void exitStartAnim() {
        mContentHiddrenAnim.start();
        mHeadHiddrenAnim.start();
        setIsShowing();
    }

    /***
     * 开始进入动画
     */
    private void enterStartAnim() {
        if (popupWindow != null && !popupWindow.isShowing()) {
            mContentStartAnim.start();
            mHeadStartAnim.start();
        }
    }

    /***
     * popupwindow进入动画
     */
    public void startAnim() {
        if (mContentStartAnim == null && mHeadStartAnim == null) {
            mContentStartAnim = ObjectAnimator.ofFloat(id_ll_content, "translationY",
                    translationY, 0);
            mHeadStartAnim = ObjectAnimator.ofFloat(id_ll_empty, "alpha", 0f, 1f);
            mContentStartAnim.setDuration(DURATION_START);
            mHeadStartAnim.setDuration(500);
        }
    }

    /***
     * popupwindow退出动画
     */
    public void exitAnim() {
        if (mContentHiddrenAnim == null && mHeadHiddrenAnim == null) {
            mContentHiddrenAnim = ObjectAnimator.ofFloat(id_ll_content,
                    "translationY", 0, translationY);
            mHeadHiddrenAnim = ObjectAnimator.ofFloat(id_ll_empty, "alpha", 1f, 0f);
            mHeadHiddrenAnim.setDuration(DURATION_END);
            mContentHiddrenAnim.setDuration(DURATION_END);
            mContentHiddrenAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    // 等待动画执行完毕关闭popupwindow
                    popupWindow.dismiss();
                    if (isDayNightModeToogle > 0) {
                        String nightModeTag = (String) id_night_mode.getTag();
                        if("night".equals(nightModeTag)){
                            ((DmzjActivity) mContext).startDayNightModeToogleAnim(true);
                        }else{
                            ((DmzjActivity) mContext).startDayNightModeToogleAnim(false);
                        }
                    }
                }
            });
        }
    }


}
