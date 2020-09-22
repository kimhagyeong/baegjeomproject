package com.luxand.bubble.referenceClass;

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
        this.editPath = editPath;
        this.mContext = mContext;
        this.targetAbPath = targetAbPath;
        this.name = name;
        this.targetName = targetName;

        searchRealTime realTime = new searchRealTime(targetAbPath,targetPath_Date);
        this.targetPath_Date=realTime.getRealDate();
    }

    public void setIsStartEdit(boolean isStart){ this.isStartEdit = isStart; }

    // exif의 date값, gps값까지 변경하고 성공하면 true를 실패하면 false를 출력한다.
    // 단 실패할 때에는 새로운 이미지를 생성하는 editCreateImg를 호출하게 되고,
    // 새로운 이미지의 exif 변경과 mediastore의 변경이 이루어진다.
    public boolean startEditExif(){
        try{
            setExif(editAbPath,targetPath_Date);
        }catch (Exception e){
            e.printStackTrace();
            isTakenCamera=true;
            String title = name.substring(0, name.lastIndexOf("."));
            String tag = name.substring(name.lastIndexOf("."));
            Boolean isCreate=true;
            try {
                new EditCreateImg(editPath, title + "_2" + tag, mContext);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                isCreate=false;
            } finally {
                if(isCreate) {
                    String childName = "/Camera/" + title + "_2" + tag;
                    while (true) {
                        String tmpPath = getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+childName;
                        File file = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), childName);

                        if (file.exists()) {
                            Toast.makeText(mContext.getApplicationContext(), "메타데이터를 변경하기 어려운 이미지이거나 SD카드의 이미지 입니다.\n복사하여 새로운 이미지가 생성 되었습니다!", Toast.LENGTH_LONG).show();
                            //상대경로로 만들어줌
                            Uri tmpUri =Uri.fromFile(new File(tmpPath));
                            tmpUri= getUriFromPath(tmpUri.toString());

                            try {
                                setExif(tmpPath,targetPath_Date);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }finally {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    new EditMediaStore(tmpUri,targetName,targetPath_Date,tmpPath,targetAbPath,mContext);
                                }
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
        return isTakenCamera;
    }

    // exif 를 변경하는 구간으로
    // gps를 검사할지 말지에 대한 여부는 isStartEdit 변수를 통해 확인 가능하다.
    // 여기서 유의할 점은 버전 9 이하에서는 핸드폰으로 찍은 사진의 경우 exif가 들어가도 갤러리에 반영되지 않으며 exception도 일으키지 않기 때문에
    // 핸드폰으로 찍은 사진인지 여부를 판단하고 핸드폰으로 찍은 사진의 경우 새로운 이미지를 출력하게 된다.
    // 새로운 이미지를 생성하기 위해서는 exception을 throw 해주고 catch로 받아서 생성하는 구조이다.
    protected void setExif(String editAbPath, String targetPath_Date) throws Exception{
            Boolean isThrow = false;
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
                        String tmpCamera = exif.getAttribute(ExifInterface.TAG_SOFTWARE) + "";
                        if (!tmpCamera.equals("null")) {
                            isTakenCamera = true;
                            isThrow = true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!tmpLat.equals("null") && !tmpLong.equals("null")) {
                    exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, tmpLat);
                    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, tmpLong);
                }
                isStartEdit = false;
            }

            if(isThrow){
                FileNotFoundException ex = new FileNotFoundException();
                throw ex;
            }
            exif.saveAttributes();
            new SingleMediaScanner(mContext, editAbPath);

    }

    // 새로운 이미지가 생성되고 mediastore의 폴더 위치, 시간 등을 바꾸려면 상대경로가 필요하다
    // 절대경로를 상대경로로 바꾸는 함수이며
    // 버전 9에서는 동작하지 않는다.

    public Uri getUriFromPath(String path){

        String fileName= path;
        Uri fileUri = Uri.parse( fileName );
        String filePath = fileUri.getPath();
        Cursor cursor = mContext.getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, "_data = '" + filePath + "'", null, null );
        Uri uri=null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            uri=null;
        }else{
            cursor.moveToFirst();
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
        }

        return uri;
    }
}
