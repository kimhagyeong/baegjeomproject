package com.luxand.bubble.mainActivity;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.luxand.bubble.referenceClass.EditExif;
import com.luxand.bubble.referenceClass.EditMediaStore;
import com.luxand.bubble.R;
import com.luxand.bubble.referenceClass.GalleryActivity;
import com.luxand.bubble.referenceClass.searchRealTime;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

public class BubblingPhotoActivity extends GalleryActivity {
    AlertDialog.Builder builder;
    int photoState=0;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected void onCreate(Bundle savedInstanceState) {
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
                // 선택한 이미지는 총 2장이 되어야 동작하며 마지막에 선택한 이미지가 무엇인지 찾아야 함.
                // 그래야 마지막에 선택한 이미지의 메타데이터를 복사하여 첫번째에 선택한 이미지에 넣음.
                if(gridAdapter.lastAbPath!=-1&&gridAdapter.mSelectedItems.size()==2) {
                        Uri editPath;
                        String editAbPath;

                        Uri targetPath;
                        String targetAbPath;

                        String name;
                        String date;
                        String targetName;
                        String targetPath_Date;

                        Boolean isTakenCamera=false;

                        // key 값이 뒤에 있는 것이 나중에 선택한 사진일때
                        if(gridAdapter.lastAbPath==gridAdapter.mSelectedItems.keyAt(1)){
                            //0번째 사진이 1번째 사진 옆에 배치
                            editPath = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(0)).getImagePath();
                            editAbPath = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(0)).getImageAbPate();

                            targetPath = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(1)).getImagePath();
                            targetAbPath = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(1)).getImageAbPate();

                            name=imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(0)).getImageName();
                            targetName = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(1)).getImageName();

                            date=imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(0)).getImageDate();
                            targetPath_Date = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(1)).getImageDate();

                        }
                        else{
                            editPath = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(1)).getImagePath();
                            editAbPath = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(1)).getImageAbPate();

                            targetPath = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(0)).getImagePath();
                            targetAbPath = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(0)).getImageAbPate();

                            name=imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(1)).getImageName();
                            targetName = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(0)).getImageName();

                            date=imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(1)).getImageDate();
                            targetPath_Date = imageBitmapList.get(gridAdapter.mSelectedItems.keyAt(0)).getImageDate();
                        }

                        /////////Exif 변경/////////
                        /////////Exif 변경되지 않으면 새로운 이미지가 형성되고 아래있는 MEDIASTORE가 동작하지 않는다.
                        EditExif setExif = new EditExif(editAbPath,targetPath_Date,editPath,mContext,targetAbPath,name,targetName);
                        isTakenCamera = setExif.startEditExif();



                        ////////////////mediastore 변경////////////////////
                        if(!isTakenCamera){
                            searchRealTime realTime = new searchRealTime(targetAbPath, targetPath_Date);
                            new EditMediaStore(editPath,targetName,realTime.getRealDate(),editAbPath,targetAbPath,mContext);
                        }
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