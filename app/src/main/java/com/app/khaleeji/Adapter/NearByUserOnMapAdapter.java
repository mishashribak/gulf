package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.app.khaleeji.R;
import com.app.khaleeji.Response.NearUserDetail;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import CustomView.CustomTextView;
import Utility.ApiClass;

public class NearByUserOnMapAdapter extends RecyclerView.Adapter<NearByUserOnMapAdapter.MyViewHolder> implements Filterable {

    private List<NearUserDetail> mUserList;
    private List<NearUserDetail> mOriginalList;
    private Context context;
    private OnGroupUserClickListener onItemClickListener;

    public NearByUserOnMapAdapter(Context context, OnGroupUserClickListener onItemClickListener) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_groupuser, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.rlRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onGroupUserClick(position);
            }
        });

        if(mUserList.get(position).getProfilePicture() != null && ! mUserList.get(position).getProfilePicture().isEmpty())
            Picasso.with(context).load(ApiClass.ImageBaseUrl + mUserList.get(position).getProfilePicture()).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgProfileImg);
        else
            Picasso.with(context).load(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgProfileImg);
        holder.txtFullName.setText(mUserList.get(position).getFullName());
        holder.txtUserName.setText(mUserList.get(position).getUsername());
        if(mUserList.get(position).getDistance().doubleValue() <= 0.0){
            holder.txtDistance.setVisibility(View.INVISIBLE);
        }else{
            holder.txtDistance.setVisibility(View.VISIBLE);
            holder.txtDistance.setText(mUserList.get(position).getDistance()+" "+mUserList.get(position).getDistanceUnit());
        }

    }

    public void setData( List<NearUserDetail> list){
        this.mUserList = list;
        this.mOriginalList = list;
        notifyDataSetChanged();
    }
    
    @Override
    public int getItemCount() {
        if(mUserList != null )
            return mUserList.size();
        return 0;
    }

    public interface OnGroupUserClickListener {
        void onGroupUserClick(int index);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout rlRow;
        private ImageView imgProfileImg;
        private CustomTextView txtDistance;
        private CustomTextView txtFullName;
        private CustomTextView txtUserName;

        public MyViewHolder(View view) {
            super(view);
            rlRow = view.findViewById(R.id.rlRow);
            txtUserName = view.findViewById(R.id.txtUserName);
            txtFullName = view.findViewById(R.id.txtFullName);
            imgProfileImg = view.findViewById(R.id.imgProfile);
            txtDistance = view.findViewById(R.id.txtDistance);
        }
    }

    protected List<NearUserDetail> getFilteredResults(String constraint) {
        List<NearUserDetail> results = new ArrayList<>();

        for (NearUserDetail item : mOriginalList) {
            if (item.getFullName().toLowerCase().contains(constraint)) {
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
                List<NearUserDetail> filteredResults;
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
                mUserList = (ArrayList<NearUserDetail>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}