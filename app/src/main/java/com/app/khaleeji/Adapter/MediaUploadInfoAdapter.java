package com.app.khaleeji.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.khaleeji.Activity.ChatActivity;
import com.app.khaleeji.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import CustomView.CustomTextView;
import Model.UploadInfoModel;
import Utility.ApiClass;
import Utility.SavePref;
import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;

public class MediaUploadInfoAdapter extends RecyclerView.Adapter<MediaUploadInfoAdapter.MyViewHolder> {

    private List<UploadInfoModel> mUploadInfoList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public MediaUploadInfoAdapter(Context context, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_media_upload_info, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
//        if(position == mUploadInfoList.size()-1){
//            holder.line.setVisibility(View.GONE);
//        }

        holder.llRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mUploadInfoList.size() > 0){
                    if(mUploadInfoList.get(position).isUploadFail){
                        Toast.makeText(context, context.getResources().getString(R.string.uploading), Toast.LENGTH_SHORT).show();
                        onItemClickListener.uploadAgain(mUploadInfoList.get(position));
                    }
                }
            }
        });

        holder.uploadProgress.setTextSize(30);
        holder.uploadProgress.setTextMode(TextMode.TEXT);
        holder.uploadProgress.setMaxValue(100);
        if(mUploadInfoList.get(position).isUploadFail){
            holder.txtName.setText(context.getResources().getString(R.string.upload_failed));
            holder.txtName.setTextColor(context.getColor(R.color.upload_fail));
            holder.uploadProgress.setBarColor(context.getColor(R.color.upload_fail));
        }else{
            String dif = "";
            long difference = System.currentTimeMillis() - Long.parseLong(mUploadInfoList.get(position).uploadId);
            int difMin = (int) (difference / 1000 /60);
            if(difMin > 0){
                dif = difMin + "";
                if(mUploadInfoList.get(position).uploadType == UploadInfoModel.UploadType.DAILY){
                    holder.txtName.setText(context.getResources().getString(R.string.daily_uploading_ago, dif));
                }else if(mUploadInfoList.get(position).uploadType == UploadInfoModel.UploadType.MEMORY){
                    holder.txtName.setText(context.getResources().getString(R.string.memory_uploading_ago, dif));
                }else{
                    holder.txtName.setText(context.getResources().getString(R.string.memory_daily_uploading_ago, dif));
                }
            }else{
                dif = (difference / 1000) + "";
                if(dif.equals("0")){
                    dif = context.getResources().getString(R.string.now);
                }
                if(mUploadInfoList.get(position).uploadType == UploadInfoModel.UploadType.DAILY){
                    holder.txtName.setText(context.getResources().getString(R.string.daily_uploading_ago_sec, dif));
                }else if(mUploadInfoList.get(position).uploadType == UploadInfoModel.UploadType.MEMORY){
                    holder.txtName.setText(context.getResources().getString(R.string.memory_uploading_ago_sec, dif));
                }else{
                    holder.txtName.setText(context.getResources().getString(R.string.memory_daily_uploading_ago_sec, dif));
                }
            }

            holder.txtName.setTextColor(context.getColor(R.color.black));
            holder.uploadProgress.setBarColor(context.getColor(R.color.colorPrimary));
        }

        holder.uploadProgress.setValue(mUploadInfoList.get(position).currentProgress);
        holder.uploadProgress.setText(mUploadInfoList.get(position).currentProgress+"%");


        if(SavePref.getInstance(context).getUserdetail().getProfilePicture() != null && ! SavePref.getInstance(context).getUserdetail().getProfilePicture().isEmpty()){
            Picasso.with(context).load(ApiClass.ImageBaseUrl +
                    SavePref.getInstance(context).getUserdetail().getProfilePicture()).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgAvatar);
        }else{
            Picasso.with(context).load(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgAvatar);
        }

        holder.llClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(
                        context).create();
                alertDialog.setTitle(context.getString(R.string.choose_cancel_upload));
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.txt_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                onItemClickListener.onItemClick(mUploadInfoList.get(position));
                                dialog.dismiss();
                            }
                        });

                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getResources().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        if( mUploadInfoList != null)
            return mUploadInfoList.size();
        return 0;
    }

    public void setData( List<UploadInfoModel> list){
        this.mUploadInfoList = list;
        this.notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(UploadInfoModel uploadInfoModel);
        void uploadAgain(UploadInfoModel uploadInfoModel);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CustomTextView txtName;
        private LinearLayout llClose;
        private CircleProgressView uploadProgress;
        private View line;
        private de.hdodenhof.circleimageview.CircleImageView imgAvatar;
        private LinearLayout llRow;
        public MyViewHolder(View view) {
            super(view);
            txtName = view.findViewById(R.id.txtName);
            llClose = view.findViewById(R.id.llClose);
            line = view.findViewById(R.id.viewLine);
            uploadProgress = view.findViewById(R.id.uploadProgress);
            imgAvatar = view.findViewById(R.id.imgAvatar);
            llRow = view.findViewById(R.id.llRow);
        }
    }
}