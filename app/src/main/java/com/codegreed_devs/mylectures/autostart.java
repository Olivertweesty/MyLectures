package com.codegreed_devs.mylectures;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by FakeJoker on 06/06/2017.
 */

public class autostart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1=new Intent(context,MyLectureService.class);
        Intent intent2=new Intent(context,FcmMessagingService.class);
        context.startService(intent1);
        context.startService(intent2);
    }
}
