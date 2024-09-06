package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.khaleeji.R;

/**
 * Created by nine on 20/9/17.
 */


public class AddFriendAdapter extends RecyclerView.Adapter<AddFriendAdapter.GroupViewHolder> {

    Context context;
    private boolean is_addfriend = false;

    public AddFriendAdapter(Context context, boolean is_addfriend) {
        this.context = context;
        this.is_addfriend = is_addfriend;
    }


    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_friend_adapter, parent, false);


        GroupViewHolder viewHolder = new GroupViewHolder(itemView);

        return new GroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {

        if (is_addfriend) {
            holder.tv_action.setText("ADD FRIEND");
        } else {
            holder.tv_action.setText("UNFRIEND");
        }

    }


    @Override
    public int getItemCount() {
        return 10;
    }


    public class GroupViewHolder extends RecyclerView.ViewHolder {
        private TextView tvusername, tvmessage, tv_action;
        private ImageView iv_profile;
        private RelativeLayout rl_action;

        public GroupViewHolder(View itemView) {
            super(itemView);
            tvusername = (TextView) itemView.findViewById(R.id.tvusername);
            tvmessage = (TextView) itemView.findViewById(R.id.tvmessage);
            //tv_action = (TextView) itemView.findViewById(R.id.tv_action);
            iv_profile = (ImageView) itemView.findViewById(R.id.profile_image);
            rl_action = (RelativeLayout) itemView.findViewById(R.id.rl_action);
        }
    }
}
