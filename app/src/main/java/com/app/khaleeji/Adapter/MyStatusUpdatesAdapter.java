package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ToggleButton;
import com.app.khaleeji.R;
import java.util.List;

import Constants.AppConstants;
import CustomView.CustomTextView;
import Model.CustomGroupData;

public class MyStatusUpdatesAdapter extends RecyclerView.Adapter<MyStatusUpdatesAdapter.MyViewHolder> {

    private List<CustomGroupData> listData;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public MyStatusUpdatesAdapter(     Context context,
                              OnItemClickListener onItemClickListener) {
       // this.listData = groupDataList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_my_status_updates, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.llLikesBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position, AppConstants.TYPE_LIKES);
            }
        });
    }

//    @Override
//    public int getItemCount() {
//        return listData.size();
//    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public interface OnItemClickListener {
        void onItemClick( int index, int type);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llLikesBox;
        public MyViewHolder(View view) {
            super(view);
            llLikesBox = view.findViewById(R.id.likesBox);
        }
    }
}