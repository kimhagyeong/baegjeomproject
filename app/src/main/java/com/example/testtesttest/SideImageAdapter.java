package com.example.testtesttest;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;

public class SideImageAdapter extends RecyclerView.Adapter<SideImageAdapter.ViewHolder> {
    private int sideFolderSize = 10;
    private ArrayList<imageFolder> mData = null ;
    private Context mContext;
    // 아이템 뷰를 저장하는 뷰홀더 클래스.

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ViewHolder(View itemView) {
            super(itemView) ;
            // 뷰 객체에 대한 참조. (hold strong reference)
            imageView = itemView.findViewById(R.id.sidebar_source) ;
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams)
                    imageView.getLayoutParams();
            params.width = sideFolderSize;
            params.height = sideFolderSize;
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(2,2,2,2);
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    SideImageAdapter(ArrayList<imageFolder> list, Context context) {
        mData = list ;
        mContext = context;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public SideImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext() ;
        sideFolderSize = parent.getWidth();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        View view = inflater.inflate(R.layout.recyclerview_item_sidebar, parent, false) ;
        SideImageAdapter.ViewHolder vh = new SideImageAdapter.ViewHolder(view) ;
        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    // 클릭이벤트 여기야 여기
    @Override
    public void onBindViewHolder(SideImageAdapter.ViewHolder holder, int position) {
        imageFolder folder = mData.get(position) ;
        File image = new File(folder.getFirstPic());
        Glide.with(mContext)
                .load(image)
                .apply(new RequestOptions().centerCrop())
                .into(holder.imageView);
        final int t = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, String.format("%d 선택", t), Toast.LENGTH_SHORT).show();
                ((MainActivity)MainActivity.mContext).setFolderSelectState(t);
                ((MainActivity)MainActivity.mContext).setImageBitmapList();
            }
        });
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size() ;
    }


}