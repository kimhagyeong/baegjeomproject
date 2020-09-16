package com.luxand.bubble.referenceClass;

import android.media.ExifInterface;
import android.util.Log;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class searchRealTime {
    private String realDate;

    public searchRealTime(String abPath,String MediaStoreDate ){



        String exifDate="null";

        try {
            ExifInterface exif = new ExifInterface(abPath);
            String tmpStr = exif.getAttribute(ExifInterface.TAG_DATETIME)+"";

            if(!tmpStr.equals("null")){
                Log.d("tmpStr1",tmpStr);
                SimpleDateFormat transFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                Date to = transFormat.parse(tmpStr);
                exifDate=to.getTime()+"";
            }
            else{
                exifDate="null";
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        String date = "null";
        if(exifDate.equals("null")){
            date=MediaStoreDate;
        }
        else{
            Long tmpD = Long.parseLong(exifDate)/1000000000;
            Long tmpDate = Long.parseLong(exifDate);

            if(tmpD<=1){
                tmpDate = Long.parseLong(exifDate)*1000;
            }

            date=Long.toString(tmpDate);
        }
        realDate = date;

    }
    public String getRealDate(){return realDate;}
}
