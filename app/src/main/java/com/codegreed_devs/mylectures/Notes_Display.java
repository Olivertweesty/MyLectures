package com.codegreed_devs.mylectures;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class Notes_Display extends AppCompatActivity {
    boolean connected = false;
    SwipeRefreshLayout refreshLayout;
    ProgressDialog mprogress,progressDialog;
    ListView notes;
    AlertDialog.Builder popDialog;
    MyNotes_Adapter myNotes_adapter;
    DbHelper dbHelper;
    String id_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_notes__display);

        //getting data from the sqlite database
        dbHelper=new DbHelper(this,"",null,1);
        Cursor user=dbHelper.getReadableDatabase().rawQuery("SELECT * FROM user",null);

        while (user.moveToNext()){
            id_user=user.getString(1)+"_"+user.getString(2);
        }

        //end of getting data from the sqlite database

        //creating og my lectures folder in the phone memmory

        try {
            File dir = new File(Environment.getExternalStorageDirectory() + "/My Lectures/my notes/");
            dir.mkdirs();
        }catch (RuntimeException e){

        }

        refreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipe_notes);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent my_class_intent=getIntent();
                finish();
                overridePendingTransition(0,0);
                startActivity(my_class_intent);
                overridePendingTransition(0,0);

            }
        });


        //intiializng the progress bar
        mprogress=new ProgressDialog(this);
        mprogress.setMessage("Retrieving Data...");
        mprogress.show();

        progressDialog=new ProgressDialog(Notes_Display.this);
        progressDialog.setTitle("Downloading...");
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);


        //checking internet connection
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            //getting the list view from the xml file
            notes= (ListView) findViewById(R.id.notes_list);


            //initializing the arrayadapter
            ArrayList<MyNotes> arrayoflivematches=new ArrayList<>();
            myNotes_adapter=new MyNotes_Adapter(Notes_Display.this,0,arrayoflivematches);

            AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
            RequestParams params=new RequestParams();
            params.put("id_user",id_user.toLowerCase().trim());
            asyncHttpClient.post("http://kimesh.com/mylectures/notes.php", params,new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    mprogress.dismiss();
                    Snackbar snackbar= Snackbar.make(findViewById(R.id.swipe_notes),"Server Unreachable", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Reconnect", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent my_class_intent=getIntent();
                            finish();
                            overridePendingTransition(0,0);
                            startActivity(my_class_intent);
                            overridePendingTransition(0,0);
                        }
                    });
                    snackbar.show();
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    mprogress.dismiss();
                    check_permission();

                    try {
                        JSONArray notes_array=new JSONArray(s);
                        ArrayList<MyNotes> notes_accum=MyNotes
                                .fromJson(notes_array);
                        myNotes_adapter.addAll(notes_accum);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //end of getting JSON Data
                    notes.setAdapter(myNotes_adapter);
                }
            });






        }
        else {
            //if not connected to a network
            connected = false;
            mprogress.dismiss();
            Snackbar snackbar = Snackbar.make(findViewById(R.id.swipe_notes), "No internet connection!", Snackbar.LENGTH_INDEFINITE);
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
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==100 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            File dir = new File(Environment.getExternalStorageDirectory() + "/My Lectures/my notes/");
            dir.mkdirs();

        }else {
            popDialog=new AlertDialog.Builder(Notes_Display.this);
            popDialog.setTitle("Permission")
                    .setMessage("Please note that permission is needed so that downloaded files can be saved on your device")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            check_permission();

                        }
                    });
            popDialog.create().show();



        }
    }

    private void check_permission(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
                return;
            }else {
                File dir = new File(Environment.getExternalStorageDirectory() + "/My Lectures/my notes/");
                dir.mkdirs();
            }

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
