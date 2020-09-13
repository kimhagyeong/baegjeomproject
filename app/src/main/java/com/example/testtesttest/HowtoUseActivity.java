package com.example.testtesttest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.roughike.bottombar.BottomBar;

public class HowtoUseActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.how_to_use);
        // 화면 전환 - 인텐트 날리기 (startActivity)
        //     1. 다음 넘어갈 화면을 준비한다 (layout xml, java)
        //    2. AndroidManifest.xml 에 Activity 를 등록한다
        //    3. Intent 객체를 만들어서 startActivity 한다

        Button bb = (Button) findViewById(R.id.how_back);
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        MainActivity.class); // 다음 넘어갈 클래스 지정
                //intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent); // 다음 화면으로 넘어간다
            }
        });

        Button a = (Button) findViewById(R.id.howphoto);
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  CustomDialog customDialog = new CustomDialog(HowtoUseActivity.this);
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        HowtoPhotoActivity.class); // 다음 넘어갈 클래스 지정
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent); // 다음 화면으로 넘어간다
           }
        });

        Button b = (Button) findViewById(R.id.howgallery);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        HowtoGalleryActivity.class); // 다음 넘어갈 클래스 지정
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent); // 다음 화면으로 넘어간다
            }
        });

        Button c = (Button) findViewById(R.id.howfolder);
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        HowtoFolderActivity.class); // 다음 넘어갈 클래스 지정
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent); // 다음 화면으로 넘어간다
            }
        });

        Button d = (Button) findViewById(R.id.howmemo);
        d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        HowtoMemoActivity.class); // 다음 넘어갈 클래스 지정
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent); // 다음 화면으로 넘어간다
            }
        });

        Button e = (Button) findViewById(R.id.howface);
        e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        HowtoFaceActivity.class); // 다음 넘어갈 클래스 지정
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent); // 다음 화면으로 넘어간다
            }
        });

    } // end onCreate()

    /*
    //레이아웃을 위에 겹쳐서 올리는 부분
    LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    //레이아웃 객체생성
    LinearLayout ll = (LinearLayout)inflater.inflate(R.layout.how_to_gallery, null);
    //레이아웃 배경 투명도 주기
    ll.setBackgroundColor(Color.parseColor("#99000000"));

    //레이아웃 위에 겹치기
    LinearLayout.LayoutParams paramll = new LinearLayout.LayoutParams
            (LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
    addContentView(ll, paramll);
    //위에겹친 레이아웃에 온클릭 이벤트주기
    ll.setOnClickListener(writeListener);
*/
}