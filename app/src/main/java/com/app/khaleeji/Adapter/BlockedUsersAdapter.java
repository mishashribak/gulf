package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.UserDetails;

import java.util.List;
import CustomView.CustomTextView;
import Model.BlockedUserData;
import Model.CustomGroupData;

public class BlockedUsersAdapter extends RecyclerView.Adapter<BlockedUsersAdapter.MyViewHolder> {

    private List<UserDetails> mUserList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public BlockedUsersAdapter( Context context, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_create_new_group, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        if(position == mUserList.size()-1){
            holder.line.setVisibility(View.GONE);
        }
        holder.txtName.setText(mUserList.get(position).getFullName());
        holder.txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(0,position);
            }
        });
        holder.btToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(1,position);
                boolean on = ((ToggleButton) view).isChecked();
                if (on) {

                } else {

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if( mUserList != null)
            return mUserList.size();
        return 0;
    }

    public void setData( List<UserDetails> list){
        this.mUserList = list;
        this.notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick( int type,  int index);
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