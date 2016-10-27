package com.alibaba.sdk.android;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.ta.utdid2.android.utils.NetworkUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wuxiaojun on 16-7-28.
 */
public class FeedBackConstant {

    public static String current_AppKey;

    public final static String APPKEY_BROWSER = "23478657";

    /***
     * 打开意见反馈页面
     * 采用阿里百川的意见反馈----需要传入AppKey
     *
     * @param mContext
     */
    public static void openFeedBackActivity(Context mContext) {
        //第二个参数是appkey，就是百川应用创建时候的appkey
        //FeedbackAPI.initAnnoy((Application) mContext.getApplicationContext(), current_AppKey);
        setCustomTheme();
        //判断网络状态
        if(NetworkUtils.isConnectInternet(mContext)){
            String errorMsg = FeedbackAPI.openFeedbackActivity(mContext);
            if (!TextUtils.isEmpty(errorMsg)) {
                Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(mContext, R.string.ido_netwrok_error, Toast.LENGTH_SHORT).show();
        }
    }

    /***
     * 初始化意见反馈
     *
     * @param application
     * @param key
     */
    public static void initFeedBackAnnoy(Application application, String key) {
        current_AppKey = key;
        FeedbackAPI.initAnnoy(application, current_AppKey);
    }

    /***
     * 自定义设置
     */
    public static void setCustomTheme() {
        //可以设置反馈消息自定义参数，方便在反馈后台查看自定义数据，参数是json对象，里面所有的数据都可以由开发者自定义
        FeedbackAPI.setAppExtInfo(new JSONObject());

        //可以设置UI自定义参数，如主题色等,map的key值具体为：
        //enableAudio(是否开启语音 1：开启 0：关闭)
        //bgColor(消息气泡背景色 "#ffffff")，
        //color(消息内容文字颜色 "#ffffff")，
        //avatar(当前登录账号的头像)，string，为http url
        //toAvatar(客服账号的头像),string，为http url
        //themeColor(标题栏自定义颜色 "#ffffff")
        //profilePlaceholder: (顶部联系方式)，string
        //profileTitle: （顶部联系方式左侧提示内容）, String
        //chatInputPlaceholder: (输入框里面的内容),string
        //profileUpdateTitle:(更新联系方式标题), string
        //profileUpdateDesc:(更新联系方式文字描述), string
        //profileUpdatePlaceholder:(更新联系方式), string
        //profileUpdateCancelBtnText: (取消更新), string
        //profileUpdateConfirmBtnText: (确定更新),string
        //sendBtnText: (发消息),string
        //sendBtnTextColor: ("white"),string
        //sendBtnBgColor: ('red'),string
        //hideLoginSuccess: true  隐藏登录成功的toast
        //pageTitle: （Web容器标题）, string
        Map<String, String> map = new HashMap<String, String>();
        map.put("enableAudio", "1");
        map.put("hideLoginSuccess", "true");

        FeedbackAPI.setUICustomInfo(map);
    }

}
