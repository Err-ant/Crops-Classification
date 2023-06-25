package com.example.cropsclassification;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cropsclassification.databinding.ActivityWelcomeBinding;

public class WelcomeActivity extends AppCompatActivity {

    ActivityWelcomeBinding activityWelcomeBinding;
    private int pr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityWelcomeBinding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(activityWelcomeBinding.getRoot());
        //getSupportActionBar().setTitle("Welcome");
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();
                startApp();
            }
        });
        thread.start();

    }
    public void doWork(){
        for(pr=20; pr<=100;pr+=20){
            try {
                Thread.sleep(500);
                activityWelcomeBinding.progressBarId.setProgress(pr);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void startApp(){
        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}