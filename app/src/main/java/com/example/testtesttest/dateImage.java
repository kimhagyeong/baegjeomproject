package com.example.testtesttest;

import java.io.File;

public class dateImage{
    private String path;
    private String date;
    public dateImage(String p, String d) {
        this.path = p;
        this.date = d;
    }
    public String getImagePath() {return path;}
    public String getImageDate() {return date;}
    public int compareTo(dateImage a, String rules, boolean ascDesc){
        switch (rules) {
            case "name":
                String thisName = new File(path).getName();
                String thatName = new File(a.path).getName();
                return ascDesc?thisName.compareTo(thatName):thatName.compareTo(thisName);
            case "date":
                return ascDesc?this.date.compareTo(a.date):a.date.compareTo(this.date);
            default:
                return 0;
        }
    }
}