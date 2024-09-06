package com.app.khaleeji.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.FriendData;
import com.app.khaleeji.Response.groupListPackage.GroupMemberModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import CustomView.CustomTextView;
import Model.CustomGroupData;

public class GroupEditAdapter  extends RecyclerView.Adapter<GroupEditAdapter.MyViewHolder> implements SectionIndexer {

    private Context context;
    private OnItemClickListener onItemClickListener;
    private List<GroupMemberModel> friendlist;
    private List<String> mFirstLetterArray = new ArrayList<String>();
    private ArrayList<Integer> mSectionPositions;
    private List<String> mSections = new ArrayList<>(26);


    public GroupEditAdapter(List<GroupMemberModel> data, Context context,OnItemClickListener onItemClickListener) {
        this.context = context;
        this.friendlist = data;
        this.onItemClickListener = onItemClickListener;
    }

    public GroupEditAdapter(Context context,OnItemClickListener onItemClickListener) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    public void setData(List<GroupMemberModel> data) {
        this.friendlist = data;
        Collections.sort(friendlist, new NameComparator());
        for (int j = 0, size = friendlist.size(); j < size; j++) {
            String section = String.valueOf(friendlist.get(j).getFullName().charAt(0)).toUpperCase();
            mFirstLetterArray.add(section);
        }
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_family_group, parent, false);
        return new MyViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.txtSectionName.setVisibility(View.GONE);
        String section = String.valueOf(friendlist.get(position).getFullName().charAt(0)).toUpperCase();
        if (!mSections.contains(section)) {
            mSections.add(section);
            holder.txtSectionName.setVisibility(View.VISIBLE);
            holder.txtSectionName.setText(section);
        }

        if(position == friendlist.size()-1){
            holder.line.setVisibility(View.GONE);
        }

        if(friendlist.get(position).getStatus() == 1){
            holder.btToggle.setChecked(true);
        }else{
            holder.btToggle.setChecked(false);
        }

        holder.txtName.setText(friendlist.get(position).getFullName());
        holder.btToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean on = ((ToggleButton) view).isChecked();
                if (on) {
                    onItemClickListener.onItemClick(position, 0);
                } else {
                    onItemClickListener.onItemClick(position, 1);
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

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        List<String> sections = new ArrayList<>(26);
        mSectionPositions = new ArrayList<>(26);

        int n = 0;
        for(int i = 65 ; i < 91 ; i++){
            String str = new Character((char) i).toString();
            sections.add(str);

            if(mFirstLetterArray.contains(str)){
                mSectionPositions.add(n);
                n++;
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
        void onItemClick( int index, int type);
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

    public class NameComparator implements Comparator<GroupMemberModel>
    {
        public int compare(GroupMemberModel left, GroupMemberModel right) {
            return left.getFullName().compareTo(right.getFullName());
        }
    }
}