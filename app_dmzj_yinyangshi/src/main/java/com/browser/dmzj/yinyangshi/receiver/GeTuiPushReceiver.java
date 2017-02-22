package com.browser.dmzj.yinyangshi.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.browser.dmzj.yinyangshi.R;
import com.browser.dmzj.yinyangshi.activity.DmzjActivity;
import com.idotools.utils.LogUtils;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by wuxiaojun on 17-2-21.
 */

public class GeTuiPushReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        LogUtils.e("GetuiSdkDemo onReceive() action=" + bundle.getInt("action"));

        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                // String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");
                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");
                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
                //System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));
                if (payload != null) {
                    String data = new String(payload);
                    LogUtils.e("GetuiSdkDemo receiver payload : " + data);
                    if (!TextUtils.isEmpty(data)) {
                        try {
                            JSONObject jsonObject = new JSONObject(data);
                            String url = jsonObject.getString("url");
                            String title = jsonObject.getString("title");
                            String info = jsonObject.getString("info");
                            if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(title) && !TextUtils.isEmpty(info)) {
                                //自定义通知栏消息
                                sendNotifycation(context, title, info, url);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;

            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String cid = bundle.getString("clientid");
                LogUtils.e("个推的clientID是" + cid);
                break;
            case PushConsts.GET_SDKONLINESTATE:
                boolean online = bundle.getBoolean("onlineState");
                LogUtils.e("GetuiSdkDemo online = " + online);
                break;

            default:
                break;
        }
    }

    /***
     * 通知栏通知
     */
    private void sendNotifycation(Context context, String title, String info, String url) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle(title);//标题
        mBuilder.setContentText(info);
        mBuilder.setSmallIcon(R.mipmap.icon);//小图标
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);//优先级
        mBuilder.setWhen(System.currentTimeMillis());//即时发送通知
        mBuilder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);//设置通知的声音,震动
        //启动mainactivity
        Intent mIntent = new Intent(context, DmzjActivity.class);
        mIntent.putExtra("url", url);
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_ONE_SHOT);
        //设置点击意图
        mBuilder.setContentIntent(mPendingIntent);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;//点击清除按钮和点击消息时候都会消失
        //发送通知
        notificationManager.notify(0, notification);
    }

}