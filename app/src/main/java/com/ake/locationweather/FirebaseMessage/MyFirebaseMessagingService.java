package com.ake.locationweather.FirebaseMessage;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import com.ake.locationweather.MainActivity;
import com.ake.locationweather.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by 유현석 on 2017-04-08.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        String body = notification.getBody();
        String title = notification.getTitle();

        if(TextUtils.isEmpty(body)) {
            return;
        }

        showNotification(title, body);
    }

    private void showNotification(String title, String contents) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.mipmap.ic_launcher);

        if (TextUtils.isEmpty(title)) {
            builder.setContentTitle("기본 타이틀");
        } else {
            builder.setContentTitle(title);
        }

        builder.setContentText(contents);

        // 노티를 클릭했을 때 노티를 자동으로 날려준다..
        builder.setAutoCancel(true);

        // 노티피케이션을 클릭했을 때 실행할 인텐트
        Intent onClickNotificationIntent = new Intent(getApplicationContext(), MainActivity.class);
//                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        // Intent.FLAG_ACTIVITY_CLEAR_TOP : 시작할 액티비티가 기존에 실행이 되어있다면, 새 액티비티를 띄우지 않는다.
        // Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK : 기존에 존재하는 Activity 태스크를 비우고, 새 태스크를 만든다.
        PendingIntent contentPendingIntent = PendingIntent.getActivity(getApplicationContext(), 5, onClickNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // notification 을 클릭했을 때 수행할 펜딩 인텐트를 지정
        builder.setContentIntent(contentPendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }
}
