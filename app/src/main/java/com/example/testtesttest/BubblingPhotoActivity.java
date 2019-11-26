package com.example.testtesttest;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;

public class BubblingPhotoActivity extends AppCompatActivity {
    AlertDialog.Builder builder;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubblingphoto);

       // View inflatedView = getLayoutInflater().inflate(R.layout.activity_bubblingphoto, null);
        //BottomBar bottomBar = (BottomBar) inflatedView.findViewById(R.id.bottomBarPhoto);

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
        builder = new AlertDialog.Builder(BubblingPhotoActivity.this);

        builder.setTitle("버튼 추가 예제").setMessage("선택하세요.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                Toast.makeText(getApplicationContext(), "OK Click", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        }


        //Test용 객체 생성..
//       // ArrayList<String> list = new ArrayList<>();
//        //for (int i=0; i<30; i++) {
//            list.add(String.format("TEXT %d", i)) ;
//        }
//        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
//        RecyclerView sideBar = findViewById(R.id.sidebar_recycler) ;
//        sideBar.setLayoutManager(new LinearLayoutManager(this)) ;

        // 리사이클러뷰에 SideImageAdapter 객체 지정.

    }
