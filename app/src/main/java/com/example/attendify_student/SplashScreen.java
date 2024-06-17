package com.example.attendify_student;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;


public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ScalableVideoView videoPlayer = findViewById(R.id.videoPlayer);

        String path = "android.resource://" + getPackageName() + "/" + R.raw.attendify_animation_trim;

        videoPlayer.setVideoURI(Uri.parse(path));
        videoPlayer.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = getSharedPreferences("login",MODE_PRIVATE);
                if(pref.getBoolean("flag",false)){
                        String id = pref.getString("id","-1");
                        StudentInstance.setId(id);
                        startActivity(new Intent(getApplicationContext(), StudentHome.class));
                        finish();
                }
                else {

                    startActivity(new Intent(getApplicationContext(), StudentLogin.class));
                    finish();
                }
            }
        },7000);


    }
}