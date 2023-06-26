package com.example.cropsclassification;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cropsclassification.databinding.ActivityGsapBinding;

public class GsapActivity extends AppCompatActivity {

    ActivityGsapBinding activityGsapBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityGsapBinding = ActivityGsapBinding.inflate(getLayoutInflater());
        setContentView(activityGsapBinding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        activityGsapBinding.webView.getSettings().setJavaScriptEnabled(true); // Enable JavaScript (optional)

        // Load an HTML file from assets folder
        activityGsapBinding.webView.loadUrl("file:///android_asset/gsap.html");

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
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void startApp(){
        Intent intent = new Intent(GsapActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}