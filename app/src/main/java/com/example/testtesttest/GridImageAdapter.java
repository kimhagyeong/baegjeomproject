package com.example.testtesttest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
//이미지들을 격자로 출력하는 클래스
public class GridImageAdapter extends RecyclerView.Adapter<GridImageAdapter.ViewHolder> {

    private int chipSize = 10;
    private ArrayList<dateImage> imageBitmapList;
    private Context context;

    //선택된 아이템들 저장하는 클래스
    public SparseBooleanArray mSelectedItems=new SparseBooleanArray(0);
    String strr;
    public int total=0;
    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ViewHolder(View itemView) {
            super(itemView) ;
            // 뷰 객체에 대한 참조. (hold strong reference)
            imageView = itemView.findViewById(R.id.chipImageView) ;
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams)
                    imageView.getLayoutParams();
            params.width = chipSize;
            params.height = chipSize;
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(2,2,2,2);
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    GridImageAdapter(ArrayList<dateImage> list, Context c) {
        imageBitmapList = list ;
        chipSize = 10;
        context = c;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public GridImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        chipSize = parent.getWidth()/3;
        View view = inflater.inflate(R.layout.recyclerview_item_gridimage, parent, false) ;
        GridImageAdapter.ViewHolder vh = new GridImageAdapter.ViewHolder(view) ;

        return vh ;
    }


    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int t = position;
        //선택된 녀석만 동그라미에 회색 필터
        if ( mSelectedItems.get(position, false) ){
            holder.imageView.setColorFilter((R.color.white), PorterDuff.Mode.SCREEN);
            holder.imageView.setBackground(new ShapeDrawable(new OvalShape()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.imageView.setClipToOutline(true);
            }
        } else {
            holder.imageView.setColorFilter(null);
            holder.imageView.setBackground(new ShapeDrawable(new RectShape()));
            holder.imageView.setBackgroundColor(Color.WHITE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.imageView.setClipToOutline(true);
            }
        }
        Uri imgPath = imageBitmapList.get(position).getImagePath();
        Glide.with(context)
                .load(imgPath)
                .thumbnail(0.5f)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, String.format("%d 선택", t), Toast.LENGTH_SHORT).show();
                //아이템 클릭시 선택한 녀석만 포지션을 저장해서 noti 시켜가지구 다시 바인딩 할 수 있도록
                strr=((GalleryActivity)context).str;
                toggleItemSelected(t);
            }
        });
    }

    public void restoreSelected() {
        if (mSelectedItems.size() != 0) {
            int size = ((GalleryActivity)GalleryActivity.mContext).imageBitmapList.size();
            for (int i = size -1; i > -1 ; i--) {
                if(mSelectedItems.get(i)){
                    toggleItemSelected(i);
                    toggleItemSelected(i);
                }
            }
        }
    }

    private void toggleItemSelected(int position){
        if(strr.equals("All Album")){
            Intent img= new Intent(context,PhotoPopupActivity.class);
            img.putExtra("path",imageBitmapList.get(position).getImagePath().toString());
            img.putExtra("date",imageBitmapList.get(position).getImageDate());
//            Uri uu=imageBitmapList.get(position).getImagePath();
//            Log.e("test1",uu.toString());
            context.startActivity(img);
            notifyItemChanged(position);
        }
        else if(strr.equals("Photo")) {
            if (mSelectedItems.get(position, false)) {
                mSelectedItems.delete(position);
                notifyItemChanged(position);
                total -= 1;
            } else {
                if (total < 2) {
                    mSelectedItems.put(position, true);
                    notifyItemChanged(position);
                    total += 1;
                }
            }
        }
        else if(strr.equals("Memo")) {
            if (mSelectedItems.get(position, false)) {
                mSelectedItems.delete(position);
                notifyItemChanged(position);
                total -= 1;
            } else {
                if (total < 1) {
                    mSelectedItems.put(position, true);
                    notifyItemChanged(position);
                    total += 1;
                }
            }
//            Log.d("test1",mSelectedItems.toString());
//            Log.e("test1",imageBitmapList.get(position).getImagePath().toString());
        }
        else{
            if (mSelectedItems.get(position, false)) {
                mSelectedItems.delete(position);
                notifyItemChanged(position);
                total -= 1;
            } else {
                mSelectedItems.put(position, true);
                notifyItemChanged(position);
                total += 1;
            }
        }
        ((GalleryActivity)GalleryActivity.mContext).setVisibleMenu();
    }

    private boolean isItemSelected(int position) {
        return mSelectedItems.get(position, false);
    }

    public void clearSelectedItem() {
        int position;
        for (int i = 0; i < mSelectedItems.size(); i++) {
            position = mSelectedItems.keyAt(i);
            mSelectedItems.put(position, false);
            total-=1;
            notifyItemChanged(position);
            ((GalleryActivity)GalleryActivity.mContext).setVisibleMenu();
        }
        mSelectedItems.clear();

    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return imageBitmapList.size() ;
    }
}
