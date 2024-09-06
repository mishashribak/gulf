package com.app.khaleeji.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Adapter.CommentAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.AddCommentResponse;
import com.app.khaleeji.Response.DeleteCommentResponse;
import com.app.khaleeji.Response.GetSlctdMediaComments;
import com.app.khaleeji.Response.GetSlctdMediaData;
import com.app.khaleeji.Response.GetSlctdMediaDetailResponse;
import com.app.khaleeji.Response.MediaLikeReponse;
import com.app.khaleeji.Response.MemoryModel;
import com.app.khaleeji.Response.RemoveMediaResponse;
import com.app.khaleeji.databinding.FragmentMyPostBinding;
import com.squareup.picasso.Picasso;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.AppConstants;
import CustomView.ExoPlayerHelper;
import Utility.ApiClass;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPostFragment extends BaseFragment {

    private Context mContext;
    private FragmentMyPostBinding mbinding;
    private MemoryModel mMemoryModel;
    private ExoPlayerHelper exoPlayerHelper;
    private CommentAdapter mCommentAdapter;
    private GetSlctdMediaData slctdMediaData;
    private boolean isLiked;

    public MyPostFragment(MemoryModel memoryModel) {
        mMemoryModel = memoryModel;
    }

    public static MyPostFragment newInstance(MemoryModel memoryModel) {
        MyPostFragment fragment = new MyPostFragment(memoryModel);
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent (MessageEvent messageEvent) {
        if(messageEvent.getType() == MessageEvent.MessageType.NEW_MSG_NOTIFICATION){
            mbinding.imgBadge.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = mActivity;
        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_post, container, false);
        View rootView = mbinding.getRoot();
        if(Build.VERSION.SDK_INT >= 21)
            rootView.setClipToOutline(true);
        ((BaseActivity)mActivity).setStatusBarColor(R.color.yellow_dark);
        initView();
        return rootView;
    }

    private void initView(){
        ((MainActivity)mActivity).hide();

        if(SavePref.getInstance(mActivity).getNewMsgNotification()){
            mbinding.imgBadge.setVisibility(View.VISIBLE);
        }else{
            mbinding.imgBadge.setVisibility(View.INVISIBLE);
        }


        mbinding.imgBack.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                ((MainActivity)mActivity).back();
                MessageEvent messageEvent = new MessageEvent();
                messageEvent.setType(MessageEvent.MessageType.POST_REFRESH);
                messageEvent.setMemoryModel(mMemoryModel);
                EventBus.getDefault().postSticky(messageEvent);
            }
        });

        mbinding.txtLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopVideo();
                Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new PostLikesFragment(mMemoryModel.getId()),mActivity, R.id.framelayout_main);
            }
        });

        mbinding.rlContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(mActivity);
            }
        });

        mbinding.imgMessage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                ((MainActivity)mActivity).openMessageFragment();
            }
        });

        mbinding.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMemoryModel != null && mMemoryModel.getUser_id() != null) {
                    if(mMemoryModel.getUser_id().intValue() != SavePref.getInstance(mContext).getUserdetail().getId().intValue()){
                        stopVideo();
                        Fragment_Process.addFragment(mActivity.getSupportFragmentManager(),new FriendProfileFragment(mMemoryModel.getUser_id()),mActivity, R.id.framelayout_main);
                    }else{
                        Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), ProfileFragment.newInstance(), mActivity, R.id.framelayout_main);
                    }
                }

            }
        });

        if(mMemoryModel != null){

            SpannableString tag = getTags(mMemoryModel.getCaption(), "3");
            mbinding.txtComments.setText(tag == null ? "" : tag);
            mbinding.txtComments.setMovementMethod(LinkMovementMethod.getInstance());

            mbinding.txtFullName.setText(mMemoryModel.getFull_name());

            mbinding.player.setVisibility(View.GONE);
            mbinding.imgPost.setVisibility(View.GONE);
            mbinding.imgMute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mbinding.imgUnMute.setVisibility(View.VISIBLE);
                    mbinding.imgMute.setVisibility(View.GONE);
                    if(exoPlayerHelper != null){
                        exoPlayerHelper.setUnMute();
                    }
                }
            });
            mbinding.imgUnMute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mbinding.imgUnMute.setVisibility(View.GONE);
                    mbinding.imgMute.setVisibility(View.VISIBLE);
                    if(exoPlayerHelper != null)
                        exoPlayerHelper.setMute();
                }
            });

            if( mMemoryModel.getUrl() != null && ! mMemoryModel.getUrl().isEmpty()){
                if(mMemoryModel.getMediaType().equalsIgnoreCase("image")){
                    mbinding.imgPost.setVisibility(View.VISIBLE);
                    try{
                        Picasso.with(mActivity).load( mMemoryModel.getUrl()).fit().centerCrop().into(mbinding.imgPost);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }else if(mMemoryModel.getMediaType().equalsIgnoreCase("video")){
                    mbinding.player.setVisibility(View.VISIBLE);
                    mbinding.imgUnMute.setVisibility(View.VISIBLE);
                    exoPlayerHelper = new ExoPlayerHelper(mbinding.player, mContext, mMemoryModel.getUrl(), mMemoryModel.getId());
                    exoPlayerHelper.initializePlayer();
                    exoPlayerHelper.setMute();
                }
            }

            if(mMemoryModel.getProfile_picture() != null && ! mMemoryModel.getProfile_picture().isEmpty() ){
                try{
                    Picasso.with(mActivity).load(ApiClass.ImageBaseUrl+mMemoryModel.getProfile_picture()).fit().into(mbinding.imgProfile);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            mbinding.txtLikes.setText(mContext.getResources().getString(R.string.likes)+ " "+mMemoryModel.getLike_count());
            mbinding.txtViewAllComments.setText(mContext.getResources().getString(R.string.view_all) +" "+ mMemoryModel.getComment_count()+" "+mContext.getResources().getString(R.string.view_comments));
            mbinding.txtViewers.setText(""+mMemoryModel.getView_count());
        }

        mbinding.imgHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callLikeApi();
            }
        });

        mbinding.mediaContainer.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    if(! isLiked){
                        callLikeApi();
                    }else{
                        callUnLikeApi();
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

        mbinding.imgPost.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent motionEvent) {
                if(! isLiked){
                    callLikeApi();
                }else{
                    callUnLikeApi();
                }
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent motionEvent) {
                return false;
            }
        });

        mbinding.imgRedHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callUnLikeApi();
            }
        });

        mCommentAdapter = new CommentAdapter(mActivity, mContext, new CommentAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                GetSlctdMediaComments data = slctdMediaData.getComments().get(position);
                removeComment(data.getCommentorId(), data.getCommentId());
            }

            @Override
            public void onProfile(int userId) {
                stopVideo();
                Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new FriendProfileFragment(userId), mActivity, R.id.framelayout_main);
            }
        });

        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        mbinding.rvComments.setLayoutManager(lm);
        mbinding.rvComments.setAdapter(mCommentAdapter);

        mbinding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = mbinding.etComment.getText().toString();
                if( ! str.isEmpty()){
                    postComment(str);
                    mbinding.etComment.setText("");
                }
            }
        });

        mbinding.moreHoriz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mContext, mbinding.moreHoriz);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.menu_msg, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
                        builder.setTitle(getResources().getString(R.string.app_name));
                        builder.setMessage(getString(R.string.are_you_sure_want_to_remove));
                        builder.setPositiveButton(getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                callRemoveMedia();

                            }
                        });
                        builder.setNegativeButton(getString(R.string.txt_no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                        return true;
                    }
                });

                popup.show();
            }
//                mbinding.rlMenu.setVisibility(View.VISIBLE);
//            }
        });

//        mbinding.txtMenuRemove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mbinding.rlMenu.setVisibility(View.GONE);
//                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
//                builder.setTitle(getResources().getString(R.string.app_name));
//                builder.setMessage(getString(R.string.are_you_sure_want_to_remove));
//                builder.setPositiveButton(getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        callRemoveMedia();
//
//                    }
//                });
//                builder.setNegativeButton(getString(R.string.txt_no), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                builder.show();
//            }
//        });

        mbinding.rlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mbinding.rlMenu.setVisibility(View.GONE);
            }
        });

        getCommentsApi();
    }

    private void callRemoveMedia(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = ApiClass.getmApiClass().buildDefaultParams(mActivity);
        mparams.put("media_id", mMemoryModel.getId());
        mparams.put("user_id", SavePref.getInstance(mActivity).getUserdetail().getId().intValue());

        Call<RemoveMediaResponse> call = mApiInterface.removeMediaService(mparams);
        call.enqueue(new Callback<RemoveMediaResponse>() {
            @Override
            public void onResponse(Call<RemoveMediaResponse> call, Response<RemoveMediaResponse> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    final RemoveMediaResponse mBasic_Response = response.body();
                    if(mBasic_Response!=null && isAdded()){
                        if (mBasic_Response.getStatus().equalsIgnoreCase("true")) {
                            MessageEvent messageEvent = new MessageEvent();
                            messageEvent.setType(MessageEvent.MessageType.REMOVE_MEDIA);
                            messageEvent.setMemoryModel(mMemoryModel);
                            EventBus.getDefault().post(messageEvent);
                            ((MainActivity)mActivity).back();
                        }
//                        Toast.makeText(mActivity,mBasic_Response.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else {
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<RemoveMediaResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });

    }

    private void postComment(String comment){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().USER_ID, SavePref.getInstance(mActivity).getUserdetail().getId().toString());
        mparams.put("media_id", mMemoryModel.getId());
        mparams.put("comment", comment);

        Call<AddCommentResponse> call = mApiInterface.addCommentService(mparams);

        call.enqueue(new Callback<AddCommentResponse>() {
            @Override
            public void onResponse(Call<AddCommentResponse> call, Response<AddCommentResponse> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful())
                {
                    AddCommentResponse addCommentResponse = response.body();
                    if(addCommentResponse!=null && isAdded()){
                        if(addCommentResponse.getStatus().equalsIgnoreCase("true")){
                            mMemoryModel.setComment_count(mMemoryModel.getComment_count() + 1);
                            getCommentsApi();
                        }
//                        Utility.AlertDialog.showAlert(mActivity, getString(R.string.app_name), addCommentResponse.getMessage(), getString(R.string.txt_Done), "", false, null, null);
                    }
                } else {
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<AddCommentResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    private void removeComment(int commentor_id, int comment_id ){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();
        mparams.put("commentor_id", commentor_id);
        mparams.put("comment_id",comment_id);
        mparams.put("deleted_by",  SavePref.getInstance(mActivity).getUserdetail().getId().toString());

        Call<DeleteCommentResponse> call = mApiInterface.deleteCommentService(mparams);

        call.enqueue(new Callback<DeleteCommentResponse>() {
            @Override
            public void onResponse(Call<DeleteCommentResponse> call, Response<DeleteCommentResponse> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful())
                {
                    DeleteCommentResponse deleteCommentResponse = response.body();
                    if(deleteCommentResponse!=null && isAdded()){
                        if(deleteCommentResponse.getStatus().equalsIgnoreCase("true")){
                            mMemoryModel.setComment_count(mMemoryModel.getComment_count() - 1);
                            getCommentsApi();
                        }
                    }
                } else {
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<DeleteCommentResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    private void callLikeApi(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().USER_ID, SavePref.getInstance(mActivity).getUserdetail().getId().toString());
        mparams.put("media_id", mMemoryModel.getId());
        mparams.put("like", 1);

        Call<MediaLikeReponse> call = mApiInterface.mediaLikeService(mparams);

        call.enqueue(new Callback<MediaLikeReponse>() {
            @Override
            public void onResponse(Call<MediaLikeReponse> call, Response<MediaLikeReponse> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful())
                {
                    MediaLikeReponse likeReponse = response.body();
                    if(likeReponse!=null && isAdded()){
                        if(likeReponse.getStatus().equalsIgnoreCase("true")){
                            isLikeApi = true;
                            likeUI();
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
    public void likeUI(){
        isLiked = true;
//        mMemoryModel.setLike(true);
        mbinding.imgRedHeart.setVisibility(View.VISIBLE);
        mbinding.imgHeart.setVisibility(View.GONE);
        if(isLikeApi){
            mbinding.imgBigRedHeart.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mbinding.imgBigRedHeart.setVisibility(View.GONE);
                }
            }, 1000);
            isLikeApi = false;
            mMemoryModel.setLike(true);
            mMemoryModel.setLike_count(mMemoryModel.getLike_count() + 1);
            mbinding.txtLikes.setText(mActivity.getResources().getString(R.string.likes_post)+" "+(mMemoryModel.getLike_count()));
        }
    }

    public void unlikeUI(){
//        mMemoryModel.setLike(false);
        isLiked = false;
        mbinding.imgBigRedHeart.setVisibility(View.GONE);
        mbinding.imgRedHeart.setVisibility(View.GONE);
        mbinding.imgHeart.setVisibility(View.VISIBLE);

        if(isLikeApi && mMemoryModel.getLike_count() > 0){
            mMemoryModel.setLike(false);
            mMemoryModel.setLike_count(mMemoryModel.getLike_count() - 1);
            mbinding.txtLikes.setText(mActivity.getResources().getString(R.string.likes_post)+" "+(mMemoryModel.getLike_count()));
            isLikeApi = false;
        }
    }

    private void callUnLikeApi(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().USER_ID, SavePref.getInstance(mActivity).getUserdetail().getId().toString());
        mparams.put("media_id", mMemoryModel.getId());
        mparams.put("like", 0);

        Call<MediaLikeReponse> call = mApiInterface.mediaLikeService(mparams);

        call.enqueue(new Callback<MediaLikeReponse>() {
            @Override
            public void onResponse(Call<MediaLikeReponse> call, Response<MediaLikeReponse> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful())
                {
                    MediaLikeReponse likeReponse = response.body();
                    if(likeReponse!=null && isAdded()){
                        if(likeReponse.getStatus().equalsIgnoreCase("true")){
                            isLikeApi = true;
                            unlikeUI();
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

    private void getCommentsApi(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();
        mparams.put(ApiClass.getmApiClass().USER_ID, SavePref.getInstance(mActivity).getUserdetail().getId().toString());
        mparams.put("media_id", mMemoryModel.getId());

        Call<GetSlctdMediaDetailResponse> call = mApiInterface.getSelectedMediaDetailsService(mparams);

        call.enqueue(new Callback<GetSlctdMediaDetailResponse>() {
            @Override
            public void onResponse(Call<GetSlctdMediaDetailResponse> call, Response<GetSlctdMediaDetailResponse> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful())
                {
                    GetSlctdMediaDetailResponse getSlctdMediaDetailResponse = response.body();

                    if(getSlctdMediaDetailResponse!=null && isAdded()){
                        if (getSlctdMediaDetailResponse.getStatus().equalsIgnoreCase("true")) {

                            slctdMediaData= getSlctdMediaDetailResponse.getData();
                            if(slctdMediaData.getIsLike() == 0){
                                isLiked = false;
//                                mMemoryModel.setLike(false);
                                unlikeUI();
                            }else{
                                isLiked = true;
//                                mMemoryModel.setLike(true);
                                likeUI();
                            }

                            if(slctdMediaData != null  && slctdMediaData.getComments().size() > 0) {
                                mbinding.txtViewAllComments.setText(mContext.getResources().getString(R.string.view_all) +" "+ slctdMediaData.getComments().size()+" "+mContext.getResources().getString(R.string.view_comments));
                                mCommentAdapter.setData(slctdMediaData.getComments());
//                                mbinding.txtNoData.setVisibility(View.GONE);
                            } else {
//                                mbinding.txtNoData.setVisibility(View.VISIBLE);
                                mCommentAdapter.setData(null);
                            }

                        } else {
                            Utility.AlertDialog.showAlert(mActivity, getString(R.string.app_name), getSlctdMediaDetailResponse.getMessage(), getString(R.string.txt_Done), "", false, null, null);
                        }

                    }
                } else {
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<GetSlctdMediaDetailResponse> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        stopVideo();
    }

    private void stopVideo(){
        if(exoPlayerHelper != null)
            exoPlayerHelper.stopPlayer();
    }
}
