package com.app.khaleeji.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import androidx.databinding.DataBindingUtil;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.Basic_Response;
import com.app.khaleeji.Response.fetchDailiesOfFriends.FriendListDailiesOfFriends;
import com.app.khaleeji.databinding.ActivityDailyCommentBinding;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import ApiRequest.ApiInterface;
import ApiRequest.RequestClient;
import Constants.AppConstants;
import CustomView.Video.CacheDataSourceFactory;
import Model.ChatData;
import Model.LastMessage;
import Utility.ApiClass;
import Utility.ChatHelper;
import Utility.SavePref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static Constants.AppConstants.MEDIA_TYPE_COMMENT;


public class DailyCommentActivity extends BaseActivity {

    private ActivityDailyCommentBinding mbinding;
    private String mediaType;
    private String mediaUrl;
    private String thumbImage;
    private FriendListDailiesOfFriends friendData;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    private DatabaseReference postsRef;
    private SimpleExoPlayer exoPlayer;
    private String roomId;
    private boolean isFromChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(R.color.yellow_dark);

        mbinding = DataBindingUtil.setContentView(this, R.layout.activity_daily_comment);

        final Intent intent = getIntent();

        if (intent.getExtras() != null) {
            mediaType =  intent.getExtras().getString("mediaType");
            mediaUrl = intent.getExtras().getString("mediaUrl");
            thumbImage = intent.getExtras().getString("thumbImage");
            isFromChat = intent.getExtras().getBoolean("isFromChat");
            friendData = (FriendListDailiesOfFriends) intent.getExtras().getSerializable("friendData");

            if(mediaType.equalsIgnoreCase("image")){
                mbinding.image.setVisibility(View.VISIBLE);
                mbinding.videoplayer.setVisibility(View.GONE);
                try{
                    Picasso.with(this).load(mediaUrl).fit().centerCrop().into(mbinding.image);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }else{
                mbinding.image.setVisibility(View.GONE);
                mbinding.videoplayer.setVisibility(View.VISIBLE);
                exoPlayer= new SimpleExoPlayer.Builder(this).build();
                mbinding.videoplayer.setPlayer(exoPlayer);
                ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(new CacheDataSourceFactory
                        (this, 100 * 1024 * 1024, 5 * 1024 * 1024, 1
                        )).createMediaSource(Uri.parse( mediaUrl));
                exoPlayer.prepare(mediaSource, true, false);
                exoPlayer.setPlayWhenReady(true);
            }
        }

        if(isFromChat){
            mbinding.llSendComment.setVisibility(View.GONE);
        }else{
            showSoftKeyboard();
            mbinding.llSendComment.setVisibility(View.VISIBLE);
            mbinding.sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!mbinding.messageArea.getText().toString().isEmpty()){
                        sendMessage(mbinding.messageArea.getText().toString());
                        finish();
                    }
                }
            });
            initFirebase();
        }

        mbinding.llView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mbinding.videoplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void showSoftKeyboard() {
        if(mbinding.messageArea.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void initFirebase(){
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("chatUser");
        ChatHelper helper = new ChatHelper(this);
        roomId = helper.createRoomId(SavePref.getInstance(this).getUserdetail(), friendData, isFromChat);
        postsRef = database.getReference("Posts").child(roomId);
    }

    private void sendMessage(String messageText) {
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        if (!messageText.equals("")) {
            ChatData chatData = new ChatData();
            chatData.setDate(ts);
            chatData.setBodyText(getMessageWithMediaType(messageText));
            chatData.setSenderName(SavePref.getInstance(this).getUserdetail().getUsername());
            chatData.setIsRead("0");
            chatData.setThumbImage(thumbImage);
            chatData.setMediaType(MEDIA_TYPE_COMMENT);
            chatData.setOneTimeMediaStatus("0");
            chatData.setProfilePic(ApiClass.ImageBaseUrl + SavePref.getInstance(this).getUserdetail().getProfilePicture());
            chatData.setSenderId(String.valueOf(SavePref.getInstance(this).getUserdetail().getId()));
            postsRef.push().setValue(chatData);
            sendLastMessageData(chatData);
            mbinding.messageArea.setText("");
            sendNotification(MEDIA_TYPE_COMMENT);
        }
    }

    private void sendLastMessageData(ChatData chatData) {
        LastMessage lastMessage = new LastMessage();
        lastMessage.setMessage(chatData.getBodyText());
        lastMessage.setDate(chatData.getDate());
        lastMessage.setIsRead(chatData.getIsRead());
        lastMessage.setUsername(chatData.getSenderName());
        usersRef.child(roomId)
                .child("lastMsg").setValue(lastMessage);
    }

    public void sendNotification(String mediaType){
        ApiInterface mApiInterface = RequestClient.getClient(ApiClass.BASE_URL).create(ApiInterface.class);
        JsonObject requestParam = new JsonObject();
        JsonArray idArray = new JsonArray();
        idArray.add(friendData.getId()+"");
        requestParam.add("ids", idArray);
        requestParam.addProperty("msgType", mediaType);
        requestParam.addProperty("full_name", SavePref.getInstance(this).getUserdetail().getFullName());
        requestParam.addProperty("user_id", SavePref.getInstance(this).getUserdetail().getId());

        Call<Basic_Response> call;
        call = mApiInterface.sendChatNotificaitons(requestParam);
        call.enqueue(new Callback<Basic_Response>() {
            @Override
            public void onResponse(Call<Basic_Response> call, Response<Basic_Response> response) {
                if (response.isSuccessful()) {
                    Basic_Response basic_response = response.body();
                    if(basic_response.getStatus().equalsIgnoreCase("true")){

                    }

                } else {
                    System.out.println(response.errorBody());
                }

            }
            @Override
            public void onFailure(Call<Basic_Response> call, Throwable t) {
            }
        });
    }

    private String getMessageWithMediaType(String messageText) {
        String messagePostFix = "";
        if(mediaType.equalsIgnoreCase("image")){
            messagePostFix = AppConstants.TYPE_COMMENT_PHOTO;
        }else{
            messagePostFix = AppConstants.TYPE_COMMENT_VIDEO;
        }
        return messageText + messagePostFix + mediaUrl;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("DailyCommentActivity", "onStop");
        if(exoPlayer != null)
            exoPlayer.setPlayWhenReady(false);
        hideSoftKeyboard();
    }
}
