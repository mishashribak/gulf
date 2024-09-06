package CustomView;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.app.khaleeji.Fragments.VideoEditorFragment;
import com.app.khaleeji.R;

import java.io.IOException;

import CustomView.Video.CacheDataSourceFactory;

public class ExoPlayerHelper{

    private PlayerView playerView;
    private Context mContext;
    private SimpleExoPlayer exoPlayer;
    private ProgressiveMediaSource mediaSource;
    private String mStrUrl;
    private PlayerChangeListener playerChangeListener;
    private boolean isRepeated;
    private float currentVolume;
    private int mediaId;

    public ExoPlayerHelper(PlayerView playerView, Context context, String url){
        this.playerView = playerView;
        mContext = context;
        mStrUrl = url;
    }

    public ExoPlayerHelper(PlayerView playerView, Context context, String url, int mediaId){
        this.playerView = playerView;
        mContext = context;
        mStrUrl = url;
        this.mediaId = mediaId;
    }

    public void setMute(){
        if(exoPlayer != null){
            currentVolume = exoPlayer.getVolume();
            exoPlayer.setVolume(0f);
        }
    }

    public void setUnMute(){
        if(exoPlayer != null){
            exoPlayer.setVolume(currentVolume);
        }
    }

    public ExoPlayerHelper(PlayerView playerView, Context context, String url, boolean isRepeated){
        this.playerView = playerView;
        mContext = context;
        mStrUrl = url;
        this.isRepeated = isRepeated;
    }

    public interface PlayerChangeListener{
       void addChange(ExoPlayer exoPlayer, boolean playWhenReady, int playbackState);
    }

    public void initializePlayer() {
        exoPlayer = new SimpleExoPlayer.Builder(mContext).build();
        playerView.setPlayer(exoPlayer);
//        if(isRepeated){
            exoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
//            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
//            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
//            exoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
//        }

        exoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
               // playerChangeListener.addChange(exoPlayer, playWhenReady, playbackState);
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                if (error.type == ExoPlaybackException.TYPE_SOURCE) {
                    IOException cause = error.getSourceException();
                    if (cause instanceof HttpDataSource.HttpDataSourceException) {
                        // An HTTP error occurred.
                        HttpDataSource.HttpDataSourceException httpError = (HttpDataSource.HttpDataSourceException) cause;
                        // This is the request for which the error occurred.
                        DataSpec requestDataSpec = httpError.dataSpec;
                        // It's possible to find out more about the error both by casting and by
                        // querying the cause.
                        if (httpError instanceof HttpDataSource.InvalidResponseCodeException) {
                            // Cast to InvalidResponseCodeException and retrieve the response code,
                            // message and headers.
                        } else {
                            // Try calling httpError.getCause() to retrieve the underlying cause,
                            // although note that it may be null.
                        }
                    }
                }
            }
        });

        playerView.setPlayer(exoPlayer);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext,
                Util.getUserAgent(mContext, mContext.getString(R.string.app_name)));
        mediaSource = new ProgressiveMediaSource.Factory(new CacheDataSourceFactory(mContext, 100 * 1024 * 1024, 5 * 1024 * 1024, this.mediaId)).createMediaSource(Uri.parse(mStrUrl));
        exoPlayer.prepare(mediaSource, true, false);
        exoPlayer.setPlayWhenReady(true);
    }

    public void killPlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
            mediaSource = null;
            playerView  = null;
        }
    }

    public void pausePlayer(){
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.getPlaybackState();
    }
    public void startPlayer(){
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.getPlaybackState();
    }

    public void stopPlayer(){
        if(exoPlayer != null){
            exoPlayer.stop(true);
        }
    }

}
