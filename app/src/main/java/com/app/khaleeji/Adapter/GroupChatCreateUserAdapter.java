package com.app.khaleeji.Adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.ToggleButton;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.FriendData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Constants.AppConstants;
import CustomView.CustomTextView;
import Utility.ApiClass;

public class GroupChatCreateUserAdapter extends RecyclerView.Adapter<GroupChatCreateUserAdapter.MyViewHolder> implements  Filterable {
    private Context context;
    private OnItemClickListener onItemClickListener;
    private List<FriendData> friendlist;
    private List<FriendData> mOriginalList;


    public GroupChatCreateUserAdapter(Context context,  OnItemClickListener onItemClickListener) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_group_chat_member, parent, false);
        return new MyViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if( friendlist.get(position).getProfile_picture() != null && ! friendlist.get(position).getProfile_picture().isEmpty()){
            try {
                Picasso.with(context).load(ApiClass.ImageBaseUrl + friendlist.get(position).getProfile_picture())
                        .placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgProfile);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else
            Picasso.with(context).load(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgProfile);
        holder.txtName.setText(friendlist.get(position).getFull_name());
        holder.imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(friendlist.get(position), AppConstants.TYPE_CLOSE);
            }
        });

        holder.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(friendlist.get(position), AppConstants.TYPE_PROFILE);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(this.friendlist == null)
            return 0;
        return friendlist.size();
    }

    public void setData(List<FriendData> data) {
        this.friendlist = data;
        mOriginalList = data;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick( FriendData friendData, int type);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CustomTextView txtName;
        private com.mikhaellopez.circularimageview.CircularImageView imgProfile;
        private ImageView imgClose;
        public MyViewHolder(View view) {
            super(view);
            txtName = view.findViewById(R.id.txtName);
            imgClose = view.findViewById(R.id.imgClose);
            imgProfile = view.findViewById(R.id.imgProfile);
        }
    }

    protected List<FriendData> getFilteredResults(String constraint) {
        List<FriendData> results = new ArrayList<>();

        for (FriendData item : mOriginalList) {
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
                List<FriendData> filteredResults;
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
                friendlist = (ArrayList<FriendData>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}