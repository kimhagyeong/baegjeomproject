package com.luxand.bubble.referenceClass;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.widget.Toast;

import com.luxand.bubble.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class EditCreateImg {
    private Bitmap inputStreamBitmap;
    private Context mContext;
    private String Title;

    // from BubblingMemo
    public EditCreateImg(String Title, Context mContext, String memo, String targetPath_Date, String targetAbPath){
        this.mContext = mContext;
        String title = Title.substring(0, Title.lastIndexOf("."));
        String tag = Title.substring(Title.lastIndexOf("."));
        this.Title = title+"_memo"+tag;

        Boolean isDoneCreate=true;
        try {
            createCanvas(memo);
        }catch (Exception e){
            e.printStackTrace();
            isDoneCreate=false;
        }
        finally {
            if(isDoneCreate){
                String childName = "/Camera/" + this.Title;
                while (true) {
                    String tmpPath = getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+childName;
//                                            new SingleMediaScanner(mContext, tmpPath);
                    File file = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), childName);

                    if (file.exists()) {
                        Toast.makeText(mContext.getApplicationContext(), "이미지화 중입니다!", Toast.LENGTH_LONG).show();
                        //상대경로로 만들어줌
                        Uri tmpUri =Uri.fromFile(new File(tmpPath));
                        tmpUri= getUriFromPath(tmpUri.toString());

                        EditExif setExif = new EditExif(tmpPath,targetPath_Date,tmpUri,mContext,targetAbPath,Title,Title);

                        try {
                            setExif.setIsStartEdit(false);
                            setExif.setExif(tmpPath,targetPath_Date);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }finally {
                            new EditMediaStore(tmpUri,Title,targetPath_Date,tmpPath,targetAbPath,mContext);
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
    // from BubblingPhoto
    public EditCreateImg(Uri editPath, String Title, Context mContext) throws FileNotFoundException {
        this.mContext = mContext;
        this.Title = Title;

        try {
            createBitmap(editPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new FileNotFoundException();
        }
    }

    private void createCanvas(String memo){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.photo2,options);

        Bitmap tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(tempBitmap);

        Paint paint1 = new Paint();
        paint1.setARGB(80,204,204,204);

        TextPaint paint2 = new TextPaint();
        int textSize=bitmap.getWidth()/10;
        paint2.setTextSize(textSize);
        paint2.setColor(Color.LTGRAY);
        paint2.setAntiAlias(true);

        StaticLayout.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            builder = StaticLayout.Builder.obtain(memo, 0, memo.length(), paint2,  bitmap.getWidth()-textSize );
        }
        StaticLayout textLayout = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            textLayout = builder.build();
        }

        canvas.drawBitmap(bitmap, 0, 0, paint1);
        canvas.translate( textSize/2, textSize/2 );
        textLayout.draw(canvas);

        this.inputStreamBitmap = tempBitmap;
        inputValues();
    }

    private void createBitmap(Uri editPath) throws FileNotFoundException {
        BitmapFactory.Options bitOption=new BitmapFactory.Options();
        bitOption.inSampleSize=1;
        Bitmap bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(editPath),null,bitOption);

        this.inputStreamBitmap = bitmap;
        inputValues();
    }

    private void inputValues(){
        //storage
        ContentValues values = new ContentValues();
        ContentResolver contentResolver = mContext.getContentResolver();


        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM+"/Camera");
        values.put(MediaStore.Images.Media.TITLE, Title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, Title);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
        // 파일을 write중이라면 다른곳에서 데이터요구를 무시하겠다는 의미입니다.
        values.put(MediaStore.Images.Media.IS_PENDING, 1);


        Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Uri item = contentResolver.insert(collection, values);

        try {
            ParcelFileDescriptor pdf = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                pdf = contentResolver.openFileDescriptor(item, "w", null);
            }
            if (pdf == null) {

            } else {
                InputStream inputStream = getImageInputStream(this.inputStreamBitmap);
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

