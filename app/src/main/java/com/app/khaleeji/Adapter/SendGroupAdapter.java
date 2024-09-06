package com.app.khaleeji.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.khaleeji.R;
import com.app.khaleeji.Response.FriendData;
import com.app.khaleeji.Response.groupListPackage.GroupModel;
import com.squareup.picasso.Picasso;

import java.util.List;
import Constants.AppConstants;
import CustomView.CustomTextView;


public class SendGroupAdapter extends RecyclerView.Adapter<SendGroupAdapter.MyViewHolder> {

    private Context context;
    private List<GroupModel> groupList;
    private SendGroupAdapter.OnEventClickListener mOnclickListener;

    public SendGroupAdapter(Context context, SendGroupAdapter.OnEventClickListener clickListener) {
        this.context = context;
        mOnclickListener = clickListener;
    }

    @NonNull
    @Override
    public SendGroupAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_sendto, parent, false);

        return new SendGroupAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SendGroupAdapter.MyViewHolder holder, final int position) {
        if(position == 1){
            holder.line.setVisibility(View.GONE);
        }

        if(groupList.get(position).isChecked()){
            holder.imgCheck.setVisibility(View.VISIBLE);
        }else{
            holder.imgCheck.setVisibility(View.GONE);
        }

        holder.llRowItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupList.get(position).setChecked(!groupList.get(position).isChecked());
                if(groupList.get(position).isChecked()){
                    holder.imgCheck.setVisibility(View.VISIBLE);
                    mOnclickListener.onClick(position, true );
                }else{
                    holder.imgCheck.setVisibility(View.GONE);
                    mOnclickListener.onClick(position, false );
                }

            }
        });
        holder.txtFullName.setText(groupList.get(position).getGroupName());
//        Picasso.with(context).load(ApiClass.ImageBaseUrl + groupList.get(position).getProfile_picture()).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgCircleProfile);


    }

    @Override
    public int getItemViewType(int position) {
        return AppConstants.ITEM_TYPE_MEMORY;
    }

    @Override
    public int getItemCount() {
        if(this.groupList == null)
            return 0;
        return groupList.size();
    }


    public void setOnItemClickListener(SendGroupAdapter.OnEventClickListener mOnclickListener) {
        this.mOnclickListener = mOnclickListener;
    }

    public void setData(List<GroupModel> data) {
        this.groupList = data;
        notifyDataSetChanged();
    }

    public interface OnEventClickListener {
        void onClick(int pos, boolean isSelected);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private View line;
        private LinearLayout llRowItem;
        private ImageView imgCheck;
        private CustomTextView txtFullName;
        private de.hdodenhof.circleimageview.CircleImageView imgCircleProfile;
        public MyViewHolder(View view) {
            super(view);
            line = view.findViewById(R.id.line);
            imgCheck = view.findViewById(R.id.imgCheck);
            llRowItem = view.findViewById(R.id.rowItem);
            imgCircleProfile = view.findViewById(R.id.imgProfile);
            txtFullName = view.findViewById(R.id.txtFullName);
        }
    }
}
