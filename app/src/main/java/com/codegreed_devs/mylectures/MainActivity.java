package com.codegreed_devs.mylectures;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.crashlytics.android.Crashlytics;

import es.dmoral.toasty.Toasty;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    DbHelper splashdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        Toasty.Config.getInstance().apply(); // required
        TextView tag=(TextView)findViewById(R.id.tag);
        //Typeface fontSplash=Typeface.createFromAsset(getAssets(),"ace.ttf");
        Typeface fontSplash=Typeface.createFromAsset(getAssets(),"splashfont.otf");
        tag.setTypeface(fontSplash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                splashdb=new DbHelper(MainActivity.this,"",null,1);
                Cursor r=splashdb.getReadableDatabase().rawQuery("SELECT * FROM user",null);
                Intent sign=new Intent(MainActivity.this,signin.class);
                Intent splash=new Intent(MainActivity.this,Main2Activity.class);
                if (r.getCount()==0){
                    startActivity(sign);
                    finish();
                }
                else {
                    startActivity(splash);
                    finish();
                }
            }
        },3000);
    }
}
