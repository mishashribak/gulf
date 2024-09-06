package com.app.khaleeji.Adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SectionIndexer;
import android.widget.ToggleButton;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.FriendData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import CustomView.CustomTextView;

public class CreateGroupChatAdapter extends RecyclerView.Adapter<CreateGroupChatAdapter.MyViewHolder> implements SectionIndexer, Filterable {

    private Context context;
    private OnItemClickListener onItemClickListener;
    private static final String TAG = CreateGroupChatAdapter.class.getSimpleName();
    private List<FriendData> mOriginalList;
    private List<FriendData> friendlist;
    private List<String> mFirstLetterArray = new ArrayList<String>();
    private ArrayList<Integer> mSectionPositions;
    private List<String> mSections = new ArrayList<>(26);


    public CreateGroupChatAdapter(Context context,  OnItemClickListener onItemClickListener) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_create_group_chat, parent, false);
        return new MyViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.txtSectionName.setVisibility(View.GONE);
        String section = String.valueOf(friendlist.get(position).getFull_name().charAt(0)).toUpperCase();
        if (!mSections.contains(section)) {
            mSections.add(section);
            holder.txtSectionName.setVisibility(View.VISIBLE);
            holder.txtSectionName.setText(section);
        }

        if(position == friendlist.size()-1){
            holder.line.setVisibility(View.GONE);
        }

        holder.txtName.setText(friendlist.get(position).getFull_name());

        holder.btToggle.setChecked(friendlist.get(position).isChecked());

        holder.btToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean on = ((ToggleButton) view).isChecked();
                if (on) {
                    onItemClickListener.onItemClick(friendlist.get(position), 0);
                } else {
                    onItemClickListener.onItemClick(friendlist.get(position), 1);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if(this.friendlist == null)
            return 0;
        return friendlist.size();
    }

    public void setData(List<FriendData> data) {
        mOriginalList = data;
        this.friendlist = data;

        Collections.sort(friendlist, new NameComparator());
        for (int j = 0, size = friendlist.size(); j < size; j++) {
            String section = String.valueOf(friendlist.get(j).getFull_name().charAt(0)).toUpperCase();
            mFirstLetterArray.add(section);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        List<String> sections = new ArrayList<>(26);
        mSectionPositions = new ArrayList<>(26);

        for(int i = 65 ; i < 91 ; i++){
            String str = new Character((char) i).toString();
            sections.add(str);

            if(mFirstLetterArray.contains(str)){
                mSectionPositions.add(mFirstLetterArray.indexOf(str));
            }else{
                mSectionPositions.add(-1);
            }
        }

        sections.add("#");
        mSectionPositions.add(-1);

       /* for (int i = 0, size = mDataArray.size(); i < size; i++) {
            String section = String.valueOf(mDataArray.get(i).charAt(0)).toUpperCase();
            if (!sections.contains(section)) {
                mSectionPositions.add(i);
            }
        }*/
        return sections.toArray(new String[0]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mSectionPositions.get(sectionIndex);
    }

    public interface OnItemClickListener {
        void onItemClick( FriendData friendData, int type);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CustomTextView txtName;
        private CustomTextView txtSectionName;
        private ToggleButton btToggle;
        private View line;
        public MyViewHolder(View view) {
            super(view);
            txtName = view.findViewById(R.id.txtName);
            txtSectionName = view.findViewById(R.id.txtSectionName);
            btToggle = view.findViewById(R.id.btSelected);
            line = view.findViewById(R.id.viewLine);
        }
    }

    public class NameComparator implements Comparator<FriendData>
    {
        public int compare(FriendData left, FriendData right) {
            return left.getFull_name().compareTo(right.getFull_name());
        }
    }

    protected List<FriendData> getFilteredResults(String constraint) {
        List<FriendData> results = new ArrayList<>();

        for (FriendData item : mOriginalList) {
            if (item.getFull_name().toLowerCase().contains(constraint)) {
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
                List<FriendData> filteredResults;
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
                friendlist = (ArrayList<FriendData>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}