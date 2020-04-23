package com.example.testtesttest;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    String[] permission_list = {
            //5g 핸드폰의 경우
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE

            //5g 핸드폰 아닌 경우
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_EXTERNAL_STORAGE

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();
        ///////툴바랑 하단메뉴바 설정중
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //툴바 액티비티 이름대로 사용안할때
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //이름은 이렇게 바꿔요
        toolbar.setTitle("Bubble");

        Button button1 = findViewById(R.id.allAlbum );
        Button button2 = findViewById(R.id.bubblingPhoto );
        Button button3 = findViewById(R.id.bubblingFolder );
        Button button4 = findViewById(R.id.bubblingMemo );
        Button button5 = findViewById(R.id.bubblingFace );
        Button button6 = findViewById(R.id.Howtouse );

        View.OnClickListener Buttons=new View.OnClickListener(){
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.allAlbum:
                        Intent in1 = new Intent(MainActivity.this, AllalbumActivity.class);
                        startActivity(in1);
                        break;
                    case R.id.bubblingPhoto:
                        Intent in2 = new Intent(MainActivity.this, BubblingPhotoActivity.class);
                        startActivity(in2);
                        break;
                    case R.id.bubblingFolder:
                        Intent in3 = new Intent(MainActivity.this, BubblingFolderActivity.class);
                        startActivity(in3);
                        break;
                    case R.id.bubblingMemo:
                        Intent in4 = new Intent(MainActivity.this, BubblingMemoActivity.class);
                        startActivity(in4);
                        break;
                    case R.id.bubblingFace:
                        Intent in5 = new Intent(MainActivity.this, BubblingFaceActivity.class);
                        startActivity(in5);
                        break;
                    case R.id.Howtouse:
                        Intent in6 = new Intent(MainActivity.this, HowtouseActivity.class);
                        startActivity(in6);
                        break;
                }

            }
        };
        button1.setOnClickListener(Buttons);
        button2.setOnClickListener(Buttons);
        button3.setOnClickListener(Buttons);
        button4.setOnClickListener(Buttons);
        button5.setOnClickListener(Buttons);
        button6.setOnClickListener(Buttons);
    }
    //////////////////>권한 설정
    public void checkPermission(){
        //현재 안드로이드 버전이 6.0미만이면 메서드를 종료한다.
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;

        for(String permission : permission_list){
            //권한 허용 여부를 확인한다.
            int chk = checkCallingOrSelfPermission(permission);

            if(chk == PackageManager.PERMISSION_DENIED){
                //권한 허용을여부를 확인하는 창을 띄운다
                requestPermissions(permission_list,0);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==0)
        {
            for(int i=0; i<grantResults.length; i++)
            {
                //허용됬다면
                if(grantResults[i]== PackageManager.PERMISSION_GRANTED){
                }
                else {
                    Toast.makeText(getApplicationContext(),"앱권한설정하세요",Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }
    //////////////////>권한 설정
}
    /*Intent in = new Intent(MainActivity.this, AllalbumActivity.class);
    startActivity(in);*/