package com.luxand.bubble.mainActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.IdRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.luxand.bubble.referenceClass.EditCreateImg;
import com.luxand.bubble.R;
import com.luxand.bubble.referenceClass.GalleryActivity;
import com.luxand.bubble.referenceClass.searchRealTime;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;


public class BubblingMemoActivity extends GalleryActivity {
    AlertDialog.Builder builder;
    int folderState=0;
    String strPicFolder;

    Context context;
    Resources resources;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;    //this
        setContentView(R.layout.activity_bubblingmemo);
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
            //가운데 아이템이 활성화 되어있는 상태고 다시 눌렀을 때 선택한거 다사라짐
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
        builder = new AlertDialog.Builder(BubblingMemoActivity.this);

        final EditText input = new EditText(BubblingMemoActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        builder.setTitle("남길 메모를 작성하세요!");
        builder.setView(input);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                AlertDialog.Builder innBuilder = new AlertDialog.Builder( BubblingMemoActivity.this);
                innBuilder.setMessage(input.getText().toString());
                innBuilder.setTitle("선택한 사진 옆에\n메모가 생깁니다!");
                innBuilder.setCancelable(false);
                innBuilder .setPositiveButton( "확인", new DialogInterface.OnClickListener(){
                    public void onClick( DialogInterface dialog, int which) {
                        //여기서 메타데이터 수정 이벤트
                        //선택한 이미지의 키 값
                        int keyAt = gridAdapter.mSelectedItems.keyAt(0);
                        //미디어 스토어에 저장된 date 값이 실제 exif에 저장된 date 값과 다를 수 있기 때문에 진짜 값이 무엇인지 찾는 과정.
                        searchRealTime realTime = new searchRealTime(imageBitmapList.get(keyAt).getImageAbPate(),imageBitmapList.get(keyAt).getImageDate());
                        //memo는 새로운 이미지를 생성하고
                        //이미지를 찾아서 exif 에 입력하고
                        //그 이미지에 mediastore 를 변경함
                        new EditCreateImg(
                                imageBitmapList.get(keyAt).getImageName(),
                                mContext,
                                input.getText().toString(),
                                realTime.getRealDate(),
                                imageBitmapList.get(keyAt).getImageAbPate(),
                                realTime.getOrientation()
                        );
                        Toast.makeText(getApplicationContext(),input.getText().toString(), Toast.LENGTH_SHORT).show();
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
        bottomBar.setDefaultTab(R.id.tab_photo);
    }
}