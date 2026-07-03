/*
 * Created by Maruf Alam Porosh on 3/28/21 11:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 3/28/21 11:10 PM
 */

package com.marufalam.childrenbooks.Activites;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.marufalam.childrenbooks.R;

public class SplashActivity extends AppCompatActivity {


    CountDownTimer countDownTimer;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

       MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.splashsound);


       mediaPlayer.start();

        countDownTimer = new CountDownTimer(3200,100) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                if (mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();

            }
        }.start();

    }
}