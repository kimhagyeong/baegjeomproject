package com.luxand.bubble;

import android.net.Uri;

//주소와 시간, 위치 정보 등을 컨트롤 하는 클래스
public class dateImage{
    private Uri path;
    private String date;
    private String name;
    private String abPate;
    public dateImage(Uri path, String date, String name, String ab) {
        this.path = path;
        this.date= date;
        this.name= name;
        this.abPate = ab;
    }
    public Uri getImagePath() {return path;}
    public String getImageDate() {return date;}
    public String getImageName() {return name;}
    public String getImageAbPate() {return abPate;}
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