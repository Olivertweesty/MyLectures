package com.codegreed_devs.mylectures;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

public class lectures extends AppCompatActivity {
    String day_of_week;
    LecsListAdapter lecsListAdapter;
    DbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectures);

        dbHelper=new DbHelper(this,"",null,1);

        Bundle _day=getIntent().getExtras();
        day_of_week=_day.getString("day");

        Cursor c =dbHelper.getReadableDatabase().rawQuery("SELECT * FROM lectures WHERE day_of_week='"+day_of_week.trim()+"'",null);

        lecsListAdapter=new LecsListAdapter(this,c,0);

        ListView lecs_list=(ListView)findViewById(R.id.lecslist);
        lecs_list.setAdapter(lecsListAdapter);


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
