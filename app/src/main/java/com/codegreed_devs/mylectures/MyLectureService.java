package com.codegreed_devs.mylectures;



import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;


/**
 * Created by FakeJoker on 05/06/2017.
 */

public class MyLectureService extends Service {
    DbHelper dbHelper;
    Handler handler;
    Runnable runnable;
    String time,classname,connection,rem,trem;
    Cursor cursor,freminder;
    int count;
    Intent intent;
    PendingIntent pendingIntent;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //initializing sqlitedatabase
        dbHelper=new DbHelper(this,"",null,1);


        handler=new Handler();
        final int delay=30000;

        //start of the post delayed
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                conection_status();
                lesson_time_notify();
                runnable=this;
                handler.postDelayed(runnable,delay);
            }
        },delay);


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void lesson_time_notify(){

        //getting the time for notifications
        Calendar calendar= Calendar.getInstance();
        int hr24=calendar.get(Calendar.HOUR_OF_DAY);
        int min=calendar.get(Calendar.MINUTE);

        //getting current day
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        if(hr24<10 && min<10){
            time="0"+hr24+":"+"0"+min;
        }else if (hr24>=10 && min<10){
            time=hr24+":"+"0"+min;
        }else if (hr24<10 && min>=10){
            time="0"+hr24+":"+min;
        }else {
            time=hr24+":"+min;
        }



        cursor=dbHelper.getReadableDatabase().rawQuery("SELECT * FROM lectures WHERE day_of_week='"+dayOfTheWeek+"'",null);
        try {
            freminder=dbHelper.getReadableDatabase().rawQuery("SELECT * FROM user",null);

            while (freminder.moveToNext()){
                trem=freminder.getString(3);
            }

        }catch (SQLException e){
            trem="10";
        }


           while (cursor.moveToNext()){
               //
               //
               String remind_time=check_remind(cursor.getString(2).trim(),trem);

               if (time.trim().equals(cursor.getString(2).trim())&& cursor.getString(8).equals("yes")){
                   //getting any reminder Available
                   classname=cursor.getString(5);
                   rem=cursor.getString(4)+"\n By:"+cursor.getString(6);
                   notification_method(classname,rem);
               }else if (time.trim().equals(remind_time.trim())&&cursor.getString(8).equals("yes")){
                   classname=cursor.getString(5);
                   rem=cursor.getString(4)+"\n By:"+cursor.getString(6)+"\n" +
                           "( This is A Reminder)";
                   rem_notification_method(classname,rem);
               }
           }

    }
    public void notification_method(String classname, String reminder){
        intent=new Intent(MyLectureService.this,Main2Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent= PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri alarmSound = Uri.parse("android.resource://com.codegreed_devs.mylectures/"+R.raw.noti2);

        final String msg = "You Have a Lecture in "+classname+".\n"+reminder;

        final Notification.Builder builder = new Notification.Builder(this);
        builder.setStyle(new Notification.BigTextStyle(builder)
                .bigText(msg)
                .setBigContentTitle("My Lectures")
                .setSummaryText("Wish you all the Best"))
                .setContentTitle("My Lectures")
                .setContentText("Lecture at "+classname+" Currently")
                .setVibrate(new long[] { 1000,1000,1000, 1000,3000 })
                .setSound(alarmSound)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.splash);


        final NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, builder.build());

    }
    public void rem_notification_method(String classname, String reminder){
        intent=new Intent(MyLectureService.this,Main2Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent= PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri alarmSound = Uri.parse("android.resource://com.codegreed_devs.mylectures/"+R.raw.noti2);

        final String msg = "You Have a Lecture at "+classname+" in "+trem+" minutes time.\n"+reminder;

        final Notification.Builder builder = new Notification.Builder(this);
        builder.setStyle(new Notification.BigTextStyle(builder)
                .bigText(msg)
                .setBigContentTitle("My Lectures")
                .setSummaryText("Make sure You attend in time"))
                .setContentTitle("My Lectures")
                .setContentText("Lecture at "+classname+" in "+trem+" minutes time")
                .setVibrate(new long[] { 1000,1000,1000, 1000,3000 })
                .setSound(alarmSound)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.splash);


        final NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, builder.build());

    }


   public void conection_status(){
       ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
       if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
               connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
           //we are connected to a network
           connection ="true";
       }else {
           connection="false";
       }
   }
   public String check_remind(String real_time,String rem_time){
       String[] splitted_time=real_time.split(":");
       String final_time="";
       int remi=Integer.parseInt(rem_time);
       int hrs=Integer.parseInt(splitted_time[0]);
       int mins=Integer.parseInt(splitted_time[1]);

       if (remi>mins){
           hrs=hrs-1;
           mins=(60+mins)-remi;
       }else {
           mins=mins-remi;
       }

       if (mins<10 && hrs<10){
           final_time="0"+hrs+":"+"0"+mins;
       }else if (mins<10){
           final_time=hrs+":"+"0"+mins;
       }else if (hrs<10){
           final_time="0"+hrs+":"+mins;
       }else {
           final_time=hrs+":"+mins;
       }
       return final_time;
   }


}

