package com.example.cropsclassification;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cropsclassification.databinding.ActivityEmbedVideoBinding;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;

public class EmbedVideo extends AppCompatActivity {

    ActivityEmbedVideoBinding activityEmbedVideoBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityEmbedVideoBinding = ActivityEmbedVideoBinding.inflate(getLayoutInflater());
        setContentView(activityEmbedVideoBinding.getRoot());
        getSupportActionBar().setTitle("Video Player");


        activityEmbedVideoBinding.youtubeVideo.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = "cUWTvbn7eCI";
                youTubePlayer.cueVideo(videoId, 0);
            }
        });


    }
}