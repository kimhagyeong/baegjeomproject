package com.example.testtesttest;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static android.os.Environment.getDataDirectory;
import static android.os.Environment.getExternalStoragePublicDirectory;

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
                        String date;
                        String targetName;
                        String targetPath_Date;

                        Boolean isTakenCamera=false;

                        if(gridAdapter.lastAbPath==gridAdapter.mSelectedItems.keyAt(1)){
                            //0번째 사진이 1번째 사진 옆에 배치
                            editPath = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(0)).getImagePath();
                            editAbPath = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(0)).getImageAbPate();

                            targetPath = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(1)).getImagePath();
                            targetAbPath = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(1)).getImageAbPate();

                            name=imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(0)).getImageName();
                            targetName = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(1)).getImageName();

                            date=imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(0)).getImageDate();
                            targetPath_Date = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(1)).getImageDate();

                        }
                        else{
                            editPath = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(1)).getImagePath();
                            editAbPath = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(1)).getImageAbPate();

                            targetPath = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(0)).getImagePath();
                            targetAbPath = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(0)).getImageAbPate();

                            name=imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(1)).getImageName();
                            targetName = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(0)).getImageName();

                            date=imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(1)).getImageDate();
                            targetPath_Date = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(0)).getImageDate();
                        }


                        //exif 변경
                        InputStream st = null;
                        try {
                            st = getContentResolver().openInputStream(editPath);
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

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                                exif.setAttribute(ExifInterface.TAG_DATETIME_ORIGINAL, dateTime);
                                exif.setAttribute(ExifInterface.TAG_DATETIME, dateTime);
                                exif.setAttribute(ExifInterface.TAG_DATETIME_DIGITIZED, dateTime);
                                exif.setAttribute(ExifInterface.TAG_GPS_DATESTAMP, dateTime);

                            }
                            else{
                            exif.setAttribute(ExifInterface.TAG_DATETIME_ORIGINAL, dateTime2);
                            exif.setAttribute(ExifInterface.TAG_DATETIME, dateTime);
                            exif.setAttribute(ExifInterface.TAG_DATETIME_DIGITIZED, dateTime);
                            exif.setAttribute(ExifInterface.TAG_GPS_DATESTAMP, dateTime2);
                            }
    //                    좌표 넣는거
                            String tmpLat = "null";
                            String tmpLong = "null";
                            try {
                                ExifInterface exifOrigin = new ExifInterface(targetAbPath);

                                tmpLat = exifOrigin.getAttribute(ExifInterface.TAG_GPS_LATITUDE) + "";
                                tmpLong = exifOrigin.getAttribute(ExifInterface.TAG_GPS_LONGITUDE) + "";

//                                String tmpCamera=exifOrigin.getAttribute(ExifInterface.TAG_CAMERA_OWNER_NAME)+"";
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                    String tmpCamera=exifOrigin.getAttribute(ExifInterface.TAG_CAMERA_OWNER_NAME)+"";

                                    if(!tmpCamera.equals("null")){
                                        isTakenCamera=true;
                                        Toast.makeText(getApplicationContext(),"안드로이드 버전 10 이하는 제공하지 않는 서비스입니다.\n다른 이미지를 선택해주세요", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (Exception e) {
                               Log.e("RealBad..","잘못햇어여어ㅠ");
                                e.printStackTrace();
                            }

                            if (!tmpLat.equals("null") && !tmpLong.equals("null")) {
                                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, tmpLat);
                                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, tmpLong);
                            }


                            exif.saveAttributes();
                            new SingleMediaScanner(mContext, editAbPath);

                        } catch (Exception e) {
                            e.printStackTrace();
                            isTakenCamera=true;
                            Log.e("ExifException", e.toString());
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                Toast.makeText(getApplicationContext(), "안드로이드 버전 10 미만은 제공하지 않는 서비스입니다.\n다른 이미지를 선택해주세요", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String title = name.substring(0, name.lastIndexOf("."));
                                String tag = name.substring(name.lastIndexOf("."));
                                Boolean isCreate=true;
                                try {
                                    createIMG(editPath,editAbPath,title + "_2" + tag);
                                } catch (FileNotFoundException ex) {
                                    ex.printStackTrace();
                                    isCreate=false;
                                } finally {
                                    if(isCreate) {
                                        String childName = "/Camera/" + title + "_2" + tag;
                                        while (true) {
                                            String tmpPath = getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+childName;

//                                            new SingleMediaScanner(mContext, tmpPath);
                                            File file = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), childName);

                                            if (file.exists()) {
                                                Toast.makeText(getApplicationContext(), "메타데이터를 변경하기 어려운 이미지이거나 SD카드의 이미지 입니다.\n복사하여 새로운 이미지가 생성 되었습니다!", Toast.LENGTH_LONG).show();
                                                //상대경로로 만들어줌
                                                Uri tmpUri =Uri.fromFile(new File(tmpPath));
                                                tmpUri= getUriFromPath(tmpUri.toString());
                                                Log.e("relativePath",tmpUri.toString());

                                                setExif(tmpPath,targetPath_Date,tmpUri);

                                                setMediaStore(tmpUri,targetName,targetPath_Date,tmpPath,targetAbPath);

                                                Toast.makeText(getApplicationContext(), "새로운 이미지가 원하는 위치로 이동되었습니다!", Toast.LENGTH_SHORT).show();
                                                break;
                                            } else {
                                                Log.e("createOK", "create not yet");
                                            }
                                        }
                                    }
                                }
                            }
                        }


                        ////////////////mediastore 변경////////////////////
                    if(!isTakenCamera){

                        ContentValues values = new ContentValues();
                        ContentResolver resolver = mContext.getContentResolver();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            values.put(MediaStore.Images.Media.IS_PENDING, 1);
                            int update = resolver.update(editPath, values, null, null);
                            values.clear();
                        }

                        //이름 바꾸기
                        String title = targetName.substring(0, targetName.lastIndexOf("."));
                        String tag = targetName.substring(targetName.lastIndexOf("."));
                        Log.d("nameTitle", title + "2" + tag);
                        values.put(MediaStore.Images.Media.TITLE, title + "2" + tag);
                        values.put(MediaStore.Images.Media.DISPLAY_NAME, title + "2" + tag);

                        //날짜 바꾸기
                        Long tmp_targetPath_Date = Long.parseLong(targetPath_Date) / 1000 + 1;
                        String tmp_str_targetPath = Long.toString(tmp_targetPath_Date);
                        values.put(MediaStore.Images.Media.DATE_ADDED, tmp_str_targetPath);
                        values.put(MediaStore.Images.Media.DATE_TAKEN, tmp_str_targetPath);
                        values.put(MediaStore.Images.Media.DATE_MODIFIED, tmp_str_targetPath);

                        //폴더 바꾸기
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            if(editAbPath.indexOf("storage/emulated")==-1||targetAbPath.indexOf("storage/emulated")==-1){
                                Toast.makeText(getApplicationContext(), "내부저장소 -> SD 카드의 사진을 조작 할 때,\n메타데이터는 변경되지만 폴더 이동은 되지 않습니다.", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                if (targetAbPath.indexOf("DCIM") >= 0) {
                                    String subFolder = targetAbPath.substring(targetAbPath.indexOf("DCIM") + 4, targetAbPath.lastIndexOf("/"));
                                    Log.e("DCIMTest1", subFolder);
                                    values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + subFolder);
                                } else if (targetAbPath.indexOf("Pictures") >= 0) {
                                    String subFolder = targetAbPath.substring(targetAbPath.indexOf("Pictures") + 8, targetAbPath.lastIndexOf("/"));
                                    Log.d("DCIMTest2", subFolder);
                                    values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + subFolder);
                                } else if (targetAbPath.indexOf("Download") >= 0) {
                                    String subFolder = targetAbPath.substring(targetAbPath.indexOf("Download") + 8, targetAbPath.lastIndexOf("/"));
                                    Log.d("DCIMTest3", subFolder);
                                    values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + subFolder);
                                }else{
                                    Toast.makeText(getApplicationContext(), "예상하지 못한 파일 경로로 \n 폴더 이동이 되지 않습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "안드로이드 버전 10 미만에서는 폴더 이동이 되지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
//                        Set<String> volumeNames = null;
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                            volumeNames = MediaStore.getExternalVolumeNames(mContext);
//                        }
//                        else{
//                            volumeNames = MediaStore.;
//
//                        }
//                        Iterator<String> volumenamesiter =  volumeNames.iterator();
//                        String firstVolumeName = volumenamesiter.next();
//                        String secondVolumeName = volumenamesiter.next();
//                        String volumeName;
//
//                        if(targetAbPath.indexOf(secondVolumeName)>=0){
//                            volumeName=secondVolumeName;
//                        }
//                        else if(targetAbPath.indexOf(firstVolumeName)>=0){
//                            volumeName=firstVolumeName;
//                        }
//                        else{
//                            volumeName=MediaStore.VOLUME_EXTERNAL_PRIMARY;
//                        }

                        int update2 = resolver.update(editPath, values, null, null);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            values.clear();
                            values.put(MediaStore.Images.Media.IS_PENDING, 0);
                            int update3 = resolver.update(editPath, values, null, null);
                        }
                        new SingleMediaScanner(mContext, editAbPath);

                    }
                }
                finish();
            }
        });
    }
    public Uri getUriFromPath(String path){

    String fileName= path;
    Uri fileUri = Uri.parse( fileName );
    String filePath = fileUri.getPath();
    Cursor cursor = getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null, "_data = '" + filePath + "'", null, null );

    cursor.moveToNext();
    int id = cursor.getInt( cursor.getColumnIndex( "_id" ) );

    Uri uri = ContentUris.withAppendedId( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id );

    return uri;

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume(){
        super.onResume();
        BottomBar bottomBar = (BottomBar)findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab_photo);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createIMG(Uri editPath,String editAbPath, String Title) throws FileNotFoundException {

//      핸드폰으로 찍은 사진일 때 따로 다시 저장
        BitmapFactory.Options bitOption=new BitmapFactory.Options();
        bitOption.inSampleSize=1;
        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(editPath),null,bitOption);

        //storage
        ContentValues values = new ContentValues();
        ContentResolver contentResolver = getContentResolver();


        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM+"/Camera");
        values.put(MediaStore.Images.Media.TITLE, Title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, Title);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
        // 파일을 write중이라면 다른곳에서 데이터요구를 무시하겠다는 의미입니다.
        values.put(MediaStore.Images.Media.IS_PENDING, 1);



//        String ExternalPath=editAbPath;
////                = editAbPath.toString();
//        ExternalPath=ExternalPath.substring(0,ExternalPath.lastIndexOf("/"));
//        ExternalPath=ExternalPath.substring(0,ExternalPath.lastIndexOf("/"));
//        Log.e("externalPath",ExternalPath);
//        Uri collection = Uri.parse(ExterxnalPath);
        Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
//        Uri item = contentResolver.insert(Uri.parse(ExternalPath), values);
        Uri item = contentResolver.insert(collection, values);

        try {
            ParcelFileDescriptor pdf = contentResolver.openFileDescriptor(item, "w", null);
            if (pdf == null) {

            } else {
                InputStream inputStream = getImageInputStream(bitmap);
                byte[] strToByte = getBytes(inputStream);
                FileOutputStream fos = new FileOutputStream(pdf.getFileDescriptor());
                fos.write(strToByte);
                fos.close();
                inputStream.close();
                pdf.close();
                contentResolver.update(item, values, null, null);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        values.clear();
        // 파일을 모두 write하고 다른곳에서 사용할 수 있도록 0으로 업데이트를 해줍니다.
        values.put(MediaStore.Images.Media.IS_PENDING, 0);
        contentResolver.update(item, values, null, null);

    }
    public void setExif(String editAbPath, String targetPath_Date, Uri editPath){
        InputStream st = null;
        try {
            st = getContentResolver().openInputStream(editPath);
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
            Long tmpTargetDate = Long.parseLong(targetPath_Date) - 32400000 + 1000;
            Long T1 = Long.parseLong(Long.toString(Long.parseLong(targetPath_Date) + 1000));
            Long T2 = Long.parseLong(Long.toString(tmpTargetDate));
            String dateTime = formatter.format(new Date(T1));
            String dateTime2 = formatter.format(new Date(T2));
            Log.d("testDate1", targetPath_Date);
            Log.e("testDate", dateTime);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                exif.setAttribute(ExifInterface.TAG_DATETIME_ORIGINAL, dateTime);
                exif.setAttribute(ExifInterface.TAG_DATETIME, dateTime);
                exif.setAttribute(ExifInterface.TAG_DATETIME_DIGITIZED, dateTime);
                exif.setAttribute(ExifInterface.TAG_GPS_DATESTAMP, dateTime);

            }
            else{
                exif.setAttribute(ExifInterface.TAG_DATETIME_ORIGINAL, dateTime2);
                exif.setAttribute(ExifInterface.TAG_DATETIME, dateTime);
                exif.setAttribute(ExifInterface.TAG_DATETIME_DIGITIZED, dateTime);
                exif.setAttribute(ExifInterface.TAG_GPS_DATESTAMP, dateTime2);
            }
            //                    좌표 넣는거
//            String tmpLat = "null";
//            String tmpLong = "null";
//            try {
//                ExifInterface exifOrigin = new ExifInterface(targetAbPath);
//
//                tmpLat = exifOrigin.getAttribute(ExifInterface.TAG_GPS_LATITUDE) + "";
//                tmpLong = exifOrigin.getAttribute(ExifInterface.TAG_GPS_LONGITUDE) + "";
//
////                                String tmpCamera=exifOrigin.getAttribute(ExifInterface.TAG_CAMERA_OWNER_NAME)+"";
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
//                    String tmpCamera=exifOrigin.getAttribute(ExifInterface.TAG_CAMERA_OWNER_NAME)+"";
//
//                    if(!tmpCamera.equals("null")){
//                        isTakenCamera=true;
//                        Toast.makeText(getApplicationContext(),"안드로이드 버전 10 이하는 제공하지 않는 서비스입니다.\n다른 이미지를 선택해주세요", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            } catch (Exception e) {
//                Log.e("RealBad..","잘못햇어여어ㅠ");
//                e.printStackTrace();
//            }

//            if (!tmpLat.equals("null") && !tmpLong.equals("null")) {
//                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, tmpLat);
//                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, tmpLong);
//            }


            exif.saveAttributes();
            Log.d("scanning","스캐너 호출");
            new SingleMediaScanner(mContext, editAbPath);

        } catch (Exception e) {
            Log.d("setExif","실패!");
        }
    }
    public void setMediaStore(Uri editPath, String targetName, String targetPath_Date, String editAbPath, String targetAbPath){
        ContentValues values = new ContentValues();
        ContentResolver resolver = mContext.getContentResolver();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.IS_PENDING, 1);
            int update = resolver.update(editPath, values, null, null);
            values.clear();
        }

        //이름 바꾸기
        String title = targetName.substring(0, targetName.lastIndexOf("."));
        String tag = targetName.substring(targetName.lastIndexOf("."));
        Log.d("nameTitle", title + "2" + tag);
        values.put(MediaStore.Images.Media.TITLE, title + "2" + tag);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title + "2" + tag);

        //날짜 바꾸기
        Long tmp_targetPath_Date = Long.parseLong(targetPath_Date) / 1000 + 1;
        String tmp_str_targetPath = Long.toString(tmp_targetPath_Date);
        values.put(MediaStore.Images.Media.DATE_ADDED, tmp_str_targetPath);
        values.put(MediaStore.Images.Media.DATE_TAKEN, tmp_str_targetPath);
        values.put(MediaStore.Images.Media.DATE_MODIFIED, tmp_str_targetPath);

        //폴더 바꾸기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if(editAbPath.indexOf("storage/emulated")==-1||targetAbPath.indexOf("storage/emulated")==-1){
                Toast.makeText(getApplicationContext(), "내부저장소 -> SD 카드의 사진을 조작 할 때,\n메타데이터는 변경되지만 폴더 이동은 되지 않습니다.", Toast.LENGTH_SHORT).show();
            }
            else{
                if (targetAbPath.indexOf("DCIM") >= 0) {
                    String subFolder = targetAbPath.substring(targetAbPath.indexOf("DCIM") + 4, targetAbPath.lastIndexOf("/"));
                    Log.e("DCIMTest1", subFolder);
                    values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + subFolder);
                } else if (targetAbPath.indexOf("Pictures") >= 0) {
                    String subFolder = targetAbPath.substring(targetAbPath.indexOf("Pictures") + 8, targetAbPath.lastIndexOf("/"));
                    Log.d("DCIMTest2", subFolder);
                    values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + subFolder);
                } else if (targetAbPath.indexOf("Download") >= 0) {
                    String subFolder = targetAbPath.substring(targetAbPath.indexOf("Download") + 8, targetAbPath.lastIndexOf("/"));
                    Log.d("DCIMTest3", subFolder);
                    values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + subFolder);
                }else{
                    Toast.makeText(getApplicationContext(), "예상하지 못한 파일 경로로 \n 폴더 이동이 되지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "안드로이드 버전 10 미만에서는 폴더 이동이 되지 않습니다.", Toast.LENGTH_SHORT).show();
        }
//                        Set<String> volumeNames = null;
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                            volumeNames = MediaStore.getExternalVolumeNames(mContext);
//                        }
//                        else{
//                            volumeNames = MediaStore.;
//
//                        }
//                        Iterator<String> volumenamesiter =  volumeNames.iterator();
//                        String firstVolumeName = volumenamesiter.next();
//                        String secondVolumeName = volumenamesiter.next();
//                        String volumeName;
//
//                        if(targetAbPath.indexOf(secondVolumeName)>=0){
//                            volumeName=secondVolumeName;
//                        }
//                        else if(targetAbPath.indexOf(firstVolumeName)>=0){
//                            volumeName=firstVolumeName;
//                        }
//                        else{
//                            volumeName=MediaStore.VOLUME_EXTERNAL_PRIMARY;
//                        }

        int update2 = resolver.update(editPath, values, null, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear();
            values.put(MediaStore.Images.Media.IS_PENDING, 0);
            int update3 = resolver.update(editPath, values, null, null);
        }
        new SingleMediaScanner(mContext, editAbPath);

    }
    private InputStream getImageInputStream(Bitmap bmp) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        byte[] bitmapData = bytes.toByteArray();
        ByteArrayInputStream bs = new ByteArrayInputStream(bitmapData);

        return bs;
    }
    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

}