package com.example.testtesttest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
@RequiresApi(api = Build.VERSION_CODES.M)

public class HowtoMemoActivity extends HowtoUseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.howtomemo);  // layout xml 과 자바파일을 연결

        Button a = (Button) findViewById(R.id.how_close);
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        HowtoUseActivity.class); // 다음 넘어갈 클래스 지정
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent); // 다음 화면으로 넘어간다
            }
        });
    } // end onCreate()

} // end MyTwo
