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
//    Context BubbleContext;
    int photoState=0;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_bubblingphoto);
        super.onCreate(savedInstanceState);
        folderSelectState = getIntent().getIntExtra("folderState",0);
//        BubbleContext=getApplicationContext();
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

//                Log.e("lastAB",gridAdapter.lastAbPath);
                if(gridAdapter.lastAbPath!=-1&&gridAdapter.mSelectedItems.size()==2) {
//                    Log.e("lengthAB",gridAdapter.mSelectedItems.size());
                        Uri editPath;
                        String editAbPath;

                        Uri targetPath;
                        String targetAbPath;

                        String name;
                        String targetPath_Date;
                        if(gridAdapter.lastAbPath==gridAdapter.mSelectedItems.keyAt(1)){
                            //0번째 사진이 1번째 사진 옆에 배치
                            editPath = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(0)).getImagePath();
                            editAbPath = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(0)).getImageAbPate();

                            targetPath = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(1)).getImagePath();
                            targetAbPath = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(1)).getImageAbPate();

                            name = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(1)).getImageName();
                            targetPath_Date = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(1)).getImageDate();

                        }
                        else{
                            editPath = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(1)).getImagePath();
                            editAbPath = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(1)).getImageAbPate();

                            targetPath = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(0)).getImagePath();
                            targetAbPath = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(0)).getImageAbPate();

                            name = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(0)).getImageName();
                            targetPath_Date = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(0)).getImageDate();
                        }


                        //exif 변경
                        InputStream st = null;
                        try {
                            st = getContentResolver().openInputStream(targetPath);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Log.e("Input Stream", e.toString());
                        }

                        try {
                            ExifInterface exif = new ExifInterface(editAbPath);

                            //time Formatting
                            //1번째 사진의 시간 데이터로 입력 "2020:08:05 19:15:15"
                            String format = "yyyy:MM:dd HH:mm:ss";
                            SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);
    //                    Long T = Long.parseLong(Long.toString( Long.parseLong(targetPath_Date)*1000));
                            Long tmpTargetDate = Long.parseLong(targetPath_Date) - 32400000 + 1000;
                            Long T1 = Long.parseLong(Long.toString(Long.parseLong(targetPath_Date) + 1000));
                            Long T2 = Long.parseLong(Long.toString(tmpTargetDate));
                            String dateTime = formatter.format(new Date(T1));
                            String dateTime2 = formatter.format(new Date(T2));
                            Log.d("testDate1", targetPath_Date);
                            Log.e("testDate", dateTime);

                            exif.setAttribute(ExifInterface.TAG_DATETIME_ORIGINAL, dateTime2);
                            exif.setAttribute(ExifInterface.TAG_DATETIME, dateTime);
                            exif.setAttribute(ExifInterface.TAG_DATETIME_DIGITIZED, dateTime);
                            exif.setAttribute(ExifInterface.TAG_GPS_DATESTAMP, dateTime2);

    //                    좌표 넣는거
                            String tmpLat = "null";
                            String tmpLong = "null";

                            try {
                                ExifInterface exifOrigin = new ExifInterface(targetAbPath);

                                tmpLat = exifOrigin.getAttribute(ExifInterface.TAG_GPS_LATITUDE) + "";
                                tmpLong = exifOrigin.getAttribute(ExifInterface.TAG_GPS_LONGITUDE) + "";


                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (!tmpLat.equals("null") && !tmpLong.equals("null")) {
                                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, tmpLat);
                                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, tmpLong);
                            }


                            exif.saveAttributes();

                            new SingleMediaScanner(mContext, editAbPath);

                            ///////////


                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("set attribute error", e.toString());
                        }

                        //mediastore 변경
                        ContentValues values = new ContentValues();
                        ContentResolver resolver = mContext.getContentResolver();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            values.put(MediaStore.Images.Media.IS_PENDING, 1);
                            int update = resolver.update(editPath, values, null, null);
                            values.clear();
                        }
    //                int update = resolver.update(editPath, values, null, null);
    //
    //                values.clear();

                        String title = name.substring(0, name.lastIndexOf("."));
                        String tag = name.substring(name.lastIndexOf("."));
                        Log.d("nameTitle", title + "2" + tag);
                        values.put(MediaStore.Images.Media.TITLE, title + "2" + tag);
                        values.put(MediaStore.Images.Media.DISPLAY_NAME, title + "2" + tag);
    //                values.put(MediaStore.Images.Media.DESCRIPTION, "NewNa");
    //                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                        Long tmp_targetPath_Date = Long.parseLong(targetPath_Date) / 1000 + 1;
    //                Long tmp_targetPath_Date = Long.parseLong(targetPath_Date);
                        String tmp_str_targetPath = Long.toString(tmp_targetPath_Date);
    //                values.put(MediaStore.Images.Media.DATE_ADDED, targetPath_Date);
    //                values.put(MediaStore.Images.Media.DATE_TAKEN, targetPath_Date);
    //                values.put(MediaStore.Images.Media.DATE_MODIFIED,targetPath_Date);
                        values.put(MediaStore.Images.Media.DATE_ADDED, tmp_str_targetPath);
                        values.put(MediaStore.Images.Media.DATE_TAKEN, tmp_str_targetPath);
                        values.put(MediaStore.Images.Media.DATE_MODIFIED, tmp_str_targetPath);

                        int update2 = resolver.update(editPath, values, null, null);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            values.clear();
                            values.put(MediaStore.Images.Media.IS_PENDING, 0);
                            int update3 = resolver.update(editPath, values, null, null);
                        }
                        new SingleMediaScanner(mContext, editAbPath);
    //                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, editPath));
    //                MediaScanner scanner = MediaScanner.newInstance(mContext);
    //                scanner.mediaScanning(editPath.getPath());
    //                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
    //                File file = new File(editPath.toString());
                }
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