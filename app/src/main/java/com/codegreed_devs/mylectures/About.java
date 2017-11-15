package com.codegreed_devs.mylectures;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CardView contact_dev=(CardView)findViewById(R.id.contact_dev);

        //email sending intent
        final Intent contact = new Intent(Intent.ACTION_SEND);
        contact.setType("plain/text");
        contact.putExtra(Intent.EXTRA_EMAIL  , new String[]{"codegreeddevelopers@gmail.com"});
        contact.putExtra(Intent.EXTRA_TEXT   , "");
        contact.setPackage("com.google.android.gm");

        contact_dev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(contact);
            }
        });
    }
}
