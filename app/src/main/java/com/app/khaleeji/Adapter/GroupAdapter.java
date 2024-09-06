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

import java.util.List;

import Model.ChatUserData;
import Utility.SavePref;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyViewHolder> {

    private List<ChatUserData> chatUserDataList;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private boolean isFromGsnap;

    public GroupAdapter(List<ChatUserData> chatUserDataList,
                             Context context,
                             OnItemClickListener onItemClickListener,
                             boolean isFromGsnap) {
        this.chatUserDataList = chatUserDataList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.isFromGsnap = isFromGsnap;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_friend_chat, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        if (!chatUserDataList.get(position).getUser1().getUsername().toLowerCase()
                .equals(SavePref.getInstance(context)
                        .getUserdetail().getUsername().toLowerCase())) {
            holder.txtName.setText(chatUserDataList.get(position).getUser1().getUsername());
        } else {
            holder.txtName.setText(chatUserDataList.get(position).getUser2().getUsername());
        }
        if (!isFromGsnap) {
            if (chatUserDataList.get(position).getLastMessage() != null){
                holder.txtLastMessage.setText(chatUserDataList.get(position).getLastMessage().getMessage());
                holder.txtDate.setText(chatUserDataList.get(position).getLastMessage().getDate());
            }
//            holder.lytChat.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onItemClickListener.onItemClick(chatUserDataList.get(position).getRoomId(), position);
//                }
//            });
        }
    }

    @Override
    public int getItemCount() {
        return chatUserDataList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(String nodeName, int index);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName, txtDate, txtLastMessage;
        private ImageView imgProfile;
        private RelativeLayout lytChat;
        private ImageView imgCheck;

        public MyViewHolder(View view) {
            super(view);
            txtName = view.findViewById(R.id.txt_name);
            txtDate = view.findViewById(R.id.txt_date);
            txtLastMessage = view.findViewById(R.id.txt_last_message);
            imgProfile = view.findViewById(R.id.img_profile);
//            lytChat = view.findViewById(R.id.lyt);
//            imgCheck = itemView.findViewById(R.id.recent_friend_img_check);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (imgCheck.getVisibility() == View.VISIBLE) {
//                        imgCheck.setVisibility(View.INVISIBLE);
//                    } else {
//                        imgCheck.setVisibility(View.VISIBLE);
//                    }
//                }
//            });
        }
    }
}