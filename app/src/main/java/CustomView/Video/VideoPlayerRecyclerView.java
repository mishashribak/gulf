package CustomView.Video;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.app.khaleeji.Adapter.DailyMemoryAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.MemoryModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.exoplayer2.Player.STATE_READY;

public class VideoPlayerRecyclerView extends RecyclerView {

    private static final String TAG = "VideoPlayerRecyclerView";
    private  ExoPlayerData[] mPlayer;

    // vars
    private List<MemoryModel> memoryModels = new ArrayList<>();
    private Context context;
    public int playPosition = -1;

    public VideoPlayerRecyclerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public VideoPlayerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        this.context = context.getApplicationContext();

        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    try{
                        if(!recyclerView.canScrollVertically(1)){
                            playVideo(true);
                        }
                        else{
                            playVideo(false);
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
//                if (viewHolderParent != null && viewHolderParent.equals(view)) {
//                    resetVideoView();
//                }
            }
        });

    }

    public void setData(ExoPlayerData[] data){
        mPlayer = data;
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
            if (startPosition < 0 || endPosition < 0) {
                return;
            }

            // if there is more than 1 list-item on the screen
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
            targetPosition = memoryModels.size() ;
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

        if(targetPosition == 0){
            playPosition = 0;
            return;
        }

        resetVideoView(playPosition);
        playPosition = targetPosition;

        if (mPlayer == null || mPlayer[targetPosition-1] == null || mPlayer[targetPosition-1].exoPlayer == null) {
            playPosition = -1;
            return;
        }

        Log.e("playing", (targetPosition - 1) + "");
        mPlayer[targetPosition-1].exoPlayer.setPlayWhenReady(true);
    }


    private void resetVideoView(int pos){
        try{
            if(mPlayer == null)
                return;
            if(pos != -1 && pos != 0 && memoryModels.get(pos-1).getMediaType().equalsIgnoreCase("video")) {
                if (mPlayer[pos - 1] == null || mPlayer[pos-1].exoPlayer == null)
                    return;
                mPlayer[pos-1].exoPlayer.setPlayWhenReady(false);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void setMemoryModels(List<MemoryModel> memoryModels){
        this.memoryModels = memoryModels;
        mPlayer= new ExoPlayerData[memoryModels.size()];

    }

    public void stopVideo(){
        if(mPlayer != null){
            for(int i=0; i< mPlayer.length; i++){
                if(mPlayer[i] != null && mPlayer[i].exoPlayer != null){
                    mPlayer[i].exoPlayer.setPlayWhenReady(false);
                }
            }
            targetPosition = -1;
        }
    }

    public void releasePlayers(){
        if(mPlayer != null){
            for(int i=0; i< mPlayer.length; i++){
                if(mPlayer[i] != null && mPlayer[i].exoPlayer != null){
                    mPlayer[i].exoPlayer.release();
                }
                mPlayer[i] = null;
            }
            targetPosition = -1;
        }
    }
}
