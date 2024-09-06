package com.app.khaleeji.Adapter;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.HotSpotMemory;
import com.app.khaleeji.Response.MemoryModel;
import com.app.khaleeji.Response.UserDetails;
import com.app.khaleeji.Response.fetchHotspotTimeLine.HotspotMemory;
import com.squareup.picasso.Picasso;

import java.util.List;
import CustomView.CustomTextView;
import Model.BlockedUserData;
import Model.CustomGroupData;
import Utility.SquareImageView;

public class HotSpotMemoriesGridAdapter extends BaseAdapter {

    private List<MemoryModel> mListHotSpotMemory;
    private Context mContext;
    private Activity mActivity;
    private OnItemClickListener onItemClickListener;

    public HotSpotMemoriesGridAdapter(Activity activity, Context context, OnItemClickListener onItemClickListener) {
        this.mContext = context;
        this.mActivity = activity;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getCount() {
        if( mListHotSpotMemory != null)
            return mListHotSpotMemory.size();
        return 0;
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }

    // 5
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mActivity.getLayoutInflater().inflate(R.layout.row_other_user_grid_memory, parent, false);

        SquareImageView ivProductPic = convertView.findViewById(R.id.ivProductPic);
        MemoryModel memoryModel = mListHotSpotMemory.get(position);
        RelativeLayout rlThumb = convertView.findViewById(R.id.rlThumb);
        ImageView thumbnail = convertView.findViewById(R.id.thumbnail);

        rlThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
            }
        });

        ivProductPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
            }
        });

        ivProductPic.setVisibility(View.GONE);
        if( memoryModel.getUrl() != null && ! memoryModel.getUrl().isEmpty()){
            if(memoryModel.getMediaType().equalsIgnoreCase("image")){
                ivProductPic.setVisibility(View.VISIBLE);
                try{
                    Picasso.with(mActivity).load( memoryModel.getUrl()).fit().centerCrop().placeholder(R.drawable.hotspot_default).into(ivProductPic);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }else if(memoryModel.getMediaType().equalsIgnoreCase("video")){
                rlThumb.setVisibility(View.VISIBLE);
                try{
                    Picasso.with(mActivity).load( memoryModel.getThumbnail()).fit().into(thumbnail);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }

        return convertView;
    }


    public void setData(List<MemoryModel>  list){
        this.mListHotSpotMemory = list;
        this.notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick( int index);
    }

}