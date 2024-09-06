package com.app.khaleeji.Adapter;

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

/**
 * Created by Dcube on 27-07-2018.
 */

public class OtherUserMemoriesGridAdapter extends BaseAdapter {

    private final String TAG = OtherUserMemoriesGridAdapter.class.getName();

    GulflinkApplication global;
    private Context mContext;

    private boolean isFav[];
    private OnclickListener mOnclickListener;
    int fav=1;

    Activity mActivity;

    int dpWidth,dpHeight;

    private List<Memory> memoriesList=new ArrayList<>();

    public OtherUserMemoriesGridAdapter(Activity activity, List<Memory> dataList, Context context) {

        global = (GulflinkApplication) context.getApplicationContext();

        this.mContext=context;

        this.mActivity = activity;

        memoriesList = dataList;

        dpHeight = Utils.dpToPx(100);
        dpWidth = Utils.dpToPx(100);

        //Log.e(TAG,"memoriesList.size(): "+memoriesList.size());

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

        convertView = mActivity.getLayoutInflater().inflate(R.layout.row_other_user_grid_memory, parent, false);

        final SquareImageView ivProductPic = (SquareImageView)convertView.findViewById(R.id.ivProductPic);

        //   Log.e(TAG,"position : "+position);

        if (memoriesList.get(position).getMediaType().equalsIgnoreCase("image"))
        {
            Picasso.with(mContext)
                    .load(memoriesList.get(position).getUrl())
                    .into(ivProductPic);

            //.resize(dpHeight,dpHeight)
        }
        else
        {
            if (memoriesList.get(position).getThumbnail().isEmpty())
            {
                ivProductPic.setImageResource(R.drawable.ic_play_button);
            }
            else
            {
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
