package com.codegreed_devs.mylectures;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by FakeJoker on 20/05/2017.
 */

public class DaysListAdapter extends CursorAdapter {
    public DaysListAdapter(Context context, Cursor c, int flags) {
        super(context, c, 0);
    }
    DbHelper dbHelper;
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        return LayoutInflater.from(context).inflate(R.layout.days_display_row,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String appear="l";
        TextView classt=(TextView)view.findViewById(R.id.classt);
        TextView subjectt=(TextView)view.findViewById(R.id.subjectt);
        TextView lsn_count=(TextView)view.findViewById(R.id.lessons_count);
        String clst=cursor.getString(1);

        dbHelper=new DbHelper(context.getApplicationContext(),"",null,1);
        Cursor oli =dbHelper.getReadableDatabase().rawQuery("SELECT * FROM lectures WHERE day_of_week='"+clst.trim()+"'",null);
        int count=oli.getCount();

        while (oli.moveToNext()){
            appear=appear+oli.getString(2)+"-"+oli.getString(3)+"\t\t\t Lec: "+oli.getString(6)+"\n";
        }

        Typeface maintxt= Typeface.createFromAsset(context.getAssets(),"splashfont.otf");
        if (count>1){
            lsn_count.setText(count+" Lectures");
        }else {
            lsn_count.setText(count+" Lecture");
        }
        subjectt.setText(appear.substring(1,appear.length()-1));
        classt.setText(clst);


    }
}
