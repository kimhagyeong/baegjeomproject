package com.example.testtesttest;

import android.util.Log;
import android.util.SparseBooleanArray;

import java.util.ArrayList;

public class imageFolder implements Cloneable{
    private  String path;
    private  String FolderName;
    private int numberOfPics = 0;
    private dateImage firstPic;
    public SparseBooleanArray mSelectedItems = new SparseBooleanArray(0);
    public ArrayList<dateImage> picPathList;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public imageFolder(){
        picPathList = new ArrayList<>();
    }

    public String getPath() {
            return path;
        }
    public void setPath(String path) {
            this.path = path;
        }

    public String getFolderName() {
            return FolderName;
        }

    public void setFolderName(String folderName) {
            FolderName = folderName;
        }

    public int getNumberOfPics() {
        return numberOfPics;
    }

    public void addPics(String p, String d){
        this.numberOfPics++;
        picPathList.add(new dateImage(p,d));
    }

    public String getFirstPic() {
            return firstPic.getImagePath();
        }
    public void setFirstPic(dateImage firstPic) {
        this.firstPic = firstPic;
    }
    public void setFirstPic() {
        this.firstPic = picPathList.get(0);
    }
}
