package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.khaleeji.Activity.DailyCommentActivity;
import com.app.khaleeji.Activity.FriendDailiesViewActivity;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.fetchDailiesOfFriends.FriendListDailiesOfFriends;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Constants.AppConstants;
import CustomView.CustomTextView;
import Model.ChatData;
import Model.QAData;
import Utility.ApiClass;
import Utility.SavePref;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatData> chatDataList;
    private Context context;
    private OnMediaItemClickListener onMediaItemClickListener;
    private View view;
    private String strDate="";
    private boolean mIsFromGroup;

    public ChatAdapter(List<ChatData> chatDataList, boolean isFromGroup, Context context, OnMediaItemClickListener onMediaItemClickListener) {
        this.chatDataList = chatDataList;
        this.context = context;
        this.onMediaItemClickListener = onMediaItemClickListener;
        mIsFromGroup = isFromGroup;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case AppConstants.MEDIA_TYPE_SENDER_TEXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sender_text,
                        parent, false);
                return new TextViewHolder(view);

            case AppConstants.MEDIA_TYPE_RECEIVER_TEXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_reciver_text,
                        parent, false);
                return new TextViewHolder(view);

            case AppConstants.MEDIA_TYPE_SENDER_COMMENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sender_comment,
                        parent, false);
                return new CommentViewHolder(view);

            case AppConstants.MEDIA_TYPE_RECEIVER_COMMENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_reciver_comment,
                        parent, false);
                return new CommentViewHolder(view);

            case AppConstants.MEDIA_TYPE_SENDER_ONE_TIME_IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sender_capture_image
                        , parent, false);
                return new OneTimePhotoViewHolder(view);

            case AppConstants.MEDIA_TYPE_RECEIVER_ONE_TIME_IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_reciver_capture_image,
                        parent, false);
                return new OneTimePhotoViewHolder(view);

            case AppConstants.MEDIA_TYPE_SENDER_PHOTO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sender_image, parent,
                        false);
                return new PhotoViewHolder(view);

            case AppConstants.MEDIA_TYPE_RECEIVER_PHOTO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_reciver_image, parent,
                        false);
                return new PhotoViewHolder(view);


            case AppConstants.MEDIA_TYPE_SENDER_AUDIO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_audio_sender, parent,
                        false);
                return new AudioViewHolder(view);

            case AppConstants.MEDIA_TYPE_RECEIVER_AUDIO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_audio_reciver, parent, false);
                return new AudioViewHolder(view);

            case AppConstants.MEDIA_TYPE_SENDER_ONE_TIME_VIDEO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sender_video,
                        parent, false);
                return new OneTimeVideoViewHolder(view);

            case AppConstants.MEDIA_TYPE_RECEIVER_ONE_TIME_VIDEO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_reciver_video,
                        parent, false);
                return new OneTimeVideoViewHolder(view);

            case AppConstants.MEDIA_TYPE_SENDER_VIDEO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sender_gallary_video,
                        parent, false);
                return new VideoViewHolder(view);

            case AppConstants.MEDIA_TYPE_RECEIVER_VIDEO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_reciver_gallary_video,
                        parent, false);
                return new VideoViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int index) {
        final int position = index;
        try{
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date date = fmt.parse(chatDataList.get(position).getDate());
            Date date = new Date(Long.parseLong(chatDataList.get(position).getDate()));
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM dd, hh:mm a");
            strDate =fmtOut.format(date);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        if (holder instanceof CommentViewHolder) {
            ((CommentViewHolder) holder).txtUsername.setVisibility(View.GONE);
            String body = chatDataList.get(position).getBodyText();
            ArrayList<String> dataList = new ArrayList<>();
            boolean isImage = true;
            if(body != null &&! body.isEmpty()){
                if(body.contains(AppConstants.TYPE_COMMENT_PHOTO)){
                    isImage = true;
                    int n;
                    while ((n =body.indexOf(AppConstants.TYPE_COMMENT_PHOTO)) != -1){
                        String cutStr = body.substring(0, n);
                        dataList.add( cutStr);
                        body = body.substring(n+AppConstants.TYPE_COMMENT_PHOTO.length());
                    }
                }else{
                    isImage = false;
                    int n;
                    while ((n =body.indexOf(AppConstants.TYPE_COMMENT_VIDEO)) != -1){
                        String cutStr = body.substring(0, n);
                        dataList.add( cutStr);
                        body = body.substring(n+AppConstants.TYPE_COMMENT_VIDEO.length());
                    }
                }
                dataList.add(body);
            }

            if (chatDataList.get(position).getSenderId()
                    .equals(String.valueOf(SavePref.getInstance(context).getUserdetail().getId()))) {
                ((CommentViewHolder) holder).txtRepliedDaily.setText(context.getString(R.string.you_replied));
            }else{
                ((CommentViewHolder) holder).txtRepliedDaily.setText(context.getString(R.string.user_replied,
                        chatDataList.get(position).getSenderName()));
            }

            if(dataList.size() == 2){
                ((CommentViewHolder) holder).txtMessage.setText(dataList.get(0));
                if(isImage){
                    Glide.with(context).load(dataList.get(1)).into(((CommentViewHolder) holder).imgMediaDaily);
                }else{
                    Glide.with(context).load(chatDataList.get(position).getThumbImage()).into(((CommentViewHolder) holder).imgMediaDaily);
                }
            }

            boolean finalIsImage = isImage;
            ((CommentViewHolder) holder).imgMediaDaily.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DailyCommentActivity.class);
                    intent.putExtra("mediaType", finalIsImage ? "image": "video");
                    intent.putExtra("isFromChat", true);
                    intent.putExtra("mediaUrl", dataList.get(1));
                    intent.putExtra("thumbImage", chatDataList.get(position).getThumbImage());
                    FriendListDailiesOfFriends friendData = new FriendListDailiesOfFriends();
                    friendData.setId(Integer.parseInt(chatDataList.get(position).getSenderId()));
                    friendData.setUsername( chatDataList.get(position).getSenderName());
                    friendData.setProfilePicture( chatDataList.get(position).getProfilePic());
                    intent.putExtra("friendData", friendData);
                    context.startActivity(intent);
                }
            });

            ((CommentViewHolder) holder).txtDate.setText(strDate);
            if (!chatDataList.get(position).getProfilePic().isEmpty()) {
                Picasso.with(context).load( chatDataList.get(position).getProfilePic())
                        .placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder)
                        .fit().centerInside()
                        .into(((CommentViewHolder) holder).imgProfilePic);
            }
            else
                Picasso.with(context).load(R.drawable.profile_placeholder).fit().centerInside().into(((CommentViewHolder) holder).imgProfilePic);

            if (chatDataList.get(position).getSenderId()
                    .equals(String.valueOf(SavePref.getInstance(context).getUserdetail().getId()))) {
                ((CommentViewHolder) holder).txtUsername.setText("@"+SavePref.getInstance(context).getUserdetail().getUsername());
                setMessageReadStatus(((CommentViewHolder) holder).txtDate, chatDataList.get(position).getIsRead());
            }else{
                ((CommentViewHolder) holder).txtUsername.setText("@"+chatDataList.get(position).getSenderName());
            }
        }else if (holder instanceof TextViewHolder) {
            if(mIsFromGroup){
                ((TextViewHolder) holder).txtUsername.setVisibility(View.VISIBLE);
            }else{
                ((TextViewHolder) holder).txtUsername.setVisibility(View.GONE);
            }

            ((TextViewHolder) holder).txtMessage.setText(chatDataList.get(position).getBodyText());
            ((TextViewHolder) holder).txtDate.setText(strDate);
            if (!chatDataList.get(position).getProfilePic().isEmpty()) {
                Picasso.with(context).load( chatDataList.get(position).getProfilePic())
                        .placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder)
                        .fit().centerInside()
                        .into(((TextViewHolder) holder).imgProfilePic);
            }
            else
                Picasso.with(context).load(R.drawable.profile_placeholder).fit().centerInside().into(((TextViewHolder) holder).imgProfilePic);

            if (chatDataList.get(position).getSenderId()
                    .equals(String.valueOf(SavePref.getInstance(context).getUserdetail().getId()))) {
                ((TextViewHolder) holder).txtUsername.setText("@"+SavePref.getInstance(context).getUserdetail().getUsername());
                setMessageReadStatus(((TextViewHolder) holder).txtDate, chatDataList.get(position).getIsRead());
            }else{
                ((TextViewHolder) holder).txtUsername.setText("@"+chatDataList.get(position).getSenderName());
            }
        } else if (holder instanceof OneTimePhotoViewHolder) {
            ((OneTimePhotoViewHolder) holder).llRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMediaItemClickListener.onMediaItemClick(AppConstants.MEDIA_TYPE_ONE_TIME_IMAGE,
                            chatDataList.get(position).getBodyText(), position);
                }
            });
            ((OneTimePhotoViewHolder) holder).txtDate.setText(strDate);
            if (chatDataList.get(position).getSenderId()
                    .equals(String.valueOf(SavePref.getInstance(context).getUserdetail().getId()))) {
                if( chatDataList.get(position).getOneTimeMediaStatus().equals("2")){
                    ((OneTimePhotoViewHolder) holder).imgMsgMark.setImageResource(R.drawable.opened_sent_photo);
                }
                setMessageReadStatus(((OneTimePhotoViewHolder) holder).txtDate, chatDataList.get(position).getIsRead());
            }else{
                if( chatDataList.get(position).getOneTimeMediaStatus().equals("2")){
                    ((OneTimePhotoViewHolder) holder).imgMsgMark.setImageResource(R.drawable.opened_received_photo);
                }
                ((OneTimePhotoViewHolder) holder).txtMessage.setText(context.getString(R.string.user_sent_photo,  chatDataList.get(position).getSenderName()));
            }

            if (! chatDataList.get(position).getProfilePic().isEmpty()) {
                Picasso.with(context).load( chatDataList.get(position).getProfilePic())
                        .placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder)
                        .fit().centerInside()
                        .into(((OneTimePhotoViewHolder) holder).imgProfilePic);
            }
            else
                Picasso.with(context).load(R.drawable.profile_placeholder).fit().centerInside().into(((OneTimePhotoViewHolder) holder).imgProfilePic);

        } else if (holder instanceof PhotoViewHolder) {
            ((PhotoViewHolder) holder).txtDate.setText(strDate);
            if (chatDataList.get(position).getSenderId()
                    .equals(String.valueOf(SavePref.getInstance(context).getUserdetail().getId()))) {
                setMessageReadStatus(((PhotoViewHolder) holder).txtDate, chatDataList.get(position).getIsRead());
            }
            Picasso.with(context).load(getOriginalUrl(chatDataList.get(position).getBodyText())).placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_placeholder).into(((PhotoViewHolder) holder).img);

            ((PhotoViewHolder) holder).img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMediaItemClickListener.onMediaItemClick(AppConstants.MEDIA_TYPE_PHOTO,
                            chatDataList.get(position).getBodyText(), position);
                }
            });

            ((PhotoViewHolder) holder).img.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onMediaItemClickListener.onItemClick(AppConstants.ON_LONG_CLICK
                            , chatDataList.get(position).getDate(), position);
                    return false;
                }
            });

            if (! chatDataList.get(position).getProfilePic().isEmpty()) {
                Picasso.with(context).load( chatDataList.get(position).getProfilePic())
                        .placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder)
                        .fit().centerInside()
                        .into(((PhotoViewHolder) holder).imgProfilePic);
            }else
            Picasso.with(context).load(R.drawable.profile_placeholder).fit().centerInside().into(((PhotoViewHolder) holder).imgProfilePic);

        } else if (holder instanceof AudioViewHolder) {
            ((AudioViewHolder) holder).imgPlay.setVisibility(View.VISIBLE);
            ((AudioViewHolder) holder).imgPause.setVisibility(View.GONE);
            ((AudioViewHolder) holder).imgPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((AudioViewHolder) holder).imgPlay.setVisibility(View.GONE);
                    ((AudioViewHolder) holder).imgPause.setVisibility(View.VISIBLE);
                    onMediaItemClickListener.onMediaItemClick(AppConstants.MEDIA_TYPE_AUDIO,
                            chatDataList.get(position).getBodyText(), position);
                }
            });
            ((AudioViewHolder) holder).txtDate.setText(strDate);
            if (chatDataList.get(position).getSenderId()
                    .equals(String.valueOf(SavePref.getInstance(context).getUserdetail().getId()))) {
                setMessageReadStatus(((AudioViewHolder) holder).txtDate, chatDataList.get(position).getIsRead());
            }

            if (!chatDataList.get(position).getProfilePic().isEmpty()) {
               Picasso.with(context).load( chatDataList.get(position).getProfilePic())
                       .placeholder(R.drawable.profile_placeholder)
                       .error(R.drawable.profile_placeholder)
                       .fit().centerInside()
                       .into(((AudioViewHolder) holder).imgProfilePic);
            }
            else
                Picasso.with(context).load(R.drawable.profile_placeholder).fit().centerInside().into(((AudioViewHolder) holder).imgProfilePic);

        } else if (holder instanceof VideoViewHolder) {

            ((VideoViewHolder) holder).llRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMediaItemClickListener.onMediaItemClick(AppConstants.MEDIA_TYPE_VIDEO,
                            chatDataList.get(position).getBodyText(), position);
                }
            });
            ((VideoViewHolder) holder).txtDate.setText(strDate);
            if (chatDataList.get(position).getSenderId()
                    .equals(String.valueOf(SavePref.getInstance(context).getUserdetail().getId()))) {
                setMessageReadStatus(((VideoViewHolder) holder).txtDate, chatDataList.get(position).getIsRead());
            }

            if (!chatDataList.get(position).getProfilePic().isEmpty()) {
                Picasso.with(context).load( chatDataList.get(position).getProfilePic())
                        .placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder)
                        .fit().centerInside()
                        .into(((VideoViewHolder) holder).imgProfilePic);
            }
            else
                Picasso.with(context).load(R.drawable.profile_placeholder).fit().centerInside().into(((VideoViewHolder) holder).imgProfilePic);

            Picasso.with(context).load(chatDataList.get(position).getThumbImage())
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_placeholder)
                    .fit().centerInside()
                    .into(((VideoViewHolder) holder).img);

        } else if (holder instanceof OneTimeVideoViewHolder) {
            ((OneTimeVideoViewHolder) holder).llRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMediaItemClickListener.onMediaItemClick(AppConstants.MEDIA_TYPE_ONE_TIME_VIDEO,
                            chatDataList.get(position).getBodyText(), position);
                }
            });
            ((OneTimeVideoViewHolder) holder).txtDate.setText(strDate);

            if (chatDataList.get(position).getSenderId()
                    .equals(String.valueOf(SavePref.getInstance(context).getUserdetail().getId()))) {
                if( chatDataList.get(position).getOneTimeMediaStatus().equals("2")){
                    ((OneTimeVideoViewHolder) holder).imgMsgMark.setImageResource(R.drawable.opened_sent_video);
                }
                setMessageReadStatus(((OneTimeVideoViewHolder) holder).txtDate, chatDataList.get(position).getIsRead());
            }else{
                if( chatDataList.get(position).getOneTimeMediaStatus().equals("2")){
                    ((OneTimeVideoViewHolder) holder).imgMsgMark.setImageResource(R.drawable.opened_received_video);
                }
                ((OneTimeVideoViewHolder) holder).txtMessage.setText(context.getString(R.string.user_sent_video,  chatDataList.get(position).getSenderName()));
            }

            if (!chatDataList.get(position).getProfilePic().isEmpty()) {
               Picasso.with(context).load( chatDataList.get(position).getProfilePic())
                       .placeholder(R.drawable.profile_placeholder)
                       .error(R.drawable.profile_placeholder)
                       .fit().centerInside()
                       .into(((OneTimeVideoViewHolder) holder).imgProfilePic);
            }else
                Picasso.with(context).load(R.drawable.profile_placeholder).fit().centerInside().into(((OneTimeVideoViewHolder) holder).imgProfilePic);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onMediaItemClickListener.onItemClick(AppConstants.ON_LONG_CLICK
                        , chatDataList.get(position).getDate(), position);
                return false;
            }
        });

    }

    private void setMessageReadStatus(TextView txtDate, String isRead) {
        if (isRead.equals("1")) {
            txtDate.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    context.getResources().getDrawable(R.drawable.ic_done_all_blue_24dp), null);
        } else {
            txtDate.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    context.getResources().getDrawable(R.drawable.ic_done_all_black_24dp), null);

        }

    }

    @Override
    public int getItemCount() {
        return chatDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (chatDataList.get(position).getMediaType()) {
            case AppConstants.MEDIA_TYPE_TEXT:
                if (chatDataList.get(position).getSenderName()
                        .equals(SavePref.getInstance(context).getUserdetail().getUsername())) {
                    return AppConstants.MEDIA_TYPE_SENDER_TEXT;
                } else {
                    return AppConstants.MEDIA_TYPE_RECEIVER_TEXT;
                }
            case AppConstants.MEDIA_TYPE_COMMENT:
                if (chatDataList.get(position).getSenderName()
                        .equals(SavePref.getInstance(context).getUserdetail().getUsername())) {
                    return AppConstants.MEDIA_TYPE_SENDER_COMMENT;
                } else {
                    return AppConstants.MEDIA_TYPE_RECEIVER_COMMENT;
                }
            case AppConstants.MEDIA_TYPE_ONE_TIME_IMAGE:
                if (chatDataList.get(position).getSenderName()
                        .equals(SavePref.getInstance(context).getUserdetail().getUsername())) {
                    return AppConstants.MEDIA_TYPE_SENDER_ONE_TIME_IMAGE;
                } else {
                    return AppConstants.MEDIA_TYPE_RECEIVER_ONE_TIME_IMAGE;
                }
            case AppConstants.MEDIA_TYPE_PHOTO:
                if (chatDataList.get(position).getSenderName()
                        .equals(SavePref.getInstance(context).getUserdetail().getUsername())) {
                    return AppConstants.MEDIA_TYPE_SENDER_PHOTO;
                } else {
                    return AppConstants.MEDIA_TYPE_RECEIVER_PHOTO;
                }
            case AppConstants.MEDIA_TYPE_AUDIO:
                if (chatDataList.get(position).getSenderName()
                        .equals(SavePref.getInstance(context).getUserdetail().getUsername())) {
                    return AppConstants.MEDIA_TYPE_SENDER_AUDIO;
                } else {
                    return AppConstants.MEDIA_TYPE_RECEIVER_AUDIO;
                }
            case AppConstants.MEDIA_TYPE_ONE_TIME_VIDEO:
                if (chatDataList.get(position).getSenderName()
                        .equals(SavePref.getInstance(context).getUserdetail().getUsername())) {
                    return AppConstants.MEDIA_TYPE_SENDER_ONE_TIME_VIDEO;
                } else {
                    return AppConstants.MEDIA_TYPE_RECEIVER_ONE_TIME_VIDEO;
                }
            case AppConstants.MEDIA_TYPE_VIDEO:
                if (chatDataList.get(position).getSenderName()
                        .equals(SavePref.getInstance(context).getUserdetail().getUsername())) {
                    return AppConstants.MEDIA_TYPE_SENDER_VIDEO;
                } else {
                    return AppConstants.MEDIA_TYPE_RECEIVER_VIDEO;
                }
        }
        return 0;
    }

    public void clear() {
        int size = chatDataList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                chatDataList.remove(0);
            }

            notifyItemRangeRemoved(0, size);
        }
    }

    private String getOriginalUrl(String message) {

        if (message.contains(AppConstants.TYPE_AUDIO)) {
            return message.replace(AppConstants.TYPE_AUDIO, "");

        } else if (message.contains(AppConstants.TYPE_VIDEO)) {
            return message.replace(AppConstants.TYPE_VIDEO, "");

        } else if (message.contains(AppConstants.TYPE_PHOTO)) {
            return message.replace(AppConstants.TYPE_PHOTO, "");

        }
        return message;
    }

    public interface OnMediaItemClickListener {
        void onMediaItemClick(String mediaTpe, String message, int index);

        void onItemClick(String type, String date, int index);
    }

    class TextViewHolder extends RecyclerView.ViewHolder {

        private TextView txtMessage, txtDate, txtUsername;
        private ImageView imgProfilePic;

        public TextViewHolder(View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txt_message);
            txtDate = itemView.findViewById(R.id.txt_date);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            imgProfilePic = itemView.findViewById(R.id.img_profile_pic);

        }
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {

        private CustomTextView txtMessage, txtDate, txtUsername;
        private ImageView imgProfilePic;
        private ImageView imgMediaDaily;
        private CustomTextView txtRepliedDaily;

        public CommentViewHolder(View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txt_message);
            txtDate = itemView.findViewById(R.id.txt_date);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            imgProfilePic = itemView.findViewById(R.id.img_profile_pic);
            txtRepliedDaily = itemView.findViewById(R.id.txtRepliedDaily);
            imgMediaDaily = itemView.findViewById(R.id.imgMediaDaily);
        }
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {

        private TextView txtDate;
        private ImageView img;
        private ImageView imgProfilePic;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txt_date);
            img = itemView.findViewById(R.id.img);
            imgProfilePic = itemView.findViewById(R.id.img_profile_pic);
                    }
    }

    class OneTimePhotoViewHolder extends RecyclerView.ViewHolder {

        private TextView txtDate;
        private ImageView imgProfilePic;
        private LinearLayout llRow;
        private TextView txtMessage;
        private ImageView imgMsgMark;

        public OneTimePhotoViewHolder(View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txt_date);
            imgProfilePic = itemView.findViewById(R.id.img_profile_pic);
            llRow = itemView.findViewById(R.id.llRow);
            txtMessage = itemView.findViewById(R.id.txt_message);
            imgMsgMark = itemView.findViewById(R.id.imgMsgMark);
        }
    }


    class AudioViewHolder extends RecyclerView.ViewHolder {

        private TextView txtDate;
        private ImageView imgProfilePic;
        private ImageView imgPlay;
        private ImageView imgPause;

        public AudioViewHolder(View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txt_date);
            imgProfilePic = itemView.findViewById(R.id.img_profile_pic);
            imgPause = itemView.findViewById(R.id.img_pause);
            imgPlay = itemView.findViewById(R.id.img_play);
        }
    }

    class OneTimeVideoViewHolder extends RecyclerView.ViewHolder {
        private TextView txtDate;
        private ImageView imgProfilePic;
        private LinearLayout llRow;
        private TextView txtMessage;
        private ImageView imgMsgMark;

        public OneTimeVideoViewHolder(View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txt_date);
            imgProfilePic = itemView.findViewById(R.id.img_profile_pic);
            llRow = itemView.findViewById(R.id.llRow);
            txtMessage = itemView.findViewById(R.id.txt_message);
            imgMsgMark = itemView.findViewById(R.id.imgMsgMark);
        }
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        private TextView txtDate;
        private ImageView imgProfilePic;
        private ImageView img;
        private LinearLayout llRow;

        public VideoViewHolder(View itemView) {
            super(itemView);
            txtDate = itemView.findViewById(R.id.txt_date);
            imgProfilePic = itemView.findViewById(R.id.img_profile_pic);
            img = itemView.findViewById(R.id.img);
            llRow = itemView.findViewById(R.id.llRow);
        }
    }


}
