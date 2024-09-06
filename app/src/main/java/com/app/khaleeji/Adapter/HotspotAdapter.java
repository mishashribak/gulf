package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.khaleeji.R;
import com.app.khaleeji.Response.fetchHotspotAndFrndsDetail.HotspotDetailData;

import java.util.List;

public class HotspotAdapter extends RecyclerView.Adapter<HotspotAdapter.MyViewHolder> {

    Context context;
    List<HotspotDetailData> hotspotDetailDataList;

    public HotspotAdapter(Context context, List<HotspotDetailData> hotspotDetailDataList) {
        this.context = context;
        this.hotspotDetailDataList = hotspotDetailDataList;
    }

    @NonNull
    @Override
    public HotspotAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_picture_recent_frnds, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final HotspotAdapter.MyViewHolder holder, final int position) {
        if (position % 2 == 0) {
            holder.txtName.setText(hotspotDetailDataList.get(position).getLocationName() + " Daily");
        } else {
            holder.txtName.setText(hotspotDetailDataList.get(position).getLocationName() + " Memory");
        }
        Log.d("temp", "onBindViewHolder: " + hotspotDetailDataList.get(position).getLocationName() + " Postion " + position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.imgCheck.getVisibility() == View.VISIBLE) {
                    holder.imgCheck.setVisibility(View.INVISIBLE);
                    hotspotDetailDataList.get(position).setChecked(false);
                } else {
                    holder.imgCheck.setVisibility(View.VISIBLE);
                    hotspotDetailDataList.get(position).setChecked(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return hotspotDetailDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        ImageView imgCheck;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.tv_username);
            imgCheck = itemView.findViewById(R.id.recent_friend_img_check);
        }
    }
}
