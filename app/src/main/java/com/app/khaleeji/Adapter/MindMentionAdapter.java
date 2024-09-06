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
import android.widget.TextView;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.Search.SearchByNameModel;

import java.util.ArrayList;
import java.util.List;

import CustomView.CustomTextView;

public class MindMentionAdapter extends RecyclerView.Adapter<MindMentionAdapter.MyViewHolder> implements Filterable {

    private List<SearchByNameModel> mOriginalList;
    private List<SearchByNameModel> mList;
    private Context mContext;
    private OnItemClickListener mItemClickListener;

    public MindMentionAdapter(Context context, OnItemClickListener mItemClickListener) {
        this.mContext = context;
        this.mItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_textitem_rtl, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.rowItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(mList.get(position).getUsername());
            }
        });

        holder.tvName.setText(mList.get(position).getFull_name());
        holder.tvUserName.setText('@'+mList.get(position).getUsername());
    }

    @Override
    public int getItemCount() {
        if(mList != null)
            return mList.size();
        return 0;
    }

    public interface OnItemClickListener {
        void onItemClick(String username);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CustomTextView tvUserName;
        private CustomTextView tvName;
        private LinearLayout rowItem;
        public MyViewHolder(View view) {
            super(view);
            tvUserName = view.findViewById(R.id.tvUserName);
            tvName = view.findViewById(R.id.tvName);
            rowItem = view.findViewById(R.id.rowItem);
        }
    }

    public void setData(List<SearchByNameModel> list){
        this.mList = list;
        this.mOriginalList = list;
        notifyDataSetChanged();
    }

    protected List<SearchByNameModel> getFilteredResults(String constraint) {
        List<SearchByNameModel> results = new ArrayList<>();

        for (SearchByNameModel item : mList) {
            if (item.getUsername().toLowerCase().contains(constraint)) {
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
                mList = (ArrayList<SearchByNameModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }
    
}