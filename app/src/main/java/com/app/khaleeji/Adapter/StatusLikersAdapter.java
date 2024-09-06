package com.app.khaleeji.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.khaleeji.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import Constants.AppConstants;
import CustomView.CustomTextView;
import Model.StatusLikersModel;
import Utility.ApiClass;

public class StatusLikersAdapter extends RecyclerView.Adapter<StatusLikersAdapter.MyViewHolder> {

    private Context context;
    private List<StatusLikersModel> dataList;
    private OnItemClickListener mItemClickListener;

    public StatusLikersAdapter(Context context, OnItemClickListener itemClickListener) {
        this.context = context;
        mItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public StatusLikersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_likers, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final StatusLikersAdapter.MyViewHolder holder, final int position) {
        StatusLikersModel statusLikersModel = dataList.get(position);
        holder.imgUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(statusLikersModel, AppConstants.TYPE_CIRCLE_IMG);
            }
        });

        holder.txtFullName.setText(dataList.get(position).username);

        if(statusLikersModel.profile_picture != null && ! statusLikersModel.profile_picture.isEmpty()){
            try{
                Picasso.with(context).load(ApiClass.ImageBaseUrl + statusLikersModel.profile_picture).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgUserPic);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }else{
            Picasso.with(context).load(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgUserPic);
        }

//        holder.txtAddFriend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mItemClickListener.onItemClick(position, AppConstants.TYPE_ADD_FRIEND);
//            }
//        });
//
//        holder.txtUnFriend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mItemClickListener.onItemClick(position, AppConstants.TYPE_UNFRIEND);
//            }
//        });

//        if(StatusLikersModel.getStatus() == 1){
//            holder.txtAddFriend.setVisibility(View.GONE);
//            holder.txtUnFriend.setVisibility(View.VISIBLE);
//        }else{
//            holder.txtAddFriend.setVisibility(View.VISIBLE);
//            holder.txtUnFriend.setVisibility(View.GONE);
//        }
    }

    @Override
    public int getItemCount() {
        if(dataList != null)
            return dataList.size();
        return 0;
    }

    public void setData( List<StatusLikersModel> list){
        this.dataList = list;
        this.notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onItemClick(StatusLikersModel statusLikersModel, int type);
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
