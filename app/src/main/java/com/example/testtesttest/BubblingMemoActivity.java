package com.example.testtesttest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.IdRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.exifinterface.media.ExifInterface;

import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;


public class BubblingMemoActivity extends GalleryActivity {
    AlertDialog.Builder builder;
    int folderState=0;
    String strPicFolder;

    Context context;
    Resources resources;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;    //this
        setContentView(R.layout.activity_bubblingfolder);
        super.onCreate(savedInstanceState);
        folderSelectState = getIntent().getIntExtra("folderState",0);

        BottomBar bottomBar = (BottomBar)findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab_folder);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_Home:
                        finish();
                        break;
                    case R.id.tab_folder:

                        break;
                    case R.id.tab_next:
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                        break;
                }
            }
        });
        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            //가운데 아이템이 활성화 되어있는 상태고 다시 눌렀을 때 선택한거 다사라짐
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if(tabId==R.id.tab_folder){
                    gridAdapter.clearSelectedItem();
                }else if(tabId==R.id.tab_next){
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        createAndSetAdapter();
        builder = new AlertDialog.Builder(BubblingMemoActivity.this);

        final EditText input = new EditText(BubblingMemoActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        builder.setTitle("남길 메모를 작성하세요!");
        builder.setView(input);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                AlertDialog.Builder innBuilder = new AlertDialog.Builder( BubblingMemoActivity.this);
                innBuilder.setMessage(input.getText().toString());
                innBuilder.setTitle("선택한 사진 옆에\n메모가 생깁니다!");
                innBuilder.setCancelable(false);
                innBuilder .setPositiveButton( "확인", new DialogInterface.OnClickListener(){
                    public void onClick( DialogInterface dialog, int which) {
                        //여기서 메타데이터 수정 이벤트
                        Toast.makeText(getApplicationContext(),input.getText().toString(), Toast.LENGTH_SHORT).show();
                        for(int i=0;i<gridAdapter.mSelectedItems.size();i++){
                            //이건 키 값
                            Log.e("test1",Integer.toString(gridAdapter.mSelectedItems.keyAt(i)));
                            //이건 주소 값
                            Log.d("test1",imageBitmapList.get(i).getImagePath().toString());
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                Toast.makeText(getApplicationContext(),"안드로이드 버전 10 이하는 제공하지 않는 서비스입니다.\n다른 이미지를 선택해주세요", Toast.LENGTH_SHORT).show();
                            }else{
                                inputToGallery(imageBitmapList.get(i).getImagePath(),input.getText().toString(), imageBitmapList.get(i).getImageDate());
                            }
                        }
                        dialog.dismiss();
                        finish();
                    }
                }); innBuilder.show();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void inputToGallery(Uri path, String memo, String Date){
        context = getApplicationContext();
        resources = getResources();

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.photo2,options);

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

        //storage
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM+"/Camera");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "fileName.jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");

        Long tmp_targetPath_Date = Long.parseLong(Date) / 1000 + 1;
        String tmp_str_targetPath = Long.toString(tmp_targetPath_Date);
//        values.put(MediaStore.Images.Media.DATE_ADDED, tmp_str_targetPath);
//        values.put(MediaStore.Images.Media.DATE_TAKEN, tmp_str_targetPath);
//        values.put(MediaStore.Images.Media.DATE_MODIFIED, tmp_str_targetPath);

        // 파일을 write중이라면 다른곳에서 데이터요구를 무시하겠다는 의미입니다.
        values.put(MediaStore.Images.Media.IS_PENDING, 1);

        ContentResolver contentResolver = getContentResolver();
        Set<String> volumeNames = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            volumeNames = MediaStore.getExternalVolumeNames(context);
        }


        String firstVolumeName = volumeNames.iterator().next();
//        String secondVolumeName = volumeNames.iterator().next();

        Uri collection = MediaStore.Images.Media.getContentUri(firstVolumeName);
        Uri item = contentResolver.insert(collection, values);
//        Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
//        Log.e("externalC",volumeNames.toString());
//        Log.e("externalP",MediaStore.VOLUME_EXTERNAL_PRIMARY);
//        Log.d("externalP",MediaStore.VOLUME_INTERNAL);

        try {
            ParcelFileDescriptor pdf = contentResolver.openFileDescriptor(item, "w", null);
            if (pdf == null) {

            } else {
                InputStream inputStream = getImageInputStream(tempBitmap);
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
//        canvas.restore();

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume(){
        super.onResume();
        BottomBar bottomBar = (BottomBar)findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab_folder);
    }
}