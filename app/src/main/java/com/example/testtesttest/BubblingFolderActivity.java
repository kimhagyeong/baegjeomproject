package com.example.testtesttest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.IdRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.File;


public class BubblingFolderActivity extends GalleryActivity {
    AlertDialog.Builder builder;
    int folderState=0;
    String strPicFolder;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;    //this
        setContentView(R.layout.activity_bubblingfolder);
        super.onCreate(savedInstanceState);
        folderSelectState = getIntent().getIntExtra("folderState",0);

        BottomBar bottomBar = (BottomBar)findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab_folder);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_Home:
                        finish();
                        break;
                    case R.id.tab_folder:

                        break;
                    case R.id.tab_next:
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                        break;
                }
            }
        });
        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            //가운데 아이템이 활성화 되어있는 상태고 다시 눌렀을 때 선택한거 다사라짐
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if(tabId==R.id.tab_folder){
                    gridAdapter.clearSelectedItem();
                }else if(tabId==R.id.tab_next){
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        createAndSetAdapter();
        builder = new AlertDialog.Builder(BubblingFolderActivity.this);
//        final String[] items = {"Apple", "Banana", "Orange", "Grapes"};
        final String[] items = publicfolderNames.toArray(new String[publicfolderNames.size()]);

        builder.setTitle("Let's go Bubbling!")
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        strPicFolder=items[which];
                        Toast.makeText(getApplicationContext(), items[which]+ " is clicked", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
               AlertDialog.Builder innBuilder = new AlertDialog.Builder( BubblingFolderActivity.this);
               innBuilder.setMessage("이 폴더로 모든 사진을 이동합니다");
               innBuilder.setTitle(strPicFolder);
               innBuilder .setPositiveButton( "확인", new DialogInterface.OnClickListener(){
                   public void onClick( DialogInterface dialog, int which) {

                       //여기서 메타데이터 수정 이벤트
//                       for(int i=0;i<gridAdapter.mSelectedItems.size();i++){
//                           String editTime = imageBitmapList.get(i).getImageDate();
//                           String editName = imageBitmapList.get(i).getImageName();
//                           Uri editPath = imageBitmapList.get(i).getImagePath();
//                           String editAbPath = imageBitmapList.get(i).getImageAbPate();
//
//                           ContentValues values = new ContentValues();
//                           ContentResolver resolver = mContext.getContentResolver();
//
//                           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                               values.put(MediaStore.Images.Media.IS_PENDING, 1);
//                               int update = resolver.update(editPath, values, null, null);
//                               values.clear();
//                           }
//
//                           //이름 바꾸기
//                           String title = editTime+"_"+strPicFolder;
//                           String tag = editName.substring(editName.lastIndexOf("."));
//                           values.put(MediaStore.Images.Media.TITLE, title + tag);
//                           values.put(MediaStore.Images.Media.DISPLAY_NAME, title + tag);
//
////                           날짜 바꾸기
//                           Long tmp_targetPath_Date = Long.parseLong(editTime) / 1000 + 1;
//                           String tmp_str_targetPath = Long.toString(tmp_targetPath_Date);
//                           values.put(MediaStore.Images.Media.DATE_ADDED, tmp_str_targetPath);
//                           values.put(MediaStore.Images.Media.DATE_TAKEN, tmp_str_targetPath);
//                           values.put(MediaStore.Images.Media.DATE_MODIFIED, tmp_str_targetPath);

                           //폴더 바꾸기 폴더를 어떻게 찾지?
                           File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),strPicFolder);
                           if (folder.exists()) {
                               Log.e("folderPath","여기 있네요");
                               Log.d("folderPath",Environment.DIRECTORY_DCIM);
                               Log.d("folderPath",Environment.DIRECTORY_DOCUMENTS);

//                               values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + strPicFolder);
                           }
//                           else{
//                               folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),strPicFolder);
//                               if (folder.exists()) {
//                                   values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + strPicFolder);
//                               }
//                               else{
//                                   folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),strPicFolder);
//                                   if (folder.exists()) {
//                                       values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + strPicFolder);
//                                   }
//                                   else{
//                                       Toast.makeText(getApplicationContext(), "폴더가 범위 밖에 있어요", Toast.LENGTH_SHORT).show();
//                                   }
//                               }
//                           }


//                           Boolean isExist = file.
//                           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                               if (targetAbPath.indexOf("DCIM") >= 0) {
//                                   String subFolder = targetAbPath.substring(targetAbPath.indexOf("DCIM") + 4, targetAbPath.lastIndexOf("/"));
//                                   Log.e("DCIMTest1", subFolder);
//                                   values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + subFolder);
//                               } else if (targetAbPath.indexOf("Pictures") >= 0) {
//                                   String subFolder = targetAbPath.substring(targetAbPath.indexOf("Pictures") + 8, targetAbPath.lastIndexOf("/"));
//                                   Log.d("DCIMTest2", subFolder);
//                                   values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + subFolder);
//                               } else if (targetAbPath.indexOf("Download") >= 0) {
//                                   String subFolder = targetAbPath.substring(targetAbPath.indexOf("Download") + 8, targetAbPath.lastIndexOf("/"));
//                                   Log.d("DCIMTest3", subFolder);
//                                   values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + subFolder);
//                               } else {
//
//                               }
//                           }
//                           else{
//                               Toast.makeText(getApplicationContext(), "안드로이드 버전 10 미만에서는 폴더 이동이 되지 않습니다.", Toast.LENGTH_SHORT).show();
//                           }

//                           int update2 = resolver.update(editPath, values, null, null);
//
//                           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                               values.clear();
//                               values.put(MediaStore.Images.Media.IS_PENDING, 0);
//                               int update3 = resolver.update(editPath, values, null, null);
//                           }
//                           new SingleMediaScanner(mContext, editAbPath);
//
//
//                       }

                       dialog.dismiss();
                       finish();
                   }
               }); innBuilder.show();


            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume(){
        super.onResume();
        BottomBar bottomBar = (BottomBar)findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab_folder);
    }
}