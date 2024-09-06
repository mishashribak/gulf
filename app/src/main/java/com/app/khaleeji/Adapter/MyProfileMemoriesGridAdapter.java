package com.app.khaleeji.Adapter;

/**
 * Created by Dcube on 23-04-2018.
 */

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.app.khaleeji.Activity.GulflinkApplication;
import com.app.khaleeji.R;
import com.app.khaleeji.Response.fetchMemoryPackage.Memory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import Utility.SquareImageView;
import Utility.Utils;


public class MyProfileMemoriesGridAdapter extends BaseAdapter {

    private final String TAG = MyProfileMemoriesGridAdapter.class.getName();

    GulflinkApplication global;
    int fav = 1;
    Activity mActivity;
    int dpWidth, dpHeight;
    private Context mContext;
    private boolean isFav[];
    private OnclickListener mOnclickListener;
    private List<Memory> memoriesList = new ArrayList<>();

    public MyProfileMemoriesGridAdapter(Activity activity, List<Memory> dataList, Context context) {

        global = (GulflinkApplication) context.getApplicationContext();

        this.mContext = context;

        this.mActivity = activity;

        memoriesList = dataList;

        dpHeight = Utils.dpToPx(100);
        dpWidth = Utils.dpToPx(100);

        isFav = new boolean[6];
    }


    @Override
    public int getCount() {
        return memoriesList.size(); //memoriesList.size()
    }

    @Override
    public Object getItem(int position) {
        return memoriesList.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView = mActivity.getLayoutInflater().inflate(R.layout.card_buy_for_product, parent, false);

        final SquareImageView ivProductPic =  convertView.findViewById(R.id.ivProductPic);

//        if (memoriesList.get(position).getMediaType().toLowerCase().equals("image")) {
        if (memoriesList.get(position).getUrl().toLowerCase().contains(".jpg")) {
            Picasso.with(mContext)
                    .load(memoriesList.get(position).getUrl())
                    .resize(dpHeight, dpHeight)
                    .into(ivProductPic);
        } else {
            if (memoriesList.get(position).getThumbnail().isEmpty()) {
                ivProductPic.setImageResource(R.drawable.ic_play_button);
            } else {
                Picasso.with(mContext)
                        .load(memoriesList.get(position).getThumbnail())
                        .placeholder(R.drawable.ic_play_button)
                        .error(R.drawable.ic_play_button)
                        .into(ivProductPic);
            }

        }

        return convertView;
    }

    public interface OnclickListener {
        public void Onposclick(int pos);
    }

}
