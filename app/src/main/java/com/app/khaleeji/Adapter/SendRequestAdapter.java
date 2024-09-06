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
 * Created by nine on 19/9/17.
 */


public class SendRequestAdapter extends RecyclerView.Adapter<SendRequestAdapter.GroupViewHolder> implements Filterable {

    public static Context context;
    private OnclickListener mOnclickListener;
    private List<FriendData> mlist;
    private List<FriendData> filteredist;
    private CountryFilter mCountryFilter;
    ImageFullScreenFragment  mdialog;

    public SendRequestAdapter(Context context, List<FriendData> mlist) {
        this.context = context;
        this.mlist = mlist;
        this.filteredist = mlist;

    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_request_adapter, parent, false);
        GroupViewHolder viewHolder = new GroupViewHolder(itemView);
        return new GroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final GroupViewHolder holder, final int position) {

//        if (mlist.get(position).getToUserInfo() != null && mlist.get(position).getToUserInfo() != null && mlist.get(position).getToUserInfo().getUserId().intValue() != SavePref.getInstance(context).getUserdetail().getId())
//        {
//
//            holder.tv_username.setText(mlist.get(position).getToUserInfo().getUsername());
//            if(mlist.get(position).getToUserInfo().getPrivacy().equalsIgnoreCase("private") && mlist.get(position).getToUserInfo().getAll_other_to_seeprofile()==1){
//                if (mlist.get(position).getAnswer() != null && mlist.get(position).getAnswer().length()>0)
//                    holder.tvdescription.setText(R.string.seeyouranswer);
//                else
//                    holder.tvdescription.setText("");
//
//                if (mlist.get(position).getToUserInfo() != null && mlist.get(position).getToUserInfo().getProfilePicture() != null && !mlist.get(position).getToUserInfo().getProfilePicture().equalsIgnoreCase(""))
//                    Picasso.with(context).load(ApiClass.getmApiClass().ImageBaseUrl + mlist.get(position).getToUserInfo().getProfilePicture()).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).into(holder.profile_image);
//                else
//                    holder.profile_image.setImageResource(R.drawable.profile_placeholder);
//            }
//            else if(mlist.get(position).getToUserInfo().getPrivacy().equalsIgnoreCase("private") && mlist.get(position).getToUserInfo().getAll_other_to_seeprofile()==0){
//                if (mlist.get(position).getAnswer() != null && mlist.get(position).getAnswer().length()>0)
//                    holder.tvdescription.setText(R.string.seeyouranswer);
//                else
//                    holder.tvdescription.setText("");
//
//                holder.profile_image.setImageResource(R.drawable.private_profile);
//            }
//            else
//             {
//                 if (mlist.get(position).getAnswer() != null && mlist.get(position).getAnswer().length()>0)
//                     holder.tvdescription.setText(R.string.seeyouranswer);
//                 else
//                     holder.tvdescription.setText("");
//
//
//                 if (mlist.get(position).getToUserInfo() != null && mlist.get(position).getToUserInfo().getProfilePicture() != null && !mlist.get(position).getToUserInfo().getProfilePicture().equalsIgnoreCase(""))
//                    Picasso.with(context).load(ApiClass.getmApiClass().ImageBaseUrl + mlist.get(position).getToUserInfo().getProfilePicture()).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).into(holder.profile_image);
//                else
//                   holder.profile_image.setImageResource(R.drawable.profile_placeholder);
//            }
//
//        }
//        else if (mlist.get(position).getFromUserInfo() != null && mlist.get(position).getFromUserInfo() != null && mlist.get(position).getFromUserInfo().getUserId().intValue() != SavePref.getInstance(context).getUserdetail().getId())
//        {
//
//            holder.tv_username.setText(mlist.get(position).getFromUserInfo().getUsername());
//
//            if(mlist.get(position).getFromUserInfo().getPrivacy().equalsIgnoreCase("private") && mlist.get(position).getFromUserInfo().getAll_other_to_seeprofile()==1){
//                if (mlist.get(position).getAnswer() != null && mlist.get(position).getAnswer().length()>0)
//                    holder.tvdescription.setText(R.string.seeyouranswer);
//                else
//                    holder.tvdescription.setText("");
//
//
//                if (mlist.get(position).getFromUserInfo() != null && mlist.get(position).getFromUserInfo().getProfilePicture() != null && !mlist.get(position).getFromUserInfo().getProfilePicture().equalsIgnoreCase(""))
//                    Picasso.with(context).load(ApiClass.getmApiClass().ImageBaseUrl + mlist.get(position).getFromUserInfo().getProfilePicture()).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).into(holder.profile_image);
//                else
//                    holder.profile_image.setImageResource(R.drawable.profile_placeholder);
//            }
//            else if(mlist.get(position).getFromUserInfo().getPrivacy().equalsIgnoreCase("private") && mlist.get(position).getFromUserInfo().getAll_other_to_seeprofile()==0){
//                if (mlist.get(position).getAnswer() != null && mlist.get(position).getAnswer().length()>0)
//                    holder.tvdescription.setText(R.string.seeyouranswer);
//                else
//                    holder.tvdescription.setText("");
//
//                holder.profile_image.setImageResource(R.drawable.private_profile);
//            }
//            else{
//
//                if (mlist.get(position).getAnswer() != null && mlist.get(position).getAnswer().length()>0)
//                    holder.tvdescription.setText(R.string.seeyouranswer);
//                else
//                    holder.tvdescription.setText("");
//
//                if (mlist.get(position).getFromUserInfo() != null && mlist.get(position).getFromUserInfo().getProfilePicture() != null && !mlist.get(position).getFromUserInfo().getProfilePicture().equalsIgnoreCase(""))
//                    Picasso.with(context).load(ApiClass.getmApiClass().ImageBaseUrl + mlist.get(position).getFromUserInfo().getProfilePicture()).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).into(holder.profile_image);
//                else
//                    holder.profile_image.setImageResource(R.drawable.profile_placeholder);
//            }
//
//        }
//
//
//
//
//        holder.tv_done.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mOnclickListener != null) {
//                    mOnclickListener.onFriendRemove(holder.getPosition());
//                }
//            }
//        });
//
//        holder.profile_image.setTag(position);
//
//        holder.profile_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mOnclickListener != null) {
//                  int position=(int)v.getTag();
//                  try
//                  {
//                      if (mlist.get(position).getToUserInfo() != null && mlist.get(position).getToUserInfo() != null && mlist.get(position).getToUserInfo().getUserId().intValue() != SavePref.getInstance(context).getUserdetail().getId())
//                      {
//                             if(mlist.get(position).getToUserInfo().getPrivacy().equalsIgnoreCase("private") && mlist.get(position).getToUserInfo().getAll_other_to_seeprofile()==1){
//                                mdialog = ImageFullScreenFragment.newInstance(ApiClass.ImageBaseUrl + mlist.get(position).getToUserInfo().getProfilePicture());
//                                 mdialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme);
//                                 mdialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "DialogFragment");
//                             }
//                            else if(mlist.get(position).getToUserInfo().getPrivacy().equalsIgnoreCase("private") && mlist.get(position).getToUserInfo().getAll_other_to_seeprofile()==0){
//                                   //NOTHING TO SHOW HERE BECAUSE OF PRIVATE PROFILE
//                            }
//                            else
//                            {
//                                mdialog = ImageFullScreenFragment.newInstance(ApiClass.ImageBaseUrl + mlist.get(position).getToUserInfo().getProfilePicture());
//                                mdialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme);
//                                mdialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "DialogFragment");
//                            }
//                      }
//                      else if (mlist.get(position).getFromUserInfo() != null && mlist.get(position).getFromUserInfo() != null && mlist.get(position).getFromUserInfo().getUserId().intValue() != SavePref.getInstance(context).getUserdetail().getId())
//                      {
//                          if(mlist.get(position).getFromUserInfo().getPrivacy().equalsIgnoreCase("private") && mlist.get(position).getFromUserInfo().getAll_other_to_seeprofile()==1){
//                             mdialog = ImageFullScreenFragment.newInstance(ApiClass.ImageBaseUrl + mlist.get(position).getFromUserInfo().getProfilePicture());
//                              mdialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme);
//                              mdialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "DialogFragment");
//                          }
//                          else if(mlist.get(position).getFromUserInfo().getPrivacy().equalsIgnoreCase("private") && mlist.get(position).getFromUserInfo().getAll_other_to_seeprofile()==0){
//                              //NOTHING TO SHOW HERE BECAUSE OF PRIVATE PROFILE
//                          }
//                          else{
//                              mdialog = ImageFullScreenFragment.newInstance(ApiClass.ImageBaseUrl + mlist.get(position).getFromUserInfo().getProfilePicture());
//                              mdialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme);
//                              mdialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "DialogFragment");
//                          }
//                      }
//
//
//                     }
//                     catch (Exception e){
//
//                     }
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
//
//
//        holder.tvdescription.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mOnclickListener != null) {
//                    mOnclickListener.seeAnswer(holder.getPosition());
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public void setOnClickListener(OnclickListener mOnclickListener) {
        this.mOnclickListener = mOnclickListener;
    }

    public void setData(List<FriendData> mFriendlistResponse) {
        this.filteredist = mFriendlistResponse;
        notifyDataSetChanged();
    }

    public interface OnclickListener {
        public void Onposclick(int pos);

        public void onFriendRemove(int pos);

        public void seeAnswer(int pos);
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {
        private CircularImageView profile_image;
        private TextView tv_username, tvdescription, tv_done;
        private  View item_view;

        public GroupViewHolder(View itemView) {
            super(itemView);

            item_view  = itemView;
            profile_image = (CircularImageView) itemView.findViewById(R.id.profile_image);
            tv_username = (TextView) itemView.findViewById(R.id.tv_username);
            tvdescription = (TextView) itemView.findViewById(R.id.tvdescription);
            tv_done = (TextView) itemView.findViewById(R.id.tv_done);
        }
    }


    @Override
    public Filter getFilter() {
        if (mCountryFilter == null)
            mCountryFilter = new CountryFilter(this, mlist);
        return mCountryFilter;
    }


    private static class CountryFilter extends Filter {

        private final SendRequestAdapter adapter;
        private List<FriendData> originalList = new ArrayList<>();
        private List<FriendData> filteredList;

        private CountryFilter(SendRequestAdapter adapter, List<FriendData> originalList) {
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
//                         filteredList.add(user);
//                        }
//                    }else{
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

            if(results!=null && (List<FriendData>) results.values!=null)
            {   adapter.filteredist.clear();
                adapter.filteredist.addAll((List<FriendData>) results.values);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
