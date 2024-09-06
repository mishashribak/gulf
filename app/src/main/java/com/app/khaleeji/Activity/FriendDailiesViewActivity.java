package com.app.khaleeji.Activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.app.khaleeji.Response.RemoveMediaResponse;
import com.carlosmuvi.segmentedprogressbar.SegmentedProgressBar;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.MediaModel;
import com.app.khaleeji.Response.fetchDailiesOfFriends.FriendListDailiesOfFriends;
import com.squareup.picasso.Picasso;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.Bundle_Identifier;
import CustomView.CustomTextView;
import CustomView.CustomViewPager;
import CustomView.Video.CacheDataSourceFactory;
import CustomView.Video.VideoPlayerEventListener;
import Utility.ApiClass;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.exoplayer2.Player.STATE_READY;

public class FriendDailiesViewActivity extends BaseActivity {

    private  String TAG = FriendDailiesViewActivity.class.getName();
    private Context context;
    private CustomViewPager dummyPager;
    private SimpleExoPlayer exoPlayer;
    private SegmentedProgressBar segmentedProgressBar;
    private CountDownTimer countDownTimer;
    private int lastPosition = 0;
    private FriendProfileDailiesDummyAdapter dailiesDummyAdapter;
    private boolean isManualSelect = true;
    private int currentMediaTime, currentPage = -1;
    private boolean isDestroyed;
    private List<FriendListDailiesOfFriends> friendListDailiesOfFriends;
    private FriendListDailiesOfFriends friendData;
    private int selectedFriendIndex;
    private List<MediaModel> listDailies;
    private boolean first = true;
    private int positionToPlay = 0;
    private boolean isPaused;

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent (MessageEvent messageEvent) {
        if(messageEvent.getType() == MessageEvent.MessageType.FRIEND_DAILIES_VIEW){
            friendListDailiesOfFriends = messageEvent.getFriendLists();
            if(messageEvent.getPageNo() > -1){
                selectedFriendIndex = messageEvent.getPageNo();
                friendData = friendListDailiesOfFriends.get(selectedFriendIndex);
                listDailies = friendListDailiesOfFriends.get(selectedFriendIndex).getMedia();
                Collections.sort(listDailies, new MediaSorter());
                findPlayPosition(listDailies);
                dailiesDummyAdapter.setData(listDailies);
                dummyPager.setCurrentItem(positionToPlay);
            }
            remvoeSticky();
            return;
        }

        if(messageEvent.getType() == MessageEvent.MessageType.VIDEO_PLAY_READY){
            int oldPos = messageEvent.getPageNo();
            Log.e("FriendDailiesView", "onbind video ready: " + oldPos);
            if(aVideoHolder == null || aVideoHolder[oldPos] == null)
                return;

            if( oldPos != currentPage && aVideoHolder[oldPos].exoPlayer != null){
                aVideoHolder[oldPos].exoPlayer.setPlayWhenReady(false);
            }else{
                aVideoHolder[oldPos].exoPlayer.setPlayWhenReady(true);
                if(exoPlayer != null){
                    startCountDownTimer(currentMediaTime);
                }
            }
        }
        remvoeSticky();
    }

    public class MediaSorter implements Comparator<MediaModel>
    {
        public int compare(MediaModel left, MediaModel right) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date leftDate = sdf.parse(left.getCreatedAt());
                Date rightDate = sdf.parse(right.getCreatedAt());
                if( leftDate.getTime() < rightDate.getTime() )
                    return -1;
                else if(leftDate.getTime() == rightDate.getTime() )
                    return 0;
                else
                    return 1;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    private void findPlayPosition(List<MediaModel> mediaModels){
        for(int i = 0; i < mediaModels.size(); i++){
            if(mediaModels.get(i).getViewBefore() != null && !mediaModels.get(i).getViewBefore()){
                positionToPlay = i;
                break;
            }
        }
        Log.e("FriendDailies", "playposition" + positionToPlay);
    }

    public void remvoeSticky(){
        MessageEvent stickyEvent = EventBus.getDefault().getStickyEvent(MessageEvent.class);
        if(stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent(stickyEvent);
        }
    }

    private void mediaViewApiRequest(int media_id) {
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Call<RemoveMediaResponse> call = mApiInterface.viewMedia(SavePref.getInstance(this).getUserdetail().getId()
                , media_id);
        call.enqueue(new Callback<RemoveMediaResponse>() {
            @Override
            public void onResponse(Call<RemoveMediaResponse> call, Response<RemoveMediaResponse> response) {
                Log.d(TAG, "onResponse: ");

            }

            @Override
            public void onFailure(Call<RemoveMediaResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: ");

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        isPaused = false;
        if(exoPlayer != null)
            exoPlayer.setPlayWhenReady(true);

        if(isManualSelect){
            if(currentPage != -1 && segmentedProgressBar != null)
                segmentedProgressBar.setCompletedSegments(currentPage+1);
        }else{
            segmentedProgressBar.setCompletedSegments(currentPage);
            timerResume();
        }
    }


    private boolean isImageLoaded[];
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_friend_dailiy);
        context = this;
        //segmentedProgressBar = findViewById(R.id.segmented_progressbar);
        dummyPager = findViewById(R.id.dummyPager);
        dummyPager.setOnSwipeOutListener(new CustomViewPager.OnSwipeOutListener() {
            @Override
            public void onSwipePrev() {
                Log.e("zzz", "prev");
                if(selectedFriendIndex > 0){
                    if(exoPlayer != null)
                        exoPlayer.setPlayWhenReady(false);
                    resetVideo();
                    first = true;
                    selectedFriendIndex --;
                    listDailies = friendListDailiesOfFriends.get(selectedFriendIndex).getMedia();
                    friendData = friendListDailiesOfFriends.get(selectedFriendIndex);
                    Collections.sort(listDailies, new MediaSorter());
                    findPlayPosition(listDailies);
                    dailiesDummyAdapter.setData(listDailies);
                    dummyPager.setCurrentItem(positionToPlay);
                }

            }

            @Override
            public void onSwipeNext() {
                Log.e("zzz", "next");
                if(selectedFriendIndex < friendListDailiesOfFriends.size()-1){
                    first = true;
                    if(exoPlayer != null)
                        exoPlayer.setPlayWhenReady(false);
                    resetVideo();
                    selectedFriendIndex ++;
                    friendData = friendListDailiesOfFriends.get(selectedFriendIndex);
                    listDailies = friendListDailiesOfFriends.get(selectedFriendIndex).getMedia();
                    Collections.sort(listDailies, new MediaSorter());
                    findPlayPosition(listDailies);
                    dailiesDummyAdapter.setData(listDailies);
                    dummyPager.setCurrentItem(positionToPlay);
                }
            }
        });

        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);

            FixedSpeedScroller scroller = new FixedSpeedScroller(dummyPager.getContext(), new LinearInterpolator());
            mScroller.set(dummyPager, scroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }

        dailiesDummyAdapter = new FriendProfileDailiesDummyAdapter(context);

        dummyPager.setAdapter(dailiesDummyAdapter);
        dummyPager.setOffscreenPageLimit(0);

        setStatusBarColor(R.color.black);

        ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) { }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (first && positionOffset == 0 && positionOffsetPixels == 0){
                    isManualSelect = false;
                    onPageSelected(positionToPlay);
                    first = false;
                }
            }

            @Override
            public void onPageSelected(int position) {
                Log.e("FriendDailiesView", "onPageSelected: "+ position);
                listDailies.get(position).viewBefore = true;
                mediaViewApiRequest(listDailies.get(position).getId());
                if(position == listDailies.size() - 1){
                    Log.e("FriendDailiesView", "all viewed: "+ position);
                    MessageEvent messageEvent = new MessageEvent();
                    messageEvent.selectedFriendIndex = selectedFriendIndex;
                    messageEvent.setType(MessageEvent.MessageType.DAILY_REFRESH);

                    EventBus.getDefault().post(messageEvent);
                }
                currentPage = position;
                if(aVideoHolder == null || aVideoHolder[position] == null)
                    return;

                segmentedProgressBar =  aVideoHolder[position].segmentedProgressBar;

                pauseBeforeVideo(position);

                exoPlayer =aVideoHolder[position].exoPlayer;
                if(exoPlayer != null)
                    exoPlayer.setPlayWhenReady(true);

                segmentedProgressBar.reset();
                if(position > 0)
                    segmentedProgressBar.setCompletedSegments(position);

                if(isManualSelect){
                    lastPosition = position;
//                    if(position >= 0 && position <= listDailies.size()-1)
//                        segmentedProgressBar.setCompletedSegments(position + 1);

                }else{
                    isManualSelect = true;
                }


                if(listDailies != null && listDailies.size()>0){
                    MediaModel mediaModel = listDailies.get(position);

                    currentMediaTime = mediaModel.getMedia_time();
                    if(mediaModel.getMediaType().equalsIgnoreCase("image")){
                        if(isImageLoaded[position]){
                            Log.e("FriendDailiesView", "imageReady: "+ position);
                            startCountDownTimer(currentMediaTime);
                        }
                    }
                    else{
                        if(exoPlayer != null)
                            if(exoPlayer.getPlaybackState() == STATE_READY || exoPlayer.getPlaybackState() == Player.STATE_ENDED){
                                Log.e("FriendDailiesView", "videoready: "+ position);
                                startCountDownTimer(currentMediaTime);
                            }
                    }
                }
            }
        };

        dummyPager.addOnPageChangeListener(pageChangeListener);

    }

    public void timerPause() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void timerResume() {
        int time = (int)milliLeft/1000;
        if(time < 1){
            time = 1;
        }
        startCountDownTimer(time);
    }

    private long milliLeft;
    public void startCountDownTimer( int time) {
        if(isDestroyed)
            return;
        Log.e("FriendDailiesView", "startCountDownTimer: "+ time);
        if(segmentedProgressBar == null)
            return;

        if(time <= 0){
            segmentedProgressBar.playSegment(50);
            return;
        }

        segmentedProgressBar.playSegment(time * 1000);
        isManualSelect = false;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(time*1000, 1000) {
            public void onTick(long millisUntilFinished) {
                milliLeft = millisUntilFinished;
            }

            public void onFinish() {
                goNextScreen();
            }
        }.start();


    }

    public void goNextScreen(){
        if (dummyPager.getCurrentItem() < listDailies.size()-1)
        {
            Log.e("FriendDailiesView", "goNextScreen: "+ dummyPager.getCurrentItem() + 1);
            isManualSelect = false;
            if(exoPlayer != null){
                exoPlayer.setPlayWhenReady(false);
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dummyPager.setCurrentItem(dummyPager.getCurrentItem() + 1);
                }
            }, 100);
        }
    }

  /*  public void goPrevScreen(){
        if (dummyPager.getCurrentItem() >  0)
        {
            if(exoPlayer != null){
                exoPlayer.setPlayWhenReady(false);
            }

            isManualSelect = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dummyPager.setCurrentItem(dummyPager.getCurrentItem() - 1);
                }
            }, 100);

        }
    }*/

    public void cancelCountdownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void goManualNextScreen(){
        Log.e("FriendDailiesView", "manual next" + dummyPager.getCurrentItem());
        if (dummyPager.getCurrentItem() < listDailies.size()-1)
        {
            isManualSelect = true;
            if(exoPlayer != null){
                exoPlayer.setPlayWhenReady(false);
            }

            cancelCountdownTimer();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dummyPager.setCurrentItem(dummyPager.getCurrentItem() + 1);
                }
            }, 100);

        }
    }

    public void goManualPrevScreen(){
        if (dummyPager.getCurrentItem() >  0)
        {
            isManualSelect = true;
            if(exoPlayer != null){
                exoPlayer.setPlayWhenReady(false);
            }

            cancelCountdownTimer();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dummyPager.setCurrentItem(dummyPager.getCurrentItem() - 1);
                }
            }, 100);

        }
    }

    public VideoHolder[] aVideoHolder;
    public class VideoHolder {
        View dummyView;
        SimpleExoPlayer exoPlayer;
        ProgressBar progressBar;
        int position;
        SegmentedProgressBar segmentedProgressBar;
    }

    public class FriendProfileDailiesDummyAdapter extends PagerAdapter {

        private String TAG = FriendProfileDailiesDummyAdapter.class.getName();
        private LayoutInflater inflater;
        private Context context;
        private MediaModel mediaModel;
        private List<MediaModel> listDailies = new ArrayList<>();


        public FriendProfileDailiesDummyAdapter(Context context)  //,ArrayList<Integer> IMAGES
        {
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public void setData(List<MediaModel> list){
            listDailies = list;
            if(list != null && list.size() > 0){
                aVideoHolder = new VideoHolder[list.size()];
                isImageLoaded = new boolean[list.size()];
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if(listDailies != null)
                return listDailies.size();
            return 0;
        }

        @Override
        public Object instantiateItem(ViewGroup view,  int position) {
            VideoHolder holder = null;
            if(aVideoHolder != null){
                holder = aVideoHolder[position];
            }

            if (holder != null) {
                view.addView(holder.dummyView);
                return holder.dummyView;
            }

            holder = new VideoHolder();

            View dummyView = inflater.inflate(R.layout.row_friend_dailes_view, view, false);
            view.addView(dummyView);

            holder.dummyView = dummyView;

            if(aVideoHolder != null)
                aVideoHolder[position] = holder;

            holder.position = position;

            mediaModel = listDailies.get(position);
            SegmentedProgressBar segmentedProgressBar = dummyView.findViewById(R.id.segProgressBar);
            holder.segmentedProgressBar = segmentedProgressBar;
            ImageView imageView =  dummyView.findViewById(R.id.image);
            PlayerView player = dummyView.findViewById(R.id.videoplayer);

            LinearLayout llSendComment = dummyView.findViewById(R.id.llSendComment);
            llSendComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(segmentedProgressBar != null)
                        segmentedProgressBar.pause();
                    timerPause();
                    isPaused = true;
                    Intent intent = new Intent(FriendDailiesViewActivity.this, DailyCommentActivity.class);
                    intent.putExtra("mediaType", listDailies.get(position).getMediaType());
                    intent.putExtra("mediaUrl", listDailies.get(position).getUrl());
                    intent.putExtra("thumbImage", listDailies.get(position).getThumbnail());
                    intent.putExtra("isFromChat", false);
                    intent.putExtra("friendData", friendData);
                    startActivity(intent);
                }
            });

            LinearLayout llLink = dummyView.findViewById(R.id.llLink);

            if(listDailies.get(position).getMedia_link() != null && !listDailies.get(position).getMedia_link().isEmpty()){
                llLink.setVisibility(View.VISIBLE);
            }else{
                llLink.setVisibility(View.INVISIBLE);
            }

            llLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listDailies.get(position).getMedia_link() != null && !listDailies.get(position).getMedia_link().isEmpty()){
                        if(segmentedProgressBar != null)
                            segmentedProgressBar.pause();
                        timerPause();
                        isPaused = true;
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(listDailies.get(position).getMedia_link()));
                        startActivity(browserIntent);
                    }
                }
            });

            de.hdodenhof.circleimageview.CircleImageView profile = dummyView.findViewById(R.id.imgProfile);
            profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(FriendDailiesViewActivity.this, MainActivity.class);  //ShowCameraToolPictureActivity
                    intent.putExtra("startFragment", "FriendProfileFragment");
                    intent.putExtra("userId", friendData.getId());
                    startActivity(intent);
                }
            });

            de.hdodenhof.circleimageview.CircleImageView imgProfile = dummyView.findViewById(R.id.imgProfile);
            if(listDailies.get(position).profilePicture != null && ! listDailies.get(position).profilePicture.isEmpty()) {
                Picasso.with(context).load(ApiClass.ImageBaseUrl + listDailies.get(position).profilePicture).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).fit().centerInside().into(imgProfile);
            }
            CustomTextView txtUserName = dummyView.findViewById(R.id.txtUserName);
            txtUserName.setText(friendData.getUsername());
            CustomTextView txtCreateTime = dummyView.findViewById(R.id.txtCreateTime);
            if (Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(Bundle_Identifier.ArabicLang)) {
                txtCreateTime.setText(listDailies.get(position).getAgoArabic());
            }else{
                txtCreateTime.setText(listDailies.get(position).getAgo());
            }

            RelativeLayout baseLayout= dummyView.findViewById(R.id.mainLayout);

            baseLayout.setOnTouchListener(new TouchGesture(baseLayout));

            segmentedProgressBar.reset();
            if(listDailies.size() > 0)
                segmentedProgressBar.setSegmentCount(listDailies.size());

            player.setVisibility(View.GONE);
            player.setOnTouchListener(new TouchGesture(baseLayout));

            if(listDailies.get(position).sourceType.equalsIgnoreCase("camera")){
                player.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
            }else{
                player.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            }

            imageView.setVisibility(View.GONE);

            ProgressBar progress_bar = dummyView.findViewById(R.id.progress_bar);
            holder.progressBar = progress_bar;
            progress_bar.setVisibility(View.GONE);

            if( mediaModel.getUrl() != null && ! mediaModel.getUrl().isEmpty()){
                if(mediaModel.getMediaType().equalsIgnoreCase("image")){
                    progress_bar.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    try{
                        if(mediaModel.sourceType.equalsIgnoreCase("camera")){
                            Picasso.with(FriendDailiesViewActivity.this).load( mediaModel.getUrl()).fit().centerCrop().into(imageView, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    isImageLoaded[position] = true;
                                    progress_bar.setVisibility(View.GONE);
                                    Log.e("FriendDailiesView", "onbind image ready: " + position);
                                    if(position == currentPage){
                                        startCountDownTimer(currentMediaTime);
                                    }
                                }

                                @Override
                                public void onError() {
                                    progress_bar.setVisibility(View.GONE);
                                }
                            });
                        }else{
                            Picasso.with(FriendDailiesViewActivity.this).load( mediaModel.getUrl()).fit().centerInside().into(imageView, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    isImageLoaded[position] = true;
                                    progress_bar.setVisibility(View.GONE);
                                    Log.e("FriendDailiesView", "onbind image ready: " + position);
                                    if(position == currentPage){
                                        startCountDownTimer(currentMediaTime);
                                    }
                                }

                                @Override
                                public void onError() {
                                    progress_bar.setVisibility(View.GONE);
                                }
                            });
                        }

                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                }else if(mediaModel.getMediaType().equalsIgnoreCase("video")){
                    try{
                        player.setVisibility(View.VISIBLE);
                        exoPlayer= new SimpleExoPlayer.Builder(context).build();
                        player.setPlayer(exoPlayer);
                        exoPlayer.addListener(new VideoPlayerEventListener(aVideoHolder[position].exoPlayer ,aVideoHolder[position].progressBar, position) {
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
//                        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
//                                Util.getUserAgent(context, context.getString(R.string.app_name)));
//                        ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
//                                Uri.parse(mediaModel.getUrl()));

                        ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(new CacheDataSourceFactory
                                (context, 100 * 1024 * 1024, 5 * 1024 * 1024, mediaModel.getId()
                                )).createMediaSource(Uri.parse( mediaModel.getUrl()));

                        exoPlayer.prepare(mediaSource, true, false);
                        //exoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
                        exoPlayer.setPlayWhenReady(false);

                        holder.exoPlayer = exoPlayer;
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                }
            }
            return dummyView;

        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
    }

    public void resetVideo(){
        cancelCountdownTimer();
        if(aVideoHolder != null)
            for(int i=0; i<aVideoHolder.length; i++){
                if(aVideoHolder[i] != null && aVideoHolder[i].exoPlayer != null)
                    aVideoHolder[i].exoPlayer.release();
            }

        aVideoHolder = null;
    }

    public void pauseVideo(){
        cancelCountdownTimer();
        if(aVideoHolder != null)
            for(int i=0; i<aVideoHolder.length; i++){
                if(aVideoHolder[i] != null && aVideoHolder[i].exoPlayer != null)
                    aVideoHolder[i].exoPlayer.setPlayWhenReady(false);
            }
    }

    public void pauseBeforeVideo(int index){
        if(aVideoHolder != null)
            for(int i=0; i < index; i++){
                if(aVideoHolder[i] != null && aVideoHolder[i].exoPlayer != null)
                    aVideoHolder[i].exoPlayer.setPlayWhenReady(false);
            }
    }

    class TouchGesture implements View.OnTouchListener{
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

        TouchGesture(RelativeLayout layout){
            baseLayout = layout;
        }
        public boolean onTouch(View view, MotionEvent event) {

            // Get finger position on screen
            int Y = (int) event.getRawY();
            int X = (int) event.getRawX();
            int halfX = (int) baseLayout.getWidth()/2;

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

                    if( ! isMoved ){
                        if( X > halfX) {
                            goManualNextScreen();
                        }else{
                            goManualPrevScreen();
                        }
                    }

                    // If user was doing a scroll up
                    if(isScrollingUp){
                        // Reset baselayout position
                        baseLayout.setY(0);
                        // We are not in scrolling up mode anymore
                        isScrollingUp = false;
                    }

                    // If user was doing a scroll down
                    if(isScrollingDown){
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
                    if(Math.abs(event.getX() - baseOldX) >10 && Math.abs(event.getY()- baseOldY) > 10){
                        isMoved = true;
                    }
                    if(!isClosing){
                        int currentYPosition = (int) baseLayout.getY();

                        // If we scroll up
                        if(previousFingerPosition >Y){
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
                        else{

                            // First time android rise an event for "down" move
                            if(!isScrollingDown){
                                isScrollingDown = true;
                            }

                            // Has user scroll enough to "auto close" popup ?
                            if (Math.abs(baseLayoutPosition - currentYPosition) > defaultViewHeight / 2)
                            {
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

        public void closeUpAndDismissDialog(int currentPosition){
            isClosing = true;
            ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(baseLayout, "y", currentPosition, -baseLayout.getHeight());
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
                    finish();
                    overridePendingTransition(0,0);
                }

            });
            positionAnimator.start();
        }

        public void closeDownAndDismissDialog(int currentPosition){
            isClosing = true;
            Display display = getWindowManager().getDefaultDisplay();
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
                    finish();
                }

            });
            positionAnimator.start();
        }
    }

    public class FixedSpeedScroller extends Scroller {

        private int mDuration = 200;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }


        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("FriendDailiesView", "onStop");
        if(exoPlayer != null)
            exoPlayer.setPlayWhenReady(false);
        if(!isPaused){
            resetVideo();
        }else{
            pauseVideo();
        }
    }

}





