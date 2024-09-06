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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.Toast;
import com.carlosmuvi.segmentedprogressbar.SegmentedProgressBar;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.app.khaleeji.Adapter.ViewedUsersAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.MediaModel;
import com.app.khaleeji.Response.MediaViewsData;
import com.app.khaleeji.Response.MediaViewsListResponse;
import com.app.khaleeji.Response.RemoveMediaResponse;
import com.app.khaleeji.services.MediaDownloadService;
import com.squareup.picasso.Picasso;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import CustomView.CustomTextView;
import CustomView.Video.CacheDataSourceFactory;
import CustomView.Video.VideoPlayerEventListener;
import Utility.ApiClass;
import Utility.GlobalVariable;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.exoplayer2.Player.STATE_READY;

public class MyDailiesViewActivity extends BaseActivity {

    private  String TAG = MyDailiesViewActivity.class.getName();
    private Context context;
    private androidx.viewpager.widget.ViewPager dummyPager;
    private SimpleExoPlayer exoPlayer;
    private SegmentedProgressBar segmentedProgressBar;
    private CountDownTimer countDownTimer;
    private boolean isManualSelect = true;
    private int lastPosition = 0;
    private PlayerView player;
    private int currentPage =0 ;
    private int currentMediaTime = 0;
    List<MediaModel> listDailies;
    MyProfileDailiesDummyAdapter dailiesDummyAdapter;
    private boolean isDeletedHistory;

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent (MessageEvent messageEvent) {
        if(messageEvent.getType() == MessageEvent.MessageType.MY_DAILIES_VIEW){
            listDailies = messageEvent.getGulfProfileData().getDailyList();
            dailiesDummyAdapter.setData(listDailies);
            return;
        }

        if(messageEvent.getType() == MessageEvent.MessageType.VIDEO_PLAY_READY){
            int oldPos = messageEvent.getPageNo();
            if(aVideoHolder == null || aVideoHolder[oldPos] == null)
                return;

            if( oldPos != currentPage && aVideoHolder[oldPos].exoPlayer != null){
                aVideoHolder[oldPos].exoPlayer.setPlayWhenReady(false);
            }else{
                aVideoHolder[oldPos].exoPlayer.setPlayWhenReady(true);
                if(exoPlayer != null){// && isDoneApiCall[currentPage]){
                    startCountDownTimer(currentMediaTime);
                }
            }
        }

    }

    private boolean isImageLoaded[];
//    private boolean isDoneApiCall[];
    private boolean isDeleteUnAvailable[];
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_profile_dailies);
        context = this;

        dummyPager = findViewById(R.id.dummyPager);

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

        dailiesDummyAdapter = new MyProfileDailiesDummyAdapter(context);

        dummyPager.setAdapter(dailiesDummyAdapter);
//        dummyPager.setOffscreenPageLimit(1);

        setStatusBarColor(R.color.black);

        ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
            Boolean first = true;
            @Override
            public void onPageScrollStateChanged(int arg0) { }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (first && positionOffset == 0 && positionOffsetPixels == 0){
                    isManualSelect = false;
                    onPageSelected(0);
                    first = false;
                }
            }

            @Override
            public void onPageSelected(int position) {
                Log.e("MyDailiesViewActivity", "onPageSelected");
                try{
                    currentPage = position;
                    if(aVideoHolder == null || aVideoHolder[position] == null)
                        return;

                    segmentedProgressBar =  aVideoHolder[position].segmentedProgressBar;

                    if(exoPlayer != null)
                        exoPlayer.setPlayWhenReady(false);

                    exoPlayer =aVideoHolder[position].exoPlayer;
                    if(exoPlayer != null)
                        exoPlayer.setPlayWhenReady(true);

                    segmentedProgressBar.reset();
                    if(position > 0)
                        segmentedProgressBar.setCompletedSegments(position);

                    if(isManualSelect){
                        lastPosition = position;
//                        if(position >= 0 && position <= listDailies.size()-1)
//                            segmentedProgressBar.setCompletedSegments(position + 1);

                    }else{
                        isManualSelect = true;
                    }

                    if(listDailies != null && listDailies.size()>0){
                        MediaModel mediaModel = listDailies.get(position);

                        currentMediaTime = mediaModel.getMedia_time();
                        if(mediaModel.getMediaType().equalsIgnoreCase("image")){
                            if(isImageLoaded[position])// && isDoneApiCall[position])
                                startCountDownTimer(currentMediaTime);
                        }
                        else{
                            if(exoPlayer != null)
                                if((exoPlayer.getPlaybackState() == STATE_READY || exoPlayer.getPlaybackState() == Player.STATE_ENDED)){// && isDoneApiCall[currentPage]){
                                    startCountDownTimer(currentMediaTime);
                                }
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
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

    private void timerResume(int time) {
        Log.e("MyDailiesViewActivity", "timerResume");
        if(time < 1){
            time = 1;
        }
        startCountDownTimer(time);
    }

    private long milliLeft;
    public void startCountDownTimer( int time) {
        if(isDestroyed)
            return;
        Log.e("MyDailiesViewActivity", "startCountDownTimer");
        if(time > 0){
            segmentedProgressBar.playSegment(time * 1000);

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
        }else{
            segmentedProgressBar.playSegment(50);
        }
    }

    public void goNextScreen(){
        if (dummyPager.getCurrentItem() < listDailies.size()-1)
        {
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

        }else{
            exit();
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

        }else{
            exit();
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

    private void exit(){
        if(isDeletedHistory){
            MessageEvent messageEvent = new MessageEvent();
            messageEvent.setType(MessageEvent.MessageType.PROFILE_REFRESH);
            EventBus.getDefault().post(messageEvent);
        }
        finish();
    }
    public void callRemoveMedia(int mediaId, int position){

        if(listDailies != null){
            timerPause();
            listDailies.remove(position);

            for(int i=0 ; i< listDailies.size(); i++){
                if(aVideoHolder[i] != null && aVideoHolder[i].segmentedProgressBar != null)
                    aVideoHolder[i].segmentedProgressBar.setSegmentCount(listDailies.size());
            }

            for(int i = position; i< aVideoHolder.length; i++){
                if( i == aVideoHolder.length - 1){
                    aVideoHolder[i] = null;
                    isDeleteUnAvailable[i] = true;
//                    isDoneApiCall[i] = true;
                }else{
                    aVideoHolder[i] = aVideoHolder[i+1];
                    isDeleteUnAvailable[i] = isDeleteUnAvailable[i+1];
//                    isDoneApiCall[i] = isDoneApiCall[i+1];
                }
            }
            dailiesDummyAdapter.notifyDataSetChanged();
            if(listDailies.size() == 0){
                exit();
            }
            if(position < listDailies.size()){
                timerResume(listDailies.get(position).getMedia_time());
            }

            ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
            Map mparams = new HashMap();
            Call<RemoveMediaResponse> call;
            int userid = SavePref.getInstance(this).getUserdetail().getId().intValue();
            mparams.put("user_id", userid);
            mparams.put("media_id", mediaId);
            call = mApiInterface.removeMediaService(mparams);
            call.enqueue(new Callback<RemoveMediaResponse>() {
                @Override
                public void onResponse(Call<RemoveMediaResponse> call, Response<RemoveMediaResponse> response) {
                    isDeletedHistory = true;
    //                RemoveMediaResponse removeMediaResponse = response.body();
    //                if (removeMediaResponse != null ) {
    //                    if (removeMediaResponse.getStatus().equalsIgnoreCase("true")) {
    //
    //                    }
    //                }
                }
                @Override
                public void onFailure(Call<RemoveMediaResponse> call, Throwable t) {
                }
            });
        }
    }

    public class MyProfileDailiesDummyAdapter extends PagerAdapter {

        private String TAG = MyProfileDailiesDummyAdapter.class.getName();
        private LayoutInflater inflater;
        private Context context;
        private MediaModel memoryModel;
        private List<MediaModel> listDailies = new ArrayList<>();


        public MyProfileDailiesDummyAdapter(Context context)  //,ArrayList<Integer> IMAGES
        {
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public void setData(List<MediaModel> list){
            listDailies = list;
            if(list != null && list.size() > 0){
                aVideoHolder = new VideoHolder[list.size()];
                isImageLoaded = new boolean[list.size()];
//                isDoneApiCall = new boolean[list.size()];
                isDeleteUnAvailable = new boolean[list.size()];
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
                holder =aVideoHolder[position];
            }

            if (holder != null) {
                view.addView(holder.dummyView);
                return holder.dummyView;
            }

            holder = new VideoHolder();
            View dummyView = inflater.inflate(R.layout.row_other_user_profile_dailies_layout, view, false);
            dummyView.setTag("myview" + position);
            view.addView(dummyView);
            if(aVideoHolder != null)
                aVideoHolder[position] = holder;

            holder.dummyView = dummyView;
            holder.position = position;

            memoryModel = listDailies.get(position);
            SegmentedProgressBar segmentedProgressBar = dummyView.findViewById(R.id.segProgressBar);
            holder.segmentedProgressBar = segmentedProgressBar;
            ImageView imageView =  dummyView.findViewById(R.id.image);
            PlayerView player = dummyView.findViewById(R.id.videoplayer);

            CustomTextView txtView = dummyView.findViewById(R.id.txtView);
            CustomTextView txtBottomView = dummyView.findViewById(R.id.txtBottomView);
            CustomTextView txtNoData = dummyView.findViewById(R.id.txtNoData);
            View viewBlackTrans = dummyView.findViewById(R.id.viewBlackTrans);
            RelativeLayout baseLayout= dummyView.findViewById(R.id.mainLayout);

            baseLayout.setOnTouchListener(new TouchGesture(baseLayout));
            player.setOnTouchListener(new TouchGesture(baseLayout));

            if(memoryModel.sourceType.equalsIgnoreCase("camera")){
                player.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
            }else{
                player.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            }

            RelativeLayout topbar = dummyView.findViewById(R.id.topBar);
            RelativeLayout bottombar = dummyView.findViewById(R.id.bottomBar);
            RelativeLayout usersLayout = dummyView.findViewById(R.id.usersLayout);

            String str = String.valueOf(memoryModel.getView_count());
            txtView.setText(str);
            txtBottomView.setText(str);

            if(currentPage == position)
                ProgressDialog.showProgress(MyDailiesViewActivity.this);
            ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);

            Call<MediaViewsListResponse> call = mApiInterface.getMediaViews(listDailies.get(position).getId());

//            isDoneApiCall[position] = false;
            call.enqueue(new Callback<MediaViewsListResponse>() {
                @Override
                public void onResponse(Call<MediaViewsListResponse> call, Response<MediaViewsListResponse> response) {
                    ProgressDialog.hideprogressbar();
                    if (response.isSuccessful())
                    {
                        MediaViewsListResponse mediaViewsListResponse = response.body();

                        if(mediaViewsListResponse!=null ){
                            if (mediaViewsListResponse.getStatus().equalsIgnoreCase("true")) {
//                                isDoneApiCall[position] = true;
                                if(isImageLoaded[position])
                                    startCountDownTimer(currentMediaTime);
                                List<MediaViewsData> listMediaViewers = mediaViewsListResponse.getData();
                                if(listMediaViewers != null && listMediaViewers.size() > 0){
                                    txtNoData.setVisibility(View.GONE);
                                    ViewedUsersAdapter listViewedUser = new ViewedUsersAdapter(MyDailiesViewActivity.this, new ViewedUsersAdapter.OnItemClickListener(){
                                        @Override
                                        public void onItemClick(int pos){
                                            Intent intent = new Intent(MyDailiesViewActivity.this, MainActivity.class);  //ShowCameraToolPictureActivity
                                            intent.putExtra("startFragment", "FriendProfileFragment");
                                            intent.putExtra("userId", listMediaViewers.get(pos).getUser_id());
                                            startActivity(intent);
                                        }});

                                    RecyclerView rvViewers = dummyView.findViewById(R.id.rvUsers);
                                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(MyDailiesViewActivity.this);
                                    rvViewers.setLayoutManager(mLayoutManager);
                                    rvViewers.setAdapter(listViewedUser);
                                    listViewedUser.setData(listMediaViewers);
                                }else{
                                    txtNoData.setVisibility(View.VISIBLE);
                                }

                            } else {
//                                Toast.makeText(MyDailiesViewActivity.this,mediaViewsListResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                Utility.AlertDialog.showAlert(MyDailiesViewActivity.this, getString(R.string.app_name), mediaViewsListResponse.getMessage(), getString(R.string.txt_Done), "", false, null, null);
                            }

                        }
                    } else {
                        System.out.println(response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<MediaViewsListResponse> call, Throwable t) {
                    ProgressDialog.hideprogressbar();
                }
            });

            ImageView imgUpScroll = dummyView.findViewById(R.id.imgUpScroll);
            imgUpScroll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottombar.setVisibility(View.GONE);
                    topbar.setVisibility(View.VISIBLE);
                    usersLayout.setVisibility(View.VISIBLE);
                    viewBlackTrans.setVisibility(View.VISIBLE);

                    if(segmentedProgressBar != null)
                        segmentedProgressBar.pause();
                    timerPause();

                }
            });

            ImageView imgDownload = dummyView.findViewById(R.id.imgDownload);
            imgDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MediaDownloadService downloadService = new MediaDownloadService(listDailies.get(position).getUrl(), listDailies.get(position).getMediaType(),context);
                    downloadService.execute();
                }
            });

            ImageView imgTopDownload = dummyView.findViewById(R.id.imgTopDownload);
            imgTopDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MediaDownloadService downloadService = new MediaDownloadService(listDailies.get(position).getUrl(), listDailies.get(position).getMediaType(),context);
                    downloadService.execute();
                }
            });

            ImageView imgTopDeleteMedia = dummyView.findViewById(R.id.imgTopDeleteMedia);
            imgTopDeleteMedia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(! isDeleteUnAvailable[position] && position < listDailies.size()){
                        isDeleteUnAvailable[position] = true;
                        callRemoveMedia(listDailies.get(position).getId().intValue(), position);
                    }
                }
            });

            Log.e("setOnClickListener", ""+position);
            ImageView imgDeleteMedia = dummyView.findViewById(R.id.imgDeleteMedia);
            imgDeleteMedia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(! isDeleteUnAvailable[position] && position < listDailies.size()){
                        isDeleteUnAvailable[position] = true;
                        callRemoveMedia(listDailies.get(position).getId().intValue(), position);
                    }
                }
            });

            ImageView imgEye = dummyView.findViewById(R.id.imgEye);
            imgEye.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(segmentedProgressBar != null)
                        segmentedProgressBar.pause();
                    timerPause();
                    bottombar.setVisibility(View.GONE);
                    topbar.setVisibility(View.VISIBLE);
                    usersLayout.setVisibility(View.VISIBLE);
                    viewBlackTrans.setVisibility(View.VISIBLE);
                }
            });

            ImageView imgDownScroll = dummyView.findViewById(R.id.imgDownScroll);
            imgDownScroll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottombar.setVisibility(View.VISIBLE);
                    topbar.setVisibility(View.GONE);
                    usersLayout.setVisibility(View.GONE);
                    viewBlackTrans.setVisibility(View.GONE);

                    if(segmentedProgressBar != null)
                        segmentedProgressBar.resume();
                    timerResume();
                }
            });
            segmentedProgressBar.reset();
            if(listDailies.size() > 0)
                segmentedProgressBar.setSegmentCount(listDailies.size());

            player.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);

            ProgressBar progress_bar = dummyView.findViewById(R.id.progress_bar);
            holder.progressBar = progress_bar;
            progress_bar.setVisibility(View.GONE);
            isImageLoaded[position] = false;
            if( memoryModel.getUrl() != null && ! memoryModel.getUrl().isEmpty()){
                if(memoryModel.getMediaType().equalsIgnoreCase("image")){
                    progress_bar.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    try{
                        if(memoryModel.sourceType.equalsIgnoreCase("camera")){
                            Picasso.with(context).load( memoryModel.getUrl()).fit().
                                    centerCrop().into(imageView, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    isImageLoaded[position] = true;
                                    progress_bar.setVisibility(View.GONE);
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
                            Picasso.with(context).load( memoryModel.getUrl()).fit().
                                    centerInside().into(imageView, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    isImageLoaded[position] = true;
                                    progress_bar.setVisibility(View.GONE);
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
                }else if(memoryModel.getMediaType().equalsIgnoreCase("video")){
                    try{
                        player.setVisibility(View.VISIBLE);
                        exoPlayer = new SimpleExoPlayer.Builder(context).build();
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
//                                Uri.parse(memoryModel.getUrl()));
                        ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(new CacheDataSourceFactory
                                (context, 100 * 1024 * 1024, 5 * 1024 * 1024, memoryModel.getId()
                                )).createMediaSource(Uri.parse( memoryModel.getUrl()));
                        exoPlayer.prepare(mediaSource, true, false);
                        //                    exoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
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

    private boolean isDestroyed;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
        Log.e("MyDailiesViewActivity", "onStop");
        cancelCountdownTimer();
        if(aVideoHolder != null)
            for(int i=0; i<aVideoHolder.length; i++){
                if(aVideoHolder[i] != null && aVideoHolder[i].exoPlayer != null)
                    aVideoHolder[i].exoPlayer.release();
            }

        aVideoHolder = null;
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
                    exit();
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
                    exit();
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

}





