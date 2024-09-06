package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.khaleeji.R;

import java.util.ArrayList;
import java.util.List;

import Model.ChatUserData;
import Utility.SavePref;

public class GsnapFriendListAdapter extends RecyclerView.Adapter<GsnapFriendListAdapter.MyViewHolder> implements Filterable {

    private List<ChatUserData> chatUserDataList;
    private Context context;
    private ContactListAdapter.OnItemClickListener onItemClickListener;
    private boolean isFromGsnap;
    private NameFilter nameFilter;

    public GsnapFriendListAdapter(List<ChatUserData> chatUserDataList,
                                  Context context,
                                  ContactListAdapter.OnItemClickListener onItemClickListener,
                                  boolean isFromGsnap) {
        this.chatUserDataList = chatUserDataList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.isFromGsnap = isFromGsnap;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_picture_recent_frnds, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        if (!chatUserDataList.get(position).getUser1().getUsername().toLowerCase()
                .equals(SavePref.getInstance(context)
                        .getUserdetail().getUsername().toLowerCase())) {
            holder.txtName.setText(chatUserDataList.get(position).getUser1().getUsername());
        } else {
            holder.txtName.setText(chatUserDataList.get(position).getUser2().getUsername());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.imgCheck.getVisibility() == View.VISIBLE) {
                    holder.imgCheck.setVisibility(View.INVISIBLE);
                    chatUserDataList.get(position).setChecked(false);
                } else {
                    holder.imgCheck.setVisibility(View.VISIBLE);
                    chatUserDataList.get(position).setChecked(true);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return chatUserDataList.size();
    }

    @Override
    public Filter getFilter() {
        if (nameFilter == null)
            nameFilter = new NameFilter(this, chatUserDataList);
        return nameFilter;
    }

    public interface OnItemClickListener {
        void onItemClick(String nodeName, int index);
    }

    public class NameFilter extends Filter{

        private final GsnapFriendListAdapter adapter;
        private List<ChatUserData> originalList = new ArrayList<>();
        private List<ChatUserData> filteredList;

        private NameFilter(GsnapFriendListAdapter adapter, List<ChatUserData> originalList) {
            super();
            this.adapter = adapter;
            this.originalList.addAll(originalList);
            this.filteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList.clear();
            final FilterResults results = new FilterResults();

            if (constraint.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();

                for (final ChatUserData user : originalList) {
                    if(!user.getUser1().getUsername().toLowerCase().equals(SavePref.getInstance(context)
                                    .getUserdetail().getUsername().toLowerCase())) {
                        if (user.getUser1().getUsername().toLowerCase().contains(filterPattern))
                            filteredList.add(user);
                    }else if(!user.getUser2().getUsername().toLowerCase().equals(SavePref.getInstance(context)
                            .getUserdetail().getUsername().toLowerCase())) {
                        if (user.getUser2().getUsername().toLowerCase().contains(filterPattern))
                            filteredList.add(user);
                    }
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.chatUserDataList.clear();
            adapter.chatUserDataList.addAll((List<ChatUserData>) results.values);
            adapter.notifyDataSetChanged();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName;
        private ImageView imgCheck;

        public MyViewHolder(View view) {
            super(view);
            txtName = view.findViewById(R.id.tv_username);
            imgCheck = itemView.findViewById(R.id.recent_friend_img_check);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imgCheck.getVisibility() == View.VISIBLE) {
                        imgCheck.setVisibility(View.INVISIBLE);
                    } else {
                        imgCheck.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }
}
