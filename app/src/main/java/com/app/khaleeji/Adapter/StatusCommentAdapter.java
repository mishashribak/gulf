package com.app.khaleeji.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import androidx.recyclerview.widget.RecyclerView;
import com.app.khaleeji.R;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

import Constants.AppConstants;
import Constants.Bundle_Identifier;
import CustomView.CustomTextView;
import Model.StatusCommentModel;
import Utility.ApiClass;
import Utility.SavePref;


public class StatusCommentAdapter extends RecyclerView.Adapter<StatusCommentAdapter.ViewHolder> {

    private List<StatusCommentModel> mData;
    private ItemClickListener mClickListener;
    private Context context;
    private  StatusReplyCommentAdapter statusReplyCommentAdapter;

    // data is passed into the constructor
    public StatusCommentAdapter(Context context, StatusReplyCommentAdapter statusReplyCommentAdapter, ItemClickListener itemClickListener) {
        this.context = context;
        mClickListener = itemClickListener;
        this.statusReplyCommentAdapter = statusReplyCommentAdapter;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_status_comment, parent, false);
        return new ViewHolder(itemView);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.rvReply.setAdapter(statusReplyCommentAdapter);

        StatusCommentModel statusCommentModel = mData.get(position);
        if (Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(Bundle_Identifier.ArabicLang)) {
            holder.tvReplyTime.setText(statusCommentModel.agoArabic);
        }else{
            holder.tvReplyTime.setText(statusCommentModel.ago);
        }

        if(statusCommentModel.profile_picture == null){
            Glide.with(context).load(R.drawable.profile_placeholder).into(holder.imgProfile);
        }else{
            Glide.with(context).load(ApiClass.ImageBaseUrl +statusCommentModel.profile_picture).into(holder.imgProfile);
        }

        holder.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onProfile(statusCommentModel);
            }
        });
        holder.txtFullName.setText(statusCommentModel.username);
        holder.tvViewComment.setText(statusCommentModel.text);
        holder.txtLikes.setText(statusCommentModel.likes_count + "");
        if(statusCommentModel.replies_count == 0){
            holder.txtViewReplyCount.setText(context.getResources().getString(R.string.view_reply, "0"));
        }else{
            holder.txtViewReplyCount.setText(context.getResources().getString(R.string.view_reply, statusCommentModel.replies_count + ""));
        }

        if(statusCommentModel.showReply){
            holder.rvReply.setVisibility(View.VISIBLE);
        }else{
            holder.rvReply.setVisibility(View.GONE);
        }

        holder.llReplyCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(statusCommentModel.replies_count > 0){
                    statusCommentModel.showReply = ! statusCommentModel.showReply;
                    mClickListener.onReply(statusCommentModel);
                }
            }
        });

        if(statusCommentModel.user_id.equals(String.valueOf(SavePref.getInstance(context).getUserdetail().getId()))){
            holder.moreHoriz.setVisibility(View.VISIBLE);
        }else{
            holder.moreHoriz.setVisibility(View.GONE);
        }

        holder.moreHoriz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, holder.moreHoriz);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.menu_msg, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        mClickListener.onRemoveStatusComment(statusCommentModel);
                        return true;
                    }
                });
                popup.show();

            }
        });

        holder.txtReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onSendReply(statusCommentModel);
            }
        });

        if(statusCommentModel.likeBefore){
            holder.imgRedHeart.setVisibility(View.VISIBLE);
            holder.imgHeart.setVisibility(View.GONE);
        }else{
            holder.imgRedHeart.setVisibility(View.GONE);
            holder.imgHeart.setVisibility(View.VISIBLE);
        }

        holder.imgHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onLike(statusCommentModel);
            }
        });

        holder.imgRedHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onLike(statusCommentModel);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(mData != null)
            return mData.size();
        return 0;
    }

    public void setData(List<StatusCommentModel> list){
        mData = list;
        notifyDataSetChanged();
    }

    public List<StatusCommentModel> getData(){
        return mData;
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        private de.hdodenhof.circleimageview.CircleImageView imgProfile;
        private CustomTextView txtFullName;
        private CustomTextView tvViewComment, txtViewReplyCount;
        private ImageView moreHoriz, imgHeart, imgRedHeart;
        private RecyclerView rvReply;
        private CustomTextView tvReplyTime;
        private CustomTextView txtLikes, txtReply;
        private LinearLayout llReplyCount;

        ViewHolder(View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            tvViewComment = itemView.findViewById(R.id.tvViewComment);
            txtFullName = itemView.findViewById(R.id.txtFullName);
            tvReplyTime = itemView.findViewById(R.id.tvReplyTime);
            txtLikes = itemView.findViewById(R.id.txtLikes);
            moreHoriz = itemView.findViewById(R.id.moreHoriz);
            rvReply = itemView.findViewById(R.id.rvReply);
            txtViewReplyCount = itemView.findViewById(R.id.txtViewReplyCount);
            llReplyCount = itemView.findViewById(R.id.llReplyCount);
            txtReply = itemView.findViewById(R.id.txtReply);
            imgHeart = itemView.findViewById(R.id.imgHeart);
            imgRedHeart = itemView.findViewById(R.id.imgRedHeart);
        }

    }

    public interface ItemClickListener {
        void onRemoveStatusComment(StatusCommentModel statusCommentModel);
        void onProfile(StatusCommentModel statusCommentModel);
        void onReply(StatusCommentModel statusCommentModel);
        void onLike(StatusCommentModel statusCommentModel);
        void onSendReply(StatusCommentModel statusCommentModel);
    }
}