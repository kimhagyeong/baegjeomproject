package com.example.testtesttest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.roughike.bottombar.BottomBar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

public abstract class GalleryActivity extends AppCompatActivity {
    public static Context mContext;
    protected RecyclerView gridImage;
    protected RecyclerView sideBar;
    protected ArrayList<dateImage> imageBitmapList = new ArrayList<>();
    protected ArrayList<imageFolder> imageFolderList = new ArrayList<>();
    protected GridImageAdapter gridAdapter;
    protected SideImageAdapter sideAdapter;
    protected int folderSelectState = 0;
    public Toolbar toolbar;
    private String sortString = "date";
    private boolean ascDesc = false;

    //
    public ArrayList<String> publicfolderNames = new ArrayList<>();
    //
    public Menu menuIn;
    public static String str;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar = findViewById(R.id.gallery_toolbar);
        setSupportActionBar(toolbar);
        imageFolderList.addAll(getPicturePaths());
        imageFolderList.sort((i,j)->j.folderdate.compareTo(i.folderdate));
        try {
            imageFolderList.add(0, (imageFolder) imageFolderList.get(0).clone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    //상단 옵션메뉴들
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        menuIn=menu;
        return true;
    }
    //초기에는 이르순, 오름차순이 활성화 되어있음
    public void setVisibleMenu(){
        MenuItem item1 = menuIn.findItem(R.id.action_accending_name);
        MenuItem item2 = menuIn.findItem(R.id.action_accending_date);
        MenuItem item3 = menuIn.findItem(R.id.action_accending_ASC);
        MenuItem item4 = menuIn.findItem(R.id.action_accending_DESC);
        if(gridAdapter.total>=1){
            item1.setVisible(false);
            item2.setVisible(false);
            item3.setVisible(false);
            item4.setVisible(false);
        }else{
            item1.setVisible(true);
            item2.setVisible(true);
            item3.setVisible(true);
            item4.setVisible(true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_accending_name:
                sortString = "name";
                item.setChecked(true);
                break;
            case R.id.action_accending_date:
                sortString = "date";
                item.setChecked(true);
                break;
            case R.id.action_accending_ASC:
                ascDesc = true;
                item.setChecked(true);
                break;
            case R.id.action_accending_DESC:
                ascDesc = false;
                item.setChecked(true);
                break;
        }
        setImageBitmapList();
        return super.onOptionsItemSelected(item);
    }

    protected void createAndSetAdapter() { //어댑터 생성 및 지정
        //그리드 이미지 불러올 어댑터 지정
        gridImage = findViewById(R.id.gridImage_recycler) ;
        gridImage.setHasFixedSize(true);
        gridImage.setLayoutManager(new GridLayoutManager(this, 3)) ;
        gridAdapter = new GridImageAdapter(imageBitmapList, this) ;
        gridImage.setAdapter(gridAdapter) ;

        //사이드바 폴더 불러올 어댑터 지정
        sideBar = findViewById(R.id.sidebar_recycler) ;
        sideBar.setLayoutManager(new LinearLayoutManager(this)) ;
        sideAdapter = new SideImageAdapter(imageFolderList, this, gridAdapter) ;
        sideBar.setAdapter(sideAdapter) ;
    }
    public void setFolderSelectState(int state) {
        this.folderSelectState = state;
    }
    public int getFolderSelectState() {return folderSelectState;}

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();
        setImageBitmapList();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setImageBitmapList(){   //이미지 갱신
        imageBitmapList.clear();
        if (this.folderSelectState==0) { //전체 폴더를 출력
            imageFolderList.subList(1,imageFolderList.size())
                    .forEach(i-> imageBitmapList.addAll(i.picPathList)
                    );
            imageBitmapList.sort((i,j)->i.compareTo(j,sortString,ascDesc));//정렬 로직 들어갈 부분
        }
        else {  //폴더 하나 내부의 사진 출력
            imageFolderList.get(folderSelectState)
                    .picPathList.stream()
                    .sorted((i,j)->i.compareTo(j,sortString,ascDesc))    //정렬 로직 들어갈 부분
                    .forEach(p->imageBitmapList.add(p));
        }
        //선택된 폴더의 가장 첫 이미지를 폴더의 이미지로 설정 후 변경
        imageFolderList.get(folderSelectState).setFirstPic(imageBitmapList.get(0));
        gridAdapter.notifyDataSetChanged();                 //grid 이미지들 갱신
        sideAdapter.notifyItemChanged(folderSelectState);   //side 이미지 갱신
    }

    //폴더에서 이미지 검색
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private ArrayList<imageFolder> getPicturePaths() {  //모든 사진을 검색하여 폴더 경로 리스트 반환
        ArrayList<imageFolder> picFolders = new ArrayList<>();
        ArrayList<String> folderNames = new ArrayList<>();
        //배열로 grid를 매핑시킬것

        //최상단 Uri를 받아오고
        Uri allImagesUri= MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        //사용할 속성들
        String[] projection = {
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.DATE_ADDED,
//                MediaStore.Images.ImageColumns.DATE_MODIFIED,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATA,

        };
        Cursor cursor=null;
        try {
            //최상단 아래있는 모든 이미지 파일들을 검사할것
             cursor= this.getContentResolver().query(allImagesUri, projection, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();

                do {
                    int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
                    Long id = cursor.getLong(idColumn);
                    Uri contentUri = Uri.withAppendedPath(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id.toString()
                    );
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME));
                    String folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME));
                    String dateTaken = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN));
                    String dateAdded = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_ADDED));
//                    String dateModified = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_MODIFIED));

                    String date = null;
                    if (dateTaken != null && dateAdded != null) date = dateTaken.compareTo(dateAdded)==-1?dateTaken:dateAdded;
                    else {
                        date = dateTaken==null?(dateAdded==null?"":dateAdded):dateTaken;
                    }

                    //date가 멋있게 나오니 가독성 있게 읽으려면 아래 단계로 확인해보길
                    String format = "MM-dd-yyyy HH:mm:ss";
                    SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);
                        Long T = Long.parseLong(date);
                        String dateTime = formatter.format(new Date(T));
                        Log.e("test1", dateTime + "|" + date);
//                    }
                    //여기까지

                    // if (datapath.contains("HEIC")) continue; //아이폰만 있는 확장자제거
                    imageFolder folds = new imageFolder();

                    //이미 있는 폴더가 아니면 새로운 폴더 리스트를 추가해주고
                    if (!folderNames.contains(folder)) {
                        folderNames.add(folder);
                        publicfolderNames.add(folder);
                        folds.setFolderName(folder);
                        folds.addPics(contentUri, date,name);   //addpic을 통해서 새로운 파일을 추가한다는 뜻
                        folds.setFirstPic(0);
                        folds.folderdate = date;
                        picFolders.add(folds);
                    } else { //이미 있는 폴더면 폴더 안에 파일을 넣어줌
                        for (int i = 0; i < picFolders.size(); i++) {
                            if (picFolders.get(i).getFolderName().equals(folder)) {
                                picFolders.get(i).addPics(contentUri, date, name);
                                if (picFolders.get(i).folderdate.compareTo(date)==-1)
                                    picFolders.get(i).folderdate = date;
                                break;
                            }
                        }
                    }
                    //Log.d("test1", "[name ="+name +"], ["+date+']'+contentUri+"["+folder);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            cursor.close();
        }
//        for (int i = 0; i < picFolders.size(); i++) {
//            Log.d("picture folders", picFolders.get(i).getFolderName() + " and path = " + picFolders.get(i).getPath() + " " + picFolders.get(i).getNumberOfPics());
//        }


        return picFolders;
    }
}
