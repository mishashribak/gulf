package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.khaleeji.Fragments.ImageFullScreenFragment;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.friendsNotInGroupPackage.GetFriendsNotInGroupUserData;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import Utility.ApiClass;
import Utility.SavePref;

/**
 * Created by Dcube on 21-11-2018.
 */

public class UsersNotInGroupAdapter extends RecyclerView.Adapter<UsersNotInGroupAdapter.GroupViewHolder> {

    private final String TAG = UsersNotInGroupAdapter.class.getName();

    Context context;
    List<GetFriendsNotInGroupUserData> friendsNotInGroupUserDataList;
    private boolean is_addfriend = false;
    private ArrayList<Integer> mSectionPositions;
    private OnclickListener mOnclickListener;
    ImageFullScreenFragment mdialog;
    int userid;

    List<Integer> slctdFrndsId = new ArrayList<>();

    public UsersNotInGroupAdapter(Context context, boolean is_addfriend, List<GetFriendsNotInGroupUserData> friendsNotInGroupUserDataList) {
        this.context = context;
        this.is_addfriend = is_addfriend;
        this.friendsNotInGroupUserDataList = friendsNotInGroupUserDataList;
        userid = SavePref.getInstance(context).getUserdetail().getId().intValue();
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_not_in_group_list, parent, false);

        return new GroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final GroupViewHolder holder, final int position) {

        holder.tvParticipantName.setText(friendsNotInGroupUserDataList.get(position).getUsername());

        String profileUrl = ApiClass.ImageBaseUrl + friendsNotInGroupUserDataList.get(position).getProfilePicture();

        Picasso.with(context).
                load(profileUrl)
                .placeholder(R.drawable.profile_placeholder)
                .error(R.drawable.profile_placeholder)
                .into(holder.profile_image);

        holder.cbSlctdParticipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (slctdFrndsId.contains(friendsNotInGroupUserDataList.get(position).getUserId()))
                {
                    int index = slctdFrndsId.indexOf(friendsNotInGroupUserDataList.get(position).getUserId());

                    slctdFrndsId.remove(index);
                }
                else
                {
                    slctdFrndsId.add(friendsNotInGroupUserDataList.get(position).getUserId());
                }

                Log.e(TAG,"slctdFrndsId : "+slctdFrndsId);

                Log.e(TAG,"slctdFrndsId.size() : "+slctdFrndsId.size());
            }
        });


//        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Log.e(TAG,"position : "+position);
//
//                if (mOnclickListener != null) {
//                    mOnclickListener.deleteGroup(position);
//                }
//            }
//        });
//
//        holder.relRowView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Log.e(TAG,"position : "+position);
//
//                if (mOnclickListener != null) {
//                    mOnclickListener.selectedGroup(position);
//                }
//            }
//        });


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
        return friendsNotInGroupUserDataList.size();
    }


    public void setOnClickListener(OnclickListener mOnclickListener) {
        this.mOnclickListener = mOnclickListener;
    }

    public void setData(List<GetFriendsNotInGroupUserData> data) {
        this.friendsNotInGroupUserDataList=data;
        notifyDataSetChanged();
    }

    public List<Integer> getSlctdFrndsId()
    {
        return slctdFrndsId;
    }

    public interface OnclickListener {
        public void deleteGroup(int pos);
        public void selectedGroup(int pos);

    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {
        private TextView tvParticipantName, tvmessage;
        private CircularImageView profile_image;
        LinearLayout item_view;
        RelativeLayout relRowView;

        CheckBox cbSlctdParticipant;

        public GroupViewHolder(View itemView) {
            super(itemView);
            item_view  =  itemView.findViewById(R.id.linear_layout);
            tvParticipantName =  itemView.findViewById(R.id.tvParticipantName);
            tvmessage =  itemView.findViewById(R.id.tvmessage);
            profile_image =  itemView.findViewById(R.id.profile_image);
            cbSlctdParticipant =  itemView.findViewById(R.id.cbSlctdParticipant);
            relRowView= itemView.findViewById(R.id.relRowView);
        }
    }
}

