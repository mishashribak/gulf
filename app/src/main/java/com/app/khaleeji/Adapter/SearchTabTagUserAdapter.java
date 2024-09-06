package com.app.khaleeji.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.khaleeji.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import CustomView.CustomTextView;
import Model.TagUserModel;
import Utility.ApiClass;

public class SearchTabTagUserAdapter extends RecyclerView.Adapter<SearchTabTagUserAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private List<TagUserModel> mUserList;
    private List<TagUserModel> mOriginalList;
    private OnclickListener mOnclickListener;
    private String type;
    private String tag;

    public SearchTabTagUserAdapter(Context context) {
        this.context = context;
    }

    public void setData( List<TagUserModel> list, String type, String tag){
        this.mUserList = list;
        mOriginalList = list;
        this.type = type;
        this.tag = tag;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchTabTagUserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tag_user, parent, false);
        return new SearchTabTagUserAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchTabTagUserAdapter.MyViewHolder holder, final int position) {
        holder.llRowItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnclickListener.onItemClick(0, mUserList.get(position));
            }
        });

//        if(mUserList.get(position).isFriend.equals("2")){
//            //add friend
//            holder.txtActionAddFriend.setVisibility(View.VISIBLE);
//            holder.txtActionUnFriend.setVisibility(View.GONE);
//            holder.txtActionCancel.setVisibility(View.GONE);
//        }else if(mUserList.get(position).isFriend.equals("1")){
//            //unfriend
//            holder.txtActionAddFriend.setVisibility(View.GONE);
//            holder.txtActionUnFriend.setVisibility(View.VISIBLE);
//            holder.txtActionCancel.setVisibility(View.GONE);
//        }else if(mUserList.get(position).isFriend.equals("3")){
//            //cancel
//            holder.txtActionAddFriend.setVisibility(View.GONE);
//            holder.txtActionUnFriend.setVisibility(View.GONE);
//            holder.txtActionCancel.setVisibility(View.VISIBLE);
//        }

        holder.txtActionAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnclickListener.onItemClick(1, mUserList.get(position));
            }
        });
        holder.txtActionUnFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnclickListener.onItemClick(2, mUserList.get(position));
            }
        });
        holder.txtActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnclickListener.onItemClick(3, mUserList.get(position));
            }
        });

        holder.txtFullname.setText(mUserList.get(position).username);
        holder.txtTag.setText(tag);
        if(type.equals("1")){
            holder.txtStatus.setText(mUserList.get(position).bio);
        }else{
            holder.txtStatus.setText(mUserList.get(position).profileStatus);
        }

        if(mUserList.get(position).profilePicture != null && ! mUserList.get(position).profilePicture.isEmpty())
            Picasso.with(context).load(ApiClass.ImageBaseUrl + mUserList.get(position).profilePicture).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgProfileImg);
    }

    @Override
    public int getItemCount() {
        if( mUserList != null)
            return mUserList.size();
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llRowItem;
        private ImageView imgProfileImg;
        private CustomTextView txtFullname;
        private CustomTextView txtTag;
        private CustomTextView txtStatus;
        private CustomTextView txtActionUnFriend;
        private CustomTextView txtActionCancel;
        private CustomTextView txtActionAddFriend;

        public MyViewHolder(View itemView) {
            super(itemView);
            llRowItem = itemView.findViewById(R.id.llRowItem);
            txtFullname = itemView.findViewById(R.id.txtFullname);
            txtTag = itemView.findViewById(R.id.txtTag);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            imgProfileImg = itemView.findViewById(R.id.imgProfileImg);
            txtActionUnFriend = itemView.findViewById(R.id.txtActionUnFriend);
            txtActionAddFriend = itemView.findViewById(R.id.txtActionAddFriend);
            txtActionCancel = itemView.findViewById(R.id.txtActionCancel);
        }
    }

    public interface OnclickListener {
        void onItemClick(int type, TagUserModel TagUserModel);
    }

    public void setOnClickListener(SearchTabTagUserAdapter.OnclickListener onClickListener) {
        mOnclickListener = onClickListener;
    }

    protected List<TagUserModel>  getFilteredResults(String constraint) {
        List<TagUserModel>  results = new ArrayList<>();
        for (TagUserModel item : mOriginalList) {
            if (item.full_name.toLowerCase().contains(constraint) || item.status.toLowerCase().contains(constraint)
            || item.bio.toLowerCase().contains(constraint) || item.profileStatus.contains(constraint)) {
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
                List<TagUserModel>  filteredResults = null;
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
                mUserList = (List<TagUserModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
