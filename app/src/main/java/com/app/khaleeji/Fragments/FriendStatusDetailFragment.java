package com.app.khaleeji.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.khaleeji.Activity.BaseActivity;
import com.app.khaleeji.Activity.MainActivity;
import com.app.khaleeji.Adapter.CommentAdapter;
import com.app.khaleeji.Adapter.FriendStatusHistoryAdapter;
import com.app.khaleeji.Adapter.StatusCommentAdapter;
import com.app.khaleeji.Adapter.StatusReplyCommentAdapter;
import com.app.khaleeji.BroadCast.MessageEvent;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.GetSlctdMediaComments;
import com.app.khaleeji.databinding.FragmentFriendStatusBinding;
import com.app.khaleeji.databinding.FragmentStatusDetailBinding;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.AppConstants;
import CustomView.ExoPlayerHelper;
import Model.StatusCommentModel;
import Model.StatusModel;
import Model.StatusReplyModel;
import Utility.ApiClass;
import Utility.Fragment_Process;
import Utility.ProgressDialog;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FriendStatusDetailFragment extends BaseFragment {

    private FragmentStatusDetailBinding mbinding;
    private StatusModel statusModel;
    private boolean isFromMe;
    private StatusCommentAdapter mCommentAdapter;
    private  StatusReplyCommentAdapter statusReplyCommentAdapter;
    private List<StatusCommentModel> statusCommentModelList;
    private List<StatusReplyModel> statusReplyModelList;
    private boolean isSendReply;
    private StatusCommentModel statusCommentModel;

    public FriendStatusDetailFragment() {

    }

    public FriendStatusDetailFragment(StatusModel statusModel, boolean isFromMe) {
        this.statusModel = statusModel;
        this.isFromMe = isFromMe;
    }

    public static FriendStatusDetailFragment newInstance() {
        FriendStatusDetailFragment fragment = new FriendStatusDetailFragment();

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
        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_status_detail, container, false);
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

        mbinding.imgMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)mActivity).openMessageFragment();
            }
        });

        mbinding.title.setText(statusModel.username + " "+getResources().getString(R.string.status));

        try {
            SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Date date = dateParser.parse(statusModel.created_at);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            mbinding.txtDate.setText(sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(statusModel.profile_picture == null){
            Glide.with(mActivity).load(R.drawable.profile_placeholder).into(mbinding.imgUserPic);
        }else{
            Glide.with(mActivity).load(ApiClass.ImageBaseUrl +statusModel.profile_picture).into(mbinding.imgUserPic);
        }

        mbinding.readBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new StatusViewersFragment(statusModel), mActivity, R.id.framelayout_main);
            }
        });

        mbinding.txtLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new StatusLikesFragment(statusModel), mActivity, R.id.framelayout_main);
            }
        });

        if(statusModel.likeBefore){
            mbinding.imgRedHeart.setVisibility(View.VISIBLE);
            mbinding.imgHeart.setVisibility(View.GONE);
        }else{
            mbinding.imgRedHeart.setVisibility(View.GONE);
            mbinding.imgHeart.setVisibility(View.VISIBLE);
        }
        mbinding.txtLikes.setText(mActivity.getResources().getString(R.string.likes)+ " "+statusModel.likes_count);
        mbinding.txtDesc.setText(mActivity.getResources().getString(R.string.comments_post)+ " "+statusModel.comments_count);
        mbinding.txtEye.setText(statusModel.views_count+ "");

        SpannableString tag = getTags(statusModel.text, "2");
        mbinding.txtDesc.setText(tag == null ? "" : tag);
        mbinding.txtDesc.setMovementMethod(LinkMovementMethod.getInstance());

        mbinding.moreHoriz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mActivity, mbinding.moreHoriz);
                //Inflating the Popup using xml file
                if(statusModel.user_id.equals(String.valueOf(SavePref.getInstance(mActivity).getUserdetail().getId()))){
                    popup.getMenuInflater().inflate(R.menu.menu_msg, popup.getMenu());
                }else{
                    popup.getMenuInflater().inflate(R.menu.menu_home_report, popup.getMenu());
                }

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(statusModel.user_id.equals(String.valueOf(SavePref.getInstance(mActivity).getUserdetail().getId()))){
                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mActivity, R.style.MyAlertDialogStyle);
                            builder.setTitle(getResources().getString(R.string.app_name));
                            builder.setMessage(getString(R.string.are_you_sure_want_to_remove_status));
                            builder.setPositiveButton(getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    callRemoveStatus(statusModel);
                                }
                            });
                            builder.setNegativeButton(getString(R.string.txt_no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                        }else{
                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mActivity, R.style.MyAlertDialogStyle);
                            builder.setTitle(getResources().getString(R.string.app_name));
                            builder.setMessage(getString(R.string.are_you_sure_want_to_report_status));
                            builder.setPositiveButton(getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    callReportStatus(statusModel);
                                }
                            });
                            builder.setNegativeButton(getString(R.string.txt_no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                        }

                        return true;
                    }
                });
                popup.show();

            }
        });

        if(SavePref.getInstance(mActivity).getNewMsgNotification()){
            mbinding.imgBadge.setVisibility(View.VISIBLE);
        }else{
            mbinding.imgBadge.setVisibility(View.INVISIBLE);
        }

        mbinding.imgBack.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                MessageEvent messageEvent = new MessageEvent();
                messageEvent.setType(MessageEvent.MessageType.STATUS_REFRESH);
                EventBus.getDefault().post(messageEvent);
                ((MainActivity)mActivity).back();
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

        mbinding.imgUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(statusModel.user_id != null){
                    if( !statusModel.user_id.equals(String.valueOf(SavePref.getInstance(mActivity).getUserdetail().getId()))){
                        Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new FriendProfileFragment(Integer.parseInt(statusModel.user_id)), mActivity, R.id.framelayout_main);
                    }else{
                        Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), ProfileFragment.newInstance(), mActivity, R.id.framelayout_main);
                    }
                }
            }
        });

        mbinding.imgHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callLikeStatus(true);
            }
        });

        mbinding.imgRedHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callLikeStatus(false);
            }
        });


        statusReplyCommentAdapter = new StatusReplyCommentAdapter(mActivity, new StatusReplyCommentAdapter.ItemClickListener() {
            @Override
            public void onStatusReplyLike(StatusReplyModel statusReplyModel) {
                callLikeStatusReply(statusReplyModel);
            }

            @Override
            public void onProfile(StatusReplyModel statusReplyModel) {
                if(statusReplyModel.user_id != null){
                    if( !statusReplyModel.user_id.equals(String.valueOf(SavePref.getInstance(mActivity).getUserdetail().getId()))){
                        Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new FriendProfileFragment(Integer.parseInt(statusReplyModel.user_id)), mActivity, R.id.framelayout_main);
                    }else{
                        Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), ProfileFragment.newInstance(), mActivity, R.id.framelayout_main);
                    }
                }
            }

            @Override
            public void onRemoveStatusReply(StatusReplyModel statusReplyModel) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mActivity, R.style.MyAlertDialogStyle);
                builder.setTitle(getResources().getString(R.string.app_name));
                builder.setMessage(getString(R.string.are_you_sure_want_to_remove_status_reply));
                builder.setPositiveButton(getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callRemoveStatusCommentReply(statusReplyModel);
                    }
                });
                builder.setNegativeButton(getString(R.string.txt_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        mCommentAdapter = new StatusCommentAdapter(mActivity, statusReplyCommentAdapter, new StatusCommentAdapter.ItemClickListener() {
            @Override
            public void onRemoveStatusComment(StatusCommentModel statusCommentModel) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mActivity, R.style.MyAlertDialogStyle);
                builder.setTitle(getResources().getString(R.string.app_name));
                builder.setMessage(getString(R.string.are_you_sure_want_to_remove_status_comment));
                builder.setPositiveButton(getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callRemoveStatusComment(statusCommentModel);
                    }
                });
                builder.setNegativeButton(getString(R.string.txt_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }

            @Override
            public void onProfile(StatusCommentModel statusCommentModel) {
                if(statusCommentModel.user_id != null){
                    if( !statusCommentModel.user_id.equals(String.valueOf(SavePref.getInstance(mActivity).getUserdetail().getId()))){
                        Fragment_Process.addFragment(mActivity.getSupportFragmentManager(), new FriendProfileFragment(Integer.parseInt(statusCommentModel.user_id)), mActivity, R.id.framelayout_main);
                    }else{
                        Fragment_Process.replaceFragment(mActivity.getSupportFragmentManager(), ProfileFragment.newInstance(), mActivity, R.id.framelayout_main);
                    }
                }
            }

            @Override
            public void onReply(StatusCommentModel statusCommentModel) {
                callGetStatusCommentReplies(statusCommentModel);
            }

            @Override
            public void onLike(StatusCommentModel statusCommentModel) {
                callLikeCommentStatus(statusCommentModel);
            }

            @Override
            public void onSendReply(StatusCommentModel model) {
                statusCommentModel =  model;
                isSendReply = true;
                mbinding.etComment.setHint(getResources().getString(R.string.addStatusReply));
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
                    if(isSendReply){
                        callStatusCommentReply();
                        isSendReply = false;
                        mbinding.etComment.setHint(getResources().getString(R.string.addComment));
                    }else{
                        callStatusComment();
                    }

                    mbinding.etComment.setText("");
                }
            }
        });

        callGetStatusComment();
    }

    private void callLikeStatus(boolean isLike){
        ProgressDialog.showProgress(mActivity);
        ApiInterface apiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();
        mparams.put("user_id", SavePref.getInstance(mActivity).getUserdetail().getId());
        mparams.put("like", isLike ? "1" : "0");
        mparams.put("status_id", statusModel.status_id);
        Call<JsonObject> call = apiInterface.likeStatus(mparams);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject != null) {
                        if (jsonObject.get("status").getAsBoolean()) {
                           if(isLike){
                               statusModel.likes_count = statusModel.likes_count + 1;
                               mbinding.txtLikes.setText(getResources().getString(R.string.likes)+ " "+statusModel.likes_count);
                               mbinding.imgRedHeart.setVisibility(View.VISIBLE);
                               mbinding.imgHeart.setVisibility(View.GONE);
                           }else{
                               statusModel.likes_count = statusModel.likes_count - 1;
                               mbinding.txtLikes.setText(getResources().getString(R.string.likes)+ " "+statusModel.likes_count);
                               mbinding.imgRedHeart.setVisibility(View.GONE);
                               mbinding.imgHeart.setVisibility(View.VISIBLE);
                           }
                            MessageEvent messageEvent = new MessageEvent();
                            messageEvent.setType(MessageEvent.MessageType.STATUS_REFRESH);
                            EventBus.getDefault().post(messageEvent);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    private void callGetStatusComment(){
        if(!isNotification)
            ProgressDialog.showProgress(mActivity);
        ApiInterface apiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getStatusComments(statusModel.status_id, statusModel.user_id);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(!isNotification)
                    ProgressDialog.hideprogressbar();
                isNotification = true;
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject != null) {
                        if (jsonObject.get("status").getAsBoolean()) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<StatusCommentModel>>() {}.getType();
                            try{
                                statusCommentModelList = gson.fromJson(jsonObject.get("data").getAsJsonArray(), type);
                                if(statusCommentModelList != null && statusCommentModelList.size() > 0){
                                    mCommentAdapter.setData(statusCommentModelList);
                                    mbinding.txtNoData.setVisibility(View.GONE);
                                }else{
                                    mbinding.txtNoData.setVisibility(View.VISIBLE);
                                    mCommentAdapter.setData(null);
                                }
                            }catch (JsonSyntaxException ex){
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if(!isNotification)
                    ProgressDialog.hideprogressbar();
                isNotification = true;
            }
        });
    }

    private void callGetStatusCommentReplies(StatusCommentModel statusCommentModel){
        ApiInterface apiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.getStatusCommentReplies(statusCommentModel.comment_id);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject != null) {
                        if (jsonObject.get("status").getAsBoolean()) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<StatusReplyModel>>() {}.getType();
                            try{
                                statusReplyModelList = gson.fromJson(jsonObject.get("data").getAsJsonArray(), type);
                                if(statusReplyModelList != null && statusReplyModelList.size() > 0){
                                    statusReplyCommentAdapter.setData(statusReplyModelList);
                                    mCommentAdapter.notifyDataSetChanged();
                                }
                            }catch (JsonSyntaxException ex){
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private boolean isNotification;
    private void callStatusComment(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface apiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();
        mparams.put("user_id", SavePref.getInstance(mActivity).getUserdetail().getId());
        mparams.put("comment", mbinding.etComment.getText().toString());
        mparams.put("status_id", statusModel.status_id);
        Call<JsonObject> call = apiInterface.commentStatus(mparams);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject != null) {
                        if (jsonObject.get("status").getAsBoolean()) {
                            isNotification = true;
                            callGetStatusComment();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    private void callStatusCommentReply(){
        ProgressDialog.showProgress(mActivity);
        ApiInterface apiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();
        mparams.put("user_id", SavePref.getInstance(mActivity).getUserdetail().getId());
        mparams.put("reply", mbinding.etComment.getText().toString());
        if(statusCommentModel != null)
            mparams.put("comment_id", statusCommentModel.comment_id);
        Call<JsonObject> call = apiInterface.statusCommentReply(mparams);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject != null) {
                        if (jsonObject.get("status").getAsBoolean()) {
                            isNotification = true;
                            callGetStatusComment();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    private void callReportStatus(StatusModel statusModel){
        ProgressDialog.showProgress(mActivity);
        ApiInterface apiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();
        mparams.put("user_id", SavePref.getInstance(mActivity).getUserdetail().getId());
        mparams.put("reason", "");
        mparams.put("status_id", statusModel.status_id);
        Call<JsonObject> call = apiInterface.reportStatus(mparams);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject != null) {
                        if (jsonObject.get("status").getAsBoolean()) {
                            MessageEvent messageEvent = new MessageEvent();
                            messageEvent.setType(MessageEvent.MessageType.STATUS_REFRESH);
                            EventBus.getDefault().post(messageEvent);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    private void callRemoveStatus(StatusModel statusModel){
        ProgressDialog.showProgress(mActivity);
        ApiInterface apiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.removeStatus(statusModel.status_id);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject != null) {
                        if (jsonObject.get("status").getAsBoolean()) {
                            MessageEvent messageEvent = new MessageEvent();
                            messageEvent.setType(MessageEvent.MessageType.STATUS_REFRESH);
                            EventBus.getDefault().post(messageEvent);
                            ((MainActivity)mActivity).back();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    private void callRemoveStatusComment(StatusCommentModel statusCommentModel){
        ProgressDialog.showProgress(mActivity);
        ApiInterface apiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.removeStatusComment(statusCommentModel.comment_id);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject != null) {
                        if (jsonObject.get("status").getAsBoolean()) {
                            MessageEvent messageEvent = new MessageEvent();
                            messageEvent.setType(MessageEvent.MessageType.STATUS_REFRESH);
                            EventBus.getDefault().post(messageEvent);
                            ((MainActivity)mActivity).back();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    private void callRemoveStatusCommentReply(StatusReplyModel statusReplyModel){
        ProgressDialog.showProgress(mActivity);
        ApiInterface apiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Call<JsonObject> call = apiInterface.removeStatusCommentReply(statusReplyModel.reply_id);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject != null) {
                        if (jsonObject.get("status").getAsBoolean()) {
                            statusReplyModelList.remove(statusReplyModel);
                            statusReplyCommentAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    private void callLikeCommentStatus(StatusCommentModel statusCommentModel){
        ProgressDialog.showProgress(mActivity);
        ApiInterface apiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();
        mparams.put("user_id", SavePref.getInstance(mActivity).getUserdetail().getId());
        mparams.put("like", !statusCommentModel.likeBefore ? "1" : "0");
        mparams.put("comment_id", statusCommentModel.comment_id);
        Call<JsonObject> call = apiInterface.likeStatusComment(mparams);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject != null) {
                        if (jsonObject.get("status").getAsBoolean()) {
                            statusCommentModel.likeBefore = !statusCommentModel.likeBefore;

                            if(statusCommentModel.likeBefore)
                                statusCommentModel.likes_count = statusCommentModel.likes_count + 1;
                            else
                                statusCommentModel.likes_count = statusCommentModel.likes_count - 1;

                            mCommentAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }

    private void callLikeStatusReply(StatusReplyModel statusReplyModel){
        ProgressDialog.showProgress(mActivity);
        ApiInterface apiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        Map mparams = new HashMap();
        mparams.put("user_id", SavePref.getInstance(mActivity).getUserdetail().getId());
        mparams.put("like", !statusReplyModel.likeBefore ? "1" : "0");
        mparams.put("reply_id", statusReplyModel.reply_id);
        Call<JsonObject> call = apiInterface.likeStatusReplyComment(mparams);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                ProgressDialog.hideprogressbar();
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body().getAsJsonObject();
                    if (jsonObject != null) {
                        if (jsonObject.get("status").getAsBoolean()) {
                            statusReplyModel.likeBefore = !statusReplyModel.likeBefore;
                            if(statusReplyModel.likeBefore){
                                statusReplyModel.likes_count = statusReplyModel.likes_count + 1;
                            }else{
                                statusReplyModel.likes_count = statusReplyModel.likes_count - 1;
                            }
                            statusReplyCommentAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                ProgressDialog.hideprogressbar();
            }
        });
    }
}
