package com.codegreed_devs.mylectures;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by FakeJoker on 12/08/2017.
 */

public class MyNotes_Adapter extends ArrayAdapter<MyNotes> {

    private Handler handler;
    ProgressDialog progressDialog;
    AlertDialog.Builder builder,builder_er;
    boolean connected = false;
    //File yourFile;

    public MyNotes_Adapter(Context context, int resource, List<MyNotes> objects) {
        super(context, resource, objects);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); StrictMode.setVmPolicy(builder.build());
    }


    @Override


    public View getView(final int position, View convertView, ViewGroup parent) {
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setTitle("Downloading...");
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);



        builder_er=new AlertDialog.Builder(getContext());
        builder_er.setTitle("Download failed")
                .setMessage("File was unable to complete downloading.\nplease check your internet connection")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressDialog.dismiss();

                    }
                });



        // Get the data item for this position

        MyNotes std = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notes_list, parent, false);

        }

        // Lookup view for data population

        TextView lecturer = convertView.findViewById(R.id.lecturer_name);
        TextView unit=convertView.findViewById(R.id.unit_name);
        TextView doc=convertView.findViewById(R.id.doc_name);
        TextView date=convertView.findViewById(R.id.post_date);
        ImageView imageView=convertView.findViewById(R.id.download);


        // Populate the data into the template view using the data object

        lecturer.setText(std.unit);
        unit.setText(std.lecturer);
        doc.setText(std.doc_name);
        date.setText(std.post_date);

        final String selected_file = doc.getText().toString();

        File dir = Environment.getExternalStorageDirectory();
        final File yourFile = new File(dir, "/My Lectures/my notes/"+selected_file);

        if(yourFile.exists())
        {
            imageView.setImageResource(R.drawable.view_files);


        }
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (yourFile.exists()) {
                    openfile(yourFile,getContext());
                    //Toast.makeText(getContext(), "File exists"+selected_file, Toast.LENGTH_SHORT).show();
                } else {

                    handler = new Handler();
                    //Toast.makeText(getContext(), selected_file, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "starting download...", Toast.LENGTH_SHORT).show();
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder().url("http://www.kimesh.com/mylectures/uploads/"+selected_file).build();
                            Response response = null;
                            try {
                                response = client.newCall(request).execute();
                                float file_size = response.body().contentLength();
                                BufferedInputStream inputStream = new BufferedInputStream(response.body().byteStream());
                                OutputStream stream = new FileOutputStream(Environment.getExternalStorageDirectory() + "/My Lectures/my notes/" + selected_file);
                                byte[] data = new byte[8192];
                                float total = 0;
                                int read_bytes = 0;

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        progressDialog.show();

                                    }
                                });

                                while ((read_bytes = inputStream.read(data)) != -1) {
                                    total = total + read_bytes;
                                    stream.write(data, 0, read_bytes);
                                    progressDialog.setProgress((int) ((total / file_size) * 100));
                                }

                                progressDialog.dismiss();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        builder.create().show();

                                    }
                                });


                                stream.flush();
                                stream.close();
                                response.body().close();
                                builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Download complete")
                                        .setMessage("File was downloaded successfully")
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });
                                builder.setPositiveButton("Open", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                // Create URI
                                                //File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download");
                                                //File from = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + selected_file);
                                                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/My Lectures/my notes/" + selected_file);
                                                //from.renameTo(to);

                                                //open file after downloading
                                                Uri uri;
                                                if (Build.VERSION.SDK_INT >= 24) {
                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    uri = Uri.fromFile(file);
                                                    if (file.toString().contains(".doc") || file.toString().contains(".docx")) {
                                                        // Word document
                                                        intent.setDataAndType(uri, "application/msword");
                                                    } else if(file.toString().contains(".pdf")) {
                                                        // PDF file
                                                        intent.setDataAndType(uri, "application/pdf");
                                                    }
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    getContext().startActivity(intent);
                                                    try {
                                                        getContext().startActivity(intent);
                                                    } catch (ActivityNotFoundException e) {
                                                        Toast.makeText(getContext(), "No app installed to open this type of file", Toast.LENGTH_LONG).show();
                                                    }
                                                }else{
                                                    uri = Uri.fromFile(file);

                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    // Check what kind of file you are trying to open, by comparing the url with extensions.
                                                    // When the if condition is matched, plugin sets the correct intent (mime) type,
                                                    // so Android knew what application to use to open the file
                                                    if (file.toString().contains(".doc") || file.toString().contains(".docx")) {
                                                        // Word document
                                                        intent.setDataAndType(uri, "application/msword");
                                                    } else if(file.toString().contains(".pdf")) {
                                                        // PDF file
                                                        intent.setDataAndType(uri, "application/pdf");
                                                    }  else if(file.toString().contains(".xls") || file.toString().contains(".xlsx")) {
                                                        // Excel file
                                                        intent.setDataAndType(uri, "application/vnd.ms-excel");
                                                    } else if(file.toString().contains(".zip") || file.toString().contains(".rar")) {
                                                        // WAV audio file
                                                        intent.setDataAndType(uri, "application/x-wav");
                                                    }    else if(file.toString().contains(".txt")) {
                                                        // Text file
                                                        intent.setDataAndType(uri, "text/plain");
                                                    } else {
                                                        //if you want you can also define the intent type for any other file

                                                        //additionally use else clause below, to manage other unknown extensions
                                                        //in this case, Android will show all applications installed on the device
                                                        //so you can choose which application to use
                                                        intent.setDataAndType(uri, "*/*");
                                                    }


                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    getContext().startActivity(intent);
                                                }
                                                //end of open file after download


                                            }
                                        });


                            } catch (IOException e) {
                                e.printStackTrace();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {

                                 builder_er.create().show();

                                    }
                                });

                            }


                        }

                    });

                    t.start();


                }
            }

        });





        // Return the completed view to render on screen

        return convertView;


    }
    public void openfile(File file,Context c){
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            uri = Uri.fromFile(file);
            if (file.toString().contains(".doc") || file.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if(file.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(intent);
            try {
                c.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getContext(), "No app installed to open this type of file", Toast.LENGTH_LONG).show();
            }
        }else{
            uri = Uri.fromFile(file);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            // Check what kind of file you are trying to open, by comparing the url with extensions.
            // When the if condition is matched, plugin sets the correct intent (mime) type,
            // so Android knew what application to use to open the file
            if (file.toString().contains(".doc") || file.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if(file.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            }  else if(file.toString().contains(".xls") || file.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if(file.toString().contains(".zip") || file.toString().contains(".rar")) {
                // WAV audio file
                intent.setDataAndType(uri, "application/x-wav");
            }    else if(file.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else {
                //if you want you can also define the intent type for any other file

                //additionally use else clause below, to manage other unknown extensions
                //in this case, Android will show all applications installed on the device
                //so you can choose which application to use
                intent.setDataAndType(uri, "*/*");
            }


            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        }
    }


}
