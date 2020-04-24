package com.example.testtesttest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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

public class BubblingPhotoActivity extends GalleryActivity {
    AlertDialog.Builder builder;
    int photoState=0;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;    //this
        setContentView(R.layout.activity_bubblingphoto);
        super.onCreate(savedInstanceState);
        folderSelectState = getIntent().getIntExtra("folderState",0);

        BottomBar bottomBar = (BottomBar)findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab_photo);

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_Home:
                        finish();
                        break;
                    case R.id.tab_photo:

                        break;
                    case R.id.tab_next:
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                       break;
                }
            }
        });
        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if(tabId==R.id.tab_photo){
                    gridAdapter.clearSelectedItem();
                }else if(tabId==R.id.tab_next){
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
        createAndSetAdapter();
        builder = new AlertDialog.Builder(BubblingPhotoActivity.this);

        builder.setTitle("Let's go Bubbling").setMessage("사진을 선택한 사진 옆으로 이동할까요?");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                //여기서 메타데이터 수정 이벤트
                for(int i=0;i<gridAdapter.mSelectedItems.size();i++){
                    //이건 키 값
                    Log.e("test1",Integer.toString(gridAdapter.mSelectedItems.keyAt(i)));
                    //이건 주소 값
                    Log.d("test1",imageBitmapList.get(i).getImagePath().toString());
                }
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume(){
        super.onResume();
        BottomBar bottomBar = (BottomBar)findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab_photo);
    }
}