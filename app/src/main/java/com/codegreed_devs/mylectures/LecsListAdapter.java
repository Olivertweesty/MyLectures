package com.codegreed_devs.mylectures;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by FakeJoker on 20/05/2017.
 */

public class LecsListAdapter extends CursorAdapter {
    public LecsListAdapter(Context context, Cursor c, int flags) {
        super(context, c, 0);
    }
    DbHelper dbHelper;
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        return LayoutInflater.from(context).inflate(R.layout.lecs_display_row,parent,false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        String appear = "";
        String finalapear="";
        TextView lec_time=(TextView)view.findViewById(R.id.lec_time);
        TextView lec_subject=(TextView)view.findViewById(R.id.lec_subject);
        TextView lec_venue=(TextView)view.findViewById(R.id.lec_venue);
        TextView lec_name=(TextView)view.findViewById(R.id.lec_name);
        TextView lec_tel=(TextView)view.findViewById(R.id.lec_tel);
        ImageView lec_dir=(ImageView)view.findViewById(R.id.image_dir);
        TextView lec_status=(TextView)view.findViewById(R.id.lec_status);

        final String _lec_time,_lec_subject,_lec_venue,_lec_name,_lec_tel,_lec_ccod,_lec_status;
        _lec_time=cursor.getString(2)+"-"+cursor.getString(3);
        _lec_subject=cursor.getString(4);
        _lec_venue=cursor.getString(5);
        _lec_name=cursor.getString(6);
        _lec_tel=cursor.getString(7);
        _lec_status=cursor.getString(8);
        _lec_ccod=cursor.getString(9);

        lec_time.setText(_lec_time);
        lec_subject.setText(_lec_subject);
        lec_venue.setText(_lec_venue);
        lec_name.setText(_lec_name);
        lec_tel.setText("Tel: "+_lec_tel.trim());

        if (_lec_status.equals("yes")){
            lec_status.setText("Lecture Available");
        }else {
            lec_status.setText("Lecture Not Available");
        }


        lec_dir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_lec_ccod.equals("")){
                    Toast.makeText(context.getApplicationContext(), "Location Not Loaded", Toast.LENGTH_SHORT).show();
                }else {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q="+_lec_ccod);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    context.startActivity(mapIntent);
                }


            }
        });

        lec_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_lec_tel.equals("-")){
                    Toast.makeText(context.getApplicationContext(), "Lecturer Number Not Available", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+_lec_tel));
                context.startActivity(intent);
            }
        });


    }
}
