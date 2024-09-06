package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.MediaLikeReponse;
import com.app.khaleeji.Response.MemoryModel;
import com.app.khaleeji.Response.fetchDailiesOfFriends.FriendListDailiesOfFriends;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.AppConstants;
import Constants.Bundle_Identifier;
import CustomView.CustomTextView;
import CustomView.Video.CacheDataSourceFactory;
import CustomView.Video.ExoPlayerData;
import CustomView.Video.VideoPlayerEventListener;
import CustomView.Video.VideoPlayerRecyclerView;
import Utility.ApiClass;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DailyMemoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<MemoryModel> memoryModelList;
    private Context context;
    private OnDailyItemClickListener onItemClickListener;
    private OnGroupUserClickListener onHeaderClickListener;
    private List<FriendListDailiesOfFriends> dataUserList;
    private DailyUserAdapter dailyUserAdapter;
    private VideoPlayerRecyclerView mRecyclerView;
    private ExoPlayerData[] mExoPlayerData;
    private float[] currentVolume;
    private boolean isFromNotification;

    public DailyMemoryAdapter(Context context, OnDailyItemClickListener onItemClickListener
            ,DailyUserAdapter dailyUserAdapter) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.dailyUserAdapter = dailyUserAdapter;
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = (VideoPlayerRecyclerView)recyclerView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_dailymemory, parent, false);

            return new VideoPlayerViewHolder(itemView);
        } else if (viewType == TYPE_HEADER) {
            View itemView1 = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_dailyuser_header, parent, false);

            return new Header(itemView1);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, final int position1) {
        final int position = position1 - 1;
        if (holder1 instanceof VideoPlayerViewHolder) {
            VideoPlayerViewHolder holder = (VideoPlayerViewHolder) holder1;

            MemoryModel memoryModel = memoryModelList.get(position);

            if(memoryModel.getLike() != null && memoryModel.getLike()){
                likeUI(holder, memoryModel);
            }else{
                unlikeUI(holder, memoryModel);
            }
            
            if(position == 0){
                holder.label.setVisibility(View.VISIBLE);
            }else{
                holder.label.setVisibility(View.GONE);
            }

            holder.moreHoriz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    holder.rlFriendMenu.setVisibility(View.VISIBLE);
                    PopupMenu popup = new PopupMenu(context, holder.moreHoriz);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.menu_home_report, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            onItemClickListener.onItemClick(position, AppConstants.TYPE_MORE_REPORT);
                            return true;
                        }
                    });

                    popup.show();
                }
            });

            holder.commentsBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(position, AppConstants.TYPE_COMMENTS);
                }
            });

//            holder.rlFriendMenu.setVisibility(View.GONE);
//            holder.txtMenuReport.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    holder.rlFriendMenu.setVisibility(View.GONE);
//                    onItemClickListener.onItemClick(position, AppConstants.TYPE_MORE_REPORT);
//                }
//            });

            holder.rlRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.rlFriendMenu.setVisibility(View.GONE);
                }
            });

            holder.media_container.setOnTouchListener(new View.OnTouchListener() {
                private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        if(! memoryModel.getLike()){
                            callLikeApi(holder, memoryModel);
                        }else{
                            callUnLikeApi(holder, memoryModel);
                        }
                        return super.onDoubleTap(e);
                    }

                });

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });

            holder.imgHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callLikeApi(holder, memoryModel);
                }
            });

            holder.imgRedHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callUnLikeApi(holder, memoryModel);
                }
            });

            holder.imgPost.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                    onItemClickListener.onItemClick(position, -1);
                    return false;
                }

                @Override
                public boolean onDoubleTap(MotionEvent motionEvent) {
                    if(! memoryModel.getLike()){
                        callLikeApi(holder, memoryModel);
                    }else{
                        callUnLikeApi(holder, memoryModel);
                    }
                    return false;
                }

                @Override
                public boolean onDoubleTapEvent(MotionEvent motionEvent) {
                    return false;
                }
            });

            holder.header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(position, -1);
                }
            });

            holder.txtLikes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(position, AppConstants.TYPE_LIKES);
                }
            });

            holder.txtFullName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(position, AppConstants.TYPE_CIRCLE_IMG);
                }
            });

            holder.imgUserPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(position, AppConstants.TYPE_CIRCLE_IMG);
                }
            });
            holder.player.setVisibility(View.GONE);
            holder.imgPost.setVisibility(View.GONE);
            holder.rlThumb.setVisibility(View.GONE);
            holder.imgUnMute.setVisibility(View.GONE);
            holder.imgMute.setVisibility(View.GONE);

            holder.imgMute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.imgUnMute.setVisibility(View.VISIBLE);
                    holder.imgMute.setVisibility(View.GONE);
                    if(mExoPlayerData[position].exoPlayer != null){
                        mExoPlayerData[position].exoPlayer.setVolume(currentVolume[position]);
                    }
                }
            });

            holder.imgUnMute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.imgUnMute.setVisibility(View.GONE);
                    holder.imgMute.setVisibility(View.VISIBLE);
                    if(mExoPlayerData[position].exoPlayer != null){
                        mExoPlayerData[position].exoPlayer.setVolume(0f);
                    }
                }
            });

            holder.llViewUsers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(position, AppConstants.TYPE_VIEWS);
                }
            });

            holder.txtDaysAgo.setText(memoryModelList.get(position).getAgo());

            if (Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(Bundle_Identifier.ArabicLang)) {
                holder.txtDaysAgo.setText(memoryModelList.get(position).getAgoArabic());
            }

            holder.txtCaption.setText(memoryModelList.get(position).getCaption());

            if( memoryModelList.get(position).getProfile_picture() != null && ! memoryModelList.get(position).getProfile_picture().isEmpty()){
                try{
                    Picasso.with(context).load(ApiClass.ImageBaseUrl +memoryModelList.get(position).getProfile_picture()).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgUserPic);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            else
                Picasso.with(context).load(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgUserPic);

            holder.txtFullName.setText(memoryModelList.get(position).getFull_name());
            holder.txtComments.setText(context.getResources().getString(R.string.comments_post)+" "+ memoryModelList.get(position).getComment_count());
            holder.txtLikes.setText(context.getResources().getString(R.string.likes_post)+" "+memoryModelList.get(position).getLike_count());
            holder.txtView.setText(""+memoryModelList.get(position).getView_count());

            if( memoryModelList.get(position).getUrl() != null && ! memoryModelList.get(position).getUrl().isEmpty()) {
                if (memoryModelList.get(position).getMediaType().equalsIgnoreCase("image")) {
                    holder.imgPost.setVisibility(View.VISIBLE);
//                    showFullImage(holder.imgPost);
                    try {
                        Picasso.with(context).load(memoryModelList.get(position).getUrl()).fit().centerCrop().into(holder.imgPost);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else if (memoryModelList.get(position).getMediaType().equalsIgnoreCase("video")) {

                    holder.player.setVisibility(View.VISIBLE);
                    holder.imgMute.setVisibility(View.VISIBLE);

                    if(mExoPlayerData == null || mExoPlayerData[position] == null)
                        return;

                    if(mExoPlayerData[position].isFirst){
                        mExoPlayerData[position].isFirst = false;

                        holder.rlThumb.setVisibility(View.VISIBLE);
                        holder.player.setPlayer(mExoPlayerData[position].exoPlayer);
                        try {
                            Picasso.with(context).load(memoryModelList.get(position).getThumbnail()).fit().centerInside().into(holder.thumbnail);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(new CacheDataSourceFactory
                                (context, 100 * 1024 * 1024, 5 * 1024 * 1024, memoryModelList.get(position).getId()
                                )).createMediaSource(Uri.parse( memoryModelList.get(position).getUrl()));
                        mExoPlayerData[position].exoPlayer.prepare(mediaSource, true, false);
                        Log.e("loading", position+"");
                        mExoPlayerData[position].exoPlayer.addListener(new VideoPlayerEventListener(mExoPlayerData[position].exoPlayer, holder.rlThumb, position) {
                            @Override
                            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                                switch (playbackState) {
                                    case Player.STATE_BUFFERING:
                                    case Player.STATE_READY:
                                        Log.e("loadingReady", position+"");
                                        if (rlThumb != null) {
                                            rlThumb.setVisibility(View.GONE);
                                        }
                                        break;
                                    case Player.STATE_ENDED:
                                        exoPlayer.setPlayWhenReady(false);
                                        exoPlayer.seekTo(0);
                                        break;

                                    default:
                                        break;
                                }

                            }

                        });
                        if(position == 0 && !isFromNotification){
                            mRecyclerView.playPosition = 1;
                            mExoPlayerData[position].exoPlayer.setPlayWhenReady(true);
                        }else{
                            mExoPlayerData[position].exoPlayer.setPlayWhenReady(false);
                        }
                    }else{
                        holder.player.setPlayer(mExoPlayerData[position].exoPlayer);
                    }
                }
            }

        } else if (holder1 instanceof Header) {
            Header holder = (Header) holder1;
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
            holder.rvDailyUser.setLayoutManager(mLayoutManager);
            holder.rvDailyUser.setAdapter(dailyUserAdapter);
            if(dataUserList != null  && dataUserList.size() > 0) {
                holder.txtNoData.setVisibility(View.GONE);
            } else {
                holder.txtNoData.setVisibility(View.VISIBLE);
            }
        }
    }

    public void showFullImage(final PhotoView mPhotoView) {
        float photoViewWidth = mPhotoView.getWidth();
        float photoViewHeight = mPhotoView.getHeight();

        float viewScale = mPhotoView.getScale();
        RectF rect = mPhotoView.getDisplayRect();

        // Compute initial base rect
        float baseRectWidth = (rect.right - rect.left) / viewScale;
        float baseRectHeight = (rect.bottom - rect.top) / viewScale;

        // Compute medium scale for full size
        double mediumScale, currentScale;
        if (baseRectHeight > baseRectWidth) {
            mediumScale = photoViewWidth / baseRectWidth;
        } else {
            mediumScale = photoViewHeight / baseRectHeight;
        }

        mediumScale = Math.round(mediumScale * 100.0) / 100.0;
        currentScale = Math.round(mPhotoView.getScale() * 100.0) / 100.0;

        // Apply new scale: minimum or medium
        if (currentScale < mediumScale) {
            mPhotoView.setScale((float) mediumScale);

        } else {
            mPhotoView.setScale(mPhotoView.getMinimumScale());
        }
    }

    private void callUnLikeApi(VideoPlayerViewHolder holder, MemoryModel memoryModel){
        ProgressDialog.showProgress(context);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().USER_ID, SavePref.getInstance(context).getUserdetail().getId().toString());

        mparams.put("media_id", memoryModel.getId());

        mparams.put("like", 0);

        Call<MediaLikeReponse> call = mApiInterface.mediaLikeService(mparams);

        call.enqueue(new Callback<MediaLikeReponse>() {
            @Override
            public void onResponse(Call<MediaLikeReponse> call, Response<MediaLikeReponse> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful())
                {
                    MediaLikeReponse likeReponse = response.body();
                    if(likeReponse!=null){
                        if(likeReponse.getStatus().equalsIgnoreCase("true")){
                            isLikeApi = true;
                            unlikeUI(holder, memoryModel);
                        }
                    }
                } else {
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<MediaLikeReponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    private void callLikeApi(VideoPlayerViewHolder holder, MemoryModel memoryModel){
        ProgressDialog.showProgress(context);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().USER_ID, SavePref.getInstance(context).getUserdetail().getId().toString());

        mparams.put("media_id", memoryModel.getId());

        mparams.put("like", 1);

        Call<MediaLikeReponse> call = mApiInterface.mediaLikeService(mparams);

        call.enqueue(new Callback<MediaLikeReponse>() {
            @Override
            public void onResponse(Call<MediaLikeReponse> call, Response<MediaLikeReponse> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful())
                {
                    MediaLikeReponse likeReponse = response.body();
                    if(likeReponse!=null){
                        if(likeReponse.getStatus().equalsIgnoreCase("true")){
                            isLikeApi = true;
                            likeUI(holder, memoryModel);
                        }

                    }
                } else {
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<MediaLikeReponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    private boolean isLikeApi;
    public void likeUI(VideoPlayerViewHolder holder, MemoryModel memoryModel){
        memoryModel.setLike(true);
        holder.imgRedHeart.setVisibility(View.VISIBLE);
        holder.imgHeart.setVisibility(View.GONE);
        if(isLikeApi){
            holder.imgBigRedHeart.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    holder.imgBigRedHeart.setVisibility(View.GONE);
                }
            }, 1000);
            isLikeApi = false;
            memoryModel.setLike(true);
            memoryModel.setLike_count(memoryModel.getLike_count() + 1);
            holder.txtLikes.setText(context.getResources().getString(R.string.likes_post)+" "+(memoryModel.getLike_count()));
        }

    }

    public void unlikeUI(VideoPlayerViewHolder holder, MemoryModel memoryModel){
        memoryModel.setLike(false);
        holder.imgBigRedHeart.setVisibility(View.GONE);
        holder.imgRedHeart.setVisibility(View.GONE);
        holder.imgHeart.setVisibility(View.VISIBLE);

        if(isLikeApi && memoryModel.getLike_count() > 0){
            memoryModel.setLike(false);
            memoryModel.setLike_count(memoryModel.getLike_count() - 1);
            holder.txtLikes.setText(context.getResources().getString(R.string.likes_post)+" "+(memoryModel.getLike_count()));
            isLikeApi = false;
        }
    }

    private void webviewPlay(final WebView webView, String url){
        WebSettings webSettings = webView.getSettings();
        webView.setBackgroundColor(Color.BLACK);
        webView.setWebViewClient(new WebViewClient());
        webSettings.setSupportMultipleWindows(true);
        if(Build.VERSION.SDK_INT>17) {
            webSettings.setMediaPlaybackRequiresUserGesture(false);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setWebContentsDebuggingEnabled(true);
        }
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        String html ="<video controls width=\"100%\" height=\"100%\" src=\""+url+"\"/>";
        webView.loadData(html, "text/html", "UTF-8");
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public int getItemCount() {
        if(memoryModelList != null)
            return memoryModelList.size()+1;
        else if(dataUserList != null && dataUserList.size() > 0){
            return 1;
        }
        return 0;
    }

    public void setData( List<MemoryModel> list, List<FriendListDailiesOfFriends> list1, boolean isFromNotification){
        this.memoryModelList = list;
        this.dataUserList = list1;
        this.dailyUserAdapter.setData(list1);

        this.isFromNotification = isFromNotification;

        if(list != null){
            mExoPlayerData = new ExoPlayerData[list.size()];
            currentVolume = new float[list.size()];

            for(int i=0; i< memoryModelList.size(); i++){
                if(memoryModelList.get(i).getMediaType().equalsIgnoreCase("video")){
                    SimpleExoPlayer exoPlayer = new SimpleExoPlayer.Builder(context).build();
                    mExoPlayerData[i] = new ExoPlayerData(exoPlayer, true);
                    currentVolume[i] = mExoPlayerData[i].exoPlayer.getVolume();
                    mExoPlayerData[i].exoPlayer.setVolume(0f);
                    mExoPlayerData[i].exoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
                }
            }
            mRecyclerView.setData(mExoPlayerData);
        }
        this.notifyDataSetChanged();
    }


    public interface OnDailyItemClickListener {
        void onItemClick(int index, int type);
    }

    public interface OnGroupUserClickListener {
        void onGroupUserClick(int index);
    }


    public class VideoPlayerViewHolder extends RecyclerView.ViewHolder {
        public com.github.chrisbanes.photoview.PhotoView imgPost;
        public RelativeLayout rlRow;
        public CustomTextView txtLikes, txtFullName;
        public CustomTextView txtComments;
        public CustomTextView txtDaysAgo;
        public LinearLayout llLikesBox;
        public CustomTextView txtCaption, txtView;
        public com.google.android.exoplayer2.ui.PlayerView player;
        public com.mikhaellopez.circularimageview.CircularImageView imgUserPic;
        public ImageView thumbnail;
        public ProgressBar progressBar;
        public SimpleExoPlayer exoPlayer;
        public RelativeLayout rlThumb;
        private CustomTextView label;
        public View item;
        private CustomTextView txtMenuReport;
        private RelativeLayout rlFriendMenu;
        private ImageView moreHoriz;
        private RelativeLayout header;
        private LinearLayout commentsBox;
        private ImageView imgHeart;
        private ImageView imgRedHeart;
        private ImageView imgBigRedHeart;
        private RelativeLayout media_container;
        private ImageView imgMute, imgUnMute;
        private LinearLayout llViewUsers;

        public VideoPlayerViewHolder(View view) {
            super(view);
            item = view;
            llViewUsers = view.findViewById(R.id.llViewUsers);
            label = view.findViewById(R.id.labelMemory);
            rlRow = view.findViewById(R.id.rlRow);
            imgPost = view.findViewById(R.id.imgPost);
            txtLikes = view.findViewById(R.id.txtLikes);
            txtComments = view.findViewById(R.id.txtComments);
            txtDaysAgo = view.findViewById(R.id.txtDaysAgo);
            txtFullName = view.findViewById(R.id.txtFullName);
            llLikesBox = view.findViewById(R.id.likesBox);
            imgUserPic = view.findViewById(R.id.imgProfile);
            txtCaption = view.findViewById(R.id.txtCaption);
            player = view.findViewById(R.id.player);
            txtView = view.findViewById(R.id.txtView);
            thumbnail = view.findViewById(R.id.thumbnail);
            progressBar = view.findViewById(R.id.progressBar);
            rlThumb = view.findViewById(R.id.rlThumb);
            txtMenuReport = view.findViewById(R.id.txtMenuReport);
            rlFriendMenu = view.findViewById(R.id.rlFriendMenu);
            moreHoriz = view.findViewById(R.id.moreHoriz);
            header = view.findViewById(R.id.header);
            commentsBox = view.findViewById(R.id.commentsBox);
            imgHeart = view.findViewById(R.id.imgHeart);
            imgRedHeart = view.findViewById(R.id.imgRedHeart);
            imgBigRedHeart = view.findViewById(R.id.imgBigRedHeart);
            media_container = view.findViewById(R.id.media_container);
            imgMute = view.findViewById(R.id.imgMute);
            imgUnMute = view.findViewById(R.id.imgUnMute);
        }
    }

    public class Header extends RecyclerView.ViewHolder {
        private RecyclerView rvDailyUser;
        private CustomTextView txtNoData;
        public Header(View view) {
            super(view);
            rvDailyUser = view.findViewById(R.id.rvDailiesUsers);
            txtNoData = view.findViewById(R.id.txtNoDataUser);
        }
    }
}