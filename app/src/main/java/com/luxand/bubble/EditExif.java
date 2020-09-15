package com.luxand.bubble;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class EditExif {
    private String editAbPath;
    private String targetPath_Date;
    private Uri editPath;
    private Context mContext;
    private Boolean isStartEdit = true;
    private String targetAbPath;
    public Boolean isTakenCamera = false;
    private String name;
    private String targetName;

    public EditExif(String editAbPath, String targetPath_Date, Uri editPath, Context mContext,String targetAbPath, String name, String targetName){
        this.editAbPath = editAbPath;
        this.targetPath_Date=targetPath_Date;
        this.editPath = editPath;
        this.mContext = mContext;
        this.targetAbPath = targetAbPath;
        this.name = name;
        this.targetName = targetName;
    }

    public void setIsStartEdit(boolean isStart){ this.isStartEdit = isStart; }

    public boolean startEditExif(){
        try{
            setExif(editAbPath,targetPath_Date);
        }catch (Exception e){
            e.printStackTrace();
            isTakenCamera=true;
            Log.e("ExifException", e.toString());
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                Toast.makeText(mContext.getApplicationContext(), "안드로이드 버전 10 미만은 제공하지 않는 서비스입니다.\n다른 이미지를 선택해주세요", Toast.LENGTH_SHORT).show();
            }
            else{
                String title = name.substring(0, name.lastIndexOf("."));
                String tag = name.substring(name.lastIndexOf("."));
                Boolean isCreate=true;
                try {
                    new EditCreateImg(editPath,title + "_2" + tag,mContext);
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
                                Toast.makeText(mContext.getApplicationContext(), "메타데이터를 변경하기 어려운 이미지이거나 SD카드의 이미지 입니다.\n복사하여 새로운 이미지가 생성 되었습니다!", Toast.LENGTH_LONG).show();
                                //상대경로로 만들어줌
                                Uri tmpUri =Uri.fromFile(new File(tmpPath));
                                tmpUri= getUriFromPath(tmpUri.toString());
                                Log.e("relativePath",tmpUri.toString());

                                try {
                                    setExif(tmpPath,targetPath_Date);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }finally {
                                    new EditMediaStore(tmpUri,targetName,targetPath_Date,tmpPath,targetAbPath,mContext);
                                    Toast.makeText(mContext.getApplicationContext(), "새로운 이미지가 원하는 위치로 이동되었습니다!", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            } else {
                                Log.e("createOK", "create not yet");
                            }
                        }
                    }
                }
            }
        }
        return isTakenCamera;
    }

    protected void setExif(String editAbPath, String targetPath_Date) throws Exception{

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

//          좌표 넣는거
            if(isStartEdit) {
                String tmpLat = "null";
                String tmpLong = "null";
                try {
                    ExifInterface exifOrigin = new ExifInterface(targetAbPath);

                    tmpLat = exifOrigin.getAttribute(ExifInterface.TAG_GPS_LATITUDE) + "";
                    tmpLong = exifOrigin.getAttribute(ExifInterface.TAG_GPS_LONGITUDE) + "";

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
//                    String tmpCamera=exifOrigin.getAttribute(ExifInterface.TAG_CAMERA_OWNER_NAME)+"";
                        String tmpCamera = exifOrigin.getAttribute(ExifInterface.TAG_SOFTWARE) + "";

                        if (!tmpCamera.equals("null")) {
                            isTakenCamera = true;
                            Toast.makeText(mContext.getApplicationContext(), "안드로이드 버전 10 이하는 제공하지 않는 서비스입니다.\n다른 이미지를 선택해주세요", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Log.e("RealBad..", "잘못햇어여어ㅠ");
                    e.printStackTrace();
                }

                if (!tmpLat.equals("null") && !tmpLong.equals("null")) {
                    exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, tmpLat);
                    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, tmpLong);
                }
                isStartEdit = false;
            }

            exif.saveAttributes();
            new SingleMediaScanner(mContext, editAbPath);

    }
    public Uri getUriFromPath(String path){

        String fileName= path;
        Uri fileUri = Uri.parse( fileName );
        String filePath = fileUri.getPath();
        Cursor cursor = mContext.getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, "_data = '" + filePath + "'", null, null );

        cursor.moveToNext();
        int id = cursor.getInt( cursor.getColumnIndex( "_id" ) );

        Uri uri = ContentUris.withAppendedId( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id );

        return uri;

    }
}
