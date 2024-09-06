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
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Constants.Bundle_Identifier;
import CustomView.CustomTextView;
import Model.SubTagModel;
import Utility.ApiClass;

public class SearchTabSubTagAdapter extends RecyclerView.Adapter<SearchTabSubTagAdapter.MyViewHolder>{ // implements Filterable {

    private Context context;
    private List<SubTagModel> mSubTagModelList;
    private OnclickListener mOnclickListener;

    public SearchTabSubTagAdapter(Context context) {
        this.context = context;
    }

    public void setData( List<SubTagModel> list){
        this.mSubTagModelList = list;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchTabSubTagAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sub_tag, parent, false);
        return new SearchTabSubTagAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchTabSubTagAdapter.MyViewHolder holder, final int position) {
        SubTagModel subTagModel = mSubTagModelList.get(position);
        holder.llRowItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnclickListener.onItemClick(subTagModel);
            }
        });

        if (Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(Bundle_Identifier.ArabicLang)) {
            holder.txtCountTag.setText(subTagModel.arabicLabel);
        }else{
            holder.txtCountTag.setText(subTagModel.label);
        }
        holder.txtSubTag.setText(subTagModel.tags);

        if(subTagModel.type.equals("3")){
            holder.llMemories.setVisibility(View.VISIBLE);
            for(int i=0; i< subTagModel.images.size();i++){
                switch (i){
                    case 0:
                        Glide.with(context).load(subTagModel.images.get(i).thumbnail).centerCrop().into( holder.img1);
                        break;
                    case 1:
                        Glide.with(context).load(subTagModel.images.get(i).thumbnail).centerCrop().into( holder.img2);
                        break;
                    case 2:
                        Glide.with(context).load(subTagModel.images.get(i).thumbnail).centerCrop().into( holder.img3);
                        break;
                    case 3:
                        Glide.with(context).load(subTagModel.images.get(i).thumbnail).centerCrop().into( holder.img4);
                        break;
                }
            }
        }else{
            holder.llMemories.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if( mSubTagModelList != null)
            return mSubTagModelList.size();
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llRowItem;
        private LinearLayout llMemories;
        private CustomTextView txtSubTag;
        private CustomTextView txtCountTag;
        private ImageView img1, img2, img3, img4;

        public MyViewHolder(View itemView) {
            super(itemView);
            llRowItem = itemView.findViewById(R.id.llRowItem);
            txtSubTag = itemView.findViewById(R.id.txtSubTag);
            txtCountTag = itemView.findViewById(R.id.txtCountTag);
            llMemories = itemView.findViewById(R.id.llMemories);
            img1 = itemView.findViewById(R.id.img1);
            img2 = itemView.findViewById(R.id.img2);
            img3 = itemView.findViewById(R.id.img3);
            img4 = itemView.findViewById(R.id.img4);
        }
    }

    public interface OnclickListener {
        void onItemClick(SubTagModel SubTagModel);
    }

    public void setOnClickListener(SearchTabSubTagAdapter.OnclickListener onClickListener) {
        mOnclickListener = onClickListener;
    }

//    protected List<SubTagModel>  getFilteredResults(String constraint) {
//        List<SubTagModel>  results = new ArrayList<>();
//
//        for (SubTagModel item : mOriginalList) {
//            if (item.full_name.toLowerCase().contains(constraint)) {
//                results.add(item);
//            }
//        }
//        return results;
//    }
//
//    @Override
//    public Filter getFilter() {
//
//        return new Filter() {
//
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                List<SubTagModel>  filteredResults = null;
//                if (constraint.length() == 0) {
//                    filteredResults = mOriginalList;
//                } else {
//                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());
//                }
//
//                FilterResults results = new FilterResults();
//                results.values = filteredResults;
//
//                return results;
//            }
//
//            @Override
//            protected void publishResults(CharSequence charSequence, FilterResults results) {
//                mSubTagModelList = (List<SubTagModel>) results.values;
//                notifyDataSetChanged();
//            }
//        };
//    }
}
