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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuCompat;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                //여기서 메타데이터 수정 이벤트
                for(int i=0;i<gridAdapter.mSelectedItems.size();i++){
                    //이건 키 값
                    Log.e("test2",Integer.toString(gridAdapter.mSelectedItems.keyAt(i)));
                    int path=gridAdapter.mSelectedItems.keyAt(i);
                    //이건 주소 값
                    Log.d("test2",imageBitmapList.get(path).getImagePath().toString());

                    ContentValues values = new ContentValues();
//                    values.put(MediaStore.Images.Media.TITLE, "ChangeName");
//                    values.put(MediaStore.Images.Media.DISPLAY_NAME, "ChangeName");
//                    values.put(MediaStore.Images.Media.DESCRIPTION, "ChangeName");
//                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
//                    values.put(MediaStore.Images.Media.DATA, imageBitmapList.get(i).getImageAbPate());
                    // Add the date meta data to ensure the image is added at the front of the gallery
//                    values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
//                    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

                    ContentResolver resolver = mContext.getContentResolver();
//                    try {
//                        resolver.insert(imageBitmapList.get(i).getImagePath(), values);
//                    } catch (Exception e) {
//
//                    }

//                    ContentValues values = new ContentValues();
//                    values.put(MediaStore.Images.Media.DISPLAY_NAME, "ChangeName.JPG");
//                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
//                    values.put(MediaStore.Images.Media.IS_PENDING, 1);
//
//                    ContentResolver resolver = mContext.getContentResolver();
//                    Uri collection = imageBitmapList.get(i).getImagePath();
//                    Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
//                    Uri item = resolver.insert(collection, values);
////                    try (ParcelFileDescriptor pfd = resolver.openFileDescriptor(item, "w", null)) {
////                        // Write data into the pending image.
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
                    try {
                        ParcelFileDescriptor pdf = resolver.openFileDescriptor(imageBitmapList.get(path).getImagePath(), "w", null);
                        //if (pdf == null) {
//
//                        } else {
////                            InputStream inputStream = getImageInputStream(tempBitmap);
////                            byte[] strToByte = imageBitmapList.get(i).getImageAbPate().getBytes();
//                            FileOutputStream fos = new FileOutputStream(pdf.getFileDescriptor());
//                            fos.write(strToByte);
//                            fos.close();
//                            pdf.close();
//                        }
                    } catch (SecurityException securityException) {
                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){

                            RecoverableSecurityException recoverableSecurityException;
                            if(securityException instanceof RecoverableSecurityException){
                                recoverableSecurityException=(RecoverableSecurityException)securityException;
                            }
                            else{

                                Log.d("test2","당했다!1");
                                throw new RuntimeException(securityException.getMessage(), securityException);
                            }
                            IntentSender intentSender = recoverableSecurityException.getUserAction().getActionIntent().getIntentSender();
                            try {
                                startIntentSenderForResult(intentSender, 1,null,0,0,0,null);
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();

                                Log.d("test2","당했다!2");
                            }
                        }
                        else{
                            throw new RuntimeException(
                                    securityException.getMessage(),securityException);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("test2","당했다!");
                    }
//
//                    // Now that we're finished, release the "pending" status, and allow other apps
//                    // to view the image.
//                    values.clear();
                    values.put(MediaStore.Images.Media.IS_PENDING, 1);
                    int update = resolver.update(imageBitmapList.get(path).getImagePath(), values, null, null);
                    Log.e("test2",Integer.toString(update));
                    values.clear();
                    values.put(MediaStore.Images.Media.TITLE, "NewName23.jpg");
                    values.put(MediaStore.Images.Media.DISPLAY_NAME, "NewName223");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "NewName223");
//                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                    values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
                    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                    values.put(MediaStore.Images.Media.DATE_MODIFIED,System.currentTimeMillis());
                    int update2=resolver.update(imageBitmapList.get(path).getImagePath(), values, null, null);
                    values.clear();
                    values.put(MediaStore.Images.Media.IS_PENDING, 0);
                    int update3=resolver.update(imageBitmapList.get(path).getImagePath(), values, null, null);

                    Log.e("test2",Integer.toString(update2));
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