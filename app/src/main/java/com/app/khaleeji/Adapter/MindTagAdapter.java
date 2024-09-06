package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;

import com.app.khaleeji.R;
import com.app.khaleeji.Response.UserDetails;

import java.util.ArrayList;
import java.util.List;

import CustomView.CustomTextView;

public class MindTagAdapter extends RecyclerView.Adapter<MindTagAdapter.MyViewHolder> implements Filterable {

    private List<UserDetails> mOriginalList;
    private List<UserDetails> mList;
    private Context mContext;
    private OnItemClickListener mItemClickListener;

    public MindTagAdapter( Context context, OnItemClickListener mItemClickListener) {
        this.mContext = context;
        this.mItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_textitem, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.rowItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(mList.get(position).getTag());
            }
        });

        holder.tvTag.setText('#'+mList.get(position).getTag());
    }

    @Override
    public int getItemCount() {
        if(mList != null)
            return mList.size();
        return 0;
    }

    public void setData(List<UserDetails> dataList){
        this.mList = dataList;
        this.mOriginalList = dataList;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(String tag);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CustomTextView tvTag;
        private LinearLayout rowItem;
        public MyViewHolder(View view) {
            super(view);
            tvTag = view.findViewById(R.id.txtView);
            rowItem = view.findViewById(R.id.rowItem);
        }
    }

    protected List<UserDetails> getFilteredResults(String constraint) {
        List<UserDetails> results = new ArrayList<>();

        for (UserDetails item : mList) {
            if (item.getTag().toLowerCase().contains(constraint)) {
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
                List<UserDetails> filteredResults = null;
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
                mList = (ArrayList<UserDetails>) results.values;
                notifyDataSetChanged();
            }
        };
    }
    
}