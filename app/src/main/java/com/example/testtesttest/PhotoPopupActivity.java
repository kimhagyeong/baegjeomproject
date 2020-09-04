package com.example.testtesttest;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.location.Geocoder;

import androidx.annotation.RequiresApi;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.exifinterface.media.ExifInterface;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

//allalbum에서 클릭했을 때 사진 크게 보여주고 메타데이터 보여주는 액티비티
//메타데이터는 업그레이드되면서 안보여짐..
public class PhotoPopupActivity extends AppCompatActivity {
    Geocoder geocoder;
    AlertDialog.Builder builder;
    Context mContext = this;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_pop);
        Intent img = getIntent();
        geocoder = new Geocoder(this);

        //adapter에서 클릭하고나서 intent시작됨
        String path = img.getStringExtra("path");
        String date = img.getStringExtra("date");
        final String abPath = img.getStringExtra("abPath");
        //abPath = abPath.substring(1);
        Log.e("abPath",abPath);
        Log.e("abPath",Environment.getExternalStorageDirectory().getAbsolutePath());

        Uri uriPath=Uri.parse(path);
//        uriPath=MediaStore.setRequireOriginal((uriPath));
        InputStream stream= null;
        try {
            stream = getContentResolver().openInputStream(uriPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        EditText editTime = new EditText(this);

        //사진출력
        ImageView iv = findViewById(R.id.photopop);
        File imgFile = new File(path);
        Glide.with(this)
                .load(uriPath)
                .into(iv);

        //String data = new String();
        //exif출력 작동안됨. 오픈라이브러리 가져왔음
        builder = new AlertDialog.Builder(this);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(stream);
            builder.setTitle(date)
                    .setMessage(showExif(exif))
                    .setView(editTime);
            //data = showExif(exif);
        } catch (IOException e) {
            e.printStackTrace();
        }



        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                InputStream st = null;
                try {
                    st = getContentResolver().openInputStream(uriPath);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.e("Input Stream", e.toString());
                }
                if (editTime.getParent() != null)
                    ((ViewGroup) editTime.getParent()).removeView(editTime);
                try {
                    ExifInterface exif = new ExifInterface(abPath);

                    exif.setAttribute(ExifInterface.TAG_DATETIME_ORIGINAL, editTime.getText().toString());
                    exif.setAttribute(ExifInterface.TAG_DATETIME, editTime.getText().toString());
                    exif.setAttribute(ExifInterface.TAG_DATETIME_DIGITIZED, editTime.getText().toString());
                    exif.setAttribute(ExifInterface.TAG_GPS_DATESTAMP, editTime.getText().toString());
                    exif.saveAttributes();

                    new SingleMediaScanner(mContext, abPath);
                    ///////////


                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("set attribute error", e.toString());
                }

            }
        });
    }
    private String getTagString(String tag, ExifInterface exif) {

        return (tag + " : " + exif.getAttribute(tag) + "\n");
    }

    private String showExif(ExifInterface exif) {
        String attrLATITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        String attrLATITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
        String attrLONGITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        String attrLONGITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
        List list = null;  //Geocoder 객체 + Address 객체를 통해 제공되는 주소 서비스 결과를 리턴함

        String myAttribute = "[Exif information] \n\n";
        myAttribute += getTagString(ExifInterface.TAG_DATETIME, exif);
        myAttribute += getTagString(ExifInterface.TAG_DATETIME_ORIGINAL, exif);
        myAttribute += getTagString(ExifInterface.TAG_DATETIME_DIGITIZED, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_DATESTAMP, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LATITUDE, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LONGITUDE, exif);
        myAttribute += getTagString(ExifInterface.TAG_ORIENTATION, exif);
        myAttribute += getTagString(ExifInterface.TAG_WHITE_BALANCE, exif);

        return myAttribute;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.photo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        return super.onOptionsItemSelected(item);
    }
}
