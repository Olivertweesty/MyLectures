package com.codegreed_devs.mylectures;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class Settings extends AppCompatActivity {
    ListView settings;
    DbHelper dbHelper;
    String selected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_settings);


        //initializing the sqlite database
        dbHelper=new DbHelper(Settings.this,"",null,1);

        settings=(ListView)findViewById(R.id.settingslist);

        String[] items={"Set Reminder Time","Log Out","About"};

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);

        settings.setAdapter(adapter);
        settings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case(0):
                        remove_class_dialog();
                        break;
                    case 2:
                        Intent about=new Intent(Settings.this,About.class);
                        startActivity(about);
                        break;
                    default:
                        confirm_log_out();
                }
            }
        });
    }
    public void remove_class_dialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Time For Reminder");
        selected="10";
        String[] times={"10 Minutes Before"," 20 Minutes Before","30 Minutes Before","40 Minutes Before"," 50 Minutes Before","60 Minutes Before"};

        builder.setSingleChoiceItems(times, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case 0:
                        selected="10";
                        break;
                    case 1:
                        selected="20";
                        break;
                    case 2:
                        selected="30";
                        break;
                    case 3:
                        selected="40";
                        break;
                    case 4:
                        selected="50";
                        break;
                    default:
                        selected="60";
                }
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbHelper.getWritableDatabase().execSQL("UPDATE user SET reminder='"+selected+"' WHERE _id='1'");
                Toasty.success(Settings.this,"You will get Your reminders "+selected+" Minutes Before time",Toast.LENGTH_LONG,true).show();
            }
        });
        builder.show();
    }
    public void confirm_log_out(){
        AlertDialog.Builder confirmation=new AlertDialog.Builder(Settings.this);
        confirmation.setTitle("Confirm Logging Out");
        confirmation.setIcon(R.drawable.ic_action_log_out);
        confirmation.setCancelable(false);
        confirmation.setMessage("Are You Sure You want to Log Out?");
        confirmation.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.getReadableDatabase().delete("user",null,null);
                Intent intent=new Intent(Settings.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        confirmation.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        confirmation.create().show();
    }
    //
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
