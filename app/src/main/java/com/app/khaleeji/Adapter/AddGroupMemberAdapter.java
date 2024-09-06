package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.FriendData;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class AddGroupMemberAdapter extends RecyclerView.Adapter<AddGroupMemberAdapter.GroupViewHolder> {

   private Context context;
   private List<FriendData> friendDataList;

    public AddGroupMemberAdapter(Context context,List<FriendData> friendDataList) {
      this.context = context;
      this.friendDataList = friendDataList;
    }


    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_friends_checklist
                , parent, false);

        return new GroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, final int position) {

        //holder.txtFriendName.setText(friendDataList.get(position).getFriendName());
//        Picasso.with(context)
//                .load(ApiClass.BASE_URL+friendDataList.get(position).getFromUserInfo().getProfilePicture())
//                .error(R.drawable.profile_placeholder)
//                .placeholder(R.drawable.profile_placeholder)
//                .into(holder.imgProfile);
        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                  //  friendDataList.get(position).setChecked(isChecked);
            }
        });

    }


    @Override
    public int getItemCount() {
        return friendDataList.size();
    }


    public class GroupViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imgProfile;
        private TextView txtFriendName;
        private CheckBox cb;

        public GroupViewHolder(View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.img_profile);
            txtFriendName = itemView.findViewById(R.id.txt_friend_name);
            cb = itemView.findViewById(R.id.checkbox);

        }
    }
}
