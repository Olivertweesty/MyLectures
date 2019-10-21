package com.codegreed_devs.mylectures;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class signin extends AppCompatActivity {
    DbHelper dbHelper;
    EditText reg_no,lvl_study,s_pin,your_class;
    String[] days_array={"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
    Button signinbtn;
    String regno,lvl_stu,sub_reg_no;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        Typeface hd=Typeface.createFromAsset(getAssets(), "splashfont.otf");

        TextView head=(TextView)findViewById(R.id.header);
        head.setTypeface(hd);

        progressDialog=new ProgressDialog(this);

        progressDialog.setMessage("Validating User data");

        reg_no=(EditText)findViewById(R.id.tname);
        lvl_study=(EditText)findViewById(R.id.department);

        reg_no.setTypeface(hd);
        lvl_study.setTypeface(hd);

        dbHelper=new DbHelper(this,"",null,1);
        Cursor c=dbHelper.getReadableDatabase().rawQuery("SELECT * FROM tbldays",null);

        if (c.getCount()==0){
            dbHelper.insert_days_mtd(days_array);
        }

        //dbHelper.insert_lec("18:01","12:00","CSD 101: Introduction to Computers","SCC 100","Mr. Georfrey Kagombe","0724487464","Wednesday");



        signinbtn=(Button)findViewById(R.id.signin);

        signinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regno=reg_no.getText().toString().trim();
                lvl_stu=lvl_study.getText().toString().trim().replace('.','_');

                if (regno.isEmpty() || lvl_stu.isEmpty()){
                    Toast.makeText(signin.this, "Please fill in all the Fields", Toast.LENGTH_SHORT).show();
                }else {
                    try {
                        sub_reg_no=regno.substring(0,regno.indexOf('-'));
                        String id_user=sub_reg_no+"_"+lvl_stu.replace('.','_');
                        //Toast.makeText(signin.this, id_user, Toast.LENGTH_SHORT).show();
                        check_online(id_user);
                        progressDialog.show();

                    }catch (StringIndexOutOfBoundsException e){
                        Toast.makeText(signin.this, "Please Enter A Valid Registration Number", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });


    }
    public void check_online(final String id_user){
        AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
        RequestParams params=new RequestParams();
        params.put("class_id",id_user);
        asyncHttpClient.post(Constatnts.server+"/check_available", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                //Toast.makeText(signin.this, "Server Unreachable", Toast.LENGTH_SHORT).show();
                Toasty.error(signin.this, "Server Unreachable", Toast.LENGTH_SHORT, true).show();
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                if (s.trim().equals("true")){
                    dbHelper.insert_user(sub_reg_no,lvl_stu);
                    Intent into=new Intent(signin.this,Main2Activity.class);
                    startActivity(into);
                    finish();
                    progressDialog.dismiss();
                }else {
                    //Toast.makeText(signin.this, "Your Class Does Not Exist", Toast.LENGTH_SHORT).show();
                    Toasty.info(signin.this, "Your Class Does Not Exist", Toast.LENGTH_SHORT, true).show();
                    progressDialog.dismiss();
                }
            }
        });

    }
}
