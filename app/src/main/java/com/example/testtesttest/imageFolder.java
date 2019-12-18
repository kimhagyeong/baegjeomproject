package com.example.testtesttest;

import java.util.ArrayList;

public class imageFolder {
    private  String path;
    private  String FolderName;
    private int numberOfPics = 0;
    private dateImage firstPic;
    public ArrayList<dateImage> picPathList;

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
