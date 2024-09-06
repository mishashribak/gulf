package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.khaleeji.R;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

import Constants.Bundle_Identifier;
import CustomView.CustomTextView;
import Model.CountTagModel;

public class SearchTabTagAdapter extends RecyclerView.Adapter<SearchTabTagAdapter.MyViewHolder> {//implements Filterable {

    Context context;
    private List<CountTagModel> mTagList;
    private List<CountTagModel> mOriginalList;
    private OnclickListener mOnclickListener;

    public SearchTabTagAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public SearchTabTagAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_search_tab_hashtag, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchTabTagAdapter.MyViewHolder holder, final int position) {
        CountTagModel countTagModel = mTagList.get(position);
        if (Locale.getDefault().getDisplayLanguage().equalsIgnoreCase(Bundle_Identifier.ArabicLang)) {
            holder.txtStatus.setText(countTagModel.arabicCount);
            holder.txtTag.setText(countTagModel.arabicText);
        }else{
            holder.txtStatus.setText(countTagModel.englishCount);
            holder.txtTag.setText(countTagModel.englishText);
        }

        holder.rlRowItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnclickListener.onItemClick(countTagModel);
            }
        });

        if(countTagModel.type.equals("3")){
            holder.llMemories.setVisibility(View.VISIBLE);
            for(int i=0; i<countTagModel.images.size();i++){
                switch (i){
                    case 0:
                        Glide.with(context).load(countTagModel.images.get(i)).centerCrop().into( holder.img1);
                        break;
                    case 1:
                        Glide.with(context).load(countTagModel.images.get(i)).centerCrop().into( holder.img2);
                        break;
                    case 2:
                        Glide.with(context).load(countTagModel.images.get(i)).centerCrop().into( holder.img3);
                        break;
                    case 3:
                        Glide.with(context).load(countTagModel.images.get(i)).centerCrop().into( holder.img4);
                        break;
                }
            }
        }else{
            holder.llMemories.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if( mTagList != null)
            return mTagList.size();
        return 0;
    }

    public void setData( List<CountTagModel> list){
        this.mTagList = list;
        mOriginalList = list;
        this.notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CustomTextView txtTag;
        private CustomTextView txtStatus;
        private LinearLayout rlRowItem, llMemories;
        private ImageView img1, img2, img3, img4;
        public MyViewHolder(View itemView) {
            super(itemView);
            txtTag = itemView.findViewById(R.id.txtTag);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            rlRowItem = itemView.findViewById(R.id.rlRowItem);
            img1 = itemView.findViewById(R.id.img1);
            img2 = itemView.findViewById(R.id.img2);
            img3 = itemView.findViewById(R.id.img3);
            img4 = itemView.findViewById(R.id.img4);
            llMemories = itemView.findViewById(R.id.llMemories);
        }
    }

    public interface OnclickListener {
        void onItemClick(CountTagModel tagModel);
    }

    public void setOnClickListener(OnclickListener onClickListener) {
        mOnclickListener = onClickListener;
    }

   /* protected List<CountTagModel>  getFilteredResults(String constraint) {
        List<CountTagModel>  results = new ArrayList<>();

        for (CountTagModel item : mOriginalList) {
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
                List<CountTagModel>  filteredResults = null;
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
                mTagList = (List<CountTagModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }*/
}
