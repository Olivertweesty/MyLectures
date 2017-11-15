package com.codegreed_devs.mylectures;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class Notices extends AppCompatActivity {

    SwipeRefreshLayout refreshLayout;
    ProgressDialog mprogress;
    ListView noticeslist;
    NoticesGetterAdapter noticesGetterAdapter;
    DbHelper dbHelper;
    String id_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_notices);
        //start of swipe refreshing
        refreshLayout=(SwipeRefreshLayout)findViewById(R.id.notices_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MediaPlayer ring= MediaPlayer.create(Notices.this,R.raw.refreshed);
                ring.start();
                Intent my_class_intent=getIntent();
                finish();
                overridePendingTransition(0,0);
                startActivity(my_class_intent);
                overridePendingTransition(0,0);


            }
        });
        //end of refresshing layout
        //getting data from the sqlite database
        dbHelper=new DbHelper(this,"",null,1);
        Cursor user=dbHelper.getReadableDatabase().rawQuery("SELECT * FROM user",null);

        while (user.moveToNext()){
            id_user=user.getString(1)+"_"+user.getString(2);
        }

        //end of getting data from the sqlite database
        //intiializng the progress bar
        mprogress=new ProgressDialog(this);
        mprogress.setMessage("Retrieving Notices...");
        mprogress.show();
        //end of initializing progress bar

        if (conection_status()) {


            //getting the list view from the xml file
            noticeslist = (ListView) findViewById(R.id.notices_display_list);

            //initializing the arrayadapter
            ArrayList<NoticesGetter> arrayofmessages = new ArrayList<>();
            noticesGetterAdapter = new NoticesGetterAdapter(this, 0, arrayofmessages);

            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            RequestParams params=new RequestParams();
            params.put("id_user",id_user.toLowerCase().trim());
            asyncHttpClient.post("http://www.kimesh.com/mylectures/check_notices.php", params,new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    mprogress.dismiss();
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.notices_layout), "Server Unreachable", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Reconnect", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent my_class_intent = getIntent();
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(my_class_intent);
                            overridePendingTransition(0, 0);
                        }
                    });
                    snackbar.show();
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    mprogress.dismiss();
                    try {
                        JSONArray messages = new JSONArray(s);
                        ArrayList<NoticesGetter> notice_list = NoticesGetter.fromJson(messages);
                        noticesGetterAdapter.addAll(notice_list);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //end of getting JSON Data


                }
            });
            noticeslist.setAdapter(noticesGetterAdapter);
        }else {

            //if no internet connection
            mprogress.dismiss();
            Snackbar snackbar = Snackbar.make(findViewById(R.id.notices_layout), "No internet connection!", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Reconnect", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent my_class_intent = getIntent();
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(my_class_intent);
                    overridePendingTransition(0, 0);
                }
            });
            snackbar.show();
        }
        // end of the if check

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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
