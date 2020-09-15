package com.luxand.bubble;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.RequiresApi;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

//세 엑티비티 모두 갤러리 액티비티를 상속받고 있음 갤러리 액티비티를 통해 각 액티비티마다 새로운 사진 창을 갖는다는 것을 의미
public class AllalbumActivity extends GalleryActivity {
    public static int homeState=0;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        setContentView(R.layout.activity_allalbum);
        super.onCreate(savedInstanceState);

        folderSelectState = 0;

        //하단바 설정. 초기 하단바는 뒤로가기가 활성화 되어있음
        BottomBar bottomBar = (BottomBar)findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            //하단바 클릭했을때 이벤트
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_Home:

                        break;
                    case R.id.tab_photo:
                        Intent in = new Intent(AllalbumActivity.this, BubblingPhotoActivity.class);
                        in.putExtra("folderState", folderSelectState);
                        startActivity(in);
                        break;
                    case R.id.tab_folder:
                        Intent in2 = new Intent(AllalbumActivity.this, BubblingFolderActivity.class);
                        in2.putExtra("folderState", folderSelectState);
                        startActivity(in2);
                        break;
                }
            }
        });
        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            //하단바 다시 누를 경우는 뒤로가기 뿐
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if(R.id.tab_Home==tabId)
                    if(homeState==0) homeState+=1;
                    else {
                        homeState-=1;
                        finish();}
            }
        });
        createAndSetAdapter();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume(){
        super.onResume();
        BottomBar bottomBar = (BottomBar)findViewById(R.id.bottomBar);
        homeState=0;
        bottomBar.setDefaultTab(R.id.tab_Home);

    }
}
