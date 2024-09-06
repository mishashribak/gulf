package CustomView.Video;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.app.khaleeji.R;

abstract public class VideoPlayerEventListener implements ExoPlayer.EventListener {
    public ProgressBar progressBar;
    public SimpleExoPlayer exoPlayer;
    public RelativeLayout rlThumb;
    public PlayerView playerView;
    public int position;

    public VideoPlayerEventListener( SimpleExoPlayer exoPlayer, RelativeLayout rlThumb, int position){
        this.exoPlayer = exoPlayer;
        this.rlThumb = rlThumb;
        this.position = position;
    }

    public VideoPlayerEventListener( SimpleExoPlayer exoPlayer, RelativeLayout rlThumb){
        this.exoPlayer = exoPlayer;
        this.rlThumb = rlThumb;
    }

    public VideoPlayerEventListener(SimpleExoPlayer exoPlayer, ProgressBar progressBar, int position){
        this.progressBar = progressBar;
        this.exoPlayer = exoPlayer;
        this.position = position;
    }
}
