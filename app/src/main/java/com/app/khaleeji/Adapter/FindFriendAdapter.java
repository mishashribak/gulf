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
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import CustomView.CustomTextView;
import Model.ContactUserModel;
import Utility.ApiClass;

public class FindFriendAdapter extends RecyclerView.Adapter<FindFriendAdapter.MyViewHolder>  implements Filterable {

    private Context context;
    private List<ContactUserModel> mOriginalList;
    private List<ContactUserModel> mList;
    private OnItemClickListener mOnItemClickListener;

    public FindFriendAdapter(Context context,  OnItemClickListener itemClickListener) {
        this.context = context;
        mOnItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public FindFriendAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_find_friend, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final FindFriendAdapter.MyViewHolder holder, final int position) {
        holder.llRowItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(0, position);
            }
        });

        holder.txtAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(1, position);
            }
        });

        holder.txtActionUnFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(2, position);
            }
        });

        holder.txtActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(3, position);
            }
        });

        if(mList.get(position).isFriend.equals("2")){
            holder.txtAddFriend.setVisibility(View.VISIBLE);
            holder.txtActionUnFriend.setVisibility(View.GONE);
            holder.txtActionCancel.setVisibility(View.GONE);
        }else if(mList.get(position).isFriend.equals("1")){
            holder.txtAddFriend.setVisibility(View.GONE);
            holder.txtActionUnFriend.setVisibility(View.VISIBLE);
            holder.txtActionCancel.setVisibility(View.GONE);
        }else if(mList.get(position).isFriend.equals("3")){
            holder.txtAddFriend.setVisibility(View.GONE);
            holder.txtActionUnFriend.setVisibility(View.GONE);
            holder.txtActionCancel.setVisibility(View.VISIBLE);
        }

        holder.txtFullname.setText(mList.get(position).full_name);
        holder.txtUsername.setText(mList.get(position).username);
        if(mList.get(position).profile_picture != null && ! mList.get(position).profile_picture.isEmpty())
            Picasso.with(context).load(ApiClass.ImageBaseUrl + mList.get(position).profile_picture).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgProfileImg);
        else
            Picasso.with(context).load(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgProfileImg);
    }

    @Override
    public int getItemCount() {
        if( mList != null)
            return mList.size();
        return 0;
    }

    public void setData( List<ContactUserModel> list){
        this.mList = list;
        this.notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int type, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llRowItem;
        private ImageView imgProfileImg;
        private CustomTextView txtFullname;
        private CustomTextView txtUsername;
        private RelativeLayout rlAction;
        private CustomTextView txtAddFriend;
        private CustomTextView txtActionCancel;
        private CustomTextView txtActionUnFriend;

        public MyViewHolder(View itemView) {
            super(itemView);
            llRowItem = itemView.findViewById(R.id.llRowItem);
            txtFullname = itemView.findViewById(R.id.tvFullname);
            txtUsername = itemView.findViewById(R.id.tvUsername);
            rlAction = itemView.findViewById(R.id.rlAction);
            imgProfileImg = itemView.findViewById(R.id.imgCircleProfile);
            txtAddFriend = itemView.findViewById(R.id.txtAddFriend);
            txtActionUnFriend = itemView.findViewById(R.id.txtActionUnFriend);
            txtActionCancel = itemView.findViewById(R.id.txtActionCancel);
        }
    }

    protected List<ContactUserModel> getFilteredResults(String constraint) {
        List<ContactUserModel> results = new ArrayList<>();

        for (ContactUserModel item : mList) {
            if (item.full_name.toLowerCase().contains(constraint)) {
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
                List<ContactUserModel> filteredResults = null;
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
                mList = (ArrayList<ContactUserModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
