package com.example.testtesttest;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BubblingPhotoActivity extends GalleryActivity {
    AlertDialog.Builder builder;
    int photoState=0;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;    //this
        setContentView(R.layout.activity_bubblingphoto);
        super.onCreate(savedInstanceState);
        folderSelectState = getIntent().getIntExtra("folderState",0);

        BottomBar bottomBar = (BottomBar)findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab_photo);

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_Home:
                        finish();
                        break;
                    case R.id.tab_photo:

                        break;
                    case R.id.tab_next:
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                       break;
                }
            }
        });
        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if(tabId==R.id.tab_photo){
                    gridAdapter.clearSelectedItem();
                }else if(tabId==R.id.tab_next){
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
        createAndSetAdapter();
        builder = new AlertDialog.Builder(BubblingPhotoActivity.this);

        builder.setTitle("Let's go Bubbling").setMessage("사진을 선택한 사진 옆으로 이동할까요?");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                //0번째 사진이 1번째 사진 옆에 배치
                Uri path0=imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(0)).getImagePath();
                String abPath0 = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(0)).getImageAbPate();

                Uri path1=imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(1)).getImagePath();
                String abPath1 = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(1)).getImageAbPate();

                String path1_Date = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(1)).getImageDate();


                //exif 변경
                InputStream st = null;
                try {
                    st = getContentResolver().openInputStream(path1);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.e("Input Stream", e.toString());
                }


                try {
                    ExifInterface exif = new ExifInterface(abPath0);
//                    ExifInterface exifOrigin = new ExifInterface(abPath1);

//                    String Lat = getTagString(ExifInterface.TAG_GPS_LATITUDE, exifOrigin);
//                    getTagString(ExifInterface.TAG_GPS_LONGITUDE, exifOrigin);

//                    String Latitude =exifOrigin.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
//                    String Longitude = exifOrigin.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);


                    //time Formatting
                    //1번째 사진의 시간 데이터로 입력
                    String format = "yyyy:MM:dd HH:mm:ss";
                    SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.KOREA);
                    Long T = Long.parseLong(Long.toString( Long.parseLong(path1_Date)*1000));
                    String dateTime = formatter.format(new Date(T));
                    Log.d("testDate1",path1_Date);
                    Log.e("testDate", dateTime);
                    exif.setAttribute(ExifInterface.TAG_DATETIME_ORIGINAL, dateTime);
                    exif.setAttribute(ExifInterface.TAG_DATETIME, dateTime);
                    exif.setAttribute(ExifInterface.TAG_DATETIME_DIGITIZED, dateTime);
//                    exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE,exifOrigin.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
//                    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, exifOrigin.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
                    exif.saveAttributes();

                    new SingleMediaScanner(mContext, abPath0);
                    ///////////


                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("set attribute error", e.toString());
                }

                //mediastore 변경
                ContentValues values = new ContentValues();
                ContentResolver resolver = mContext.getContentResolver();

                values.put(MediaStore.Images.Media.IS_PENDING, 1);
                int update = resolver.update(path0, values, null, null);

                values.clear();
//                values.put(MediaStore.Images.Media.TITLE, "NewNa.jpg");
//                values.put(MediaStore.Images.Media.DISPLAY_NAME, "NewNa");
//                values.put(MediaStore.Images.Media.DESCRIPTION, "NewNa");
//                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                values.put(MediaStore.Images.Media.DATE_ADDED, path1_Date);
                values.put(MediaStore.Images.Media.DATE_TAKEN, path1_Date);
                values.put(MediaStore.Images.Media.DATE_MODIFIED,path1_Date);
                int update2=resolver.update(path0, values, null, null);
                values.clear();

                values.put(MediaStore.Images.Media.IS_PENDING, 0);
                int update3=resolver.update(path0, values, null, null);

                new SingleMediaScanner(mContext, abPath0);




                //여기까지!!!!


                //여기서 메타데이터 수정 이벤트
//                for(int i=0;i<gridAdapter.mSelectedItems.size();i++){
//                    //이건 키 값
//                    Log.e("test2",Integer.toString(gridAdapter.mSelectedItems.keyAt(i)));
//                    Uri path=imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(i)).getImagePath();
//                    String abPath = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(i)).getImageAbPate();
//                    //이건 주소 값
//                    Log.d("test2",path.toString());
//
//                    //exif 변경
//                    InputStream st = null;
//                    try {
//                        st = getContentResolver().openInputStream(path);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                        Log.e("Input Stream", e.toString());
//                    }
//
//                    try {
//                        ExifInterface exif = new ExifInterface(abPath);
//                        exif.setAttribute(ExifInterface.TAG_DATETIME_ORIGINAL, "2020:08:05 19:15:15");
//                        exif.setAttribute(ExifInterface.TAG_DATETIME, "2020:08:05 19:15:15");
//                        exif.setAttribute(ExifInterface.TAG_DATETIME_DIGITIZED, "2020:08:05 19:15:15");
//                        exif.saveAttributes();
//
//                        new SingleMediaScanner(mContext, abPath);
//                        ///////////
//
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        Log.e("set attribute error", e.toString());
//                    }
//
//                    //여기까지
//
//                    ContentValues values = new ContentValues();
////                    values.put(MediaStore.Images.Media.TITLE, "ChangeName");
////                    values.put(MediaStore.Images.Media.DISPLAY_NAME, "ChangeName");
////                    values.put(MediaStore.Images.Media.DESCRIPTION, "ChangeName");
////                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
////                    values.put(MediaStore.Images.Media.DATA, imageBitmapList.get(i).getImageAbPate());
//                    // Add the date meta data to ensure the image is added at the front of the gallery
////                    values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
////                    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
//
//                    ContentResolver resolver = mContext.getContentResolver();
////                    try {
////                        resolver.insert(imageBitmapList.get(i).getImagePath(), values);
////                    } catch (Exception e) {
////
////                    }
//
////                    ContentValues values = new ContentValues();
////                    values.put(MediaStore.Images.Media.DISPLAY_NAME, "ChangeName.JPG");
////                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
////                    values.put(MediaStore.Images.Media.IS_PENDING, 1);
////
////                    ContentResolver resolver = mContext.getContentResolver();
////                    Uri collection = imageBitmapList.get(i).getImagePath();
////                    Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
////                    Uri item = resolver.insert(collection, values);
//////                    try (ParcelFileDescriptor pfd = resolver.openFileDescriptor(item, "w", null)) {
//////                        // Write data into the pending image.
//////                    } catch (IOException e) {
//////                        e.printStackTrace();
////////                    }
////                    try {
////                        ParcelFileDescriptor pdf = resolver.openFileDescriptor(imageBitmapList.get(path).getImagePath(), "w", null);
////                        //if (pdf == null) {
//////
//////                        } else {
////////                            InputStream inputStream = getImageInputStream(tempBitmap);
////////                            byte[] strToByte = imageBitmapList.get(i).getImageAbPate().getBytes();
//////                            FileOutputStream fos = new FileOutputStream(pdf.getFileDescriptor());
//////                            fos.write(strToByte);
//////                            fos.close();
//////                            pdf.close();
//////                        }
////                    } catch (SecurityException securityException) {
////                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
////
////                            RecoverableSecurityException recoverableSecurityException;
////                            if(securityException instanceof RecoverableSecurityException){
////                                recoverableSecurityException=(RecoverableSecurityException)securityException;
////                            }
////                            else{
////                                throw new RuntimeException(securityException.getMessage(), securityException);
////                            }
////                            IntentSender intentSender = recoverableSecurityException.getUserAction().getActionIntent().getIntentSender();
////                            try {
////                                startIntentSenderForResult(intentSender, 1,null,0,0,0,null);
////                            } catch (IntentSender.SendIntentException e) {
////                                e.printStackTrace();
////
////                            }
////                        }
////                        else{
////                            throw new RuntimeException(
////                                    securityException.getMessage(),securityException);
////                        }
////
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////
////                    // Now that we're finished, release the "pending" status, and allow other apps
////                    // to view the image.
////                    values.clear();
//                    values.put(MediaStore.Images.Media.IS_PENDING, 1);
//                    int update = resolver.update(path, values, null, null);
//                    Log.e("test2",Integer.toString(update));
//                    values.clear();
//                    values.put(MediaStore.Images.Media.TITLE, "NewNa.jpg");
//                    values.put(MediaStore.Images.Media.DISPLAY_NAME, "NewNa");
//                    values.put(MediaStore.Images.Media.DESCRIPTION, "NewNa");
////                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
//                    values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis()/1000);
//                    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis()/1000);
//                    values.put(MediaStore.Images.Media.DATE_MODIFIED,System.currentTimeMillis()/1000);
//                    int update2=resolver.update(path, values, null, null);
//                    values.clear();
//                    values.put(MediaStore.Images.Media.IS_PENDING, 0);
//                    int update3=resolver.update(path, values, null, null);
//
//                    new SingleMediaScanner(mContext, abPath);
//
//                    Log.e("test2",Integer.toString(update2));
//                    Log.e("resultDDDD",Long.toString(System.currentTimeMillis()));
//                }
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume(){
        super.onResume();
        BottomBar bottomBar = (BottomBar)findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab_photo);
    }
}