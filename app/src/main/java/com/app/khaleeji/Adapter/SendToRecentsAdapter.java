package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.khaleeji.R;
import com.app.khaleeji.Response.TimeLineResponseData;
import com.squareup.picasso.Picasso;

import Constants.AppConstants;

public class SendToRecentsAdapter extends RecyclerView.Adapter<SendToRecentsAdapter.MyViewHolder> {

    private Context context;
    private OnItemClickListener onItemClickListener;
    private boolean isSel = true;

    public SendToRecentsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_sendto_recents, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
       if(position == 1){
           holder.line.setVisibility(View.GONE);
       }

        holder.llRowItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSel = !isSel;
                if(isSel)
                    holder.imgCheck.setVisibility(View.VISIBLE);
                else
                    holder.imgCheck.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
            return AppConstants.ITEM_TYPE_MEMORY;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, String clickType);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private View line;
        private LinearLayout llRowItem;
        private ImageView imgCheck;
        public MyViewHolder(View view) {
            super(view);
            line = view.findViewById(R.id.line);
            imgCheck = view.findViewById(R.id.imgCheck);
            llRowItem = view.findViewById(R.id.rowItem);
        }
    }
}
