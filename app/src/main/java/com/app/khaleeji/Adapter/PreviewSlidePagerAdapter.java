package com.app.khaleeji.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.app.khaleeji.R;

import java.util.List;

import Model.ZipCategoryData;
import photoEditor.ImageFragment;

public class PreviewSlidePagerAdapter extends FragmentStatePagerAdapter {

    private List<ZipCategoryData> categoryDataList;
    private Context mContext;
    private OnPagerItemClickListener onPagerItemClickListener;

    public PreviewSlidePagerAdapter(FragmentManager fm, List<ZipCategoryData> categoryDataList
            , Context context, OnPagerItemClickListener onPagerItemClickListener) {
        super(fm);
        this.categoryDataList = categoryDataList;
        this.mContext = context;
        this.onPagerItemClickListener = onPagerItemClickListener;

    }

    @Override
    public Fragment getItem(int position) {
        if (categoryDataList == null) {
            return (null);
        }

        String categoryName="";
        switch (position){
            case 0:
                categoryName = mContext.getString(R.string.trending);
                break;
            case 1:
                categoryName = mContext.getString(R.string.words);
                break;
            case 2:
                categoryName = mContext.getString(R.string.nationalism);
                break;
            case 3:
                categoryName = mContext.getString(R.string.sports);
                break;
            case 4:
                categoryName = mContext.getString(R.string.spiritual);
                break;
        }

        ImageFragment fragment = new ImageFragment(new ImageFragment.OnStrickerClickListener() {
            @Override
            public void onStickerClick(int  resId ,String type) {
                onPagerItemClickListener.onPagerItemClick(resId, type);
            }
        }, categoryName);

        fragment.setData(categoryDataList.get(position), position);
        return fragment;
    }

    @Override
    public int getCount() {
        return categoryDataList.size();
    }

    public interface OnPagerItemClickListener {

        void onPagerItemClick(int resId, String type);
    }

}

