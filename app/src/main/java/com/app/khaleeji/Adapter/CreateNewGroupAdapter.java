package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.FriendData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import CustomView.CustomTextView;
import Model.BlockedUserData;
import Model.CustomGroupData;

public class CreateNewGroupAdapter extends RecyclerView.Adapter<CreateNewGroupAdapter.MyViewHolder> {

    private List<FriendData> friendlist;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public CreateNewGroupAdapter(Context context, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_create_new_group, parent, false);
        return new MyViewHolder(itemView);
    }

    public void setData(List<FriendData> data) {
        this.friendlist = data;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        if(position == friendlist.size()-1){
            holder.line.setVisibility(View.GONE);
        }
        holder.txtName.setText(friendlist.get(position).getFull_name());
        holder.btToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(position);
                boolean on = ((ToggleButton) view).isChecked();
                if (on) {

                } else {

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(this.friendlist == null)
            return 0;
        return friendlist.size();
    }

    public interface OnItemClickListener {
        void onItemClick( int index);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CustomTextView txtName;
        private ToggleButton btToggle;
        private View line;
        public MyViewHolder(View view) {
            super(view);
            txtName = view.findViewById(R.id.txtName);
            btToggle = view.findViewById(R.id.btSelected);
            line = view.findViewById(R.id.viewLine);
        }
    }
}