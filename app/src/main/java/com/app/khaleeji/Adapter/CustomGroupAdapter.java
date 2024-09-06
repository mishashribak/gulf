package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.UserDetails;
import com.app.khaleeji.Response.groupListPackage.GroupData;
import com.app.khaleeji.Response.groupListPackage.GroupModel;

import java.util.ArrayList;
import java.util.List;
import CustomView.CustomTextView;
import Model.BlockedUserData;
import Model.CustomGroupData;

public class CustomGroupAdapter extends RecyclerView.Adapter<CustomGroupAdapter.MyViewHolder> {

    private  List<GroupModel> mGroupList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public CustomGroupAdapter(Context context,
                        OnItemClickListener onItemClickListener) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;

    }

    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_custom_group, parent, false);

        return new MyViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder( MyViewHolder holder, final int position) {
        if(position == mGroupList.size()-1){
           holder.line.setVisibility(View.GONE);
        }
        holder.txtGroupName.setText(mGroupList.get(position).getGroupName());
        holder.llGroupEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
            }
        });

        holder.imgGroupRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onRemove(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if( mGroupList != null)
            return mGroupList.size();
        return 0;
    }

    public void setData( List<GroupModel> list){
        this.mGroupList = list;
        this.notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick( int index);
        void onRemove(int index);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CustomTextView txtGroupName;
        private LinearLayout llGroupEdit;
        private ImageView imgGroupRemove;
        private View line;
        public MyViewHolder(View view) {
            super(view);
            txtGroupName = view.findViewById(R.id.txtGroupName);
            llGroupEdit = view.findViewById(R.id.llGroupEdit);
            line = view.findViewById(R.id.viewLine);
            imgGroupRemove = view.findViewById(R.id.imgGroupRemove);
        }
    }
}