package com.luxand.bubble;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;

public class SplashActivity extends Activity {

    SoundPool soundf;
    int tom;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        soundf = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
       // tom = soundf.load(this, R.raw.test, 1);

        try{
            Thread.sleep(500);

            soundf.play(tom,1,1,0,0,1);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

}

