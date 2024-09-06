package com.app.khaleeji.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.recyclerview.widget.RecyclerView;
import com.app.khaleeji.R;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

import Constants.Bundle_Identifier;
import CustomView.CustomTextView;
import Model.StatusReplyModel;
import Utility.ApiClass;
import Utility.SavePref;

public class StatusReplyCommentAdapter extends RecyclerView.Adapter<StatusReplyCommentAdapter.ViewHolder> {

    private List<StatusReplyModel> mData;
    private ItemClickListener mClickListener;
    private Context context;

    public StatusReplyCommentAdapter(Context context, ItemClickListener itemClickListener) {
        mClickListener = itemClickListener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_status_reply_comment, parent, false);
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StatusReplyModel statusReplyModel = mData.get(position);
        holder.txtFullName.setText(statusReplyModel.username);

        if (Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(Bundle_Identifier.ArabicLang)) {
            holder.tvReplyTime.setText(statusReplyModel.agoArabic);
        }else{
            holder.tvReplyTime.setText(statusReplyModel.ago);
        }

        if(statusReplyModel.profile_picture == null){
            Glide.with(context).load(R.drawable.profile_placeholder).into(holder.imgProfile);
        }else{
            Glide.with(context).load(ApiClass.ImageBaseUrl +statusReplyModel.profile_picture).into(holder.imgProfile);
        }

        holder.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onProfile(statusReplyModel);
            }
        });
        holder.tvComment.setText(statusReplyModel.text);
        holder.txtLikes.setText(String.valueOf(statusReplyModel.likes_count));

        if(statusReplyModel.user_id.equals(String.valueOf(SavePref.getInstance(context).getUserdetail().getId()))){
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
                        mClickListener.onRemoveStatusReply(statusReplyModel);
                        return true;
                    }
                });
                popup.show();

            }
        });

        if(statusReplyModel.likeBefore){
            holder.imgRedHeart.setVisibility(View.VISIBLE);
            holder.imgHeart.setVisibility(View.GONE);
        }else{
            holder.imgRedHeart.setVisibility(View.GONE);
            holder.imgHeart.setVisibility(View.VISIBLE);
        }

        holder.imgHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onStatusReplyLike(statusReplyModel);
            }
        });

        holder.imgRedHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onStatusReplyLike(statusReplyModel);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(mData != null)
            return mData.size();
        return 0;
    }

    public void setData(List<StatusReplyModel> list){
        mData = list;
        notifyDataSetChanged();
    }


    public List<StatusReplyModel> getData(){
        return mData;
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        private de.hdodenhof.circleimageview.CircleImageView imgProfile;
        private CustomTextView txtFullName;
        private CustomTextView tvComment;
        private ImageView moreHoriz;
        private ImageView imgHeart, imgRedHeart;
        private CustomTextView tvReplyTime;
        private CustomTextView txtLikes;

        ViewHolder(View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            tvComment = itemView.findViewById(R.id.tvComment);
            txtFullName = itemView.findViewById(R.id.txtFullName);
            tvReplyTime = itemView.findViewById(R.id.tvReplyTime);
            txtLikes = itemView.findViewById(R.id.txtLikes);
            moreHoriz = itemView.findViewById(R.id.moreHoriz);
            imgHeart = itemView.findViewById(R.id.imgHeart);
            imgRedHeart = itemView.findViewById(R.id.imgRedHeart);
        }

    }

    public interface ItemClickListener {
        void onStatusReplyLike(StatusReplyModel statusReplyModel);
        void onProfile(StatusReplyModel statusReplyModel);
        void onRemoveStatusReply(StatusReplyModel statusReplyModel);
    }
}