package com.app.khaleeji.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.app.khaleeji.R;

import CustomView.ExoPlayerHelper;


public class VideoPlayerActivity extends AppCompatActivity {
    private com.google.android.exoplayer2.ui.PlayerView videoView;
    private ExoPlayerHelper exoPlayerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        initView();
    }

    private void initView() {
        videoView = findViewById(R.id.player);
        exoPlayerHelper = new ExoPlayerHelper(videoView, this, getIntent().getStringExtra("videoUrl"));
        exoPlayerHelper.initializePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        exoPlayerHelper.killPlayer();
    }

}
