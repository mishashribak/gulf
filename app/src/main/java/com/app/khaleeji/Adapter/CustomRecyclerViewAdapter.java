package com.app.khaleeji.Adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.khaleeji.R;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import Model.NewChatModel;

/**
 * Created by Dcube on 08-06-2018.
 */

public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = CustomRecyclerViewAdapter.class.getSimpleName();


    private ArrayList<String> mData = new ArrayList<String>();
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<NewChatModel> itemObjects;

    public CustomRecyclerViewAdapter(List<NewChatModel> itemObjects) {
        this.itemObjects = itemObjects;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEADER)
        {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_layout, parent, false);
            return new HeaderViewHolder(layoutView);
        }
        else if (viewType == TYPE_ITEM) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
            return new ItemViewHolder(layoutView);
        }
        throw new RuntimeException("No match for " + viewType + ".");
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        NewChatModel mObject = itemObjects.get(position);
        if(holder instanceof HeaderViewHolder)
        {
            ((HeaderViewHolder) holder).headerTitle.setText(mObject.getHeaderTitle());
        }
        else if(holder instanceof ItemViewHolder)
        {
            ((ItemViewHolder) holder).itemContent.setText(mObject.getHeaderTitle());
        }
    }

    private String getItem(int position) {
      //  return itemObjects.get(position);
        return mData.get(position);
    }



    @Override
    public int getItemCount() {
     //   return itemObjects.size();
        return mData.size();
    }
    @Override
    public int getItemViewType(int position) {
//        if (isPositionHeader(position))
//            return TYPE_HEADER;
//        return TYPE_ITEM;


        return sectionHeader.contains(position) ? TYPE_HEADER : TYPE_ITEM;
    }



    private boolean isPositionHeader(int position) {
        return position == 0;
    }


    public void addItem(final String item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final String item) {
        mData.add(item);
        sectionHeader.add(mData.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder{
        public TextView headerTitle;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            headerTitle = (TextView)itemView.findViewById(R.id.header_id);
        }
    }



    public class ItemViewHolder extends RecyclerView.ViewHolder{
        public TextView itemContent;
        public ItemViewHolder(View itemView) {
            super(itemView);
            itemContent = (TextView)itemView.findViewById(R.id.item_content);
        }
    }

}
