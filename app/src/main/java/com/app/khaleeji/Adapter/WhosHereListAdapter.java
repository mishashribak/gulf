package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.khaleeji.R;
import com.app.khaleeji.Response.HotSpotUser;
import com.app.khaleeji.Response.HotSpotUser;
import com.app.khaleeji.Response.UserDetails;
import com.app.khaleeji.Response.fetchHotspotAndFrndsDetail.HotspotDetailData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Constants.AppConstants;
import Constants.Bundle_Identifier;
import CustomView.CustomTextView;
import Model.ChatUserData;
import Utility.ApiClass;

public class WhosHereListAdapter extends RecyclerView.Adapter<WhosHereListAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private List<HotSpotUser> mHotSpotUserList;
    private OnItemClickListener onItemClickListener;
    private List<HotSpotUser> mOriginalList;

    public WhosHereListAdapter(Context context, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public WhosHereListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_whoshere, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final WhosHereListAdapter.MyViewHolder holder, final int position) {
//        holder.txtTime.setText("17 "+context.getResources().getString(R.string.hours)+" "+context.getResources().getString(R.string.ago));
//        if (Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(Bundle_Identifier.ArabicLang)) {
//            holder.txtTime.setText(context.getResources().getString(R.string.ago)+" 17 "+context.getResources().getString(R.string.hours));
//        }

        holder.imgUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position, AppConstants.TYPE_CIRCLE_IMG);
            }
        });

        holder.txtUnFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position, AppConstants.TYPE_UNFRIEND);
            }
        });

        holder.txtAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position, AppConstants.TYPE_ADD_FRIEND);
            }
        });


        holder.txtAddFriend.setVisibility(View.GONE);
        holder.txtUnFriend.setVisibility(View.GONE);
//        if(mHotSpotUserList.get(position).getStatus().equalsIgnoreCase("1")){
//            holder.txtAddFriend.setVisibility(View.GONE);
//            holder.txtUnFriend.setVisibility(View.VISIBLE);
//        }else{
//            holder.txtAddFriend.setVisibility(View.VISIBLE);
//            holder.txtUnFriend.setVisibility(View.GONE);
//        }

        if(mHotSpotUserList.get(position).getProfilePicture() != null && ! mHotSpotUserList.get(position).getProfilePicture().isEmpty())
            Picasso.with(context).load(ApiClass.ImageBaseUrl + mHotSpotUserList.get(position).getProfilePicture()).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgUserPic);
        else
            Picasso.with(context).load(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgUserPic);

        holder.txtTime.setText(mHotSpotUserList.get(position).getAgo());
        holder.txtFullName.setText(mHotSpotUserList.get(position).getFull_name());
        holder.txtUserName.setText(mHotSpotUserList.get(position).getUsername());
    }

    @Override
    public int getItemCount() {
        if( mHotSpotUserList != null)
            return mHotSpotUserList.size();
        return 0;
    }

    public void setData( List<HotSpotUser> list){
        this.mHotSpotUserList = list;
        mOriginalList = list;
        this.notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick( int index, int type);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CustomTextView txtTime;
        private com.mikhaellopez.circularimageview.CircularImageView imgUserPic;
        private CustomTextView txtFullName;
        private CustomTextView txtUserName;
        private CustomTextView txtUnFriend;
        private CustomTextView txtAddFriend;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtFullName = itemView.findViewById(R.id.txtFullName);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            imgUserPic = itemView.findViewById(R.id.imgProfile);
            txtUnFriend = itemView.findViewById(R.id.txtUnFriend);
            txtAddFriend =  itemView.findViewById(R.id.txtAddFriend);
        }
    }

    protected List<HotSpotUser> getFilteredResults(String constraint) {
        List<HotSpotUser> results = new ArrayList<>();

        for (HotSpotUser item : mHotSpotUserList) {
            if (item.getFull_name().toLowerCase().contains(constraint)) {
                results.add(item);
            }
        }
        return results;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<HotSpotUser> filteredResults = null;
                if (constraint.length() == 0) {
                    filteredResults = mOriginalList;
                } else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                mHotSpotUserList = (ArrayList<HotSpotUser>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
