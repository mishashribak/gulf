package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.khaleeji.R;
import com.app.khaleeji.Response.FetchMediaLikeUserDetail;
import com.app.khaleeji.Response.fetchDailiesOfFriends.FriendListDailiesOfFriends;
import com.app.khaleeji.Response.fetchHotspotAndFrndsDetail.HotspotDetailData;
import com.squareup.picasso.Picasso;

import java.util.List;

import Constants.AppConstants;
import CustomView.CustomTextView;
import Model.ChatUserData;
import Utility.ApiClass;

public class PostLikesListAdapter extends RecyclerView.Adapter<PostLikesListAdapter.MyViewHolder> {

    private Context context;
    private List<FetchMediaLikeUserDetail> dataList;
    private OnItemClickListener mItemClickListener;

    public PostLikesListAdapter(Context context, OnItemClickListener itemClickListener) {
        this.context = context;
        mItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public PostLikesListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_post_likes, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostLikesListAdapter.MyViewHolder holder, final int position) {
        holder.imgUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(position, AppConstants.TYPE_CIRCLE_IMG);
            }
        });

        holder.txtAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(position, AppConstants.TYPE_ADD_FRIEND);
            }
        });

        holder.txtUnFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(position, AppConstants.TYPE_UNFRIEND);
            }
        });

        FetchMediaLikeUserDetail fetchMediaLikeUserDetail = dataList.get(position);
//        if(fetchMediaLikeUserDetail.getStatus() == 1){
//            holder.txtAddFriend.setVisibility(View.GONE);
//            holder.txtUnFriend.setVisibility(View.VISIBLE);
//        }else{
//            holder.txtAddFriend.setVisibility(View.VISIBLE);
//            holder.txtUnFriend.setVisibility(View.GONE);
//        }

        holder.txtFullName.setText(fetchMediaLikeUserDetail.getFullName());

        if(fetchMediaLikeUserDetail.getProfilePicture() != null && ! fetchMediaLikeUserDetail.getProfilePicture().isEmpty()){
            try{
                Picasso.with(context).load(ApiClass.ImageBaseUrl + fetchMediaLikeUserDetail.getProfilePicture()).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgUserPic);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }else{
            Picasso.with(context).load(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgUserPic);
        }
    }

    @Override
    public int getItemCount() {
        if(dataList != null)
            return dataList.size();
        return 0;
    }

    public void setData( List<FetchMediaLikeUserDetail> list){
        this.dataList = list;
        this.notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onItemClick(int position, int type);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private com.mikhaellopez.circularimageview.CircularImageView imgUserPic;
        private CustomTextView txtAddFriend, txtUnFriend, txtFullName;
        public MyViewHolder(View itemView) {
            super(itemView);
            imgUserPic = itemView.findViewById(R.id.imgProfile);
            txtAddFriend = itemView.findViewById(R.id.txtAddFriend);
            txtFullName = itemView.findViewById(R.id.txtFullName);
            txtUnFriend = itemView.findViewById(R.id.txtUnFriend);
        }
    }
}
