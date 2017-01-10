package com.base.browser.manager.popupwindow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.base.browser.R;
import com.base.browser.activity.AboutActivity;
import com.base.browser.activity.HistoryActivity;
import com.base.browser.activity.MainActivity;
import com.base.browser.manager.dialog.AlertDialog;
import com.base.browser.manager.webview.WebViewManager;
import com.base.browser.utils.ActivitySlideAnim;
import com.base.browser.utils.ShareUtils;
import com.base.browser.utils.ShortCutUtils;
import com.base.browser.view.ImageTextViewGroup;
import com.ido.autoupdate.AutoUpdate;
import com.idotools.utils.MetricsUtils;
import com.idotools.utils.MobileScreenUtils;


/**
 * Created by wuxiaojun on 16-10-2.
 */
public class MainPopupWindow implements View.OnClickListener {

    public static final int DURATION_START = 200;
    public static final int DURATION_END = 200;
    private Context mContext;
    private PopupWindow pw;
    private View contentView;
    LinearLayout id_ll_empty;
    LinearLayout id_ll_content;
    ImageTextViewGroup id_share;
    ImageTextViewGroup id_history;
    ImageTextViewGroup id_add_shortcut;
    ImageTextViewGroup id_night_mode;
    ImageTextViewGroup id_check_update;
    ImageTextViewGroup id_feedback;
    ImageTextViewGroup id_about;
    ImageTextViewGroup id_exit;
    //平移的高度
    private int translationY = 0;
    //地步控制布局的高度
    private int bottomHeight = 0;
    //退出动画
    private ObjectAnimator mContentHiddrenAnim;
    private ObjectAnimator mHeadHiddrenAnim;
    //开始动画
    private ObjectAnimator mContentStartAnim;
    private ObjectAnimator mHeadStartAnim;
    //检查更新
    private AutoUpdate mAutoUpdate;
    //是否开始夜间模式的切换动画 0 默认　１从白天到夜间模式切换  2从夜间到白天模式切换
    public int isDayNightModeToogle;
    //获取当前webview的标题和url
    private WebViewManager mWebViewManager;
    //创建快捷方式打开的第一个activity的名称
    private String className;

    public MainPopupWindow(Context context,String className) {
        this.mContext = context;
        this.className = className;
        translationY = MetricsUtils.dipToPx(180);
        bottomHeight = MetricsUtils.dipToPx(45.0f);
        init();
        startAnim();
        exitAnim();
    }

    private void init() {
        contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_home_popupwindow, null);
        initView(contentView);
        //-emptyHeight-MobileScreenUtils.getStatusHeight(mContext)
        pw = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, MobileScreenUtils.getScreenHeight(mContext) - bottomHeight - MobileScreenUtils.getStatusHeight(mContext));
        pw.setOutsideTouchable(true);//设置点击其他区域也会消失
    }

    /**
     * 初始化view和点击事件
     * @param contentView
     */
    private void initView(View contentView) {
        id_ll_empty = (LinearLayout) contentView.findViewById(R.id.id_ll_empty);
        id_ll_content = (LinearLayout) contentView.findViewById(R.id.id_ll_content);
        id_share = (ImageTextViewGroup) contentView.findViewById(R.id.id_share);
        id_history = (ImageTextViewGroup) contentView.findViewById(R.id.id_history);
        id_add_shortcut = (ImageTextViewGroup) contentView.findViewById(R.id.id_add_shortcut);
        id_night_mode = (ImageTextViewGroup) contentView.findViewById(R.id.id_night_mode);
        id_check_update = (ImageTextViewGroup) contentView.findViewById(R.id.id_check_update);
        id_feedback = (ImageTextViewGroup) contentView.findViewById(R.id.id_feedback);
        id_about = (ImageTextViewGroup) contentView.findViewById(R.id.id_about);
        id_exit = (ImageTextViewGroup) contentView.findViewById(R.id.id_exit);

        id_ll_empty.setOnClickListener(this);
        id_share.setOnClickListener(this);
        id_history.setOnClickListener(this);
        id_add_shortcut.setOnClickListener(this);
        id_check_update.setOnClickListener(this);
        id_night_mode.setOnClickListener(this);
        id_about.setOnClickListener(this);
        id_exit.setOnClickListener(this);
        id_feedback.setOnClickListener(this);
    }

    /**
     * 显示popupwindow窗口
     * @param rootView
     */
    public void showPopupWindow(View rootView) {
        if (pw != null) {
            enterStartAnim();
            // 显示 -emptyHeight
            pw.showAsDropDown(rootView, 0, 0);
        }
    }

    /**
     * 判断是否正在显示
     *
     * @return
     */
    public boolean isShow() {
        if (pw != null) {
            return pw.isShowing();
        }
        return false;
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.id_ll_empty) {
            exitStartAnim();

        } else if (id == R.id.id_share) {//分享
            exitStartAnim();
            if (mWebViewManager == null) {
                mWebViewManager = ((MainActivity) mContext).getWebViewManager();
            }
            ShareUtils.shareText((MainActivity) mContext, mWebViewManager.getCurrentTitle() + mWebViewManager.getCurrentUrl());

        } else if (id == R.id.id_history) {//历史记录
            exitStartAnim();
            Intent mIntent = new Intent(((MainActivity) mContext), HistoryActivity.class);
            mIntent.putExtra("className",className);
            ((MainActivity) mContext).startActivity(mIntent);
            ActivitySlideAnim.slideInAnim((MainActivity) mContext);


        } else if (id == R.id.id_add_shortcut) {//添加桌面快捷方式
            if (mWebViewManager == null) {
                mWebViewManager = ((MainActivity) mContext).getWebViewManager();
            }
            addShortcut(mWebViewManager.getCurrentTitle(), mWebViewManager.getCurrentUrl());

        } else if (id == R.id.id_check_update) {//检查更新
            checkUpdate();

        } else if (id == R.id.id_about) {
            exitStartAnim();
            mContext.startActivity(new Intent(mContext, AboutActivity.class));
            ActivitySlideAnim.slideInAnim((MainActivity) mContext);

        } else if (id == R.id.id_exit) {//退出
            exitApp();

        }
    }


    /***
     * 更新
     */
    private void checkUpdate() {
        if (mAutoUpdate == null)
            mAutoUpdate = new AutoUpdate();
        mAutoUpdate.forceUpdate((MainActivity) mContext);
    }

    /***
     * 添加桌面快捷方式
     * 获取当前页面的网址
     */
    protected void addShortcut(final String title, final String url) {
        new AlertDialog((MainActivity) mContext).builder().setTitle(R.string.string_prompt)
                .setMsg(R.string.string_confirm_add_shortcut)
                .setPositiveButton(R.string.string_confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //确定添加桌面快捷方式
                        Intent mIntent = new Intent();
                        mIntent.setClassName(mContext,className);
                        mIntent.putExtra("url", url);
                        ShortCutUtils.addShortCut(mContext, title, R.mipmap.icon, mIntent);
                    }
                }).setNegativeButton(R.string.string_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).show();
    }

    /***
     * 退出
     */
    private void exitApp() {
        new AlertDialog((MainActivity) mContext).builder().setTitle(R.string.string_prompt)
                .setMsg(R.string.string_confirm_exit_app)
                .setPositiveButton(R.string.string_confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        exitStartAnim();

//                        System.exit(0);
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
    }

    /***
     * 开始进入动画
     */
    private void enterStartAnim() {
        mContentStartAnim.start();
        mHeadStartAnim.start();
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
                    pw.dismiss();
                    if (isDayNightModeToogle > 0) {
                        ((MainActivity) mContext).startDayNightModeToogleAnim();
                    }
                }
            });
        }
    }


}
