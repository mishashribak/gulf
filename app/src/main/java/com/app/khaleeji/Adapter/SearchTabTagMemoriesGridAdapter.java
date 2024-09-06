package com.app.khaleeji.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.MemoryModel;
import com.squareup.picasso.Picasso;
import java.util.List;
import Model.SubTagImageModel;
import Model.SubTagModel;

public class SearchTabTagMemoriesGridAdapter extends BaseAdapter {

    private List<MemoryModel> mMemoryList;
    private Activity mActivity;
    private OnItemClickListener onItemClickListener;
    private String mStrTag;

    public SearchTabTagMemoriesGridAdapter(Activity activity, OnItemClickListener onItemClickListener) {
        this.mActivity = activity;
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public int getCount() {
        if( mMemoryList != null)
            return mMemoryList.size();
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

//        SquareImageView ivProductPic = convertView.findViewById(R.id.ivProductPic);
        MemoryModel memoryModel = mMemoryList.get(position);
//        RelativeLayout rlThumb = convertView.findViewById(R.id.rlThumb);
        ImageView thumbnail = convertView.findViewById(R.id.thumbnail);

//        rlThumb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onItemClickListener.onItemClick(position);
//            }
//        });

        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position, mMemoryList, mStrTag);
            }
        });

        try{
            Picasso.with(mActivity).load( memoryModel.getThumbnail()).fit().centerCrop().into(thumbnail);
        }catch (Exception ex){
            ex.printStackTrace();
        }

//        ivProductPic.setVisibility(View.GONE);
//        if( SubTagImageModel.getUrl() != null && ! SubTagImageModel.getUrl().isEmpty()){
//            if(SubTagImageModel.getMediaType().equalsIgnoreCase("image")){
//                ivProductPic.setVisibility(View.VISIBLE);
//                try{
//                    Picasso.with(mActivity).load( SubTagImageModel.getUrl()).fit().centerCrop().into(ivProductPic);
//                }catch (Exception ex){
//                    ex.printStackTrace();
//                }
//            }else if(SubTagImageModel.getMediaType().equalsIgnoreCase("video")){
//                rlThumb.setVisibility(View.VISIBLE);
//                try{
//                    Picasso.with(mActivity).load( SubTagImageModel.getThumbnail()).fit().into(thumbnail);
//                }catch (Exception ex){
//                    ex.printStackTrace();
//                }
//            }
//        }

        return convertView;
    }


    public void setData(List<MemoryModel> memoryModelList, String tag){
        mStrTag = tag;
        this.mMemoryList = memoryModelList;
        this.notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int index, List<MemoryModel> memoryModels, String tag);
    }

}