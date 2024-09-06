package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.app.khaleeji.Fragments.ImageFullScreenFragment;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.FriendData;
import com.app.khaleeji.Response.FriendData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import CustomView.CustomTextView;
import Utility.ApiClass;

public class MyFriendAdapter extends RecyclerView.Adapter<MyFriendAdapter.MyViewHolder>  implements Filterable {// implements SectionIndexer, Filterable {

    private Context context;
    private List<FriendData> mOriginalList;
    private List<FriendData> friendlist;
    private OnEventClickListener mOnclickListener;
    private int mType;

    public MyFriendAdapter(Context context, int type, List<FriendData> friendlist, OnEventClickListener clickListener) {
        this.context = context;
        mType = type;
        mOnclickListener = clickListener;
        this.friendlist = friendlist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_friend_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        switch (mType){
            case 0:
                holder.llMyFriends.setVisibility(View.VISIBLE);
                holder.llReceivedReq.setVisibility(View.GONE);
                holder.llSentReq.setVisibility(View.GONE);
                break;
            case 1:
                holder.llMyFriends.setVisibility(View.GONE);
                holder.llReceivedReq.setVisibility(View.VISIBLE);
                holder.llSentReq.setVisibility(View.GONE);
                break;
            case 2:
                holder.llMyFriends.setVisibility(View.GONE);
                holder.llReceivedReq.setVisibility(View.GONE);
                holder.llSentReq.setVisibility(View.VISIBLE);
                break;
        }

        if(friendlist.get(position).getQuestion() == null || friendlist.get(position).getQuestion().isEmpty()){
            holder.txtYourAnswer.setVisibility(View.GONE);
            holder.txtSeeAnswer.setVisibility(View.GONE);
        }else{
            holder.txtYourAnswer.setVisibility(View.VISIBLE);
            holder.txtSeeAnswer.setVisibility(View.VISIBLE);
        }

        holder.txtUnfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnclickListener.onClick(friendlist,0, friendlist.get(position));
            }
        });
        holder.txtYourAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnclickListener.onClick(friendlist,1, friendlist.get(position));
            }
        });
        holder.txtSeeAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnclickListener.onClick(friendlist,2, friendlist.get(position));
            }
        });

        holder.llMyFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnclickListener.onClick(friendlist,3, friendlist.get(position));
            }
        });

        holder.llReceivedReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnclickListener.onClick(friendlist,4, friendlist.get(position));
            }
        });

        holder.llSentReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnclickListener.onClick(friendlist,5, friendlist.get(position));
            }
        });

        holder.txtConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnclickListener.onClick(friendlist,6, friendlist.get(position));
            }
        });

        holder.txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnclickListener.onClick(friendlist,7, friendlist.get(position));
            }
        });

        holder.txtNudge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnclickListener.onClick(friendlist,8, friendlist.get(position));
            }
        });

        holder.txtRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnclickListener.onClick(friendlist,9, friendlist.get(position));
            }
        });
        holder.tvFullName.setText(friendlist.get(position).getFull_name());
        holder.tvFullNameR.setText(friendlist.get(position).getFull_name());
        holder.tvFullNameS.setText(friendlist.get(position).getFull_name());
//        holder.tvUserName.setText(friendlist.get(position).get);
        if(friendlist.get(position).getProfile_picture() != null && ! friendlist.get(position).getProfile_picture().isEmpty()){
            Picasso.with(context).load(ApiClass.ImageBaseUrl + friendlist.get(position).getProfile_picture()).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgCircleProfile);
            Picasso.with(context).load(ApiClass.ImageBaseUrl + friendlist.get(position).getProfile_picture()).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgCircleProfileR);
            Picasso.with(context).load(ApiClass.ImageBaseUrl + friendlist.get(position).getProfile_picture()).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgCircleProfileS);
        }else{
            Picasso.with(context).load(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgCircleProfile);
            Picasso.with(context).load(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgCircleProfileR);
            Picasso.with(context).load(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgCircleProfileS);
        }

    }

    @Override
    public int getItemCount() {
        if(this.friendlist == null)
            return 0;
        return friendlist.size();
    }


    public void setOnItemClickListener(OnEventClickListener mOnclickListener) {
        this.mOnclickListener = mOnclickListener;
    }

    public void setData(List<FriendData> data) {
        mOriginalList = data;
        this.friendlist = data;
        notifyDataSetChanged();
    }

    public interface OnEventClickListener {
        void onClick(List<FriendData> list, int type, FriendData pos);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llMyFriends;
        private LinearLayout llReceivedReq;
        private LinearLayout llSentReq;
        private CustomTextView txtYourAnswer;
        private CustomTextView txtSeeAnswer;
        private CustomTextView txtUnfriend;
        private CustomTextView tvFullName;
        private CustomTextView tvFullNameR;
        private CustomTextView tvFullNameS;
        private CustomTextView txtConfirm, txtDelete, txtNudge, txtRemove;
        private de.hdodenhof.circleimageview.CircleImageView imgCircleProfile;
        private de.hdodenhof.circleimageview.CircleImageView imgCircleProfileR;
        private de.hdodenhof.circleimageview.CircleImageView imgCircleProfileS;
        public MyViewHolder(View itemView) {
            super(itemView);
            llMyFriends = itemView.findViewById(R.id.llMyFriends);
            llReceivedReq = itemView.findViewById(R.id.llReceiviedReq);
            llSentReq = itemView.findViewById(R.id.llSentReq);
            txtSeeAnswer = itemView.findViewById(R.id.btSeeAnswer);
            txtYourAnswer = itemView.findViewById(R.id.btYourAnswer);
            txtUnfriend = itemView.findViewById(R.id.btUnfriend);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvFullNameR = itemView.findViewById(R.id.tvFullNameR);
            tvFullNameS = itemView.findViewById(R.id.tvFullNameS);
            txtConfirm = itemView.findViewById(R.id.txtConfirm);
            txtDelete = itemView.findViewById(R.id.txtDelete);
            txtNudge = itemView.findViewById(R.id.txtNudge);
            txtRemove = itemView.findViewById(R.id.txtRemove);
            imgCircleProfile = itemView.findViewById(R.id.imgCircleProfile);
            imgCircleProfileR = itemView.findViewById(R.id.imgCircleProfileR);
            imgCircleProfileS = itemView.findViewById(R.id.imgCircleProfileS);
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
