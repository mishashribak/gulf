package com.app.khaleeji.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SectionIndexer;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.khaleeji.R;
import java.util.ArrayList;
import java.util.List;
import CustomView.CustomTextView;
import Model.LocalContactModel;

public class InviteAdapter extends RecyclerView.Adapter<InviteAdapter.MyViewHolder>  implements SectionIndexer, Filterable {

    private Context context;
    private List<LocalContactModel> mOriginalList;
    private List<LocalContactModel> mList;
    private OnItemClickListener mOnItemClickListener;
    private List<String> mFirstLetterArray = new ArrayList<String>();
    private ArrayList<Integer> mSectionPositions;

    public InviteAdapter(Context context, OnItemClickListener itemClickListener) {
        this.context = context;
        mOnItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public InviteAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_users_invite, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final InviteAdapter.MyViewHolder holder, final int position) {

        holder.txtFullname.setText(mList.get(position).getName());
        holder.txtUsername.setText(mList.get(position).getPhoneNumber());
        holder.txtInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onInvite(mList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        if( mList != null)
            return mList.size();
        return 0;
    }

    public void setData( List<LocalContactModel> list){
        this.mList = list;
        this.mOriginalList = list;
        this.notifyDataSetChanged();
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
                sections.add(str);
                mSectionPositions.add(n);
                n++;
            }else{
                mSectionPositions.add(-1);
            }
        }

        sections.add("#");
        mSectionPositions.add(-1);
        return sections.toArray(new String[0]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mSectionPositions.get(sectionIndex);
    }

    public interface OnItemClickListener {
        void onInvite(LocalContactModel localContactModel);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CustomTextView txtFullname;
        private CustomTextView txtUsername;
        private CustomTextView txtInvite;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtFullname = itemView.findViewById(R.id.tvFullname);
            txtUsername = itemView.findViewById(R.id.tvUsername);
            txtInvite = itemView.findViewById(R.id.txtInvite);
        }
    }

    protected List<LocalContactModel> getFilteredResults(String constraint) {
        List<LocalContactModel> results = new ArrayList<>();

        for (LocalContactModel item : mList) {
            if (item.getName().toLowerCase().contains(constraint)) {
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
                List<LocalContactModel> filteredResults = null;
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
                mList = (ArrayList<LocalContactModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
