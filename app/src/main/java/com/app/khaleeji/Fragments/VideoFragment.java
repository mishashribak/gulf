package com.app.khaleeji.Fragments;


import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.app.khaleeji.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

public class VideoFragment extends BaseFragment implements View.OnClickListener {
    private SimpleExoPlayer exoPlayer;
    private SimpleExoPlayerView simpleExoPlayerView;
    private View view;
    private Button btnSkip;
    private ImageView imgBack;
    private boolean isFromSignUp;
    private List<String> videosList = new ArrayList<>();
    private int currentPlayingVideoIndex;
    private Button btnPrevious;
    private String userName;

    public VideoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_video, container, false);
        isFromSignUp = getArguments().getBoolean("isFromSignUp");
        userName = getArguments().getString("userName");
        initViews();
        setExoPlayer();
        setListener();
        return view;
    }

    private void setListener() {
        exoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest,int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                switch (playbackState) {
                    case Player.STATE_BUFFERING:
                        break;
                    case Player.STATE_ENDED:
                        if (isFromSignUp) {
                            playNextVideo(false);
                        } else {
                            stopExoplayer();
                        }

                        break;
                    case Player.STATE_IDLE:
                        break;

                    case Player.STATE_READY:
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });


    }

    private void initViews() {
        simpleExoPlayerView = view.findViewById(R.id.player);
        btnSkip = view.findViewById(R.id.btn_skip);
        imgBack = view.findViewById(R.id.imgBack);
        btnPrevious = view.findViewById(R.id.btn_previous);
        btnSkip.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        videosList.clear();
        videosList.add("file:///android_asset/Part1.mp4");
        videosList.add("file:///android_asset/Part2.mp4");
        videosList.add("file:///android_asset/Part3.mp4");
//        if (isFromSignUp) {
//            btnPrevious.setVisibility(View.VISIBLE);
//        } else {
//            btnPrevious.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        exoPlayer.setPlayWhenReady(false);
    }

    private void setExoPlayer() {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        final TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
// 2. Create a default_img LoadControl
        LoadControl loadControl = new DefaultLoadControl();
// 3. Create the player
        exoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
        //  simpleExoPlayerView = findViewById(R.id.player_view);
        simpleExoPlayerView.setPlayer(exoPlayer);
        simpleExoPlayerView.setUseController(false);
        if (isFromSignUp) {
            playLiveTv(videosList.get(0));
            currentPlayingVideoIndex = 0;
        } else {
            playLiveTv("file:///android_asset/gulf_app_full.mp4");
        }
    }

    private void playLiveTv(String songPath) {
        try {
            Uri uri = Uri.parse(songPath);
            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
// Produces DataSource instances through which media data is loaded.
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mActivity,
                    Util.getUserAgent(getActivity(), "yourApplicationName"), bandwidthMeter);
// Produces Extractor instances for parsing the media data.
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
// This is the MediaSource representing the media to be played.
            MediaSource videoSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
            exoPlayer.prepare(videoSource);
            exoPlayer.setPlayWhenReady(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_skip:
                if (isFromSignUp) {
                    playNextVideo(true);
                } else {
                    stopExoplayer();
                }
                break;
            case R.id.imgBack:
                stopExoplayer();
                break;
            case R.id.btn_previous:
                playPreviousVideo();
                break;
        }
    }

    private void openProfileEditorFragment() {
//        Bundle bundle = new Bundle();
//        bundle.putBoolean("isFromSignUp", true);
//        bundle.putString("userName", userName);
//        replaceFragment(new EditMeetupprofileFragment(), bundle, R.id.framelayout_login);
    }

    public void replaceFragment(Fragment fragment, Bundle bundle, int layout) {
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(layout, fragment);
//        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void stopExoplayer() {
        if (isFromSignUp) {
            openProfileEditorFragment();
        } else {
            getActivity().onBackPressed();
        }
    }

    private void playNextVideo(boolean isSkipButton) {
        if (currentPlayingVideoIndex >= 2) {
            stopExoplayer();
            return;
        }
        if (isSkipButton) {
            exoPlayer.setPlayWhenReady(false);
        }
        currentPlayingVideoIndex = currentPlayingVideoIndex + 1;
        playLiveTv(videosList.get(currentPlayingVideoIndex));
    }

    private void playPreviousVideo() {

        if (currentPlayingVideoIndex == 0) {

            return;
        }

        exoPlayer.setPlayWhenReady(false);
        currentPlayingVideoIndex = currentPlayingVideoIndex - 1;
        playLiveTv(videosList.get(currentPlayingVideoIndex));
    }

}
