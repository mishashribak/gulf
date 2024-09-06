package CustomView.Video;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.app.khaleeji.Adapter.DailyMemoryAdapter;
import com.app.khaleeji.Adapter.FriendPostsAdapter;
import com.app.khaleeji.Adapter.MyPostsAdapter;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.MemoryModel;
import com.app.khaleeji.Response.fetchMemoryPackage.Memory;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Constants.Bundle_Identifier;
import CustomView.ExoPlayerHelper;
import Utility.ApiClass;

public class PostsFriendVideoPlayerRecyclerView extends RecyclerView {

    private static final String TAG = "PostsFriendVideoPlayerRecyclerView";

    private List<MemoryModel> memoryModels = new ArrayList<>();
    private  ExoPlayerData[] mPlayer;
    private Context context;
    public int playPosition = -1;

    public PostsFriendVideoPlayerRecyclerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public PostsFriendVideoPlayerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setData(ExoPlayerData[] data){
        mPlayer = data;
    }


    private void init(Context context){
        this.context = context.getApplicationContext();

        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(memoryModels != null && memoryModels.size()>0){
                        if(!recyclerView.canScrollVertically(1)){
                            playVideo(true);
                        }
                        else{
                            playVideo(false);
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }

    int targetPosition;
    public void playVideo(boolean isEndOfList) {

        if(!isEndOfList){
            int startPosition = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
            int endPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();

            // if there is more than 2 list-items on the screen, set the difference to be 1
            if (endPosition - startPosition > 1) {
                endPosition = startPosition + 1;
            }

            // something is wrong. return.
            if (startPosition != endPosition) {

                LinearLayoutManager layoutManager = ((LinearLayoutManager) getLayoutManager());

                final int firstPosition = layoutManager.findFirstVisibleItemPosition();
                final int lastPosition = layoutManager.findLastVisibleItemPosition();

                Rect rvRect = new Rect();
                getGlobalVisibleRect(rvRect);

                int maxHeight=0;
                for (int i = firstPosition; i <= lastPosition; i++) {
                    Rect rowRect = new Rect();
                    layoutManager.findViewByPosition(i).getGlobalVisibleRect(rowRect);

                    int visibleHeightFirst;
                    if (rowRect.bottom >= rvRect.bottom){
                        visibleHeightFirst =rvRect.bottom - rowRect.top;

                    }else {
                        visibleHeightFirst = rowRect.bottom - rvRect.top;

                    }
                    if(visibleHeightFirst > maxHeight){
                        maxHeight = visibleHeightFirst;
                        targetPosition = i;
                    }

                }
            }
            else {
                targetPosition = startPosition;
            }
        }
        else{
            targetPosition = memoryModels.size()-1 ;
        }


        // video is already playing so return
        if (targetPosition == playPosition) {
//            if (mPlayer == null || mPlayer[targetPosition-1] == null || mPlayer[targetPosition-1].exoPlayer == null) {
//                return;
//            }
//
//            if(mPlayer[targetPosition-1].exoPlayer.getPlaybackState() == Player.STATE_IDLE){
//                mPlayer[targetPosition-1].exoPlayer.setPlayWhenReady(true);
//            }
            return;
        }

        resetVideoView(playPosition);
        playPosition = targetPosition;

        if (mPlayer == null || mPlayer[targetPosition] == null || mPlayer[targetPosition].exoPlayer == null) {
            playPosition = -1;
            return;
        }

        mPlayer[targetPosition].exoPlayer.setPlayWhenReady(true);
    }


    private void resetVideoView(int pos){
        if(mPlayer == null)
            return;
        if(pos != -1 && memoryModels.get(pos).getMediaType().equalsIgnoreCase("video")) {
            if (mPlayer[pos] == null || mPlayer[pos].exoPlayer == null)
                return;
            mPlayer[pos].exoPlayer.setPlayWhenReady(false);
        }

    }

    public void setMemoryModels(List<MemoryModel> memoryModels){
        this.memoryModels = memoryModels;
    }

    public void stopVideo(){
        if(mPlayer != null){
            for(int i=0; i< mPlayer.length; i++){
                if(mPlayer[i] != null && mPlayer[i].exoPlayer != null){
                    mPlayer[i].exoPlayer.setPlayWhenReady(false);
                    mPlayer[i].exoPlayer.release();
                }
            }
            targetPosition = -1;
        }
    }

    public void pauseVideo(){
        if(mPlayer != null){
            for(int i=0; i< mPlayer.length; i++){
                if(mPlayer[i] != null && mPlayer[i].exoPlayer != null){
                    mPlayer[i].exoPlayer.setPlayWhenReady(false);
                }
            }
            targetPosition = -1;
        }
    }
}
