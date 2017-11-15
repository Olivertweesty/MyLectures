package com.codegreed_devs.mylectures;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.IntegerRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class Main2Activity extends AppCompatActivity {
    DbHelper dbHelper;
    DaysListAdapter daysListAdapter;
    ProgressDialog progressDialog;
    String token,id_user;
    ListView listView;
    String appserver_url="http://kimesh.com/mylectures/appusers.php";
    SwipeRefreshLayout refreshLayout;
    Boolean fabIsOpen=false;
    FloatingActionButton fab,fab_map,fab_notific,fab_docs;
    Animation fabOpn,fabClosd;
    private boolean doubleBackToExitPressedOnce = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        refreshLayout=(SwipeRefreshLayout)findViewById(R.id.contentmain_activity);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent my_class_intent=getIntent();
                finish();
                overridePendingTransition(0,0);
                startActivity(my_class_intent);
                overridePendingTransition(0,0);
                MediaPlayer ring= MediaPlayer.create(Main2Activity.this,R.raw.refreshed);
                ring.start();
            }
        });



        dbHelper=new DbHelper(this,"",null,1);
        Cursor user=dbHelper.getReadableDatabase().rawQuery("SELECT * FROM user",null);

        while (user.moveToNext()){
            id_user=user.getString(1)+"_"+user.getString(2);
        }

        listView=(ListView)findViewById(R.id.days_of_week);
        //Toast.makeText(this, id_user, Toast.LENGTH_SHORT).show();

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading Data From Server...");
        if (conection_status()){
            progressDialog.show();
            load_online_data(id_user);
        }else {
            getting_lesson();
        }

        progressDialog.setCancelable(false);


        //starting the background service;
        Intent intent=new Intent(this,MyLectureService.class);
        Intent intent1=new Intent(this,FcmMessagingService.class);
        startService(intent1);
        startService(intent);
        //end of starting service
        //initializing fab animations
        fabOpn= AnimationUtils.loadAnimation(Main2Activity.this,R.anim.fab_opn);
        fabClosd=AnimationUtils.loadAnimation(Main2Activity.this,R.anim.fab_closd);

        //initializing fab
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab_docs=(FloatingActionButton)findViewById(R.id.fab_docs);
        fab_notific = (FloatingActionButton) findViewById(R.id.fab_notific);
        fab_notific.setImageResource(R.drawable.ic_notifications);
        fab_map = (FloatingActionButton) findViewById(R.id.fab_map);
        fab_map.setImageResource(R.drawable.ic_map);
        fab_docs.setImageResource(R.drawable.ic_description_white_24dp);
        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!fabIsOpen){
                    fabIsOpen=true;

                    fab_map.startAnimation(fabOpn);
                    fab_map.setClickable(true);

                    fab_notific.startAnimation(fabOpn);
                    fab_notific.setClickable(true);

                    fab_docs.startAnimation(fabOpn);
                    fab_docs.setClickable(true);

                    fab.setImageResource(R.drawable.ic_close_white);
                    //Toasty.info(Main2Activity.this,"Fab Opened",Toast.LENGTH_LONG,true).show();
                }else {
                    animation_close();
                    //Toasty.error(Main2Activity.this,"Fab Closed",Toast.LENGTH_LONG,true).show();
                }
            }
        });
        fab_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animation_close();
                Intent mapsP=new Intent(Main2Activity.this,MapsActivity.class);
                startActivity(mapsP);
            }
        });
        fab_notific.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animation_close();
                Intent notices_open=new Intent(Main2Activity.this,Notices.class);
                startActivity(notices_open);
            }
        });
        fab_docs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animation_close();
                Intent intent2=new Intent(Main2Activity.this,Notes_Display.class);
                startActivity(intent2);
            }
        });




        //initializing the cursor adapter to get days from the database
        Cursor c=dbHelper.getReadableDatabase().rawQuery("SELECT * FROM tbldays WHERE EXISTS (SELECT day_of_week FROM lectures WHERE  day_of_week = tbldays.day_of_week)",null);

        daysListAdapter=new DaysListAdapter(this,c,0);



        listView.setAdapter(daysListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cur=(Cursor)daysListAdapter.getItem(i);
                cur.moveToPosition(i);
                String day=cur.getString(1);
                //parsing class code to the next activity
                Bundle bundle=new Bundle();
                bundle.putString("day",day);
                Intent class_stds=new Intent(Main2Activity.this,lectures.class);
                class_stds.putExtras(bundle);
                startActivity(class_stds);
               // Toast.makeText(Main2Activity.this, day, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (fabIsOpen) {
            animation_close();
        } else {
            if (doubleBackToExitPressedOnce) {
                this.doubleBackToExitPressedOnce = false;
                Toast.makeText(this,"Please click BACK again to exit.", Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id==R.id.action_share){
            Intent share=new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT,"Stay Updated With your lecture times, locations Around School and Notes from Lecturers Using This free Android app https://play.google.com/store/apps/details?id=com.codegreed_devs.mylectures");
            startActivity(Intent.createChooser(share,"Share Using"));
        }else if (id==R.id.action_rate){
            Uri playstore = Uri.parse("https://play.google.com/store/apps/details?id=com.codegreed_devs.mylectures");
            Intent store = new Intent(Intent.ACTION_VIEW, playstore);
            store.setPackage("com.android.vending");

            try {
                startActivity(store);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.codegreed_devs.mylectures")));

            }
        }
        else if (id==R.id.action_setting){
            Intent intentst=new Intent(Main2Activity.this,Settings.class);
            startActivity(intentst);
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendtoken() {

        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        token=sharedPreferences.getString(getString(R.string.FCM_TOKEN),"");
        if (token.isEmpty()){
            token= FirebaseInstanceId.getInstance().getToken();
        }
        //Toast.makeText(this, token, Toast.LENGTH_SHORT).show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, appserver_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<String,String>();
                params.put("fcm_token",token);
                params.put("id_user",id_user);
                return params;
            }
        };
        MySingleton.getmInstance(Main2Activity.this).addToRequestque(stringRequest);

    }
    public Boolean conection_status(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }else {
            return false;
        }
    }

    public void load_online_data(String id_use){
        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
        RequestParams params=new RequestParams();
        params.put("id_user",id_use.toLowerCase());
        asyncHttpClient.post("http://www.kimesh.com/mylectures/load_lessons.php",params,new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                //Toast.makeText(Main2Activity.this, "Server Unreachable", Toast.LENGTH_SHORT).show();
                Toasty.error(Main2Activity.this,"Server Unreachable",Toast.LENGTH_LONG,true).show();
                progressDialog.dismiss();
                getting_lesson();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {

                dbHelper.getReadableDatabase().delete("lectures",null,null);
                sendtoken();
                try {
                    JSONArray jsonArray=new JSONArray(s);
                    for (int r=0;r<jsonArray.length();r++){
                        JSONObject lec=jsonArray.getJSONObject(r);
                        String day_of_week=lec.getString("day_of_week");
                        String start_time=lec.getString("str_time");
                        String stop_time=lec.getString("st_time");
                        String lec_sub=lec.getString("subject");
                        String venue=lec.getString("venue");
                        String lec_name=lec.getString("lec_name");
                        String lec_tel=lec.getString("lec_tel");
                        String status=lec.getString("status");
                        String ccodnates=lec.getString("coodinates");
                        try {
                            dbHelper.insert_lec(start_time,stop_time,lec_sub,venue,lec_name,lec_tel,day_of_week,status,ccodnates);
                        }catch (SQLException e){
                            //Toast.makeText(Main2Activity.this, ""+e, Toast.LENGTH_SHORT).show();
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Cursor c_new=dbHelper.getReadableDatabase().rawQuery("SELECT * FROM tbldays WHERE EXISTS (SELECT day_of_week FROM lectures WHERE  day_of_week = tbldays.day_of_week)",null);
                daysListAdapter.swapCursor(c_new);
                listView.setAdapter(daysListAdapter);
                progressDialog.dismiss();
                getting_lesson();

            }
        });

    }

    public void animation_close(){
        fabIsOpen=false;

        fab_map.startAnimation(fabClosd);
        fab_map.setClickable(false);

        fab_notific.startAnimation(fabClosd);
        fab_notific.setClickable(false);

        fab_docs.startAnimation(fabClosd);
        fab_docs.setClickable(false);

        fab.setImageResource(R.drawable.ic_add);
    }
    public void alert_dg(String curr,String nxt){

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE \tdd MMMM, yyyy");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        //end of getting the date to display

        LayoutInflater inflater=Main2Activity.this.getLayoutInflater();
        View view=inflater.inflate(R.layout.dialog_start,null);

        //getting all the textview from the layout
        TextView lec_start1=(TextView)view.findViewById(R.id.lec_start);
        TextView lec_start2=(TextView)view.findViewById(R.id.lec_start_2);
        TextView lec_end1=(TextView)view.findViewById(R.id.lec_end);
        TextView lec_end2=(TextView)view.findViewById(R.id.lec_end_2);
        TextView lec_sub1=(TextView)view.findViewById(R.id.lec_sub);
        TextView lec_sub2=(TextView)view.findViewById(R.id.lec_sub_2);
        TextView lec_name1=(TextView)view.findViewById(R.id.lec_name);
        TextView lec_name2=(TextView)view.findViewById(R.id.lec_name_2);
        TextView lec_loc1=(TextView)view.findViewById(R.id.lec_loc);
        TextView lec_loc2=(TextView)view.findViewById(R.id.lec_loc_2);

        //end of getting all the listview
        String[] currentarr=curr.split("#");
        String[] nexrarr=nxt.split("#");
        //start of setting names
        lec_start1.append(currentarr[0]);
        lec_end1.append(currentarr[1]);
        lec_sub1.setText(currentarr[2]);
        lec_name1.append(currentarr[4]);
        lec_loc1.append(currentarr[3]);

        lec_start2.append(nexrarr[0]);
        lec_end2.append(nexrarr[1]);
        lec_sub2.setText(nexrarr[2]);
        lec_name2.append(nexrarr[4]);
        lec_loc2.append(nexrarr[3]);


        //Toast.makeText(this, ""+nxt, Toast.LENGTH_SHORT).show();

        AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
        alertDialog.setView(view);
        alertDialog.setTitle(dayOfTheWeek)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
        alertDialog.create().show();

    }

    public void getting_lesson() {
        String current_lec="-#-#NOT AVAILABLE#-#-",nxt_lec="-#-#NOT AVAILABLE#-#-";

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String _dayOfTheWeek = sdf.format(d).trim();
        //end of getting dates

        //getting the time for notifications
        Calendar calendar = Calendar.getInstance();
        int hr24 = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int total_cur = (hr24 * 60) + min;
        //end of getting time in 24hours system

        Cursor current = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM lectures WHERE day_of_week='" + _dayOfTheWeek + "'", null);

        int x=0;
        int present = current.getCount();
        while (current.moveToNext()) {
            String[] start = current.getString(2).split(":");
            String[] stop = current.getString(3).split(":");
            int start_hr = Integer.parseInt(start[0]);
            int stop_hr = Integer.parseInt(stop[0]);
            int start_min = Integer.parseInt(start[1]);
            int stop_min = Integer.parseInt(stop[1]);
            int total_stat = (start_hr * 60) + start_min;
            int total_stop = (stop_hr * 60) + stop_min;

            if (total_cur >= total_stat && total_cur <= total_stop) {
                current_lec=current.getString(2)+"#"+current.getString(3)+"#"+current.getString(4)+"#"+current.getString(5)+"#"+current.getString(6);
                x = current.getPosition()+1;
                break;
            }else if (total_stat>total_cur){
                x=present+1;
                nxt_lec=current.getString(2)+"#"+current.getString(3)+"#"+current.getString(4)+"#"+current.getString(5)+"#"+current.getString(6);
                break;
            }
        }
            if (x==0){

            }
            else if (x<present){
                current.moveToPosition(x);
                nxt_lec=current.getString(2)+"#"+current.getString(3)+"#"+current.getString(4)+"#"+current.getString(5)+"#"+current.getString(6);
            }

            /*if (current_lec.equals("")){
                current_lec=;
            }else if (nxt_lec.equals("")){
                nxt_lec="-/-/NOT AVAILABLE/-/-";
            }else if(current_lec.equals("")&&nxt_lec.equals("")){
                nxt_lec="-/-/NOT AVAILABLE/-/-";
                current_lec="-/-/NOT AVAILABLE/-/-";
            }*/

            String no_ava="-#-#NOT AVAILABLE#-#-";
            if (current_lec.equals(no_ava) && nxt_lec.equals(no_ava)){

            }else {
                alert_dg(current_lec,nxt_lec);
            }



    }

}
