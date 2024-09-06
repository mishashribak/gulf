package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.app.khaleeji.R;
import com.app.khaleeji.Response.nearByLocation.NearByLocationResult;

import java.util.List;

import CustomView.CustomTextView;

/**
 * Created by Dcube on 11-10-2018.
 */

public class NearByLocationAdapter extends RecyclerView.Adapter<NearByLocationAdapter.ViewHolder> {
    List<NearByLocationResult> nearByLocations;
    private LayoutInflater inflater;
    private OnChoosingLocationListener onChoosingLocationListener;

    public NearByLocationAdapter(@NonNull Context context, List<NearByLocationResult> nearByLocations) {
        this.inflater = LayoutInflater.from(context);
        this.nearByLocations = nearByLocations;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_near_by_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvLocation.setText(nearByLocations.get(position).getName());
        holder.tvCityName.setText(nearByLocations.get(position).getVicinity());
    }

    @Override
    public int getItemCount() {
        return nearByLocations.size();
    }

    public void setOnImageClickListener(OnChoosingLocationListener onImageClickListener) {
        this.onChoosingLocationListener = onImageClickListener;
    }

    public interface OnChoosingLocationListener {
        void onChoosingLocationListener(int pos);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CustomTextView tvLocation, tvCityName;
        RelativeLayout relParentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvCityName = itemView.findViewById(R.id.tvCityName);
            relParentLayout = itemView.findViewById(R.id.relParentLayout);
            relParentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onChoosingLocationListener != null)
                        onChoosingLocationListener.onChoosingLocationListener(getAdapterPosition());
                }
            });
        }
    }

}
