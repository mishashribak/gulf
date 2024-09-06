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
import Model.StatusViewersModel;
import Utility.ApiClass;

public class StatusViewersAdapter extends RecyclerView.Adapter<StatusViewersAdapter.MyViewHolder> {

    private Context context;
    private List<StatusViewersModel> dataList;
    private OnItemClickListener mItemClickListener;

    public StatusViewersAdapter(Context context, OnItemClickListener itemClickListener) {
        this.context = context;
        mItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public StatusViewersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_likers, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final StatusViewersAdapter.MyViewHolder holder, final int position) {
        StatusViewersModel statusViewersModel = dataList.get(position);
        holder.imgUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(statusViewersModel, AppConstants.TYPE_CIRCLE_IMG);
            }
        });

        holder.txtFullName.setText(dataList.get(position).username);

        if(statusViewersModel.profile_picture != null && ! statusViewersModel.profile_picture.isEmpty()){
            try{
                Picasso.with(context).load(ApiClass.ImageBaseUrl + statusViewersModel.profile_picture).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgUserPic);
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

    public void setData( List<StatusViewersModel> list){
        this.dataList = list;
        this.notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onItemClick(StatusViewersModel statusViewersModel, int type);
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
