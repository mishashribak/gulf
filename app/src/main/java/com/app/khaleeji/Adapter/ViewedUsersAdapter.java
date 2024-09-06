package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.khaleeji.R;
import com.app.khaleeji.Response.MediaViewsData;
import com.app.khaleeji.Response.MemoryModel;
import com.app.khaleeji.Response.TimeLineResponseData;
import com.squareup.picasso.Picasso;

import java.util.List;

import Constants.AppConstants;
import CustomView.CustomTextView;
import Utility.ApiClass;

public class ViewedUsersAdapter extends RecyclerView.Adapter<ViewedUsersAdapter.MyViewHolder> {

    private Context context;
    private OnItemClickListener onItemClickListener;
    private List<MediaViewsData> dataList;

    public ViewedUsersAdapter(Context context , OnItemClickListener itemClickListener) {
        this.context = context;
        onItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_viewed_users, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.llRowItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
            }
        });
        holder.txtFullName.setText(dataList.get(position).getFull_name());
        if(dataList.get(position).getProfile_picture() != null && ! dataList.get(position).getProfile_picture().isEmpty()){
            try{
                Picasso.with(context).load(ApiClass.ImageBaseUrl+dataList.get(position).getProfile_picture()).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgProfile);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }else
            Picasso.with(context).load(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgProfile);
    }

    @Override
    public int getItemCount() {
        if(dataList != null)
            return dataList.size();
        return 0;
    }

    public void setData( List<MediaViewsData> list){
        this.dataList = list;
        this.notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llRowItem;
        private de.hdodenhof.circleimageview.CircleImageView imgProfile;
        private CustomTextView txtFullName;

        public MyViewHolder(View view) {
            super(view);
            llRowItem = view.findViewById(R.id.llRowItem);
            imgProfile = view.findViewById(R.id.imgProfile);
            txtFullName = view.findViewById(R.id.txtFullName);
        }
    }
}
