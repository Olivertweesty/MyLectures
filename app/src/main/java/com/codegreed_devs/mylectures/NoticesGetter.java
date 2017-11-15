package com.codegreed_devs.mylectures;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class NoticesGetter {
    public String msg_from, msg,dateposted,subject_tx;

    public NoticesGetter(JSONObject object){
        try {
            this.msg_from=object.getString("from");
            this.msg=object.getString("message");
            this.dateposted=object.getString("date");
            this.subject_tx=object.getString("subject");
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    public static ArrayList<NoticesGetter> fromJson(JSONArray jsonArray){
        ArrayList<NoticesGetter> noticesGetters = new ArrayList<NoticesGetter>();

        for (int i = 0; i <jsonArray.length(); i++) {

            try {

                noticesGetters.add(new NoticesGetter(jsonArray.getJSONObject(i)));

            } catch (JSONException e) {

                e.printStackTrace();

            }

        }
        return noticesGetters;
    }
}
