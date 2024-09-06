package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.khaleeji.Fragments.ImageFullScreenFragment;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.groupListPackage.Group;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

import Utility.SavePref;

/**
 * Created by Dcube on 21-11-2018.
 */

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.GroupViewHolder> {

    private final String TAG = GroupListAdapter.class.getName();

    Context context;
    List<Group> groupList;
    private boolean is_addfriend = false;
    private ArrayList<Integer> mSectionPositions;
    private OnclickListener mOnclickListener;
    ImageFullScreenFragment mdialog;
    int userid;

    List<Integer> slctdFrndsId = new ArrayList<>();

    public GroupListAdapter(Context context, boolean is_addfriend, List<Group> groupList) {
        this.context = context;
        this.is_addfriend = is_addfriend;
        this.groupList = groupList;
        userid = SavePref.getInstance(context).getUserdetail().getId().intValue();
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_group_list, parent, false);
        GroupViewHolder viewHolder = new GroupViewHolder(itemView);

        return new GroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final GroupViewHolder holder, final int position) {

        holder.tvGroupName.setText(groupList.get(position).getGroupName());

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"position : "+position);
                if (mOnclickListener != null) {
                    mOnclickListener.deleteGroup(position);
                }
            }
        });

        holder.linGroupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"position : "+position);
                if (mOnclickListener != null) {
                    mOnclickListener.selectedGroup(position);
                }
            }
        });


//        holder.iv_profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (mOnclickListener != null)
//                {
//                    if(friendlist.get(position).getToUserInfo()!=null && friendlist.get(position).getToUserInfo()!=null && friendlist.get(position).getToUserInfo().getUserId().intValue()!= SavePref.getInstance(context).getUserdetail().getId())
//                        mdialog = ImageFullScreenFragment.newInstance(ApiClass.ImageBaseUrl + friendlist.get(position).getToUserInfo().getProfilePicture());
//                    else if(friendlist.get(position).getFromUserInfo()!=null && friendlist.get(position).getFromUserInfo()!=null && friendlist.get(position).getFromUserInfo().getUserId().intValue()!= SavePref.getInstance(context).getUserdetail().getId())
//                        mdialog = ImageFullScreenFragment.newInstance(ApiClass.ImageBaseUrl + friendlist.get(position).getFromUserInfo().getProfilePicture());
//
//                    mdialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme);
//                    mdialog.show(((AppCompatActivity)context).getSupportFragmentManager(), "DialogFragment");
//                }
//
//            }
//        });


    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }


    public void setOnClickListener(OnclickListener mOnclickListener) {
        this.mOnclickListener = mOnclickListener;
    }

    public void setData(List<Group> data) {
        this.groupList=data;
        notifyDataSetChanged();
    }

    public interface OnclickListener {
        public void deleteGroup(int pos);
        public void selectedGroup(int pos);

    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGroupName, tvmessage;
        private CircularImageView iv_profile;
        LinearLayout item_view;
        ImageView ivDelete;
        RelativeLayout relRowView;
        LinearLayout linGroupLayout;

        public GroupViewHolder(View itemView) {
            super(itemView);
            item_view  = (LinearLayout) itemView.findViewById(R.id.linear_layout);
            linGroupLayout  =  itemView.findViewById(R.id.linGroupLayout);
            tvGroupName = (TextView) itemView.findViewById(R.id.tvGroupName);
            tvmessage = (TextView) itemView.findViewById(R.id.tvmessage);
            iv_profile = (CircularImageView) itemView.findViewById(R.id.profile_image);
            ivDelete= itemView.findViewById(R.id.ivDelete);
            relRowView= itemView.findViewById(R.id.relRowView);
        }
    }
}


