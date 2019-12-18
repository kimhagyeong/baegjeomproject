package com.example.testtesttest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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

public class AllalbumActivity extends GalleryActivity {
    int homeState=0;
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        setContentView(R.layout.activity_allalbum);
        super.onCreate(savedInstanceState);
        createAndSetAdapter();
        folderSelectState = 0;

        BottomBar bottomBar = (BottomBar)findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_Home:
                        Toast.makeText(getApplicationContext(), Integer.toString(tabId), Toast.LENGTH_LONG).show();

                        break;
                    case R.id.tab_photo:
                        Toast.makeText(getApplicationContext(), Integer.toString(tabId + 1), Toast.LENGTH_LONG).show();
                        Intent in = new Intent(AllalbumActivity.this, BubblingPhotoActivity.class);
                        in.putExtra("folderState", folderSelectState);
                        startActivity(in);
                        break;
                    case R.id.tab_folder:
                        Toast.makeText(getApplicationContext(), Integer.toString(tabId + 2), Toast.LENGTH_LONG).show();
                        Intent in2 = new Intent(AllalbumActivity.this, BubblingFolderActivity.class);
                        in2.putExtra("folderState", folderSelectState);
                        startActivity(in2);
                        break;
                }
            }
        });
        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if(R.id.tab_Home==tabId)
                    if(homeState==0) homeState+=1;
                    else {
                        homeState-=1;
                        finish();}
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume(){
        super.onResume();
        BottomBar bottomBar = (BottomBar)findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab_Home);

    }
}
