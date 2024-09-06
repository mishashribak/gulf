package com.app.khaleeji.Adapter;

import android.app.DialogFragment;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.app.khaleeji.Fragments.ImageFullScreenFragment;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.FriendData;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import Utility.ApiClass;
import Utility.SavePref;

/**
 * Created by nine on 20/9/17.
 */


public class DiscoverFriendListAdapter extends RecyclerView.Adapter<DiscoverFriendListAdapter.GroupViewHolder> implements SectionIndexer, Filterable {

    Context context;
    List<FriendData> friendlist;
    private boolean is_addfriend = false;
    private ArrayList<Integer> mSectionPositions;
    private OnclickListener mOnclickListener;
    private List<FriendData> filteredist;
    private CountryFilter mCountryFilter;
    ImageFullScreenFragment mdialog;

    public DiscoverFriendListAdapter(Context context, boolean is_addfriend, List<FriendData> friendlist) {
        this.context = context;
        this.is_addfriend = is_addfriend;
        this.friendlist = friendlist;
        this.filteredist = friendlist;
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_friend_adapter, parent, false);
        GroupViewHolder viewHolder = new GroupViewHolder(itemView);

        return new GroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final GroupViewHolder holder, final int position) {
        holder.tv_action.setText(context.getString(R.string.unfriend));
        holder.rl_action.setVisibility(View.GONE);

//        if (friendlist.get(position).getToUserInfo() != null && friendlist.get(position).getToUserInfo() != null && friendlist.get(position).getToUserInfo().getUserId().intValue() != SavePref.getInstance(context).getUserdetail().getId()) {
//
//            holder.tvusername.setText(friendlist.get(position).getToUserInfo().getUsername());
//            if (friendlist.get(position).getAnswer() != null)
//                holder.tvmessage.setText(friendlist.get(position).getAnswer());
//            else
//                holder.tvmessage.setText("");
//            if (friendlist.get(position).getToUserInfo() != null && friendlist.get(position).getToUserInfo().getProfilePicture() != null && !friendlist.get(position).getToUserInfo().getProfilePicture().equalsIgnoreCase(""))
//                Picasso.with(context).load(ApiClass.getmApiClass().ImageBaseUrl + friendlist.get(position).getToUserInfo().getProfilePicture()).placeholder(R.mipmap.placeholder).error(R.mipmap.placeholder).into(holder.iv_profile);
//            else
//                holder.iv_profile.setImageResource(R.mipmap.placeholder);
//        } else if (friendlist.get(position).getFromUserInfo() != null && friendlist.get(position).getFromUserInfo() != null && friendlist.get(position).getFromUserInfo().getUserId().intValue() != SavePref.getInstance(context).getUserdetail().getId()) {
//
//            holder.tvusername.setText(friendlist.get(position).getFromUserInfo().getUsername());
//
//            if (friendlist.get(position).getFromUserInfo() != null && friendlist.get(position).getFromUserInfo().getAddress() != null)
//                holder.tvmessage.setText(friendlist.get(position).getFromUserInfo().getAddress());
//            else
//                holder.tvmessage.setText("");
//
//            if (friendlist.get(position).getFromUserInfo() != null && friendlist.get(position).getFromUserInfo().getProfilePicture() != null && !friendlist.get(position).getFromUserInfo().getProfilePicture().equalsIgnoreCase(""))
//                Picasso.with(context).load(ApiClass.getmApiClass().ImageBaseUrl + friendlist.get(position).getFromUserInfo().getProfilePicture()).placeholder(R.mipmap.placeholder).error(R.mipmap.placeholder).into(holder.iv_profile);
//            else
//                holder.iv_profile.setImageResource(R.mipmap.placeholder);
//
//        }


//        holder.tv_action.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mOnclickListener != null) {
//                    mOnclickListener.OnRemovelick(holder.getPosition());
//                }
//            }
//        });


//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mOnclickListener != null) {
//                    mOnclickListener.Onposclick(holder.getPosition());
//                }
//            }
//        });
//
//        holder.iv_profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mOnclickListener != null) {
//                    if (friendlist.get(position).getToUserInfo() != null && friendlist.get(position).getToUserInfo() != null && friendlist.get(position).getToUserInfo().getUserId().intValue() != SavePref.getInstance(context).getUserdetail().getId())
//                        mdialog = ImageFullScreenFragment.newInstance(ApiClass.ImageBaseUrl + friendlist.get(position).getToUserInfo().getProfilePicture());
//                    else if (friendlist.get(position).getFromUserInfo() != null && friendlist.get(position).getFromUserInfo() != null && friendlist.get(position).getFromUserInfo().getUserId().intValue() != SavePref.getInstance(context).getUserdetail().getId())
//                        mdialog = ImageFullScreenFragment.newInstance(ApiClass.ImageBaseUrl + friendlist.get(position).getFromUserInfo().getProfilePicture());
//
//                    //mdialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme);
//                    mdialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "DialogFragment");
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return friendlist.size();
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        List<String> sections = new ArrayList<>(26);
        mSectionPositions = new ArrayList<>(26);
        for (int i = 0, size = friendlist.size(); i < size; i++) {

//            if (friendlist.get(i).getToUserInfo() != null && friendlist.get(i).getToUserInfo() != null && friendlist.get(i).getToUserInfo().getUserId().intValue() != SavePref.getInstance(SendRequestAdapter.context).getUserdetail().getId()) {
//                String section = String.valueOf(friendlist.get(i).getToUserInfo().getUsername().charAt(0)).toUpperCase();
//                if (!sections.contains(section)) {
//                    sections.add(section);
//                    mSectionPositions.add(i);
//                }
//
//            } else {
//                String section = String.valueOf(friendlist.get(i).getFromUserInfo().getUsername().charAt(0)).toUpperCase();
//                if (!sections.contains(section)) {
//                    sections.add(section);
//                    mSectionPositions.add(i);
//                }
//
//            }


        }
        return sections.toArray(new String[0]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mSectionPositions.get(sectionIndex);
    }

    public void setOnClickListener(OnclickListener mOnclickListener) {
        this.mOnclickListener = mOnclickListener;
    }

    public void setData(List<FriendData> data) {
        this.friendlist = data;
        notifyDataSetChanged();
    }

    public interface OnclickListener {
        public void Onposclick(int pos);

        public void OnRemovelick(int pos);

    }


    public class GroupViewHolder extends RecyclerView.ViewHolder {
        private TextView tvusername, tvmessage, tv_action;
        private CircularImageView iv_profile;
        LinearLayout linear_layout;
        // private View item_view;
        private RelativeLayout rl_action;

        public GroupViewHolder(View itemView) {
            super(itemView);
            linear_layout = (LinearLayout) itemView.findViewById(R.id.linear_layout);
            tvusername = (TextView) itemView.findViewById(R.id.tvusername);
            tvmessage = (TextView) itemView.findViewById(R.id.tvmessage);
            //tv_action = (TextView) itemView.findViewById(R.id.tv_action);
            iv_profile = (CircularImageView) itemView.findViewById(R.id.profile_image);
//            rl_action = (RelativeLayout) itemView.findViewById(R.id.rl_action);

        }
    }

    @Override
    public Filter getFilter() {
        if (mCountryFilter == null)
            mCountryFilter = new CountryFilter(this, friendlist);
        return mCountryFilter;
    }

    private static class CountryFilter extends Filter {

        private final DiscoverFriendListAdapter adapter;
        private List<FriendData> originalList = new ArrayList<>();
        private List<FriendData> filteredList;

        private CountryFilter(DiscoverFriendListAdapter adapter, List<FriendData> originalList) {
            super();
            this.adapter = adapter;
            this.originalList.addAll(originalList);
            this.filteredList = new ArrayList<>();
        }


        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            //Here you have to implement filtering way
            filteredList.clear();
            final FilterResults results = new FilterResults();

            if (constraint.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();

                for (final FriendData user : originalList) {

//                    if (user.getToUserInfo() != null && user.getToUserInfo() != null && user.getToUserInfo().getUserId().intValue() != SavePref.getInstance(SendRequestAdapter.context).getUserdetail().getId()) {
//                        if (user.getToUserInfo().getUsername().toLowerCase().contains(filterPattern)) {
//                            filteredList.add(user);
//                        }
//                    } else {
//                        if (user.getFromUserInfo().getUsername().toLowerCase().contains(filterPattern)) {
//                            filteredList.add(user);
//                        }
//                    }


                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.filteredist.clear();
            adapter.filteredist.addAll((List<FriendData>) results.values);
            adapter.notifyDataSetChanged();
        }
    }
}
