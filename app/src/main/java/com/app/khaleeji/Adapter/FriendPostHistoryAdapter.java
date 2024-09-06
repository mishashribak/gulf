package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;
import com.app.khaleeji.R;
import java.util.List;

import Constants.AppConstants;
import CustomView.CustomTextView;
import Model.CustomGroupData;

public class FriendPostHistoryAdapter extends RecyclerView.Adapter<FriendPostHistoryAdapter.MyViewHolder> {

    private List<CustomGroupData> listData;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public FriendPostHistoryAdapter(     Context context,
                              OnItemClickListener onItemClickListener) {
       // this.listData = groupDataList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_post_history, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.txtLike.setText(context.getResources().getString(R.string.likes)+ " 20");
        holder.txtLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position, AppConstants.TYPE_LIKES);
            }
        });

        holder.imgUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position, AppConstants.TYPE_CIRCLE_IMG);
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
        CustomTextView txtLike;
        com.mikhaellopez.circularimageview.CircularImageView imgUserPic;
        public MyViewHolder(View view) {
            super(view);
            txtLike = view.findViewById(R.id.txtLike);
            imgUserPic = view.findViewById(R.id.imgProfile);
        }
    }
}