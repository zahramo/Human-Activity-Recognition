package com.example.activityrecognition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

public class VideoPlayActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        playVideo();
    }

    public void playVideo() {
        videoView = findViewById(R.id._videoView);
        Uri videoUri = Uri.parse(getIntent().getExtras().getString("videoUri"));
        videoView.setVideoURI(videoUri);
        videoView.start();
    }

    public void recognizeAction(View view) {
        Intent playIntent = new Intent(this, RecognizeActionActivity.class);
        startActivity(playIntent);
    }
}
