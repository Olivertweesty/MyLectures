package com.codegreed_devs.mylectures;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by FakeJoker on 15/08/2017.
 */

public class FcmMessagingService extends FirebaseMessagingService {
    Intent intent;
    PendingIntent pendingIntent;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title=remoteMessage.getNotification().getTitle();
        String message=remoteMessage.getNotification().getBody();
//        String sound=remoteMessage.getNotification().getSound();


            intent=new Intent(FcmMessagingService.this,Main2Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent=PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_ONE_SHOT);


        long[] vibrate = new long[] { 100, 100, (long) 10000 };



        NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
        builder.setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setVibrate(vibrate)
                .setSound(Uri.parse("noti2"))
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,builder.build());

    }
}
