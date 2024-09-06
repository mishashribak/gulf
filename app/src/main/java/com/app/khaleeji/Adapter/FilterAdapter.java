package com.app.khaleeji.Adapter;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.app.khaleeji.Fragments.CameraKitFragment;
import com.app.khaleeji.R;
import com.otaliastudios.cameraview.filter.Filters;

import java.util.List;

import CustomView.CustomTextView;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.GroupViewHolder> {

    Context context;
    private OnclickListener mOnclickListener;
    private OnTouchListener mOnTouchListener;
    CameraKitFragment gsnapFragmentvskitkat;

    Activity activity;

    Filters[] listFilters;

    int selectedFilter;


    public FilterAdapter(Activity activity,Context context, Filters[] filtersList,
                         CameraKitFragment mTimeLineFriendsFragment) {
        this.context = context;
        this.activity = activity;
        this.listFilters = filtersList;
        gsnapFragmentvskitkat = mTimeLineFriendsFragment;
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_filter_layout, parent, false);

        return new GroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final GroupViewHolder holder, final int position) {

        if (selectedFilter == position)
        {
            holder.parentLayout.setBackgroundColor(context.getResources().getColor(R.color.colorBgOptionGray));
        }
        else
        {
            holder.parentLayout.setBackgroundColor(context.getResources().getColor(R.color.gray));
        }

        holder.tvFilterName.setText(listFilters[position].toString());


        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mOnclickListener != null)
                {
                    mOnclickListener.Onposclick(position, listFilters[position]);

                    selectedFilter = position;

                    notifyDataSetChanged();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return listFilters.length;
    }

    public void setOnTouchListener(OnTouchListener monTouchListener) {
        this.mOnTouchListener = monTouchListener;
    }


    public interface OnTouchListener {

        public void onSwipeLeft(int pos);

        public void onSwipeRight(int pos);
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder{


        CustomTextView tvFilterName;
        RelativeLayout parentLayout;


        public GroupViewHolder(View itemView) {
            super(itemView);

            tvFilterName = itemView.findViewById(R.id.tvFilterName);
            parentLayout = itemView.findViewById(R.id.parentLayout);

        }
    }

    public void setOnClickListener(OnclickListener mOnclickListener) {
        this.mOnclickListener = mOnclickListener;
    }

    public interface OnclickListener {
        void Onposclick(int pos, Filters filter);
    }

}