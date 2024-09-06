package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.khaleeji.R;
import com.app.khaleeji.Response.DialiesData;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StoryListAdapter extends RecyclerView.Adapter<StoryListAdapter.MyViewHolder> {

    private List<DialiesData> dataList;
    private Context context;
    private OnStroyClickListener onItemClickListener;

    public StoryListAdapter(List<DialiesData> dataList, Context context, OnStroyClickListener onItemClickListener) {
        this.dataList = dataList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_daily, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        if (position == 0) {
            holder.txtUserName.setText(dataList.get(position).getName());
            holder.rlAddDaily.setVisibility(View.VISIBLE);
            holder.imgUserProfile.setVisibility(View.GONE);
            (holder).imgAllView.setVisibility(View.GONE);
        } else {
            holder.rlAddDaily.setVisibility(View.GONE);
            holder.txtUserName.setText(dataList.get(position).getName());
            if (dataList.get(position).getIs_all_viewed().equals("1")) {
                (holder).imgAllView.setVisibility(View.VISIBLE);
                (holder).imgUserProfile.setVisibility(View.GONE);
            } else {
                (holder).imgAllView.setVisibility(View.GONE);
                Picasso.with(context)
                        .load(dataList.get(position).getPicture())
                        .error(R.drawable.profile_placeholder)
                        .placeholder(R.drawable.profile_placeholder)
                        .into((holder).imgUserProfile);
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onStoryClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        //    return 10;
        return dataList.size();
    }

    public interface OnStroyClickListener {
        void onStoryClick(int index);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgUserProfile, imgAllView;
        private TextView txtUserName;
        private RelativeLayout rlAddDaily;

        public MyViewHolder(View view) {
            super(view);
            imgUserProfile = view.findViewById(R.id.img_user_daily);
            txtUserName = view.findViewById(R.id.user_name);
            rlAddDaily = view.findViewById(R.id.img_add_daily);
            imgAllView = view.findViewById(R.id.img_user_all_view);
        }
    }
}