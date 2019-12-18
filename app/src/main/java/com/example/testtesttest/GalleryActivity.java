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
import java.util.ArrayList;
import java.util.Arrays;
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
    private String sortString = "name";
    private boolean ascDesc = true;

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

        try {
            imageFolderList.add(0, (imageFolder) imageFolderList.get(0).clone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        menuIn=menu;
        return true;
    }

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
                //이름은 이렇게 바꿔요
                sortString = "name";
                item.setChecked(true);

                break;
            case R.id.action_accending_date:
                //이름은 이렇게 바꿔요
                sortString = "date";
                item.setChecked(true);
                if(item.isChecked())
                {
                    Toast.makeText(getApplicationContext(), "tt", Toast.LENGTH_SHORT).show();}
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
        ArrayList<String> picPaths = new ArrayList<>();
        Uri allImagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media._ID,MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.LATITUDE,MediaStore.Images.Media.LONGITUDE};
        Cursor cursor = this.getContentResolver().query(allImagesUri, projection, null, null, null);
        try {
            if (cursor != null) {
                cursor.moveToFirst();

                do {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                    String folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                    String datapath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN));
                    String latt = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.LATITUDE));
                    String lngt = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.LONGITUDE));
                    if (datapath.contains("HEIC")) continue;
                    String folderPaths = datapath.substring(0, datapath.lastIndexOf(folder + "/"));
                    folderPaths = folderPaths + folder + "/";
                    imageFolder folds = new imageFolder();
                    if (!picPaths.contains(folderPaths)) {
                        picPaths.add(folderPaths);
                        folds.setPath(folderPaths);
                        folds.setFolderName(folder);
                        folds.addPics(datapath, date);
                        folds.setFirstPic();
                        picFolders.add(folds);
                    } else {
                        for (int i = 0; i < picFolders.size(); i++) {
                            if (picFolders.get(i).getPath().equals(folderPaths)) {
                                picFolders.get(i).addPics(datapath, date);
                            }
                        }
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < picFolders.size(); i++) {
            Log.d("picture folders", picFolders.get(i).getFolderName() + " and path = " + picFolders.get(i).getPath() + " " + picFolders.get(i).getNumberOfPics());
        }
        return picFolders;
    }
}
