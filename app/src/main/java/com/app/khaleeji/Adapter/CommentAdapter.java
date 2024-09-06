package com.app.khaleeji.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.khaleeji.Activity.ChatActivity;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.GetSlctdMediaComments;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import CustomView.CustomTextView;
import Utility.ApiClass;
import Utility.SavePref;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<GetSlctdMediaComments> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Activity mActivity;
    private int userId;
    private boolean lock;
    private LinearLayout rlRow;
    private ImageView imgClose;

    // data is passed into the constructor
    public CommentAdapter(Activity activity, Context context, ItemClickListener itemClickListener) {
        this.mInflater = LayoutInflater.from(context);
        mActivity = activity;
        userId = SavePref.getInstance(mActivity).getUserdetail().getId();
        mClickListener = itemClickListener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_comment, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        rlRow = holder.rowItem;
        imgClose = holder.imgClose;
        holder.imgClose.setVisibility(View.GONE);
        holder.rowItem.setBackgroundColor(Color.parseColor("#ffffff"));
        
        holder.tvViewComment.setText(mData.get(position).getComment());
        holder.txtFullName.setText(mData.get(position).getFull_name());

        try{
            if(mData.get(position).getProfilePicture() != null && !mData.get(position).getProfilePicture().isEmpty())
                Picasso.with(mActivity).load(ApiClass.ImageBaseUrl+mData.get(position).getProfilePicture()).placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgProfile);
            else
                Picasso.with(mActivity).load(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgProfile);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        holder.rowItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mData.get(position).getCommentorId() == userId){
                    lock = true;
                    holder.imgClose.setVisibility(View.VISIBLE);
                    holder.rowItem.setBackgroundColor(Color.parseColor("#e6e6e9"));
                }else{
                    lock = false;
                }
               
                return true;
            }
        });

        holder.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mData.get(position).getCommentorId() != userId){
                    mClickListener.onProfile(mData.get(position).getCommentorId());
                }
            }
        });

        holder.rowItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imgClose.setVisibility(View.GONE);
                holder.rowItem.setBackgroundColor(Color.parseColor("#ffffff"));
                lock = false;
            }
        });
        
        holder.imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lock = false;
                showConfirmDlg(position);

            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        if(mData != null)
            return mData.size();
        return 0;
    }

    public void setData(List<GetSlctdMediaComments> list){
        mData = list;
        notifyDataSetChanged();
    }


    public void showConfirmDlg(int position){
        Utility.AlertDialog.showAlert(mActivity, mActivity.getString(R.string.app_name),
                mActivity.getString(R.string.deleteCommentMessage),
                mActivity.getString(R.string.txt_yes), mActivity.getString(R.string.cancel), true,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogBox, int which) {
                        mClickListener.onItemClick(position);
                        dialogBox.cancel();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogBox, int which) {
                        dialogBox.cancel();
                    }
                });
    }
    

    public List<GetSlctdMediaComments> getData(){
        return mData;
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        private de.hdodenhof.circleimageview.CircleImageView imgProfile;
        private CustomTextView txtFullName;
        private CustomTextView tvViewComment;
        private LinearLayout rowItem;
        private ImageView imgClose;

        ViewHolder(View itemView) {
            super(itemView);
            tvViewComment = itemView.findViewById(R.id.tvViewComment);
            txtFullName = itemView.findViewById(R.id.txtFullName);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            rowItem = itemView.findViewById(R.id.rowItem);
            imgClose = itemView.findViewById(R.id.imgClose);
        }

    }

    public interface ItemClickListener {
        void onItemClick(int position);
        void onProfile(int userId);
    }
}