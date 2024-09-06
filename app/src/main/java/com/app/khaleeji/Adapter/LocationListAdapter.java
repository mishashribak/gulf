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
import android.widget.TextView;

import com.app.khaleeji.R;
import com.app.khaleeji.Response.FriendData;
import com.app.khaleeji.Response.HotSpotDatum;

import java.util.ArrayList;
import java.util.List;

import Constants.AppConstants;

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.MyViewHolder> implements Filterable {
    private List<HotSpotDatum> mOriginalList;
    private List<HotSpotDatum> dataList;
    private Context context;
    private OnLocationClickListener onItemClickListener;

    public LocationListAdapter(Context context, OnLocationClickListener onItemClickListener) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_location, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.llRowItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onLocationClick(dataList, position);
            }
        });
        HotSpotDatum hotspot= dataList.get(position);
        holder.data.setText(hotspot.getLocationName());
        if(hotspot.getIsHotspot()  && hotspot.getIs_admin().intValue() == 1 ) {
            holder.imgCategory.setImageDrawable(context.getResources().getDrawable(R.drawable.tea));
            return;
        }
        switch (hotspot.getCategory_id()){
            case AppConstants.RESTAURANT:
                //restaurants
                if(hotspot.getIsHotspot()  && hotspot.getIs_admin().intValue() == 0 ) {
                    holder.imgCategory.setImageDrawable(context.getResources().getDrawable(R.drawable.food));
                }else if( ! hotspot.getIsHotspot()){
                    //grey
                    holder.imgCategory.setImageDrawable(context.getResources().getDrawable(R.drawable.food_grey));
                }
                break;
            case AppConstants.CAFE:
                //cafe
                if(hotspot.getIsHotspot()  && hotspot.getIs_admin().intValue() == 0 ) {
                    holder.imgCategory.setImageDrawable(context.getResources().getDrawable(R.drawable.drink));
                }else if( ! hotspot.getIsHotspot()){
                    //grey
                    holder.imgCategory.setImageDrawable(context.getResources().getDrawable(R.drawable.drink_grey));
                }
                break;

            case AppConstants.SHOPPING:
                //shopping
                if(hotspot.getIsHotspot()  && hotspot.getIs_admin().intValue() == 0 ) {
                    holder.imgCategory.setImageDrawable(context.getResources().getDrawable(R.drawable.shop));
                }else if( ! hotspot.getIsHotspot()){
                    //grey
                    holder.imgCategory.setImageDrawable(context.getResources().getDrawable(R.drawable.shop_grey));
                }
                break;

            case AppConstants.HOTEL:
                //hotels
                if(hotspot.getIsHotspot()  && hotspot.getIs_admin().intValue() == 0 ) {
                    holder.imgCategory.setImageDrawable(context.getResources().getDrawable(R.drawable.sleep));
                }else if( ! hotspot.getIsHotspot()){
                    //grey
                    holder.imgCategory.setImageDrawable(context.getResources().getDrawable(R.drawable.sleep_grey));
                }
                break;

            case AppConstants.OUTDOOR:
                //outdoor
                if(hotspot.getIsHotspot()  && hotspot.getIs_admin().intValue() == 0 ) {
                    holder.imgCategory.setImageDrawable(context.getResources().getDrawable(R.drawable.tree));
                }else if( ! hotspot.getIsHotspot()){
                    //grey
                    holder.imgCategory.setImageDrawable(context.getResources().getDrawable(R.drawable.tree_grey));
                }
                break;

        }
    }

    public void setData(List<HotSpotDatum> dataList ){
        mOriginalList = dataList;
        this.dataList = dataList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(dataList == null){
            return 0;
        }
        return dataList.size();
    }


    public interface OnLocationClickListener {
        void onLocationClick(List<HotSpotDatum> dataList, int index);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView data;
        private TextView noData;
        private ImageView imgCategory;
        private LinearLayout llRowItem;
        public MyViewHolder(View view) {
            super(view);
            data = view.findViewById(R.id.data);
            noData = view.findViewById(R.id.noData);
            imgCategory = view.findViewById(R.id.imgPic);
            llRowItem = view.findViewById(R.id.llRowItem);
        }
    }

    protected List<HotSpotDatum> getFilteredResults(String constraint) {
        List<HotSpotDatum> results = new ArrayList<>();

        for (HotSpotDatum item : dataList) {
            if (item.getLocationName().toLowerCase().contains(constraint)) {
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
                List<HotSpotDatum> filteredResults = null;
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
                dataList = (ArrayList<HotSpotDatum>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}