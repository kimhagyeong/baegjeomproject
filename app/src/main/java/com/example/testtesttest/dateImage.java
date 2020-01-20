package com.example.testtesttest;

import android.net.Uri;

import java.io.File;

//주소와 시간, 위치 정보 등을 컨트롤 하는 클래스
public class dateImage{
    private Uri path;
    private String date;
    private String name;
    public dateImage(Uri path, String date, String name) {
        this.path = path;
        this.date= date;
        this.name= name;
    }
    public Uri getImagePath() {return path;}
    public String getImageDate() {return date;}
    public String getImageName() {return name;}
    public int compareTo(dateImage a, String rules, boolean ascDesc){
        switch (rules) {
            case "name":
                String thisName = this.name;
                String thatName = a.getImageName();
                return ascDesc?thisName.compareTo(thatName):thatName.compareTo(thisName);
            case "date":
                return ascDesc?this.date.compareTo(a.date):a.date.compareTo(this.date);
            default:
                return 0;
        }
    }
}