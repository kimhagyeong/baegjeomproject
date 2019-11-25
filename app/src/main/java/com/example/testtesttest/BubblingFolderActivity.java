package com.example.testtesttest;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.roughike.bottombar.BottomBar;
import java.util.ArrayList;

public class BubblingFolderActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubblingfolder);
        BottomBar bottomBar = (BottomBar)findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab_folder);

        //Test용 객체 생성..
        ArrayList<String> list = new ArrayList<>();
        for (int i=0; i<30; i++) {
            list.add(String.format("TEXT %d", i)) ;
        }
        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        RecyclerView sideBar = findViewById(R.id.sidebar_recycler) ;
        sideBar.setLayoutManager(new LinearLayoutManager(this)) ;

        // 리사이클러뷰에 SideImageAdapter 객체 지정.
//        SideImageAdapter sideAdapter = new SideImageAdapter(list) ;
//        sideBar.setAdapter(sideAdapter) ;
    }
}