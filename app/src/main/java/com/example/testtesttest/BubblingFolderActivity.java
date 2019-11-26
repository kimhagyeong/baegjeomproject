package com.example.testtesttest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class BubblingFolderActivity extends AppCompatActivity {
    AlertDialog.Builder builder;

    public static Context mContext;
    private RecyclerView gridImage;
    private RecyclerView sideBar;
    private ArrayList<String> imageBitmapList = new ArrayList<>();
    private ArrayList<imageFolder> imageFolderList = new ArrayList<>();
    private GridImageAdapter gridAdapter;
    private SideImageAdapter sideAdapter;
    private int folderSelectState;
    File loadPath;

    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;    //this
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubblingfolder);

        BottomBar bottomBar = (BottomBar)findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab_folder);

        folderSelectState = 1;      //this

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_Home:
                        finish();
                        break;
                    case R.id.tab_photo:
                        //Toast.makeText(getApplicationContext(), "지우는 함수", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.tab_next:
                        Toast.makeText(getApplicationContext(), "사진처리하고 종료", Toast.LENGTH_LONG).show();
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                        break;
                }
            }
        });
        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if(R.id.tab_photo==tabId)
                    Toast.makeText(getApplicationContext(), "지우는 함수", Toast.LENGTH_LONG).show();
            }
        });
        builder = new AlertDialog.Builder(BubblingFolderActivity.this);

        builder.setTitle("Bubbling Photo complete!").setMessage("사진을 선택한 사진 옆으로 이동했어요!");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                Toast.makeText(getApplicationContext(), "OK Click", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        // 리사이클러뷰에 SideImageAdapter 객체 지정.

        ////////////이밑으로 다 content_main사항
        gridImage = findViewById(R.id.gridImage_recycler) ;
        gridImage.setHasFixedSize(true);
        gridImage.setLayoutManager(new GridLayoutManager(this, 3)) ;
        gridAdapter = new GridImageAdapter(imageBitmapList, this) ;

        gridImage.setAdapter(gridAdapter) ;

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        sideBar = findViewById(R.id.sidebar_recycler) ;
        sideBar.setLayoutManager(new LinearLayoutManager(this)) ;

        // 리사이클러뷰에 SideImageAdapter 객체 지정.
        sideAdapter = new SideImageAdapter(imageFolderList, this) ;
        sideBar.setAdapter(sideAdapter) ;
    }

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();
        setImageFolderList();
        setImageBitmapList();
        BottomBar bottomBar = (BottomBar)findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab_folder);
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void setImageFolderList() {
        imageFolderList.clear();
        imageFolderList.addAll(getPicturePaths());
        imageFolderList.add(0,imageFolderList.get(0));

        sideAdapter.notifyDataSetChanged();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setImageBitmapList(){
        imageBitmapList.clear();
        if (folderSelectState==0) {
            imageFolderList.subList(1,imageFolderList.size()).stream()
                    .forEach(i->{
                        File a = new File(i.getPath());
                        Arrays.asList(a.listFiles())
                                .stream()
                                .map(f->f.getAbsolutePath())
                                .forEach(p->imageBitmapList.add(p));
                    });
        }
        else {
            File target = new File(imageFolderList.get(folderSelectState).getPath());
            Arrays.asList(target.listFiles())
                    .stream()
                    .map(f->f.getAbsolutePath())
                    .forEach(p->imageBitmapList.add(p));
        }
        gridAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_accending_name) {

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            //이름은 이렇게 바꿔요
            toolbar.setTitle("ss");
            return true;
        }
        else if (id == R.id.action_accending_date) {

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            //이름은 이렇게 바꿔요
            toolbar.setTitle("ss");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    //////////////////-->폴더 검색
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private ArrayList<imageFolder> getPicturePaths() {
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