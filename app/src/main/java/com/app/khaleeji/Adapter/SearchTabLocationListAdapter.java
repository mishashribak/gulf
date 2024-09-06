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
import com.app.khaleeji.Response.FriendData;
import com.app.khaleeji.Response.NearByResponse;
import com.app.khaleeji.Response.NearUserDetail;
import com.app.khaleeji.Response.Search.SearchByNameModel;
import com.app.khaleeji.Response.UserDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import CustomView.CustomTextView;
import Utility.ApiClass;


public class SearchTabLocationListAdapter extends RecyclerView.Adapter<SearchTabLocationListAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private List<SearchByNameModel> mUserList;
    private List<SearchByNameModel> mOriginalList;
    private OnclickListener mOnclickListener;

    public SearchTabLocationListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public SearchTabLocationListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_search_tab_location, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchTabLocationListAdapter.MyViewHolder holder, final int position) {
        holder.llRowItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnclickListener.onItemClick(0,position);
            }
        });
        holder.txtActionAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnclickListener.onItemClick(1, position);
            }
        });

        holder.txtActionUnFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnclickListener.onItemClick(2, position);
            }
        });
        holder.txtActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnclickListener.onItemClick(3, position);
            }
        });


        if(mUserList.get(position).getIsFriend().equals("2")){
            //add friend
            holder.txtActionAddFriend.setVisibility(View.VISIBLE);
            holder.txtActionUnFriend.setVisibility(View.GONE);
            holder.txtActionCancel.setVisibility(View.GONE);
        }else if(mUserList.get(position).getIsFriend().equals("1")){
            //unfriend
            holder.txtActionAddFriend.setVisibility(View.GONE);
            holder.txtActionUnFriend.setVisibility(View.VISIBLE);
            holder.txtActionCancel.setVisibility(View.GONE);
        }else if(mUserList.get(position).getIsFriend().equals("3")){
            //cancel
            holder.txtActionAddFriend.setVisibility(View.GONE);
            holder.txtActionUnFriend.setVisibility(View.GONE);
            holder.txtActionCancel.setVisibility(View.VISIBLE);
        }
        if(mUserList.get(position).getProfilePicture() != null && ! mUserList.get(position).getProfilePicture().isEmpty())
            Picasso.with(context).load(ApiClass.ImageBaseUrl + mUserList.get(position).getProfilePicture()).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgProfileImg);
        else
            Picasso.with(context).load(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgProfileImg);

        holder.tvFullName.setText(mUserList.get(position).getFull_name());
        holder.tvUserName.setText(mUserList.get(position).getUsername());
    }

    @Override
    public int getItemCount() {
        if( mUserList != null)
            return mUserList.size();
        return 0;
    }

    public void setData(List<SearchByNameModel> list){
        this.mUserList = list;
        mOriginalList = list;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llRowItem;
        private ImageView imgProfileImg;
        private CustomTextView tvFullName;
        private CustomTextView tvUserName;
        private CustomTextView txtActionAddFriend;
        private CustomTextView txtActionUnFriend;
        private CustomTextView txtActionCancel;
        public MyViewHolder(View itemView) {
            super(itemView);
            llRowItem = itemView.findViewById(R.id.llRowItem);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            imgProfileImg = itemView.findViewById(R.id.imgCircleProfile);
            txtActionUnFriend = itemView.findViewById(R.id.txtActionUnFriend);
            txtActionAddFriend = itemView.findViewById(R.id.txtActionAddFriend);
            txtActionCancel = itemView.findViewById(R.id.txtActionCancel);
        }
    }

    public interface OnclickListener {
        void onItemClick(int type, int pos);
    }

    public void setOnClickListener(OnclickListener onClickListener) {
        mOnclickListener = onClickListener;
    }

    protected List<SearchByNameModel> getFilteredResults(String constraint) {
        List<SearchByNameModel> results = new ArrayList<>();

        for (SearchByNameModel item : mUserList) {
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
                List<SearchByNameModel> filteredResults = null;
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
                mUserList = (List<SearchByNameModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
