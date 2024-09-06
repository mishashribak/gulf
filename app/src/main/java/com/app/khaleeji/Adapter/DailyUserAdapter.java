package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.khaleeji.Fragments.HotSpotDetailsFragment;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.HotspotData;
import com.app.khaleeji.Response.UserDetails;
import com.app.khaleeji.Response.fetchDailiesOfFriends.FriendListDailiesOfFriends;
import com.squareup.picasso.Picasso;

import java.util.List;

import CustomView.CustomTextView;
import Utility.ApiClass;
import Utility.Fragment_Process;

public class DailyUserAdapter extends RecyclerView.Adapter<DailyUserAdapter.MyViewHolder> {

    private List<FriendListDailiesOfFriends> dataList;
    private Context context;
    private OnGroupUserClickListener onItemClickListener;

    public DailyUserAdapter(Context context, OnGroupUserClickListener onItemClickListener) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_dailyuser, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.llRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onGroupUserClick(position);
            }
        });

        holder.txtHotspot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onHotspotClick(position);
            }
        });

        holder.txtName.setText(dataList.get(position).getFullName());
        if(dataList.get(position).getProfilePicture() != null && ! dataList.get(position).getProfilePicture().isEmpty()){
            try{
                Picasso.with(context).load(ApiClass.ImageBaseUrl+dataList.get(position).getProfilePicture()).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgProfile);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        else
            Picasso.with(context).load(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgProfile);
    }

    @Override
    public int getItemCount() {
        if(dataList != null)
            return dataList.size();
        return 0;
    }

    public void setData( List<FriendListDailiesOfFriends> list){
        this.dataList = list;
        this.notifyDataSetChanged();
    }

    public interface OnGroupUserClickListener {
        void onGroupUserClick(int indexz);
        void onHotspotClick(int index);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llRow;
        private com.mikhaellopez.circularimageview.CircularImageView imgProfile;
        private CustomTextView txtName, txtHotspot;
        public View item;
        public MyViewHolder(View view) {
            super(view);
            item = view;
            llRow = view.findViewById(R.id.llRow);
            txtName = view.findViewById(R.id.txtName);
            imgProfile = view.findViewById(R.id.imgProfile);
            txtHotspot = view.findViewById(R.id.txtHotspot);
        }
    }
}