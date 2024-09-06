package CustomView.Video;

import com.google.android.exoplayer2.SimpleExoPlayer;

public class ExoPlayerData {
    public SimpleExoPlayer exoPlayer;
    public boolean isFirst;

    public ExoPlayerData( SimpleExoPlayer exoPlayer, boolean isFirst){
        this.exoPlayer = exoPlayer;
        this.isFirst = isFirst;
    }
}
