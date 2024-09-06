package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.khaleeji.R;
import com.app.khaleeji.Response.FriendData;
import com.app.khaleeji.Response.Search.SearchByNameModel;
import com.app.khaleeji.Response.TimeLineResponseData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import Constants.AppConstants;
import CustomView.CustomTextView;
import Utility.ApiClass;

public class SendToAdapter extends RecyclerView.Adapter<SendToAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private List<FriendData> mOriginalList;
    private List<FriendData> friendlist;
    private OnEventClickListener mOnclickListener;

    public SendToAdapter(Context context, OnEventClickListener clickListener) {
        this.context = context;
        mOnclickListener = clickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_sendto, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
       if(position == 1){
           holder.line.setVisibility(View.GONE);
       }

       if(friendlist.get(position).isChecked()){
           holder.imgCheck.setVisibility(View.VISIBLE);
       }else{
           holder.imgCheck.setVisibility(View.INVISIBLE);
       }

       holder.llRowItem.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               friendlist.get(position).setChecked(!friendlist.get(position).isChecked());
               if(friendlist.get(position).isChecked()){
                   holder.imgCheck.setVisibility(View.VISIBLE);
                   mOnclickListener.onClick(position, true );
               }else{
                   holder.imgCheck.setVisibility(View.GONE);
                   mOnclickListener.onClick(position, false );
               }

           }
       });
        holder.txtFullName.setText(friendlist.get(position).getFull_name());
        if(friendlist.get(position).getProfile_picture() != null && ! friendlist.get(position).getProfile_picture().isEmpty())
            Picasso.with(context).load(ApiClass.ImageBaseUrl + friendlist.get(position).getProfile_picture()).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgCircleProfile);
        else
            Picasso.with(context).load(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgCircleProfile);


    }

    @Override
    public int getItemViewType(int position) {
            return AppConstants.ITEM_TYPE_MEMORY;
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
        void onClick(int pos, boolean isSelected);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private View line;
        private LinearLayout llRowItem;
        private ImageView imgCheck;
        private CustomTextView txtFullName;
        private de.hdodenhof.circleimageview.CircleImageView imgCircleProfile;
        public MyViewHolder(View view) {
            super(view);
            line = view.findViewById(R.id.line);
            imgCheck = view.findViewById(R.id.imgCheck);
            llRowItem = view.findViewById(R.id.rowItem);
            imgCircleProfile = view.findViewById(R.id.imgProfile);
            txtFullName = view.findViewById(R.id.txtFullName);
        }
    }

    protected List<FriendData>  getFilteredResults(String constraint) {
        List<FriendData>  results = new ArrayList<>();

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
                List<FriendData>  filteredResults = null;
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
                friendlist = (List<FriendData>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
