package com.codegreed_devs.mylectures;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by FakeJoker on 12/08/2017.
 */

public class NoticesGetterAdapter extends ArrayAdapter<NoticesGetter> {
    public NoticesGetterAdapter(Context context, int resource, List<NoticesGetter> objects) {
        super(context, resource, objects);
    }
    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position

        NoticesGetter std = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notices_display, parent, false);

        }

        // Lookup view for data population

        TextView real_msg = convertView.findViewById(R.id.message);
        TextView date_posted=convertView.findViewById(R.id.date_posted);
        TextView from_txt=convertView.findViewById(R.id.from);
        TextView subject_txt=convertView.findViewById(R.id.subject_of);


        // Populate the data into the template view using the data object
        real_msg.setText(std.msg);
        date_posted.setText(std.dateposted);
        from_txt.setText("Frm: "+std.msg_from);
        subject_txt.setText("Sub: "+std.subject_tx);

        // Return the completed view to render on screen

        return convertView;

    }
}
