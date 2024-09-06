package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.app.khaleeji.R;

import java.util.ArrayList;
import java.util.List;

public class ProfileQuestionAdapter extends RecyclerView.Adapter<ProfileQuestionAdapter.MyViewHolder> implements Filterable {

    private List<String> mOriginalList;
    private List<String> mList;
    private Context mContext;
    private OnItemClickListener mItemClickListener;
    private int mRowIndex;

    public ProfileQuestionAdapter(List<String> dataList, Context context, OnItemClickListener mItemClickListener) {
        this.mList = dataList;
        this.mOriginalList = dataList;
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
        holder.txtView.setText(mList.get(position));
        holder.rowItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRowIndex = position;
                mItemClickListener.onItemClick(position);
            }
        });
        /*if(mRowIndex==position){
           holder.rowItem.setBackground(mContext.getResources().getDrawable(R.drawable.rounded_box_select_item));
        }
        else
        {
            holder.rowItem.setBackground(mContext.getResources().getDrawable(R.drawable.rounded_box_white));
        }*/
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int index);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtView;
        LinearLayout rowItem;
        public MyViewHolder(View view) {
            super(view);
            txtView = view.findViewById(R.id.txtView);
            rowItem = view.findViewById(R.id.rowItem);
        }
    }

    protected List<String> getFilteredResults(String constraint) {
        List<String> results = new ArrayList<>();

        for (String item : mList) {
            if (item.toLowerCase().contains(constraint)) {
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
                List<String> filteredResults = null;
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
                mList = (ArrayList<String>) results.values;
                notifyDataSetChanged();
            }
        };
    }
    
}