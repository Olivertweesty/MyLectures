package com.codegreed_devs.mylectures;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Augustine on 9/27/2017.
 */

public class MyNotes {
    public String unit;
    public String lecturer;
    public String doc_name;
    public String post_date;

    public MyNotes(JSONObject object){
        try {
            this.unit=object.getString("unit_name");
            this.lecturer=object.getString("lecturer_name");
            this.doc_name=object.getString("doc_name");
            this.post_date=object.getString("postdate");
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    public static ArrayList<MyNotes> fromJson(JSONArray jsonArray){
        ArrayList<MyNotes> notes_down = new ArrayList<>();

        for (int i = 0; i <jsonArray.length(); i++) {

            try {

                notes_down.add(new MyNotes(jsonArray.getJSONObject(i)));


            } catch (JSONException e) {

                e.printStackTrace();

            }

        }
        return notes_down;
    }
}

