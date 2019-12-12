package com.example.testtesttest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.roughike.bottombar.BottomBar;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class GalleryActivity extends AppCompatActivity {
    public static Context mContext;
    protected RecyclerView gridImage;
    protected RecyclerView sideBar;
    protected ArrayList<String> imageBitmapList = new ArrayList<>();
    protected ArrayList<imageFolder> imageFolderList = new ArrayList<>();
    protected GridImageAdapter gridAdapter;
    protected SideImageAdapter sideAdapter;
    protected int folderSelectState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        sideAdapter = new SideImageAdapter(imageFolderList, this) ;
        sideBar.setAdapter(sideAdapter) ;
    }
    public void setFolderSelectState(int state) {
        folderSelectState = state;
    }

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();
        setImageFolderList();
        setImageBitmapList();
        BottomBar bottomBar = (BottomBar)findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab_Home);
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void setImageFolderList() {  //폴더 갱신
        imageFolderList.clear();
        imageFolderList.addAll(getPicturePaths());
        imageFolderList.add(0,imageFolderList.get(0));  //전체 폴더와 첫 번째 폴더

        sideAdapter.notifyDataSetChanged();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setImageBitmapList(){   //이미지 갱신
        imageBitmapList.clear();
        if (folderSelectState==0) { //전체 폴더를 출력
            imageFolderList.subList(1,imageFolderList.size()).stream()
                    .forEach(i->{
                        File a = new File(i.getPath());
                        Arrays.asList(a.listFiles())
                                .stream()
                                .map(f->f.getAbsolutePath())
                                .forEach(p->imageBitmapList.add(p));
                    });
        }
        else {  //폴더 하나 내부의 사진 출력
            File target = new File(imageFolderList.get(folderSelectState).getPath());
            Arrays.asList(target.listFiles())
                    .stream()
                    .map(f->f.getAbsolutePath())
                    .forEach(p->imageBitmapList.add(p));
        }
        gridAdapter.notifyDataSetChanged();
    }
    //폴더에서 이미지 검색
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private ArrayList<imageFolder> getPicturePaths() {  //모든 사진을 검색하여 폴더 경로 리스트 반환
        ArrayList<imageFolder> picFolders = new ArrayList<>();
        ArrayList<String> picPaths = new ArrayList<>();
        Uri allImagesUri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID};
        Cursor cursor = this.getContentResolver().query(allImagesUri, projection, null, null, null);
        try {
            if (cursor != null) {
                cursor.moveToFirst();

                do {
                    imageFolder folds = new imageFolder();
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                    String folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                    String datapath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                    //String date = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN));
                    //String folderpaths =  datapath.replace(name,"");
                    String folderPaths = datapath.substring(0, datapath.lastIndexOf(folder + "/"));
                    folderPaths = folderPaths + folder + "/";
                    if (!picPaths.contains(folderPaths)) {
                        picPaths.add(folderPaths);
                        folds.setPath(folderPaths);
                        folds.setFolderName(folder);
                        folds.setFirstPic(datapath);
                        folds.addPics();
                        picFolders.add(folds);
                    } else {
                        for (int i = 0; i < picFolders.size(); i++) {
                            if (picFolders.get(i).getPath().equals(folderPaths)) {
                                picFolders.get(i).setFirstPic(datapath);
                                picFolders.get(i).addPics();
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
            Log.d("picture folders", picFolders.get(i).getFolderName() + " and path = " + picFolders.get(i).getFirstPic() + " " + picFolders.get(i).getNumberOfPics());
        }
        return picFolders;
    }
}
