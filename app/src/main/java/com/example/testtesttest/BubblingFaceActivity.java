package com.example.testtesttest;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.IdRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.exifinterface.media.ExifInterface;

import com.bumptech.glide.Glide;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class BubblingFaceActivity extends GalleryActivity {
    AlertDialog.Builder builder;
    int folderState=0;
    String strPicFolder;
    private DBHelper helper;
    private SQLiteDatabase db;

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
        builder = new AlertDialog.Builder(BubblingFaceActivity.this);
//        final String[] items = {"Apple", "Banana", "Orange", "Grapes"};
        final String[] items = publicfolderNames.toArray(new String[publicfolderNames.size()]);

        builder.setTitle("Let's go Bubbling!")
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        strPicFolder=items[which];
                        Toast.makeText(getApplicationContext(), items[which]+ " is clicked", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                AlertDialog.Builder innBuilder = new AlertDialog.Builder( BubblingFaceActivity.this);
                innBuilder.setMessage("이 폴더로 모든 사진을 이동합니다");
                innBuilder.setTitle(strPicFolder);
                innBuilder.setPositiveButton( "확인", new DialogInterface.OnClickListener(){
                    public void onClick( DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                        String tel;
                        Cursor cursor;

                        //여기서 메타데이터 수정 이벤트
                        for(int i=0;i<gridAdapter.mSelectedItems.size();i++){
                            cursor = db.rawQuery("SELECT name, uri FROM contacts WHERE date='" + imageBitmapList.get(i).getImageDate() +"';", null);

                            // 반환된 커서에 ResultSets의 행의 개수가 0개일 경우
                            if(cursor.getCount() == 0) {
                                Toast.makeText(getApplicationContext(), "해당 이름이 없습니다", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            // 반환된 커서를 가지고 전화번호 얻고 EditText에 표시
                            while(cursor.moveToNext()) {
                                tel = cursor.getString(0);
                            }
                            cursor.close();
                        }

                    }
                }); innBuilder.show();


            }
        });
    }

    @Override
    public void makeDB(){
        // SQLiteOpenHelper 클래스의 subclass인 DBHelper 클래스 객체 생성
        helper = new DBHelper(this);
        // DBHelper 객체를 이용하여 DB 생성
        try {
            db = helper.getWritableDatabase();
        } catch (SQLiteException e) {
            db = helper.getReadableDatabase();
        }
    }

    @Override
    public void insertDB(String name, Uri uri, String date, String position){
        int config=0;

        InputStream stream= null;
        try {
            stream = getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //exif출력 작동안됨. 오픈라이브러리 가져왔음
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        position=showExif(exif);

        position="t";
        db.execSQL("INSERT INTO contacts VALUES ('"+name+"','"+uri.toString()+"','"+date+"','"+position+"','"+config+"');");

    }

    private String getTagString(String tag, ExifInterface exif) {

        return (tag + " : " + exif.getAttribute(tag) + "\n");
    }

    private String showExif(ExifInterface exif) {
        String myAttribute = "[Exif information] \n\n";
        myAttribute += getTagString(ExifInterface.TAG_GPS_LATITUDE, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LATITUDE_REF, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LONGITUDE, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LONGITUDE_REF, exif);

        return myAttribute;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume(){
        super.onResume();
        BottomBar bottomBar = (BottomBar)findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab_folder);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.execSQL("DELETE FROM contacts");
        helper.close();

    }
}