package com.example.testtesttest;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class EditCreateImg {

    public EditCreateImg(Uri editPath, String Title, Context mContext)throws FileNotFoundException{

//      핸드폰으로 찍은 사진일 때 따로 다시 저장
        BitmapFactory.Options bitOption=new BitmapFactory.Options();
        bitOption.inSampleSize=1;
        Bitmap bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(editPath),null,bitOption);

        //storage
        ContentValues values = new ContentValues();
        ContentResolver contentResolver = mContext.getContentResolver();


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
            ParcelFileDescriptor pdf = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                pdf = contentResolver.openFileDescriptor(item, "w", null);
            }
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

