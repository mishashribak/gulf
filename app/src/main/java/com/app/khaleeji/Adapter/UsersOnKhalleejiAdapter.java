package com.app.khaleeji.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import androidx.recyclerview.widget.RecyclerView;
import com.app.khaleeji.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import CustomView.CustomTextView;
import Model.ContactUserModel;
import Utility.ApiClass;

public class UsersOnKhalleejiAdapter extends RecyclerView.Adapter<UsersOnKhalleejiAdapter.MyViewHolder> implements Filterable{// , SectionIndexer {

    private Context context;
    private OnItemClickListener mOnItemClickListener;
    private List<ContactUserModel> mList;
    private List<ContactUserModel> mOriginalList;
    private List<String> mFirstLetterArray = new ArrayList<String>();
    private ArrayList<Integer> mSectionPositions;

    public UsersOnKhalleejiAdapter(Context context, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setData(List<ContactUserModel> data) {
        this.mList = data;
        mOriginalList = data;
        if(mList != null){
            Collections.sort(mList, new NameComparator());
            for (int j = 0, size = mList.size(); j < size; j++) {
                String section = String.valueOf(mList.get(j).full_name.charAt(0)).toUpperCase();
                mFirstLetterArray.add(section);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_users_khaleeji, parent, false);
        return new MyViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.llRowItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(0, position);
            }
        });

        holder.txtAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(1, position);
            }
        });

        holder.txtActionUnFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(2, position);
            }
        });

        holder.txtActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(3, position);
            }
        });


        if(mList.get(position).status.equals("0")){
            holder.txtAddFriend.setVisibility(View.VISIBLE);
            holder.txtActionUnFriend.setVisibility(View.GONE);
            holder.txtActionCancel.setVisibility(View.GONE);
        }else if(mList.get(position).status.equals("1")){
            holder.txtAddFriend.setVisibility(View.GONE);
            holder.txtActionUnFriend.setVisibility(View.VISIBLE);
            holder.txtActionCancel.setVisibility(View.GONE);
        }else if(mList.get(position).status.equals("2")){
            holder.txtAddFriend.setVisibility(View.GONE);
            holder.txtActionUnFriend.setVisibility(View.GONE);
            holder.txtActionCancel.setVisibility(View.VISIBLE);
        }

        holder.txtFullname.setText(mList.get(position).full_name);
        holder.txtUsername.setText(mList.get(position).username);
        if(mList.get(position).profile_picture != null && ! mList.get(position).profile_picture.isEmpty())
            Picasso.with(context).load(ApiClass.ImageBaseUrl + mList.get(position).profile_picture).placeholder(R.drawable.profile_placeholder).error(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgProfileImg);
        else
            Picasso.with(context).load(R.drawable.profile_placeholder).fit().centerInside().into(holder.imgProfileImg);
    }

    @Override
    public int getItemCount() {
        if(this.mList == null)
            return 0;
        return mList.size();
    }
//
//    @Override
//    public int getSectionForPosition(int position) {
//        return 0;
//    }

//    @Override
//    public Object[] getSections() {
//        List<String> sections = new ArrayList<>(26);
//        mSectionPositions = new ArrayList<>(26);

//        int n = 0;
//        for(int i = 65 ; i < 91 ; i++){
//            String str = new Character((char) i).toString();
//            sections.add(str);

//            if(mFirstLetterArray.contains(str)){
//                sections.add(str);
//                mSectionPositions.add(n);
//                n++;
//            }else{
//                mSectionPositions.add(-1);
//            }
//        }
//
//        sections.add("#");
//        mSectionPositions.add(-1);
//        return sections.toArray(new String[0]);
//    }
//
//    @Override
//    public int getPositionForSection(int sectionIndex) {
//        return mSectionPositions.get(sectionIndex);
//    }

    public interface OnItemClickListener {
        void onItemClick(int type, int position);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llRowItem;
        private ImageView imgProfileImg;
        private CustomTextView txtFullname;
        private CustomTextView txtUsername;
        private RelativeLayout rlAction;
        private CustomTextView txtAddFriend;
        private CustomTextView txtActionCancel;
        private CustomTextView txtActionUnFriend;

        public MyViewHolder(View itemView) {
            super(itemView);
            llRowItem = itemView.findViewById(R.id.llRowItem);
            txtFullname = itemView.findViewById(R.id.tvFullname);
            txtUsername = itemView.findViewById(R.id.tvUsername);
            rlAction = itemView.findViewById(R.id.rlAction);
            imgProfileImg = itemView.findViewById(R.id.imgCircleProfile);
            txtAddFriend = itemView.findViewById(R.id.txtAddFriend);
            txtActionUnFriend = itemView.findViewById(R.id.txtActionUnFriend);
            txtActionCancel = itemView.findViewById(R.id.txtActionCancel);
        }
    }

    public class NameComparator implements Comparator<ContactUserModel>
    {
        public int compare(ContactUserModel left, ContactUserModel right) {
            return left.full_name.compareTo(right.full_name);
        }
    }

    protected List<ContactUserModel> getFilteredResults(String constraint) {
        List<ContactUserModel> results = new ArrayList<>();

        for (ContactUserModel item : mList) {
            if (item.full_name.toLowerCase().contains(constraint)) {
                results.add(item);
            }
        }
        return results;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<ContactUserModel> filteredResults = null;
                if (constraint.length() == 0) {
                    filteredResults = mOriginalList;
                } else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                mList = (ArrayList<ContactUserModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}