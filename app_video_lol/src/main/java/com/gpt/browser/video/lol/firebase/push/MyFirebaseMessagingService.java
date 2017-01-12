/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *//*


package com.gpt.browser.video.lol.firebase.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.gp.browser.video.lol.R;
import com.gpt.browser.video.lol.activity.SplashActivity;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    */
/**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     *//*

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String url = null;
        String info = null;
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> map = remoteMessage.getData();
            url = map.get("url");
            info = map.get("info");
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            //{"title":"标题","content":"内容","url":"http://www.bejson.com"}
            String title = remoteMessage.getNotification().getBody();
            if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(title) && !TextUtils.isEmpty(info)) {
                //自定义通知栏消息
                sendNotifycation(getApplicationContext(), title, info, url);
            }
        }
    }

    */
/***
     * 通知栏通知
     *//*

    private void sendNotifycation(Context context, String title, String info, String url) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle(title);//标题
        mBuilder.setContentText(info);
        mBuilder.setSmallIcon(R.mipmap.img_push_notification_with_background_color);//小图标
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);//优先级
        mBuilder.setWhen(System.currentTimeMillis());//即时发送通知
        mBuilder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);//设置通知的声音,震动
        //启动mainactivity
        Intent mIntent = new Intent(context, SplashActivity.class);
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
*/
