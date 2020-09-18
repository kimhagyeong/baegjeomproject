package com.luxand.bubble.referenceClass;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

public class EditMediaStore {

    // memo 나 photo 의 경우 시간과 폴더 위치, 파일 이름 모두 바꾸지만
    // folder 에 경우는 폴더 위치만
    // allalbum activity의 경우는 시간만 바꾸면 된다.
    // mediastore는 버전에 따라 먹히는 태그 들이 다르다.
    public EditMediaStore(Uri editPath, String targetName, String targetPath_Date, String editAbPath, String targetAbPath, Context mContext){
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
                Toast.makeText(mContext.getApplicationContext(), "내부저장소 -> SD 카드의 사진을 조작 할 때,\n메타데이터는 변경되지만 폴더 이동은 되지 않습니다.", Toast.LENGTH_SHORT).show();
            }
            else{
                if (targetAbPath.indexOf("DCIM") >= 0) {
                    String subFolder = targetAbPath.substring(targetAbPath.indexOf("DCIM") + 4, targetAbPath.lastIndexOf("/"));
                    values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + subFolder);
                } else if (targetAbPath.indexOf("Pictures") >= 0) {
                    String subFolder = targetAbPath.substring(targetAbPath.indexOf("Pictures") + 8, targetAbPath.lastIndexOf("/"));
                    values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + subFolder);
                } else if (targetAbPath.indexOf("Download") >= 0) {
                    String subFolder = targetAbPath.substring(targetAbPath.indexOf("Download") + 8, targetAbPath.lastIndexOf("/"));
                    values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + subFolder);
                }else{
                    Toast.makeText(mContext.getApplicationContext(), "예상하지 못한 파일 경로로 \n 폴더 이동이 되지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else{
            Toast.makeText(mContext.getApplicationContext(), "안드로이드 버전 10 미만에서는 폴더 이동이 되지 않습니다.", Toast.LENGTH_SHORT).show();
        }
        int update2 = resolver.update(editPath, values, null, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear();
            values.put(MediaStore.Images.Media.IS_PENDING, 0);
            int update3 = resolver.update(editPath, values, null, null);
        }
        new SingleMediaScanner(mContext, editAbPath);
        Toast.makeText(mContext.getApplicationContext(), "정상 완료!", Toast.LENGTH_SHORT).show();
    }

    // call from BubblingFolder
    public EditMediaStore(Context mContext, Uri editPath, String editAbPath, String targetAbPath,String targetName,String date){
        ContentValues values = new ContentValues();
        ContentResolver resolver = mContext.getContentResolver();

        values.put(MediaStore.Images.Media.IS_PENDING, 1);
        int update = resolver.update(editPath, values, null, null);
        values.clear();

        //이름 바꾸기
        String tag = targetName.substring(targetName.lastIndexOf("."));

        //폴더 바꾸기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if(editAbPath.indexOf("storage/emulated")==-1){
                Toast.makeText(mContext.getApplicationContext(), "카메라로 찍은 사진이나 \nSD 카드의 사진을 조작 할 때,\n폴더 이동은 되지 않습니다.", Toast.LENGTH_SHORT).show();
            }
            else{
                if (targetAbPath.indexOf("DCIM") >= 0) {
                    String subFolder = targetAbPath.substring(targetAbPath.indexOf("DCIM") + 4);
                    values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + subFolder);

                    values.put(MediaStore.Images.Media.TITLE, subFolder.substring(1)+"_"+date + "2" + tag);
                    values.put(MediaStore.Images.Media.DISPLAY_NAME, subFolder.substring(1)+"_"+date + "2" + tag);

                } else if (targetAbPath.indexOf("Pictures") >= 0) {
                    String subFolder = targetAbPath.substring(targetAbPath.indexOf("Pictures") + 8);
                    values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + subFolder);

                    values.put(MediaStore.Images.Media.TITLE, subFolder.substring(1)+"_"+date + "2" + tag);
                    values.put(MediaStore.Images.Media.DISPLAY_NAME, subFolder.substring(1)+"_"+date + "2" + tag);

                } else if (targetAbPath.indexOf("Download") >= 0) {
                    String subFolder = targetAbPath.substring(targetAbPath.indexOf("Download") + 8);
                    values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + subFolder);

                    values.put(MediaStore.Images.Media.TITLE, subFolder.substring(1)+"_"+date + "2" + tag);
                    values.put(MediaStore.Images.Media.DISPLAY_NAME, subFolder.substring(1)+"_"+date + "2" + tag);
                }else{
                    Toast.makeText(mContext.getApplicationContext(), "예상하지 못한 파일 경로로 \n 폴더 이동이 되지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else{
            Toast.makeText(mContext.getApplicationContext(), "안드로이드 버전 10 미만에서는 폴더 이동이 되지 않습니다.", Toast.LENGTH_SHORT).show();
        }
        int update2 = resolver.update(editPath, values, null, null);

        values.clear();
        values.put(MediaStore.Images.Media.IS_PENDING, 0);
        int update3 = resolver.update(editPath, values, null, null);

        new SingleMediaScanner(mContext, editAbPath);
        Toast.makeText(mContext.getApplicationContext(), "정상 완료!", Toast.LENGTH_SHORT).show();
    }

    // from AllalbumActivity
    public EditMediaStore(Uri editPath,String targetPath_Date, String editAbPath, Context mContext){
        ContentValues values = new ContentValues();
        ContentResolver resolver = mContext.getContentResolver();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.IS_PENDING, 1);
            int update = resolver.update(editPath, values, null, null);
            values.clear();
        }

        //날짜 바꾸기
        Long tmp_targetPath_Date = Long.parseLong(targetPath_Date) / 1000 + 1;
        String tmp_str_targetPath = Long.toString(tmp_targetPath_Date);
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
        Toast.makeText(mContext.getApplicationContext(), "정상 완료!", Toast.LENGTH_SHORT).show();
    }
}
