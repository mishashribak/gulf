package com.app.khaleeji.Adapter;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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
import android.widget.TextView;

import com.app.khaleeji.Fragments.SearchTabFragments;
import com.google.android.exoplayer2.ExoPlayer;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.MediaLikeReponse;
import com.app.khaleeji.Response.MemoryModel;
import com.app.khaleeji.Response.fetchMemoryPackage.Memory;
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
import CustomView.Video.PostsFriendVideoPlayerRecyclerView;
import CustomView.Video.VideoPlayerEventListener;
import CustomView.Video.VideoPlayerRecyclerView;
import Utility.ApiClass;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendPostsAdapter extends RecyclerView.Adapter<FriendPostsAdapter.MyViewHolder> {

    private List<MemoryModel> listData;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private FragmentActivity mActivity;
    private PostsFriendVideoPlayerRecyclerView mRecyclerView;
    private ExoPlayerData[] mExoPlayerData;
    private float[] currentVolume;
    private int scrollPos;

    public FriendPostsAdapter(FragmentActivity activity, int scrollPos,
                              OnItemClickListener onItemClickListener) {
        mActivity = activity;
        this.context = activity;
        this.scrollPos = scrollPos;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = (PostsFriendVideoPlayerRecyclerView)recyclerView;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_my_posts, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        MemoryModel memoryModel = listData.get(position);


        SpannableString tag = getTags(memoryModel.getCaption(), "3");
        holder.txtCaption.setText(tag == null ? "" : tag);
        holder.txtCaption.setMovementMethod(LinkMovementMethod.getInstance());

        holder.txtFullName.setText(memoryModel.getFull_name());

        if(memoryModel.getLike()){
            likeUI(holder, memoryModel);
        }else{
            unlikeUI(holder, memoryModel);
        }

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
                onItemClickListener.onItemClick(position, AppConstants.TYPE_POST_IMG);
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

        holder.player.setVisibility(View.GONE);
        holder.imgUnMute.setVisibility(View.GONE);
        holder.imgMute.setVisibility(View.GONE);
        holder.imgPost.setVisibility(View.GONE);
        holder.rlThumb.setVisibility(View.GONE);

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
        if( memoryModel.getUrl() != null && ! memoryModel.getUrl().isEmpty()) {
            if (memoryModel.getMediaType().equalsIgnoreCase("image")) {
                holder.imgPost.setVisibility(View.VISIBLE);
                try {
                    Picasso.with(context).load(memoryModel.getUrl()).fit().centerCrop().into(holder.imgPost);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (memoryModel.getMediaType().equalsIgnoreCase("video")) {
                holder.player.setVisibility(View.VISIBLE);
                holder.imgMute.setVisibility(View.VISIBLE);
                holder.imgUnMute.setVisibility(View.GONE);

                if(mExoPlayerData == null || mExoPlayerData[position] == null)
                    return;

                if(mExoPlayerData[position].isFirst){
                    mExoPlayerData[position].isFirst = false;
                    holder.rlThumb.setVisibility(View.VISIBLE);
                    holder.player.setPlayer(mExoPlayerData[position].exoPlayer);
                    try {
                        Picasso.with(context).load(memoryModel.getThumbnail()).fit().centerInside().into(holder.thumbnail);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(new CacheDataSourceFactory(context, 100 * 1024 * 1024, 5 * 1024 * 1024,
                            listData.get(position).getId())).createMediaSource(Uri.parse( listData.get(position).getUrl()));
                    mExoPlayerData[position].exoPlayer.prepare(mediaSource, true, false);
                    mExoPlayerData[position].exoPlayer.addListener(new VideoPlayerEventListener(mExoPlayerData[position].exoPlayer, holder.rlThumb) {
                        @Override
                        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                            switch (playbackState) {
                                case Player.STATE_BUFFERING:
                                case Player.STATE_READY:
                                    if (rlThumb != null) {
                                        rlThumb.setVisibility(View.GONE);
                                    }
                                    break;
                                case Player.STATE_ENDED:
                                    exoPlayer.seekTo(0);
                                    exoPlayer.setPlayWhenReady(false);
                                    break;

                                default:
                                    break;
                            }

                        }
                    });
                    if(scrollPos == position){
                        mRecyclerView.playPosition = 0;
                        mExoPlayerData[position].exoPlayer.setPlayWhenReady(true);
                    }else{
                        mExoPlayerData[position].exoPlayer.setPlayWhenReady(false);
                    }
                }else{
                    holder.player.setPlayer(mExoPlayerData[position].exoPlayer);
                }

            }
        }

        if(memoryModel.getProfile_picture() != null && ! memoryModel.getProfile_picture().isEmpty() ){
            try{
                Picasso.with(mActivity).load(ApiClass.ImageBaseUrl+memoryModel.getProfile_picture()).placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgUserPic);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        holder.txtLikes.setText(context.getResources().getString(R.string.likes)+ " "+memoryModel.getLike_count());
        holder.txtViewAllComments.setText(context.getResources().getString(R.string.view_all) +" "+ memoryModel.getComment_count()+" "+context.getResources().getString(R.string.view_comments));
        holder.txtView.setText(""+memoryModel.getView_count());

        holder.txtPostDate.setText(memoryModel.getAgo());
        if (Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(Bundle_Identifier.ArabicLang)) {
            holder.txtPostDate.setText(memoryModel.getAgoArabic());
        }

        holder.txtLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position, AppConstants.TYPE_LIKES);
            }
        });

        holder.imgUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position, AppConstants.TYPE_CIRCLE_IMG);
            }
        });


        holder.commentsBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position, AppConstants.TYPE_POST_IMG);
            }
        });

        holder.rlFriendMenu.setVisibility(View.GONE);

        holder.moreHoriz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
//                holder.rlFriendMenu.setVisibility(View.VISIBLE);
            }
        });

//        holder.txtMenuReport.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                holder.rlFriendMenu.setVisibility(View.GONE);
//                onItemClickListener.onItemClick(position, AppConstants.TYPE_MORE_REPORT);
//            }
//        });

        holder.rlRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.rlFriendMenu.setVisibility(View.GONE);
            }
        });

        holder.llViewUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(position, AppConstants.TYPE_VIEWS);
            }
        });
    }

    @Override
    public int getItemCount() {
        if( listData != null)
            return listData.size();
        return 0;
    }

    public void setData(List<MemoryModel>  list){
        this.listData = list;
        if(list != null && list.size()>0){
            mExoPlayerData = new ExoPlayerData[list.size()];
            currentVolume = new float[list.size()];
            for(int i=0; i< listData.size(); i++){
                if(listData.get(i).getMediaType().equalsIgnoreCase("video")){
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

    private void callLikeApi(MyViewHolder holder, MemoryModel memoryModel){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().USER_ID, SavePref.getInstance(mActivity).getUserdetail().getId().toString());

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
    public void likeUI(MyViewHolder holder, MemoryModel memoryModel){
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

    public void unlikeUI(MyViewHolder holder, MemoryModel memoryModel){
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

    private void callUnLikeApi(MyViewHolder holder, MemoryModel memoryModel){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().USER_ID, SavePref.getInstance(mActivity).getUserdetail().getId().toString());

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

    public interface OnItemClickListener {
        void onItemClick( int index, int type);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CustomTextView txtViewAllComments, txtView;
        public CustomTextView txtLikes, txtCaption;
        public CustomTextView txtPostDate, txtFullName;
        public LinearLayout llLikesBox, llViewUsers;
        public com.github.chrisbanes.photoview.PhotoView imgPost;
        public com.mikhaellopez.circularimageview.CircularImageView imgUserPic;
        public com.google.android.exoplayer2.ui.PlayerView player;
        public ImageView thumbnail;
        public ProgressBar progressBar;
        public View parent;
        public ExoPlayer exoPlayer;
        public RelativeLayout rlThumb;
        private LinearLayout commentsBox;
        private CustomTextView txtMenuReport;
        private RelativeLayout rlFriendMenu, rlRow;
        private ImageView moreHoriz;
        private ImageView imgHeart;
        private ImageView imgRedHeart;
        private ImageView imgBigRedHeart;
        private RelativeLayout media_container;
        private ImageView imgMute, imgUnMute;

        public MyViewHolder(View view) {
            super(view);
            txtLikes = view.findViewById(R.id.txtLikes);
            llViewUsers = view.findViewById(R.id.llViewUsers);
            commentsBox = view.findViewById(R.id.commentsBox);
            txtViewAllComments = view.findViewById(R.id.txtViewAllComments);
            txtPostDate = view.findViewById(R.id.txtPostDate);
            llLikesBox = view.findViewById(R.id.likesBox);
            imgUserPic = view.findViewById(R.id.imgProfile);
            imgPost = view.findViewById(R.id.imgPost);
            txtCaption = view.findViewById(R.id.txtCaption);
            player = view.findViewById(R.id.player);
            txtFullName = view.findViewById(R.id.txtFullName);
            txtView = view.findViewById(R.id.txtView);
            thumbnail = view.findViewById(R.id.thumbnail);
            progressBar = view.findViewById(R.id.progressBar);
            parent = view;
            rlThumb = view.findViewById(R.id.rlThumb);
            exoPlayer= new SimpleExoPlayer.Builder(context).build();
            player.setPlayer(exoPlayer);
            moreHoriz = view.findViewById(R.id.moreHoriz);
            rlRow = view.findViewById(R.id.rlRow);
            rlFriendMenu = view.findViewById(R.id.rlFriendMenu);
            txtMenuReport = view.findViewById(R.id.txtMenuReport);
            imgHeart = view.findViewById(R.id.imgHeart);
            imgRedHeart = view.findViewById(R.id.imgRedHeart);
            imgBigRedHeart = view.findViewById(R.id.imgBigRedHeart);
            media_container = view.findViewById(R.id.media_container);
            imgMute = view.findViewById(R.id.imgMute);
            imgUnMute = view.findViewById(R.id.imgUnMute);
        }
    }

    public SpannableString getTags(String str, String hashType){
        if(str != null &&! str.isEmpty()){
            SpannableString ss = new SpannableString(str);
            int startPos;
            int len = 0;
            while ((startPos = str.indexOf("#")) != -1){
                int endPos;
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View textView) {
                        TextView tv = (TextView) textView;
                        Spanned s = (Spanned) tv.getText();
                        int start = s.getSpanStart(this);
                        int end = s.getSpanEnd(this);
                        Log.d("ProfileFragment", "onClick [" + s.subSequence(start, end) + "]");
                        Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new SearchTabFragments(s.subSequence(start, end).toString(), hashType), mActivity, R.id.framelayout_main);
                    }
                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                    }
                };
                len = len + startPos;
                str = str.substring(startPos);
                if ((endPos = str.indexOf(" ")) != -1){
                    ss.setSpan(clickableSpan, len, len + endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }else{
                    ss.setSpan(clickableSpan, len, len + str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    return ss;
                }
                len = len + endPos + 1;
                str = str.substring(endPos + 1);
            }

            return ss;
        }
        return null;
    }
}