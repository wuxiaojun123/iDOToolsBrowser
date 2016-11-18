package com.idotools.utils;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 包含设备处理方法的工具类
 *
 * @version 1.0.0 2011-10-10
 */
public class DeviceUtil {


    /**
     * 获得设备型号
     *
     * @return
     */
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    /**
     * 获得国际移动设备身份码
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    /**
     * 获得国际移动用户识别码
     *
     * @param context
     * @return
     */
    public static String getIMSI(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
    }

    /**
     * 获得设备屏幕矩形区域范围
     *
     * @param context
     * @return
     */
    public static Rect getScreenRect(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();
        return new Rect(0, 0, w, h);
    }

    /**
     * 获得系统版本
     */
    public static int getSDKVersion() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 关闭软键盘
     */
    public static void closeInputMethod(EditText editTxt, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTxt.getWindowToken(), 0);
    }

    /***
     * 获取versionName
     * @param cxt
     * @return
     */
    public static String getPackageName(Context cxt) {
        try {
            return cxt.getPackageManager().getPackageInfo(cxt.getPackageName(), 0).packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /***
     * 获取versionName
     * @param cxt
     * @return
     */
    public static String getVersionName(Context cxt) {
        try {
            return cxt.getPackageManager().getPackageInfo(cxt.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /***
     * 获取versionCode
     * @param cxt
     * @return
     */
    public static int getVersionCode(Context cxt) {
        try {
            return cxt.getPackageManager().getPackageInfo(cxt.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}



