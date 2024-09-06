package com.app.khaleeji.Fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;

import com.carlosmuvi.segmentedprogressbar.SegmentedProgressBar;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.app.khaleeji.Activity.FriendDailiesViewActivity;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.MediaModel;
import com.app.khaleeji.databinding.FragmentDailyFriendBinding;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import CustomView.Video.VideoPlayerEventListener;


public class FriendDailyFragment extends BaseFragment {

    private List<MediaModel> listDailies = new ArrayList<MediaModel>();
    private MediaModel mediaModel;
    private FragmentDailyFriendBinding mbinding;
    private int currentScreen = 0;
    private SimpleExoPlayer exoPlayer;

    public VideoHolder[] aVideoHolder;

    public class VideoHolder {
        SimpleExoPlayer exoPlayer;
        ProgressBar progressBar;
        int position;
        SegmentedProgressBar segmentedProgressBar;
    }

    public FriendDailyFragment(){

    }

    public FriendDailyFragment(List<MediaModel> mediaModelList) {
        listDailies = mediaModelList;
        if (mediaModelList != null && mediaModelList.size() > 0)
            aVideoHolder = new VideoHolder[mediaModelList.size()];
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_daily_friend, container, false);
        View view = mbinding.getRoot();
        initView();
        return view;
    }

    private void initView() {

        mediaModel = listDailies.get(currentScreen);

        mbinding.mainLayout.setOnTouchListener(new TouchGesture(mbinding.mainLayout));
        mbinding.segProgressBar.setSegmentCount(listDailies.size());
        mbinding.videoplayer.setVisibility(View.GONE);
        mbinding.videoplayer.setOnTouchListener(new TouchGesture(mbinding.mainLayout));
        mbinding.image.setVisibility(View.GONE);

        if (mediaModel.getUrl() != null && !mediaModel.getUrl().isEmpty()) {
            if (mediaModel.getMediaType().equalsIgnoreCase("image")) {
                mbinding.image.setVisibility(View.VISIBLE);
                try {
                    Picasso.with(mActivity).load(mediaModel.getUrl()).fit().into(mbinding.image);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (mediaModel.getMediaType().equalsIgnoreCase("video")) {
                try {
                    mbinding.videoplayer.setVisibility(View.VISIBLE);
                    exoPlayer = new SimpleExoPlayer.Builder(mActivity).build();
                    mbinding.videoplayer.setPlayer(exoPlayer);
                    exoPlayer.addListener(new VideoPlayerEventListener(exoPlayer, mbinding.progressBar, currentScreen) {
                        @Override
                        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                            switch (playbackState) {
                                case Player.STATE_READY:
                                    MessageEvent messageEvent = new MessageEvent();
                                    messageEvent.setType(MessageEvent.MessageType.VIDEO_PLAY_READY);
                                    messageEvent.setPageNo(position);
                                    EventBus.getDefault().post(messageEvent);

                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mActivity,
                            Util.getUserAgent(mActivity, mActivity.getString(R.string.app_name)));
                    ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
                            Uri.parse(mediaModel.getUrl()));
                    exoPlayer.prepare(mediaSource, true, false);
                    //exoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
                    exoPlayer.setPlayWhenReady(true);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
    }

    public void goManualNextScreen(){
        if ( currentScreen < listDailies.size())
        {
            if(exoPlayer != null){
                exoPlayer.setPlayWhenReady(false);
            }

//            cancelCountdownTimer();
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    dummyPager.setCurrentItem(dummyPager.getCurrentItem() + 1);
//                }
//            }, 100);

        }
    }

    public void goManualPrevScreen(){
//        if (dummyPager.getCurrentItem() >  0)
//        {
//            isManualSelect = true;
//            if(exoPlayer != null){
//                exoPlayer.setPlayWhenReady(false);
//            }
//
//            cancelCountdownTimer();
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    dummyPager.setCurrentItem(dummyPager.getCurrentItem() - 1);
//                }
//            }, 100);
//
//        }
    }
    class TouchGesture implements View.OnTouchListener {
        private RelativeLayout baseLayout;
        private int previousFingerPosition = 0;
        private int baseLayoutPosition = 0;
        private int defaultViewHeight;
        private boolean isMoved = false;
        private boolean isClosing = false;
        private boolean isScrollingUp = false;
        private boolean isScrollingDown = false;
        int baseOldX = 0;
        int baseOldY = 0;

        TouchGesture(RelativeLayout layout) {
            baseLayout = layout;
        }

        public boolean onTouch(View view, MotionEvent event) {

            // Get finger position on screen
            int Y = (int) event.getRawY();
            int X = (int) event.getRawX();
            int halfX = (int) baseLayout.getWidth() / 2;

            // Switch on motion event type
            switch (event.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:
                    isMoved = false;
                    baseOldX = (int) event.getX();
                    baseOldY = (int) event.getY();
                    // save default base layout height
                    defaultViewHeight = baseLayout.getHeight();

                    // Init finger and view position
                    previousFingerPosition = Y;
                    baseLayoutPosition = (int) baseLayout.getY();
                    break;
                case MotionEvent.ACTION_UP:

                    if (!isMoved) {
                        if (X > halfX) {
                            goManualNextScreen();
                        } else {
                            goManualPrevScreen();
                        }
                    }

                    // If user was doing a scroll up
                    if (isScrollingUp) {
                        // Reset baselayout position
                        baseLayout.setY(0);
                        // We are not in scrolling up mode anymore
                        isScrollingUp = false;
                    }

                    // If user was doing a scroll down
                    if (isScrollingDown) {
                        // Reset baselayout position
                        baseLayout.setY(0);
                        // Reset base layout size
                        baseLayout.getLayoutParams().height = defaultViewHeight;
                        baseLayout.requestLayout();
                        // We are not in scrolling down mode anymore
                        isScrollingDown = false;
                    }

                    break;
                case MotionEvent.ACTION_MOVE:
                    if (Math.abs(event.getX() - baseOldX) > 10 && Math.abs(event.getY() - baseOldY) > 10) {
                        isMoved = true;
                    }
                    if (!isClosing) {
                        int currentYPosition = (int) baseLayout.getY();

                        // If we scroll up
                        if (previousFingerPosition > Y) {
                            // First time android rise an event for "up" move
//                            if(!isScrollingUp){
//                                isScrollingUp = true;
//                            }
//
//                            // Has user scroll down before -> view is smaller than it's default size -> resize it instead of change it position
//                            if(baseLayout.getHeight()<defaultViewHeight){
//                                baseLayout.getLayoutParams().height = baseLayout.getHeight() - (Y - previousFingerPosition);
//                                baseLayout.requestLayout();
//                            }
//                            else {
//                                // Has user scroll enough to "auto close" popup ?
//                                if ((baseLayoutPosition - currentYPosition) > defaultViewHeight / 4) {
//                                    closeUpAndDismissDialog(currentYPosition);
//                                    return true;
//                                }
//
//                                //
//                            }
//                            baseLayout.setY(baseLayout.getY() + (Y - previousFingerPosition));

                        }
                        // If we scroll down
                        else {

                            // First time android rise an event for "down" move
                            if (!isScrollingDown) {
                                isScrollingDown = true;
                            }

                            // Has user scroll enough to "auto close" popup ?
                            if (Math.abs(baseLayoutPosition - currentYPosition) > defaultViewHeight / 2) {
                                closeDownAndDismissDialog(currentYPosition);
                                return true;
                            }

                            // Change base layout size and position (must change position because view anchor is top left corner)
                            baseLayout.setY(baseLayout.getY() + (Y - previousFingerPosition));
                            baseLayout.getLayoutParams().height = baseLayout.getHeight() - (Y - previousFingerPosition);
                            baseLayout.requestLayout();
                        }

                        // Update position
                        previousFingerPosition = Y;
                    }
                    break;
            }
            return true;
        }

        public void closeDownAndDismissDialog(int currentPosition){
            isClosing = true;
            Display display = mActivity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int screenHeight = size.y;
            ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(baseLayout, "y", currentPosition, screenHeight+baseLayout.getHeight());
            positionAnimator.setDuration(300);
            positionAnimator.addListener(new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animator)
                {

                }
                @Override
                public void onAnimationCancel(Animator animator)
                {

                }

                @Override
                public void onAnimationRepeat(Animator animator)
                {

                }
                @Override
                public void onAnimationEnd(Animator animator)
                {
                    mActivity.finish();
                }

            });
            positionAnimator.start();
        }
    }

}