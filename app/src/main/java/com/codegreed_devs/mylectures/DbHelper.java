package com.codegreed_devs.mylectures;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.TextView;

/**
 * Created by FakeJoker on 18/05/2017.
 */

public class DbHelper extends SQLiteOpenHelper {
    public static final String usertbl="CREATE TABLE user(_id TEXT,reg_num TEXT, lvl_study DOUBLE,reminder TEXT);";
    public static final String daystbl="CREATE TABLE tbldays(_id INTEGER AUTO INCREMENT,day_of_week TEXT);";
    public static final String lecturestbl="CREATE TABLE lectures(_id INTEGER AUTO INCREMENT,day_of_week TEXT,str_time TEXT,st_time TEXT,subject TEXT,venue TEXT,lecturer TEXT,tel TEXT,status TEXT,coodnates TEXT)";
    public static final String usertbld="DROP TABLE IF EXISTS user;";
    public static final String daystbld="DROP TABLE IF EXISTS tbldays;";
    public static final String lecturestbld="DROP TABLE IF EXISTS lectures;";


    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "mylectures.db", factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL(usertbl);
      db.execSQL(daystbl);
        db.execSQL(lecturestbl);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(usertbld);
        db.execSQL(daystbld);
        db.execSQL(lecturestbld);
        onCreate(db);
    }
    public void insert_days_mtd(String[] days){
        for (int i=0;i<days.length;i++){
            insert_day(days[i]);
        }
    }

    public void insert_day(String day){
        ContentValues wkday=new ContentValues();
        wkday.put("day_of_week",day);
        this.getWritableDatabase().insertOrThrow("tbldays","",wkday);
    }
    public void insert_user(String reg_no,String lvl_stu){
        ContentValues userdata=new ContentValues();
        userdata.put("_id","1");
        userdata.put("reg_num",reg_no);
        userdata.put("lvl_study",lvl_stu);
        userdata.put("reminder","10");
        this.getWritableDatabase().insert("user","",userdata);
    }
    public void insert_lec(String str_time, String st_time,String subject,String venue,String lecturer,String tel,String day_of_week,String status,String coodnates){
        ContentValues lect_data=new ContentValues();
        lect_data.put("day_of_week",day_of_week);
        lect_data.put("str_time",str_time);
        lect_data.put("st_time",st_time);
        lect_data.put("subject",subject);
        lect_data.put("venue",venue);
        lect_data.put("lecturer",lecturer);
        lect_data.put("tel",tel);
        lect_data.put("status",status);
        lect_data.put("coodnates",coodnates);
        this.getWritableDatabase().insert("lectures","",lect_data);
    }


}
